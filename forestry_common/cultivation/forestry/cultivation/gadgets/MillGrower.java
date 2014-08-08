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
package forestry.cultivation.gadgets;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.core.EnumErrorCode;
import forestry.core.gadgets.Mill;
import forestry.core.gadgets.TileMill;
import forestry.core.proxy.Proxies;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Vect;
import forestry.cultivation.Overgrowth;

public abstract class MillGrower extends Mill {

	public final ItemStack catalyst;
	private ArrayList<Integer> validCropIds = new ArrayList<Integer>();
	private ArrayList<Overgrowth> validOvergrowth = new ArrayList<Overgrowth>();

	public MillGrower(TileMill machine, ItemStack catalyst) {
		super(machine);
		tileMill = machine;
		this.catalyst = catalyst;
		inventory = new TileInventoryAdapter(machine, 1, "Items");
	}

	protected void putOvergrowth(Overgrowth overgrowth) {
		validCropIds.add(overgrowth.crop.itemID);
		validOvergrowth.add(overgrowth);
	}

	public boolean hasOvergrowthByCropId(int cropid) {
		return validCropIds.contains(cropid);
	}

	public Overgrowth getOvergrowthByCrop(ItemStack crop) {
		for (Overgrowth growth : validOvergrowth)
			if (growth.hasCrop(crop))
				return growth;
		return null;
	}

	public abstract void growCrop(World world, int cropId, Vect pos);

	// private ItemStack[] fuelStacks = new ItemStack[1];
	private TileInventoryAdapter inventory;

	private Vect area = new Vect(21, 3, 21);
	private Vect posOffset = new Vect(-10, -1, -10);
	private Vect posCurrent = new Vect(0, 0, 0);
	private boolean isFinished = false;

	/**
	 * Read saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		inventory.readFromNBT(nbttagcompound);
		isFinished = nbttagcompound.getBoolean("IsFinished");

		charge = nbttagcompound.getInteger("Charge");
		progress = nbttagcompound.getFloat("Progress");
		stage = nbttagcompound.getInteger("Stage");
	}

	/**
	 * Write save data
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		inventory.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("IsFinished", isFinished);

		nbttagcompound.setInteger("Charge", charge);
		nbttagcompound.setFloat("Progress", progress);
		nbttagcompound.setInteger("Stage", stage);
	}

	// Inventory Managment
	/**
	 * Returns the next free catalyst slot.
	 * 
	 * @return
	 */
	private int getFreeCatalystSlot() {
		for (int i = 0; i < 1; i++) {
			if (inventory.getStackInSlot(i) == null)
				return i;

			if (inventory.getStackInSlot(i).isItemEqual(catalyst) && inventory.getStackInSlot(i).stackSize < inventory.getStackInSlot(i).getMaxStackSize())
				return i;
		}

		return -1;
	}

	// Progress managment
	public void resetProgress() {
		isFinished = false;
		posCurrent = new Vect(0, 0, 0);
	}

	private void advanceProgress() {
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

	@Override
	public void activate() {
		float f = tile.xCoord + 0.5F;
		float f1 = tile.yCoord + 0.0F + (tile.worldObj.rand.nextFloat() * 6F) / 16F;
		float f2 = tile.zCoord + 0.5F;
		float f3 = 0.52F;
		float f4 = tile.worldObj.rand.nextFloat() * 0.6F - 0.3F;

		Proxies.common.addEntityBiodustFX(tile.worldObj, (f - f3), f1, (f2 + f4), 0F, 0F, 0F);
		Proxies.common.addEntityBiodustFX(tile.worldObj, (f + f3), f1, (f2 + f4), 0F, 0F, 0F);
		Proxies.common.addEntityBiodustFX(tile.worldObj, (f + f4), f1, (f2 - f3), 0F, 0F, 0F);
		Proxies.common.addEntityBiodustFX(tile.worldObj, (f + f4), f1, (f2 + f3), 0F, 0F, 0F);

		if (Proxies.common.isSimulating(tile.worldObj)) {
			catalyze();
			charge = 0;
			tileMill.sendNetworkUpdate();
		}
	}

	public void catalyze() {

		// Actual work logic
		while (!isFinished && canCatalyze()) {
			advanceProgress();
			Vect posBlock = posCurrent.add(tile.Coords());
			posBlock = posBlock.add(posOffset);
			int blockid = tile.worldObj.getBlockId(posBlock.x, posBlock.y, posBlock.z);
			if (hasOvergrowthByCropId(blockid))
				if (applyCatalyst(posBlock)) {
					// We successfully applied the catalyst, it was used up.
					decrStackSize(0, 1);
					break;
				}
		}

		// Reset working area if we finished this cycle
		if (isFinished) {
			resetProgress();
		}

	}

	/**
	 * Called by the power provider when enough energy is available.
	 */
	@Override
	public boolean doWork() {

		if (!Proxies.common.isSimulating(tile.worldObj))
			return false;

		if (charge != 0)
			return false;

		if (canCatalyze()) {
			setErrorState(EnumErrorCode.OK);
			charge = 1;
		} else {
			setErrorState(EnumErrorCode.NORESOURCE);
		}

		return true;
	}

	@Override
	public boolean isWorking() {
		return charge != 0 || canCatalyze();
	}

	/**
	 * Returns true if catalyzer is available.
	 * 
	 * @return
	 */
	private boolean canCatalyze() {
		if (inventory.getStackInSlot(0) == null)
			return false;

		// Need catalyst
		if (!inventory.getStackInSlot(0).isItemEqual(catalyst))
			return false;

		if (inventory.getStackInSlot(0).stackSize > 0)
			return true;

		return false;
	}

	/**
	 * Trys to apply catalyst to a sapling
	 * 
	 * @param posBlock
	 * @return True if tree was successfully grown, false if failed.
	 */
	private boolean applyCatalyst(Vect posBlock) {
		int cropId = tile.worldObj.getBlockId(posBlock.x, posBlock.y, posBlock.z);
		int cropMeta = tile.worldObj.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);

		// Can only try to grow saplings
		Overgrowth growth = getOvergrowthByCrop(new ItemStack(cropId, 1, cropMeta));
		if (growth == null)
			return false;

		growCrop(tile.worldObj, cropId, posBlock);

		// Now check the result
		int blockid = tile.worldObj.getBlockId(posBlock.x, posBlock.y, posBlock.z);
		int meta = tile.worldObj.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);
		if (growth.hasRipe(new ItemStack(blockid, 1, meta)))
			return true;
		else
			return false;
	}

	// IINVENTORY IMPLEMENTATION
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

	// ISPECIALINVENTORY IMPLEMENTATION
	/**
	 * Only catalyst can be piped into a forester.
	 */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		// We only accept vialCatalyst
		if (stack.isItemEqual(catalyst)) {
			int slot = getFreeCatalystSlot();
			// No free slot for this type!
			if (slot < 0)
				return 0;

			return inventory.addStack(stack, false, doAdd);
		}

		return 0;
	}
}
