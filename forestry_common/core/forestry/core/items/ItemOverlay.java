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
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;

public class ItemOverlay extends ItemForestry {

	public static class OverlayInfo {

		public String name;
		public int primaryColor = 0;
		public int secondaryColor = 0;
		public boolean isSecret = false;

		public OverlayInfo(String name, int primaryColor, int secondaryColor) {
			this.name = name;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
		}

		public OverlayInfo(String name, int primaryColor) {
			this(name, primaryColor, 0);
		}

		public OverlayInfo setIsSecret() {
			isSecret = true;
			return this;
		}
	}

	private OverlayInfo[] overlays;
	private int primaryIconIndex = 0;
	private int secondaryIconIndex = 0;

	public ItemOverlay(int i, CreativeTabs tab, OverlayInfo[] overlays) {
		super(i);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(tab);

		this.overlays = overlays;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	public ItemOverlay setIcons(int primary, int secondary) {
		this.primaryIconIndex = primary;
		this.secondaryIconIndex = secondary;
		return this;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < overlays.length; i++)
			if (Defaults.DEBUG || !overlays[i].isSecret) {
				itemList.add(new ItemStack(this, 1, i));
			}
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		if (secondaryIconIndex != 0)
			return true;

		return false;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= overlays.length)
			return null;

		return StringUtil.localize(getItemName() + "." + overlays[itemstack.getItemDamage()].name);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0 || overlays[itemstack.getItemDamage()].secondaryColor == 0)
			return overlays[itemstack.getItemDamage()].primaryColor;
		else
			return overlays[itemstack.getItemDamage()].secondaryColor;
	}

	@Override
	public int getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0 && overlays[i].secondaryColor != 0)
			return secondaryIconIndex;
		else
			return primaryIconIndex;
	}

}
