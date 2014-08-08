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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.api.gates.ITrigger;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterManager;
import forestry.core.EnumErrorCode;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.RecipeUtil;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.core.utils.Utils;

public class MachineFermenter extends Machine {

	// / CONSTANTS
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_FUEL = 1;
	public static final short SLOT_CAN_OUTPUT = 2;
	public static final short SLOT_CAN_INPUT = 3;
	public static final short SLOT_INPUT = 4;

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineFermenter((TileMachine) tile);
		}
	}

	// / RECIPE MANAGMENT
	public static class Recipe {
		public final ItemStack resource;
		public final int fermentationValue;
		public final float modifier;
		public final LiquidStack output;
		public final LiquidStack liquid;

		public Recipe(ItemStack resource, int fermentationValue, float modifier, LiquidStack output, LiquidStack liquid) {
			this.resource = resource;
			this.fermentationValue = fermentationValue;
			this.modifier = modifier;
			this.output = output;
			this.liquid = liquid;
		}

		public boolean matches(ItemStack res, LiquidStack liqu) {
			// No recipe without resource!
			if (res == null)
				return false;

			if(resource.itemID != res.itemID)
				return false;
			if(resource.getItemDamage() >= 0
					&& resource.getItemDamage() != res.getItemDamage())
				return false;

			// No liquid required
			if (liquid == null)
				return true;

			// Liquid required but none given
			if (liquid != null && liqu == null)
				return false;

			// Wrong liquid
			if (!liquid.isLiquidEqual(liqu))
				return false;

			// Enough liquid
			if (liquid.amount <= liqu.amount)
				return true;

			return false;
		}
	}

	public static class RecipeManager implements IFermenterManager {
		public static ArrayList<MachineFermenter.Recipe> recipes = new ArrayList<MachineFermenter.Recipe>();

		@Override
		public void addRecipe(ItemStack resource, int fermentationValue, float modifier, LiquidStack output, LiquidStack liquid) {
			recipes.add(new Recipe(resource, fermentationValue, modifier, output, liquid));
		}

		@Override
		public void addRecipe(ItemStack resource, int fermentationValue, float modifier, LiquidStack output) {
			recipes.add(new Recipe(resource, fermentationValue, modifier, output, null));
		}

		public static Recipe findMatchingRecipe(ItemStack res, LiquidStack liqu) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(res, liqu))
					return recipe;
			}
			return null;
		}

		public static boolean isResource(ItemStack resource) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.resource.isItemEqual(resource))
					return true;
			}
			return false;
		}

		public static boolean isLiquidResource(LiquidStack liquid) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.liquid.isLiquidEqual(liquid))
					return true;
			}
			return false;
		}

		public static boolean isLiquidProduct(LiquidStack liquid) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.output.isLiquidEqual(liquid))
					return true;
			}
			return false;
		}

		@Override
		public List<Entry<ItemStack[], ItemStack[]>> getRecipes() {
			HashMap<ItemStack[], ItemStack[]> recipeList = new HashMap<ItemStack[], ItemStack[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(new ItemStack[] { recipe.resource, recipe.liquid.asItemStack() }, new ItemStack[] { recipe.output.asItemStack() });
			}

			return (List<Entry<ItemStack[], ItemStack[]>>) recipeList;
		}
	}

	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	@EntityNetData
	public TankSlot productTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);

	private ItemStack[] inventoryStacks = new ItemStack[5];
	private Recipe currentRecipe;

	public int fermentationTime = 0;
	public int fermentationTotalTime = 0;
	public int fuelBurnTime = 0;
	public int fuelTotalTime = 0;
	public int fuelCurrentFerment = 0;

	public MachineFermenter(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("fermenter"));
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.0");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.FermenterGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("FermentationTime", fermentationTime);
		nbttagcompound.setInteger("FermentationTotalTime", fermentationTotalTime);
		nbttagcompound.setInteger("FuelBurnTime", fuelBurnTime);
		nbttagcompound.setInteger("FuelTotalTime", fuelTotalTime);
		nbttagcompound.setInteger("FuelCurrentFerment", fuelCurrentFerment);

		NBTTagCompound NBTresourceSlot = new NBTTagCompound();
		NBTTagCompound NBTproductSlot = new NBTTagCompound();

		resourceTank.writeToNBT(NBTresourceSlot);
		productTank.writeToNBT(NBTproductSlot);

		nbttagcompound.setTag("ResourceTank", NBTresourceSlot);
		nbttagcompound.setTag("ProductTank", NBTproductSlot);

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

		fermentationTime = nbttagcompound.getInteger("FermentationTime");
		fermentationTotalTime = nbttagcompound.getInteger("FermentationTotalTime");
		fuelBurnTime = nbttagcompound.getInteger("FuelBurnTime");
		fuelTotalTime = nbttagcompound.getInteger("FuelTotalTime");
		fuelCurrentFerment = nbttagcompound.getInteger("FuelCurrentFerment");

		resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
		productTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
		if (nbttagcompound.hasKey("ResourceTank")) {
			resourceTank.readFromNBT(nbttagcompound.getCompoundTag("ResourceTank"));
			productTank.readFromNBT(nbttagcompound.getCompoundTag("ProductTank"));
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

		// Check if we have suitable items waiting in the item slot
		if (inventoryStacks[SLOT_INPUT] != null) {

			LiquidContainerData container = LiquidHelper.getLiquidContainer(inventoryStacks[SLOT_INPUT]);
			if (container != null && RecipeManager.isLiquidResource(container.stillLiquid)) {

				inventoryStacks[SLOT_INPUT] = StackUtils.replenishByContainer(tile, inventoryStacks[SLOT_INPUT], container, resourceTank);
				if (inventoryStacks[SLOT_INPUT].stackSize <= 0) {
					inventoryStacks[SLOT_INPUT] = null;
				}

			}
		}
		// Can/capsule input/output needs to be handled here.
		if (inventoryStacks[SLOT_CAN_INPUT] != null) {
			LiquidContainerData container = LiquidHelper.getEmptyContainer(inventoryStacks[SLOT_CAN_INPUT], new LiquidStack(productTank.liquidId,
					productTank.quantity, productTank.liquidMeta));

			if (container != null) {
				inventoryStacks[SLOT_CAN_OUTPUT] = bottleIntoContainer(inventoryStacks[SLOT_CAN_INPUT], inventoryStacks[SLOT_CAN_OUTPUT], container,
						productTank);
				if (inventoryStacks[SLOT_CAN_INPUT].stackSize <= 0) {
					inventoryStacks[SLOT_CAN_INPUT] = null;
				}
			}
		}

		if (tile.worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		if (RecipeManager.findMatchingRecipe(inventoryStacks[SLOT_RESOURCE], new LiquidStack(resourceTank.liquidId, resourceTank.quantity, resourceTank.liquidMeta)) != null) {
			setErrorState(EnumErrorCode.OK);
		} else if (inventoryStacks[SLOT_FUEL] == null && fuelBurnTime <= 0) {
			setErrorState(EnumErrorCode.NOFUEL);
		} else {
			setErrorState(EnumErrorCode.NORECIPE);
		}
	}

	@Override
	public boolean doWork() {

		if (currentRecipe == null) {
			checkRecipe();
			resetRecipe();

			if (currentRecipe != null) {
				decrStackSize(SLOT_RESOURCE, 1);
				return true;
			} else
				return false;

			// If we have burnTime left, just decrease it.
		} else if (fuelBurnTime > 0 && resourceTank.quantity > 0 && productTank.quantity < Defaults.PROCESSOR_TANK_CAPACITY) {
			fuelBurnTime--;

			if (currentRecipe == null)
				return true;

			// Nothing to do, return
			if (fermentationTime <= 0)
				return false;

			resourceTank.drain(fuelCurrentFerment, true);
			fermentationTime -= this.fuelCurrentFerment;
			addProduct(new LiquidStack(currentRecipe.output.itemID, Math.round(fuelCurrentFerment * currentRecipe.modifier), currentRecipe.output.itemMeta));

			// Not done yet
			if (fermentationTime > 0)
				return true;

			currentRecipe = null;
			return true;

		} else if (fuelBurnTime <= 0) {

			// Use only fuel that provides value
			fuelBurnTime = fuelTotalTime = determineFuelValue(getFuelStack());
			if (fuelBurnTime > 0) {
				this.fuelCurrentFerment = determineFermentPerCycle(getFuelStack());
				decrStackSize(1, 1);
				return true;
			} else {
				this.fuelCurrentFerment = 0;
				return false;
			}
		}

		return false;
	}

	private void addProduct(LiquidStack output) {
		productTank.fill(output, true);
		if (productTank.quantity > Defaults.PROCESSOR_TANK_CAPACITY) {
			productTank.quantity = Defaults.PROCESSOR_TANK_CAPACITY;
		}
	}

	private void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(inventoryStacks[SLOT_RESOURCE], new LiquidStack(resourceTank.liquidId, resourceTank.quantity, resourceTank.liquidMeta));

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
		}
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			fermentationTime = 0;
			fermentationTotalTime = 0;
			return;
		}

		fermentationTime = currentRecipe.fermentationValue;
		fermentationTotalTime = currentRecipe.fermentationValue;
	}

	/**
	 * Returns the burnTime an item of the passed ItemStack provides
	 * 
	 * @param item
	 * @return
	 */
	private int determineFuelValue(ItemStack item) {
		if (item == null)
			return 0;
		int fuelid = item.getItem().itemID;

		if (fuelid == 0)
			return 0;

		if (FuelManager.fermenterFuel.containsKey(item))
			return FuelManager.fermenterFuel.get(item.getItem()).burnDuration;
		else
			return 0;
	}

	private int determineFermentPerCycle(ItemStack item) {
		if (item == null)
			return 0;
		int fuelid = item.getItem().itemID;

		if (fuelid == 0)
			return 0;

		if (FuelManager.fermenterFuel.containsKey(item.getItem()))
			return FuelManager.fermenterFuel.get(item.getItem()).fermentPerCycle;
		else
			return 0;
	}

	@Override
	public boolean isWorking() {
		if (currentRecipe == null
				&& RecipeManager.findMatchingRecipe(inventoryStacks[SLOT_RESOURCE], new LiquidStack(resourceTank.liquidId, resourceTank.quantity, resourceTank.liquidMeta)) == null)
			return false;
		if (fuelBurnTime > 0)
			return resourceTank.quantity > 0 && productTank.quantity < Defaults.PROCESSOR_TANK_CAPACITY;
		else
			return determineFuelValue(getFuelStack()) > 0;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		if (this.getFermentationStack() == null)
			return false;

		return ((float) getFermentationStack().stackSize / (float) getFermentationStack().getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		if (this.getFuelStack() == null)
			return false;

		return ((float) getFuelStack().stackSize / (float) getFuelStack().getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		if (this.getFuelStack() == null && fuelBurnTime <= 0)
			return false;
		else if (fuelBurnTime <= 0)
			if (RecipeManager.findMatchingRecipe(inventoryStacks[SLOT_RESOURCE], new LiquidStack(resourceTank.liquidId, resourceTank.quantity, resourceTank.liquidMeta)) == null)
				return false;

		if (this.getFermentationStack() == null && fermentationTime <= 0)
			return false;
		else if (fermentationTime <= 0)
			if (RecipeManager.findMatchingRecipe(inventoryStacks[SLOT_RESOURCE], new LiquidStack(resourceTank.liquidId, resourceTank.quantity, resourceTank.liquidMeta)) == null)
				return false;

		if (resourceTank.quantity <= 0)
			return false;

		if (productTank.quantity >= productTank.capacity)
			return false;

		return true;
	}

	public int getBurnTimeRemainingScaled(int i) {
		if (fuelTotalTime == 0)
			return 0;

		return (fuelBurnTime * i) / fuelTotalTime;
	}

	public int getFermentationProgressScaled(int i) {
		if (fermentationTotalTime == 0)
			return 0;

		return (fermentationTime * i) / fermentationTotalTime;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public int getProductScaled(int i) {
		return (productTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return Utils.rateTankLevel(getProductScaled(100));
	}

	public ItemStack getFermentationStack() {
		return inventoryStacks[0];
	}

	public ItemStack getFuelStack() {
		return inventoryStacks[1];
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			fuelBurnTime = j;
			break;
		case 1:
			fuelTotalTime = j;
			break;
		case 2:
			fermentationTime = j;
			break;
		case 3:
			fermentationTotalTime = j;
			break;
		case 4:
			resourceTank.liquidId = j;
			break;
		case 5:
			resourceTank.quantity = j;
			break;
		case 6:
			resourceTank.liquidMeta = j;
			break;
		case 7:
			productTank.liquidId = j;
			break;
		case 8:
			productTank.quantity = j;
			break;
		case 9:
			productTank.liquidMeta = j;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, fuelBurnTime);
		iCrafting.sendProgressBarUpdate(container, 1, fuelTotalTime);
		iCrafting.sendProgressBarUpdate(container, 2, fermentationTime);
		iCrafting.sendProgressBarUpdate(container, 3, fermentationTotalTime);
		iCrafting.sendProgressBarUpdate(container, 4, resourceTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 5, resourceTank.quantity);
		iCrafting.sendProgressBarUpdate(container, 6, resourceTank.liquidMeta);
		iCrafting.sendProgressBarUpdate(container, 7, productTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 8, productTank.quantity);
		iCrafting.sendProgressBarUpdate(container, 9, productTank.liquidMeta);
	}

	// / ISPECIALINVENTORY IMPLEMENTATION

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		int inventory;
		if (FuelManager.fermenterFuel.containsKey(stack.getItem()))
			inventory = SLOT_FUEL;
		else if (RecipeManager.isResource(stack))
			inventory = SLOT_RESOURCE;
		else if (LiquidHelper.isEmptyContainer(stack))
			inventory = SLOT_CAN_INPUT;
		else {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(stack);
			if (container != null && RecipeManager.isLiquidResource(container.stillLiquid)) {
				inventory = SLOT_INPUT;
			} else
				return 0;
		}

		int space = 0;
		if (inventoryStacks[inventory] == null) {
			space = getInventoryStackLimit();
		} else {
			if(!StackUtils.isIdenticalItem(inventoryStacks[inventory], stack))
				return 0;
			// If there is already a stack of a different item, we refuse the
			// new stack.
			space = inventoryStacks[inventory].getMaxStackSize() - inventoryStacks[inventory].stackSize;
		}

		// No space? Refuse!
		if (space <= 0)
			return 0;

		if (space >= stack.stackSize) {
			if (doAdd) {
				if (inventoryStacks[inventory] == null) {
					inventoryStacks[inventory] = stack.copy();
				} else {
					inventoryStacks[inventory].stackSize += stack.stackSize;
				}
			}
			return stack.stackSize;
		}

		// We have some space, but not enough
		if (doAdd) {
			inventoryStacks[inventory].stackSize += space;
		}

		return Math.min(stack.stackSize, space);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product = null;

		if (inventoryStacks[SLOT_CAN_OUTPUT] != null) {

			product = new ItemStack(inventoryStacks[SLOT_CAN_OUTPUT].itemID, 1, inventoryStacks[SLOT_CAN_OUTPUT].getItemDamage());
			if (doRemove) {
				inventoryStacks[SLOT_CAN_OUTPUT].stackSize--;
				if (inventoryStacks[SLOT_CAN_OUTPUT].stackSize <= 0) {
					inventoryStacks[SLOT_CAN_OUTPUT] = null;
				}
			}
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return new ItemStack[0];
	}

	// / IINVENTORY IMPLEMENTATION
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

	// / ISIDEDINVENTORY IMPLEMENTATION
	@Override
	public int getSizeInventorySide(int side) {
		return 1;
	}

	@Override
	public int getStartInventorySide(int side) {
		switch (side) {
		case 0:
			return SLOT_FUEL; // Fertilizer
		case 1:
			return SLOT_RESOURCE; // Saplings
		case 2:
			return SLOT_CAN_OUTPUT; // Buckets
		case 3:
			return SLOT_CAN_INPUT; // Cans
		default:
			return SLOT_INPUT;
		}
	}

	// ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		// We only accept water
		if (!RecipeManager.isLiquidResource(resource))
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

	@Override
	public LiquidStack drain(int tankIndex, int quantityMax, boolean doEmpty) {
		if (tankIndex != 0)
			return null;

		return productTank.drain(quantityMax, doEmpty);
	}

	@Override
	public TankSlot[] getTanks(ForgeDirection direction) {
		return new TankSlot[] { resourceTank, productTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		if (direction == tile.getOrientation().getRotation(ForgeDirection.UP))
			return resourceTank;
		else if (direction == tile.getOrientation().getRotation(ForgeDirection.DOWN))
			return productTank;
		return null;
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowFuel25);
		res.add(ForestryTrigger.lowFuel10);
		res.add(ForestryTrigger.lowResource25);
		res.add(ForestryTrigger.lowResource10);
		res.add(ForestryTrigger.hasWork);
		return res;
	}

	// / INITIALIZATION
	public static void initialize() {

		RecipeUtil.injectLeveledRecipe(new ItemStack(Block.sapling, 1, 0), GameMode.getGameMode().getFermentedPerSapling(), new ItemStack(
				ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(new ItemStack(Block.sapling, 1, 1), GameMode.getGameMode().getFermentedPerSapling(), new ItemStack(
				ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(new ItemStack(Block.sapling, 1, 2), GameMode.getGameMode().getFermentedPerSapling(), new ItemStack(
				ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(new ItemStack(Block.sapling, 1, 3), GameMode.getGameMode().getFermentedPerSapling(), new ItemStack(
				ForestryItem.liquidBiomass));

		RecipeUtil.injectLeveledRecipe(new ItemStack(Block.cactus), GameMode.getGameMode().getFermentedPerCacti(), new ItemStack(ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(new ItemStack(Item.wheat), GameMode.getGameMode().getFermentedPerWheat(), new ItemStack(ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(new ItemStack(Item.reed), GameMode.getGameMode().getFermentedPerCane(), new ItemStack(ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(new ItemStack(Block.mushroomBrown), GameMode.getGameMode().getFermentedPerMushroom(), new ItemStack(
				ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(new ItemStack(Block.mushroomRed), GameMode.getGameMode().getFermentedPerMushroom(), new ItemStack(
				ForestryItem.liquidBiomass));
	}

}
