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
package forestry.cultivation.planters;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.gates.ITrigger;
import forestry.api.core.GlobalManager;
import forestry.api.cultivation.ICropProvider;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.TileMachine;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StructureBlueprint;
import forestry.core.utils.StructureConstruction;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Vect;
import forestry.cultivation.gadgets.TilePlanter;

public abstract class Planter extends Machine {

	// / CONSTANTS
	public static final short SLOT_SOIL_1 = 0;
	public static final short SLOT_GERMLING_1 = 4;
	public static final short SLOT_WASTE_1 = 8;
	public static final short SLOT_COUNT_PART = 4;

	public ItemStack validSoil; // Block or item that can be used to create the
								// ground to plant on
	public ItemStack validGround; // Block that can be planted on
	public ItemStack validWaste; // Block that is waste to be collected
	public ItemStack validDisposal; // Block that is put into inventory when
									// waste is collected

	public StructureBlueprint site;
	public Vect siteOffset;
	public StructureBlueprint soil;
	public Vect soilOffset;
	public StructureBlueprint plantation;
	public Vect plantationOffset;

	protected boolean requiresSoil = true;
	protected boolean requiresGermling = false;

	public abstract void openGui(EntityPlayer player, IInventory tile);

	// / HELPER FUNCTIONS
	public boolean hasGermlingBySeed(ItemStack germling) {
		for (ICropProvider provider : cropProviders)
			if (provider.isGermling(germling))
				return true;
		return false;
	}

	private ICropProvider getCropProvider(ItemStack germling) {
		for (ICropProvider provider : cropProviders)
			if (provider.isGermling(germling))
				return provider;
		return null;
	}

	// / MEMBER
	protected ArrayList<ICropProvider> cropProviders = new ArrayList<ICropProvider>();

	// protected ItemStack[] fuelItemStacks = new ItemStack[12];
	protected TileInventoryAdapter inventory;

	protected StructureConstruction templateArboretum;
	protected boolean isCleared = false; // Whether the arboretum area has been
											// cleared.
	protected boolean isUnbroken = false; // Whether the arboretum has already
											// been fully built
	protected StructureConstruction templateSoil;
	protected StructureConstruction templateWater; // carbon copy of
													// templateSoil
	protected StructureConstruction templatePlantation;

	// / CONSTRUCTOR
	public Planter(TileMachine tile) {
		super(tile);
		inventory = new TileInventoryAdapter(tile, 12, "Items");
	}

	public Planter(TileMachine tile, ICropProvider provider) {
		super(tile);
		cropProviders.add(provider);
		inventory = new TileInventoryAdapter(tile, 12, "Items");
	}

	public Planter(TileMachine tile, ArrayList<ICropProvider> providers) {
		super(tile);
		for (ICropProvider provider : providers) {
			cropProviders.add(provider);
		}
		inventory = new TileInventoryAdapter(tile, 12, "Items");
	}

	// / LOADING AND SAVING
	/**
	 * Read saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		isCleared = nbttagcompound.getBoolean("IsCleared");
		isUnbroken = nbttagcompound.getBoolean("IsBuilt");

		inventory.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("TemplateArboretum")) {
			templateArboretum = new StructureConstruction();
			NBTTagList nbttaglistStructure = nbttagcompound.getTagList("TemplateArboretum");
			templateArboretum.readFromNBT((NBTTagCompound) nbttaglistStructure.tagAt(0));
		}

		if (nbttagcompound.hasKey("TemplateSoil")) {
			templateSoil = new StructureConstruction();
			templateWater = new StructureConstruction();
			NBTTagList nbttaglistStructure = nbttagcompound.getTagList("TemplateSoil");
			templateSoil.readFromNBT((NBTTagCompound) nbttaglistStructure.tagAt(0));
			templateWater.readFromNBT((NBTTagCompound) nbttaglistStructure.tagAt(0));
		}

		if (nbttagcompound.hasKey("TemplatePlantation")) {
			templatePlantation = new StructureConstruction();
			NBTTagList nbttaglistStructure = nbttagcompound.getTagList("TemplatePlantation");
			templatePlantation.readFromNBT((NBTTagCompound) nbttaglistStructure.tagAt(0));
		}
	}

	/**
	 * Write save data
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsCleared", isCleared);
		nbttagcompound.setBoolean("IsBuilt", isUnbroken);

		inventory.writeToNBT(nbttagcompound);

		NBTTagList nbttaglistStructure;
		NBTTagCompound nbttagcompoundStructure;
		if (templateArboretum != null) {
			nbttaglistStructure = new NBTTagList();
			nbttagcompoundStructure = new NBTTagCompound();
			templateArboretum.writeToNBT(nbttagcompoundStructure);
			nbttaglistStructure.appendTag(nbttagcompoundStructure);
			nbttagcompound.setTag("TemplateArboretum", nbttaglistStructure);
		}

		if (templateSoil != null) {
			nbttaglistStructure = new NBTTagList();
			nbttagcompoundStructure = new NBTTagCompound();
			templateSoil.writeToNBT(nbttagcompoundStructure);
			nbttaglistStructure.appendTag(nbttagcompoundStructure);
			nbttagcompound.setTag("TemplateSoil", nbttaglistStructure);
		}

		if (templatePlantation != null) {
			nbttaglistStructure = new NBTTagList();
			nbttagcompoundStructure = new NBTTagCompound();
			templatePlantation.writeToNBT(nbttagcompoundStructure);
			nbttaglistStructure.appendTag(nbttagcompoundStructure);
			nbttagcompound.setTag("TemplatePlantation", nbttaglistStructure);
		}
	}

	// / INVENTORY HANDLING
	protected boolean isSoilSlot(int i) {
		return i < 4;
	}

	protected boolean isGermlingSlot(int i) {
		return i < 8;
	}

	protected boolean isDisposalSlot(int i) {
		return i >= 8;
	}

	/**
	 * Get a valid soil stack.
	 * 
	 * @return id of the slot if available. -1 otherwise.
	 */
	protected int getSoilStack() {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (!isSoilSlot(i)) {
				continue;
			}

			if (inventory.getStackInSlot(i) == null) {
				continue;
			}

			if (inventory.getStackInSlot(i).isItemEqual(validSoil))
				return i;
		}

		return -1;
	}

	/**
	 * Get a valid germling stack.
	 * 
	 * @return id of the slot if available. -1 otherwise.
	 */
	protected int getGermlingStack() {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (isGermlingStack(i))
				return i;
		}

		return -1;
	}

	protected boolean isGermlingStack(int i) {
		if (!isGermlingSlot(i))
			return false;

		if (inventory.getStackInSlot(i) == null)
			return false;

		return hasGermlingBySeed(inventory.getStackInSlot(i));
	}

	/**
	 * Get a valid waste stack.
	 * 
	 * @return id of the slot if available. -1 otherwise.
	 */
	protected int getDisposalStack() {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (!isDisposalSlot(i)) {
				continue;
			}

			if (inventory.getStackInSlot(i) == null) {
				continue;
			}

			if (inventory.getStackInSlot(i).isItemEqual(validDisposal))
				return i;
		}

		return -1;
	}

	/**
	 * Returns the next free waste slot.
	 * 
	 * @return
	 */
	protected int getFreeSoilSlot() {
		for (int i = 0; i < 4; i++) {
			if (inventory.getStackInSlot(i) == null)
				return i;

			if (inventory.getStackInSlot(i).isItemEqual(validSoil) && inventory.getStackInSlot(i).stackSize < inventory.getStackInSlot(i).getMaxStackSize())
				return i;
		}

		return -1;
	}

	/**
	 * Returns the next free germling slot.
	 * 
	 * @return
	 */
	protected int getFreeGermlingSlot(ItemStack germling) {
		for (int i = 4; i < 8; i++) {
			if (inventory.getStackInSlot(i) == null)
				return i;

			if (inventory.getStackInSlot(i).isItemEqual(germling) && inventory.getStackInSlot(i).stackSize < inventory.getStackInSlot(i).getMaxStackSize())
				return i;
		}

		return -1;
	}

	/**
	 * Returns the next free waste slot.
	 * 
	 * @return
	 */
	protected int getFreeDisposalSlot() {
		for (int i = 8; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null)
				return i;

			if (inventory.getStackInSlot(i).isItemEqual(validDisposal) && inventory.getStackInSlot(i).stackSize < inventory.getStackInSlot(i).getMaxStackSize())
				return i;
		}

		return -1;
	}

	// / WORK
	@Override
	public void updateServerSide() {

		if (tile.worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		if (requiresSoil && getSoilStack() < 0) {
			setErrorState(EnumErrorCode.NORESOURCE);
		} else if (requiresGermling && getGermlingStack() < 0) {
			setErrorState(EnumErrorCode.NORESOURCE);
		} else if (getFreeDisposalSlot() < 0) {
			setErrorState(EnumErrorCode.NODISPOSAL);
		} else {
			setErrorState(EnumErrorCode.OK);
		}

	}

	/**
	 * Called by the power provider if enough energy is available
	 */
	@Override
	public boolean doWork() {
		if (!Proxies.common.isSimulating(tile.worldObj))
			return false;

		if (templateArboretum == null) {
			this.templateArboretum = new StructureConstruction(site, tile.Coords(), siteOffset);
		}

		if (soil != null && templateSoil == null) {
			this.templateSoil = new StructureConstruction(soil, tile.Coords(), soilOffset);
			this.templateWater = new StructureConstruction(soil, tile.Coords(), soilOffset);
		}

		if (plantation != null && templatePlantation == null) {
			this.templatePlantation = new StructureConstruction(plantation, tile.Coords(), plantationOffset);
		}

		// Check whether the arboretum area is already cleared
		if (!this.isCleared) {
			this.clearArea();
			return true;
		}

		if (this.maintainWater()) {
		}

		// Maintain the soil layer
		if (this.maintainSoil()) {
		} else // Plant saplings according to pattern.
		if (this.maintainVegetation()) {
		}

		dumpStash();

		return true;
	}

	/**
	 * Clear build area for the arboretum.
	 */
	private void clearArea() {
		if (this.templateArboretum.isFinished) {
			this.isCleared = true;
			this.templateArboretum.reset();
			return;
		}

		int curblockid = 0;
		while (curblockid == 0 && !this.templateArboretum.isFinished) {
			Vect curPos = new Vect(templateArboretum.getCurrentX(), templateArboretum.getCurrentY(), templateArboretum.getCurrentZ());
			curblockid = tile.worldObj.getBlockId(curPos.x, curPos.y, curPos.z);

			// Don't kill myself or holy blocks or nulled space.
			if (curblockid != 0 && !GlobalManager.holyBlockIds.contains(curblockid) && templateArboretum.getCurrentBlockId() > -1) {
				ArrayList<ItemStack> items = BlockUtil.getBlockItemStack(tile.worldObj, curPos);

				tile.worldObj.setBlockWithNotify(curPos.x, curPos.y, curPos.z, 0);

				// We need to drop what was destroyed.
				if (items != null) {
					for (int i = 0; i < items.size(); i++) {
						if (items.get(i) == null) {
							continue;
						}

						if (items.get(i).getItem() == null) {
							// null for
							// unexplicable
							// reasons
							// (buildcraft
							// pipes).
							continue;
						}

						if (items.get(i).stackSize > 0) {
							EntityItem entity = new EntityItem(tile.worldObj, curPos.x, curPos.y, curPos.z, items.get(i));
							tile.worldObj.spawnEntityInWorld(entity);
						}
					}
				}
			}
			templateArboretum.advanceStep(); // Is skipped if no item stacks
												// were returned/dropped!
		}

	}

	/**
	 * Checks whether the areable area is covered in appropriate soil.
	 * 
	 * @return True if repair on a block was successful. False otherwise.
	 */
	private boolean maintainSoil() {
		if (templateSoil.isFinished) {
			templateSoil.reset();
		}

		int processedBlocks = 0;
		while (!templateSoil.isFinished && processedBlocks < Config.planterThrottle) {
			processedBlocks++;
			// Plant required soil
			if (templateSoil.getCurrentBlockId() == validGround.itemID) {
				Vect pos = templateSoil.getCurrentPos();

				int blockid = tile.worldObj.getBlockId(pos.x, pos.y, pos.z);
				int aboveid = tile.worldObj.getBlockId(pos.x, pos.y + 1, pos.z);
				// We need to fix soil if it's not valid ground and has nothing
				// or just snow above it.
				if (!(Block.blocksList[blockid] != null && Block.blocksList[blockid].getBlockHardness(tile.worldObj, pos.x, pos.y, pos.z) < 0)
						&& blockid != validGround.itemID && (aboveid == 0 || aboveid == Block.snow.blockID)) {
					if (validWaste != null) {
						if (blockid == validWaste.itemID) {
							collectSand(pos);
						}

						// Account for overgrown dirt - Hackish!
						if (validWaste.itemID == Block.dirt.blockID && blockid == Block.grass.blockID) {
							collectSand(pos);
						}

					}
					return fillBlock(pos);
				}
			}
			templateSoil.advanceStep();
		}
		this.isUnbroken = true;

		return false;
	}

	/**
	 * Checks whether water is placed at the appropriate points.
	 * 
	 * @return True if repair on a block was successful. False otherwise.
	 */
	private boolean maintainWater() {
		if (templateWater.isFinished) {
			templateWater.reset();
		}

		while (!templateWater.isFinished) {
			// Place water if required
			if (templateWater.getCurrentBlockId() == Block.waterStill.blockID) {
				Vect pos = templateWater.getCurrentPos();
				boolean skip = false;

				int blockid = tile.worldObj.getBlockId(pos.x, pos.y, pos.z);
				if (blockid != Block.waterStill.blockID) {
					// Make sure we are contained
					int[] neighbourids = new int[] { tile.worldObj.getBlockId(pos.x - 1, pos.y, pos.z), tile.worldObj.getBlockId(pos.x + 1, pos.y, pos.z),
							tile.worldObj.getBlockId(pos.x, pos.y, pos.z - 1), tile.worldObj.getBlockId(pos.x, pos.y, pos.z + 1) };
					for (int i = 0; i < neighbourids.length; i++)
						if (neighbourids[i] != validGround.itemID && neighbourids[i] != validWaste.itemID) {
							skip = true;
							break;
						}
					if (!skip)
						return waterBlock(templateWater.getCurrentPos());
				}
			}
			templateWater.advanceStep();
		}

		return false;
	}

	/**
	 * Disposes of a specific block
	 * 
	 * @param blockPos
	 */
	private void collectSand(Vect blockPos) {
		tile.worldObj.setBlockWithNotify(blockPos.x, blockPos.y, blockPos.z, 0);

		int slot = this.getFreeDisposalSlot();
		if (slot >= 0) {
			if (inventory.getStackInSlot(slot) == null) {
				inventory.setInventorySlotContents(slot, validDisposal.copy());
			} else {
				inventory.getStackInSlot(slot).stackSize++;
			}

			return;
		}
	}

	/**
	 * Checks whether the vegetation is in order.
	 * 
	 * @return True if repair on a block was successful. False otherwise.
	 */
	/**
	 * Checks whether the vegetation is in order.
	 * 
	 * @return True if repair on a block was successful. False otherwise.
	 */
	protected boolean maintainVegetation() {
		// Not every planter has vegetation
		if (templatePlantation == null)
			return false;

		if (templatePlantation.isFinished) {
			templatePlantation.reset();
		}

		int processedBlocks = 0;
		while (!templatePlantation.isFinished && processedBlocks < Config.planterThrottle) {
			processedBlocks++;
			if (templatePlantation.getCurrentBlockId() == Block.sapling.blockID) {
				int x = templatePlantation.getCurrentX();
				int y = templatePlantation.getCurrentY();
				int z = templatePlantation.getCurrentZ();

				if (plantSapling(x, y, z)) {
					templatePlantation.advanceStep();
					return true;
				}
			}
			templatePlantation.advanceStep();
		}
		return false;
	}

	/**
	 * Are resources available to perform earthworks (replace other blocks with humus)?
	 * 
	 * @return
	 */
	private boolean canFill() {
		return (this.getSoilStack() >= 0);
	}

	/**
	 * Decreases a soil stack
	 * 
	 * @param n
	 */
	private void decrSoilStack(int n) {
		int i = this.getSoilStack();
		if (i < 0)
			return;

		inventory.decrStackSize(i, n);

	}

	/**
	 * Decreases a sapling stack
	 * 
	 * @param n
	 */
	protected void decrSaplingStack(int i, int n) {
		if (i < 0)
			return;

		inventory.decrStackSize(i, n);

	}

	private boolean waterBlock(Vect pos) {
		tile.worldObj.setBlockWithNotify(pos.x, pos.y, pos.z, Block.waterStill.blockID);
		return true;
	}

	/**
	 * Replaces a single block with a humus block. Consumes one humus block from an item stack.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private boolean fillBlock(Vect pos) {
		if (!this.canFill())
			return false;

		tile.worldObj.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, validGround.itemID, validGround.getItemDamage());

		// Only decrease stash if replacing was successful
		int blockid = tile.worldObj.getBlockId(pos.x, pos.y, pos.z);
		if (blockid == validGround.itemID) {
			this.decrSoilStack(1); // decrease stash by one
		}

		return true;
	}

	/**
	 * Plants next possible sapling.
	 * 
	 * @return True if a sapling was successfully planted. False otherwise.
	 */
	protected boolean plantSapling(int x, int y, int z) {
		for (int stack = 0; stack < inventory.getSizeInventory(); stack++) {
			if (isGermlingStack(stack)) {
				// Can't plant without germling
				if (stack <= 0)
					return false;

				// Don't continue if no provider for some reason
				ICropProvider provider = getCropProvider(inventory.getStackInSlot(stack));
				if (provider == null) {
					continue;
				}

				if (provider.doPlant(inventory.getStackInSlot(stack), tile.worldObj, x, y, z)) {
					this.decrSaplingStack(stack, 1); // decrease stash by one
					return true;
				}
			}
		}
		return false;
	}

	// OUTPUT HANDLING
	/**
	 * Searches for available IInventories and dumps stored harvest if possible
	 */
	private void dumpStash() {
		ForgeDirection[] pipes = BlockUtil.getPipeDirections(tile.worldObj, tile.Coords(), ForgeDirection.UNKNOWN);

		if (pipes.length > 0) {
			dumpToPipe(pipes);
		} else {
			IInventory[] inventories = BlockUtil.getAdjacentInventories(tile.worldObj, tile.Coords(), ForgeDirection.UNKNOWN);
			dumpToInventory(inventories);
		}
	}

	private void dumpToPipe(ForgeDirection[] pipes) {

		for (int i = 8; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).stackSize <= 0) {
				continue;
			}

			ForgeDirection[] filtered;
			if (!Config.planterSideSensitive) {
				filtered = pipes;
			} else {
				filtered = BlockUtil.filterPipeDirections(pipes, new ForgeDirection[] { ForgeDirection.DOWN, ForgeDirection.UP });
			}

			while (inventory.getStackInSlot(i).stackSize > 0 && filtered.length > 0) {
				BlockUtil.putFromStackIntoPipe(tile, filtered, inventory.getStackInSlot(i));
			}

			if (inventory.getStackInSlot(i).stackSize <= 0) {
				inventory.setInventorySlotContents(i, null);
			}
		}

	}

	private void dumpToInventory(IInventory[] inventories) {

		for (int i = 8; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).stackSize <= 0) {
				continue;
			}

			for (int j = 0; j < inventories.length; j++) {
				// can become zero, if matching inventory was found.
				if (inventory.getStackInSlot(i) == null) {
					continue;
				}

				// Don't dump in arboretums!
				if (inventories[j] instanceof TilePlanter) {
					continue;
				}

				for (int k = 0; k < inventories[j].getSizeInventory(); k++) {
					ItemStack stack = inventories[j].getStackInSlot(k);
					if (stack == null) {
						inventories[j].setInventorySlotContents(k, inventory.getStackInSlot(i));
						inventory.setInventorySlotContents(i, null);
						break;
					}

					if (stack.itemID != inventory.getStackInSlot(i).itemID) {
						continue;
					}

					int available = inventories[j].getInventoryStackLimit() - stack.stackSize;
					if (available <= 0) {
						continue;
					}

					if (available >= inventory.getStackInSlot(i).stackSize) {
						stack.stackSize += inventory.getStackInSlot(i).stackSize;
						inventory.setInventorySlotContents(i, null);
						break;
					} else {
						stack.stackSize = inventories[j].getInventoryStackLimit();
						inventory.getStackInSlot(i).stackSize -= available;
						continue;
					}
				}
			}
		}

	}

	// / STATE INFORMATION
	@Override
	public boolean isWorking() {
		return true;
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		int max = 0;
		int avail = 0;

		for (int i = 4; i < 8; i++) {
			max += 64;
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			avail += inventory.getStackInSlot(i).stackSize;
		}

		return ((float) avail / (float) max) > percentage;
	}

	@Override
	public boolean hasFuelMin(float percentage) {
		int max = 0;
		int avail = 0;

		for (int i = 0; i < 4; i++) {
			max += 64;
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			avail += inventory.getStackInSlot(i).stackSize;
		}

		return ((float) avail / (float) max) > percentage;
	}

	// ISPECIALINVENTORY IMPLEMENTATION
	/**
	 * Adds humus and saplings to the appropriate free slots of the arboretum. Does not care where the stuff comes from.
	 * 
	 * @param stack
	 * @param doAdd
	 * @param from
	 * @return
	 */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		// Humus
		if (stack.isItemEqual(validSoil))
			return inventory.addStack(stack, SLOT_SOIL_1, SLOT_COUNT_PART, false, doAdd);

		if (hasGermlingBySeed(stack))
			return inventory.addStack(stack, SLOT_GERMLING_1, SLOT_COUNT_PART, false, doAdd);

		return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		for (int i = 8; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}

			// Only sand can be extracted
			if (!inventory.getStackInSlot(i).isItemEqual(validDisposal)) {
				continue;
			}

			ItemStack product = validDisposal.copy();
			if (doRemove) {
				decrStackSize(i, 1);
			}
			return new ItemStack[] { product };
		}

		return new ItemStack[0];
	}

	// IINVENTORY/ISIDEDINVENTORY IMPLEMENTATION
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	/**
	 * Waste can be extracted from the sides, raw materials are added from top or bottom.
	 */
	@Override
	public int getStartInventorySide(int side) {
		if (side == 0)
			return SLOT_SOIL_1;
		else if (side == 1)
			return SLOT_GERMLING_1;
		else
			return SLOT_WASTE_1;
	}

	@Override
	public int getSizeInventorySide(int side) {
		return 4;
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowSoil25);
		res.add(ForestryTrigger.lowSoil10);
		res.add(ForestryTrigger.lowGermlings25);
		res.add(ForestryTrigger.lowGermlings10);
		return res;
	}

}
