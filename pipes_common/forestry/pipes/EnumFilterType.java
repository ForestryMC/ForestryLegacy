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
package forestry.pipes;

import net.minecraft.item.ItemStack;
import forestry.core.config.ForestryItem;

public enum EnumFilterType {
	CLOSED, ANYTHING, ITEM, BEE, DRONE, PRINCESS, QUEEN, PURE_BREED, NOCTURNAL, PURE_NOCTURNAL, FLYER, PURE_FLYER, CAVE, PURE_CAVE;

	public static EnumFilterType getType(ItemStack stack) {
		if (stack.itemID == ForestryItem.beeDroneGE.itemID)
			return DRONE;
		if (stack.itemID == ForestryItem.beePrincessGE.itemID)
			return PRINCESS;
		if (stack.itemID == ForestryItem.beeQueenGE.itemID)
			return QUEEN;

		return ITEM;
	}
}
