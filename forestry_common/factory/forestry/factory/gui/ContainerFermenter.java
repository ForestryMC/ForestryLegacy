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
package forestry.factory.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import forestry.core.gadgets.TileMachine;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.SlotLiquidContainer;
import forestry.factory.gadgets.MachineFermenter;

public class ContainerFermenter extends ContainerLiquidTanks {
	protected TileMachine fermenter;

	public ContainerFermenter(InventoryPlayer player, TileMachine fermenter) {
		super(fermenter, fermenter);

		this.fermenter = fermenter;
		this.addSlot(new Slot(fermenter, MachineFermenter.SLOT_RESOURCE, 85, 23));
		this.addSlot(new Slot(fermenter, MachineFermenter.SLOT_FUEL, 75, 57));
		this.addSlot(new Slot(fermenter, MachineFermenter.SLOT_CAN_OUTPUT, 150, 58));
		this.addSlot(new SlotLiquidContainer(fermenter, MachineFermenter.SLOT_CAN_INPUT, 150, 22, true));
		this.addSlot(new SlotLiquidContainer(fermenter, MachineFermenter.SLOT_INPUT, 10, 40));

		for (int i = 0; i < 3; ++i) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlot(new Slot(player, var4 + i * 9 + 9, 8 + var4 * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));
		}

	}

	@Override
	public void updateProgressBar(int i, int j) {
		fermenter.machine.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			fermenter.machine.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return fermenter.isUseableByPlayer(entityplayer);
	}
}
