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
package forestry.api.apiculture;

import net.minecraft.item.ItemStack;

public interface IBeeListener {
	
	/**
	 * Called on queen update.
	 * 
	 * @param queen
	 */
	void onQueenChange(ItemStack queen);
	
	/**
	 * Called when the bees wear out the housing's equipment.
	 * 
	 * @param amount
	 *            Integer indicating the amount worn out.
	 */
	void wearOutEquipment(int amount);
	
	/**
	 * Called just before the children are generated, and the queen removed.
	 * 
	 * @param queen
	 */
	void onQueenDeath(IBee queen);
	
	/**
	 * Called after the children have been spawned, but before the queen appears
	 * 
	 * @param queen
	 */
	void onPostQueenDeath(IBee queen);

}
