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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import forestry.core.utils.StringUtil;

public class ItemMisc extends ItemForestry {

	public ItemMisc(int i) {
		super(i);
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
	public String getItemDisplayName(ItemStack itemstack) {

		String name = "item.";

		switch (itemstack.getItemDamage()) {
		case 0:
			name += "pulsatingDust";
			break;
		case 1:
			name += "pulsatingMesh";
			break;
		case 2:
			name += "silkWisp";
			break;
		case 3:
			name += "wovenSilk";
			break;
		case 4:
			name += "dissipationCharge";
			break;
		case 5:
			name += "iceShard";
			break;
		case 6:
			name += "scentedPaneling";
			break;
		default:
			name += "unknown";
			break;
		}

		return StringUtil.localize(name);
	}

	@Override
	public int getIconFromDamage(int damage) {
		switch (damage) {
		case 0:
			return 96;
		case 1:
			return 97;
		case 2:
			return 99;
		case 3:
			return 100;
		case 4:
			return 80;
		case 5:
			return 72;
		case 6:
			return 62;
		default:
			return 0;
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 7; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

}
