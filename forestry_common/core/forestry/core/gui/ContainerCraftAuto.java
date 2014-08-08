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

import net.minecraft.inventory.IInventory;
import forestry.core.gadgets.TileMachine;

public abstract class ContainerCraftAuto extends ContainerLiquidTanks {

	public ContainerCraftAuto(IInventory inventory, TileMachine tile) {
		this(inventory, tile, inventory.getSizeInventory());
	}

	public ContainerCraftAuto(IInventory inventory, TileMachine tile, int slotCount) {
		super(inventory, tile, slotCount);
	}

	/**
	 * Replaces the original onCraftMatrixChanged
	 * 
	 * @param iinventory
	 * @param slot
	 */
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		inventory.setInventorySlotContents(slot, iinventory.getStackInSlot(slot));
	}

}
