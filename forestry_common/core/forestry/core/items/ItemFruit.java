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

import java.util.List;
import java.util.Locale;

import forestry.core.utils.StringUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;


public class ItemFruit extends ItemForestryFood {

	public static enum EnumFruit {
		CHERRY(176), WALNUT(177), CHESTNUT(178);
		
		final int iconIndex;
		private EnumFruit(int iconIndex) {
			this.iconIndex = iconIndex;
			
		}
	} 
	
	public ItemFruit(int id) {
		super(id, 1, 0.2f);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public int getIconFromDamage(int meta) {
		return EnumFruit.values()[meta].iconIndex;
	}
	
	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < EnumFruit.values().length; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= EnumFruit.values().length)
			return null;

		return StringUtil.localize("item.fruit." + EnumFruit.values()[itemstack.getItemDamage()].toString().toLowerCase(Locale.ENGLISH));
	}


}
