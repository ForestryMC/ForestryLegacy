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

import net.minecraft.inventory.IInventory;
import forestry.core.gui.ContainerCraftAuto;
import forestry.core.gui.SlotForestry;

/**
 * Informs the passed container of slot changes. Contains a dummy itemstack.
 */
public class SlotCraftMatrix extends SlotForestry {

	private ContainerCraftAuto eventHandler;
	private int slot;

	public SlotCraftMatrix(ContainerCraftAuto container, IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k, true);
		this.eventHandler = container;
		this.slot = i;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		eventHandler.onCraftMatrixChanged(inventory, slot);
	}

}
