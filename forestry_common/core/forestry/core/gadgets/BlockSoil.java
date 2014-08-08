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
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;

/**
 * Humus, bog earth, peat
 * 
 * @author
 * 
 */
public class BlockSoil extends Block {

	private int degradeDelimiter = 3;
	private int textureHumus = 1;
	private int textureBogEarth = 13;
	private int texturePeat = 15;

	public BlockSoil(int i) {
		super(i, Material.sand);
		setTickRandomly(true);
		setHardness(0.5f);
		setBlockName("humus");
		setStepSound(soundGrassFootstep);
		setTextureFile(Defaults.TEXTURE_BLOCKS);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public int tickRate() {
		return 500;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int type = (metadata & 0x03);
		int maturity = metadata >> 2;

		if (maturity >= this.degradeDelimiter && type == 1) {
			ret.add(new ItemStack(ForestryItem.peat));
			ret.add(new ItemStack(Block.dirt));
		} else {
			ret.add(new ItemStack(this, 1, type));
		}

		return ret;
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!Proxies.common.isSimulating(world))
			return;

		int meta = world.getBlockMetadata(i, j, k);
		int type = (meta & 0x03);

		if (type == 0) {
			updateTickHumus(world, i, j, k, random);
		} else if (type == 1) {
			updateTickBogEarth(world, i, j, k, random);
		} else {
			Proxies.log.warning(this.getClass() + " with unknown type " + type + " encountered.");
		}
	}

	private void updateTickHumus(World world, int i, int j, int k, Random random) {
		if (isEnrooted(world, i, j, k)) {
			degradeSoil(world, i, j, k);
		}
	}

	private void updateTickBogEarth(World world, int i, int j, int k, Random random) {
		if (isMoistened(world, i, j, k)) {
			matureBog(world, i, j, k);
		}
	}

	/**
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean isEnrooted(World world, int x, int y, int z) {

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int blockid = world.getBlockId(x + i, y + 1, z + j);
				if (blockid == Block.wood.blockID || blockid == Block.sapling.blockID || blockid == ForestryBlock.firsapling.blockID)
					// We are not returning true if we are the base of a
					// sapling.
					if (i == 0 && j == 0)
						return false;
					else
						return true;
			}
		}

		return false;
	}

	/**
	 * If a tree or sapling is in the vicinity, there is a chance, that the soil will degrade.
	 * 
	 * @param world
	 * @param i
	 * @param j
	 * @param k
	 */
	private void degradeSoil(World world, int i, int j, int k) {

		if (world.rand.nextInt(70) != 0)
			return;

		int meta = world.getBlockMetadata(i, j, k);

		// Unpack first
		int type = meta & 0x03;
		int grade = meta >> 2;

		// Increment (de)gradation
		grade++;

		// Repackage in format TTGG
		meta = (grade << 2 | type);

		if (grade >= this.degradeDelimiter) {
			world.setBlockWithNotify(i, j, k, Block.sand.blockID);
		} else {
			world.setBlockMetadataWithNotify(i, j, k, meta);
		}
		world.markBlockForUpdate(i, j, k);
	}

	public static boolean isMoistened(World world, int x, int y, int z) {

		for (int i = -2; i < 3; i++) {
			for (int j = -2; j < 3; j++) {
				int blockid = world.getBlockId(x + i, y, z + j);
				if (blockid == Block.waterStill.blockID || blockid == Block.waterMoving.blockID)
					return true;
			}
		}

		return false;
	}

	private void matureBog(World world, int i, int j, int k) {

		if (world.rand.nextInt(13) != 0)
			return;

		int meta = world.getBlockMetadata(i, j, k);

		// Unpack first

		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (maturity >= this.degradeDelimiter)
			return;

		// Increment (de)gradation
		maturity++;

		meta = (maturity << 2 | type);
		world.setBlockMetadataWithNotify(i, j, k, meta);
		world.markBlockForUpdate(i, j, k);
	}

	@Override
	public boolean canSustainPlant(World world, int x, int y, int z, ForgeDirection direction, IPlantable plant) {
		EnumPlantType plantType = plant.getPlantType(world, x, y, z);
		if (plantType != EnumPlantType.Crop && plantType != EnumPlantType.Plains)
			return false;

		int meta = world.getBlockMetadata(x, y, z);

		return (meta & 0x03) == 0;
	}

	// / CREATIVE INVENTORY
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
	}

	/**
	 * Tells MC what texture to use
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {

		int meta = j;
		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (type == 0)
			return textureHumus;
		else if (type == 1)
			if (maturity < this.degradeDelimiter)
				return textureBogEarth;
			else
				return texturePeat;

		return 0;
	}

}
