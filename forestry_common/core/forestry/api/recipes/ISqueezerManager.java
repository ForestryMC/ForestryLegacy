/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

/**
 * Provides an interface to the recipe manager of the suqeezer.
 * 
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * 
 * Accessible via {@link RecipeManagers.squeezerManager}
 * 
 * @author SirSengir
 */
public interface ISqueezerManager extends ICraftingProvider {

	/**
	 * Add a recipe to the squeezer.
	 * 
	 * @param timePerItem
	 *            Number of work cycles required to squeeze one set of resources.
	 * @param resources
	 *            Array of item stacks representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid
	 *            {@link LiquidStack} representing the output of this recipe.
	 * @param remnants
	 *            Item stack representing the possible remnants from this recipe.
	 * @param chance
	 *            Chance remnants will be produced by a single recipe cycle.
	 */
	public void addRecipe(int timePerItem, ItemStack[] resources, LiquidStack liquid, ItemStack remnants, int chance);

	/**
	 * Add a recipe to the squeezer.
	 * 
	 * @param timePerItem
	 *            Number of work cycles required to squeeze one set of resources.
	 * @param resources
	 *            Array of item stacks representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid
	 *            {@link LiquidStack} representing the output of this recipe.
	 */
	public void addRecipe(int timePerItem, ItemStack[] resources, LiquidStack liquid);
}