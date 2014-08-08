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
package forestry.core.utils;

import java.util.ArrayList;

import forestry.core.gadgets.TileForestry;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.oredict.OreDictionary;

public class StackUtils {

	/**
	 * Compares item id, damage and NBT. Accepts wildcard damage.
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static boolean isIdenticalItem(ItemStack lhs, ItemStack rhs) {
		if(lhs == null || rhs == null)
			return false;
		
		if(lhs.itemID != rhs.itemID)
			return false;
		
		if(lhs.getItemDamage() >= 0)
			if(lhs.getItemDamage() != rhs.getItemDamage())
				return false;
		
		return ItemStack.areItemStackTagsEqual(lhs, rhs);
	}
	
	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd) {
		return stowInInventory(itemstack, inventory, doAdd, 0, inventory.getSizeInventory());
	}

	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd, int slot1, int count) {

		boolean added = false;

		for (int i = slot1; i < slot1 + count; i++) {
			ItemStack inventoryStack = inventory.getStackInSlot(i);

			// Grab those free slots
			if (inventoryStack == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(i, itemstack.copy());
					itemstack.stackSize = 0;
				}
				return true;
			}

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			// Not same type
			if (!inventoryStack.isItemEqual(itemstack)) {
				continue;
			}
			if(!ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
				continue;
			}

			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;

			// Enough space to add all
			if (space > itemstack.stackSize) {
				if (doAdd) {
					inventoryStack.stackSize += itemstack.stackSize;
					itemstack.stackSize = 0;
				}
				return true;
				// Only part can be added
			} else {
				if (doAdd) {
					inventoryStack.stackSize = inventoryStack.getMaxStackSize();
					itemstack.stackSize -= space;
				}
				added = true;
			}

		}

		return added;

	}

	public static int addToInventory(ItemStack itemstack, IInventory inventory, boolean doAdd, int slot1, int count) {

		int added = 0;

		for (int i = slot1; i < slot1 + count; i++) {
			ItemStack inventoryStack = inventory.getStackInSlot(i);

			// Grab those free slots
			if (inventoryStack == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(i, itemstack.copy());
				}
				return itemstack.stackSize;
			}

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			// Not same type
			if (!inventoryStack.isItemEqual(itemstack)) {
				continue;
			}

			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;

			// Enough space to add all
			if (space > itemstack.stackSize - added) {
				if (doAdd) {
					inventoryStack.stackSize += itemstack.stackSize;
				}
				return itemstack.stackSize;
				// Only part can be added
			} else {
				if (doAdd) {
					inventoryStack.stackSize = inventoryStack.getMaxStackSize();
				}
				added += space;
			}

		}

		return added;

	}

	/**
	 * Merges the giving stack into the receiving stack as far as possible
	 * 
	 * @param giver
	 * @param receptor
	 */
	public static void mergeStacks(ItemStack giver, ItemStack receptor) {
		if (receptor.stackSize >= 64)
			return;

		if (!receptor.isItemEqual(giver))
			return;

		if (giver.stackSize <= (receptor.getMaxStackSize() - receptor.stackSize)) {
			receptor.stackSize += giver.stackSize;
			giver.stackSize = 0;
			return;
		}

		ItemStack temp = giver.splitStack(receptor.getMaxStackSize() - receptor.stackSize);
		receptor.stackSize += temp.stackSize;
		temp.stackSize = 0;
		return;
	}

	public static boolean freeSpaceInStack(ItemStack stack, int maxSize) {
		if (stack == null)
			return true;

		if (stack.stackSize >= maxSize)
			return false;

		return true;
	}

	public static boolean canAddToStack(ItemStack stack, ItemStack inventory) {
		if (inventory == null)
			return true;

		return stack.itemID == inventory.itemID;
	}

	/**
	 * Creates a split stack of the specified amount, preserving NBT data, without decreasing the source stack.
	 * 
	 * @param stack
	 * @param i
	 * @return
	 */
	public static ItemStack createSplitStack(ItemStack stack, int amount) {
		ItemStack split = new ItemStack(stack.itemID, amount, stack.getItemDamage());
		if (stack.getTagCompound() != null) {
			NBTTagCompound nbttagcompound = (NBTTagCompound) stack.getTagCompound().copy();
			split.setTagCompound(nbttagcompound);
		}
		return split;
	}

	public static ItemStack[] condenseStacks(ItemStack[] stacks) {
		ArrayList<ItemStack> condensed = new ArrayList<ItemStack>();

		for (ItemStack stack : stacks) {
			if (stack == null) {
				continue;
			}

			boolean matched = false;
			for (ItemStack cached : condensed)
				if (cached.isItemEqual(stack)) {
					cached.stackSize += stack.stackSize;
					matched = true;
				}

			if (!matched) {
				condensed.add(stack.copy());
			}

		}

		return condensed.toArray(new ItemStack[0]);
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 * 
	 * @param set
	 * @param stock
	 * @return
	 */
	public static int containsSets(ItemStack[] set, ItemStack[] stock) {
		return containsSets(set, stock, false);
	}

	public static int containsSets(ItemStack[] set, ItemStack[] stock, boolean oreDictionary) {
		int count = 0;

		ItemStack[] condensedRequired = StackUtils.condenseStacks(set);
		ItemStack[] condensedOffered = StackUtils.condenseStacks(stock);

		for (ItemStack req : condensedRequired) {

			boolean matched = false;
			for (ItemStack offer : condensedOffered) {

				boolean isEqual = false;
				if (oreDictionary) {
					isEqual = isItemOreEqual(req, offer);
				} else {
					isEqual = req.isItemEqual(offer);
				}

				if (isEqual) {

					matched = true;

					int stackCount = (int) Math.floor(offer.stackSize / req.stackSize);
					if (stackCount <= 0)
						return 0;
					else if (count == 0) {
						count = stackCount;
					} else if (count > stackCount) {
						count = stackCount;
					}
				}
			}
			if (!matched)
				return 0;
		}

		return count;
	}

	public static boolean isItemOreEqual(ItemStack base, ItemStack comparison) {
		if (base.isItemEqual(comparison))
			return true;

		ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
		for (ItemStack ore : copperIngots)
			if (ore.isItemEqual(base)) {
				for (ItemStack ore1 : copperIngots)
					if (ore1.isItemEqual(comparison))
						return true;
				return false;
			}

		ArrayList<ItemStack> tinIngots = OreDictionary.getOres("ingotTin");
		for (ItemStack ore : tinIngots)
			if (ore.isItemEqual(base)) {
				for (ItemStack ore1 : tinIngots)
					if (ore1.isItemEqual(comparison))
						return true;
				return false;
			}

		ArrayList<ItemStack> bronzeIngots = OreDictionary.getOres("ingotBronze");
		for (ItemStack ore : bronzeIngots)
			if (ore.isItemEqual(base)) {
				for (ItemStack ore1 : bronzeIngots)
					if (ore1.isItemEqual(comparison))
						return true;
				return false;
			}

		return false;
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, int x, int y, int z) {
		if (items.stackSize <= 0)
			return;

		float f1 = 0.7F;
		double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, items);
		entityitem.delayBeforeCanPickup = 10;

		world.spawnEntityInWorld(entityitem);

	}
	
	public static void replenishByContainer(TileForestry tile, ItemStack inventoryStack, TankSlot tank) {
		LiquidContainerData container = LiquidHelper.getLiquidContainer(inventoryStack);
		replenishByContainer(tile, inventoryStack, container, tank);
	}

	public static ItemStack replenishByContainer(TileForestry tile, ItemStack inventoryStack, LiquidContainerData container, TankSlot tank) {
		if (container == null)
			return inventoryStack;

		if (tank.fill(container.stillLiquid, false) >= container.stillLiquid.amount) {
			tank.fill(container.stillLiquid, true);
			if (container.filled != null && container.filled.getItem().hasContainerItem()) {
				inventoryStack = container.container.copy();
			} else {
				inventoryStack.stackSize--;
			}
			tile.sendNetworkUpdate();
		}

		return inventoryStack;
	}

}
