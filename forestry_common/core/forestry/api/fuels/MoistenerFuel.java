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
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class MoistenerFuel {
	/**
	 * The item to use
	 */
	public final ItemStack item;
	/**
	 * The item that leaves the moistener's working slot (i.e. mouldy wheat, decayed wheat, mulch)
	 */
	public final ItemStack product;
	/**
	 * How much this item contributes to the final product of the moistener (i.e. mycelium)
	 */
	public final int moistenerValue;
	/**
	 * What stage this product represents. Resources with lower stage value will be consumed first.
	 */
	public final int stage;

	public MoistenerFuel(ItemStack item, ItemStack product, int stage, int moistenerValue) {
		this.item = item;
		this.product = product;
		this.stage = stage;
		this.moistenerValue = moistenerValue;
	}
}
