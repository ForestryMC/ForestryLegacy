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
package forestry.core.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;

public class ItemForestry extends Item {

	public ItemForestry(int id) {
		super(id);
		maxStackSize = 64;
		setTextureFile(Defaults.TEXTURE_ITEMS);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getItemNameIS(itemstack));
	}

}
