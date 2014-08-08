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

import net.minecraft.block.Block;
import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.BlockType;

public class WorldGenPine extends WorldGenTree {

	public WorldGenPine(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;
		float diameterchange = (float)1 / height;
		int leafSpawned = 2;

		generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 2, 1, leaf, false);

		while (leafSpawn > 1) {
			generateCylinder(new Vector(0, leafSpawn--, 0), 4*diameterchange*leafSpawned, 1, leaf, false);
			generateCylinder(new Vector(0, leafSpawn--, 0), 3*diameterchange*leafSpawned, 1, leaf, false);
			leafSpawned += 2;
		}

	}
	
	@Override
	public void preGenerate() {
		height = determineHeight(6, 4);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(Block.wood.blockID, 1);
	}

}
