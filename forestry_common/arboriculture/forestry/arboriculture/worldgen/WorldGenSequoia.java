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
package forestry.arboriculture.worldgen;

import forestry.api.world.ITreeGenData;
import forestry.core.config.ForestryBlock;
import forestry.core.worldgen.BlockType;

public class WorldGenSequoia extends WorldGenTree {

	public WorldGenSequoia(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int topLength = height / 4;

		int topHeight = height - topLength + rand.nextInt(height / 4);

		int leafSpawn = height + 2;

		generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 2, 1, leaf, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 3, 1, leaf, false);

		while (leafSpawn > topHeight) {
			generateCylinder(new Vector(0, leafSpawn--, 0), 4, 1, leaf, false);
		}

		generateCylinder(new Vector(0, leafSpawn--, 0), 3, 1, leaf, false);

		for (int times = 0; times < height / 4; times++) {
			int h = 15 + rand.nextInt(height - 10);
			if (rand.nextBoolean() && h < height / 2) {
				h = height / 2 + rand.nextInt(height / 2);
			}
			int x_off = -1 + rand.nextInt(3);
			int y_off = -1 + rand.nextInt(3);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(2), leaf, false);
		}

		/*
		 * 
		 * int leafSpawn = height;
		 * 
		 * addLeaf(0, leafSpawn--, 0, false);
		 * 
		 * generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false);
		 * 
		 * if(height > 10) { generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false); }
		 * 
		 * leafSpawn--;
		 * 
		 * while(leafSpawn > 6) { generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false); leafSpawn--; }
		 */

	}

	@Override
	public void preGenerate() {
		height = determineHeight(20, 5);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log2.blockID, 3);
	}

}
