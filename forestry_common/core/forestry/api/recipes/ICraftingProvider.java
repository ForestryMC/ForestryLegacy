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

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public interface ICraftingProvider {
	/**
	 * DOES NOT WORK FOR MANY MACHINES, DON'T USE IT!
	 * 
	 * Access to the full list of recipes contained in the crafting provider.
	 * 
	 * @return List of the given format where the first array represents inputs and the second outputs. Input and output liquids are returned as itemstacks as
	 *         well, representing itemID and damage.
	 */
	@Deprecated
	public List<Map.Entry<ItemStack[], ItemStack[]>> getRecipes();
}
