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
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import forestry.api.apiculture.BeeManager;
import forestry.apiculture.items.ItemBeeGE;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.SlotCustom;
import forestry.core.proxy.Proxies;

public class ContainerApiaristInventory extends ContainerForestry {

	private IInventory inventory;

	public ContainerApiaristInventory(InventoryPlayer player, IInventory inventory, int pages, int pageSize) {
		super(inventory);

		this.inventory = inventory;

		// Inventory
		for (int z = 0; z < pages; z++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					addSlot(new SlotCustom(inventory, new Object[] { ItemBeeGE.class }, k + z * pageSize + j * 5, 100 + k * 18, 21 + j * 18));
				}
			}
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlot(new Slot(player, l1 + i1 * 9 + 9, 18 + l1 * 18, 120 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlot(new Slot(player, j1, 18 + j1 * 18, 178));
		}
	}

	public void purgeBag(EntityPlayer player) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) {
				continue;
			}

			if (BeeManager.beeInterface.isBee(stack)) {
				continue;
			}

			Proxies.common.dropItemPlayer(player, stack);
			inventory.setInventorySlotContents(i, null);
		}
	}
}
