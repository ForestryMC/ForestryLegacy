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
package forestry.core.gui;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Slot which only specific items. (With permission by RichardG.)
 * 
 * @author Richard
 */
public class SlotCustom extends SlotForestry {
	protected Object[] items;
	private boolean exclusion;

	protected SlotCustom(IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
	}

	public SlotCustom(IInventory iinventory, ArrayList items, int i, int j, int k) {
		this(iinventory, items.toArray(), i, j, k);
	}

	public SlotCustom(IInventory iinventory, Collection items, int i, int j, int k) {
		this(iinventory, items.toArray(), i, j, k);
	}

	public SlotCustom(IInventory iinventory, Object[] items, int i, int j, int k, boolean exclusion) {
		this(iinventory, items, i, j, k);
		this.exclusion = exclusion;
	}

	public SlotCustom(IInventory iinventory, Object[] items, int i, int j, int k) {
		super(iinventory, i, j, k);
		this.items = items;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		if (exclusion)
			return !determineValidity(itemstack);
		else
			return determineValidity(itemstack);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean determineValidity(ItemStack itemstack) {
		for (Object o : items) {
			if (o == null) {
				continue;
			}
			if (o instanceof Class) {
				if (itemstack.itemID < Block.blocksList.length && Block.blocksList[itemstack.itemID] != null
						&& ((Class) o).isAssignableFrom(Block.blocksList[itemstack.itemID].getClass()))
					return true;
				if ((itemstack.itemID >= Block.blocksList.length || Block.blocksList[itemstack.itemID] == null) && Item.itemsList[itemstack.itemID] != null
						&& ((Class) o).isAssignableFrom(Item.itemsList[itemstack.itemID].getClass()))
					return true;
			} else if (o instanceof ItemStack) {
				if (((ItemStack) o).getItemDamage() < 0 && itemstack.itemID == ((ItemStack) o).itemID)
					return true;
				else if (itemstack.isItemEqual((ItemStack) o))
					return true;
			} else if (o instanceof Block && itemstack.itemID == ((Block) o).blockID)
				return true;
			else if (o instanceof Item && itemstack.itemID == ((Item) o).itemID)
				return true;
			else if (o instanceof Integer && itemstack.itemID == (Integer) o)
				return true;
		}
		return false;
	}
}
