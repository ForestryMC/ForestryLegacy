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
package forestry.cultivation;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;

/**
 * Contains a few modifications for BlockHumus Creates a block of sand beneath the tree instead of a block of dirt
 * 
 * @author
 * 
 */
public class WorldGenFirTrees extends WorldGenTrees {

	private int metaWood = 0;
	private int metaLeaves = 0;

	public WorldGenFirTrees(boolean flag) {
		this(flag, 0, 0);
	}

	public WorldGenFirTrees(boolean flag, int metaWood, int metaLeaves) {
		super(flag);
		this.metaWood = metaWood;
		this.metaLeaves = metaLeaves;
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		int l;
		boolean flag;
		label0: {
			l = random.nextInt(3) + 4;
			flag = true;
			if (j >= 1) {
				world.getClass();
				if (j + l + 1 <= Defaults.WORLD_HEIGHT) {
					break label0;
				}
			}
			return false;
		}
		label1: {
			for (int i1 = j; i1 <= j + 1 + l; i1++) {
				byte byte0 = 1;
				if (i1 == j) {
					byte0 = 0;
				}
				if (i1 >= (j + 1 + l) - 2) {
					byte0 = 2;
				}
				for (int i2 = i - byte0; i2 <= i + byte0 && flag; i2++) {
					for (int l2 = k - byte0; l2 <= k + byte0 && flag; l2++) {
						if (i1 >= 0) {
							world.getClass();
							if (i1 < Defaults.WORLD_HEIGHT) {
								int j3 = world.getBlockId(i2, i1, l2);
								if (j3 != 0 && j3 != Block.leaves.blockID) {
									flag = false;
								}
								continue;
							}
						}
						flag = false;
					}
				}

			}

			if (!flag)
				return false;
			int j1 = world.getBlockId(i, j - 1, k);
			if (j1 == ForestryBlock.soil.blockID) {
				world.getClass();
				if (j < Defaults.WORLD_HEIGHT - l - 1) {
					break label1;
				}
			}
			return false;
		}
		setBlock(world, i, j - 1, k, Block.sand.blockID);
		for (int k1 = (j - 3) + l; k1 <= j + l; k1++) {
			int j2 = k1 - (j + l);
			int i3 = 1 - j2 / 2;
			for (int k3 = i - i3; k3 <= i + i3; k3++) {

				int l3 = k3 - i;
				for (int i4 = k - i3; i4 <= k + i3; i4++) {
					int j4 = i4 - k;
					if ((Math.abs(l3) != i3 || Math.abs(j4) != i3 || random.nextInt(2) != 0 && j2 != 0)
							&& !Block.opaqueCubeLookup[world.getBlockId(k3, k1, i4)]) {
						setBlockAndMetadata(world, k3, k1, i4, Block.leaves.blockID, this.metaLeaves);
					}
				}

			}

		}

		for (int l1 = 0; l1 < l; l1++) {
			int k2 = world.getBlockId(i, j + l1, k);
			if (k2 == 0 || k2 == Block.leaves.blockID) {
				setBlockAndMetadata(world, i, j + l1, k, Block.wood.blockID, this.metaWood);
				//generateFirefly(world, random, i, j + l1, k);

			}
		}

		return true;
	}

	/**
	 * Generates fireflys on every tenth log if the mod twilight forest is present.
	 * 
	 * @param world
	 * @param rand
	 * @param i
	 * @param j
	 * @param k
	 */
	/*
	protected void generateFirefly(World world, Random rand, int i, int j, int k) {
		if (PluginTwilightForest.firefly == null)
			return;

		// Only on every tenth log.
		if (rand.nextInt(100) > 10)
			return;

		int side = rand.nextInt(3);
		switch (side) {
		case 0:
			i++;
			break;
		case 1:
			i--;
			break;
		case 2:
			k++;
			break;
		case 3:
			k--;
			break;
		}

		setBlock(world, i, j, k, PluginTwilightForest.firefly.blockID);
	}
	*/
}
