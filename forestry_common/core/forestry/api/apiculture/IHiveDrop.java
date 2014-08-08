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

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Bees can be seeded either as hive drops or as mutation results.
 * 
 * Add IHiveDrops to BeeManager.hiveDrops
 * 
 * @author SirSengir
 */
public interface IHiveDrop {

	ItemStack getPrincess(World world, int x, int y, int z, int fortune);

	Collection<ItemStack> getDrones(World world, int x, int y, int z, int fortune);

	Collection<ItemStack> getAdditional(World world, int x, int y, int z, int fortune);

	/**
	 * Chance to drop. Default drops have 80 (= 80 %).
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	int getChance(World world, int x, int y, int z);
}
