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
import java.util.Map;
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
import buildcraft.api.gates.ITrigger;
import forestry.api.core.ForestryAPI;
import forestry.api.recipes.ICentrifugeManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.StringUtil;

public class MachineCentrifuge extends Machine {

	/**
	 * Factory class to produce {@link MachineCentrifuge}s.
	 * 
	 * @author SirSengir
	 * 
	 */
	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineCentrifuge((TileMachine) tile);
		}
	}

	public static class Recipe {
		public final int timePerItem;
		public final ItemStack resource;
		public final HashMap<ItemStack, Integer> products;

		public Recipe(int timePerItem, ItemStack resource, HashMap<ItemStack, Integer> products) {
			this.timePerItem = timePerItem;
			this.resource = resource;
			this.products = products;
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

	public static class RecipeManager implements ICentrifugeManager {
		public static ArrayList<MachineCentrifuge.Recipe> recipes = new ArrayList<MachineCentrifuge.Recipe>();

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, HashMap<ItemStack, Integer> products) {
			recipes.add(new Recipe(timePerItem, resource, products));
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, ItemStack[] produce, int[] chances) {
			HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();

			int i = 0;
			for (ItemStack prod : produce) {
				products.put(prod, chances[i]);
				i++;
			}

			addRecipe(timePerItem, resource, products);
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, ItemStack primary, ItemStack secondary, int chance) {
			HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();
			products.put(primary, 100);
			if (secondary != null) {
				products.put(secondary, chance);
			}
			addRecipe(timePerItem, resource, products);
		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, ItemStack primary) {
			HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();
			products.put(primary, 100);
			addRecipe(timePerItem, resource, products);
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
				recipeList.put(new ItemStack[] { recipe.resource }, recipe.products.keySet().toArray(new ItemStack[0]));
			}

			return (List<Entry<ItemStack[], ItemStack[]>>) recipeList;
		}
	}

	private ItemStack[] inventoryStacks = new ItemStack[10];
	public MachineCentrifuge.Recipe currentRecipe;

	private int resourceSlot = 0;
	private int inventorySlot1 = 1;

	private Stack<ItemStack> pendingProducts = new Stack<ItemStack>();
	private int productionTime;
	private int timePerItem;

	public MachineCentrifuge(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("centrifuge"));
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.8");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.CentrifugeGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	// / LOADING & SAVING
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("ProductionTime", productionTime);
		nbttagcompound.setInteger("TimePerItem", timePerItem);

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

		productionTime = nbttagcompound.getInteger("ProductionTime");
		timePerItem = nbttagcompound.getInteger("TimePerItem");

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

		if (tile.worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		// Check and reset recipe if necessary
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
		for (Map.Entry<ItemStack, Integer> entry : currentRecipe.products.entrySet())
			if (entry.getValue() >= 100) {
				pendingProducts.push(entry.getKey().copy());
			} else if (tile.worldObj.rand.nextInt(100) < entry.getValue()) {
				pendingProducts.push(entry.getKey().copy());
			}

		inventoryStacks[resourceSlot].stackSize--;
		if (inventoryStacks[resourceSlot].stackSize <= 0) {
			inventoryStacks[resourceSlot] = null;
		}
		checkRecipe();
		resetRecipe();

		tryAddPending();
		return true;
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(inventoryStacks[resourceSlot]);

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
		for (int i = inventorySlot1; i < inventoryStacks.length; i++) {

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

	@Override
	public boolean isWorking() {
		return currentRecipe != null;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		if (inventoryStacks[resourceSlot] == null)
			return false;

		return ((float) inventoryStacks[resourceSlot].stackSize / (float) inventoryStacks[resourceSlot].getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		return currentRecipe != null;
	}

	public int getProgressScaled(int i) {
		if (timePerItem == 0)
			return i;

		return (productionTime * i) / timePerItem;
	}

	@Override
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			productionTime = j;
			break;
		case 1:
			timePerItem = j;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, productionTime);
		iCrafting.sendProgressBarUpdate(container, 1, timePerItem);
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

	// / ISIDEDINVENTORY
	@Override
	public int getStartInventorySide(int side) {
		// BOTTOM OR TOP
		if (side == 0 || side == 1)
			return resourceSlot;
		// SIDES
		else
			return inventorySlot1;
	}

	@Override
	public int getSizeInventorySide(int side) {
		if (side == 0 || side == 1)
			return 1;
		else
			return 9;
	}

	// / ISPECIALINVENTORY
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		ItemStack product = null;

		for (int i = inventorySlot1; i < inventoryStacks.length; i++) {
			if (inventoryStacks[i] == null) {
				continue;
			}

			product = new ItemStack(inventoryStacks[i].itemID, 1, inventoryStacks[i].getItemDamage());
			if (doRemove) {
				decrStackSize(i, 1);
			}

			break;
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return new ItemStack[0];
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (inventoryStacks[resourceSlot] == null) {
			if (doAdd) {
				inventoryStacks[resourceSlot] = stack.copy();
			}
			return stack.stackSize;
		}

		if (!inventoryStacks[resourceSlot].isItemEqual(stack))
			return 0;

		int space = inventoryStacks[resourceSlot].getMaxStackSize() - inventoryStacks[resourceSlot].stackSize;
		if (space <= 0)
			return 0;

		if (doAdd)
			if (stack.stackSize <= space) {
				inventoryStacks[resourceSlot].stackSize += stack.stackSize;
			} else {
				inventoryStacks[resourceSlot].stackSize += space;
			}

		return Math.min(stack.stackSize, space);
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
