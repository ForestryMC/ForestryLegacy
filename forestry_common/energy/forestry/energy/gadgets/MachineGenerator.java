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
package forestry.energy.gadgets;

import buildcraft.api.inventory.ISpecialInventory;
import ic2.api.Direction;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.GeneratorFuel;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.core.utils.Utils;
import forestry.plugins.PluginIC2;

public class MachineGenerator extends TileBase implements ISpecialInventory, ILiquidTankContainer, IEnergySource {

	// / CONSTANTS
	public static final short SLOT_CAN = 0;

	/**
	 * Factory class to produce {@link MachineGenerator}s.
	 */
	/*
	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineGenerator((TileMachine) tile);
		}
	}
	*/

	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	public int energyStored;
	public int energyMax;
	public boolean isAddedToEnergyNet;
	private int tickCount = 0;

	ItemStack[] inventoryStacks = new ItemStack[1];

	public MachineGenerator() {
		setHints(Config.hints.get("generator"));
		this.energyMax = 30000;
	}

	@Override
	public String getInvName() {
		return StringUtil.localize("tile.machine.4");
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.GeneratorGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("EnergyStored", energyStored);
		nbttagcompound.setInteger("EnergyMax", energyMax);
		nbttagcompound.setBoolean("IsAddedToEnergyNet", isAddedToEnergyNet);

		NBTTagCompound NBTresourceSlot = new NBTTagCompound();
		resourceTank.writeToNBT(NBTresourceSlot);
		nbttagcompound.setTag("ResourceTank", NBTresourceSlot);

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

		energyStored = nbttagcompound.getInteger("EnergyStored");
		energyMax = nbttagcompound.getInteger("EnergyMax");
		isAddedToEnergyNet = nbttagcompound.getBoolean("IsAddedToEnergyNet");

		resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
		if (nbttagcompound.hasKey("ResourceTank")) {
			resourceTank.readFromNBT(nbttagcompound.getCompoundTag("ResourceTank"));
		}

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

	@Override
	public void updateServerSide() {

		// Check inventory slots for fuel
		// Check if we have suitable items waiting in the item slot
		if (inventoryStacks[SLOT_CAN] != null) {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(inventoryStacks[SLOT_CAN]);
			if (container != null)
				if (GeneratorFuel.fuels.containsKey(container.stillLiquid.itemID)) {
					inventoryStacks[SLOT_CAN] = StackUtils.replenishByContainer(this, inventoryStacks[SLOT_CAN], container, resourceTank);
					if (inventoryStacks[SLOT_CAN].stackSize <= 0) {
						inventoryStacks[SLOT_CAN] = null;
					}
				}
		}

		// No work to be done if IC2 is unavailable.
		if (!PluginIC2.instance.isAvailable()) {
			setErrorState(EnumErrorCode.NOENERGYNET);
			return;
		}

		if (!isAddedToEnergyNet) {
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			this.isAddedToEnergyNet = true;
		}

		if (resourceTank.quantity > 0 && energyStored <= energyMax - 10) {

			if (GeneratorFuel.fuels.containsKey(resourceTank.liquidId)) {
				GeneratorFuel fuel = GeneratorFuel.fuels.get(resourceTank.liquidId);
				this.tickCount++;
				if (tickCount >= fuel.rate) {
					tickCount = 0;
					energyStored += EnergyNet.getForWorld(worldObj).emitEnergyFrom((IEnergySource) this, fuel.eu);
					resourceTank.drain(fuel.fuelConsumed.amount, true);
				}
			}

		} else if (energyStored > 0) {
			int emit;

			if (energyStored >= 32) {
				emit = 32;
				energyStored -= 32;

			} else {
				emit = energyStored;
				energyStored = 0;
			}

			energyStored += EnergyNet.getForWorld(worldObj).emitEnergyFrom((IEnergySource) this, emit);
		}

		if (resourceTank.quantity <= 0) {
			setErrorState(EnumErrorCode.NOFUEL);
		} else {
			setErrorState(EnumErrorCode.OK);
		}
	}

	public boolean isWorking() {
		return resourceTank.quantity > 0;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public int getStoredScaled(int i) {
		return (energyStored * i) / energyMax;
	}

	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			resourceTank.liquidId = j;
			break;
		case 1:
			resourceTank.quantity = j;
			break;
		case 2:
			energyStored = j;
			break;
		case 3:
			energyMax = j;
			break;
		case 4:
			resourceTank.liquidMeta = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, resourceTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 1, resourceTank.quantity);
		iCrafting.sendProgressBarUpdate(container, 2, energyStored);
		iCrafting.sendProgressBarUpdate(container, 3, energyMax);
		iCrafting.sendProgressBarUpdate(container, 4, resourceTank.liquidMeta);
	}

	// IINVENTORY IMPLEMENTATION
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

		ItemStack var3;
		if (inventoryStacks[i].stackSize <= j) {
			var3 = inventoryStacks[i];
			inventoryStacks[i] = null;
			return var3;
		} else {
			var3 = inventoryStacks[i].splitStack(j);
			if (inventoryStacks[i].stackSize == 0) {
				inventoryStacks[i] = null;
			}

			return var3;
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

	// / ISPECIALINVENTORY
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		LiquidContainerData container = LiquidHelper.getLiquidContainer(stack);
		if (container == null)
			return 0;

		if (!GeneratorFuel.fuels.containsKey(container.stillLiquid.itemID))
			return 0;

		if (inventoryStacks[0] == null) {
			if (doAdd) {
				inventoryStacks[0] = stack.copy();
			}

			return stack.stackSize;
		}

		if (!inventoryStacks[0].isItemEqual(stack))
			return 0;

		int space = inventoryStacks[0].getMaxStackSize() - inventoryStacks[0].stackSize;
		if (space <= 0)
			return 0;

		if (doAdd) {
			inventoryStacks[0].stackSize += stack.stackSize;
		}

		return Math.min(space, stack.stackSize);

	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		return null;
	}

	/* ILIQUIDCONTAINER IMPLEMENTATION */
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		if (tankIndex != 0)
			return 0;

		// We only accept water
		if (!GeneratorFuel.fuels.containsKey(resource.itemID))
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0) {
			// TODO: Slow down updates
			sendNetworkUpdate();
		}

		return used;
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

	// / IC2 IMPLEMENTATION
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		return true;
	}

	@Override
	public boolean isAddedToEnergyNet() {
		return isAddedToEnergyNet;
	}

	@Override
	public int getMaxEnergyOutput() {
		return 20;
	}

}
