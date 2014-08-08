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
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package forestry.farming.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import forestry.core.config.Defaults;

// Referenced classes of package net.minecraft.src:
//            WorldGenerator, World, Block, BlockLeaves,
//            BlockGrass, BlockMycelium, BlockFlower

public class WorldGenBigMushroom extends WorldGenerator {
	private int mushroomType;

	public WorldGenBigMushroom(int i) {
		mushroomType = i;
	}

	public WorldGenBigMushroom() {
		mushroomType = -1;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		int type = random.nextInt(2);
		if (mushroomType >= 0) {
			type = mushroomType;
		}

		int height = random.nextInt(1) + 2;
		boolean flag = true;
		if (y < 1 || y + height + 1 > Defaults.WORLD_HEIGHT)
			return false;

		for (int i = y; i <= y + 1 + height; i++) {
			byte offset = 3;
			if (i == y) {
				offset = 0;
			}

			for (int j = x - offset; j <= x + offset && flag; j++) {
				for (int k = z - offset; k <= z + offset && flag; k++)
					if (i >= 0 && i < Defaults.WORLD_HEIGHT) {
						int blockid = world.getBlockId(j, i, k);
						if (blockid != 0 && blockid != Block.leaves.blockID) {
							flag = false;
						}
					} else {
						flag = false;
					}
			}
		}

		if (!flag)
			return false;

		int groundid = world.getBlockId(x, y - 1, z);
		if (groundid != Block.mycelium.blockID)
			return false;

		if (!Block.mushroomBrown.canPlaceBlockAt(world, x, y, z))
			return false;

		setBlock(world, x, y - 1, z, Block.dirt.blockID);

		int capStartY = y + height;
		if (type == 1) {
			capStartY = (y + height) - 1;
		}

		for (int i = capStartY; i <= y + height; i++) {

			int capRad = 1;

			if (type == 0) {
				capRad = 1;
			}

			for (int j = x - capRad; j <= x + capRad; j++) {
				for (int k = z - capRad; k <= z + capRad; k++) {
					int remain = 5;
					if (j == x - capRad) {
						remain--;
					}

					if (j == x + capRad) {
						remain++;
					}

					if (k == z - capRad) {
						remain -= 3;
					}

					if (k == z + capRad) {
						remain += 3;
					}

					if (type == 0 || i < y + height) {

						if (j == x - (capRad - 1) && k == z - capRad) {
							remain = 1;
						}

						if (j == x - capRad && k == z - (capRad - 1)) {
							remain = 1;
						}

						if (j == x + (capRad - 1) && k == z - capRad) {
							remain = 3;
						}

						if (j == x + capRad && k == z - (capRad - 1)) {
							remain = 3;
						}

						if (j == x - (capRad - 1) && k == z + capRad) {
							remain = 7;
						}

						if (j == x - capRad && k == z + (capRad - 1)) {
							remain = 7;
						}

						if (j == x + (capRad - 1) && k == z + capRad) {
							remain = 9;
						}

						if (j == x + capRad && k == z + (capRad - 1)) {
							remain = 9;
						}

					}
					if (remain == 5 && i < y + height) {
						remain = 0;
					}

					if ((remain != 0 || y >= (y + height) - 1) && !Block.opaqueCubeLookup[world.getBlockId(j, i, k)]) {
						setBlockAndMetadata(world, j, i, k, Block.mushroomCapBrown.blockID + type, remain);
					}

				}
			}

		}

		for (int i = 0; i < height; i++) {
			int blockid = world.getBlockId(x, y + i, z);
			if (!Block.opaqueCubeLookup[blockid]) {
				setBlockAndMetadata(world, x, y + i, z, Block.mushroomCapBrown.blockID + type, 10);
			}
		}

		return true;
	}
}
