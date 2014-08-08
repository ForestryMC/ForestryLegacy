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
package forestry.core.circuits;

import java.util.List;
import java.util.Locale;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemForestry;
import forestry.core.utils.StringUtil;

public class ItemCircuitBoard extends ItemForestry {

	public ItemCircuitBoard(int i) {
		super(i);
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(createCircuitboard(EnumCircuitBoardType.BASIC, null, new ICircuit[] {}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.ENHANCED, null, new ICircuit[] {}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[] {}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.INTRICATE, null, new ICircuit[] {}));
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[itemstack.getItemDamage()];
		if (j == 0)
			return type.primaryColor;
		else
			return type.secondaryColor;
	}

	// Return texture index for color overlay
	@Override
	public int getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0)
			return 22;
		else
			return 23;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[itemstack.getItemDamage()];
		return StringUtil.localize("item.circuitboard." + type.toString().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		ICircuitBoard circuitboard = getCircuitboard(itemstack);
		if (circuitboard != null) {
			circuitboard.addTooltip(list);
		}
	}

	/* HELPER FUNCTIONS */
	public static boolean isChipset(ItemStack itemstack) {
		if (itemstack == null)
			return false;

		return itemstack.itemID == ForestryItem.circuitboards.itemID;
	}

	public static ItemStack createCircuitboard(EnumCircuitBoardType type, ICircuitLayout layout, ICircuit[] circuits) {
		ItemStack chipset = new ItemStack(ForestryItem.circuitboards, 1, type.ordinal());
		saveChipset(chipset, new CircuitBoard(type, layout, circuits));
		return chipset;
	}

	public static void saveChipset(ItemStack itemstack, ICircuitBoard circuitboard) {
		if (circuitboard == null) {
			itemstack.setTagCompound(null);
			return;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		circuitboard.writeToNBT(nbttagcompound);
		itemstack.setTagCompound(nbttagcompound);
	}

	public static ICircuitBoard getCircuitboard(ItemStack itemstack) {
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		if (nbttagcompound == null)
			return null;

		return new CircuitBoard(nbttagcompound);
	}
}
