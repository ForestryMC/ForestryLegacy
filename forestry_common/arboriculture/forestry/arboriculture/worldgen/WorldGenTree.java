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
import forestry.core.worldgen.BlockType;

public abstract class WorldGenTree extends WorldGenArboriculture {

	protected int girth;
	protected int height;
	
	protected int minHeight = 4;
	protected int maxHeight = 80;

	public WorldGenTree(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateCylinder(new Vector(0, leafSpawn--, 0), 1, 1, leaf, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 1.5f, 1, leaf, false);

		generateCylinder(new Vector(0, leafSpawn--, 0), 2.9f, 1, leaf, false);
		generateCylinder(new Vector(0, leafSpawn--, 0), 2.9f, 1, leaf, false);

	}

	@Override
	public boolean canGrow() {
		return tree.canGrow(world, startX, startY, startZ, tree.getGirth(world, startX, startY, startZ), height);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(5, 2);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	protected int determineGirth(int base) {
		return base;
	}
	
	protected int modifyByHeight(int val, int min, int max) {
		int determined = Math.round(val * tree.getHeightModifier());
		return determined < min ? min : determined > max ? max : determined;
	}
	
	protected int determineHeight(int required, int variation) {
		int determined = Math.round((required + rand.nextInt(variation)) * tree.getHeightModifier());
		return determined < minHeight ? minHeight : determined > maxHeight ? maxHeight : determined;
	}
	
	@Override
	public BlockType getLeaf() {
		return new BlockTypeLeaf();
	}

	@Override
	public abstract BlockType getWood();

}
