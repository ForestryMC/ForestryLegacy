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
package forestry.mail.items;

import net.minecraft.item.ItemStack;
import forestry.core.CreativeTabForestry;
import forestry.core.items.ItemOverlay;
import forestry.mail.EnumPostage;
import forestry.mail.IStamps;

public class ItemStamps extends ItemOverlay implements IStamps {

	public static class StampInfo extends OverlayInfo {

		private EnumPostage postage;

		public StampInfo(String name, EnumPostage postage, int primaryColor, int secondaryColor) {
			super(name, primaryColor, secondaryColor);
			this.postage = postage;
		}

		public EnumPostage getPostage() {
			return this.postage;
		}

	}

	private StampInfo[] stampInfo;

	public ItemStamps(int i, StampInfo[] overlays) {
		super(i, CreativeTabForestry.tabForestry, overlays);
		this.stampInfo = overlays;
	}

	@Override
	public EnumPostage getPostage(ItemStack itemstack) {
		if (itemstack.itemID != this.itemID)
			return EnumPostage.P_0;

		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= stampInfo.length)
			return EnumPostage.P_0;

		return stampInfo[itemstack.getItemDamage()].getPostage();
	}
}
