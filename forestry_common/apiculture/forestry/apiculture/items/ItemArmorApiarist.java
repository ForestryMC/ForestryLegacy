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
package forestry.apiculture.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IArmorTextureProvider;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.Tabs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.utils.StringUtil;

public class ItemArmorApiarist extends ItemArmor implements IArmorTextureProvider, IArmorApiarist {

	public ItemArmorApiarist(int id, int slot) {
		super(id, EnumArmorMaterial.CLOTH, 0, slot);
		this.setMaxDamage(100);
		this.setTextureFile(Defaults.TEXTURE_ITEMS);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public String getArmorTextureFile(ItemStack itemstack) {
		if (itemstack.itemID == ForestryItem.apiaristLegs.itemID)
			return Defaults.TEXTURE_APIARIST_ARMOR_SECONDARY;
		else
			return Defaults.TEXTURE_APIARIST_ARMOR_PRIMARY;
	}

	@Override
	public int getIconFromDamageForRenderPass(int par1, int par2) {
		return this.iconIndex;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getItemNameIS(itemstack));
	}

	public static boolean wearsHelmet(EntityPlayer player) {
		ItemStack armorItem = player.inventory.armorInventory[3];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist;
	}

	public static boolean wearsChest(EntityPlayer player) {
		ItemStack armorItem = player.inventory.armorInventory[2];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist;
	}

	public static boolean wearsLegs(EntityPlayer player) {
		ItemStack armorItem = player.inventory.armorInventory[1];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist;
	}

	public static boolean wearsBoots(EntityPlayer player) {
		ItemStack armorItem = player.inventory.armorInventory[0];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist;
	}

	public static int wearsItems(EntityPlayer player) {
		int count = 0;

		if (wearsHelmet(player)) {
			count++;
		}
		if (wearsChest(player)) {
			count++;
		}
		if (wearsLegs(player)) {
			count++;
		}
		if (wearsBoots(player)) {
			count++;
		}

		return count;
	}
}
