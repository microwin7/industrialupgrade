package com.denfop.tiles.neutroniumgenerator;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotDischarge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityElectricMachine extends TileEntityInventory implements IEnergySink {
	public double energy;

	public double maxEnergy;

	private boolean addedToEnergyNet;

	private int tier;

	public final InvSlotDischarge dischargeSlot;

	public TileEntityElectricMachine(double maxenergy, int tier1, int oldDischargeIndex) {
		this.energy = 0.0D;
		this.addedToEnergyNet = false;
		this.maxEnergy = maxenergy;
		this.tier = tier1;
		this.dischargeSlot = new InvSlotDischarge(this, oldDischargeIndex, InvSlot.Access.NONE, tier1);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.energy = nbttagcompound.getDouble("energy");
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setDouble("energy", this.energy);
	}

	public void onLoaded() {
		super.onLoaded();
		if (IC2.platform.isSimulating()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
	}

	public void onUnloaded() {
		if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnergyNet = false;
		}
		super.onUnloaded();
	}



	public double getDemandedEnergy() {
		return this.maxEnergy - this.energy;
	}

	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		if (this.energy >= this.maxEnergy)
			return amount;
		this.energy += amount;
		return 0.0D;
	}

	public int getSinkTier() {
		return this.tier;
	}

	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}

	public void onNetworkUpdate(String field) {
		super.onNetworkUpdate(field);
		if (field.equals("tier"))
			setTier(this.tier);
	}

	public void setTier(int tier1) {
		if (this.tier == tier1)
			return;
		boolean addedToENet = this.addedToEnergyNet;
		if (addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnergyNet = false;
		}
		this.tier = tier1;
		this.dischargeSlot.setTier(tier1);
		if (addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
	}
}
