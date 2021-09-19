package com.denfop.container;

import com.denfop.tiles.base.TileSolarGeneratorEnergy;
import ic2.core.ContainerFullInv;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class ContainerSolarGeneratorEnergy extends ContainerFullInv<TileSolarGeneratorEnergy> {

    public ContainerSolarGeneratorEnergy(EntityPlayer entityPlayer, TileSolarGeneratorEnergy tileEntity) {
        super(entityPlayer, tileEntity, 196);

        addSlotToContainer(new SlotInvSlot(tileEntity.outputSlot, 0, 67 + 2, 34 + 1));
    }

    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("energy");

        return ret;

    }

}
