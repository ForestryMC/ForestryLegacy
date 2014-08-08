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
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import forestry.api.core.ForestryAPI;
import forestry.api.recipes.IFabricatorManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.interfaces.ICrafter;
import forestry.core.interfaces.ICraftingPlan;
import forestry.core.network.GuiId;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import forestry.core.utils.TankSlot;

public class MachineFabricator extends Machine implements ICrafter {

	/**
	 * Factory class to create fabricators
	 */
	public static class Factory extends MachineFactory {

		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineFabricator((TileMachine) tile);
		}

	}

	/* RECIPE MANAGMENT */
	public static class Recipe {
		ItemStack plan;

		LiquidStack molten;
		ShapedRecipeCustom internal;

		public Recipe(ItemStack plan, LiquidStack molten, ShapedRecipeCustom internal) {
			this.plan = plan;
			this.molten = molten;
			this.internal = internal;
		}

		public boolean matches(ItemStack plan) {
			if (this.plan == null)
				return true;

			if (plan == null && this.plan == null)
				return true;
			else if (plan == null && this.plan != null)
				return false;

			if (this.plan.getItemDamage() < 0)
				return plan.itemID == this.plan.itemID;
			else
				return plan.isItemEqual(this.plan);
		}

		public boolean matches(ItemStack plan, ItemStack[][] resources) {
			if (!this.matches(plan))
				return false;

			return internal.matches(resources);
		}

		public boolean hasLiquid(LiquidStack resource) {
			if (!resource.isLiquidEqual(molten))
				return false;

			return molten.amount <= resource.amount;
		}
		
		public LiquidStack getLiquid() {
			return molten;
		}
		
		public IRecipe asIRecipe() {
			return internal;
		}

	}

	public static class Smelting {
		ItemStack resource;
		LiquidStack product;
		int meltingPoint;

		public Smelting(ItemStack resource, LiquidStack molten, int meltingPoint) {
			this.resource = resource;
			this.product = molten;
			this.meltingPoint = meltingPoint;
		}

		public boolean matches(ItemStack resource) {
			return this.resource.isItemEqual(resource);
		}

		public boolean matches(LiquidStack product) {
			return this.product.isLiquidEqual(product);
		}
	}

	public static class RecipeManager implements IFabricatorManager {
		public static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		private static ArrayList<Smelting> smeltings = new ArrayList<Smelting>();

		@Override
		public void addRecipe(ItemStack plan, LiquidStack molten, ItemStack result, Object[] pattern) {
			recipes.add(new Recipe(plan, molten, ShapedRecipeCustom.createShapedRecipe(pattern, result)));
		}

		@Override
		public void addSmelting(ItemStack resource, LiquidStack molten, int meltingPoint) {
			smeltings.add(new Smelting(resource, molten, meltingPoint));
		}

		public static Recipe findMatchingRecipe(ItemStack plan) {
			for (Recipe recipe : recipes)
				if (recipe.matches(plan))
					return recipe;

			return null;
		}

		public static Recipe findMatchingRecipe(ItemStack plan, LiquidStack liquid, ItemStack[] resources) {
			ItemStack[][] gridResources = new ItemStack[3][3];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					gridResources[j][i] = resources[i * 3 + j];
				}
			}

			for (Recipe recipe : recipes)
				if (recipe.matches(plan, gridResources))
					if (recipe.hasLiquid(liquid))
						return recipe;

			return null;
		}

		public static boolean isResourceLiquid(LiquidStack liquid) {
			for (Recipe recipe : recipes)
				if (recipe.hasLiquid(liquid))
					return true;

			return false;
		}

		public static Smelting findMatchingSmelting(ItemStack resource) {
			if (resource == null)
				return null;

			for (Smelting smelting : smeltings)
				if (smelting.matches(resource))
					return smelting;

			return null;
		}

		public static Smelting findMatchingSmelting(LiquidStack product) {
			if (product == null)
				return null;

			for (Smelting smelting : smeltings)
				if (smelting.matches(product))
					return smelting;

			return null;
		}

		@Override
		public List<Entry<ItemStack[], ItemStack[]>> getRecipes() {
			return null;
		}

	}

	/* CONSTANTS */
	private static final int MAX_HEAT = 5000;

	public static final short SLOT_METAL = 0;
	public static final short SLOT_PLAN = 1;
	public static final short SLOT_RESULT = 2;
	public static final short SLOT_CRAFTING_1 = 3;
	public static final short SLOT_INVENTORY_1 = 12;
	public static final short SLOT_INVENTORY_COUNT = 18;

	/* MEMBER */
	private GenericInventoryAdapter inventory = new GenericInventoryAdapter(30, "Items");
	private TankSlot moltenTank = new TankSlot(2 * Defaults.BUCKET_VOLUME);
	private int heat = 0;
	private int guiMeltingPoint = 0;

	private LiquidStack pendingSmelt;

	public MachineFabricator(TileMachine tile) {
		super(tile);
	}

	@Override
	public String getName() {
		return "tile.machine." + Defaults.ID_PACKAGE_MACHINE_FABRICATOR;
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.FabricatorGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Heat", heat);

		// Tank
		NBTTagCompound nbtMoltenTank = new NBTTagCompound();
		moltenTank.writeToNBT(nbtMoltenTank);
		nbttagcompound.setTag("MoltenTank", nbtMoltenTank);

		// Pending Smelt
		if (pendingSmelt != null) {
			NBTTagCompound smelt = new NBTTagCompound();
			pendingSmelt.writeToNBT(smelt);
			nbttagcompound.setTag("PendingSmelt", smelt);
		}

		// / Inventory
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		heat = nbttagcompound.getInteger("Heat");

		// Tank
		moltenTank = new TankSlot(Defaults.BUCKET_VOLUME * 2);
		if (nbttagcompound.hasKey("MoltenTank")) {
			moltenTank.readFromNBT(nbttagcompound.getCompoundTag("MoltenTank"));
		}

		// Pending Smelt
		if (nbttagcompound.hasKey("PendingSmelt")) {
			NBTTagCompound smelt = nbttagcompound.getCompoundTag("PendingSmelt");
			pendingSmelt = LiquidStack.loadLiquidStackFromNBT(smelt);
		}

		// / Inventory
		inventory.readFromNBT(nbttagcompound);

	}

	/* UPDATING */
	@Override
	public void updateServerSide() {

		// Remove smelt if we have gone below metling point
		if (moltenTank.quantity > 0) {
			Smelting smelt = RecipeManager.findMatchingSmelting(moltenTank.asLiquidStack());
			if (smelt != null && heat < smelt.meltingPoint) {
				moltenTank.drain(5, true);
			}
		}

		// Add pending smelt
		if (pendingSmelt != null) {

			int filled = moltenTank.fill(pendingSmelt, true);
			pendingSmelt.amount -= filled;

			if (pendingSmelt.amount <= 0) {
				pendingSmelt = null;
				// Smelt if necessary and possible
			}
		} else if (moltenTank.quantity < moltenTank.capacity) {

			if (inventory.getStackInSlot(SLOT_METAL) != null) {

				Smelting smelt = RecipeManager.findMatchingSmelting(inventory.getStackInSlot(SLOT_METAL));
				if (smelt != null && smelt.meltingPoint <= heat) {

					this.decrStackSize(SLOT_METAL, 1);
					pendingSmelt = smelt.product.copy();
				}
			}
		}

		this.dissipateHeat();
	}

	@Override
	public boolean doWork() {
		return addHeat(25);
	}

	private boolean addHeat(int addition) {
		if (this.heat >= MAX_HEAT)
			return false;

		this.heat += addition;
		if (this.heat > MAX_HEAT) {
			this.heat = MAX_HEAT;
		}

		return true;
	}

	private void dissipateHeat() {
		if (heat > 2500) {
			this.heat -= 2;
		} else if (heat > 0) {
			this.heat--;
		}
	}

	public Object[] getPlan() {
		if (inventory.getStackInSlot(SLOT_PLAN) == null)
			return null;

		Recipe myRecipe = RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_PLAN));
		if (myRecipe == null)
			return null;

		return myRecipe.internal.getIngredients();
	}

	/* ICRAFTER */
	@Override
	public ItemStack getResult() {
		Recipe myRecipe = RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_PLAN), moltenTank.asLiquidStack(),
				inventory.getStacks(SLOT_CRAFTING_1, 9));
		if (myRecipe == null)
			return null;

		return myRecipe.internal.getRecipeOutput().copy();
	}

	@Override
	public ItemStack takeResult(boolean consumeRecipe) {
		Recipe myRecipe = RecipeManager.findMatchingRecipe(inventory.getStackInSlot(SLOT_PLAN), moltenTank.asLiquidStack(),
				inventory.getStacks(SLOT_CRAFTING_1, 9));
		if (myRecipe == null)
			return null;

		LiquidStack liquid = myRecipe.molten;

		// Remove resources
		if (removeFromInventory(1, inventory.getStacks(SLOT_CRAFTING_1, 9), false)) {
			removeFromInventory(1, inventory.getStacks(SLOT_CRAFTING_1, 9), true);
			moltenTank.drain(liquid.amount, true);
		} else if (consumeRecipe) {
			removeFromCraftMatrix(myRecipe);
			moltenTank.drain(liquid.amount, true);
		} else
			return null;

		ItemStack result = myRecipe.internal.getRecipeOutput().copy();
		// Damage plan
		if (inventory.getStackInSlot(SLOT_PLAN) != null) {
			Item planItem = inventory.getStackInSlot(SLOT_PLAN).getItem();
			if (planItem instanceof ICraftingPlan) {
				inventory.setInventorySlotContents(SLOT_PLAN, ((ICraftingPlan) planItem).planUsed(inventory.getStackInSlot(SLOT_PLAN), result));
			}
		}

		// Return result
		return result;
	}

	private void removeFromCraftMatrix(Recipe recipe) {

		/*
		 * Object[] ingredients = recipe.internal.getIngredients(); System.out.println("ingredients.length: " + ingredients.length); for (int i = 0; i <
		 * ingredients.length; i++) { if (ingredients[i] != null) { int decrease = 0; if(ingredients[i] instanceof ItemStack) decrease =
		 * ((ItemStack)ingredients[i]).stackSize; else decrease = ((ItemStack)((ArrayList)ingredients[i]).get(0)).stackSize;
		 * inventory.decrStackSize(SLOT_CRAFTING_1 + i, decrease); } }
		 */
		for (int i = 0; i < 9; i++) {
			if (inventory.getStackInSlot(SLOT_CRAFTING_1 + i) == null) {
				continue;
			}
			inventory.decrStackSize(SLOT_CRAFTING_1 + i, 1);
		}

	}

	private boolean removeFromInventory(int count, ItemStack[] set, boolean doRemove) {

		boolean hasRemoved = true;
		for (int i = 0; i < count; i++) {
			ItemStack[] condensedSet = StackUtils.condenseStacks(set);
			for (ItemStack req : condensedSet) {

				for (int j = SLOT_INVENTORY_1; j < SLOT_INVENTORY_1 + SLOT_INVENTORY_COUNT; j++) {
					ItemStack pol = inventory.getStackInSlot(j);
					if (pol == null) {
						continue;
					}
					if (!StackUtils.isItemOreEqual(pol, req)) {
						continue;
					}

					if (pol.stackSize > req.stackSize) {
						if (doRemove) {
							inventory.decrStackSize(j, req.stackSize);
						}
						req.stackSize = 0;
					} else {
						if (doRemove) {
							inventory.decrStackSize(j, req.stackSize);
						}
						req.stackSize -= pol.stackSize;
					}
				}
			}
			boolean hasLeft = false;
			for (ItemStack req : condensedSet) {
				if (req != null && req.stackSize > 0) {
					hasLeft = true;
				}
			}
			if (hasLeft) {
				hasRemoved = false;
			}

		}

		return hasRemoved;
	}

	@Override
	public boolean isWorking() {
		return this.heat <= MAX_HEAT;
	}

	public int getHeatScaled(int i) {
		return (heat * i) / MAX_HEAT;
	}

	public int getMeltingPoint() {
		if (moltenTank.quantity > 0) {
			Smelting smelt = RecipeManager.findMatchingSmelting(moltenTank.asLiquidStack());
			if (smelt != null)
				return smelt.meltingPoint;
		} else if (this.getStackInSlot(SLOT_METAL) != null) {
			Smelting smelt = RecipeManager.findMatchingSmelting(this.getStackInSlot(SLOT_METAL));
			if (smelt != null)
				return smelt.meltingPoint;
		}

		return 0;
	}

	public int getMeltingPointScaled(int i) {
		// / For SMP clients
		if (guiMeltingPoint > 0)
			return (guiMeltingPoint * i) / MAX_HEAT;

		int meltingPoint = getMeltingPoint();

		if (meltingPoint <= 0)
			return 0;
		else
			return (meltingPoint * i) / MAX_HEAT;
	}

	/* SMP */
	@Override
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			moltenTank.liquidId = j;
			break;
		case 1:
			moltenTank.quantity = j;
			break;
		case 2:
			heat = j;
			break;
		case 3:
			guiMeltingPoint = j;
			break;
		case 4:
			moltenTank.liquidMeta = j;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, moltenTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 1, moltenTank.quantity);
		iCrafting.sendProgressBarUpdate(container, 2, heat);
		iCrafting.sendProgressBarUpdate(container, 3, getMeltingPoint());
		iCrafting.sendProgressBarUpdate(container, 4, moltenTank.liquidMeta);
	}

	// / ISPECIALINVENTORY
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (from == ForgeDirection.UP && RecipeManager.findMatchingSmelting(stack) != null)
			return inventory.addStack(stack, SLOT_METAL, 1, false, doAdd);
		return inventory.addStack(stack, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, false, doAdd);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		ItemStack taken;
		if (doRemove) {
			taken = this.takeResult(false);
		} else {
			taken = this.getResult();
		}

		if (taken != null)
			return new ItemStack[] { taken };
		else
			return new ItemStack[0];
	}

	// / IINVENTORY
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
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	// / ILIQUIDCONTAINER
	@Override
	public TankSlot[] getTanks(ForgeDirection direction) {
		return new TankSlot[] { moltenTank };
	}

	public static void initialize() {
		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Block.glass), new LiquidStack(ForestryItem.liquidGlass, 1000), 1000);
		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Block.thinGlass), new LiquidStack(ForestryItem.liquidGlass, 375), 1000);
		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Block.sand), new LiquidStack(ForestryItem.liquidGlass, 1000), 3000);
	}
}
