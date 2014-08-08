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
package forestry.mail;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.mail.items.ItemLetter;

public class POBox extends WorldSavedData implements IInventory {

	public static final String SAVE_NAME = "POBox_";
	public static final short SLOT_SIZE = 84;

	private String owner;
	private GenericInventoryAdapter letters = new GenericInventoryAdapter(SLOT_SIZE, "Letters");

	public POBox(String owner, boolean isUser) {
		super(SAVE_NAME + owner);
		this.owner = owner;
	}

	public POBox(String savename) {
		super(savename);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		owner = nbttagcompound.getString("Owner");
		letters.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("Owner", owner);
		letters.writeToNBT(nbttagcompound);
	}

	public String getOwnerName() {
		return this.owner;
	}

	public boolean storeLetter(ItemStack letterstack) {
		ILetter letter = ItemLetter.getLetter(letterstack);

		// Mark letter as processed
		letter.setProcessed(true);
		letter.invalidatePostage();
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		letter.writeToNBT(nbttagcompound);
		letterstack.setTagCompound(nbttagcompound);

		this.markDirty();
		return this.letters.tryAddStack(letterstack, true);
	}

	public POBoxInfo getPOBoxInfo() {
		int playerLetters = 0;
		int tradeLetters = 0;
		for (int i = 0; i < letters.getSizeInventory(); i++) {
			if (letters.getStackInSlot(i) == null) {
				continue;
			}
			ILetter letter = new Letter(letters.getStackInSlot(i).getTagCompound());
			if (letter.getSender().isPlayer()) {
				playerLetters++;
			} else {
				tradeLetters++;
			}
		}

		return new POBoxInfo(playerLetters, tradeLetters);
	}

	/* IINVENTORY */
	@Override
	public void onInventoryChanged() {
		this.markDirty();
		letters.onInventoryChanged();
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.markDirty();
		letters.setInventorySlotContents(var1, var2);
	}

	@Override
	public int getSizeInventory() {
		return letters.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return letters.getStackInSlot(var1);
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return letters.decrStackSize(var1, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return letters.getStackInSlotOnClosing(var1);
	}

	@Override
	public String getInvName() {
		return letters.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return letters.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return letters.isUseableByPlayer(var1);
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

}
