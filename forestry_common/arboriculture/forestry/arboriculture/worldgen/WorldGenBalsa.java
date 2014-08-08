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

public class WorldGenBalsa extends WorldGenTree {

	public WorldGenBalsa(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height;

		addLeaf(0, leafSpawn--, 0, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false);

		if (height > 10) {
			generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false);
		}

		leafSpawn--;

		while (leafSpawn > 6) {
			generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false);
			leafSpawn--;
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(6, 6);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log3.blockID, 3);
	}

}
