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

public class WorldGenMahogany extends WorldGenTree {

	public WorldGenMahogany(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth, 0.6f);
		generateSupportStems(height, girth, 0.4f, 0.4f);
		
		int leafSpawn = height + 1;

		generateCylinder(new Vector(0.5f, leafSpawn--, 0.5f), 1, 1, leaf, false);

		generateCylinder(new Vector(0.5f, leafSpawn--, 0.5f), 3.5f, 1, leaf, false);
		generateCylinder(new Vector(0.5f, leafSpawn--, 0.5f), 4f, 1, leaf, false);
		
		generateCylinder(new Vector(0.5f, leafSpawn--, 0.5f), 5f, 1, leaf, false);
		generateCylinder(new Vector(0.5f, leafSpawn--, 0.5f), 5f, 1, leaf, false);
		generateCylinder(new Vector(0.5f, leafSpawn--, 0.5f), 4f, 1, leaf, false);
		
	}

	@Override
	public void preGenerate() {
		height = determineHeight(12, 6);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log3.blockID, 2);
	}

}
