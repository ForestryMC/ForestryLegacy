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
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
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
import forestry.api.recipes.IBottlerManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.core.utils.Utils;

public class MachineBottler extends Machine {

	// / CONSTANTS
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_PRODUCT = 1;
	public static final short SLOT_CAN = 2;

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineBottler((TileMachine) tile);
		}
	}

	public static class Recipe {
		public final int cyclesPerUnit;
		public final LiquidStack input;
		public final ItemStack can;
		public final ItemStack bottled;

		public Recipe(int cyclesPerUnit, LiquidStack input, ItemStack can, ItemStack bottled) {
			this.cyclesPerUnit = cyclesPerUnit;
			this.input = input;
			this.can = can;
			this.bottled = bottled;
		}

		public boolean matches(LiquidStack res, ItemStack empty) {
			return input.isLiquidEqual(res) && res.amount >= input.amount && can.isItemEqual(empty);
		}

		public boolean hasInput(LiquidStack res) {
			return input.isLiquidEqual(res);
		}

		public boolean hasCan(ItemStack res) {
			return can.isItemEqual(res);
		}
	}

	public static class RecipeManager implements IBottlerManager {
		public static ArrayList<MachineBottler.Recipe> recipes = new ArrayList<MachineBottler.Recipe>();

		@Override
		public void addRecipe(int cyclesPerUnit, LiquidStack input, ItemStack can, ItemStack bottled) {
			recipes.add(new MachineBottler.Recipe(cyclesPerUnit, input, can, bottled));
		}

		/**
		 * 
		 * @param res
		 * @param empty
		 * @return Recipe matching both res and empty, null if none
		 */
		public static Recipe findMatchingRecipe(LiquidStack res, ItemStack empty) {
			// We need both ingredients
			if (res == null || empty == null)
				return null;

			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(res, empty))
					return recipe;
			}
			return null;
		}

		/**
		 * 
		 * @param res
		 * @return true if any recipe has a matching input
		 */
		public static boolean isInput(LiquidStack res) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.hasInput(res))
					return true;
			}
			return false;

		}

		public static boolean hasCan(ItemStack res) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.hasCan(res))
					return true;
			}
			return false;
		}

		@Override
		public List<Entry<ItemStack[], ItemStack[]>> getRecipes() {
			HashMap<ItemStack[], ItemStack[]> recipeList = new HashMap<ItemStack[], ItemStack[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(new ItemStack[] { recipe.input.asItemStack(), recipe.can }, new ItemStack[] { recipe.bottled });
			}

			return (List<Entry<ItemStack[], ItemStack[]>>) recipeList;
		}
	}

	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);

	private ItemStack[] inventoryStacks = new ItemStack[3];

	private boolean productPending = false;

	private Recipe currentRecipe;
	private Stack<ItemStack> pendingProducts = new Stack<ItemStack>();
	private int fillingTime;
	private int fillingTotalTime;

	public MachineBottler(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("bottler"));
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.2");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.BottlerGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("FillingTime", fillingTime);
		nbttagcompound.setInteger("FillingTotalTime", fillingTotalTime);
		nbttagcompound.setBoolean("ProductPending", productPending);

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

		nbttaglist = new NBTTagList();
		ItemStack[] offspring = pendingProducts.toArray(new ItemStack[pendingProducts.size()]);
		for (int i = 0; i < offspring.length; i++)
			if (offspring[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				offspring[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingProducts", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		fillingTime = nbttagcompound.getInteger("FillingTime");
		fillingTotalTime = nbttagcompound.getInteger("FillingTotalTime");
		productPending = nbttagcompound.getBoolean("ProductPending");

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

		nbttaglist = nbttagcompound.getTagList("PendingProducts");
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			pendingProducts.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

		checkRecipe();
	}

	@Override
	public void updateServerSide() {
		// Check if we have suitable items waiting in the item slot
		if (inventoryStacks[SLOT_CAN] != null) {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(inventoryStacks[SLOT_CAN]);
			if (container != null && RecipeManager.isInput(container.stillLiquid)) {

				inventoryStacks[SLOT_CAN] = StackUtils.replenishByContainer(tile, inventoryStacks[SLOT_CAN], container, resourceTank);
				if (inventoryStacks[SLOT_CAN].stackSize <= 0) {
					inventoryStacks[SLOT_CAN] = null;
				}
			}
		}

		if (tile.worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		checkRecipe();
		if (getErrorState() == EnumErrorCode.NORECIPE && currentRecipe != null) {
			setErrorState(EnumErrorCode.OK);
		}
	}

	@Override
	public boolean doWork() {

		checkRecipe();

		// If we add pending products, we skip to the next work cycle.
		if (tryAddPending())
			return false;

		if (!pendingProducts.isEmpty())
			return false;

		// Continue work if nothing needs to be added
		if (fillingTime <= 0)
			return false;

		if (currentRecipe == null) {
			setErrorState(EnumErrorCode.NORECIPE);
			return false;
		}

		fillingTime--;
		// Still not done, return
		if (fillingTime > 0) {
			setErrorState(EnumErrorCode.OK);
			return true;
		}

		// We are done, add products to queue and remove resources
		pendingProducts.push(currentRecipe.bottled.copy());

		inventoryStacks[SLOT_RESOURCE].stackSize--;
		if (inventoryStacks[SLOT_RESOURCE].stackSize <= 0) {
			inventoryStacks[SLOT_RESOURCE] = null;
		}
		resourceTank.quantity -= currentRecipe.input.amount;
		if (resourceTank.quantity < 0) {
			resourceTank.quantity = 0;
		}
		checkRecipe();
		resetRecipe();

		while (tryAddPending()) {
			;
		}
		return true;
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(new LiquidStack(resourceTank.liquidId, resourceTank.quantity), inventoryStacks[SLOT_RESOURCE]);

		if (sameRec == null) {
			setErrorState(EnumErrorCode.NORECIPE);
		}

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			fillingTime = 0;
			fillingTotalTime = 0;
			return;
		}

		fillingTime = currentRecipe.cyclesPerUnit;
		fillingTotalTime = currentRecipe.cyclesPerUnit;
	}

	private boolean tryAddPending() {
		if (pendingProducts.isEmpty())
			return false;

		ItemStack next = pendingProducts.peek();
		if (addProduct(next, true)) {
			pendingProducts.pop();
			return true;
		}

		setErrorState(EnumErrorCode.NOSPACE);
		return false;
	}

	private boolean addProduct(ItemStack product, boolean all) {
		for (int i = SLOT_PRODUCT; i < SLOT_PRODUCT + 1; i++) {

			// Empty slot. Add
			if (inventoryStacks[i] == null) {
				inventoryStacks[i] = product;
				return true;
			}

			// Already occupied by different item, skip this slot.
			if (!inventoryStacks[i].isItemEqual(product)) {
				continue;
			}

			int space = inventoryStacks[i].getMaxStackSize() - inventoryStacks[i].stackSize;
			// No space left, skip this slot.
			if (space <= 0) {
				continue;
			}
			// Enough space
			if (space >= product.stackSize) {
				inventoryStacks[i].stackSize += product.stackSize;
				product.stackSize = 0;
				return true;
			}

			// Not enough space
			if (all) {
				continue;
			}

			inventoryStacks[i].stackSize = inventoryStacks[i].getMaxStackSize();
			product.stackSize -= space;

			return true;
		}
		return false;
	}

	// / STATE INFORMATION
	@Override
	public boolean isWorking() {
		return fillingTime > 0;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		if (inventoryStacks[SLOT_RESOURCE] == null)
			return false;

		return ((float) inventoryStacks[SLOT_RESOURCE].stackSize / (float) inventoryStacks[SLOT_RESOURCE].getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		return currentRecipe != null;
	}

	public int getFillProgressScaled(int i) {
		if (fillingTotalTime == 0)
			return 0;

		return (fillingTime * i) / fillingTotalTime;

	}

	public int getResourceScaled(int i) {
		return (resourceTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	/*
	 * private boolean addProduct(int i) { if(inventoryStacks[productSlot] == null) { inventoryStacks[productSlot] = new ItemStack(PluginIC2.fuelcanFilled, i,
	 * PluginIC2.fuelcanMeta); return true; }
	 * 
	 * // There may be only one item in the product slot. return false; }
	 */

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

	// / ISPECIALINVENTORY IMPLEMENTATION
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		// Only add empty fuel cans
		if (!MachineBottler.RecipeManager.hasCan(stack))
			return 0;

		// Add if the stack is empty
		if (inventoryStacks[SLOT_RESOURCE] == null) {
			if (doAdd) {
				inventoryStacks[SLOT_RESOURCE] = stack.copy();
			}
			return stack.stackSize;
		}

		// Only add if there is still space
		if (inventoryStacks[SLOT_RESOURCE].stackSize >= inventoryStacks[SLOT_RESOURCE].getMaxStackSize())
			return 0;

		// Only add if not occupied by another item
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

		ItemStack product = new ItemStack(inventoryStacks[SLOT_PRODUCT].itemID, 1, inventoryStacks[SLOT_PRODUCT].getItemDamage());
		if (doRemove) {
			inventoryStacks[SLOT_PRODUCT].stackSize--;
			if (inventoryStacks[SLOT_PRODUCT].stackSize <= 0) {
				inventoryStacks[SLOT_PRODUCT] = null;
			}
		}
		return new ItemStack[] { product };
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
	public int getStartInventorySide(int side) {
		switch (side) {
		case 0:
			return SLOT_PRODUCT; // Filled fuel cans
		case 1:
			return SLOT_RESOURCE; // Empty fuel cans
		default:
			return SLOT_CAN; // Buckets
		}
	}

	@Override
	public int getSizeInventorySide(int side) {
		return 1;
	}

	// ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {

		// We only accept what is already in the tank or valid ingredients
		if (resourceTank.quantity > 0 && resourceTank.liquidId != resource.itemID)
			return 0;
		else if (!RecipeManager.isInput(resource))
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0) {
			// TODO: Slow down updates
			tile.sendNetworkUpdate();
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

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowResource25);
		res.add(ForestryTrigger.lowResource10);
		res.add(ForestryTrigger.hasWork);
		return res;
	}

	// / INITIALIZATION
	public static void initialize() {
		// Everything done by ForestryCore.injectLiquidContainer now.
	}

}
