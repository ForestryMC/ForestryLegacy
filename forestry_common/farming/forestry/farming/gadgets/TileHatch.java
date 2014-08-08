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
package forestry.farming.gadgets;

import java.util.LinkedList;

import forestry.api.core.ITileStructure;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.plugins.PluginForestryFarming;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.inventory.ISpecialInventory;

public class TileHatch extends TileFarm implements ISpecialInventory, ISidedInventory {

	public static int TEXTURE_SHIFT = 80;
	
	public TileHatch() {
		textureShift = TEXTURE_SHIFT;
	}
	
	@Override
	public boolean hasFunction() {
		return true;
	}

	@Override
	protected void createInventory() {
	}

	protected void updateServerSide() {
		if(worldObj.getWorldTime() % 60 == 0)
			dumpStash();
	}
	
	/* AUTO-EJECTING */
	protected void dumpStash() {
		
		if(!hasMaster())
			return;
		
		ForgeDirection[] pipes = BlockUtil.getPipeDirections(worldObj, Coords(), ForgeDirection.UP);

		if (pipes.length > 0) {
			dumpToPipe(pipes);
		} else {
			IInventory[] inventories = BlockUtil.getAdjacentInventories(worldObj, Coords(), ForgeDirection.UP);
			dumpToInventory(inventories);
		}
	}

	private void dumpToPipe(ForgeDirection[] pipes) {

		ItemStack[] products = extractItem(true, ForgeDirection.DOWN, 1);
		for(int i = 0; i < products.length; i++) {
			while (products[i].stackSize > 0)
				BlockUtil.putFromStackIntoPipe(this, pipes, products[i]);
		}

	}

	private void dumpToInventory(IInventory[] inventories) {

		IInventory inv = getCentralTE().getInventory();

		for (int i = TileFarmPlain.SLOT_PRODUCTION_1; i < TileFarmPlain.SLOT_PRODUCTION_1 + TileFarmPlain.SLOT_COUNT_PRODUCTION; i++) {
			if (inv.getStackInSlot(i) == null) {
				continue;
			}

			ItemStack stack = inv.getStackInSlot(i);

			if (stack.stackSize <= 0) {
				continue;
			}

			for (int j = 0; j < inventories.length; j++) {

				// Don't dump in arboretums!
				if (inventories[j].getSizeInventory() < 4) {
					continue;
				}

				// Get complete inventory (for double chests)
				IInventory inventory = Utils.getChest(inventories[j]);

				StackUtils.stowInInventory(stack, inventory, true);
				if (stack.stackSize <= 0) {
					inv.setInventorySlotContents(i, null);
					break;
				}
			}
		}

	}

	
	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		if(this.hasMaster())
			return getCentralTE().getInventory().getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(this.hasMaster())
			return getCentralTE().getInventory().getStackInSlot(slotIndex);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if(this.hasMaster())
			return getCentralTE().getInventory().decrStackSize(slotIndex, amount);
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if(this.hasMaster())
			return getCentralTE().getInventory().getStackInSlotOnClosing(slotIndex);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		// Client side handling for container synch
		//if(inventory == null && !Proxies.common.isSimulating(worldObj))
		//	createInventory();
		
		if(this.hasMaster())
			getCentralTE().getInventory().setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		if(this.hasMaster())
			return getCentralTE().getInventory().getInventoryStackLimit();
		else
			return 0;
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

	/* ISIDEDINVENTORY */
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		switch(side) {
		case UP:
			return TileFarmPlain.SLOT_GERMLINGS_1;
		case DOWN:
			return TileFarmPlain.SLOT_PRODUCTION_1;
		case NORTH:
		case SOUTH:
			return TileFarmPlain.SLOT_RESOURCES_1;
		case WEST:
		case EAST:
			return TileFarmPlain.SLOT_FERTILIZER;
		default:
			return 0;
		}
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		switch(side) {
		case UP:
			return TileFarmPlain.SLOT_COUNT_RESERVOIRS;
		case DOWN:
			return TileFarmPlain.SLOT_COUNT_PRODUCTION;
		case NORTH:
		case SOUTH:
			return TileFarmPlain.SLOT_COUNT_RESERVOIRS;
		case WEST:
		case EAST:
			return 1;
		default:
			return getSizeInventory();
		}
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		if(!hasMaster())
			return 0;
		
		ITileStructure struct = getCentralTE();
		if(!(struct instanceof TileFarmPlain))
			return 0;
		
		TileFarmPlain housing = (TileFarmPlain)struct;
		GenericInventoryAdapter inv = (GenericInventoryAdapter)housing.getInventory();
		if(housing.acceptsAsFertilizer(stack)) {
			return inv.addStack(stack, TileFarmPlain.SLOT_FERTILIZER, 1, false, doAdd);
		} else if (housing.acceptsAsResource(stack)) {
			return inv.addStack(stack, TileFarmPlain.SLOT_RESOURCES_1, TileFarmPlain.SLOT_COUNT_RESERVOIRS, false, doAdd);
		} else if (housing.acceptsAsGermling(stack)) {
			return inv.addStack(stack, TileFarmPlain.SLOT_GERMLINGS_1, TileFarmPlain.SLOT_COUNT_RESERVOIRS, false, doAdd);
		}	
		
		return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		
		IInventory inv;
		if (hasMaster()) {
			inv = getCentralTE().getInventory();
		} else
			return new ItemStack[0];

		ItemStack product = null;

		for (int i = TileFarmPlain.SLOT_PRODUCTION_1; i < TileFarmPlain.SLOT_PRODUCTION_1 + TileFarmPlain.SLOT_COUNT_PRODUCTION; i++) {
			if (inv.getStackInSlot(i) == null) {
				continue;
			}

			ItemStack stack = inv.getStackInSlot(i);

			if (doRemove) {
				product = inv.decrStackSize(i, 1);
			} else {
				product = stack.copy();
				product.stackSize = 1;
			}
			break;
		}

		if(product != null)
			return new ItemStack[] { product };
		else
			return new ItemStack[0];
	}

	/* ITRIGGERPROVIDER */
	public LinkedList<ITrigger> getCustomTriggers() {
		if(!hasMaster())
			return null;
		
		LinkedList<ITrigger> list = new LinkedList<ITrigger>();
		list.add(PluginForestryFarming.lowResourceLiquid50);
		list.add(PluginForestryFarming.lowResourceLiquid25);
		list.add(PluginForestryFarming.lowSoil128);
		list.add(PluginForestryFarming.lowSoil64);
		list.add(PluginForestryFarming.lowSoil32);
		list.add(PluginForestryFarming.lowFertilizer50);
		list.add(PluginForestryFarming.lowFertilizer25);
		return list;
	}

}
