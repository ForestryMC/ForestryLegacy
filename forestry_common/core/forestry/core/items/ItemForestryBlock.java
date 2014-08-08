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

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import forestry.core.utils.StringUtil;

public class ItemForestryBlock extends ItemBlock {

	protected String name;

	public ItemForestryBlock(int i, String name) {
		super(i);
		setMaxDamage(0);
		setHasSubtypes(true);
		this.name = name;
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	protected Block getBlock() {
		return Block.blocksList[this.getBlockID()];
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getItemNameIS(itemstack));
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "tile." + name + "." + itemstack.getItemDamage();
	}

}
