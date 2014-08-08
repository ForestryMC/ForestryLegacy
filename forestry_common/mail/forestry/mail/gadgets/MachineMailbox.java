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

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.inventory.ISpecialInventory;
import forestry.api.core.ForestryAPI;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.StringUtil;
import forestry.mail.EnumDeliveryState;
import forestry.mail.ILetter;
import forestry.mail.IMailContainer;
import forestry.mail.IPostalState;
import forestry.mail.POBox;
import forestry.mail.PostOffice;
import forestry.mail.items.ItemLetter;
import forestry.plugins.PluginForestryMail;

public class MachineMailbox extends TileBase implements IMailContainer, ISpecialInventory {

	private boolean isLinked = false;

	public MachineMailbox() {
		setHints(Config.hints.get("mailbox"));
	}

	@Override
	public String getInvName() {
		return "tile.mill." + Defaults.ID_PACKAGE_MILL_MAILBOX;
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, TileBase tile) {

		if (!Proxies.common.isSimulating(worldObj))
			return;

		ItemStack held = player.getCurrentEquippedItem();

		// Handle letter sending
		if (ItemLetter.isLetter(held)) {
			IPostalState result = this.tryDispatchLetter(held, true);
			if (!result.isOk()) {
				player.addChatMessage(StringUtil.localize("chat.mail." + result.getIdentifier()));
			} else {
				held.stackSize--;
			}
		} else {
			player.openGui(ForestryAPI.instance, GuiId.MailboxGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
		nbttagcompound.setBoolean("LNK", this.isLinked);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		
		this.isLinked = nbttagcompound.getBoolean("LNK");
	}

	/* UPDATING */
	@Override
	public void updateServerSide() {
		if (!isLinked) {
			getOrCreateMailInventory();
			isLinked = true;
		}
	}

	/* MAIL HANDLING */
	public IInventory getOrCreateMailInventory() {

		// Handle client side
		if (!Proxies.common.isSimulating(worldObj))
			return new GenericInventoryAdapter(POBox.SLOT_SIZE, "Letters");

		if (getOwnerName() == null || getOwnerName().isEmpty())
			return new GenericInventoryAdapter(POBox.SLOT_SIZE, "Letters");

		return PostOffice.getOrCreatePOBox(worldObj, getOwnerName());
	}

	private IPostalState tryDispatchLetter(ItemStack letterstack, boolean dispatchLetter) {
		ILetter letter = ItemLetter.getLetter(letterstack);
		IPostalState result;

		if (letter != null) {
			result = PostOffice.getPostOffice(worldObj).lodgeLetter(worldObj, letterstack, dispatchLetter);
		} else {
			result = EnumDeliveryState.NOT_MAILABLE;
		}

		return result;
	}

	/* IMAILCONTAINER */
	@Override
	public boolean hasMail() {

		IInventory mailInventory = getOrCreateMailInventory();
		for (int i = 0; i < mailInventory.getSizeInventory(); i++)
			if (mailInventory.getStackInSlot(i) != null)
				return true;

		return false;
	}

	/* ITRIGGERPROVIDER */
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(PluginForestryMail.triggerHasMail);
		return res;
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (!ItemLetter.isLetter(stack))
			return 0;

		IPostalState result = tryDispatchLetter(stack, doAdd);

		if (!result.isOk())
			return 0;
		else
			return 1;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		ItemStack product = null;
		IInventory mailInventory = getOrCreateMailInventory();

		for (int i = 0; i < mailInventory.getSizeInventory(); i++) {
			ItemStack slotStack = mailInventory.getStackInSlot(i);
			if (slotStack == null) {
				continue;
			}

			product = slotStack;
			if (doRemove) {
				mailInventory.setInventorySlotContents(i, null);
			}
			break;
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return new ItemStack[0];
	}

	@Override public int getSizeInventory() { return 0; }
	@Override public ItemStack getStackInSlot(int var1) { return null; }
	@Override public ItemStack decrStackSize(int var1, int var2) { return null; }
	@Override public ItemStack getStackInSlotOnClosing(int var1) { return null; }
	@Override public void setInventorySlotContents(int var1, ItemStack var2) {}
	@Override public int getInventoryStackLimit() { return 0; }
	@Override public void openChest() {}
	@Override public void closeChest() {}
}
