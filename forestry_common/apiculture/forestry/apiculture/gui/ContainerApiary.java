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

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import forestry.api.apiculture.IHiveFrame;
import forestry.apiculture.gadgets.MachineApiary;
import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.SlotClosed;
import forestry.core.gui.SlotCustom;

public class ContainerApiary extends ContainerForestry {

	private MachineApiary tile;

	public ContainerApiary(InventoryPlayer player, MachineApiary tile) {
		super(tile);

		this.tile = tile;
		tile.sendNetworkUpdate();

		// Queen/Princess
		this.addSlot(new SlotCustom(tile, new Object[] { ForestryItem.beePrincessGE, ForestryItem.beeQueenGE }, MachineApiary.SLOT_QUEEN, 29, 39));

		// Drone
		this.addSlot(new SlotCustom(tile, new Object[] { ForestryItem.beeDroneGE }, MachineApiary.SLOT_DRONE, 29, 65));

		// Frames
		this.addSlot(new SlotCustom(tile, new Object[] { IHiveFrame.class }, MachineApiary.SLOT_FRAMES_1, 66, 23));
		this.addSlot(new SlotCustom(tile, new Object[] { IHiveFrame.class }, MachineApiary.SLOT_FRAMES_1 + 1, 66, 52));
		this.addSlot(new SlotCustom(tile, new Object[] { IHiveFrame.class }, MachineApiary.SLOT_FRAMES_1 + 2, 66, 81));

		// Product Inventory
		this.addSlot(new SlotClosed(tile, 2, 116, 52));
		this.addSlot(new SlotClosed(tile, 3, 137, 39));
		this.addSlot(new SlotClosed(tile, 4, 137, 65));
		this.addSlot(new SlotClosed(tile, 5, 116, 78));
		this.addSlot(new SlotClosed(tile, 6, 95, 65));
		this.addSlot(new SlotClosed(tile, 7, 95, 39));
		this.addSlot(new SlotClosed(tile, 8, 116, 26));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlot(new Slot(player, l1 + i1 * 9 + 9, 8 + l1 * 18, 108 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlot(new Slot(player, j1, 8 + j1 * 18, 166));
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
