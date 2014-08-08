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
package forestry.cultivation.gadgets;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import forestry.core.proxy.Proxies;

public abstract class BlockGermling extends BlockSapling {

	private WorldGenerator[] generators;
	private ItemStack[] drops;
	private int[] textureIndices;

	public BlockGermling(int i, int j, WorldGenerator[] generators, ItemStack[] drops, int[] textureIndices) {
		super(i, j);
		setHardness(0.0f);
		this.generators = generators;
		this.drops = drops;
		this.textureIndices = textureIndices;
		setCreativeTab(null);
	}

	// / DROPS
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int X, int Y, int Z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int type = metadata & 0x03;
		ret.add(drops[type]);

		return ret;
	}

	@Override
	protected abstract boolean canThisPlantGrowOnThisBlockID(int i);

	/**
	 * Overridden to preserve in case another mod (Nature Overhaul) changes the base function.
	 */
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {

		if (!Proxies.common.isSimulating(world))
			return;

		int meta = world.getBlockMetadata(i, j, k);
		int type = meta & 0x03;
		int maturity = meta >> 2;

		tickGermling(world, i, j, k, random, type, maturity);
	}

	protected abstract void tickGermling(World world, int i, int j, int k, Random random, int type, int maturity);

	@Override
	public void growTree(World world, int i, int j, int k, Random random) {
		int type = world.getBlockMetadata(i, j, k) & 0x03;

		world.setBlock(i, j, k, 0);
		if (!generators[type].generate(world, random, i, j, k)) {
			world.setBlockAndMetadata(i, j, k, blockID, type);
		}
	}

	/*
	@Override
	public boolean onUseBonemeal(World world, int blockID, int X, int Y, int Z) {
		if (blockID != this.blockID)
			return false;

		int type = world.getBlockMetadata(X, Y, Z) & 0x03;

		world.setBlockAndMetadata(X, Y, Z, 0, 0);
		if (!generators[type].generate(world, world.rand, X, Y, Z)) {
			world.setBlockAndMetadata(X, Y, Z, this.blockID, type);
			return false;
		}
		return true;
	} */

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		int type = meta & 0x03;
		return textureIndices[type];
	}

}
