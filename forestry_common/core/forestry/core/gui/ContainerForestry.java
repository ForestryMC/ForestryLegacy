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
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import forestry.core.proxy.Proxies;

public class ContainerForestry extends Container {

	protected IInventory inventory;
	private int slotCount;

	public ContainerForestry(IInventory inventory) {
		this(inventory, inventory.getSizeInventory());
	}

	public ContainerForestry(IInventory inventory, int slotCount) {
		this.inventory = inventory;
		this.slotCount = slotCount;
	}

	/**
	 * Adds a slot to the container, shortcut for addSlotToContainer(slot).
	 * 
	 * @param slot
	 */
	protected void addSlot(Slot slot) {
		this.addSlotToContainer(slot);
	}

	@Override
	public ItemStack slotClick(int slotIndex, int button, int modifier, EntityPlayer player) {

		if (slotIndex == -999)
			return super.slotClick(slotIndex, button, modifier, player);

		Slot slot = getSlot(slotIndex);
		if (!(slot instanceof SlotForestry))
			return super.slotClick(slotIndex, button, modifier, player);

		if (((SlotForestry) slot).isPhantom()) {
			phantomClick(slot, button, modifier, player);
		} else {
			return super.slotClick(slotIndex, button, modifier, player);
		}

		return null;
	}

	private void phantomClick(Slot slot, int button, int modifier, EntityPlayer player) {
		if (button == 1) {
			slot.putStack(null);
			return;
		}

		ItemStack held = player.inventory.getItemStack();
		if (held == null)
			return;

		slot.putStack(new ItemStack(held.itemID, 1, held.getItemDamage()));
	}

	@Override
	public final ItemStack transferStackInSlot(EntityPlayer player, int i) {

		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);

		// Dirty hack to prevent shift clicking on product displays in carpenter
		// and moistener.
		if (slot instanceof SlotWorking)
			return null;

		if (slot != null && slot.getHasStack()) {

			ItemStack itemstackMerge = slot.getStack();
			itemstack = itemstackMerge.copy();
			if (i < slotCount) {
				mergeItemStack(itemstackMerge, slotCount, inventorySlots.size(), true);
			} else {
				mergeItemStack(itemstackMerge, 0, slotCount, false);
			}

			if (itemstackMerge.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstackMerge.stackSize != itemstack.stackSize) {
				slot.onPickupFromSlot(player, itemstackMerge);
			} else
				return null;

		}

		return itemstack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack transferStack, int startSlot, int endSlot, boolean flag) {

		boolean didTransfer = false;
		int k = startSlot;
		if (flag) {
			k = endSlot - 1;
		}

		if (transferStack.isStackable()) {
			while (transferStack.stackSize > 0 && (!flag && k < endSlot || flag && k >= startSlot)) {

				Slot slot = (Slot) inventorySlots.get(k);
				ItemStack inventoryStack = slot.getStack();

				if (inventoryStack != null && inventoryStack.itemID == transferStack.itemID && slot.isItemValid(transferStack)
						&& (!transferStack.getHasSubtypes() || transferStack.getItemDamage() == inventoryStack.getItemDamage())
						&& Proxies.common.isItemStackTagEqual(transferStack, inventoryStack) && inventoryStack.stackSize < slot.getSlotStackLimit()) {

					int combinedStackSize = inventoryStack.stackSize + transferStack.stackSize;

					if (combinedStackSize > slot.getSlotStackLimit()) {
						transferStack.stackSize -= slot.getSlotStackLimit() - inventoryStack.stackSize;
						inventoryStack.stackSize = slot.getSlotStackLimit();
						didTransfer = true;

					} else if (combinedStackSize <= transferStack.getMaxStackSize()) {
						transferStack.stackSize = 0;
						inventoryStack.stackSize = combinedStackSize;
						slot.onSlotChanged();
						didTransfer = true;

					} else if (inventoryStack.stackSize < transferStack.getMaxStackSize()) {
						transferStack.stackSize -= transferStack.getMaxStackSize() - inventoryStack.stackSize;
						inventoryStack.stackSize = transferStack.getMaxStackSize();
						slot.onSlotChanged();
						didTransfer = true;
					}
				}

				if (flag) {
					k--;
				} else {
					k++;
				}
			}
		}

		// Did not add everything to existing stacks, so let's look at the empty ones.
		if (transferStack.stackSize > 0) {
			int l;
			if (flag) {
				l = endSlot - 1;
			} else {
				l = startSlot;
			}
			do {
				if ((flag || l >= endSlot) && (!flag || l < startSlot)) {
					break;
				}

				Slot slot = (Slot) inventorySlots.get(l);
				ItemStack inventoryStack = slot.getStack();
				if (inventoryStack == null && slot.isItemValid(transferStack)) {

					if (transferStack.stackSize > slot.getSlotStackLimit()) {
						slot.putStack(new ItemStack(transferStack.itemID, slot.getSlotStackLimit(), transferStack.getItemDamage()));
						slot.onSlotChanged();
						transferStack.stackSize -= slot.getSlotStackLimit();
					} else {
						slot.putStack(transferStack.copy());
						slot.onSlotChanged();
						transferStack.stackSize = 0;
					}
					didTransfer = true;
					break;

				}

				if (flag) {
					l--;
				} else {
					l++;
				}
			} while (true);
		}
		return didTransfer;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

}
