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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import forestry.api.world.ITreeGenData;
import forestry.core.utils.Vect;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.WorldGenBase;

public abstract class WorldGenArboriculture extends WorldGenBase {

	protected ITreeGenData tree;
	protected int startX;
	protected int startY;
	protected int startZ;

	public WorldGenArboriculture(ITreeGenData tree) {
		this.tree = tree;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {

		super.generate(world, random, x, y, z);
		
		this.startX = x;
		this.startY = y;
		this.startZ = z;
		
		this.leaf = getLeaf();
		this.wood = getWood();
		
		preGenerate();
		if (!canGrow())
			return false;
		else {
			generate();
			return true;
		}

	}

	public abstract void preGenerate();

	public abstract void generate();

	public abstract boolean canGrow();

	public abstract BlockType getLeaf();

	public abstract BlockType getWood();

	BlockType leaf;
	BlockType wood;
	BlockType vine = new BlockType(Block.vine.blockID, 0);
	BlockType air = new BlockTypeVoid();

	public final Vect getStartVector() {
		return new Vect(startX, startY, startZ);
	}

	protected void generateTreeTrunk(int height, int width) {
		generateTreeTrunk(height, width, 0);
	}
	
	protected void generateTreeTrunk(int height, int width, float vines) {
		int offset = (width - 1) / 2;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < width; y++) {
				for (int i = 0; i < height; i++) {
					addWood(x - offset, i, y - offset, true);
					
					if(rand.nextFloat() < vines)
						addVine(x - offset - 1, i, y - offset);
					if(rand.nextFloat() < vines)
						addVine(x - offset + 1, i, y - offset);
					if(rand.nextFloat() < vines)
						addVine(x - offset, i, y - offset - 1);
					if(rand.nextFloat() < vines)
						addVine(x - offset, i, y - offset + 1);
				}
			}
		}
	}

	protected void generateSupportStems(int height, int girth, float chance, float maxHeight) {
		
		int offset = 1;
		
		for (int x = - offset; x < girth + offset; x++) {
			for (int z = - offset; z < girth + offset; z++) {
				
				if(x == -offset && z == -offset)
					continue;
				if(x == girth+offset && z == girth+offset)
					continue;
				if(x == -offset && z == girth+offset)
					continue;
				if(x == girth+offset && z == -offset)
					continue;
				
				int stemHeight = rand.nextInt(Math.round(height*maxHeight));
				if(rand.nextFloat() < chance) {
					for (int i = 0; i < stemHeight; i++) {
						addWood(x, i, z, false);
					}
				}
			}
		}
		
	}
	
	@Override
	protected void addBlock(int x, int y, int z, BlockType type, boolean doReplace) {
		if (doReplace || world.isAirBlock(startX + x, startY + y, startZ + z)) {
			type.setBlock(world, tree, startX + x, startY + y, startZ + z);
		}
	}

	protected final void clearBlock(int x, int y, int z) {
		air.setBlock(world, tree, startX + x, startY + y, startZ + z);
	}
	
	protected final void addWood(int x, int y, int z, boolean doReplace) {
		addBlock(x, y, z, wood, doReplace);
	}

	protected final void addLeaf(int x, int y, int z, boolean doReplace) {
		addBlock(x, y, z, leaf, doReplace);
	}

	protected final void addVine(int x, int y, int z) {
		addBlock(x, y, z, vine, false);
	}
	
}
