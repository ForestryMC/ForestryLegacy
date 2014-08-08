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
package forestry.arboriculture.gui;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemTreealyzer.TreealyzerInventory;
import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.SlotCustom;
import forestry.core.proxy.Proxies;

public class ContainerTreealyzer extends ContainerItemInventory {

	TreealyzerInventory inventory;

	public ContainerTreealyzer(InventoryPlayer inventoryplayer, TreealyzerInventory inventory) {
		super(inventory);

		this.inventory = inventory;

		// Energy
		this.addSlot(new SlotCustom(inventory, new Object[] { ForestryItem.honeydew, ForestryItem.honeyDrop }, TreealyzerInventory.SLOT_ENERGY, 172, 8));

		// Bee to analyze
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemGermlingGE.class, Block.sapling }, TreealyzerInventory.SLOT_SPECIMEN, 172, 26));

		// Analyzed bee
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemGermlingGE.class }, TreealyzerInventory.SLOT_ANALYZE_1, 172, 57));
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemGermlingGE.class }, TreealyzerInventory.SLOT_ANALYZE_2, 172, 75));
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemGermlingGE.class }, TreealyzerInventory.SLOT_ANALYZE_3, 172, 93));
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemGermlingGE.class }, TreealyzerInventory.SLOT_ANALYZE_4, 172, 111));
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemGermlingGE.class }, TreealyzerInventory.SLOT_ANALYZE_5, 172, 129));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 18 + l1 * 18, 156 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlot(new Slot(inventoryplayer, j1, 18 + j1 * 18, 214));
		}

	}

	@Override
	public void onCraftGuiClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		// Last slot is the energy slot, so we don't save that one.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if(i == TreealyzerInventory.SLOT_ENERGY)
				continue;
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) {
				continue;
			}

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}

		inventory.onGuiSaved(entityplayer);

	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		return true;
	}

}
