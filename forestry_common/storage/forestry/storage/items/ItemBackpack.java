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
package forestry.storage.items;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import forestry.api.core.ForestryAPI;
import forestry.api.storage.BackpackStowEvent;
import forestry.api.storage.IBackpackDefinition;
import forestry.apiculture.gadgets.MachineApiaristChest;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IInventoriedItem;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Utils;
import forestry.storage.BackpackMode;

public class ItemBackpack extends ItemForestry implements IInventoriedItem {

	IBackpackDefinition info;
	byte type;

	public ItemBackpack(int i, IBackpackDefinition info, int type) {
		super(i);
		this.info = info;
		this.type = (byte)type;
		setMaxStackSize(1);
	}

	public IBackpackDefinition getDefinition() {
		return info;
	}
	
	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {

		if (!Proxies.common.isSimulating(world))
			return itemstack;

		if (!player.isSneaking())
			openGui(player, itemstack);
		else
			switchMode(itemstack);
		return itemstack;

	}

	@Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(getInventoryHit(itemstack, player, world, x, y, z) != null)
			return true;
		else
			return false;
    }

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		if (!Proxies.common.isSimulating(world))
			return false;

		// We only do this when shift is clicked
		if (!player.isSneaking()) {
			return false;
		}

		return evaluateTileHit(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	public ItemStack tryStowing(EntityPlayer player, ItemStack backpackStack, ItemStack itemstack) {

		ItemBackpack backpack = ((ItemBackpack)backpackStack.getItem());
		ItemInventory inventory = new ItemInventory(backpack.getBackpackSize(), backpackStack);
		if (backpackStack.getItemDamage() == 1)
			return itemstack;

		Event event = new BackpackStowEvent(
				player,
				backpack.getDefinition(),
				inventory,
				itemstack
				);
		MinecraftForge.EVENT_BUS.post(event);
		if(itemstack.stackSize <= 0)
			return null;
		if(event.isCanceled())
			return itemstack;
		
		int freeSlots = 0;
		int slot = -1;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {

			ItemStack slotStack = inventory.getStackInSlot(i);

			// We do not add at once to free inventorySlots.
			if (slotStack == null) {
				freeSlots++;
				if (slot < 0) {
					slot = i;
				}
				continue;
			}

			if (slotStack.isItemEqual(itemstack)
					&& ItemStack.areItemStackTagsEqual(slotStack, itemstack)) {
				int space = slotStack.getMaxStackSize() - slotStack.stackSize;
				if (space <= 0) {
					continue;
				}

				// Only partially used, continue to see if other slots have
				// still space
				if (space < itemstack.stackSize) {
					slotStack.stackSize = slotStack.getMaxStackSize();
					itemstack.stackSize -= space;
					continue;
				}

				// Consumed, return null
				slotStack.stackSize += itemstack.stackSize;
				itemstack.stackSize = 0;
				inventory.save();
				return null;
			}
		}

		// Now let's check if there is at least one free slot available since we
		// couldn't add everything to already occupied slots
		if (freeSlots <= 0) {
			inventory.save();
			return itemstack;
		}

		inventory.setInventorySlotContents(slot, itemstack.copy());
		itemstack.stackSize = 0;
		inventory.save();
		return null;
	}

	private void switchMode(ItemStack itemstack) {
		BackpackMode mode = getMode(itemstack);
		if (mode == BackpackMode.RESUPPLY) {
			itemstack.setItemDamage(0);
		} else if (mode == BackpackMode.RECEIVE) {
			itemstack.setItemDamage(3);
		} else if (mode == BackpackMode.LOCKED) {
			itemstack.setItemDamage(2);
		} else {
			itemstack.setItemDamage(1);
		}
	}
	
	private MovingObjectPosition getPlayerTarget(EntityPlayer player) {
        double distance = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
        Vec3 posVec = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        Vec3 lookVec = player.getLook(1);
        posVec.yCoord += player.getEyeHeight();
        lookVec = posVec.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
        return player.worldObj.rayTraceBlocks(posVec, lookVec);
	}
	
	private IInventory getInventoryHit(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z) {
		
		IInventory inventory = null;
		
		MovingObjectPosition movingobjectposition = getPlayerTarget(player);
		
		if (movingobjectposition != null && movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {
			int i = movingobjectposition.blockX;
			int j = movingobjectposition.blockY;
			int k = movingobjectposition.blockZ;
			TileEntity targeted = world.getBlockTileEntity(i, j, k);

			if (targeted instanceof TileEntityChest) {
				inventory = Utils.getChest((IInventory) targeted);
			} else if (targeted instanceof MachineApiaristChest) {
				if (((ItemBackpack) ForestryItem.apiaristBackpack).isBackpack(itemstack)) {
					inventory = (IInventory) targeted;
				}
			} else if (targeted instanceof IInventory) {
				IInventory test = (IInventory) targeted;
				if (test.getSizeInventory() > 26) {
					inventory = test;
				}
			}
		}

		return inventory;
	}
	
	private boolean evaluateTileHit(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		
		// Shift right-clicking on an inventory tile will attempt to transfer
		// items contained in the backpack
		IInventory inventory = getInventoryHit(itemstack, player, world, x, y, z);
		// Process only inventories
		if (inventory != null) {

			// Must have inventory slots
			if (inventory.getSizeInventory() <= 0)
				return true;

			// Create our own backpack inventory
			ItemInventory backpackInventory = new ItemInventory(getBackpackSize(), itemstack);

			if (itemstack.getItemDamage() == 2) {
				tryChestReceive(player, backpackInventory, inventory);
			} else {
				tryChestTransfer(backpackInventory, inventory);
			}

			backpackInventory.save();

			return true;
		}
		
		return false;
	}
	
	private void tryChestTransfer(ItemInventory backpackInventory, IInventory inventory) {

		for (int l = 0; l < backpackInventory.getSizeInventory(); l++) {
			ItemStack packstack = backpackInventory.getStackInSlot(l);
			if (packstack == null) {
				continue;
			}

			// Check for an available target slot
			for (int m = 0; m < inventory.getSizeInventory(); m++) {
				ItemStack targetstack = inventory.getStackInSlot(m);

				// Free stack, add all
				if (targetstack == null) {
					inventory.setInventorySlotContents(m, packstack.copy());
					backpackInventory.setInventorySlotContents(l, null);
					break;
				}

				// Check if item is equal
				if (!targetstack.isItemEqual(packstack)
						|| !ItemStack.areItemStackTagsEqual(targetstack, packstack)) {
					continue;
				}

				int space = targetstack.getMaxStackSize() - targetstack.stackSize;
				// Not enough space, skip
				if (space <= 0) {
					continue;
				}

				// Enough space for all
				if (space >= packstack.stackSize) {
					targetstack.stackSize += packstack.stackSize;
					backpackInventory.decrStackSize(l, packstack.stackSize);
					break;
				}

				// Space for some
				targetstack.stackSize = targetstack.getMaxStackSize();
				backpackInventory.decrStackSize(l, space);

			}
		}

	}

	private void tryChestReceive(EntityPlayer player, ItemInventory backpackInventory, IInventory inventory) {

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = inventory.getStackInSlot(i);
			if (inventoryStack == null) {
				continue;
			}

			// Check validity for this backpack
			if (!info.isValidItem(player, inventoryStack)) {
				continue;
			}

			// Check for an available backpack slot
			for (int j = 0; j < backpackInventory.getSizeInventory(); j++) {
				ItemStack backpackStack = backpackInventory.getStackInSlot(j);

				// Slot is empty, add all
				if (backpackStack == null) {
					backpackInventory.setInventorySlotContents(j, inventoryStack.copy());
					inventory.setInventorySlotContents(i, null);
					break;
				}

				if (!backpackStack.isItemEqual(inventoryStack)
						|| !ItemStack.areItemStackTagsEqual(inventoryStack, backpackStack)) {
					continue;
				}

				int space = backpackStack.getMaxStackSize() - backpackStack.stackSize;
				// Not enough space, skip
				if (space <= 0) {
					continue;
				}

				// Enough space for all
				if (space >= inventoryStack.stackSize) {
					backpackStack.stackSize += inventoryStack.stackSize;
					inventory.decrStackSize(i, inventoryStack.stackSize);
					break;
				}

				// Space for some
				backpackStack.stackSize = backpackStack.getMaxStackSize();
				inventory.decrStackSize(i, space);
			}
		}

	}

	public void openGui(EntityPlayer entityplayer, ItemStack itemstack) {
		if (getBackpackSize() == Defaults.SLOTS_BACKPACK_DEFAULT) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.BackpackGUI.ordinal(), entityplayer.worldObj, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		} else if (getBackpackSize() == Defaults.SLOTS_BACKPACK_T2) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.BackpackT2GUI.ordinal(), entityplayer.worldObj, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}
	}

	public boolean isBackpack(ItemStack stack) {
		if (stack == null)
			return false;

		return stack.itemID == this.itemID;
	}

	public Collection<ItemStack> getValidItems(EntityPlayer player) {
		return info.getValidItems(player);
	}

	public int getBackpackSize() {
		return getSlotsForType(type);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		ItemInventory inventory = new ItemInventory(getBackpackSize(), itemstack);

		// HashMap<ItemStack, Integer> contents = new HashMap<ItemStack,
		// Integer>();
		int occupied = 0;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (inventory.getStackInSlot(i).stackSize <= 0) {
				continue;
			}

			// Count the slot as occupied
			occupied++;
		}

		BackpackMode mode = getMode(itemstack);
		if (mode == BackpackMode.LOCKED) {
			list.add("(LOCKED)");
		} else if (mode == BackpackMode.RECEIVE) {
			list.add("(RECEIVING)");
		} else if (mode == BackpackMode.RESUPPLY) {
			list.add("(RESUPPLY)");
		}
		list.add(occupied + "/" + getBackpackSize() + " " + StringUtil.localize("gui.slots"));

	}

	// Return true to enable color overlay - client side only
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		if (type > 1)
			return StringUtil.localize("storage.backpack.t2adj") + " " + info.getName();
		else
			return info.getName();
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0)
			return info.getPrimaryColour();
		else
			return info.getSecondaryColour();
	}

	@Override
	public int getIconFromDamageForRenderPass(int i, int j) {

		int iconIndex = 0;

		if (j == 0) {
			iconIndex = 112;
		} else {
			iconIndex = 113 + type;
		}

		if (i > 2)
			return iconIndex + 48;
		else if (i > 1)
			return iconIndex + 32;
		else if (i > 0)
			return iconIndex + 16;
		else
			return iconIndex;
	}

	public static int getSlotsForType(int type) {
		switch (type) {
		case 0:
			return Defaults.SLOTS_BACKPACK_APIARIST;
		case 2:
			return Defaults.SLOTS_BACKPACK_T2;
		case 1:
		default:
			return Defaults.SLOTS_BACKPACK_DEFAULT;
		}
	}
	
	public static BackpackMode getMode(ItemStack backpack) {
		int meta = backpack.getItemDamage();

		if (meta >= 3)
			return BackpackMode.RESUPPLY;
		else if (meta >= 2)
			return BackpackMode.RECEIVE;
		else if (meta >= 1)
			return BackpackMode.LOCKED;
		else
			return BackpackMode.NORMAL;
	}

}
