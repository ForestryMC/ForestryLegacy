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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.cultivation.WorldGenFirTrees;
import forestry.cultivation.WorldGenRubberTrees;
import forestry.farming.worldgen.WorldGenBigMushroom;
import forestry.plugins.PluginIC2;

public class BlockFirSapling extends BlockSapling {

	public BlockFirSapling(int i, int j) {
		super(i, j);
		setHardness(0.0f);
		setCreativeTab(null);
	}

	@Override
	public int idDropped(int i, Random random, int j) {
		int type = i & 0x03;
		if (type == 1)
			return PluginIC2.rubbersapling.itemID;
		else if (type == 2)
			if (random.nextInt(2) <= 0)
				return Block.mushroomBrown.blockID;
			else
				return Block.mushroomRed.blockID;
		else
			return Block.sapling.blockID;

	}

	@Override
	public int damageDropped(int meta) {
		return meta & 0x03;
	}

	@Override
	protected boolean canThisPlantGrowOnThisBlockID(int i) {
		return i == ForestryBlock.soil.blockID || i == Block.mycelium.blockID;
	}

	/**
	 * Overridden to preserve in case another mod (Nature Overhaul) changes the base function.
	 */
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {

		if (!Proxies.common.isSimulating(world))
			return;

		// super.updateTick(world, i, j, k, random);
		// checkFlowerChange(world, i, j, k);

		int meta = world.getBlockMetadata(i, j, k);
		int type = meta & 0x03;
		int maturity = meta >> 2;

		/*
		 * int lightvalue = world.getBlockLightValue(i, j + 1, k);
		 * 
		 * if(lightvalue >= 9 && random.nextInt(30) == 0) { if(maturity != 3) { maturity = 3; int matX = maturity << 2;
		 * //ModLoader.getLogger().fine("Setting maturity to 3: " + matX + "/" + Integer.toBinaryString(matX)); meta = (matX | type);
		 * //ModLoader.getLogger().fine("Results combined with type " + type + "/" + Integer.toBinaryString(type) + " in " + meta + "/" +
		 * Integer.toBinaryString(meta));
		 * 
		 * world.setBlockMetadataWithNotify(i, j, k, meta); } else growTree(world, i, j, k, random); }
		 */

		if (type != 2) {
			tickSapling(world, i, j, k, random, type, maturity);
		} else {
			tickMushroom(world, i, j, k, random, type, maturity);
		}
	}

	private void tickSapling(World world, int i, int j, int k, Random random, int type, int maturity) {

		int lightvalue = world.getBlockLightValue(i, j + 1, k);

		// Rubber saplings have a slightly higher chance to grow.
		int growchance;
		if (type == 1) {
			growchance = 8;
		} else {
			growchance = 15;
		}

		if (lightvalue >= 9 && random.nextInt(growchance) == 0)
			if (maturity != 3) {
				maturity = 3;
				int matX = maturity << 2;
				int meta = (matX | type);

				world.setBlockMetadataWithNotify(i, j, k, meta);
			} else {
				growTree(world, i, j, k, random);
			}

	}

	private void tickMushroom(World world, int i, int j, int k, Random random, int type, int maturity) {
		int lightvalue = world.getBlockLightValue(i, j + 1, k);

		// Mushrooms only grow in the dark
		if (lightvalue > 7)
			return;

		if (random.nextInt(10) != 0)
			return;

		if (maturity != 3) {
			maturity = 3;
			int matX = maturity << 2;
			int meta = (matX | type);
			world.setBlockMetadataWithNotify(i, j, k, meta);
		} else {
			growMushroom(world, i, j, k, random, type);
		}
	}

	/**
	 * Creates a tree generator
	 * 
	 * @param world
	 * @return
	 */
	private WorldGenerator createTreeGenerator(World world, int type) {
		WorldGenerator generator;

		if (type == 1 && PluginIC2.instance.isAvailable()) {
			generator = new WorldGenRubberTrees(true);
		} else {
			generator = new WorldGenFirTrees(true);
		}

		return generator;
	}

	/**
	 * Since trees in vanilla minecraft won't grow on BlockHumus, we need a special WorldGenFirTrees
	 */
	@Override
	public void growTree(World world, int i, int j, int k, Random random) {
		int type = world.getBlockMetadata(i, j, k) & 0x03;
		WorldGenerator generator = createTreeGenerator(world, type);

		world.setBlock(i, j, k, 0);
		if (!generator.generate(world, random, i, j, k)) {
			world.setBlockAndMetadata(i, j, k, blockID, type);
		}
	}

	public void growMushroom(World world, int i, int j, int k, Random random, int type) {
		WorldGenerator generator = new WorldGenBigMushroom(random.nextInt(1));

		world.setBlock(i, j, k, 0);
		if (!generator.generate(world, random, i, j, k)) {
			world.setBlockAndMetadata(i, j, k, blockID, type);
		}
	}

	/**
	 * Handles bonemeal usage for fir saplings type.
	 */
	// / FIXME: This is gone.
	// @Override
	public boolean onUseBonemeal(World world, int bid, int i, int j, int k) {
		if (bid != ForestryBlock.firsapling.blockID)
			return false;

		int type = world.getBlockMetadata(i, j, k) & 0x03;

		WorldGenerator generator;
		if (type != 2) {
			generator = createTreeGenerator(world, type);
		} else {
			generator = new WorldGenBigMushroom();
		}

		world.setBlockAndMetadata(i, j, k, 0, 0);
		if (!generator.generate(world, world.rand, i, j, k)) {
			world.setBlockAndMetadata(i, j, k, ForestryBlock.firsapling.blockID, type);
			return false;
		}
		return true;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		int type = meta & 0x03;
		if (type == 0)
			return 15;
		else if (type == 1)
			return 63;
		else if (type == 2)
			return 29;
		else
			return 79;
	}

}
