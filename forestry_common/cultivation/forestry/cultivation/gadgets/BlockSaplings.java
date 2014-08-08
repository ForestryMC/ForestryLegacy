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
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import forestry.core.config.ForestryBlock;
import forestry.cultivation.WorldGenFirTrees;

public class BlockSaplings extends BlockGermling {

	public BlockSaplings(int i, int j) {
		super(i, j, new WorldGenerator[] { new WorldGenFirTrees(true, 0, 0), new WorldGenFirTrees(true, 1, 1), new WorldGenFirTrees(true, 2, 2),
				new WorldGenFirTrees(true, 3, 3) }, new ItemStack[] { new ItemStack(Block.sapling, 1, 0), new ItemStack(Block.sapling, 1, 1),
				new ItemStack(Block.sapling, 1, 2), new ItemStack(Block.sapling, 1, 3) }, new int[] { 15, 63, 79, 30 });
	}

	@Override
	protected boolean canThisPlantGrowOnThisBlockID(int i) {
		return i == ForestryBlock.soil.blockID || i == Block.dirt.blockID;
	}

	@Override
	protected void tickGermling(World world, int i, int j, int k, Random random, int type, int maturity) {

		int lightvalue = world.getBlockLightValue(i, j + 1, k);

		int growchance = 15;

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

}
