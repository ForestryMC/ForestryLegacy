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
package forestry.apiculture.gadgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.core.config.Defaults;
import forestry.core.utils.StackUtils;

public class BlockBeehives extends BlockContainer {
	public static int textureCopper = 3;
	public static int textureForestHive = 2;
	public static int textureMeadowsHive = 4;
	public static int textureForestHiveTop = 5;
	public static int textureMeadowsHiveTop = 6;
	public static int textureParchedHive = 7;
	public static int textureParchedHiveTop = 8;
	public static int textureTropicalHive = 9;
	public static int textureTropicalHiveTop = 10;
	public static int textureEndHive = 11;
	public static int textureEndHiveTop = 12;
	public static int textureSnowHive = 23;
	public static int textureSnowHiveTop = 24;
	public static int textureSwampHive = 28;
	public static int textureSwampHiveTop = 29;
	public static int textureSwarmHive = 44;
	public static int textureSwarmHiveTop = 45;

	public BlockBeehives(int i) {
		super(i, new MaterialBeehive(true));
		setTextureFile(Defaults.TEXTURE_BLOCKS);
		setLightValue(0.8f);
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileSwarm();
	}

	@Override
	public boolean canDragonDestroy(World world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (canHarvestBlock(player, world.getBlockMetadata(x, y, z))) {
			// Handle TE'd beehives
			TileEntity tile = world.getBlockTileEntity(x, y, z);

			if (tile instanceof TileSwarm) {
				TileSwarm swarm = (TileSwarm) tile;
				if (swarm.containsBees()) {
					for (ItemStack beeStack : swarm.contained.getStacks())
						if (beeStack != null) {
							StackUtils.dropItemStackAsEntity(beeStack, world, x, y, z);
						}
				}
			}
		}

		return world.setBlockWithNotify(x, y, z, 0);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		// Handle legacy block
		if (metadata == 0) {
			ret.add(new ItemStack(this));
			return ret;
		}

		ArrayList<IHiveDrop> dropList = BeeManager.hiveDrops[metadata - 1];

		Collections.shuffle(dropList);
		// Grab a princess
		int tries = 0;
		boolean hasPrincess = false;
		while (tries <= 10 && !hasPrincess) {
			tries++;

			for (IHiveDrop drop : dropList) {
				if (world.rand.nextInt(100) < drop.getChance(world, x, y, z)) {
					ret.add(drop.getPrincess(world, x, y, z, fortune));
					hasPrincess = true;
					break;
				}
			}
		}

		// Grab drones
		for (IHiveDrop drop : dropList) {
			if (world.rand.nextInt(100) < drop.getChance(world, x, y, z)) {
				ret.addAll(drop.getDrones(world, x, y, z, fortune));
				break;
			}
		}
		// Grab anything else on offer
		for (IHiveDrop drop : dropList) {
			if (world.rand.nextInt(100) < drop.getChance(world, x, y, z)) {
				ret.addAll(drop.getAdditional(world, x, y, z, fortune));
				break;
			}
		}

		return ret;
	}

	// / CREATIVE INVENTORY
	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
		itemList.add(new ItemStack(this, 1, 3));
		itemList.add(new ItemStack(this, 1, 4));
		// End hive not added
		itemList.add(new ItemStack(this, 1, 6));
		itemList.add(new ItemStack(this, 1, 7));
		// Swarm hive not added
	}

	/**
	 * Tells MC what texture to use
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {

		switch (j) {
		case 0:
			return textureCopper;
		case 1:
			if (i == 1 || i == 0)
				return textureForestHiveTop;
			else
				return textureForestHive;
		case 2:
			if (i == 1 || i == 0)
				return textureMeadowsHiveTop;
			else
				return textureMeadowsHive;
		case 3:
			if (i == 1 || i == 0)
				return textureParchedHiveTop;
			else
				return textureParchedHive;
		case 4:
			if (i == 1 || i == 0)
				return textureTropicalHiveTop;
			else
				return textureTropicalHive;
		case 5:
			if (i == 1 || i == 0)
				return textureEndHiveTop;
			else
				return textureEndHive;
		case 6:
			if (i == 1 || i == 0)
				return textureSnowHiveTop;
			else
				return textureSnowHive;
		case 7:
			if (i == 1 || i == 0)
				return textureSwampHiveTop;
			else
				return textureSwampHive;
		case 8:
			if (i == 1 || i == 0)
				return textureSwarmHiveTop;
			else
				return textureSwarmHive;
		default:
			return 0;
		}
	}

}
