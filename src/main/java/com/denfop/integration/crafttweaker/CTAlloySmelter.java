package com.denfop.integration.crafttweaker;

import com.denfop.api.IDoubleMachineRecipeManager;
import com.denfop.api.Recipes;
import ic2.api.recipe.RecipeOutput;
import minetweaker.MineTweakerAPI;
import minetweaker.OneWayAction;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.mods.ic2.IC2RecipeInput;
import modtweaker2.helpers.InputHelper;
import modtweaker2.utils.BaseMapRemoval;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@ZenClass("mods.industrialupgrade.AlloySmelter")
public class CTAlloySmelter {
	@ZenMethod
	public static void addAlloSmelterRecipe(IItemStack output, IIngredient container, IIngredient fill) {
		MineTweakerAPI.apply(new AddAlloSmelterIngredientAction(container, fill, output));
	}

	private static class AddAlloSmelterIngredientAction extends OneWayAction {
		private final IIngredient container;

		private final IIngredient fill;

		private final IItemStack output;

		public AddAlloSmelterIngredientAction(IIngredient container, IIngredient fill, IItemStack output) {
			this.container = container;
			this.fill = fill;
			this.output = output;
		}

		public void apply() {
			Recipes.Alloysmelter.addRecipe(new IC2RecipeInput(this.container),
					new IC2RecipeInput(this.fill),null,

					MineTweakerMC.getItemStack(this.output));

		}

		public String describe() {
			return "Adding alloy smelter bottle recipe " + this.container + " + " + this.fill + " => " + this.output;
		}

		public Object getOverrideKey() {
			return null;
		}

		public int hashCode() {
			int hash = 7;
			hash = 67 * hash + ((this.container != null) ? this.container.hashCode() : 0);
			hash = 67 * hash + ((this.fill != null) ? this.fill.hashCode() : 0);
			hash = 67 * hash + ((this.output != null) ? this.output.hashCode() : 0);
			return hash;
		}

		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AddAlloSmelterIngredientAction other = (AddAlloSmelterIngredientAction) obj;
			if (!Objects.equals(this.container, other.container))
				return false;
			if (!Objects.equals(this.fill, other.fill))
				return false;
			return Objects.equals(this.output, other.output);
		}
	}
	@ZenMethod
	public static void removeRecipe(IItemStack output) {
		LinkedHashMap<IDoubleMachineRecipeManager.Input, RecipeOutput> recipes = new LinkedHashMap();

		for (Map.Entry<IDoubleMachineRecipeManager.Input, RecipeOutput> iRecipeInputRecipeOutputEntry : Recipes.Alloysmelter.getRecipes().entrySet()) {

			for (ItemStack stack : iRecipeInputRecipeOutputEntry.getValue().items) {
				if (stack.isItemEqual(InputHelper.toStack(output))) {
					recipes.put(iRecipeInputRecipeOutputEntry.getKey(), iRecipeInputRecipeOutputEntry.getValue());
				}
			}
		}

		MineTweakerAPI.apply(new CTAlloySmelter.Remove(recipes));
	}
	private static class Remove extends BaseMapRemoval<IDoubleMachineRecipeManager.Input, RecipeOutput> {
		protected Remove(Map<IDoubleMachineRecipeManager.Input, RecipeOutput> recipes) {
			super("alloysmelter",Recipes.Alloysmelter.getRecipes(), recipes);
		}

		protected String getRecipeInfo(Map.Entry<IDoubleMachineRecipeManager.Input, RecipeOutput> recipe) {
			return recipe.toString();
		}
	}
}
