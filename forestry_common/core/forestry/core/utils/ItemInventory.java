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

public class ItemInventory implements IInventory, INBTTagable {

	public boolean isItemInventory = false;
	public ItemStack parent;
	protected ItemStack[] inventoryStacks;

	protected ItemInventory() {
	}

	public ItemInventory(int slots) {
		inventoryStacks = new ItemStack[slots];
	}

	public ItemInventory(int size, ItemStack itemstack) {
		this(size);
		
		parent = itemstack;
		isItemInventory = true;

		// Set an uid to identify the itemstack on SMP
		setUID();

		readFromNBT(itemstack.getTagCompound());
	}

	protected void setUID() {
		if (parent.getTagCompound() == null) {
			parent.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound nbttagcompound = parent.getTagCompound();
		if (!nbttagcompound.hasKey("UID")) {
			nbttagcompound.setInteger("UID", Utils.getUID());
			parent.setTagCompound(nbttagcompound);
		}
	}

	public void onGuiSaved(EntityPlayer player) {
		parent = determineParentInInventory(player);
		if (parent != null) {
			save();
		}
	}

	public ItemStack determineParentInInventory(EntityPlayer player) {
		if (parent == null)
			return null;

		NBTTagCompound nbttagcompound = parent.getTagCompound();
		if (nbttagcompound == null)
			return null;

		int uid = nbttagcompound.getInteger("UID");

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {

			if (player.inventory.getStackInSlot(i) == null) {
				continue;
			}
			NBTTagCompound nbttagcompoundSlot = player.inventory.getStackInSlot(i).getTagCompound();
			if (nbttagcompoundSlot == null) {
				continue;
			}

			if (uid == nbttagcompoundSlot.getInteger("UID")) {
				return player.inventory.getStackInSlot(i);
			}
		}

		// Search itemstack at mouse cursor
		if (player.inventory.getItemStack() != null) {
			NBTTagCompound nbttagcompoundSlot = player.inventory.getItemStack().getTagCompound();
			if (nbttagcompoundSlot != null && uid == nbttagcompoundSlot.getInteger("UID")) {
				return player.inventory.getItemStack();
			}
		}

		return null;
	}

	public boolean matchesUID(int otherId) {
		if (parent == null)
			return false;

		NBTTagCompound nbttagcompound = parent.getTagCompound();
		if (nbttagcompound == null)
			return false;

		int uid = nbttagcompound.getInteger("UID");
		return uid == otherId;
	}

	public void save() {
		NBTTagCompound nbttagcompound = parent.getTagCompound();
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		writeToNBT(nbttagcompound);
		parent.setTagCompound(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		if (nbttagcompound == null)
			return;

		if (nbttagcompound.hasKey("Items")) {
			NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
			inventoryStacks = new ItemStack[getSizeInventory()];
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
				byte byte0 = nbttagcompound1.getByte("Slot");
				if (byte0 >= 0 && byte0 < inventoryStacks.length) {
					inventoryStacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				}
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventoryStacks.length; i++)
			if (inventoryStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventoryStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("Items", nbttaglist);

	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventoryStacks[i] == null)
			return null;

		ItemStack product;
		if (inventoryStacks[i].stackSize <= j) {
			product = inventoryStacks[i];
			inventoryStacks[i] = null;
			return product;
		} else {
			product = inventoryStacks[i].splitStack(j);
			if (inventoryStacks[i].stackSize == 0) {
				inventoryStacks[i] = null;
			}

			return product;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventoryStacks[i] = itemstack;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks[i];
	}

	@Override
	public int getSizeInventory() {
		return inventoryStacks.length;
	}

	@Override
	public String getInvName() {
		return "BeeBag";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (inventoryStacks[slot] == null)
			return null;
		ItemStack toReturn = inventoryStacks[slot];
		inventoryStacks[slot] = null;
		return toReturn;
	}
}
