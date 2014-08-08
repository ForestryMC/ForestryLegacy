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

public class Overgrowth {
	public final ItemStack crop;
	public final ItemStack ripe;

	public Overgrowth(ItemStack crop, ItemStack ripe) {
		this.crop = crop;
		this.ripe = ripe;
	}

	public boolean hasCrop(ItemStack other) {
		return crop.isItemEqual(new ItemStack(other.itemID, 1, 0));
	}

	public boolean hasRipe(ItemStack other) {
		return ripe.isItemEqual(new ItemStack(other.itemID, 1, 0));
	}
}
