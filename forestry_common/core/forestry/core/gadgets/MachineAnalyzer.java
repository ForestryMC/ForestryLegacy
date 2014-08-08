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

import java.util.Stack;

import buildcraft.api.inventory.ISpecialInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;

public class MachineAnalyzer extends TileBase implements ISpecialInventory, ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final int TIME_TO_ANALYZE = 500;
	public static final int HONEY_REQUIRED = 100;

	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_CAN = 1;
	public static final short SLOT_INPUT_1 = 2;
	public static final short SLOT_OUTPUT_1 = 8;

	/* MEMBER */
	private GenericInventoryAdapter inventory = new GenericInventoryAdapter(12, "Items");

	private int analyzeTime;

	private short analyzeSlot = 0;
	private short canSlot = 1;
	private short inputSlot1 = 2;
	private short outputSlot1 = 8;

	public LiquidStack resource = new LiquidStack(ForestryItem.liquidHoney, HONEY_REQUIRED);
	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	private Stack<ItemStack> pendingProducts = new Stack<ItemStack>();

	/* CONSTRUCTOR */
	public MachineAnalyzer() {
	}

	@Override
	public String getInvName() {
		return StringUtil.localize("tile.mill.4");
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.AnalyzerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("AnalyzeTime", analyzeTime);

		// / Resource tank
		resourceTank.writeToNBT(nbttagcompound);

		// / Pending Products
		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] pending = pendingProducts.toArray(new ItemStack[pendingProducts.size()]);
		for (int i = 0; i < pending.length; i++)
			if (pending[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				pending[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingProducts", nbttaglist);

		// / Inventory
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		analyzeTime = nbttagcompound.getInteger("AnalyzeTime");

		// / Resource tank
		resourceTank.readFromNBT(nbttagcompound);

		// / Pending Products
		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingProducts");
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			pendingProducts.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

		// / Inventory
		inventory.readFromNBT(nbttagcompound);
	}

	/* WORKING */
	@Override
	public void updateServerSide() {
		// If we add pending products, we skip to the next work cycle.
		if (tryAddPending())
			return;

		if (!pendingProducts.isEmpty()) {
			setErrorState(EnumErrorCode.NOSPACE);
			return;
		}

		// Check if we have suitable items waiting in the can slot
		if (getStackInSlot(canSlot) != null) {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(getStackInSlot(canSlot));
			if (container != null && resource.isLiquidEqual(container.stillLiquid)) {

				setInventorySlotContents(canSlot, StackUtils.replenishByContainer(this, getStackInSlot(canSlot), container, resourceTank));
				if (getStackInSlot(canSlot).stackSize <= 0) {
					setInventorySlotContents(canSlot, null);
				}
			}
		}

		if (analyzeTime > 0 && getStackInSlot(analyzeSlot) != null && AlleleManager.alleleRegistry.isIndividual(getStackInSlot(analyzeSlot))) {

			analyzeTime--;

			// Still not done
			if (analyzeTime > 0) {
				setErrorState(EnumErrorCode.OK);
				return;
			}

			// Analyzation is done.
			IIndividual individual = AlleleManager.alleleRegistry.getIndividual(getStackInSlot(analyzeSlot));
			// No bee, abort
			if (individual == null)
				return;

			individual.analyze();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			individual.writeToNBT(nbttagcompound);
			getStackInSlot(analyzeSlot).setTagCompound(nbttagcompound);

			pendingProducts.push(getStackInSlot(analyzeSlot));
			setInventorySlotContents(analyzeSlot, null);

		} else {
			analyzeTime = 0;

			// Don't start if analyze slot already occupied
			if (getStackInSlot(analyzeSlot) != null)
				return;

			// We need our liquid honey
			if (resourceTank.quantity < resource.amount) {
				setErrorState(EnumErrorCode.NORESOURCE);
				return;
			}

			// Look for bees in input slots.
			for (int i = inputSlot1; i < outputSlot1; i++) {
				if (getStackInSlot(i) == null || !AlleleManager.alleleRegistry.isIndividual(getStackInSlot(i))) {
					continue;
				}

				// Analyzed bees in the input buffer are added to the output
				// queue at once.
				IIndividual individual = AlleleManager.alleleRegistry.getIndividual(getStackInSlot(i));
				if (individual.isAnalyzed()) {
					pendingProducts.push(getStackInSlot(i));
					setInventorySlotContents(i, null);
					continue;
				}

				setInventorySlotContents(analyzeSlot, getStackInSlot(i));
				setInventorySlotContents(i, null);
				resourceTank.drain(resource.amount, true);
				analyzeTime = TIME_TO_ANALYZE;
				return;
			}

			// Nothing to analyze
			setErrorState(EnumErrorCode.NOTHINGANALYZE);
		}
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty())
			return false;

		ItemStack next = pendingProducts.peek();
		if (inventory.tryAddStack(next, outputSlot1, inventory.getSizeInventory() - outputSlot1, true)) {
			pendingProducts.pop();
			return true;
		}
		return false;
	}

	/* STATE INFORMATION */
	//@Override
	public boolean isWorking() {
		return analyzeTime > 0;
	}

	public int getProgressScaled(int i) {
		return (analyzeTime * i) / TIME_TO_ANALYZE;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public ItemStack getBeeOnDisplay() {
		if (getStackInSlot(analyzeSlot) != null)
			return getStackInSlot(analyzeSlot);
		else
			return null;
	}

	/* SMP */
	//@Override
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			analyzeTime = j;
			break;
		case 1:
			resourceTank.liquidId = j;
			break;
		case 2:
			resourceTank.quantity = j;
			break;
		}
	}

	//@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, analyzeTime);
		iCrafting.sendProgressBarUpdate(container, 1, resourceTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 2, resourceTank.quantity);

	}

	// / IINVENTORY
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	/* ISPECIALINVENTORY */
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		ItemStack product = null;

		for (int i = outputSlot1; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null) {
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

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		if (!AlleleManager.alleleRegistry.isIndividual(stack)) {

			LiquidContainerData container = LiquidHelper.getLiquidContainer(stack);
			if (container == null || !container.stillLiquid.isLiquidEqual(resource))
				return 0;

			if (getStackInSlot(canSlot) == null) {
				if (doAdd) {
					setInventorySlotContents(canSlot, stack.copy());
				}

				return stack.stackSize;
			}

			int space = getStackInSlot(canSlot).getMaxStackSize() - getStackInSlot(canSlot).stackSize;
			if (space <= 0)
				return 0;

			if (doAdd) {
				getStackInSlot(canSlot).stackSize += stack.stackSize;
				if (getStackInSlot(canSlot).stackSize > getStackInSlot(canSlot).getMaxStackSize()) {
					getStackInSlot(canSlot).stackSize = getStackInSlot(canSlot).getMaxStackSize();
				}
			}

			return space;
		}

		for (int i = inputSlot1; i < outputSlot1; i++) {
			if (getStackInSlot(i) == null) {
				if (doAdd) {
					setInventorySlotContents(i, stack.copy());
				}

				return stack.stackSize;
			}
		}

		return 0;
	}

	/* ISIDEDINVENTORY */
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		// BOTTOM
		if (side == ForgeDirection.DOWN)
			return outputSlot1;
		// TOP
		else if (side == ForgeDirection.UP)
			return canSlot;
		// SIDES
		else
			return inputSlot1;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		if (side == ForgeDirection.UP)
			return 1;
		else if (side == ForgeDirection.DOWN)
			return 4;
		else
			return 6;
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

	/* ILIQUIDCONTAINER */
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {

		// We only accept what is already in the tank or valid ingredients
		if (resourceTank.quantity > 0 && resourceTank.liquidId != resource.itemID)
			return 0;
		else if (resource.itemID != ForestryItem.liquidHoney.itemID)
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0) {
			sendNetworkUpdate();
		}

		return used;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}

	@Override
	public TankSlot[] getTanks(ForgeDirection direction) {
		return new TankSlot[] { resourceTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return resourceTank;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return null;
	}
}
