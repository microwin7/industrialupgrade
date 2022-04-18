package com.denfop.tiles.mechanism;

import com.denfop.Config;
import com.denfop.Ic2Items;
import com.denfop.api.Recipes;
import com.denfop.api.recipe.BaseMachineRecipe;
import com.denfop.api.recipe.Input;
import com.denfop.invslot.InvSlotProcessableMultiGeneric;
import com.denfop.tiles.base.EnumMultiMachine;
import com.denfop.tiles.base.TileEntityMultiMachine;
import com.denfop.tiles.panels.entity.TileEntitySolarPanel;
import ic2.api.recipe.IRecipeInputFactory;
import ic2.api.recipe.RecipeOutput;
import ic2.core.init.Localization;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Random;


public class TileEntityQuadCombRecycler extends TileEntityMultiMachine {

    public TileEntityQuadCombRecycler() {
        super(EnumMultiMachine.QUAD_RECYCLER.usagePerTick, EnumMultiMachine.QUAD_RECYCLER.lenghtOperation, 1);
    }
    public BaseMachineRecipe getOutput(int slotId) {

        if (this.inputSlots.isEmpty(slotId)) {
            this.output[slotId] = null;
            return null;
        }
        this.output[slotId] = this.inputSlots.process(slotId);
        if (output[slotId] == null) {
            final IRecipeInputFactory input = ic2.api.recipe.Recipes.inputFactory;
            output[slotId] = new BaseMachineRecipe(new Input(input.forStack(this.inputSlots.get(slotId))),new RecipeOutput(null,
                    Ic2Items.scrap)) ;

        }
        if (this.outputSlot.canAdd(output[slotId].output.items)) {
            return output[slotId];
        }

        return null;
    }
    @Override
    public EnumMultiMachine getMachine() {
        return EnumMultiMachine.QUAD_RECYCLER;
    }

    public String getInventoryName() {
        return Localization.translate("iu.blockCombRecycler2.name");
    }

    public String getStartSoundFile() {
        return "Machines/RecyclerOp.ogg";
    }

    public String getInterruptSoundFile() {
        return "Machines/InterruptOne.ogg";
    }


}
