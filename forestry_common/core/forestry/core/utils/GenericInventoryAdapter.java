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
package forestry.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import forestry.api.core.INBTTagable;

/**
 * With permission from Krapht.
 */
public class GenericInventoryAdapter implements IInventory, INBTTagable {

	private PlainInventory inventory = null;

	public GenericInventoryAdapter(int size, String name) {
		this(size, name, 64);
	}

	public GenericInventoryAdapter(int size, String name, int stackLimit) {
		this.inventory = new PlainInventory(size, name, stackLimit);
	}

	/**
	 * @return Copy of this inventory. Stacks are copies.
	 */
	public GenericInventoryAdapter copy() {
		GenericInventoryAdapter copy = new GenericInventoryAdapter(inventory.getSizeInventory(), inventory.getInvName(), inventory.getInventoryStackLimit());

		for (int i = 0; i < inventory.getSizeInventory(); i++)
			if (inventory.getStackInSlot(i) != null) {
				copy.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
			}

		return copy;
	}

	public ItemStack[] getStacks() {
		return inventory.getContents();
	}

	public ItemStack[] getStacks(int slot1, int length) {
		ItemStack[] result = new ItemStack[length];
		System.arraycopy(inventory.getContents(), slot1, result, 0, length);
		return result;
	}

	public boolean tryAddStacksCopy(ItemStack[] stacks, boolean all) {

		boolean addedAll = true;
		for (ItemStack stack : stacks) {
			if (stack == null) {
				continue;
			}

			if (!tryAddStack(stack.copy(), all)) {
				addedAll = false;
			}
		}

		return addedAll;
	}

	public boolean tryAddStacks(ItemStack[] stacks, boolean all) {

		boolean addedAll = true;
		for (ItemStack stack : stacks) {
			if (stack == null) {
				continue;
			}

			if (!tryAddStack(stack, all)) {
				addedAll = false;
			}
		}

		return addedAll;
	}

	public boolean tryAddStack(ItemStack stack, boolean all) {
		return tryAddStack(stack, 0, this.getSizeInventory(), all);
	}

	/**
	 * Tries to add a stack to the specified slot range.
	 * 
	 * @param stack
	 * @param startSlot
	 * @param slots
	 * @param all
	 * @return
	 */
	public boolean tryAddStack(ItemStack stack, int startSlot, int slots, boolean all) {
		return tryAddStack(stack, startSlot, slots, all, true);
	}

	public boolean tryAddStack(ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {
		return addStack(stack, startSlot, slots, all, doAdd) > 0;
	}

	public int addStack(ItemStack stack, boolean all, boolean doAdd) {
		return addStack(stack, 0, this.getSizeInventory(), all, doAdd);
	}

	public int addStack(ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {

		int added = 0;
		// Add to existing stacks first
		for (int i = startSlot; i < startSlot + slots; i++) {

			// Empty slot. Add
			if (inventory.getStackInSlot(i) == null) {
				/*
				if (doAdd) {
					setInventorySlotContents(i, stack.copy());
				}
				return stack.stackSize;
				*/
				continue;
			}

			// Already occupied by different item, skip this slot.
			if (!inventory.getStackInSlot(i).isItemEqual(stack)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(inventory.getStackInSlot(i), stack)) {
				continue;
			}

			int remain = stack.stackSize - added;
			int space = inventory.getStackInSlot(i).getMaxStackSize() - inventory.getStackInSlot(i).stackSize;
			// No space left, skip this slot.
			if (space <= 0) {
				continue;
			}
			// Enough space
			if (space >= remain) {
				if (doAdd) {
					inventory.getStackInSlot(i).stackSize += remain;
				}
				return stack.stackSize;
			}

			// Not enough space
			/*
			if (all) {
				continue;
			}
			*/

			if (doAdd) {
				inventory.getStackInSlot(i).stackSize = inventory.getStackInSlot(i).getMaxStackSize();
			}

			added += space;
		}

		if(added >= stack.stackSize)
			return added;
		
		for (int i = startSlot; i < startSlot + slots; i++) {
			if (inventory.getStackInSlot(i) != null)
				continue;
			
			if (doAdd) {
				setInventorySlotContents(i, stack.copy());
				inventory.getStackInSlot(i).stackSize = stack.stackSize - added;
			}
			return stack.stackSize;

		}
		
		return added;

	}
	
	/* CONTAINS */
	public boolean contains(ItemStack[] query, int startSlot, int slots) {
		for(ItemStack queried : query) {
			
			int itemCount = 0;
			for (int i = startSlot; i < startSlot + slots; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				
				if (stack == null)
					continue;
				
				if(queried.itemID < 0) {
					itemCount += stack.stackSize;
					continue;
				}
				if(queried.getItemDamage() < 0) {
					
					if(stack.itemID == queried.itemID)
						itemCount += stack.stackSize;
					continue;
				}
				
				if(stack.isItemEqual(queried)
					&& ItemStack.areItemStackTagsEqual(stack, queried)) {
					itemCount += stack.stackSize;
				}
			}
			
			if(itemCount < queried.stackSize)
				return false;
			
		}
		
		return true;
	}

	/* REMOVAL */
	public void removeResources(ItemStack[] query, int startSlot, int slots) {
		for(ItemStack queried : query) {
			
			ItemStack remain = queried.copy();
			
			for (int i = startSlot; i < startSlot + slots; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				
				if (stack == null)
					continue;
				
				if(queried.getItemDamage() < 0) {
					
					if(stack.itemID == queried.itemID) {
						ItemStack removed = decrStackSize(i, remain.stackSize);
						remain.stackSize -= removed.stackSize;
					}
					
				} else {
				
					if(stack.isItemEqual(remain)
						&& ItemStack.areItemStackTagsEqual(stack, remain)) {
						ItemStack removed = decrStackSize(i, remain.stackSize);
						remain.stackSize -= removed.stackSize;
					}
				}
				
				if(remain.stackSize <= 0)
					break;
			}
			
		}

	}
	
	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return inventory.getStackInSlot(slotId);
	}

	@Override
	public ItemStack decrStackSize(int slotId, int count) {
		return inventory.decrStackSize(slotId, count);
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemstack) {
		inventory.setInventorySlotContents(slotId, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		inventory.onInventoryChanged();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		return inventory.getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey(inventory.getInvName()))
			return;

		NBTTagList nbttaglist = nbttagcompound.getTagList(inventory.getInvName());

		for (int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbttaglist.tagAt(j);
			int index = nbttagcompound2.getByte("Slot");
			inventory.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(nbttagcompound2));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttagcompound2.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}
		}
		nbttagcompound.setTag(inventory.getInvName(), nbttaglist);
	}

}
