package com.denfop.tiles.base;

import cofh.api.energy.IEnergyReceiver;
import com.denfop.Config;
import com.denfop.IUCore;
import com.denfop.api.Recipes;
import com.denfop.audio.AudioSource;
import com.denfop.container.ContainerBaseDoubleMolecular;
import ic2.api.energy.EnergyNet;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public abstract class TileEntityBaseDoubleMolecular extends TileEntityElectricMachine implements IHasGui, INetworkTileEntityEventListener, IEnergyReceiver {
    public boolean queue;
    protected double progress;


    public final int defaultOperationLength;

    public final int defaultTier;

    public int operationLength;

    public int operationsPerTick;

    protected double guiProgress;

    public AudioSource audioSource;

    public InvSlotProcessable inputSlot;

    public final InvSlotOutput outputSlot;


    public double perenergy;
    public double differenceenergy;

    public boolean rf = false;

    public TileEntityBaseDoubleMolecular(int length) {
        super(0, 10, 1);

        this.progress = 0;
        this.defaultOperationLength = this.operationLength = length;

        this.defaultTier = 11;
        this.outputSlot = new InvSlotOutput(this, "output", 2, 1);

        this.queue = false;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getDouble("progress");
        this.rf = nbttagcompound.getBoolean("rf");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("progress", this.progress);
        nbttagcompound.setBoolean("rf", this.rf);

    }

    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {

        return null;
    }

    public double getProgress() {
        return Math.min(this.guiProgress, 1);
    }

    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);

            setOverclockRates();
        }

    }

    public void onUnloaded() {
        super.onUnloaded();
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IUCore.audioManager.removeSources(this);
            this.audioSource = null;
        }
    }

    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating())
            setOverclockRates();
    }

    public void updateEntityServer() {
        super.updateEntityServer();

        RecipeOutput output = getOutput();

        IC2.network.get().updateTileEntityField(this, "redstoneMode");
        if (!queue) {
            if (output != null) {
                this.differenceenergy = this.energy - this.perenergy;
                this.perenergy = this.energy;
                setActive(true);
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
                if (this.worldObj.provider.getWorldTime() % 200 == 0)
                    IC2.network.get().initiateTileEntityEvent(this, 2, true);


                this.progress = this.energy;
                double k = this.progress;
                this.guiProgress = (k / output.metadata.getDouble("energy"));
                if (this.energy >= output.metadata.getDouble("energy")) {
                    operate(output);

                    this.progress = 0;
                    this.energy = 0;

                    IC2.network.get().initiateTileEntityEvent(this, 2, true);
                }
            } else {
                if (this.energy != 0 && getActive())
                    IC2.network.get().initiateTileEntityEvent(this, 1, true);
                this.energy = 0;
                this.maxEnergy = 0;
                setActive(false);
            }

        } else {
            if (output != null) {
                setActive(true);
                this.differenceenergy = this.energy - this.perenergy;
                this.perenergy = this.energy;
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
                if (this.worldObj.provider.getWorldTime() % 200 == 0)
                    IC2.network.get().initiateTileEntityEvent(this, 2, true);

                int size = 0;
                int size2 = 0;
                boolean getrecipe = false;
                for (int i = 0; !getrecipe; i++)
                    for (int j = 0; j < 8; j++) {
                        ItemStack stack = new ItemStack(this.inputSlot.get(0).getItem(), i, this.inputSlot.get().getItemDamage());
                        ItemStack stack1 = new ItemStack(this.inputSlot.get(1).getItem(), j, this.inputSlot.get(1).getItemDamage());

                        if (Recipes.doublemolecular.getOutputFor(stack, stack1, false, false) != null) {
                            size = i;
                            size2 = j;
                            getrecipe = true;
                            break;

                        }
                    }

                size = (int) Math.floor((float) this.inputSlot.get(0).stackSize / size);
                size2 = (int) Math.floor((float) this.inputSlot.get(1).stackSize / size2);
                size = Math.min(size, size2);
                int size1 = this.outputSlot.get() != null ? 64 - this.outputSlot.get().stackSize : 64;
                size = Math.min(size1, size);
                this.progress = this.energy;
                double k = this.progress;
                double p = (k / (output.metadata.getDouble("energy") * size));
                if (p <= 1)
                    this.guiProgress = p;
                if (p > 1)
                    this.guiProgress = 1;
                if (this.energy >= (output.metadata.getDouble("energy") * size)) {
                    operate(output, size);

                    this.progress = 0;
                    this.energy = 0;

                    IC2.network.get().initiateTileEntityEvent(this, 2, true);
                }
            } else {
                if (this.energy != 0 && getActive())
                    IC2.network.get().initiateTileEntityEvent(this, 1, true);
                this.energy = 0;
                this.maxEnergy = 0;
                setActive(false);
            }
        }
        if (getActive() && output == null)
            setActive(false);
    }


    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (!this.inputSlot.isEmpty()) {
            if (this.energy >= this.maxEnergy)
                return 0;
            if (this.energy + amount >= this.maxEnergy) {
                double m = this.maxEnergy - this.energy;
                this.energy = this.maxEnergy;
                return amount - m;
            } else {
                this.energy += amount;

            }
        }
        return 0.0D;
    }

    public void setOverclockRates() {

        double previousProgress = this.progress / this.operationLength;
        double stackOpLen = (this.defaultOperationLength) * 64.0D * 1;
        this.operationsPerTick = (int) Math.min(Math.ceil(64.0D / stackOpLen), 2.147483647E9D);
        this.operationLength = (int) Math.round(stackOpLen * this.operationsPerTick / 64.0D);
        setTier(applyModifier(this.defaultTier, 0, 1.0D));
        RecipeOutput output = getOutput();

        if (!this.queue) {
            if (inputSlot.isEmpty()) {
                this.maxEnergy = 0;
            } else if (output != null) {
                this.maxEnergy = output.metadata.getDouble("energy");
            } else {
                this.maxEnergy = 0;
            }
        } else {

            if (inputSlot.isEmpty()) {
                this.maxEnergy = 0;
            } else if (output != null) {
                int size;
                size = this.outputSlot.get() != null ? 64 - this.outputSlot.get().stackSize : 64;
                size = Math.min(this.inputSlot.get().stackSize, size);
                this.maxEnergy = output.metadata.getDouble("energy") * size;
            } else {
                this.maxEnergy = 0;
            }
        }
        this.progress = (short) (int) Math.floor(previousProgress * this.operationLength + 0.1D);


    }

    public int getEnergyStored(ForgeDirection from) {
        return (int) this.energy;
    }

    public boolean canConnectEnergy(ForgeDirection arg0) {
        return true;
    }

    public int getMaxEnergyStored(ForgeDirection from) {
        return (int) this.maxEnergy;
    }

    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (this.rf)
            return receiveEnergy(maxReceive, simulate);
        else
            return 0;
    }

    public int receiveEnergy(int paramInt, boolean paramBoolean) {
        int i = (int) Math.min((this.maxEnergy - this.energy) / Config.coefficientrf, Math.min(EnergyNet.instance.getPowerFromTier(this.getSinkTier()) * Config.coefficientrf, paramInt));
        if (!paramBoolean)
            this.energy += (double) i / Config.coefficientrf;
        return i;
    }

    public void operate(RecipeOutput output) {
        List<ItemStack> processResult = output.items;
        operateOnce(processResult);
    }

    public void operate(RecipeOutput output, int size) {
        List<ItemStack> processResult = output.items;
        operateOnce(processResult, size);
    }

    public void operateOnce(List<ItemStack> processResult) {
        this.inputSlot.consume();
        this.outputSlot.add(processResult);
    }

    public void operateOnce(List<ItemStack> processResult, int size) {
        for (int i = 0; i < size; i++) {
            this.inputSlot.consume();
            this.outputSlot.add(processResult);
        }
    }

    public RecipeOutput getOutput() {
        if (this.inputSlot.isEmpty())
            return null;
        RecipeOutput output = this.inputSlot.process();
        if (output == null)
            return null;
        if (this.outputSlot.canAdd(output.items))
            return output;
        return null;
    }

    public abstract String getInventoryName();

    public ContainerBase<? extends TileEntityBaseDoubleMolecular> getGuiContainer(EntityPlayer entityPlayer) {
        return (ContainerBase<? extends TileEntityBaseDoubleMolecular>) new ContainerBaseDoubleMolecular(entityPlayer, this);
    }


    public String getStartSoundFile() {
        return "Machines/molecular.ogg";
    }

    public String getInterruptSoundFile() {
        return null;
    }

    public void onNetworkEvent(int event) {
        if (this.audioSource == null && getStartSoundFile() != null)
            this.audioSource = IUCore.audioManager.createSource(this, getStartSoundFile());
        switch (event) {
            case 0:
                if (this.audioSource != null)
                    this.audioSource.play();
                break;
            case 1:
                if (this.audioSource != null) {
                    this.audioSource.stop();
                    if (getInterruptSoundFile() != null)
                        IUCore.audioManager.playOnce(this, getInterruptSoundFile());
                }
                break;
            case 2:
                if (this.audioSource != null)
                    this.audioSource.stop();
                break;
        }
    }

    public static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round((base + extra) * multiplier);
        return (ret > 2.147483647E9D) ? Integer.MAX_VALUE : (int) ret;
    }


    public void onGuiClosed(EntityPlayer entityPlayer) {
    }


}
