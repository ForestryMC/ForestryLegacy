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
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotForestry extends Slot {

	protected boolean isPhantom;
	protected int stackLimit;

	public SlotForestry(IInventory inventory, int slotIndex, int xPos, int yPos) {
		this(inventory, slotIndex, xPos, yPos, false, -1);
	}

	public SlotForestry(IInventory inventory, int slotIndex, int xPos, int yPos, boolean isPhantom) {
		this(inventory, slotIndex, xPos, yPos, isPhantom, -1);
	}

	public SlotForestry(IInventory inventory, int slotIndex, int xPos, int yPos, int stackLimit) {
		this(inventory, slotIndex, xPos, yPos, false, stackLimit);
	}

	public SlotForestry(IInventory inventory, int slotIndex, int xPos, int yPos, boolean isPhantom, int stackLimit) {
		super(inventory, slotIndex, xPos, yPos);
		this.isPhantom = isPhantom;
		this.stackLimit = stackLimit;
	}

	public SlotForestry setStackLimit(int limit) {
		this.stackLimit = limit;
		return this;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return !isPhantom;
	}

	public boolean isPhantom() {
		return this.isPhantom;
	}

	@Override
	public int getSlotStackLimit() {
		if (stackLimit < 0)
			return super.getSlotStackLimit();
		else
			return stackLimit;
	}
}
