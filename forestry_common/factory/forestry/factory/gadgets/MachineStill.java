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
import forestry.api.recipes.IStillManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.EnumErrorCode;
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
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.core.utils.Utils;

public class MachineStill extends Machine {

	// / CONSTANTS
	public static final short SLOT_OUTPUT = 0;
	public static final short SLOT_CAN = 1;
	public static final short SLOT_INPUT = 2;

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineStill((TileMachine) tile);
		}
	}

	public static class Recipe {
		public final int timePerUnit;
		public final LiquidStack input;
		public final LiquidStack output;

		public Recipe(int timePerUnit, LiquidStack input, LiquidStack output) {
			this.timePerUnit = timePerUnit;
			this.input = input;
			this.output = output;
		}

		public boolean matches(LiquidStack res) {
			if (res == null && input == null)
				return true;
			else if (res == null && input != null)
				return false;
			else if (res != null && input == null)
				return false;
			else
				return input.isLiquidEqual(res);
		}
	}

	public static class RecipeManager implements IStillManager {
		public static ArrayList<MachineStill.Recipe> recipes = new ArrayList<MachineStill.Recipe>();

		@Override
		public void addRecipe(int timePerUnit, LiquidStack input, LiquidStack output) {
			recipes.add(new MachineStill.Recipe(timePerUnit, input, output));
		}

		public static Recipe findMatchingRecipe(LiquidStack item) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(item))
					return recipe;
			}
			return null;
		}

		public static boolean isInput(LiquidStack res) {
			return findMatchingRecipe(res) != null;
		}

		@Override
		public List<Entry<ItemStack[], ItemStack[]>> getRecipes() {
			HashMap<ItemStack[], ItemStack[]> recipeList = new HashMap<ItemStack[], ItemStack[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(new ItemStack[] { recipe.input.asItemStack() }, new ItemStack[] { recipe.output.asItemStack() });
			}

			return (List<Entry<ItemStack[], ItemStack[]>>) recipeList;
		}
	}

	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	@EntityNetData
	public TankSlot productTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);

	private ItemStack[] inventoryStacks = new ItemStack[3];

	private Recipe currentRecipe;
	private LiquidStack bufferedLiquid;
	public int distillationTime = 0;
	public int distillationTotalTime = 0;

	public MachineStill(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("still"));
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.1");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.StillGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("DistillationTime", distillationTime);
		nbttagcompound.setInteger("DistillationTotalTime", distillationTotalTime);

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

		distillationTime = nbttagcompound.getInteger("DistillationTime");
		distillationTotalTime = nbttagcompound.getInteger("DistillationTotalTime");

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

		checkRecipe();
	}

	@Override
	public void updateServerSide() {

		// Check if we have suitable items waiting in the item slot
		if (inventoryStacks[SLOT_INPUT] != null) {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(inventoryStacks[SLOT_INPUT]);

			if (container != null && RecipeManager.isInput(container.stillLiquid)) {

				inventoryStacks[SLOT_INPUT] = StackUtils.replenishByContainer(tile, inventoryStacks[SLOT_INPUT], container, resourceTank);
				if (inventoryStacks[SLOT_INPUT].stackSize <= 0) {
					inventoryStacks[SLOT_INPUT] = null;
				}

			}
		}

		// Can product liquid if possible
		if (inventoryStacks[SLOT_CAN] != null) {
			LiquidContainerData container = LiquidHelper.getEmptyContainer(inventoryStacks[SLOT_CAN], new LiquidStack(productTank.liquidId,
					productTank.quantity, productTank.liquidMeta));
			if (container != null) {
				inventoryStacks[SLOT_OUTPUT] = bottleIntoContainer(inventoryStacks[SLOT_CAN], inventoryStacks[SLOT_OUTPUT], container, productTank);
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

		// Ongoing process
		if (distillationTime > 0 && currentRecipe != null) {

			distillationTime -= currentRecipe.input.amount;
			addProduct(currentRecipe.output.itemID, currentRecipe.output.amount);

			setErrorState(EnumErrorCode.OK);
			return true;

		} else if (currentRecipe != null && productTank.quantity + currentRecipe.output.amount <= Defaults.PROCESSOR_TANK_CAPACITY) {

			int resReq = currentRecipe.timePerUnit * currentRecipe.input.amount;
			// Start next cycle if enough bio mass is available
			if (resourceTank.quantity >= resReq) {

				distillationTime = distillationTotalTime = resReq;
				resourceTank.drain(resReq, true);
				bufferedLiquid = new LiquidStack(currentRecipe.input.itemID, resReq, currentRecipe.input.itemMeta);

				setErrorState(EnumErrorCode.OK);
				return true;

			} else {
				setErrorState(EnumErrorCode.NORESOURCE);
			}

		}

		bufferedLiquid = null;
		return false;
	}

	private void addProduct(int id, int amount) {

		productTank.fill(new LiquidStack(id, amount), true);
		if (productTank.quantity > Defaults.PROCESSOR_TANK_CAPACITY) {
			productTank.quantity = Defaults.PROCESSOR_TANK_CAPACITY;
		}
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(new LiquidStack(resourceTank.liquidId, resourceTank.quantity));

		if (sameRec == null && bufferedLiquid != null && distillationTime > 0) {
			sameRec = RecipeManager.findMatchingRecipe(new LiquidStack(bufferedLiquid.itemID, distillationTime, bufferedLiquid.itemMeta));
		}

		if (sameRec == null) {
			setErrorState(EnumErrorCode.NORECIPE);
		}

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
	}

	@Override
	public boolean isWorking() {
		return distillationTime > 0 || currentRecipe != null && productTank.quantity + currentRecipe.output.amount <= Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null)
			return false;

		return (distillationTime > 0 || resourceTank.quantity >= currentRecipe.timePerUnit * currentRecipe.input.amount)
				&& productTank.quantity <= productTank.capacity - currentRecipe.output.amount;
	}

	public int getDistillationProgressScaled(int i) {
		if (distillationTotalTime == 0)
			return i;

		return (distillationTime * i) / distillationTotalTime;
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

	// / SMP GUI

	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			distillationTime = j;
			break;
		case 1:
			distillationTotalTime = j;
			break;
		case 2:
			resourceTank.liquidId = j;
			break;
		case 3:
			resourceTank.quantity = j;
			break;
		case 4:
			productTank.liquidId = j;
			break;
		case 5:
			productTank.quantity = j;
			break;
                case 6:
                        productTank.liquidMeta = j;
                        break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, distillationTime);
		iCrafting.sendProgressBarUpdate(container, 1, distillationTotalTime);
		iCrafting.sendProgressBarUpdate(container, 2, resourceTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 3, resourceTank.quantity);
		iCrafting.sendProgressBarUpdate(container, 4, productTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 5, productTank.quantity);
                iCrafting.sendProgressBarUpdate(container, 6, productTank.liquidMeta);
	}

	// / ISPECIALINVENTORY IMPLEMENTATION
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		int inventory;

		LiquidContainerData container = LiquidHelper.getLiquidContainer(stack);
		if (container != null && RecipeManager.isInput(container.stillLiquid)) {
			inventory = SLOT_INPUT;
		} else if (LiquidHelper.isEmptyContainer(stack)) {
			inventory = SLOT_CAN;
		} else
			return 0;

		if (inventoryStacks[inventory] == null) {
			if (doAdd) {
				inventoryStacks[inventory] = stack.copy();
			}
			return stack.stackSize;
		}

		if (!inventoryStacks[inventory].isItemEqual(stack))
			return 0;

		int space = inventoryStacks[inventory].getMaxStackSize() - inventoryStacks[inventory].stackSize;
		if (space <= 0)
			return 0;

		if (doAdd) {
			inventoryStacks[inventory].stackSize += stack.stackSize;
		}

		return Math.min(space, stack.stackSize);
	}

	/**
	 * Transport pipes cannot extract items from the still.
	 */
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack product = null;

		if (inventoryStacks[SLOT_OUTPUT] != null) {

			product = new ItemStack(inventoryStacks[SLOT_OUTPUT].itemID, 1, inventoryStacks[SLOT_OUTPUT].getItemDamage());
			if (doRemove) {
				inventoryStacks[SLOT_OUTPUT].stackSize--;
				if (inventoryStacks[SLOT_OUTPUT].stackSize <= 0) {
					inventoryStacks[SLOT_OUTPUT] = null;
				}
			}
		}

		if (product != null)
			return new ItemStack[] { product };
		else
			return new ItemStack[0];
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
		if (itemstack != null && itemstack.stackSize > tile.getInventoryStackLimit()) {
			itemstack.stackSize = tile.getInventoryStackLimit();
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
		case 1:
			return SLOT_OUTPUT;
		case 2:
		case 3:
			return SLOT_INPUT;
		default:
			return SLOT_CAN;
		}
	}

	@Override
	public int getSizeInventorySide(int side) {
		return 1;
	}

	// / ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {

		// We only accept what is already in the tank or valid ingredients
		if (resourceTank.quantity > 0 && resourceTank.liquidId != resource.itemID)
			return 0;
		else if (!MachineStill.RecipeManager.isInput(resource))
			return 0;

		int used = resourceTank.fill(resource, doFill);

		if (doFill && used > 0) {
			tile.sendNetworkUpdate();
		}

		return used;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int quantityMax, boolean doEmpty) {
		return drain(1, quantityMax, doEmpty);
	}

	@Override
	public LiquidStack drain(int tankIndex, int quantityMax, boolean doEmpty) {
		if (tankIndex != 1)
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
		res.add(ForestryTrigger.hasWork);
		return res;
	}

	public static void initialize() {
		RecipeManagers.stillManager.addRecipe(Defaults.STILL_DESTILLATION_DURATION, new LiquidStack(ForestryItem.liquidBiomass,
				Defaults.STILL_DESTILLATION_INPUT), new LiquidStack(ForestryItem.liquidBiofuel, Defaults.STILL_DESTILLATION_OUTPUT));
	}

}
