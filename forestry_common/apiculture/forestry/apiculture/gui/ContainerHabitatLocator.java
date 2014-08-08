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
package forestry.apiculture.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.apiculture.items.ItemHabitatLocator.HabitatLocatorInventory;
import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.SlotCustom;
import forestry.core.proxy.Proxies;

public class ContainerHabitatLocator extends ContainerItemInventory {

	public HabitatLocatorInventory inventory;

	public ContainerHabitatLocator(InventoryPlayer inventoryplayer, HabitatLocatorInventory inventory) {
		super(inventory);

		this.inventory = inventory;

		// Energy
		this.addSlot(new SlotCustom(inventory, new Object[] { ForestryItem.honeydew, ForestryItem.honeyDrop }, 2, 152, 8));

		// Bee to analyze
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemBeeGE.class }, 0, 152, 32));
		// Analyzed bee
		this.addSlot(new SlotCustom(inventory, new Object[] { ItemBeeGE.class }, 1, 152, 75));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 102 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 160));
		}
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		((ItemHabitatLocator) ForestryItem.biomeFinder).startBiomeSearch(entityplayer.worldObj, entityplayer, inventory.biomesToSearch);

		for (int i = 0; i < inventory.getSizeInventory() - 1; i++) {
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
		return false;
	}

}
