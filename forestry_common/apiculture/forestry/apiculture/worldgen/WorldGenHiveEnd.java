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
package forestry.apiculture.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenHiveEnd extends WorldGenHive {

	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(i, k);
		if (biome.biomeID != BiomeGenBase.sky.biomeID)
			return false;

		int i1 = (i + random.nextInt(8)) - random.nextInt(8);
		int j1 = (j + random.nextInt(4)) - random.nextInt(4);
		int k1 = (k + random.nextInt(8)) - random.nextInt(8);
		if (world.isAirBlock(i1, j1, k1) && world.isAirBlock(i1, j1 + 1, k1) && world.getBlockId(i1, j1 - 1, k1) == Block.whiteStone.blockID) {
			setHive(world, i1, j1, k1, 5);
			return true;
		}

		return false;
	}

}
