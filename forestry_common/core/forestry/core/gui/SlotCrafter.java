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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import forestry.core.interfaces.ICrafter;

public class SlotCrafter extends Slot {

	ICrafter crafter;

	public SlotCrafter(ICrafter crafter, int slot, int xPos, int yPos) {
		super((IInventory) crafter, slot, xPos, yPos);
		this.crafter = crafter;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		if (!this.getHasStack())
			return null;

		return this.getStack();
	}

	@Override
	public ItemStack getStack() {
		return this.crafter.getResult();
	}

	@Override
	public boolean getHasStack() {
		return this.getStack() != null;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
		crafter.takeResult(true);
	}
}
