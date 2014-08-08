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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.core.GlobalManager;
import forestry.core.gadgets.BlockForestry;
import forestry.core.gadgets.GadgetManager;
import forestry.core.proxy.Proxies;

public class BlockPlanter extends BlockForestry {

	private int textureFront = 2;
	private int textureTop = 2;
	private int textureSide = 2;

	public BlockPlanter(int i) {
		super(i, Material.rock);
		setHardness(1.5f);

		GlobalManager.holyBlockIds.add(blockID);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return Proxies.common.getByBlockModelId();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TilePlanter();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float facingX, float facingY, float facingZ) {

		if (player.isSneaking())
			return false;

		TilePlanter tile = (TilePlanter) world.getBlockTileEntity(x, y, z);
		if (tile.machine != null) {
			tile.machine.openGui(player, tile);
		}
		return true;
	}

	/**
	 * Called when block is added to the world
	 */
	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		if (!Proxies.common.isSimulating(world))
			return;

		super.onBlockAdded(world, i, j, k);
		setDefaultDirection(world, i, j, k);
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	/**
	 * Determines default direction block is facing
	 * 
	 * @param world
	 * @param i
	 * @param j
	 * @param k
	 */
	private void setDefaultDirection(World world, int i, int j, int k) {
		// Don't execute in multiplayer world
		if (!Proxies.common.isSimulating(world))
			return;

		TilePlanter tile = (TilePlanter) world.getBlockTileEntity(i, j, k);
		int l = world.getBlockId(i, j, k - 1);
		int i1 = world.getBlockId(i, j, k + 1);
		int j1 = world.getBlockId(i - 1, j, k);
		int k1 = world.getBlockId(i + 1, j, k);
		tile.setOrientation(ForgeDirection.WEST);
		if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) {
			tile.setOrientation(ForgeDirection.WEST);
		}
		if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) {
			tile.setOrientation(ForgeDirection.NORTH);
		}
		if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) {
			tile.setOrientation(ForgeDirection.SOUTH);
		}
		if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) {
			tile.setOrientation(ForgeDirection.EAST);
		}
	}

	/**
	 * Determines direction block is facing when placed by a player
	 */
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {

		super.onBlockPlacedBy(world, i, j, k, entityliving);

		if (!Proxies.common.isSimulating(world))
			return;

		TilePlanter tile = (TilePlanter) world.getBlockTileEntity(i, j, k);
		tile.setOrientation(ForgeDirection.WEST);
	}

	// / CREATIVE INVENTORY
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 16; i++)
			if (GadgetManager.hasPlanterPackage(i)) {
				itemList.add(new ItemStack(this, 1, i));
			}
	}

	/**
	 * Tells MC what texture to use
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {

		// If no metadata is set, then this is an icon.
		if (j == 0 && i == 3)
			return textureFront;

		// If the side (i) equals the the side the block is facing (metadata j),
		// we return the front.
		if (i == j)
			return textureFront;

		switch (i) {
		case 1:
			return textureTop;
		default:
			return textureSide;
		}
	}

}
