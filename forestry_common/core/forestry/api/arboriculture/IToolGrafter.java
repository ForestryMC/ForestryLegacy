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
package forestry.api.arboriculture;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IToolGrafter {
	/**
	 * Called by leaves to determine the increase in sapling droprate.
	 * @param stack
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	float getSaplingModifier(ItemStack stack, World world, int x, int y, int z);
}
