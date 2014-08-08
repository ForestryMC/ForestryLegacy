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
package forestry.farming.gadgets;

import java.util.ArrayList;
import java.util.Random;

import forestry.core.proxy.Proxies;
import forestry.farming.worldgen.WorldGenBigMushroom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockMushroom extends BlockSapling {

	private WorldGenerator[] generators;
	private ItemStack[] drops;
	private int[] textureIndices;

	public BlockMushroom(int i, int j) {
		super(i, j);
		setHardness(0.0f);
		this.generators = new WorldGenerator[] { new WorldGenBigMushroom(0), new WorldGenBigMushroom(1) };
		this.drops = new ItemStack[] { new ItemStack(Block.mushroomBrown), new ItemStack(Block.mushroomRed) };
		this.textureIndices = new int[] { 29, 28 };
		setCreativeTab(null);
		setTickRandomly(true);
	}

	@Override
    public boolean getTickRandomly() {
        return true;
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
	protected boolean canThisPlantGrowOnThisBlockID(int i) {
		return i == Block.mycelium.blockID;
	}
	
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {

		if (!Proxies.common.isSimulating(world))
			return;

		int meta = world.getBlockMetadata(i, j, k);
		int type = meta & 0x03;
		int maturity = meta >> 2;

		tickGermling(world, i, j, k, random, type, maturity);
	}

	private void tickGermling(World world, int i, int j, int k, Random random, int type, int maturity) {

		int lightvalue = world.getBlockLightValue(i, j + 1, k);

		if (random.nextInt(5) != 0)
			return;

		if (maturity != 3) {
			maturity = 3;
			int matX = maturity << 2;
			int meta = (matX | type);
			world.setBlockMetadataWithNotify(i, j, k, meta);
		} else if (lightvalue <= 7) {
			growTree(world, i, j, k, random);
		}
	}

	@Override
	public void growTree(World world, int i, int j, int k, Random random) {
		int type = world.getBlockMetadata(i, j, k) & 0x03;

		world.setBlock(i, j, k, 0);
		if (!generators[type].generate(world, random, i, j, k)) {
			world.setBlockAndMetadata(i, j, k, blockID, type);
		}
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		int type = meta & 0x03;
		return textureIndices[type];
	}

}
