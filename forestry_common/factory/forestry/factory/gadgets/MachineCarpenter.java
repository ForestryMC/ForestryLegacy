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
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.api.gates.ITrigger;
import forestry.api.core.ForestryAPI;
import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.network.EntityNetData;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Utils;
import forestry.factory.gui.ContainerCarpenter;

public class MachineCarpenter extends Machine {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MachineCarpenter((TileMachine) tile);
		}
	}

	// / RECIPE MANAGMENT
	public static class Recipe {
		private int packagingTime;
		private LiquidStack liquid;
		private ItemStack box;
		private ShapedRecipeCustom internal;

		public Recipe(int packagingTime, LiquidStack liquid, ItemStack box, ShapedRecipeCustom internal) {
			this.packagingTime = packagingTime;
			this.liquid = liquid;
			this.box = box;
			this.internal = internal;
		}

		public ItemStack getCraftingResult() {
			return internal.getRecipeOutput();
		}

		public boolean matches(LiquidStack resource, ItemStack item, InventoryCrafting inventorycrafting, World world) {

			// Check liquid
			if (liquid != null && resource == null)
				return false;
			if (liquid != null && !liquid.isLiquidEqual(resource))
				return false;

			// Check box
			if (box != null && item == null)
				return false;
			if (box != null && !box.isItemEqual(item))
				return false;

			return internal.matches(inventorycrafting, world);
		}

		public boolean hasLiquid(LiquidStack resource) {
			if (liquid != null && resource != null)
				return liquid.isLiquidEqual(resource);
			else
				return false;
		}

		public boolean hasBox(ItemStack resource) {
			if (box == null && resource == null)
				return true;
			else if (box == null)
				return true;

			if (box.getItemDamage() > 0)
				return box.isItemEqual(resource);
			else
				return box.itemID == resource.itemID;
		}

		public boolean isIngredient(ItemStack resource) {
			return internal.isIngredient(resource);
		}
		
		public LiquidStack getLiquid() {
			return liquid;
		}
		
		public IRecipe asIRecipe() {
			return internal;
		}
	}

	public static class RecipeManager implements ICarpenterManager {
		public static ArrayList<MachineCarpenter.Recipe> recipes = new ArrayList<MachineCarpenter.Recipe>();

		@Override
		public void addCrating(ItemStack itemStack) {
			ItemStack uncrated = ((forestry.core.items.ItemCrated) itemStack.getItem()).getContained(itemStack);
			addRecipe(Defaults.CARPENTER_CRATING_CYCLES, new LiquidStack(Block.waterStill.blockID, Defaults.CARPENTER_CRATING_LIQUID_QUANTITY), new ItemStack(
					ForestryItem.crate), itemStack, new Object[] { "###", "###", "###", Character.valueOf('#'), uncrated });
			addRecipe(null, new ItemStack(uncrated.itemID, 9, uncrated.getItemDamage()), new Object[] { "#", Character.valueOf('#'), itemStack });
		}

		@Override
		public void addCrating(String toCrate, ItemStack unpack, ItemStack crated) {
			addRecipe(Defaults.CARPENTER_CRATING_CYCLES, new LiquidStack(Block.waterStill.blockID, Defaults.CARPENTER_CRATING_LIQUID_QUANTITY), new ItemStack(
					ForestryItem.crate), crated, new Object[] { "###", "###", "###", Character.valueOf('#'), toCrate });
			addRecipe(null, new ItemStack(unpack.itemID, 9, unpack.getItemDamage()), new Object[] { "#", Character.valueOf('#'), crated });
		}

		@Override
		public void addRecipe(ItemStack box, ItemStack product, Object materials[]) {
			addRecipe(5, null, box, product, materials);
		}

		@Override
		public void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object materials[]) {
			addRecipe(packagingTime, null, box, product, materials);
		}

		@Override
		public void addRecipe(int packagingTime, LiquidStack liquid, ItemStack box, ItemStack product, Object materials[]) {
			recipes.add(new Recipe(packagingTime, liquid, box, ShapedRecipeCustom.createShapedRecipe(materials, product)));
		}

		public static Recipe findMatchingRecipe(LiquidStack liquid, ItemStack item, InventoryCrafting inventorycrafting, World world) {
			for (int i = 0; i < recipes.size(); i++) {
				Recipe recipe = recipes.get(i);
				if (recipe.matches(liquid, item, inventorycrafting, world))
					return recipe;
			}
			return null;
		}

		public static boolean isResourceLiquid(LiquidStack liquid) {
			for (Recipe recipe : recipes)
				if (recipe.hasLiquid(liquid))
					return true;

			return false;
		}

		public static boolean isBox(ItemStack resource) {
			for (Recipe recipe : recipes)
				if (recipe.hasBox(resource))
					return true;

			return false;
		}

		@Override
		public List<Entry<ItemStack[], ItemStack[]>> getRecipes() {
			/*
			HashMap<ItemStack[], ItemStack[]> recipeList = new HashMap<ItemStack[], ItemStack[]>();

			for (Recipe recipe : recipes) recipeList.put(recipe.internal.getIngredients(), new ItemStack[] { recipe.getCraftingResult() });

			return (List<Entry<ItemStack[], ItemStack[]>>) recipeList;
			*/
			return null;
		}

	}

	/* CONSTANTS */
	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_BOX = 9;
	public final static int SLOT_PRODUCT = 10;
	public final static int SLOT_CAN_INPUT = 11;
	public final static short SLOT_INVENTORY_1 = 12;
	public final static short SLOT_INVENTORY_COUNT = 18;

	@EntityNetData
	public TankSlot resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	private TileInventoryAdapter inventory;
	public MachineCarpenter.Recipe currentRecipe;

	public ContainerCarpenter activeContainer;
	private int packageTime;
	private int totalTime;
	private ItemStack currentProduct;
	private ItemStack pendingProduct;

	public ItemStack getBoxStack() {
		return inventory.getStackInSlot(SLOT_BOX);
	}

	public MachineCarpenter(TileMachine tile) {
		super(tile);
		setHints(Config.hints.get("carpenter"));
		this.inventory = new TileInventoryAdapter(tile, 30, "Items");
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.machine.5");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.CarpenterGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("PackageTime", packageTime);
		nbttagcompound.setInteger("PackageTotalTime", totalTime);

		NBTTagCompound NBTresourceSlot = new NBTTagCompound();

		resourceTank.writeToNBT(NBTresourceSlot);
		nbttagcompound.setTag("ResourceTank", NBTresourceSlot);

		inventory.writeToNBT(nbttagcompound);

		// Write pending product
		if (pendingProduct != null) {
			NBTTagCompound nbttagcompoundP = new NBTTagCompound();
			pendingProduct.writeToNBT(nbttagcompoundP);
			nbttagcompound.setTag("PendingProduct", nbttagcompoundP);
		}
		// Write current product
		if (currentProduct != null) {
			NBTTagCompound nbttagcompoundC = new NBTTagCompound();
			currentProduct.writeToNBT(nbttagcompoundC);
			nbttagcompound.setTag("CurrentProduct", nbttagcompoundC);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		packageTime = nbttagcompound.getInteger("PackageTime");
		totalTime = nbttagcompound.getInteger("PackageTotalTime");

		resourceTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
		if (nbttagcompound.hasKey("ResourceTank")) {
			resourceTank.readFromNBT(nbttagcompound.getCompoundTag("ResourceTank"));
		}

		inventory.readFromNBT(nbttagcompound);

		// Load pending product
		if (nbttagcompound.hasKey("PendingProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("PendingProduct");
			pendingProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}
		// Load current product
		if (nbttagcompound.hasKey("CurrentProduct")) {
			NBTTagCompound nbttagcompoundP = nbttagcompound.getCompoundTag("CurrentProduct");
			currentProduct = ItemStack.loadItemStackFromNBT(nbttagcompoundP);
		}

		// Reset recipe according to contents
		ContainerCarpenter container = new ContainerCarpenter((TileMachine) tile);
		currentRecipe = RecipeManager.findMatchingRecipe(resourceTank.asLiquidStack(), getBoxStack(), container.craftMatrix, tile.worldObj);
	}

	@Override
	public void updateServerSide() {

		if (tile.worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN_INPUT) != null) {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_CAN_INPUT));
			if (container != null && RecipeManager.isResourceLiquid(container.stillLiquid)) {

				inventory.setInventorySlotContents(SLOT_CAN_INPUT, StackUtils.replenishByContainer(tile, inventory.getStackInSlot(SLOT_CAN_INPUT), container, resourceTank));
				if (inventory.getStackInSlot(SLOT_CAN_INPUT).stackSize <= 0) {
					inventory.setInventorySlotContents(SLOT_CAN_INPUT, null);
				}
			}
		}

		if (tile.worldObj.getWorldTime() % 40 * 10 != 0)
			return;

		if (currentRecipe == null) {
			ContainerCarpenter container = new ContainerCarpenter((TileMachine) tile);
			currentRecipe = MachineCarpenter.RecipeManager
					.findMatchingRecipe(resourceTank.asLiquidStack(), getBoxStack(), container.craftMatrix, tile.worldObj);
		}

		if (currentRecipe == null) {
			setErrorState(EnumErrorCode.NORECIPE);
		} else if (!validateResources()) {
			setErrorState(EnumErrorCode.NORESOURCE);
		} else {
			setErrorState(EnumErrorCode.OK);
		}

	}

	@Override
	public boolean doWork() {

		if (packageTime > 0) {
			packageTime--;

			// Check whether we have become invalid and need to abort production
			if (currentRecipe == null || !currentProduct.isItemEqual(currentRecipe.getCraftingResult()) || !validateResources()) {
				currentProduct = null;
				packageTime = totalTime = 0;
				return false;
			}

			if (packageTime <= 0) {

				pendingProduct = currentProduct;
				currentProduct = null;
				totalTime = 0;

				// Remove resources
				removeResources(currentRecipe);

				// Update product display
				if (activeContainer != null) {
					activeContainer.updateProductDisplay();
				}

				return tryAddPending();
			}
			return true;
		} else if (pendingProduct != null)
			return tryAddPending();
		else {

			if (currentRecipe != null) {

				if (!validateResources())
					return false;

				// Enough items available, start the process
				packageTime = totalTime = currentRecipe.packagingTime;
				currentProduct = currentRecipe.getCraftingResult();

				// Update product display
				if (activeContainer != null) {
					activeContainer.updateProductDisplay();
				}

				return true;
			}

			return false;
		}
	}

	private boolean validateResources() {
		// Check whether liquid is needed and if there is enough available
		if (currentRecipe.liquid != null)
			if (resourceTank.quantity < currentRecipe.liquid.amount)
				return false;

		// Check whether boxes are available
		if (currentRecipe.box != null)
			if (inventory.getStackInSlot(SLOT_BOX) == null)
				return false;

		// Need at least one matched set
		return StackUtils.containsSets(inventory.getStacks(SLOT_CRAFTING_1, 9), inventory.getStacks(SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT), true) > 0;
	}

	private void removeResources(Recipe recipe) {

		// Remove resources
		if (recipe.liquid != null) {
			resourceTank.quantity -= recipe.liquid.amount;
		}
		// Remove boxes
		if (recipe.box != null) {
			inventory.decrStackSize(SLOT_BOX, 1);
		}

		removeSets(1, inventory.getStacks(SLOT_CRAFTING_1, 9));

	}

	private void removeSets(int count, ItemStack[] set) {

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

					ItemStack removed = inventory.decrStackSize(j, req.stackSize);
					req.stackSize -= removed.stackSize;
				}
			}
		}

	}

	private boolean tryAddPending() {
		if (inventory.getStackInSlot(SLOT_PRODUCT) == null) {
			inventory.setInventorySlotContents(SLOT_PRODUCT, pendingProduct.copy());
			pendingProduct = null;
			return true;
		}

		if (inventory.getStackInSlot(SLOT_PRODUCT).isItemEqual(pendingProduct)
				&& inventory.getStackInSlot(SLOT_PRODUCT).stackSize <= inventory.getStackInSlot(SLOT_PRODUCT).getMaxStackSize() - pendingProduct.stackSize) {
			inventory.getStackInSlot(SLOT_PRODUCT).stackSize += pendingProduct.stackSize;
			pendingProduct = null;
			return true;
		}

		setErrorState(EnumErrorCode.NOSPACE);
		return false;
	}

	/* STATE INFORMATION */
	@Override
	public boolean isWorking() {
		return packageTime > 0 || pendingProduct != null || currentRecipe != null && validateResources();
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null)
			return false;

		// Stop working if the output slot cannot take more
		if (inventory.getStackInSlot(SLOT_PRODUCT) != null
				&& inventory.getStackInSlot(SLOT_PRODUCT).getMaxStackSize() - inventory.getStackInSlot(SLOT_PRODUCT).stackSize < currentRecipe
						.getCraftingResult().stackSize)
			return false;

		return validateResources();
	}

	public int getCraftingProgressScaled(int i) {
		if (totalTime == 0)
			return 0;

		return (packageTime * i) / totalTime;

	}

	public int getResourceScaled(int i) {
		return (resourceTank.quantity * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	public GenericInventoryAdapter getInternalInventory() {
		return this.inventory;
	}

	// IINVENTORY
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory() - 9;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i + 9);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i + 9, itemstack);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i + 9, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	// ISIDEDINVENTORY
	@Override
	public int getStartInventorySide(int side) {
		// BOTTOM
		if (side == 0)
			return SLOT_PRODUCT - 9;
		// TOP
		else if (side == 1)
			return SLOT_BOX - 9;
		// SIDES
		else
			return SLOT_INVENTORY_1 - 9;
	}

	@Override
	public int getSizeInventorySide(int side) {
		if (side == 0)
			return 1;
		else if (side == 1)
			return 1;
		else
			return SLOT_INVENTORY_COUNT;
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			packageTime = j;
			break;
		case 1:
			totalTime = j;
			break;
		case 2:
			resourceTank.liquidId = j;
			break;
		case 3:
			resourceTank.quantity = j;
			break;
                case 4: resourceTank.liquidMeta = j;
                        break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, packageTime);
		iCrafting.sendProgressBarUpdate(container, 1, totalTime);
		iCrafting.sendProgressBarUpdate(container, 2, resourceTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 3, resourceTank.quantity);
                iCrafting.sendProgressBarUpdate(container, 4, resourceTank.liquidMeta);
	}

	// / ISPECIALINVENTORY IMPLEMENTATION
	/**
	 * Stacks from top to bottom go into the box slot. Stacks from the sides into the crafting matrix.
	 */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		// / Liquid container
		LiquidContainerData container = LiquidHelper.getLiquidContainer(stack);
		if (container != null && RecipeManager.isResourceLiquid(container.stillLiquid))
			return inventory.addStack(stack, SLOT_CAN_INPUT, 1, false, doAdd);

		// / We only try to add other resources if we have a recipe set
		if (currentRecipe != null) {
			// Store ingredients
			if (currentRecipe.isIngredient(stack))
				return inventory.addStack(stack, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, false, doAdd);
		}

		// / Store boxes
		if (RecipeManager.isBox(stack))
			return inventory.addStack(stack, SLOT_BOX, 1, false, doAdd);

		return 0;

	}

	/**
	 * Transport pipes can only extract from the carpenter's product stack.
	 */
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		if (inventory.getStackInSlot(SLOT_PRODUCT) == null)
			return new ItemStack[0];

		if (inventory.getStackInSlot(SLOT_PRODUCT).stackSize <= 0)
			return new ItemStack[0];

		ItemStack product = new ItemStack(inventory.getStackInSlot(SLOT_PRODUCT).itemID, 1, inventory.getStackInSlot(SLOT_PRODUCT).getItemDamage());
		if (doRemove) {
			inventory.decrStackSize(SLOT_PRODUCT, 1);
		}

		return new ItemStack[] { product };
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

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.hasWork);
		return res;
	}

	// / INITIALIZATION
	public static void initialize() {

		// RESOURCES
		RecipeManagers.carpenterManager.addRecipe(10, new LiquidStack(ForestryItem.liquidSeedOil, 100), null, new ItemStack(ForestryItem.stickImpregnated, 2),
				new Object[] { "#", "#", Character.valueOf('#'), "logWood" });
		RecipeManagers.carpenterManager.addRecipe(5, new LiquidStack(Block.waterStill.blockID, 250), null, new ItemStack(ForestryItem.woodPulp, 4),
				new Object[] { "#", Character.valueOf('#'), "logWood" });
		RecipeManagers.carpenterManager.addRecipe(5, new LiquidStack(Block.waterStill.blockID, 250), null, new ItemStack(Item.paper, 1), new Object[] { "#",
				"#", Character.valueOf('#'), "pulpWood" });
		RecipeManagers.carpenterManager.addRecipe(5, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryBlock.soil, 9, 0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.dirt, Character.valueOf('X'), ForestryItem.mulch });
		// ForestryCore.oreHandler.registerCarpenterRecipe(humus);
		RecipeManagers.carpenterManager.addRecipe(5, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryBlock.soil, 8, 1),
				new Object[] { "#X#", "XYX", "#X#", Character.valueOf('#'), Block.dirt, Character.valueOf('X'), Block.sand, Character.valueOf('Y'),
						ForestryItem.mulch });
		// ForestryCore.oreHandler.registerCarpenterRecipe(bogEarth);
		RecipeManagers.carpenterManager.addRecipe(75, new LiquidStack(Block.waterStill.blockID, 5000), null, new ItemStack(ForestryItem.hardenedCasing),
				new Object[] { "# #", " Y ", "# #", Character.valueOf('#'), Item.diamond, Character.valueOf('Y'), ForestryItem.sturdyCasing });
		// ForestryCore.oreHandler.registerCarpenterRecipe(hardenedMachine);

		// / CHIPSETS
		RecipeManagers.carpenterManager.addRecipe(20, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.circuitboards, 1, 0),
				new Object[] { "R R", "R#R", "R R", Character.valueOf('#'), "ingotTin", Character.valueOf('R'), Item.redstone });
		RecipeManagers.carpenterManager.addRecipe(40, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.circuitboards, 1, 1),
				new Object[] { "R#R", "R#R", "R#R", Character.valueOf('#'), "ingotBronze", Character.valueOf('R'), Item.redstone });
		RecipeManagers.carpenterManager.addRecipe(80, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.circuitboards, 1, 2),
				new Object[] { "R#R", "R#R", "R#R", Character.valueOf('#'), Item.ingotIron, Character.valueOf('R'), Item.redstone });
		RecipeManagers.carpenterManager.addRecipe(80, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.circuitboards, 1, 3),
				new Object[] { "R#R", "R#R", "R#R", Character.valueOf('#'), Item.ingotGold, Character.valueOf('R'), Item.redstone });
		RecipeManagers.carpenterManager.addRecipe(40, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.solderingIron),
				new Object[] { " # ", "# #", "  B", Character.valueOf('#'), Item.ingotIron, Character.valueOf('B'), "ingotBronze" });
		// ForestryCore.oreHandler.registerCarpenterRecipe(solderingIron);

		// RAIN SUBSTRATES
		RecipeManagers.carpenterManager.addRecipe(5, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.iodineCharge, 1),
				new Object[] { "Z#Z", "#Y#", "X#X", Character.valueOf('#'), ForestryItem.pollen, Character.valueOf('X'), Item.gunpowder,
						Character.valueOf('Y'), ForestryItem.canEmpty, Character.valueOf('Z'), ForestryItem.honeyDrop });
		// ForestryCore.oreHandler.registerCarpenterRecipe(iodineCapsule);
		RecipeManagers.carpenterManager.addRecipe(
				5,
				new LiquidStack(Block.waterStill.blockID, 1000),
				null,
				new ItemStack(ForestryItem.craftingMaterial, 1, 4),
				new Object[] { "Z#Z", "#Y#", "X#X", Character.valueOf('#'), ForestryItem.royalJelly, Character.valueOf('X'), Item.gunpowder,
						Character.valueOf('Y'), ForestryItem.canEmpty, Character.valueOf('Z'), ForestryItem.honeydew });
		// ForestryCore.oreHandler.registerCarpenterRecipe(dissipationCharge);

		// Ender pearl
		RecipeManagers.carpenterManager.addRecipe(100, null, new ItemStack(Item.enderPearl, 1), new Object[] { " # ", "###", " # ", Character.valueOf('#'),
				new ItemStack(ForestryItem.craftingMaterial, 1, 1) });
		// Woven Silk
		RecipeManagers.carpenterManager.addRecipe(10, new LiquidStack(Block.waterStill.blockID, 500), null, new ItemStack(ForestryItem.craftingMaterial, 1, 3),
				new Object[] { "###", "###", "###", Character.valueOf('#'), new ItemStack(ForestryItem.craftingMaterial, 1, 2) });

		// Boxes
		RecipeManagers.carpenterManager.addRecipe(5, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.carton, 2),
				new Object[] { " # ", "# #", " # ", Character.valueOf('#'), "pulpWood" });
		RecipeManagers.carpenterManager.addRecipe(20, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.crate, 24),
				new Object[] { " # ", "# #", " # ", Character.valueOf('#'), "logWood" });

		// Assembly Kits
		RecipeManagers.carpenterManager.addRecipe(20, new LiquidStack(0, 0), new ItemStack(ForestryItem.carton), new ItemStack(ForestryItem.kitPickaxe),
				new Object[] { "###", " X ", " X ", Character.valueOf('#'), "ingotBronze", Character.valueOf('X'), "stickWood" });
		// ForestryCore.oreHandler.registerCarpenterRecipe(kitPickaxe);
		RecipeManagers.carpenterManager.addRecipe(20, new LiquidStack(0, 0), new ItemStack(ForestryItem.carton), new ItemStack(ForestryItem.kitShovel),
				new Object[] { " # ", " X ", " X ", Character.valueOf('#'), "ingotBronze", Character.valueOf('X'), "stickWood" });
		// ForestryCore.oreHandler.registerCarpenterRecipe(kitShovel);

		// Reclamation
		RecipeManagers.carpenterManager.addRecipe(null, new ItemStack(ForestryItem.ingotBronze.itemID, 2, ForestryItem.ingotBronze.getMaxDamage()),
				new Object[] { "#", Character.valueOf('#'), ForestryItem.brokenBronzePickaxe });
		RecipeManagers.carpenterManager
				.addRecipe(null, ForestryItem.ingotBronze, new Object[] { "#", Character.valueOf('#'), ForestryItem.brokenBronzeShovel });

		// Crating and uncrating condensed
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedWood));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedCobblestone));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedDirt));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedStone));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedBrick));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedCacti));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedSand));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedObsidian));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedNetherrack));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedSoulsand));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedSandstone));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedBogearth));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedHumus));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedNetherbrick));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedPeat));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedApatite));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedFertilizer));
		((RecipeManager) RecipeManagers.carpenterManager).addCrating("ingotTin", ForestryItem.ingotTin, new ItemStack(ForestryItem.cratedTin));
		((RecipeManager) RecipeManagers.carpenterManager).addCrating("ingotCopper", ForestryItem.ingotCopper, new ItemStack(ForestryItem.cratedCopper));
		((RecipeManager) RecipeManagers.carpenterManager).addCrating("ingotBronze", ForestryItem.ingotBronze, new ItemStack(ForestryItem.cratedBronze));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedWheat));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedMycelium));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedMulch));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedCookies));

		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedHoneycombs));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedBeeswax));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedPollen));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedPropolis));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedHoneydew));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedRoyalJelly));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedCocoaComb));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedRedstone));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedLapis));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedReeds));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedClay));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedGlowstone));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedApples));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedNetherwart));

		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedSimmeringCombs));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedStringyCombs));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedFrozenCombs));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedDrippingCombs));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedRefractoryWax));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedPhosphor));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedAsh));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedCharcoal));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedGravel));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedCoal));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedSeeds));
		RecipeManagers.carpenterManager.addCrating(new ItemStack(ForestryItem.cratedSaplings));
	}
}
