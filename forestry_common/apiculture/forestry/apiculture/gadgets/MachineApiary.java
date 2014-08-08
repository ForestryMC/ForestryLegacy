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
package forestry.apiculture.gadgets;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.inventory.ISpecialInventory;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.TileBase;
import forestry.core.genetics.ClimateHelper;
import forestry.core.interfaces.IClimatised;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketInventoryStack;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginForestryApiculture;

public class MachineApiary extends TileBase implements IBeeHousing, IClimatised, ISpecialInventory, ISidedInventory {

	/**
	 * Factory class to produce {@link MachineApiary}s.
	 * 
	 * @author SirSengir
	 * 
	 */
	/*
	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineApiary((TileMachine) tile);
		}
	}
	*/

	// CONSTANTS
	public static final int SLOT_QUEEN = 0;
	public static final int SLOT_DRONE = 1;
	public static final int SLOT_INVENTORY_1 = 2;
	private static int SLOT_PRODUCT_1 = 2;
	private static int SLOT_PRODUCT_COUNT = 7;
	public static final int SLOT_FRAMES_1 = 9;
	public static final int SLOT_INVENTORY_COUNT = 7;
	public static final int SLOT_FRAMES_COUNT = 3;

	// Inventory
	public GenericInventoryAdapter inventory = new GenericInventoryAdapter(12, "Items");

	private IBeekeepingLogic logic;

	private int biomeId;

	private float temperature;
	private float humidity;

	private int displayHealthMax = 0;
	private int displayHealth = 0;

	public MachineApiary() {
		setHints(Config.hints.get("apiary"));

		updateBiome();
		logic = BeeManager.breedingManager.createBeekeepingLogic(this);
	}

	@Override
	public String getInvName() {
		return StringUtil.localize("tile.machine.7");
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.ApiaryGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	// / LOADING & SAVING
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setFloat("Temp", temperature);
		nbttagcompound.setFloat("Humidity", humidity);
		nbttagcompound.setInteger("BiomeId", biomeId);

		inventory.writeToNBT(nbttagcompound);
		if(logic != null)
			logic.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		temperature = nbttagcompound.getFloat("Temp");
		humidity = nbttagcompound.getFloat("Humidity");
		biomeId = nbttagcompound.getInteger("BiomeId");

		inventory.readFromNBT(nbttagcompound);
		logic.readFromNBT(nbttagcompound);
		
		updateBiome();

	}
	
	@Override
	public void validate() {
		updateBiome();
	}

	// / ICLIMATISED
	@Override
	public boolean isClimatized() {
		return true;
	}

	@Override
	public EnumTemperature getTemperature() {
		if(biomeId == BiomeGenBase.hell.biomeID)
			return EnumTemperature.HELLISH;
		return ClimateHelper.getTemperature(temperature);
	}

	@Override
	public EnumHumidity getHumidity() {
		return ClimateHelper.getHumidity(humidity);
	}

	@Override
	public float getExactTemperature() {
		return this.temperature;
	}

	@Override
	public float getExactHumidity() {
		return this.humidity;
	}

	// / UPDATING
	@Override
	public void updateClientSide() {

		// / Multiplayer FX
		if (BeeManager.beeInterface.isMated(inventory.getStackInSlot(SLOT_QUEEN))) {
			if (getErrorState() == EnumErrorCode.OK && worldObj.getWorldTime() % 2 % 2 == 0) {
				IBee displayQueen = BeeManager.beeInterface.getBee(inventory.getStackInSlot(SLOT_QUEEN));
				displayQueen.doFX(logic.getEffectData(), this);
			}
		}
		return;

	}

	@Override
	public void updateServerSide() {

		logic.update();

		IBee queen = logic.getQueen();
		if (queen == null)
			return;

		// Add swarm effects
		if (worldObj.getWorldTime() % 200 * 10 == 0) {
			onQueenChange(inventory.getStackInSlot(SLOT_QUEEN));
		}
		if (getErrorState() == EnumErrorCode.OK && worldObj.getWorldTime() % 2 % 2 == 0) {
			queen.doFX(logic.getEffectData(), this);
		}

		if (getErrorState() == EnumErrorCode.OK && worldObj.getWorldTime() % 50 == 0) {
			float f = xCoord + 0.5F;
			float f1 = yCoord + 0.0F + (worldObj.rand.nextFloat() * 6F) / 16F;
			float f2 = zCoord + 0.5F;
			float f3 = 0.52F;
			float f4 = worldObj.rand.nextFloat() * 0.6F - 0.3F;

			Proxies.common.addEntitySwarmFX(worldObj, (f - f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntitySwarmFX(worldObj, (f + f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 - f3), 0F, 0F, 0F);
			Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 + f3), 0F, 0F, 0F);
		}

	}

	//@Override
	public boolean isWorking() {
		return getErrorState() == EnumErrorCode.OK;
	}

	public boolean addProduct(ItemStack product, boolean all) {
		return inventory.tryAddStack(product, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT, all, true);
	}

	/* NETWORK SYNCH */
	@Override
	public void onQueenChange(ItemStack queenStack) {
		if (!Proxies.common.isSimulating(worldObj))
			return;

		Proxies.net.sendNetworkPacket(new PacketInventoryStack(PacketIds.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_QUEEN, queenStack),
				xCoord, yCoord, zCoord);
		Proxies.net.sendNetworkPacket(new PacketTileUpdate(this), xCoord, yCoord, zCoord);
	}

	/* STATE INFORMATION */
	private int getHealthDisplay() {
		if (inventory.getStackInSlot(SLOT_QUEEN) == null)
			return 0;

		if (inventory.getStackInSlot(SLOT_QUEEN).itemID == ForestryItem.beeQueenGE.itemID)
			return BeeManager.beeInterface.getBee(inventory.getStackInSlot(SLOT_QUEEN)).getHealth();
		else if (inventory.getStackInSlot(SLOT_QUEEN).itemID == ForestryItem.beePrincessGE.itemID)
			return displayHealth;
		else
			return 0;
	}

	private int getMaxHealthDisplay() {
		if (inventory.getStackInSlot(SLOT_QUEEN) == null)
			return 0;

		if (inventory.getStackInSlot(SLOT_QUEEN).itemID == ForestryItem.beeQueenGE.itemID)
			return BeeManager.beeInterface.getBee(inventory.getStackInSlot(SLOT_QUEEN)).getMaxHealth();
		else if (inventory.getStackInSlot(SLOT_QUEEN).itemID == ForestryItem.beePrincessGE.itemID)
			return displayHealthMax;
		else
			return 0;
	}

	/**
	 * Returns scaled queen health or breeding progress
	 * 
	 * @param i
	 * @return
	 */
	public int getHealthScaled(int i) {
		if (getMaxHealthDisplay() == 0)
			return 0;

		return (getHealthDisplay() * i) / getMaxHealthDisplay();
	}

	public int getTemperatureScaled(int i) {
		return Math.round(i * (temperature / 2));
	}

	public int getHumidityScaled(int i) {
		return Math.round(i * humidity);
	}
	
	public void updateBiome() {
		if (worldObj != null) {
			BiomeGenBase biome = worldObj.getBiomeGenForCoords(xCoord, zCoord);
			if (biome != null) {
				this.biomeId = biome.biomeID;
				this.temperature = biome.temperature;
				this.humidity = biome.rainfall;
				setErrorState(EnumErrorCode.OK);
			}
		}
	}

	/* SMP */
	//@Override
	public void getGUINetworkData(int i, int j) {
		if (logic == null)
			return;

		switch (i) {
		case 0:
			displayHealth = j;
			break;
		case 1:
			displayHealthMax = j;
			break;
		}
	}

	//@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		if (logic == null)
			return;

		iCrafting.sendProgressBarUpdate(container, 0, logic.getBreedingTime());
		iCrafting.sendProgressBarUpdate(container, 1, logic.getTotalBreedingTime());
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override public int getInventoryStackLimit() { return inventory.getInventoryStackLimit(); }
	@Override public void openChest() {}
	@Override public void closeChest() {}


	// / ISIDEDINVENTORY
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		// BOTTOM
		if (side == ForgeDirection.DOWN)
			return SLOT_DRONE;
		// TOP
		else if (side == ForgeDirection.UP)
			return SLOT_QUEEN;
		// SIDES
		else
			return SLOT_PRODUCT_1;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		if (side == ForgeDirection.DOWN || side == ForgeDirection.UP)
			return 1;
		else
			return SLOT_PRODUCT_COUNT;
	}

	// / ISPECIALINVENTORY
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product = null;

		for (int i = SLOT_PRODUCT_1; i < SLOT_PRODUCT_1 + SLOT_PRODUCT_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}

			// Princesses can only be extracted from top.
			if (inventory.getStackInSlot(i).itemID == ForestryItem.beePrincessGE.itemID) {
				if (PluginForestryApiculture.apiarySideSensitive && from != ForgeDirection.UP) {
					continue;
				}

				product = inventory.getStackInSlot(i).copy();
				if (doRemove) {
					inventory.getStackInSlot(i).stackSize = 0;
					inventory.setInventorySlotContents(i, null);
				}
				break;

				// Drones can only be extracted from the bottom.
			} else if (inventory.getStackInSlot(i).itemID == ForestryItem.beeDroneGE.itemID) {
				if (PluginForestryApiculture.apiarySideSensitive && from != ForgeDirection.DOWN) {
					continue;
				}

				product = StackUtils.createSplitStack(inventory.getStackInSlot(i), 1);
				product.stackSize = 1;
				if (doRemove) {
					inventory.getStackInSlot(i).stackSize--;
					if (inventory.getStackInSlot(i).stackSize <= 0) {
						inventory.setInventorySlotContents(i, null);
					}
				}
				break;

				// Everything else to be extracted from the sides
			} else {
				if (PluginForestryApiculture.apiarySideSensitive && (from == ForgeDirection.UP || from == ForgeDirection.DOWN)) {
					continue;
				}

				product = StackUtils.createSplitStack(inventory.getStackInSlot(i), 1);
				if (doRemove) {
					inventory.getStackInSlot(i).stackSize--;
					if (inventory.getStackInSlot(i).stackSize <= 0) {
						inventory.setInventorySlotContents(i, null);
					}
				}
				break;
			}
		}

		return new ItemStack[] { product };
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		// Princesses && Queens
		if (stack.itemID == ForestryItem.beePrincessGE.itemID || stack.itemID == ForestryItem.beeQueenGE.itemID)
			if (inventory.getStackInSlot(SLOT_QUEEN) == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(SLOT_QUEEN, stack.copy());
					inventory.getStackInSlot(SLOT_QUEEN).stackSize = 1;
				}

				return 1;
			}

		// Drones
		if (stack.itemID == ForestryItem.beeDroneGE.itemID) {
			
			ItemStack droneStack = inventory.getStackInSlot(SLOT_DRONE);
			if (droneStack == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(SLOT_DRONE, stack.copy());
				}
				return stack.stackSize;
			} else {
				if(!droneStack.isItemEqual(stack))
					return 0;
				if(!ItemStack.areItemStackTagsEqual(droneStack, stack))
					return 0;
				int space = droneStack.getMaxStackSize() - droneStack.stackSize;
				if(space <= 0)
					return 0;
				
				int added = space > stack.stackSize ? stack.stackSize : space;
				if(doAdd)
					droneStack.stackSize += added;
				return added;
			}
		}

		return 0;
	}

	// / ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.missingQueen);
		res.add(ForestryTrigger.missingDrone);
		res.add(PluginForestryApiculture.triggerNoFrames);
		return res;
	}

	// / IBEEHOUSING
	@Override
	public int getXCoord() {
		return xCoord;
	}

	@Override
	public int getYCoord() {
		return yCoord;
	}

	@Override
	public int getZCoord() {
		return zCoord;
	}

	@Override
	public int getBiomeId() {
		return this.biomeId;
	}

	@Override
	public ItemStack getQueen() {
		return getStackInSlot(SLOT_QUEEN);
	}

	@Override
	public ItemStack getDrone() {
		return getStackInSlot(SLOT_DRONE);
	}

	@Override
	public void setQueen(ItemStack itemstack) {
		setInventorySlotContents(SLOT_QUEEN, itemstack);
	}

	@Override
	public void setDrone(ItemStack itemstack) {
		setInventorySlotContents(SLOT_DRONE, itemstack);
	}

	@Override
	public float getTerritoryModifier(IBeeGenome genome) {
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getTerritoryModifier(genome);
			}
		}
		return mod;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome) {
		float mod = 0.1f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getProductionModifier(genome);
			}
		}
		return mod;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate) {
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getMutationModifier(genome, mate);
			}
		}
		return mod;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate) {
		float mod = 1.0f;
		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).getItem() instanceof IHiveFrame) {
				mod *= ((IHiveFrame) inventory.getStackInSlot(i).getItem()).getLifespanModifier(genome, mate);
			}
		}
		return mod;
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public void setErrorState(int state) {
		setErrorState(EnumErrorCode.values()[state]);
	}

	@Override
	public int getErrorOrdinal() {
		return getErrorState().ordinal();
	}

	@Override
	public boolean canBreed() {
		return true;
	}

	@Override
	public void wearOutEquipment(int amount) {
		int wear = Math.round(amount * BeeManager.breedingManager.getBeekeepingMode(worldObj).getWearModifier());

		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (!(inventory.getStackInSlot(i).getItem() instanceof IHiveFrame)) {
				continue;
			}

			inventory.setInventorySlotContents(
					i,
					((IHiveFrame) inventory.getStackInSlot(i).getItem()).frameUsed(this, inventory.getStackInSlot(i),
							BeeManager.beeInterface.getBee(inventory.getStackInSlot(SLOT_QUEEN)), wear));
		}
	}

	@Override
	public boolean isSealed() {
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		return false;
	}

	@Override
	public boolean isHellish() {
		return false;
	}

	@Override
	public float getFloweringModifier(IBeeGenome genome) {
		return 1f;
	}

	@Override
	public void onQueenDeath(IBee queen) {
	}

	@Override
	public void onPostQueenDeath(IBee queen) {
	}

}
