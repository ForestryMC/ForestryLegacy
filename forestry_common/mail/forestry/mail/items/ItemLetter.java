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
package forestry.mail.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.IInventoriedItem;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;
import forestry.core.utils.StringUtil;
import forestry.mail.ILetter;
import forestry.mail.Letter;

public class ItemLetter extends ItemForestry implements IInventoriedItem {

	public static class LetterInventory extends ItemInventory implements IErrorSource, IHintSource {

		ILetter letter;

		protected LetterInventory() {
		}

		public LetterInventory(ItemStack itemstack) {
			this.parent = itemstack;
			this.isItemInventory = true;

			// Set an uid to identify the itemstack on SMP
			setUID();

			readFromNBT(itemstack.getTagCompound());
		}

		public ILetter getLetter() {
			return this.letter;
		}

		@Override
		public void onGuiSaved(EntityPlayer player) {
			super.onGuiSaved(player);

			// Already delivered mails can't be made usable anymore.
			int state = getState(parent.getItemDamage());
			if (state >= 2) {
				if (state == 2 && letter.countAttachments() <= 0) {
					parent.setItemDamage(encodeMeta(3, getSize(parent.getItemDamage())));
				}
				return;
			}

			int type = getType(letter);

			if (parent != null && letter.isMailable() && letter.isPostPaid()) {
				parent.setItemDamage(encodeMeta(1, type));
			} else {
				parent.setItemDamage(encodeMeta(0, type));
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {

			if (nbttagcompound == null)
				return;

			letter = new Letter(nbttagcompound);
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			letter.writeToNBT(nbttagcompound);
		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			return letter.decrStackSize(i, j);
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
			letter.setInventorySlotContents(i, itemstack);
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return letter.getStackInSlot(i);
		}

		@Override
		public int getSizeInventory() {
			return letter.getSizeInventory();
		}

		@Override
		public String getInvName() {
			return letter.getInvName();
		}

		@Override
		public int getInventoryStackLimit() {
			return letter.getInventoryStackLimit();
		}

		@Override
		public void onInventoryChanged() {
			letter.onInventoryChanged();
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return letter.isUseableByPlayer(entityplayer);
		}

		@Override
		public void openChest() {
		}

		@Override
		public void closeChest() {
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return letter.getStackInSlotOnClosing(slot);
		}

		// / IERRORSOURCE
		@Override
		public boolean throwsErrors() {
			return true;
		}

		@Override
		public EnumErrorCode getErrorState() {

			if (!letter.hasRecipient())
				return EnumErrorCode.NORECIPIENT;

			if (!letter.isProcessed() && !letter.isPostPaid())
				return EnumErrorCode.NOTPOSTPAID;

			return EnumErrorCode.OK;
		}

		// / IHINTSOURCE
		@Override
		public boolean hasHints() {
			return Config.hints.get("letter") != null && Config.hints.get("letter").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("letter");
		}

	}

	public ItemLetter(int i) {
		super(i);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			if(itemstack.stackSize == 1)
				entityplayer.openGui(ForestryAPI.instance, GuiId.LetterGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public int getIconFromDamage(int damage) {

		int state = getState(damage);
		int size = getSize(damage);

		int icon = 52;

		if (state > 2) {
			icon = 55;
		} else if (state > 1) {
			icon = 54;
		} else if (state > 0) {
			icon = 53;
		}

		icon += (size * 16);
		return icon;
	}

	public static int encodeMeta(int state, int size) {
		int meta = size << 4;
		meta |= state;

		size = getSize(meta);
		state = getState(meta);

		return meta;
	}

	public static int getState(int meta) {
		return meta & 0x0f;
	}

	public static int getSize(int meta) {
		return meta >> 4;
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		if (nbttagcompound == null) {
			list.add("<" + StringUtil.localize("gui.blank") + ">");
			return;
		}

		ILetter letter = new Letter(nbttagcompound);
		letter.addTooltip(list);
	}

	public static ILetter getLetter(ItemStack itemstack) {
		if (itemstack == null)
			return null;

		if (!isLetter(itemstack))
			return null;

		if (itemstack.getTagCompound() == null)
			return null;

		return new Letter(itemstack.getTagCompound());
	}

	public static boolean isLetter(ItemStack itemstack) {
		if (itemstack == null)
			return false;

		return itemstack.itemID == ForestryItem.letters.itemID;
	}

	public static int getType(ILetter letter) {
		int count = letter.countAttachments();

		if (count > 5)
			return 2;
		else if (count > 1)
			return 1;
		else
			return 0;
	}

}
