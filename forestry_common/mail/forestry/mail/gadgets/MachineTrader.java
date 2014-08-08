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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.inventory.ISpecialInventory;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.StackUtils;
import forestry.mail.IStamps;
import forestry.mail.PostOffice;
import forestry.mail.TradeStation;
import forestry.plugins.PluginForestryMail;

public class MachineTrader extends TileBase implements ISpecialInventory, ISidedInventory {

	@EntityNetData
	public String moniker = "";

	@Override
	public String getInvName() {
		return "tile.mill." + Defaults.ID_PACKAGE_MILL_TRADER;
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		if (isLinked()) {
			player.openGui(ForestryAPI.instance, GuiId.TraderGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		} else {
			player.openGui(ForestryAPI.instance, GuiId.TraderNameGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void onRemoval() {
		if (isLinked()) {
			PostOffice.deleteTradeStation(worldObj, moniker);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
		if (moniker != null) {
			nbttagcompound.setString("MNK", moniker);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		
		if (nbttagcompound.hasKey("MNK")) {
			this.moniker = nbttagcompound.getString("MNK");
		}
	}

	/* UPDATING */
	@Override
	public void updateServerSide() {

		if (worldObj.getWorldTime() % 40 * 10 != 0)
			return;

		setErrorState(EnumErrorCode.OK);

		if (!hasPaperMin(0.0f) || !hasInputBufMin(0.0f)) {
			setErrorState(EnumErrorCode.NORESOURCE);
			return;
		}
		if (!hasPostageMin(2)) {
			setErrorState(EnumErrorCode.NOSTAMPS);
			return;
		}

	}

	/* STATE INFORMATION */
	
	public boolean isLinked() {
		return getMoniker() != null && !getMoniker().isEmpty();
	}

	private float percentOccupied(int startSlot, int countSlots) {
		int max = 0;
		int avail = 0;

		IInventory tradeInventory = this.getOrCreateTradeInventory();
		for (int i = startSlot; i < startSlot + countSlots; i++) {
			max += 64;
			if (tradeInventory.getStackInSlot(i) == null) {
				continue;
			}
			avail += tradeInventory.getStackInSlot(i).stackSize;
		}

		return ((float) avail / (float) max);
	}

	public boolean hasPaperMin(float percentage) {
		return percentOccupied(TradeStation.SLOT_LETTERS_1, TradeStation.SLOT_LETTERS_COUNT) > percentage;
	}

	public boolean hasInputBufMin(float percentage) {
		return percentOccupied(TradeStation.SLOT_INPUTBUF_1, TradeStation.SLOT_BUFFER_COUNT) > percentage;
	}

	public boolean hasOutputBufMin(float percentage) {
		return percentOccupied(TradeStation.SLOT_OUTPUTBUF_1, TradeStation.SLOT_BUFFER_COUNT) > percentage;
	}

	public boolean hasPostageMin(int postage) {

		int posted = 0;

		IInventory tradeInventory = this.getOrCreateTradeInventory();
		for (int i = TradeStation.SLOT_STAMPS_1; i < TradeStation.SLOT_STAMPS_1 + TradeStation.SLOT_STAMPS_COUNT; i++) {
			ItemStack stamp = tradeInventory.getStackInSlot(i);
			if (stamp == null) {
				continue;
			}
			if (!(stamp.getItem() instanceof IStamps)) {
				continue;
			}

			posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.stackSize;
		}

		return posted >= postage;
	}

	/* MONIKER */
	public String getMoniker() {
		return this.moniker;
	}

	public void setMoniker(String moniker) {

		if (Proxies.common.isSimulating(worldObj)) {
			if (!PostOffice.isValidTradeMoniker(worldObj, moniker)) {
				setErrorState(EnumErrorCode.NOTALPHANUMERIC);
				return;
			}

			if (!PostOffice.isAvailableTradeMoniker(worldObj, moniker)) {
				setErrorState(EnumErrorCode.NOTUNIQUE);
				return;
			}

			this.moniker = moniker;
			PostOffice.getOrCreateTradeStation(worldObj, getOwnerName(), this.moniker);
			setErrorState(EnumErrorCode.OK);
			sendNetworkUpdate();
		} else {
			this.moniker = moniker;
		}
	}

	/* TRADING */
	public IInventory getOrCreateTradeInventory() {

		// Handle client side
		if (!Proxies.common.isSimulating(worldObj))
			return new GenericInventoryAdapter(TradeStation.SLOT_SIZE, "INV");

		if (this.moniker == null || this.moniker.isEmpty())
			return new GenericInventoryAdapter(TradeStation.SLOT_SIZE, "INV");

		return PostOffice.getOrCreateTradeStation(worldObj, getOwnerName(), this.moniker);
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		if (!this.isLinked())
			return 0;

		IInventory inventory = getOrCreateTradeInventory();
		ItemStack tradegood = inventory.getStackInSlot(TradeStation.SLOT_TRADEGOOD);

		// Special handling for paper
		if (stack.itemID == Item.paper.itemID) {

			// Handle paper as resource if its not the trade good or pumped in from above or below
			if ((tradegood != null && tradegood.itemID != Item.paper.itemID) || from == ForgeDirection.DOWN || from == ForgeDirection.UP)
				return StackUtils.addToInventory(stack, inventory, doAdd, TradeStation.SLOT_LETTERS_1, TradeStation.SLOT_LETTERS_COUNT);

		}

		// Special handling for stamps
		if (stack.getItem() instanceof IStamps) {

			// Handle stamps as resource if its not the trade good or pumped in from above or below
			if ((tradegood != null && !(tradegood.getItem() instanceof IStamps)) || from == ForgeDirection.DOWN || from == ForgeDirection.UP)
				return StackUtils.addToInventory(stack, inventory, doAdd, TradeStation.SLOT_STAMPS_1, TradeStation.SLOT_STAMPS_COUNT);

		}

		// Everything else
		if (tradegood == null)
			return 0;

		if (!tradegood.isItemEqual(stack))
			return 0;

		return StackUtils.addToInventory(stack, inventory, doAdd, TradeStation.SLOT_INPUTBUF_1, TradeStation.SLOT_BUFFER_COUNT);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		if (!this.isLinked())
			return new ItemStack[0];

		ItemStack product = null;
		IInventory inventory = getOrCreateTradeInventory();
		for (int i = TradeStation.SLOT_OUTPUTBUF_1; i < TradeStation.SLOT_OUTPUTBUF_1 + TradeStation.SLOT_BUFFER_COUNT; i++) {
			ItemStack stackSlot = inventory.getStackInSlot(i);
			if (stackSlot == null) {
				continue;
			}
			if (stackSlot.stackSize <= 0) {
				continue;
			}

			product = inventory.decrStackSize(i, 1);
			break;
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return new ItemStack[0];
	}

	/* ISIDEDINVENTORY */
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		switch (side) {
		case DOWN:
			return TradeStation.SLOT_LETTERS_1;
		case UP:
			return TradeStation.SLOT_STAMPS_1;
		case NORTH:
		case SOUTH:
			return TradeStation.SLOT_INPUTBUF_1;
		case EAST:
		case WEST:
			return TradeStation.SLOT_OUTPUTBUF_1;
		default:
			return 0;
		}

	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		switch (side) {
		case DOWN:
			return TradeStation.SLOT_LETTERS_COUNT;
		case UP:
			return TradeStation.SLOT_STAMPS_COUNT;
		default:
			return TradeStation.SLOT_BUFFER_COUNT;
		}
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return getOrCreateTradeInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getOrCreateTradeInventory().getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getOrCreateTradeInventory().decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getOrCreateTradeInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getOrCreateTradeInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public void onInventoryChanged() {
		getOrCreateTradeInventory().onInventoryChanged();
	}

	@Override
	public int getInventoryStackLimit() {
		return getOrCreateTradeInventory().getInventoryStackLimit();
	}
	@Override public void openChest() {}
	@Override public void closeChest() {}


	/* ITRIGGERPROVIDER */
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(PluginForestryMail.lowPaper25);
		res.add(PluginForestryMail.lowPaper10);
		res.add(PluginForestryMail.lowInput25);
		res.add(PluginForestryMail.lowInput10);
		res.add(PluginForestryMail.lowPostage40);
		res.add(PluginForestryMail.lowPostage20);
		res.add(PluginForestryMail.highBuffer90);
		res.add(PluginForestryMail.highBuffer75);
		return res;
	}

}
