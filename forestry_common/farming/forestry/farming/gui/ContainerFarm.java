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
package forestry.farming.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.SlotLiquidContainer;
import forestry.core.utils.TileInventoryAdapter;
import forestry.farming.gadgets.TileFarmPlain;

public class ContainerFarm extends ContainerSocketed {

	TileFarmPlain tile;
	
	public ContainerFarm(InventoryPlayer playerinventory, TileFarmPlain tile) {
		super(playerinventory, tile);

		this.tile = tile;
		
		IInventory inventory = tile.getInventory();
		// Tile will not have an inventory client side.
		if(inventory == null)
			inventory = new TileInventoryAdapter(tile, TileFarmPlain.SLOT_COUNT, "Items");
		
		// Resources
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new Slot(inventory, TileFarmPlain.SLOT_RESOURCES_1 + j + i * 2, 123 + j * 18, 22 + i * 18));
			}
		}
		
		// Germlings
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new Slot(inventory, TileFarmPlain.SLOT_GERMLINGS_1 + j + i * 2, 164 + j * 18, 22 + i * 18));
			}
		}

		// Production 1
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new Slot(inventory, TileFarmPlain.SLOT_PRODUCTION_1 + j + i * 2, 123 + j * 18, 86 + i * 18));
			}
		}

		// Production 2
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new Slot(inventory, TileFarmPlain.SLOT_PRODUCTION_1 + 4 + j + i * 2, 164 + j * 18, 86 + i * 18));
			}
		}

		// Fertilizer
		addSlot(new Slot(inventory, TileFarmPlain.SLOT_FERTILIZER, 63, 95));
		// Can Slot
		addSlot(new SlotLiquidContainer(inventory, TileFarmPlain.SLOT_CAN, 15, 95));
		
		// Player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerinventory, j + i * 9 + 9, 28 + j * 18, 138 + i * 18));
			}
		}
		// Player hotbar
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerinventory, i, 28 + i * 18, 196));
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		tile.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			tile.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}
	}

}
