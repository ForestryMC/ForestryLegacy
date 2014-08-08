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
package forestry.cultivation;

import net.minecraft.item.ItemStack;

public class OvergrowthStrict extends Overgrowth {

	public OvergrowthStrict(ItemStack crop, ItemStack ripe) {
		super(crop, ripe);
	}

	public boolean hasCrop(ItemStack other) {
		return crop.isItemEqual(other);
	}

	public boolean hasRipe(ItemStack other) {
		return ripe.isItemEqual(other);
	}
}
