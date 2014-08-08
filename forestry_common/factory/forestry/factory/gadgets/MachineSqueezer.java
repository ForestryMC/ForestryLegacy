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
import forestry.api.recipes.ISqueezerManager;
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
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.core.utils.Utils;

public class MachineSqueezer extends Machine {

	/**
	 * Factory class to produce new {@link MachineSqueezer}s.
	 */
	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineSqueezer((TileMachine) tile);
		}
	}

	// / RECIPE MANAGMENT
	public static class Recipe {
		public final int timePerItem;
		public final ItemStack[] resources;
		public final LiquidStack liquid;
		public final ItemStack remnants;
		public final int chance;

		public Recipe(int timePerItem, ItemStack[] resources, LiquidStack liquid, ItemStack remnants, int chance) {
			this.timePerItem = timePerItem;
			this.resources = resources;
			this.liquid = liquid;
			this.remnants = remnants;
			this.chance = chance;
		}

		public boolean matches(ItemStack[] res) {
			// No recipe without resource!
			if (res == null || res.length <= 0)
				return false;

			boolean matchedAll = true;

			for (ItemStack stack : resources) {
				boolean matched = false;
				for (ItemStack matchStack : res) {
					if (matchStack == null) {
						continue;
					}

					// If the item matches, we need enough items
					if (matchStack.isItemEqual(stack))
						if (matchStack.stackSize >= stack.stackSize) {
							matched = true;
							break;
						}
				}
				if (!matched) {
					matchedAll = false;
				}
			}
			return matchedAll;
		}
	}

	public static class RecipeManager implements ISqueezerManager {
		public static ArrayList<MachineSqueezer.Recipe> recipes = new ArrayList<MachineSqueezer.Recipe>();

		@Override
		public void addRecipe(int timePerItem, ItemStack[] resources, LiquidStack liquid, ItemStack remnants, int chance) {
			recipes.add(new MachineSqueezer.Recipe(timePerItem, resources, liquid, remnants, chance));
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack[] resources, LiquidStack liquid) {
			addRecipe(timePerItem, resources, liquid, null, 0);
		}

		public static Recipe findMatchingRecipe(ItemStack[] items) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(items))
					return recipe;
			}
			return null;
		}

		@Override
		public List<Entry<ItemStack[], ItemStack[]>> getRecipes() {
			HashMap<ItemStack[], ItemStack[]> recipeList = new HashMap<ItemStack[], ItemStack[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(recipe.resources, new ItemStack[] { recipe.remnants, recipe.liquid.asItemStack() });
			}

			return (List<Entry<ItemStack[], ItemStack[]>>) recipeList;
		}
	}

	// / MEMBER
	@EntityNetData
	public TankSlot productTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	private ItemStack[] inventoryStacks = new ItemStack[12];
	private Recipe currentRecipe;

	private short resourceSlot1 = 0;
	private short remnantSlot = 9;
	private short canSlot = 10;
	private short outputSlot = 11;

	private Stack<LiquidStack> pendingLiquids = new Stack<LiquidStack>();
	private Stack<ItemStack> pendingRemnants = new Stack<ItemStack>();
	private int productionTime;
	private int timePerItem;

	public MachineSqueezer(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("squeezer"));
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.9");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.SqueezerGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	// / LOADING & SAVING
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("ProductionTime", productionTime);
		nbttagcompound.setInteger("TimePerItem", timePerItem);

		// Inventory
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventoryStacks.length; i++)
			if (inventoryStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventoryStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("Items", nbttaglist);

		// Pending remnants
		nbttaglist = new NBTTagList();
		ItemStack[] remnants = pendingRemnants.toArray(new ItemStack[pendingRemnants.size()]);
		for (int i = 0; i < remnants.length; i++)
			if (remnants[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				remnants[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingRemnants", nbttaglist);

		// Pending liquids
		nbttaglist = new NBTTagList();
		LiquidStack[] liquids = pendingLiquids.toArray(new LiquidStack[pendingLiquids.size()]);
		for (int i = 0; i < liquids.length; i++)
			if (liquids[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				liquids[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingLiquids", nbttaglist);

		// ProductTank
		NBTTagCompound NBTproductSlot = new NBTTagCompound();
		productTank.writeToNBT(NBTproductSlot);
		nbttagcompound.setTag("ProductTank", NBTproductSlot);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		productionTime = nbttagcompound.getInteger("ProductionTime");
		timePerItem = nbttagcompound.getInteger("TimePerItem");

		// Inventory
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		inventoryStacks = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < inventoryStacks.length) {
				inventoryStacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		// Pending remnants
		nbttaglist = nbttagcompound.getTagList("PendingRemnants");
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			pendingRemnants.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

		// Pending liquids
		nbttaglist = nbttagcompound.getTagList("PendingLiquids");
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			pendingLiquids.add(LiquidStack.loadLiquidStackFromNBT(nbttagcompound1));
		}

		// Product tank
		productTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
		if (nbttagcompound.hasKey("ProductTank")) {
			productTank.readFromNBT(nbttagcompound.getCompoundTag("ProductTank"));
		}

		checkRecipe();
	}

	// / WORKING
	@Override
	public void updateServerSide() {

		// Can/capsule input/output needs to be handled here.
		if (inventoryStacks[canSlot] != null) {

			LiquidContainerData container = LiquidHelper.getEmptyContainer(inventoryStacks[canSlot],
					new LiquidStack(productTank.liquidId, productTank.quantity, productTank.liquidMeta));
			if (container != null) {
				inventoryStacks[outputSlot] = bottleIntoContainer(inventoryStacks[canSlot], inventoryStacks[outputSlot], container, productTank);
				if (inventoryStacks[canSlot].stackSize <= 0) {
					inventoryStacks[canSlot] = null;
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
		tryAddPending();

		if (!pendingLiquids.isEmpty() || !pendingRemnants.isEmpty())
			return false;

		// Continue work if nothing needs to be added
		if (productionTime <= 0)
			return false;

		if (currentRecipe == null)
			return false;

		productionTime--;
		// Still not done, return
		if (productionTime > 0) {
			setErrorState(EnumErrorCode.OK);
			return true;
		}

		// We are done, add products to queue
		pendingLiquids.push(currentRecipe.liquid.copy());
		if (currentRecipe.remnants != null && tile.worldObj.rand.nextInt(100) < currentRecipe.chance) {
			pendingRemnants.push(currentRecipe.remnants.copy());
		}

		removeResources(currentRecipe.resources);

		checkRecipe();
		resetRecipe();

		tryAddPending();
		setErrorState(EnumErrorCode.OK);

		return true;
	}

	private void checkRecipe() {
		ItemStack[] resources = new ItemStack[9];
		System.arraycopy(inventoryStacks, 0, resources, 0, 9);

		Recipe sameRec = RecipeManager.findMatchingRecipe(resources);

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
			productionTime = 0;
			timePerItem = 0;
			return;
		}

		productionTime = currentRecipe.timePerItem;
		timePerItem = currentRecipe.timePerItem;
	}

	private boolean tryAddPending() {

		if (!pendingLiquids.isEmpty()) {
			LiquidStack next = pendingLiquids.peek();
			if (addProduct(next)) {
				pendingLiquids.pop();
				return true;
			}
		}

		if (!pendingRemnants.isEmpty()) {
			ItemStack next = pendingRemnants.peek();
			if (addRemnant(next)) {
				pendingRemnants.pop();
				return true;
			}
		}

		if (!pendingLiquids.isEmpty() || !pendingRemnants.isEmpty()) {
			setErrorState(EnumErrorCode.NOSPACE);
		}
		return false;
	}

	private boolean addProduct(LiquidStack stack) {
		stack.amount -= productTank.fill(stack, true);

		if (stack.amount <= 0)
			return true;
		else
			return false;
	}

	private boolean addRemnant(ItemStack stack) {

		// Add if remnant stack is empty anyway
		if (inventoryStacks[remnantSlot] == null) {
			inventoryStacks[remnantSlot] = stack;
			return true;
		}

		// Don't add if already occupied by different item
		if (!inventoryStacks[remnantSlot].isItemEqual(stack))
			return false;

		int space = inventoryStacks[remnantSlot].getMaxStackSize() - inventoryStacks[remnantSlot].stackSize;
		if (space <= 0)
			return false;

		inventoryStacks[remnantSlot].stackSize += stack.stackSize;
		stack.stackSize -= space;
		return true;
	}

	private void removeResources(ItemStack[] stacks) {
		int[] removed = new int[stacks.length];

		for (int i = resourceSlot1; i < remnantSlot; i++) {
			if (inventoryStacks[i] == null) {
				continue;
			}

			for (int j = 0; j < stacks.length; j++) {
				// Can become null
				if (inventoryStacks[i] == null) {
					break;
				}
				// We have already removed everything
				if (removed[j] >= stacks[j].stackSize) {
					continue;
				}

				if (inventoryStacks[i].isItemEqual(stacks[j])) {
					if (inventoryStacks[i].stackSize >= stacks[j].stackSize) {
						decrStackSize(i, stacks[j].stackSize);
						removed[j] = stacks[j].stackSize;
						continue;
					}

					int avail = inventoryStacks[i].stackSize;
					inventoryStacks[i].stackSize -= stacks[j].stackSize;
					removed[j] += avail;

					if (inventoryStacks[i].stackSize <= 0) {
						inventoryStacks[i] = null;
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean isWorking() {
		return currentRecipe != null && productTank.quantity < productTank.capacity;
	}

	@Override
	public boolean hasWork() {
		return currentRecipe != null && productTank.quantity < productTank.capacity;
	}

	public int getProgressScaled(int i) {
		if (timePerItem == 0)
			return i;

		return (productionTime * i) / timePerItem;
	}

	public int getResourceScaled(int i) {
		return (productTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			productionTime = j;
			break;
		case 1:
			timePerItem = j;
			break;
		case 2:
			productTank.liquidId = j;
			break;
		case 3:
			productTank.quantity = j;
			break;
                case 4:
                        productTank.liquidMeta = j;
                        break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, productionTime);
		iCrafting.sendProgressBarUpdate(container, 1, timePerItem);
		iCrafting.sendProgressBarUpdate(container, 2, productTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 3, productTank.quantity);
                iCrafting.sendProgressBarUpdate(container, 4, productTank.liquidMeta);
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

		ItemStack product;
		if (inventoryStacks[i].stackSize <= j) {
			product = inventoryStacks[i];
			inventoryStacks[i] = null;
			return product;
		} else {
			product = inventoryStacks[i].splitStack(j);
			if (inventoryStacks[i].stackSize == 0) {
				inventoryStacks[i] = null;
			}

			return product;
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
		// BOTTOM
		if (side == 0)
			return canSlot;
		// TOP
		else if (side == 1)
			return remnantSlot;
		// SIDES
		else
			return resourceSlot1;
	}

	@Override
	public int getSizeInventorySide(int side) {
		if (side == 0)
			return 2;
		else if (side == 1)
			return 1;
		else
			return 9;
	}

	// / ISPECIALINVENTORY IMPLEMENTATION
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		// Try to add to can slot if input is from top or bottom.
		if (LiquidHelper.isEmptyContainer(stack)) {

			// Add if empty
			if (inventoryStacks[canSlot] == null) {
				if (doAdd) {
					inventoryStacks[canSlot] = stack.copy();
				}

				return stack.stackSize;
			}
			// Don't add if already occupied by another item
			if (!inventoryStacks[canSlot].isItemEqual(stack))
				return 0;

			int space = inventoryStacks[canSlot].getMaxStackSize() - inventoryStacks[canSlot].stackSize;
			// No space left
			if (space <= 0)
				return 0;

			// Not enough space
			if (space < stack.stackSize) {
				if (doAdd) {
					inventoryStacks[canSlot].stackSize = inventoryStacks[canSlot].getMaxStackSize();
				}

				return space;
				// Ample space
			} else {
				if (doAdd) {
					inventoryStacks[canSlot].stackSize += stack.stackSize;
				}
				return stack.stackSize;
			}
		}

		int freeSlots = 0;
		int slot = -1;
		int used = 0;

		for (int i = 0; i < remnantSlot; i++) {

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

		// Now let's check if there are free slots available since we couldn't
		// add everything to already occupied slots
		if (freeSlots <= 0)
			return used;

		if (doAdd) {
			inventoryStacks[slot] = stack.copy();
			inventoryStacks[slot].stackSize = stack.stackSize - used;
		}
		return stack.stackSize;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product;

		if (inventoryStacks[outputSlot] != null) {

			product = new ItemStack(inventoryStacks[outputSlot].itemID, 1, inventoryStacks[outputSlot].getItemDamage());
			if (doRemove) {
				inventoryStacks[outputSlot].stackSize--;
				if (inventoryStacks[outputSlot].stackSize <= 0) {
					inventoryStacks[outputSlot] = null;
				}
			}
			return new ItemStack[] { product };
		} else {

			if (inventoryStacks[remnantSlot] == null)
				return new ItemStack[0];

			product = new ItemStack(inventoryStacks[remnantSlot].itemID, 1, inventoryStacks[remnantSlot].getItemDamage());
			if (doRemove) {
				inventoryStacks[remnantSlot].stackSize--;
				if (inventoryStacks[remnantSlot].stackSize <= 0) {
					inventoryStacks[remnantSlot] = null;
				}
			}
			return new ItemStack[] { product };
		}

	}

	// / ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public LiquidStack drain(ForgeDirection from, int quantityMax, boolean doEmpty) {
		return drain(0, quantityMax, doEmpty);
	}

	@Override
	public LiquidStack drain(int tankIndex, int quantityMax, boolean doEmpty) {
		return productTank.drain(quantityMax, doEmpty);
	}

	@Override
	public TankSlot[] getTanks(ForgeDirection direction) {
		return new TankSlot[] { productTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return productTank;
	}

	// / ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.hasWork);
		return res;
	}

	// / INITIALIZATION
	public static void initialize() {
	}

}
