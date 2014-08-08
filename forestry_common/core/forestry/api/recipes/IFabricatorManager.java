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

public interface IFabricatorManager extends ICraftingProvider {

	void addRecipe(ItemStack plan, LiquidStack molten, ItemStack result, Object[] pattern);

	void addSmelting(ItemStack resource, LiquidStack molten, int meltingPoint);

}
