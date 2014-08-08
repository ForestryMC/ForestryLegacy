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
package forestry.core;

import forestry.api.core.ItemInterface;
import forestry.api.core.Tabs;
import forestry.core.utils.StringUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabForestry extends CreativeTabs {

	static {
		Tabs.tabApiculture = new CreativeTabForestry(1, "apiculture");
		Tabs.tabArboriculture = new CreativeTabForestry(2, "arboriculture");
	}
	public static final CreativeTabs tabForestry = new CreativeTabForestry(0, "forestry");
	
	private int icon;
	
	public CreativeTabForestry(int icon, String label) {
		super(label);
		this.icon = icon;
	}

	@Override
    public ItemStack getIconItemStack() {
		switch(icon) {
		case 1:
			return ItemInterface.getItem("beeDroneGE");
		case 2:
			return ItemInterface.getItem("sapling");
		case 0:
		default:
			return ItemInterface.getItem("fertilizerCompound");
		}
    }

	@Override
    public String getTranslatedTabLabel() {
        return StringUtil.localize("itemGroup." + this.getTabLabel());
    }

}
