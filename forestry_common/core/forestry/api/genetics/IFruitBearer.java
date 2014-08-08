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
package forestry.api.genetics;

import java.util.Collection;

import net.minecraft.item.ItemStack;

public interface IFruitBearer {

	/**
	 * @return true if the actual tile can bear fruits.
	 */
	boolean hasFruit();
	/**
	 * @return Family of the potential fruits on this tile.
	 */
	IFruitFamily getFruitFamily();
	/**
	 * Picks the fruits of this tile, resetting it to unripe fruits. 
	 * @param tool Tool used in picking the fruits. May be null.
	 * @return Picked fruits.
	 */
	Collection<ItemStack> pickFruit(ItemStack tool);
	/**
	 * @return float indicating the ripeness of the fruit with >= 1.0f indicating full ripeness.
	 */
	float getRipeness();
	/**
	 * Increases the ripeness of the fruit.
	 * @param add Float to add to the ripeness. Will truncate to valid values.
	 */
	void addRipeness(float add);
}
