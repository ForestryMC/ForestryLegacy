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
package forestry.factory.gadgets;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.api.gates.ITrigger;
import forestry.api.core.EnumHumidity;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.genetics.ClimateHelper;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.core.utils.Utils;

public class MachineRaintank extends Machine {

	// / CONSTANTS
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_PRODUCT = 1;

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineRaintank((TileMachine) tile);
		}
	}

	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.RAINTANK_TANK_CAPACITY);
	private ItemStack[] inventoryStacks = new ItemStack[3];

	private boolean isValidBiome = true;
	private int fillingTime;
	private int fillingTotalTime;
	private LiquidContainerData productPending;
	private ItemStack usedEmpty;

	public MachineRaintank(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("raintank"));

		// Raintanks in desert and snow biomes are useless
		if (tile.worldObj != null) {
			BiomeGenBase biome = tile.worldObj.getBiomeGenForCoords(tile.xCoord, tile.zCoord);
			if (ClimateHelper.getHumidity(biome.rainfall) == EnumHumidity.ARID) {
				setErrorState(EnumErrorCode.INVALIDBIOME);
				isValidBiome = false;
			}
		}
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.3");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.RaintankGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("FillingTime", fillingTime);
		nbttagcompound.setInteger("FillingTotalTime", fillingTotalTime);
		nbttagcompound.setBoolean("IsValidBiome", isValidBiome);

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

		// Preserve used empty container
		if (usedEmpty != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			usedEmpty.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("UsedEmpty", nbttagcompoundP);
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		fillingTime = nbttagcompound.getInteger("FillingTime");
		fillingTotalTime = nbttagcompound.getInteger("FillingTotalTime");
		isValidBiome = nbttagcompound.getBoolean("IsValidBiome");

		resourceTank = new TankSlot(Defaults.RAINTANK_TANK_CAPACITY);
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

		// Load preserved empty container
		if (nbttagcompound.hasKey("UsedEmpty")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("UsedEmpty");
			usedEmpty = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}
	}

	@Override
	public void updateServerSide() {
		if (!Proxies.common.isSimulating(tile.worldObj))
			return;

		if (!isValidBiome) {
			setErrorState(EnumErrorCode.INVALIDBIOME);
			return;
		}

		if (!tile.worldObj.canBlockSeeTheSky(tile.Coords().x, tile.Coords().y, tile.Coords().z)) {
			setErrorState(EnumErrorCode.NOSKY);
			return;
		}
		if (!tile.worldObj.isRaining()) {
			setErrorState(EnumErrorCode.NOTRAINING);
			return;
		}

		resourceTank.fill(new LiquidStack(Block.waterStill.blockID, Defaults.RAINTANK_AMOUNT_PER_UPDATE), true);
		setErrorState(EnumErrorCode.OK);
	}

	@Override
	public boolean doWork() {
		if (!Proxies.common.isSimulating(tile.worldObj))
			return false;

		// Try to add product from previous cycle if still available
		if (productPending != null)
			if (tryProductAdd(productPending.filled.copy(), true)) {
				productPending = null;
				return true;
			} else
				return false;

		if (fillingTime > 0) {
			fillingTime--;
			if (fillingTime <= 0)
				if (usedEmpty != null) {
					LiquidContainerData container = LiquidHelper.getEmptyContainer(usedEmpty, new LiquidStack(Block.waterStill, 1));
					if (container != null && !tryProductAdd(container.filled.copy(), true)) {
						productPending = container;
					}
				}
			return true;
		} else if (tryStart(true))
			return true;
		else {
			usedEmpty = null;
			return false;
		}
	}

	private boolean tryStart(boolean doStart) {

		// Nothing to do if no empty cans are available
		if (inventoryStacks[SLOT_RESOURCE] == null)
			return false;
		if (inventoryStacks[SLOT_RESOURCE].stackSize <= 0)
			return false;

		LiquidContainerData container = LiquidHelper.getEmptyContainer(inventoryStacks[SLOT_RESOURCE], new LiquidStack(Block.waterStill, 1));
		if (container == null)
			return false;

		// Nothing to do if the output slot is full or occupied by a wrong item
		// stack
		if (!tryProductAdd(container.filled, false))
			return false;

		// Nothing to do if not enough liquid is available
		if (resourceTank.quantity < container.stillLiquid.amount)
			return false;

		// Let's fill a can
		if (doStart) {
			resourceTank.quantity -= container.stillLiquid.amount;
			if (resourceTank.quantity < 0) {
				resourceTank.quantity = 0;
			}
			decrStackSize(SLOT_RESOURCE, 1);
			fillingTime = this.fillingTotalTime = Defaults.RAINTANK_FILLING_TIME;
			usedEmpty = container.container;
		}

		return true;
	}

	private boolean tryProductAdd(ItemStack product, boolean doAdd) {

		if (product == null)
			return false;

		// Nothing to do if the output slot is full or occupied by a wrong item
		// stack
		if (inventoryStacks[SLOT_PRODUCT] != null) {
			if (inventoryStacks[SLOT_PRODUCT].itemID != product.itemID)
				return false;
			if (inventoryStacks[SLOT_PRODUCT].stackSize >= inventoryStacks[SLOT_PRODUCT].getMaxStackSize())
				return false;
		}

		// Skip actual adding if we are not required to
		if (!doAdd)
			return true;

		// Add product
		if (inventoryStacks[SLOT_PRODUCT] == null) {
			inventoryStacks[SLOT_PRODUCT] = product;
		} else {
			inventoryStacks[SLOT_PRODUCT].stackSize += product.stackSize;
		}

		return true;
	}

	@Override
	public boolean isWorking() {
		return fillingTime > 0 || productPending != null || tryStart(false);
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		if (inventoryStacks[SLOT_RESOURCE] == null)
			return false;

		return ((float) inventoryStacks[SLOT_RESOURCE].stackSize / (float) inventoryStacks[SLOT_RESOURCE].getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		// Always send redstone signal if we are in the process of bottling
		if (fillingTime > 0)
			return true;

		// Otherwise only if we have sufficient raw materials for a new bottling
		// process
		if (inventoryStacks[SLOT_RESOURCE] == null)
			return false;

		LiquidContainerData container = LiquidHelper.getEmptyContainer(inventoryStacks[SLOT_RESOURCE], new LiquidStack(Block.waterStill, 1));
		if (container == null)
			return false;
		if (resourceTank.quantity < container.stillLiquid.amount)
			return false;

		return true;
	}

	public int getFillProgressScaled(int i) {
		if (fillingTotalTime == 0)
			return 0;

		return (fillingTime * i) / fillingTotalTime;

	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	public int getResourceScaled(int i) {
		return (resourceTank.quantity * i) / Defaults.RAINTANK_TANK_CAPACITY;
	}

	// / ISPECIALINVENTORY

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		// We only accept empty water containers
		LiquidContainerData container = LiquidHelper.getEmptyContainer(stack, new LiquidStack(Block.waterStill, 1));
		if (container == null)
			return 0;

		if (inventoryStacks[SLOT_RESOURCE] == null) {
			if (doAdd) {
				inventoryStacks[SLOT_RESOURCE] = stack.copy();
			}
			return stack.stackSize;
		}

		if (inventoryStacks[SLOT_RESOURCE].stackSize >= inventoryStacks[SLOT_RESOURCE].getMaxStackSize())
			return 0;
		if (!inventoryStacks[SLOT_RESOURCE].isItemEqual(stack))
			return 0;

		// Determine available space
		int space = getInventoryStackLimit() - inventoryStacks[SLOT_RESOURCE].stackSize;
		if (space >= stack.stackSize) {
			if (doAdd) {
				inventoryStacks[SLOT_RESOURCE].stackSize += stack.stackSize;
			}
			return stack.stackSize;
		} else {
			if (doAdd) {
				inventoryStacks[SLOT_RESOURCE].stackSize = getInventoryStackLimit();
				stack.stackSize -= space;
			}
			return space;
		}
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		if (inventoryStacks[SLOT_PRODUCT] == null)
			return new ItemStack[0];

		if (inventoryStacks[SLOT_PRODUCT].stackSize <= 0)
			return new ItemStack[0];

		ItemStack product = new ItemStack(inventoryStacks[SLOT_PRODUCT].getItem(), 1);
		if (doRemove) {
			inventoryStacks[SLOT_PRODUCT].stackSize--;
			if (inventoryStacks[SLOT_PRODUCT].stackSize <= 0) {
				inventoryStacks[SLOT_PRODUCT] = null;
			}
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

		ItemStack removed;
		if (inventoryStacks[i].stackSize <= j) {
			removed = inventoryStacks[i];
			inventoryStacks[i] = null;
			return removed;
		} else {
			removed = inventoryStacks[i].splitStack(j);
			if (inventoryStacks[i].stackSize == 0) {
				inventoryStacks[i] = null;
			}

			return removed;
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

	// / ISIDEDINVENTORY

	@Override
	public int getStartInventorySide(int side) {
		if (side == 0 || side == 1)
			return 0;
		else
			return 1;
	}

	@Override
	public int getSizeInventorySide(int side) {
		return 1;
	}

	// / SMP GUI

	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			fillingTime = j;
			break;
		case 1:
			fillingTotalTime = j;
			break;
		case 2:
			resourceTank.liquidId = j;
			break;
		case 3:
			resourceTank.quantity = j;
			break;
                case 4:
                        resourceTank.liquidMeta = j;
                        break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, fillingTime);
		iCrafting.sendProgressBarUpdate(container, 1, fillingTotalTime);
		iCrafting.sendProgressBarUpdate(container, 2, resourceTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 3, resourceTank.quantity);
                iCrafting.sendProgressBarUpdate(container, 4, resourceTank.liquidMeta);
	}

	// / ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		// We only accept water
		if (resource.itemID != Block.waterStill.blockID)
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0) {
			// updateNetworkTime.markTime(worldObj);
			tile.sendNetworkUpdate();
		}

		return used;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int quantityMax, boolean doEmpty) {
		return drain(0, quantityMax, doEmpty);
	}

	public LiquidStack drain(int tankIndex, int quantityMax, boolean doEmpty) {
		return resourceTank.drain(quantityMax, doEmpty);
	}

	@Override
	public TankSlot[] getTanks(ForgeDirection direction) {
		return new TankSlot[] { resourceTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return resourceTank;
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowResource25);
		res.add(ForestryTrigger.lowResource10);
		res.add(ForestryTrigger.hasWork);
		return res;
	}

}
