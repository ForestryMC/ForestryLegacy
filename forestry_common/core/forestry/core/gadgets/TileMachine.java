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

import ic2.api.Direction;
import ic2.api.energy.tile.IEnergySource;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.PowerFramework;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IEnergyConsumer;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.interfaces.IParticularInventory;
import forestry.core.network.IndexInPayload;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.EnumTankLevel;
import forestry.plugins.PluginBuildCraft;
import forestry.plugins.PluginForestryApiculture;
import forestry.plugins.PluginForestryCore;
import forestry.plugins.PluginForestryEnergy;
import forestry.plugins.PluginForestryMail;

public class TileMachine extends TileInventory implements ILiquidTankContainer, IParticularInventory, IEnergyConsumer, IEnergySource, IClimatised, IHintSource {
	public Machine machine;

	private IPowerProvider powerProvider;

	public TileMachine() {
		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(Defaults.MACHINE_LATENCY, Defaults.MACHINE_MIN_ENERGY_RECEIVED, Defaults.MACHINE_MAX_ENERGY_RECEIVED,
				Defaults.MACHINE_MIN_ACTIVATION_ENERGY, Defaults.MACHINE_MAX_ENERGY);
	}

	/**
	 * Utility function that should be overwritten by TileEntitys that inherit for more specialised types of machines
	 * 
	 * @param meta
	 * @return
	 */
	protected MachinePackage getPackage(int meta) {
		return GadgetManager.getMachinePackage(meta);
	}

	// / MACHINERY
	public Gadget getMachine() {
		return this.machine;
	}

	// / IERRORSOURCE
	@Override
	public boolean throwsErrors() {
		if (machine != null)
			return machine.throwsErrors();
		else
			return false;
	}

	// / IHINTSOURCE
	@Override
	public boolean hasHints() {
		if (machine != null)
			return machine.hasHints();
		else
			return false;
	}

	@Override
	public String[] getHints() {
		if (machine != null)
			return machine.getHints();
		else
			return null;
	}

	// / ICLIMATISED
	@Override
	public boolean isClimatized() {
		if (machine != null)
			return machine.isClimatized();
		else
			return false;
	}

	@Override
	public EnumTemperature getTemperature() {
		if (machine != null)
			return machine.getTemperature();
		else
			return EnumTemperature.NORMAL;
	}

	@Override
	public EnumHumidity getHumidity() {
		if (machine != null)
			return machine.getHumidity();
		else
			return EnumHumidity.NORMAL;
	}

	@Override
	public float getExactTemperature() {
		if (machine != null)
			return machine.getExactTemperature();
		return 0;
	}
	@Override
	public float getExactHumidity() {
		if (machine != null)
			return machine.getExactHumidity();
		return 0;
	}

	// / IOWNABLE
	@Override
	public boolean isOwnable() {
		return true;
	}

	/**
	 * Utility function that should be overwritten by TileEntitys that inherit for more specialised types of machines
	 * 
	 * @param meta
	 * @return
	 */
	protected boolean hasPackage(int meta) {
		return GadgetManager.hasMachinePackage(meta);
	}

	protected void createMachine() {

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (!hasPackage(meta)) {
			Proxies.log.info("Encountered a " + getClass() + " with meta " + meta + ". However no such MachinePackage exists.");
			return;
			//meta = 0;
		}

		pack = getPackage(meta);
		MachinePackage packMachine = (MachinePackage) pack;

		// Reconfigure power provider for actual machine
		PluginBuildCraft.instance.configurePowerProvider(powerProvider, packMachine.energyConfig);

		MachineFactory factory = packMachine.factory;
		if (factory != null) {
			machine = factory.createMachine(this);
		} else
			throw new RuntimeException("Missing MachineFactory for " + getClass() + " and meta " + meta);

		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, meta);
	}

	int oldkind;
	NBTTagCompound olddata;
	@Override
	public void initialize() {

		if (!Proxies.common.isSimulating(worldObj))
			return;

		if(olddata != null) {
			legacyConversion(oldkind, olddata);
		} else if (machine == null) {
			createMachine();
		}
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		if (machine != null) {
			if (!Proxies.common.isSimulating(worldObj)) {
				machine.updateClientSide();
			} else {
				machine.updateServerSide();
			}
		}
	}

	@Override
	public void onRemoval() {
		if (machine != null) {
			machine.onRemoval();
		}
	}

	/**
	 * Read saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		PowerFramework.currentFramework.loadPowerProvider(this, nbttagcompound);

		int kind = nbttagcompound.getInteger("Kind");
		pack = getPackage(kind);
		// Fallback
		if (pack == null) {
			oldkind = kind;
			olddata = nbttagcompound;
			return;
		}

		machine = ((MachinePackage) pack).factory.createMachine(this);
		if (nbttagcompound.hasKey("Machine")) {
			machine.readFromNBT(nbttagcompound.getCompoundTag("Machine"));
		}
		// TODO: Legacy function for old savegames
		if (nbttagcompound.hasKey("Boiler")) {
			machine.readFromNBT(nbttagcompound.getCompoundTag("Boiler"));
		}

	}

	private HashMap<Integer, HashMap<Integer, MachineDefinition>> definitionMap;
	private void createDefinitionMap() {
		definitionMap = new HashMap<Integer, HashMap<Integer, MachineDefinition>>();

		HashMap<Integer, MachineDefinition> machineMap = new HashMap<Integer, MachineDefinition>();
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_APIARY, PluginForestryApiculture.definitionApiary);
		machineMap.put(4, PluginForestryEnergy.definitionGenerator);
		definitionMap.put(ForestryBlock.machine.blockID, machineMap);

		HashMap<Integer, MachineDefinition> millMap = new HashMap<Integer, MachineDefinition>();
		millMap.put(Defaults.ID_PACKAGE_MILL_MAILBOX, PluginForestryMail.definitionMailbox);
		millMap.put(Defaults.ID_PACKAGE_MILL_TRADER, PluginForestryMail.definitionTradestation);
		millMap.put(Defaults.ID_PACKAGE_MILL_PHILATELIST, PluginForestryMail.definitionPhilatelist);
		millMap.put(Defaults.ID_PACKAGE_MILL_APIARIST_CHEST, PluginForestryApiculture.definitionChest);
		millMap.put(Defaults.ID_PACKAGE_MILL_ANALYZER, PluginForestryCore.definitionAnalyzer);
		definitionMap.put(ForestryBlock.mill.blockID, millMap);

		HashMap<Integer, MachineDefinition> engineMap = new HashMap<Integer, MachineDefinition>();
		engineMap.put(0, PluginForestryEnergy.definitionEngineBronze);
		engineMap.put(1, PluginForestryEnergy.definitionEngineCopper);
		engineMap.put(2, PluginForestryEnergy.definitionEngineTin);
		definitionMap.put(ForestryBlock.engine.blockID, engineMap);


	}
	
	private void legacyConversion(int kind, NBTTagCompound nbttagcompound) {
		if(definitionMap == null)
			createDefinitionMap();
		
		int blockid = worldObj.getBlockId(xCoord, yCoord, zCoord);
		if(!definitionMap.containsKey(blockid))
			return;
		if(!definitionMap.get(blockid).containsKey(kind))
			return;
		
		MachineDefinition definition = definitionMap.get(blockid).get(kind);
		Proxies.log.info("Converting obsolete gadget %s-%s to new machine %s-%s", blockid, kind, definition.blockID, definition.meta);
		
		Proxies.log.info("Removing old tile entity...");
		worldObj.removeBlockTileEntity(xCoord, yCoord, zCoord);
		worldObj.setBlockAndMetadata(xCoord, yCoord, zCoord, 0, 0);
		Proxies.log.info("Setting to new block id...");
		worldObj.setBlockAndMetadata(xCoord, yCoord, zCoord, definition.blockID, definition.meta);
		TileEntity tile = worldObj.getBlockTileEntity(xCoord, yCoord, zCoord);
		if(tile == null) {
			throw new RuntimeException("Failed to set new block tile entity!");
		} else if(tile.getClass() != definition.teClass) {
			throw new RuntimeException("Converted tile entity is of an unexpected class:" + tile.getClass());
		}
		Proxies.log.info("Refreshing converted tile entity %s with nbt data...", tile.getClass());
		if(nbttagcompound.hasKey("Machine"))
			tile.readFromNBT(complementNBT(nbttagcompound, nbttagcompound.getCompoundTag("Machine"), definition));
		else
			tile.readFromNBT(nbttagcompound);
	}
	
	private NBTTagCompound complementNBT(NBTTagCompound parent, NBTTagCompound inner, MachineDefinition definition) {

		inner.setString("id", definition.teIdent);
		inner.setInteger("x", this.xCoord);
		inner.setInteger("y", this.yCoord);
		inner.setInteger("z", this.zCoord);

		inner.setInteger("Access", parent.getInteger("Access"));
		if (parent.hasKey("Owner")) {
			inner.setString("Owner", parent.getString("Owner"));
		}
		if (parent.hasKey("Orientation")) {
			inner.setInteger("Orientation", parent.getInteger("Orientation"));
		}

		return inner;
	}
	
	/**
	 * Write save data
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		PowerFramework.currentFramework.savePowerProvider(this, nbttagcompound);

		// Legacy for old fermenter with strange meta data
		int kind = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (hasPackage(kind)) {
			nbttagcompound.setInteger("Kind", kind);
		} else {
			nbttagcompound.setInteger("Kind", 0);
		}

		if (machine != null) {
			NBTTagCompound NBTmachine = new NBTTagCompound();
			machine.writeToNBT(NBTmachine);
			nbttagcompound.setTag("Machine", NBTmachine);
		} else {
			Proxies.log.warning("Saved a " + getClass() + " without machine.");
		}
	}
	
	@Override
	public void validate() {
		super.validate();
		
		if (machine != null)
			machine.validate();
	}

	public boolean isWorking() {
		if (machine != null)
			return machine.isWorking();
		else
			return false;
	}

	public int getChargeReceivedScaled(int i) {
		return (this.energyReceived * i) / powerProvider.getMaxEnergyReceived();
	}

	public static EnumTankLevel rateTankLevel(int scaled) {

		if (scaled < 5)
			return EnumTankLevel.EMPTY;
		else if (scaled < 30)
			return EnumTankLevel.LOW;
		else if (scaled < 60)
			return EnumTankLevel.MEDIUM;
		else if (scaled < 90)
			return EnumTankLevel.HIGH;
		else
			return EnumTankLevel.MAXIMUM;
	}

	// / REDSTONE SIGNALS
	public boolean isIndirectlyPoweringTo(IBlockAccess world, int i, int j, int k, int l) {

		if (machine != null)
			return machine.isIndirectlyPoweringTo(world, i, j, k, l);
		else
			return false;
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {

		if (machine != null)
			return machine.isPoweringTo(iblockaccess, i, j, k, l);
		else
			return false;
	}

	// IPOWERRECEPTOR IMPLEMENTATION

	@Override
	public void doWork() {

		if (!Proxies.common.isSimulating(worldObj))
			return;

		// Hard limit to 4 cycles / second.
		if (worldObj.getWorldTime() % 5 * 10 != 0)
			return;

		PluginBuildCraft.instance.invokeUseEnergyMethod(powerProvider, powerProvider.getActivationEnergy(), powerProvider.getActivationEnergy(), false);

		// Do not consume energy if the boiler didn't do any work.
		if (!machine.doWork())
			return;

		// Use up energy since we did some work.
		PluginBuildCraft.instance.invokeUseEnergyMethod(powerProvider, powerProvider.getActivationEnergy(), powerProvider.getActivationEnergy(), true);
	}

	@Override
	public void setPowerProvider(IPowerProvider provider) {
		powerProvider = provider;
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public int powerRequest() {
		if (isWorking())
			return Math.min(getPowerProvider().getMaxEnergyReceived(), getPowerProvider().getActivationEnergy() / 5 + 1);
		else
			return 0;
	}

	// IINVENTORY IMPLEMENTATION
	@Override
	public int getSizeInventory() {
		if (machine != null)
			return machine.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (machine != null)
			return machine.getStackInSlot(i);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (machine != null)
			return machine.decrStackSize(i, j);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (machine != null) {
			machine.setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getInvName() {
		if (machine != null)
			return machine.getName();
		else
			return "[Unknown]";
	}

	@Override
	public int getInventoryStackLimit() {
		if (machine != null)
			return machine.getInventoryStackLimit();
		else
			return 64;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (machine != null)
			return machine.getStackInSlotOnClosing(slot);
		else
			return null;
	}

	// ISIDEDINVENTORY
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if (getAccess() == EnumAccess.PRIVATE)
			return 0;

		if (machine != null)
			return machine.getStartInventorySide(side.ordinal());
		else
			return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		if (getAccess() == EnumAccess.PRIVATE)
			return 0;

		if (machine != null)
			return machine.getSizeInventorySide(side.ordinal());
		else
			return 0;
	}

	// IPARTICULARINVENTORY IMPLEMENTATION
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (getAccess() == EnumAccess.PRIVATE)
			return 0;

		if (machine != null)
			return machine.addItem(stack, doAdd, from);
		else
			return 0;
	}

	// @Override
	// public boolean canAccess(String username) {
	// return isOwned() && getOwnerName().equals(username);
	// }

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		if (getAccess() == EnumAccess.PRIVATE)
			return new ItemStack[0];

		if (machine != null)
			return machine.extractItem(doRemove, from, maxItemCount);
		else
			return new ItemStack[0];
	}

	// INETWORKEDTILE IMPLEMENTATION
	@Override
	public PacketPayload getPacketPayload() {

		PacketPayload payload = null;

		if (machine != null) {
			payload = machine.getPacketPayload();
		}

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		if (machine == null) {
			createMachine();
		}

		if(machine != null)
			machine.fromPacketPayload(payload, new IndexInPayload(0, 0, 0));
	}

	// NEIGHBOUR CHANGE
	public void onNeighborBlockChange() {
		if (machine == null)
			return;

		if(machine != null)
			machine.onNeighborBlockChange();
	}

	// / IC2 IMPLEMENTATION
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		if (machine != null)
			return machine.emitsEnergyTo(receiver, direction);
		else
			return false;
	}

	@Override
	public boolean isAddedToEnergyNet() {
		if (machine != null)
			return machine.isAddedToEnergyNet();
		else
			return false;
	}

	@Override
	public int getMaxEnergyOutput() {
		if (machine != null)
			return machine.getMaxEnergyOutput();
		else
			return 0;
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		if (machine != null)
			return machine.getCustomTriggers();
		else
			return null;
	}

	// / IENERGYCONSUMER
	@Override
	public boolean consumesEnergy() {
		return powerProvider.getMaxEnergyReceived() > 0;
	}

	@Override
	public float getMaxEnergyStored() {
		return powerProvider.getMaxEnergyStored();
	}

	@Override
	public float getEnergyStored() {
		return powerProvider.getEnergyStored();
	}

	@Override
	public float getMaxEnergyReceived() {
		return powerProvider.getMaxEnergyReceived();
	}

	// / ILIQUIDTANKCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		if (machine == null)
			return 0;

		return machine.fill(from, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		if (machine == null)
			return 0;

		return machine.fill(tankIndex, resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (machine == null)
			return null;

		return machine.drain(from, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		if (machine == null)
			return null;
		return machine.drain(tankIndex, maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		if (machine != null)
			return machine.getTanks(direction);
		else
			return new LiquidTank[0];
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		if (machine != null)
			return machine.getTank(direction, type);
		else
			return null;
	}

}
