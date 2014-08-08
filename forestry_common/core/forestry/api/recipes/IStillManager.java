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

import net.minecraftforge.liquids.LiquidStack;

/**
 * Provides an interface to the recipe manager of the still.
 * 
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * 
 * Accessible via {@link RecipeManagers.stillManager}
 * 
 * Note that this is untested with anything other than biomass->biofuel conversion.
 * 
 * @author SirSengir
 */
public interface IStillManager extends ICraftingProvider {
	/**
	 * Add a recipe to the still
	 * 
	 * @param cyclesPerUnit
	 *            Amount of work cycles required to run through the conversion once.
	 * @param input
	 *            ItemStack representing the input liquid.
	 * @param output
	 *            ItemStack representing the output liquid
	 */
	public void addRecipe(int cyclesPerUnit, LiquidStack input, LiquidStack output);
}
