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
package forestry.api.storage;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBackpackDefinition {

	/**
	 * @return A unique string identifier
	 */
	String getKey();

	/**
	 * @return Human-readable name of the backpack.
	 */
	String getName();

	/**
	 * @return Primary colour for the backpack icon.
	 */
	int getPrimaryColour();

	/**
	 * @return Secondary colour for backpack icon.
	 */
	int getSecondaryColour();

	/**
	 * Adds an item as valid for this backpack.
	 * @param validItem
	 */
	void addValidItem(ItemStack validItem);

	/**
	 * Returns an arraylist of all items valid for this backpack type.
	 * @param player
	 * @return
	 */
	Collection<ItemStack> getValidItems(EntityPlayer player);

	/**
	 * Returns true if the itemstack is a valid item for this backpack type.
	 * @param player
	 * @param itemstack
	 * @return
	 */
	boolean isValidItem(EntityPlayer player, ItemStack itemstack);

}
