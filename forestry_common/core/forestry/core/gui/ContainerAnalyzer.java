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
package forestry.core.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import forestry.core.gadgets.MachineAnalyzer;
import forestry.core.genetics.ItemGE;

public class ContainerAnalyzer extends ContainerLiquidTanks {

	private MachineAnalyzer tile;

	public ContainerAnalyzer(InventoryPlayer player, MachineAnalyzer tile) {
		super(tile, tile);

		this.tile = tile;

		// Input buffer
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 2; k++) {
				addSlot(new SlotCustom(tile, new Object[] { ItemGE.class }, MachineAnalyzer.SLOT_INPUT_1 + i * 2 + k, 8 + k * 18, 28 + i * 18));
			}
		}

		// Analyze slot
		addSlot(new SlotCustom(tile, new Object[] {}, MachineAnalyzer.SLOT_ANALYZE, 73, 59));

		// Can slot
		addSlot(new SlotLiquidContainer(tile, MachineAnalyzer.SLOT_CAN, 143, 24));

		// Output buffer
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 2; k++) {
				addSlot(new SlotCustom(tile, new Object[] { ItemGE.class }, MachineAnalyzer.SLOT_OUTPUT_1 + i * 2 + k, 134 + k * 18, 48 + i * 18));
			}
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlot(new Slot(player, l1 + i1 * 9 + 9, 8 + l1 * 18, 94 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlot(new Slot(player, j1, 8 + j1 * 18, 152));
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
