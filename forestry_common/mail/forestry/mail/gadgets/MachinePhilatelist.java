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
package forestry.mail.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import forestry.api.core.ForestryAPI;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.StackUtils;
import forestry.mail.IStamps;
import forestry.mail.PostOffice;

public class MachinePhilatelist extends TileBase implements IInventory {

	// / CONSTANTS
	public static final short SLOT_FILTER = 0;
	public static final short SLOT_BUFFER_1 = 1;
	public static final short SLOT_BUFFER_COUNT = 27;

	private GenericInventoryAdapter inventory = new GenericInventoryAdapter(28, "INV");

	public MachinePhilatelist() {
	}

	@Override
	public String getInvName() {
		return "tile.mill." + Defaults.ID_PACKAGE_MILL_PHILATELIST;
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.PhilatelistGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		
		inventory.readFromNBT(nbttagcompound);
	}

	// / UPDATING
	@Override
	public void updateServerSide() {
		if (worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		ItemStack stamp = null;

		if (inventory.getStackInSlot(SLOT_FILTER) == null) {
			stamp = PostOffice.getPostOffice(worldObj).getAnyStamp(1);
		} else {
			ItemStack filter = inventory.getStackInSlot(SLOT_FILTER);
			if (filter.getItem() instanceof IStamps) {
				stamp = PostOffice.getPostOffice(worldObj).getAnyStamp(((IStamps) filter.getItem()).getPostage(filter), 1);
			}
		}

		if (stamp == null)
			return;

		// Store it.
		StackUtils.stowInInventory(stamp, inventory, true, SLOT_BUFFER_1, SLOT_BUFFER_COUNT);
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void onInventoryChanged() {
		inventory.onInventoryChanged();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

}
