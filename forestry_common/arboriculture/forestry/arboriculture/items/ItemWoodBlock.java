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
package forestry.arboriculture.items;

import net.minecraft.item.ItemStack;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.core.items.ItemForestryBlock;
import forestry.core.utils.StringUtil;

public class ItemWoodBlock extends ItemForestryBlock {

	public ItemWoodBlock(int id, String name) {
		super(id, name);
	}

	public static int getTypeFromMeta(int damage) {
		return damage & 3;
	}

	private String getWoodNameIS(WoodType type) {
		return StringUtil.localize("wood." + type.ordinal());
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {

		if (this.getBlock() instanceof IWoodTyped)
			return getWoodNameIS(((IWoodTyped) this.getBlock()).getWoodType(itemstack.getItemDamage())) + " " + StringUtil.localize("tile." + name);
		else
			return StringUtil.localize(getItemNameIS(itemstack));
	}

}
