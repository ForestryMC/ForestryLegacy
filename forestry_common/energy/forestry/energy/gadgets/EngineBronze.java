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
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.TankSlot;
import forestry.core.utils.TileInventoryAdapter;

public class EngineBronze extends Engine implements ISpecialInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_CAN = 0;

	/* NETWORK */
	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = super.getPacketPayload();

		if (shutdown) {
			payload.append(new int[] { 1 });
		} else {
			payload.append(new int[] { 0 });
		}

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		super.fromPacketPayload(payload);

		if (payload.intPayload[6] > 0) {
			shutdown = true;
		} else {
			shutdown = false;
		}
	}

	public TankSlot fuelTank = new TankSlot(Defaults.ENGINE_TANK_CAPACITY);
	public TankSlot heatingTank = new TankSlot(Defaults.ENGINE_TANK_CAPACITY);

	private TileInventoryAdapter inventory;

	public int currentLiquidId;
	public int currentLiquidMeta;
	public int burnTime;
	public int totalTime;

	// true if the engine is too cold and needs to warm itself up.
	private boolean shutdown;

	public EngineBronze() {
		setHints(Config.hints.get("engine.bronze"));

		maxEnergy = 100000;
		maxEnergyExtracted = 500;
		maxHeat = Defaults.ENGINE_BRONZE_HEAT_MAX;

		inventory = new TileInventoryAdapter(this, 1, "Items");
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.EngineBronzeGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void updateServerSide() {

		super.updateServerSide();

		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(0) != null) {

			LiquidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(0));
			if (container != null) {

				TankSlot tank = null;

				if (container.stillLiquid.isLiquidEqual(new LiquidStack(Block.lavaStill.blockID, 1)))
					tank = heatingTank;
				else if (FuelManager.bronzeEngineFuel.containsKey(container.stillLiquid.asItemStack()))
					tank = fuelTank;

				if (tank != null) {
					inventory.setInventorySlotContents(0, replenishByContainer(inventory.getStackInSlot(0), container, tank));
					if (inventory.getStackInSlot(0).stackSize <= 0) {
						inventory.setInventorySlotContents(0, null);
					}
				}
			}
		}

		if (worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		if (getHeatLevel() <= 0.2 && heatingTank.quantity <= 0) {
			setErrorState(EnumErrorCode.NOHEAT);
		} else if (burnTime <= 0 && fuelTank.quantity <= 0) {
			setErrorState(EnumErrorCode.NOFUEL);
		} else {
			setErrorState(EnumErrorCode.OK);
		}
	}

	/**
	 * Burns fuel increasing stored energy
	 */
	@Override
	public void burn() {

		currentOutput = 0;

		if (isActivated() && (fuelTank.quantity >= Defaults.BUCKET_VOLUME || burnTime > 0)) {

			double heatStage = getHeatLevel();

			// If we have reached a safe temperature, we reenable energy
			// transfer
			if (heatStage > 0.25 && shutdown) {
				shutdown(false);
			} else if (shutdown)
				if (heatingTank.quantity > 0 && heatingTank.liquidId == Block.lavaStill.blockID) {
					addHeat(Defaults.ENGINE_HEAT_VALUE_LAVA);
					heatingTank.quantity -= 1;
				}

			// We need a minimum temperature to generate energy
			if (heatStage > 0.2) {

				if (burnTime > 0) {
					burnTime--;
					if (currentLiquidId > 0) {
						currentOutput = determineFuelValue(new ItemStack(currentLiquidId, 1, currentLiquidMeta));
						addEnergy(currentOutput);
					}
				} else {
					burnTime = totalTime = this.determineBurnTime(fuelTank.getLiquid().asItemStack());
					currentLiquidId = fuelTank.liquidId;
					currentLiquidMeta = fuelTank.liquidMeta;
					fuelTank.drain(Defaults.BUCKET_VOLUME, true);
				}

			}
			// If we are below necessary fermentation temperature we shutdown
			// energy transfer
			else {
				shutdown(true);
			}
		}
	}

	private void shutdown(boolean val) {
		shutdown = val;
	}

	@Override
	public int dissipateHeat() {
		if (heat <= 0)
			return 0;

		int loss = 1; // Basic loss even when running

		if (!isBurning()) {
			loss++;
		}

		double heatStage = getHeatLevel();
		if (heatStage > 0.55) {
			loss++;
		}

		// Lose extra heat when using water as fuel.
		EngineBronzeFuel fuel = FuelManager.bronzeEngineFuel.get(new ItemStack(currentLiquidId, 1, currentLiquidMeta));
		if (fuel != null)
			loss = loss * fuel.dissipationMultiplier;

		heat -= loss;
		return loss;
	}

	@Override
	public int generateHeat() {

		int generate = 0;

		if (isActivated()) {
			double heatStage = getHeatLevel();
			if (heatStage >= 0.75) {
				generate += Defaults.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 3;
			} else if (heatStage > 0.24) {
				generate += Defaults.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 2;
			} else if (heatStage > 0.2) {
				generate += Defaults.ENGINE_BRONZE_HEAT_GENERATION_ENERGY;
			}
		}

		heat += generate;
		return generate;

	}

	/**
	 * Returns the fuel value (power per cycle) an item of the passed ItemStack provides
	 * 
	 * @param item
	 * @return
	 */
	private int determineFuelValue(ItemStack item) {
		if (FuelManager.bronzeEngineFuel.containsKey(item))
			return FuelManager.bronzeEngineFuel.get(item).powerPerCycle;
		else
			return 0;
	}

	/**
	 * 
	 * @param fuelid
	 * @return Duration of burn cycle of one bucket
	 */
	private int determineBurnTime(ItemStack item) {
		if (FuelManager.bronzeEngineFuel.containsKey(item))
			return FuelManager.bronzeEngineFuel.get(item).burnDuration;
		else
			return 0;
	}

	// / STATE INFORMATION
	@Override
	public boolean isBurning() {
		return mayBurn() && burnTime > 0;
	}

	@Override
	public int getBurnTimeRemainingScaled(int i) {
		if (totalTime == 0)
			return 0;

		return (burnTime * i) / totalTime;
	}

	public int getOperatingTemperatureScaled(int i) {
		return (int) Math.round((heat * i) / (maxHeat * 0.2));
	}

	public int getFuelScaled(int i) {
		return (fuelTank.quantity * i) / Defaults.ENGINE_TANK_CAPACITY;
	}

	public int getHeatingFuelScaled(int i) {
		return (heatingTank.quantity * i) / Defaults.ENGINE_TANK_CAPACITY;
	}

	/**
	 * Reads saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("CurrentLiquidId"))
			currentLiquidId = nbttagcompound.getInteger("CurrentLiquidId");
		if (nbttagcompound.hasKey("CurrentLiquidMeta"))
			currentLiquidMeta = nbttagcompound.getInteger("CurrentLiquidMeta");
		burnTime = nbttagcompound.getInteger("EngineBurnTime");
		totalTime = nbttagcompound.getInteger("EngineTotalTime");

		fuelTank = new TankSlot(Defaults.ENGINE_TANK_CAPACITY);
		heatingTank = new TankSlot(Defaults.ENGINE_TANK_CAPACITY);
		if (nbttagcompound.hasKey("FuelSlot")) {
			fuelTank.readFromNBT(nbttagcompound.getCompoundTag("FuelSlot"));
			heatingTank.readFromNBT(nbttagcompound.getCompoundTag("HeatingSlot"));
		}

		inventory.readFromNBT(nbttagcompound);

	}

	/**
	 * Writes data to save
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("CurrentLiquidId", currentLiquidId);
		nbttagcompound.setInteger("CurrentLiquidMeta", currentLiquidMeta);
		nbttagcompound.setInteger("EngineBurnTime", burnTime);
		nbttagcompound.setInteger("EngineTotalTime", totalTime);

		NBTTagCompound nbtFuelSlot = new NBTTagCompound();
		NBTTagCompound nbtHeatingSlot = new NBTTagCompound();

		fuelTank.writeToNBT(nbtFuelSlot);
		heatingTank.writeToNBT(nbtHeatingSlot);

		nbttagcompound.setTag("FuelSlot", nbtFuelSlot);
		nbttagcompound.setTag("HeatingSlot", nbtHeatingSlot);

		inventory.writeToNBT(nbttagcompound);
	}

	/* GUI */
	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			burnTime = j;
			break;
		case 1:
			totalTime = j;
			break;
		case 2:
			fuelTank.liquidId = j;
			break;
		case 3:
			fuelTank.quantity = j;
			break;
		case 4:
			heatingTank.liquidId = j;
			break;
		case 5:
			heatingTank.quantity = j;
			break;
		case 6:
			currentOutput = j;
			break;
		case 7:
			storedEnergy = j;
			break;
		case 8:
			heat = j;
			break;
		case 9:
			currentLiquidId = j;
			break;
		case 10:
			currentLiquidMeta = j;
			break;
                case 11:
                        fuelTank.liquidMeta = j;
                        break;
		}
	}

	@Override
	public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(containerEngine, 0, burnTime);
		iCrafting.sendProgressBarUpdate(containerEngine, 1, totalTime);
		iCrafting.sendProgressBarUpdate(containerEngine, 2, fuelTank.liquidId);
		iCrafting.sendProgressBarUpdate(containerEngine, 3, fuelTank.quantity);
		iCrafting.sendProgressBarUpdate(containerEngine, 4, heatingTank.liquidId);
		iCrafting.sendProgressBarUpdate(containerEngine, 5, heatingTank.quantity);
		iCrafting.sendProgressBarUpdate(containerEngine, 6, currentOutput);
		iCrafting.sendProgressBarUpdate(containerEngine, 7, storedEnergy);
		iCrafting.sendProgressBarUpdate(containerEngine, 8, heat);
		iCrafting.sendProgressBarUpdate(containerEngine, 9, currentLiquidId);
		iCrafting.sendProgressBarUpdate(containerEngine, 10, currentLiquidMeta);
                iCrafting.sendProgressBarUpdate(containerEngine, 11, fuelTank.liquidMeta);
	}

	// / IMPLEMENTATION OF IINVENTORY
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		LiquidContainerData container = LiquidHelper.getLiquidContainer(stack);
		if (container == null)
			return 0;

		return inventory.addStack(stack, false, doAdd);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		return null;
	}

	// / ITANKCONTAINER
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		// We only accept biomass and water as fuel
		int used = 0;
		
		if (FuelManager.bronzeEngineFuel.containsKey(resource.asItemStack()))
			used = fuelTank.fill(resource, doFill);
		
		if (resource.itemID == Block.lavaStill.blockID) {
			used = heatingTank.fill(resource, doFill);
		}

		return used;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		if (tankIndex == 0)
			return fuelTank.fill(resource, doFill);
		else if (tankIndex == 1)
			return heatingTank.fill(resource, doFill);
		else
			return 0;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new TankSlot[] { fuelTank, heatingTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return fuelTank;
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
