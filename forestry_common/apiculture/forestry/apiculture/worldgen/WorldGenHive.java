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
package forestry.apiculture.worldgen;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import forestry.apiculture.gadgets.TileSwarm;
import forestry.core.config.ForestryBlock;

public class WorldGenHive extends WorldGenerator {

	private ItemStack[] bees;

	public WorldGenHive() {
	}

	public WorldGenHive(ItemStack[] bees) {
		this.bees = bees;
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {

		int i1 = (i + random.nextInt(8)) - random.nextInt(8);
		int j1 = (j + random.nextInt(4)) - random.nextInt(4);
		int k1 = (k + random.nextInt(8)) - random.nextInt(8);
		if (world.blockExists(i1, j1, k1) && world.isAirBlock(i1, j1, k1) && (!world.isAirBlock(i1, j1 - 1, k1) || !world.isAirBlock(i1, j1 + 1, k1))) {
			setHive(world, i1, j1, k1, 8);
			return true;
		}

		return false;
	}

	protected void setHive(World world, int x, int y, int z, int meta) {

		boolean placed = world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.beehives.blockID, meta);
		if (!placed)
			return;

		if (world.getBlockId(x, y, z) != ForestryBlock.beehives.blockID)
			return;

		ForestryBlock.beehives.onBlockAdded(world, x, y, z);
		world.markBlockForUpdate(x, y, z);

		if (bees == null || bees.length <= 0) {
			ForestryBlock.beehives.onBlockPlaced(world, x, y, z, 0, 0.0f, 0.0f, 0.0f, meta);
			return;
		}

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileSwarm) {
			((TileSwarm) tile).setContained(bees);
		}
	}

}
