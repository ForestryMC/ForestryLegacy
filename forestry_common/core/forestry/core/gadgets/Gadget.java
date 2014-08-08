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
package forestry.core.gadgets;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.api.gates.ITrigger;
import forestry.core.EnumErrorCode;
import forestry.core.network.IndexInPayload;
import forestry.core.network.PacketPayload;
import forestry.core.utils.TankSlot;

public abstract class Gadget implements IInventory, ITankContainer {

	public abstract PacketPayload getPacketPayload();

	public abstract void fromPacketPayload(PacketPayload payload, IndexInPayload index);

	protected TileInventory tile;

	public Gadget(TileInventory tile) {
		this.tile = tile;
	}

	public abstract void openGui(EntityPlayer player, IInventory tile);

	public World getWorld() {
		return tile.worldObj;
	}

	// / SAVING & LOADING
	public void writeToNBT(NBTTagCompound nbttagcompound) {
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
	}
	
	public void validate() {
	}

	// / UPDATING
	public void updateClientSide() {
	}

	public void updateServerSide() {
	}

	// / SMP GUI
	public abstract void getGUINetworkData(int i, int j);

	public abstract void sendGUINetworkData(Container container, ICrafting iCrafting);

	// ERROR HANDLING
	public void setErrorState(EnumErrorCode state) {
		tile.setErrorState(state);
	}

	public boolean throwsErrors() {
		return true;
	}

	public EnumErrorCode getErrorState() {
		return tile.getErrorState();
	}

	// / IHINTSOURCE
	private String[] hints;

	public void setHints(String[] hints) {
		this.hints = hints;
	}

	public boolean hasHints() {
		return hints != null && hints.length > 0;
	}

	public String[] getHints() {
		return hints;
	}

	// / IINVENTORY
	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public String getInvName() {
		return "";
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	// / ISIDEDINVENTORY
	public int getStartInventorySide(int side) {
		return 0;
	}

	public int getSizeInventorySide(int side) {
		return getSizeInventory();
	}

	// / IPARTICULARINVENTORY
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		return 0;
	}

	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		return new ItemStack[0];
	}

	// ITRIGGERPROVIDER
	public LinkedList<ITrigger> getCustomTriggers() {
		return null;
	}

	// / ITANKCONTAINER
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new TankSlot[0];
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return null;
	}

}
