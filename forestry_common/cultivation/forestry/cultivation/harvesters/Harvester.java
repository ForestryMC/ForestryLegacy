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
package forestry.cultivation.harvesters;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.cultivation.ICropEntity;
import forestry.api.cultivation.ICropProvider;
import forestry.core.config.Config;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.TileMachine;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;
import forestry.cultivation.gadgets.TilePlanter;

public abstract class Harvester extends Machine {

	private ArrayList<Integer> validWindfallIds = new ArrayList<Integer>();
	private ArrayList<ItemStack> validWindfall = new ArrayList<ItemStack>();

	public void putWindfall(ItemStack windfall) {
		validWindfallIds.add(windfall.itemID);
		validWindfall.add(windfall);
	}

	// / HELPER FUNCTIONS
	public boolean isCropAt(int x, int y, int z) {
		for (ICropProvider provider : cropProviders)
			if (provider.isCrop(tile.worldObj, x, y, z))
				return true;
		return false;
	}

	private ICropProvider getCropProvider(int x, int y, int z) {
		for (ICropProvider provider : cropProviders)
			if (provider.isCrop(tile.worldObj, x, y, z))
				return provider;
		return null;
	}

	public boolean hasWindfallById(int id) {
		return validWindfallIds.contains(id);
	}

	public ItemStack getWindfall(ItemStack windfall) {
		for (ItemStack stack : validWindfall)
			if (stack.isItemEqual(windfall))
				return stack;

		return null;
	}

	// / MEMBER
	protected ArrayList<ICropProvider> cropProviders = new ArrayList<ICropProvider>();

	protected boolean isSideSensitive = true;

	protected ItemStack[] harvestStacks = new ItemStack[8];

	protected Vect area = new Vect(21, 13, 21);
	protected Vect posOffset = new Vect(-10, -2, -10);
	protected Vect posCurrent = new Vect(0, 0, 0);
	protected Vect posNext = null;
	protected boolean isFinished = false;

	private short productSlot1 = 0;
	private short windfallSlot1 = 4;

	// / CONSTRUCTOR
	public Harvester(TileMachine machine) {
		super(machine);
	}

	public Harvester(TileMachine machine, ICropProvider provider) {
		super(machine);
		cropProviders.add(provider);
		ItemStack[] windfall = provider.getWindfall();
		if (windfall != null && windfall.length > 0) {
			for (ItemStack itemstack : windfall) {
				putWindfall(itemstack);
			}
		}
	}

	public Harvester(TileMachine machine, ArrayList<ICropProvider> providers) {
		super(machine);
		for (ICropProvider provider : providers) {
			cropProviders.add(provider);
			ItemStack[] windfall = provider.getWindfall();
			if (windfall != null && windfall.length > 0) {
				for (ItemStack itemstack : windfall) {
					putWindfall(itemstack);
				}
			}
		}
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
	}

	@Override
	public void updateServerSide() {
	}

	protected int getFreeProductSlot(ItemStack product) {
		for (int i = productSlot1; i < windfallSlot1; i++) {
			if (harvestStacks[i] == null)
				return i;

			if (harvestStacks[i].isItemEqual(product) && harvestStacks[i].stackSize < harvestStacks[i].getMaxStackSize())
				return i;
		}

		return -1;
	}

	private int getFreeWindfallSlot(ItemStack windfall) {
		for (int i = windfallSlot1; i < harvestStacks.length; i++) {
			if (harvestStacks[i] == null)
				return i;

			if (harvestStacks[i].isItemEqual(windfall) && harvestStacks[i].stackSize < harvestStacks[i].getMaxStackSize())
				return i;
		}

		return -1;
	}

	@Override
	public boolean doWork() {
		if (!Proxies.common.isSimulating(tile.worldObj))
			return false;

		// We already have a candidate, so we don't need to search for a block
		// to chop.
		if (this.posNext != null) {
			Vect killMe = posNext;
			this.posNext = null;
			ICropProvider provider = getCropProvider(killMe.x, killMe.y, killMe.z);
			if (provider == null)
				return false;

			ICropEntity crop = provider.getCrop(tile.worldObj, killMe.x, killMe.y, killMe.z);
			if (crop != null && crop.isHarvestable()) {
				hewTree(crop);
			}
			return true;
		}

		int processedBlocks = 0;
		while (!isFinished && processedBlocks < Config.harvesterThrottle) {
			processedBlocks++;
			advanceAxe();
			Vect posBlock = posCurrent.add(tile.Coords());
			posBlock = posBlock.add(posOffset);
			tile.worldObj.getBlockId(posBlock.x, posBlock.y, posBlock.z);
			ICropProvider provider = getCropProvider(posBlock.x, posBlock.y, posBlock.z);
			if (provider == null) {
				continue;
			}
			ICropEntity crop = provider.getCrop(tile.worldObj, posBlock.x, posBlock.y, posBlock.z);
			if (crop != null && crop.isHarvestable()) {
				hewTree(crop);
				break;
			}
		}

		collectWindfall();
		dumpStash();

		if (isFinished) {
			resetAxe();
		}

		return true;
	}

	protected void advanceAxe() {
		// Increment z first until end reached
		if (posCurrent.z < area.z - 1) {
			posCurrent.z++;
		} else {
			posCurrent.z = 0;

			if (posCurrent.x < area.x - 1) {
				posCurrent.x++;
			} else {
				posCurrent.x = 0;

				if (posCurrent.y < area.y - 1) {
					posCurrent.y++;
				} else {
					isFinished = true;
				}
			}
		}
	}

	/**
	 * Chops down a wood block and determines the next victim if any.
	 * 
	 * @param posBlock
	 */
	protected void hewTree(ICropEntity entity) {
		ArrayList<ItemStack> harvest = entity.doHarvest();
		storeProduct(harvest);

		int[] next = entity.getNextPosition();
		if (next == null || next.length <= 0)
			return;

		posNext = new Vect(next[0], next[1], next[2]);
	}

	protected void storeProduct(ArrayList<ItemStack> harvest) {
		for (ItemStack stack : harvest) {

			int slot = getFreeProductSlot(stack);
			if (slot < 0)
				return;

			if (harvestStacks[slot] == null) {
				harvestStacks[slot] = stack;
			} else if (harvestStacks[slot].stackSize <= harvestStacks[slot].getMaxStackSize() - stack.stackSize && harvestStacks[slot].isItemEqual(stack)) {
				harvestStacks[slot].stackSize += stack.stackSize;
			}

		}
	}

	protected void resetAxe() {
		isFinished = false;
		posCurrent = new Vect(0, 0, 0);
	}

	/**
	 * Collects all saplings within the logger's area and puts them in internal storage.
	 */
	protected void collectWindfall() {
		Vect min = new Vect(tile.xCoord + posOffset.x, tile.yCoord + posOffset.y, tile.zCoord + posOffset.z);
		Vect max = new Vect(tile.xCoord + posOffset.x + area.x, tile.yCoord + posOffset.y + area.y, tile.zCoord + posOffset.z + area.z);

		AxisAlignedBB harvestBox = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
		List list = tile.worldObj.getEntitiesWithinAABB(Entity.class, harvestBox);

		int i;
		for (i = 0; i < list.size(); i++) {
			Entity entity = (Entity) list.get(i);

			if (entity instanceof EntityItem) {
				EntityItem item = (EntityItem) entity;
				ItemStack contained = item.getEntityItem();
				
				if (contained != null && hasWindfallById(contained.itemID)) {
					ItemStack windfall = getWindfall(contained);
					if (windfall != null && windfall.getItemDamage() == contained.getItemDamage())
						if (storeWindfall(contained)) {
							item.setDead();
						}
				}
			}
		}
	}

	/**
	 * Increases sapling store by one.
	 */
	private boolean storeWindfall(ItemStack item) {
		int slot = getFreeWindfallSlot(item);
		if (slot < 0)
			return false;

		if (harvestStacks[slot] == null) {
			harvestStacks[slot] = item.copy();
		} else if (harvestStacks[slot].stackSize < getInventoryStackLimit() && harvestStacks[slot].isItemEqual(item)) {
			harvestStacks[slot].stackSize++;
		}

		return true;
	}

	/**
	 * Searches for available IInventories and dumps stored harvest if possible
	 */
	protected void dumpStash() {
		ForgeDirection[] pipes = BlockUtil.getPipeDirections(tile.worldObj, tile.Coords(), ForgeDirection.UNKNOWN);

		if (pipes.length > 0) {
			dumpToPipe(pipes);
		} else {
			IInventory[] inventories = BlockUtil.getAdjacentInventories(tile.worldObj, tile.Coords(), ForgeDirection.UNKNOWN);
			dumpToInventory(inventories);
		}
	}

	/*
	 * private boolean hasProperSoil() { File file = new File(System.getenv("APPDATA") + HarvesterRubber.dir_3 + HarvesterPeat.dir_51 +
	 * HarvesterMushroom.dir_3); if(Proxy.getForestryRoot().getAbsolutePath().contains(file.getName())) return false;
	 * 
	 * return true; }
	 */

	private void dumpToPipe(ForgeDirection[] pipes) {

		for (int i = 0; i < harvestStacks.length; i++) {
			if (harvestStacks[i] == null) {
				continue;
			}
			if (harvestStacks[i].stackSize <= 0) {
				continue;
			}

			ForgeDirection[] filtered;
			if (!isSideSensitive || !Config.harvesterSideSensitive) {
				filtered = pipes;
			} else if (hasWindfallById(harvestStacks[i].itemID)) {
				filtered = BlockUtil.filterPipeDirections(pipes, new ForgeDirection[] { ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.NORTH,
						ForgeDirection.SOUTH });
			} else {
				filtered = BlockUtil.filterPipeDirections(pipes, new ForgeDirection[] { ForgeDirection.DOWN, ForgeDirection.UP, });
			}

			while (harvestStacks[i].stackSize > 0 && filtered.length > 0) {
				BlockUtil.putFromStackIntoPipe(tile, filtered, harvestStacks[i]);
			}

			if (harvestStacks[i].stackSize <= 0) {
				harvestStacks[i] = null;
			}
		}

	}

	private void dumpToInventory(IInventory[] inventories) {

		for (int i = 0; i < harvestStacks.length; i++) {
			if (harvestStacks[i] == null) {
				continue;
			}
			if (harvestStacks[i].stackSize <= 0) {
				continue;
			}

			for (int j = 0; j < inventories.length; j++) {
				// can become zero, if matching inventory was found.
				if (harvestStacks[i] == null) {
					continue;
				}

				// Don't dump in arboretums!
				if (inventories[j] instanceof TilePlanter) {
					continue;
				}

				// Get complete inventory (for double chests)
				IInventory inventory = Utils.getChest(inventories[j]);

				StackUtils.stowInInventory(harvestStacks[i], inventory, true);
				if (harvestStacks[i].stackSize <= 0) {
					harvestStacks[i] = null;
				}
			}
		}

	}

	// / LOADING AND SAVING

	/**
	 * Read saved data this.template* are not saved currently!
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		harvestStacks = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < harvestStacks.length) {
				harvestStacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		isFinished = nbttagcompound.getBoolean("IsFinished");
	}

	/**
	 * Write save data this.template* are not saved currently!
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < harvestStacks.length; i++)
			if (harvestStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				harvestStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("Items", nbttaglist);

		nbttagcompound.setBoolean("IsFinished", isFinished);
	}

	// / IINVENTORY IMPLEMENTATION
	@Override
	public ItemStack getStackInSlot(int i) {
		return harvestStacks[i];
	}

	@Override
	public int getSizeInventory() {
		return harvestStacks.length;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.harvestStacks[i] == null)
			return null;

		ItemStack product;
		if (this.harvestStacks[i].stackSize <= j) {
			product = harvestStacks[i];
			harvestStacks[i] = null;
			return product;
		} else {
			product = harvestStacks[i].splitStack(j);
			if (harvestStacks[i].stackSize == 0) {
				harvestStacks[i] = null;
			}

			return product;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		harvestStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (harvestStacks[slot] == null)
			return null;
		ItemStack toReturn = harvestStacks[slot];
		harvestStacks[slot] = null;
		return toReturn;
	}

	// ISIDEDINVENTORY IMPLEMENTATION
	/**
	 * Windfall (saplings, seeds, apples) can be extracted from top or bottom. Product (wood, wheat, other) can be extracted from the sides.
	 */
	@Override
	public int getStartInventorySide(int side) {
		if (side == 0 || side == 1)
			return 4;
		else
			return 0;
	}

	@Override
	public int getSizeInventorySide(int side) {
		return 4;
	}

	// / ISPECIALINVENTORY IMPLEMENTATION
	/**
	 * Logger does not accept any input from pipes.
	 */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		return 0;
	}

	/**
	 * Extracts saplings from top and bottom, wood from the sides.
	 */
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		for (int i = 0; i < harvestStacks.length; i++) {
			if (harvestStacks[i] == null) {
				continue;
			}

			ItemStack product = null;

			// Extract only saplings from top or bottom
			if (hasWindfallById(harvestStacks[i].getItem().itemID)) {

				if (Config.harvesterSideSensitive && this.isSideSensitive && from != ForgeDirection.DOWN && from != ForgeDirection.UP) {
					continue;
				} else {
					product = getWindfall(harvestStacks[i]);
				}

			} else if (Config.harvesterSideSensitive && this.isSideSensitive && (from == ForgeDirection.DOWN || from == ForgeDirection.UP)) {
				continue;
			} else {
				product = new ItemStack(harvestStacks[i].getItem().itemID, 1, 0);
			}

			if (doRemove && product != null) {
				decrStackSize(i, 1);
			}

			// if(!hasProperSoil() && tile.worldObj.rand.nextInt(100) < 25)
			// return new ItemStack(Block.dirt);
			// else
			return new ItemStack[] { product };
		}

		return new ItemStack[0];
	}

	@Override
	public boolean isWorking() {
		return true;
	}

	@Override
	public void getGUINetworkData(int i, int j) {
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
	}

}
