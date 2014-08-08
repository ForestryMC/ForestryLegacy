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
package forestry.apiculture.gadgets;

import buildcraft.api.inventory.ISpecialInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.apiculture.BeeManager;
import forestry.api.core.ForestryAPI;
import forestry.core.config.Config;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;
import forestry.core.utils.StringUtil;

public class MachineApiaristChest extends TileBase implements ISpecialInventory {

	/**
	 * Factory class to produce {@link MachineApiaristChest}s.
	 * 
	 * @author SirSengir
	 * 
	 */
	/*
	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineApiaristChest((TileMachine) tile);
		}
	}
	*/

	private ItemStack[] inventoryStacks = new ItemStack[125];

	public MachineApiaristChest() {
		setHints(Config.hints.get("apiarist.chest"));
	}

	@Override
	public String getInvName() {
		return StringUtil.localize("tile.mill.3");
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.ApiaristChestGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	// / SAVING & LOADING
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

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
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

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

	// / WORKING
	@Override
	public void updateServerSide() {
	}

	// / ERROR HANDLING
	@Override
	public boolean throwsErrors() {
		return false;
	}

	// / ISPECIALINVENTORY
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		if (!BeeManager.beeInterface.isBee(stack))
			return 0;

		int freeSlots = 0;
		int slot = -1;
		for (int i = 0; i < getSizeInventory(); i++) {

			// We do not add at once to free inventorySlots.
			if (getStackInSlot(i) == null) {
				freeSlots++;
				if (slot < 0) {
					slot = i;
				}
				continue;
			}

		}

		// Now let's check if there are more than two free slots available since
		// we couldn't add everything to already occupied slots
		if (freeSlots <= 0)
			return 0;

		if (doAdd) {
			setInventorySlotContents(slot, stack.copy());
		}
		return stack.stackSize;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product = null;

		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) == null) {
				continue;
			}

			product = getStackInSlot(i).copy();
			if (doRemove) {
				getStackInSlot(i).stackSize = 0;
				setInventorySlotContents(i, null);
			}
			break;
		}

		return new ItemStack[] { product };
	}

	// / IINVENTORY
	@Override
	public int getSizeInventory() {
		return inventoryStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks[i];
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
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (inventoryStacks[slot] == null)
			return null;
		ItemStack toReturn = inventoryStacks[slot];
		inventoryStacks[slot] = null;
		return toReturn;
	}

	@Override public int getInventoryStackLimit() { return 64; }
	@Override public void openChest() {}
	@Override public void closeChest() {}

}
