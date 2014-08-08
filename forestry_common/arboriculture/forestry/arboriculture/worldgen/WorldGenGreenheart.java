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

public class WorldGenGreenheart extends WorldGenTree {

	public WorldGenGreenheart(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		
		generateTreeTrunk(height, girth, 0.4f);
		generateSupportStems(height, girth, 0.1f, 0.2f);

		int leafSpawn = height + 1;

		generateCylinder(new Vector(0f, leafSpawn--, 0f), 1, 1, leaf, false);
		generateCylinder(new Vector(0f, leafSpawn--, 0f), 1.5f, 1, leaf, false);

		generateCylinder(new Vector(0f, leafSpawn--, 0f), 2.5f, 1, leaf, false);
		
		while(leafSpawn > height - 4)
			generateCylinder(new Vector(0f, leafSpawn--, 0f), 2.9f, 1, leaf, false);
		generateCylinder(new Vector(0f, leafSpawn--, 0f), 2.5f, 1, leaf, false);
		generateCylinder(new Vector(0f, leafSpawn--, 0f), 1.5f, 1, leaf, false);

		// Add some smaller twigs below for flavour
		for (int times = 0; times < height / 4; times++) {
			int h = 10 + rand.nextInt(height - 10);
			if (rand.nextBoolean() && h < height / 2) {
				h = height / 2 + rand.nextInt(height / 2);
			}
			int x_off = -1 + rand.nextInt(3);
			int y_off = -1 + rand.nextInt(3);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(1), leaf, false);
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(10, 8);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log4.blockID, 2);
	}


}
