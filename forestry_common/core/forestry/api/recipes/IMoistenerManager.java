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

/**
 * Provides an interface to the recipe manager of the moistener.
 * 
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * 
 * Accessible via {@link RecipeManagers.moistenerManager}
 * 
 * @author SirSengir
 */
public interface IMoistenerManager extends ICraftingProvider {

	/**
	 * Add a recipe to the moistener
	 * 
	 * @param resource
	 *            Item required in resource stack. Will be reduced by one per produced item.
	 * @param product
	 *            Item to produce per resource processed.
	 * @param timePerItem
	 *            Moistener runs at 1 - 4 time ticks per ingame tick depending on light level. For mycelium this value is currently 5000.
	 */
	public void addRecipe(ItemStack resource, ItemStack product, int timePerItem);
}
