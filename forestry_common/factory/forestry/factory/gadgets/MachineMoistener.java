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
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerManager;
import forestry.api.recipes.RecipeManagers;
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

public class MachineMoistener extends Machine {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineMoistener((TileMachine) tile);
		}
	}

	public static class Recipe {
		public int timePerItem;
		public ItemStack resource;
		public ItemStack product;

		public Recipe(ItemStack resource, ItemStack product, int timePerItem) {
			this.timePerItem = timePerItem;
			this.resource = resource;
			this.product = product;
		}

		public boolean matches(ItemStack res) {
			if (res == null && resource == null)
				return true;
			else if (res == null && resource != null)
				return false;
			else if (res != null && resource == null)
				return false;
			else
				return resource.isItemEqual(res);
		}
	}

	public static class RecipeManager implements IMoistenerManager {
		public static ArrayList<MachineMoistener.Recipe> recipes = new ArrayList<MachineMoistener.Recipe>();

		@Override
		public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {
			recipes.add(new MachineMoistener.Recipe(resource, product, timePerItem));
		}

		public static Recipe findMatchingRecipe(ItemStack item) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(item))
					return recipe;
			}
			return null;
		}

		@Override
		public List<Entry<ItemStack[], ItemStack[]>> getRecipes() {
			HashMap<ItemStack[], ItemStack[]> recipeList = new HashMap<ItemStack[], ItemStack[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(new ItemStack[] { recipe.resource }, new ItemStack[] { recipe.product });
			}

			return (List<Entry<ItemStack[], ItemStack[]>>) recipeList;
		}
	}

	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	private ItemStack[] inventoryStacks = new ItemStack[12];
	public MachineMoistener.Recipe currentRecipe;

	private short stashSlot1 = 0;
	private short reservoirSlot1 = 6;
	private short reservoirSize = 3;
	private short workingSlot = 9;
	private short productSlot = 10;
	private short resourceSlot = 11;

	public int burnTime = 0;
	public int totalTime = 0;
	public int productionTime = 0;
	private int timePerItem = 0;
	private ItemStack currentProduct;
	private ItemStack pendingProduct;

	public ItemStack getResourceStack() {
		return inventoryStacks[resourceSlot];
	}

	public MachineMoistener(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("moistener"));
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.6");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.MoistenerGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	// / LOADING & SAVING
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("BurnTime", burnTime);
		nbttagcompound.setInteger("TotalTime", totalTime);
		nbttagcompound.setInteger("ProductionTime", productionTime);

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

		// Write pending product
		if (pendingProduct != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			pendingProduct.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("PendingProduct", nbttagcompoundP);
		}
		if (currentProduct != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			currentProduct.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("CurrentProduct", nbttagcompoundP);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		burnTime = nbttagcompound.getInteger("BurnTime");
		totalTime = nbttagcompound.getInteger("TotalTime");
		productionTime = nbttagcompound.getInteger("ProductionTime");

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

		// Load pending product
		if (nbttagcompound.hasKey("PendingProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("PendingProduct");
			pendingProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}
		if (nbttagcompound.hasKey("CurrentProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("CurrentProduct");
			currentProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}

		checkRecipe();
	}

	@Override
	public void updateServerSide() {

		// Check if we have suitable water container waiting in the item slot
		if (inventoryStacks[productSlot] != null) {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(inventoryStacks[productSlot]);
			if (container != null && container.stillLiquid.isLiquidEqual(new ItemStack(Block.waterStill))) {

				inventoryStacks[productSlot] = StackUtils.replenishByContainer(tile, inventoryStacks[productSlot], container, resourceTank);
				if (inventoryStacks[productSlot].stackSize <= 0) {
					inventoryStacks[productSlot] = null;
				}
			}
		}

		// Let's get to work
		int lightvalue = tile.worldObj.getBlockLightValue(tile.xCoord, tile.yCoord + 1, tile.zCoord);

		// Not working in broad daylight
		if (lightvalue > 11) {
			setErrorState(EnumErrorCode.NOTGLOOMY);
			return;
		}

		// The darker, the better
		int speed;
		if (lightvalue >= 9) {
			speed = 1;
		} else if (lightvalue >= 7) {
			speed = 2;
		} else if (lightvalue >= 5) {
			speed = 3;
		} else {
			speed = 4;
		}

		// Already running
		if (burnTime > 0 && pendingProduct == null) {
			// Not working if there is no water available.
			if (resourceTank.quantity <= 0)
				return;

			checkRecipe();

			if (currentRecipe == null)
				return;

			resourceTank.quantity--;
			burnTime -= speed;
			productionTime -= speed;

			if (productionTime <= 0) {
				pendingProduct = currentProduct;
				decrStackSize(resourceSlot, 1);
				resetRecipe();
				tryAddPending();
			}

		} else if (pendingProduct != null) {
			tryAddPending();
			// Try to start process
		} else // Make sure we have a new item in the working slot.
		if (rotateWorkingSlot()) {
			checkRecipe();

			// Let's see if we have a valid resource in the working slot
			if (inventoryStacks[workingSlot] == null)
				return;

			if (FuelManager.moistenerResource.containsKey(inventoryStacks[workingSlot])) {
				MoistenerFuel res = FuelManager.moistenerResource.get(inventoryStacks[workingSlot]);
				burnTime = totalTime = res.moistenerValue;
			}
		} else {
			rotateReservoir();
		}

		if (currentRecipe != null) {
			setErrorState(EnumErrorCode.OK);
		} else {
			setErrorState(EnumErrorCode.NORECIPE);
		}
	}

	private boolean tryAddPending() {
		if (pendingProduct == null)
			return false;

		if (inventoryStacks[productSlot] == null) {
			inventoryStacks[productSlot] = pendingProduct.copy();
			pendingProduct = null;
			return true;
		}

		if (inventoryStacks[productSlot].isItemEqual(pendingProduct)
				&& inventoryStacks[productSlot].stackSize <= inventoryStacks[productSlot].getMaxStackSize() - pendingProduct.stackSize) {
			inventoryStacks[productSlot].stackSize += pendingProduct.stackSize;
			pendingProduct = null;
			return true;
		}

		return false;
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(getResourceStack());
		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
		if (currentRecipe == null) {
			currentProduct = null;
			productionTime = 0;
			timePerItem = 0;
			setErrorState(EnumErrorCode.NORECIPE);
			return;
		}

		currentProduct = currentRecipe.product;
		productionTime = currentRecipe.timePerItem;
		timePerItem = currentRecipe.timePerItem;
	}

	private int getFreeSlot(ItemStack deposit, int startSlot, int endSlot, boolean emptyOnly) {
		int slot = -1;

		for (int i = startSlot; i < endSlot; i++) {
			// Empty slots are okay.
			if (inventoryStacks[i] == null) {
				if (slot < 0) {
					slot = i;
				}
				continue;
			}

			if(emptyOnly)
				continue;
			
			// Wrong item or full
			if (!inventoryStacks[i].isItemEqual(deposit) || inventoryStacks[i].stackSize >= inventoryStacks[i].getMaxStackSize()) {
				continue;
			}

			slot = i;
		}

		return slot;
	}

	private int getFreeStashSlot(ItemStack deposit, boolean emptyOnly) {
		return getFreeSlot(deposit, 0, reservoirSlot1, emptyOnly);
	}

	private int getFreeReservoirSlot(ItemStack deposit) {
		return getFreeSlot(deposit, reservoirSlot1, reservoirSlot1 + reservoirSize, false);

	}

	private int getNextResourceSlot(int startSlot, int endSlot) {
		// Let's look for a new resource to put into the working slot.
		int stage = -1;
		int resourceSlot = -1;

		for (int i = startSlot; i < endSlot; i++) {
			if (inventoryStacks[i] == null) {
				continue;
			}

			if (!FuelManager.moistenerResource.containsKey(inventoryStacks[i]))
				continue;

			MoistenerFuel res = FuelManager.moistenerResource.get(inventoryStacks[i]);
			if (stage < 0 || res.stage < stage) {
				stage = res.stage;
				resourceSlot = i;
			}
		}

		return resourceSlot;
	}

	private boolean rotateWorkingSlot() {
		// Put working slot contents into inventory if space is available
		if (inventoryStacks[workingSlot] != null) {
			// Get the result of the consumed item in the working slot
			ItemStack deposit;
			if (FuelManager.moistenerResource.containsKey(inventoryStacks[workingSlot])) {
				MoistenerFuel res = FuelManager.moistenerResource.get(inventoryStacks[workingSlot]);
				deposit = res.product.copy();
			} else {
				deposit = inventoryStacks[workingSlot].copy();
			}

			int targetSlot = getFreeReservoirSlot(deposit);
			// We stop the whole thing, if we don't have any room anymore.
			if (targetSlot < 0)
				return false;

			if (inventoryStacks[targetSlot] == null) {
				inventoryStacks[targetSlot] = deposit;
			} else {
				inventoryStacks[targetSlot].stackSize++;
			}

			decrStackSize(workingSlot, 1);
		}

		if (inventoryStacks[workingSlot] != null)
			return true;

		// Let's look for a new resource to put into the working slot.
		int resourceSlot = getNextResourceSlot(reservoirSlot1, reservoirSlot1 + reservoirSize);
		// Nothing found, stop.
		if (resourceSlot < 0)
			return false;

		inventoryStacks[workingSlot] = inventoryStacks[resourceSlot].splitStack(1);
		if (inventoryStacks[resourceSlot].stackSize <= 0) {
			inventoryStacks[resourceSlot] = null;
		}

		return true;
	}

	private void rotateReservoir() {
		ArrayList<Integer> slotsToShift = new ArrayList();

		for (int i = reservoirSlot1; i < reservoirSlot1 + reservoirSize; i++) {
			if (inventoryStacks[i] == null) {
				continue;
			}

			if (!FuelManager.moistenerResource.containsKey(inventoryStacks[i]))
				slotsToShift.add(i);
		}

		// Move consumed items back to stash
		int shiftedSlots = 0;
		for (int slot : slotsToShift) {
			int targetSlot = getFreeStashSlot(inventoryStacks[slot], true);
			if (targetSlot < 0) {
				continue;
			}
			
			inventoryStacks[targetSlot] = inventoryStacks[slot];
			inventoryStacks[slot] = null;
			shiftedSlots++;
			//} else {
			//	StackUtils.mergeStacks(inventoryStacks[slot], inventoryStacks[targetSlot]);
			//	if (inventoryStacks[slot] != null && inventoryStacks[slot].stackSize <= 0) {
			//		inventoryStacks[slot] = null;
			//	}
		}

		// Grab new items from stash
		for (int i = 0; i < (slotsToShift.size() > 0 ? shiftedSlots : 2); i++) {
			int resourceSlot = getNextResourceSlot(0, reservoirSlot1);
			// Stop if no resources are available
			if (resourceSlot < 0) {
				break;
			}
			int targetSlot = getFreeReservoirSlot(inventoryStacks[resourceSlot]);
			// No free target slot, stop
			if (targetSlot < 0) {
				break;
			}
			// Else shift
			if (inventoryStacks[targetSlot] == null) {
				inventoryStacks[targetSlot] = inventoryStacks[resourceSlot];
				inventoryStacks[resourceSlot] = null;
			} else {
				StackUtils.mergeStacks(inventoryStacks[resourceSlot], inventoryStacks[targetSlot]);
				if (inventoryStacks[resourceSlot] != null && inventoryStacks[resourceSlot].stackSize <= 0) {
					inventoryStacks[resourceSlot] = null;
				}
			}
		}
	}

	@Override
	public boolean doWork() {
		return false;
	}

	@Override
	public boolean isWorking() {
		return burnTime > 0 && resourceTank.quantity > 0;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		int max = 0;
		int avail = 0;

		for (int i = stashSlot1; i < reservoirSlot1; i++) {
			if (inventoryStacks[i] == null) {
				max += 64;
				continue;
			}
			if (FuelManager.moistenerResource.containsKey(inventoryStacks[i])) {
				MoistenerFuel res = FuelManager.moistenerResource.get(inventoryStacks[i]);
				if (res.item.isItemEqual(inventoryStacks[i])) {
					max += 64;
					avail += inventoryStacks[i].stackSize;
				}
			}
		}

		return ((float) avail / (float) max) > percentage;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		if (inventoryStacks[resourceSlot] == null)
			return false;

		return ((float) inventoryStacks[resourceSlot].stackSize / (float) inventoryStacks[resourceSlot].getMaxStackSize()) > percentage;
	}

	public boolean isProducing() {
		return productionTime > 0;
	}

	public int getProductionProgressScaled(int i) {
		if (timePerItem == 0)
			return 0;

		return (productionTime * i) / timePerItem;

	}

	public int getConsumptionProgressScaled(int i) {
		if (totalTime == 0)
			return 0;

		return (burnTime * i) / totalTime;

	}

	public int getResourceScaled(int i) {
		return (resourceTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
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
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventoryStacks[i] = itemstack;
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

	// ISIDEDINVENTORY
	@Override
	public int getStartInventorySide(int side) {
		// BOTTOM
		if (side == 0)
			return productSlot;
		// TOP
		else if (side == 1)
			return resourceSlot;
		// SIDES
		else
			return 0;
	}

	@Override
	public int getSizeInventorySide(int side) {
		if (side == 0)
			return 1;
		else if (side == 1)
			return 1;
		else
			return 6;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (inventoryStacks[slot] == null)
			return null;
		ItemStack toReturn = inventoryStacks[slot];
		inventoryStacks[slot] = null;
		return toReturn;
	}

	// / ISPECIALINVENTORY
	/**
	 * Will try to keep one stack free.
	 */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		LiquidContainerData container = LiquidHelper.getLiquidContainer(stack);
		if (container != null && container.stillLiquid.isLiquidEqual(new LiquidStack(Block.waterStill, 1))) {
			// Add if empty
			if (inventoryStacks[productSlot] == null) {
				if (doAdd) {
					inventoryStacks[productSlot] = stack.copy();
				}
				return stack.stackSize;
			}

			if (!inventoryStacks[productSlot].isItemEqual(stack))
				return 0;

			int space = inventoryStacks[productSlot].getMaxStackSize() - inventoryStacks[productSlot].stackSize;
			// No space left
			if (space <= 0)
				return 0;

			// Not enough space
			if (space < stack.stackSize) {
				if (doAdd) {
					inventoryStacks[productSlot].stackSize = inventoryStacks[productSlot].getMaxStackSize();
				}
				return space;
				// Ample space
			} else {
				if (doAdd) {
					inventoryStacks[productSlot].stackSize += stack.stackSize;
				}
				return stack.stackSize;
			}
		}

		// Try to add to resource slot if input is from top or bottom.
		if (from == ForgeDirection.UP || from == ForgeDirection.DOWN) {

			// Add if empty
			if (inventoryStacks[resourceSlot] == null) {
				if (doAdd) {
					inventoryStacks[resourceSlot] = stack.copy();
				}
				return stack.stackSize;
			}
			// Don't add if already occupied by another item
			if (!inventoryStacks[resourceSlot].isItemEqual(stack))
				return 0;

			int space = inventoryStacks[resourceSlot].getMaxStackSize() - inventoryStacks[resourceSlot].stackSize;
			// No space left
			if (space <= 0)
				return 0;

			// Not enough space
			if (space < stack.stackSize) {
				if (doAdd) {
					inventoryStacks[resourceSlot].stackSize = inventoryStacks[resourceSlot].getMaxStackSize();
				}
				return space;
				// Ample space
			} else {
				if (doAdd) {
					inventoryStacks[resourceSlot].stackSize += stack.stackSize;
				}
				return stack.stackSize;
			}
		}

		int freeSlots = 0;
		int slot = -1;
		int used = 0;

		for (int i = 0; i < reservoirSlot1; i++) {

			// We do not add at once to free inventorySlots.
			if (inventoryStacks[i] == null) {
				freeSlots++;
				slot = i;
				continue;
			}

			if (inventoryStacks[i].isItemEqual(stack)) {
				int space = inventoryStacks[i].getMaxStackSize() - inventoryStacks[i].stackSize;
				if (space <= 0) {
					continue;
				}
				if (space < stack.stackSize - used) {
					if (doAdd) {
						inventoryStacks[i].stackSize = inventoryStacks[i].getMaxStackSize();
					}

					used += space;
					continue;
				}

				if (doAdd) {
					inventoryStacks[i].stackSize += stack.stackSize - used;
				}
				return stack.stackSize;
			}
		}

		// Now let's check if there are more than two free slots available since
		// we couldn't add everything to already occupied slots
		if (freeSlots <= 2)
			return used;

		if (doAdd) {
			inventoryStacks[slot] = stack.copy();
			inventoryStacks[slot].stackSize = stack.stackSize - used;
		}
		return stack.stackSize;
	}

	/**
	 * Transport pipes can extract from the moistener's product stack and it can extract all items that are not valid moistener resources.
	 */
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product;

		if (inventoryStacks[productSlot] != null && inventoryStacks[productSlot].stackSize > 0) {

			product = new ItemStack(inventoryStacks[productSlot].itemID, 1, inventoryStacks[productSlot].getItemDamage());
			if (doRemove) {
				inventoryStacks[productSlot].stackSize--;
				if (inventoryStacks[productSlot].stackSize <= 0) {
					inventoryStacks[productSlot] = null;
				}
			}
			return new ItemStack[] { product };
		} else {
			for (int i = 0; i < reservoirSlot1; i++) {
				if (inventoryStacks[i] == null) {
					continue;
				}

				if (!FuelManager.moistenerResource.containsKey(inventoryStacks[i])) {
					product = new ItemStack(inventoryStacks[i].itemID, 1, inventoryStacks[i].getItemDamage());
					if (doRemove) {
						inventoryStacks[i].stackSize--;
						if (inventoryStacks[i].stackSize <= 0) {
							inventoryStacks[i] = null;
						}
					}
					return new ItemStack[] { product };
				}
			}
		}

		return new ItemStack[0];
	}

	// ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
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

	// / SMP GUI
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
			productionTime = j;
			break;
		case 3:
			timePerItem = j;
			break;
		case 4:
			resourceTank.liquidId = j;
			break;
		case 5:
			resourceTank.quantity = j;
			break;
                case 6: resourceTank.liquidMeta = j;
                        break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, burnTime);
		iCrafting.sendProgressBarUpdate(container, 1, totalTime);
		iCrafting.sendProgressBarUpdate(container, 2, productionTime);
		iCrafting.sendProgressBarUpdate(container, 3, timePerItem);
		iCrafting.sendProgressBarUpdate(container, 4, resourceTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 5, resourceTank.quantity);
                iCrafting.sendProgressBarUpdate(container, 6, resourceTank.liquidMeta);
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowFuel25);
		res.add(ForestryTrigger.lowFuel10);
		res.add(ForestryTrigger.lowResource25);
		res.add(ForestryTrigger.lowResource10);
		return res;
	}

	public static void initialize() {
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Item.seeds), new ItemStack(Block.mycelium), 5000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Block.cobblestone), new ItemStack(Block.cobblestoneMossy), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Block.stoneBrick), new ItemStack(Block.stoneBrick, 1, 1), 20000);
	}
}
