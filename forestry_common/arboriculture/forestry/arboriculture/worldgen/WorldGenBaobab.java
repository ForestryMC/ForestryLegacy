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

public class WorldGenBaobab extends WorldGenTree {

	public WorldGenBaobab(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height - 1, girth);
		
		if(rand.nextFloat() < 0.3f)
			generateCylinder(new Vector(0, height - 1, 0), girth, 1, wood, false);
		else if(rand.nextBoolean())
			generateCylinder(new Vector(0, height - 1, 0), girth - 1, 1, wood, false);

		int leafSpawn = height + 1;

		generateCylinder(new Vector(0, leafSpawn--, 0), 5f, 1, leaf, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 4.5f, 1, leaf, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 4f, 1, leaf, false);

		// Add tree top
		for (int times = 0; times < height / 2; times++) {
			int h = height - 1 + rand.nextInt(4);
			if (rand.nextBoolean() && h < height / 2) {
				h = height / 2 + rand.nextInt(height / 2);
			}

			int x_off = -girth + rand.nextInt(2*girth);
			int y_off = -girth + rand.nextInt(2*girth);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(girth - 1), leaf, false);
		}

		// Add some smaller twigs below for flavour
		for (int times = 0; times < height / 4; times++) {
			int delim = modifyByHeight(6, 0, height);
			int h = delim + (delim < height ? rand.nextInt(height - delim) : 0);
			if (rand.nextBoolean() && h < height / 2) {
				h = height / 2 + rand.nextInt(height / 2);
			}
			int x_off = -1 + rand.nextInt(3);
			int y_off = -1 + rand.nextInt(3);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(2), leaf, false);
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(6, 6);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log2.blockID, 2);
	}

}
