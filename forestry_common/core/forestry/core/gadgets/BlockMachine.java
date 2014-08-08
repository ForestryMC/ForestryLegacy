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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class BlockMachine extends BlockForestry {

	private int textureFront = 5;
	private int textureTop = 8;
	private int textureSide = 7;

	public BlockMachine(int i) {
		super(i, Material.rock);
		setHardness(1.5f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return Proxies.common.getByBlockModelId();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileMachine();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {

		if (player.isSneaking())
			return false;

		TileMachine tile = (TileMachine) world.getBlockTileEntity(x, y, z);
		if (!tile.isUseableByPlayer(player))
			return false;

		if (!Proxies.common.isSimulating(world))
			return true;

		if (tile.machine == null)
			return true;

		if (tile.allowsInteraction(player)) {
			tile.machine.openGui(player, tile);
		} else {
			player.addChatMessage("\u00A7c" + tile.getOwnerName() + " " + StringUtil.localize("chat.accesslocked"));
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

	/**
	 * Called when a neighboring block changes
	 */
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockId) {
		if (!Proxies.common.isSimulating(world))
			return;

		super.onNeighborBlockChange(world, x, y, z, neighborBlockId);
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (!(entity instanceof TileMachine))
			return;

		((TileMachine) entity).onNeighborBlockChange();
	}

	/**
	 * Determines default direction (what does it do actually?)
	 * 
	 * @param world
	 * @param i
	 * @param j
	 * @param k
	 */
	private void setDefaultDirection(World world, int i, int j, int k) {

		if (!Proxies.common.isSimulating(world))
			return;

		TileMachine tile = (TileMachine) world.getBlockTileEntity(i, j, k);
		int l = world.getBlockId(i, j, k - 1);
		int i1 = world.getBlockId(i, j, k + 1);
		int j1 = world.getBlockId(i - 1, j, k);
		int k1 = world.getBlockId(i + 1, j, k);
		tile.setOrientation(ForgeDirection.WEST);

		if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) {
			tile.setOrientation(ForgeDirection.WEST);
		}
		if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) {
			tile.setOrientation(ForgeDirection.EAST);
		}
		if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) {
			tile.setOrientation(ForgeDirection.SOUTH);
		}
		if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) {
			tile.setOrientation(ForgeDirection.NORTH);
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

		TileMachine tile = (TileMachine) world.getBlockTileEntity(i, j, k);
		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		if (l == 0) {
			tile.setOrientation(ForgeDirection.NORTH);
		}
		if (l == 1) {
			tile.setOrientation(ForgeDirection.EAST);
		}
		if (l == 2) {
			tile.setOrientation(ForgeDirection.SOUTH);
		}
		if (l == 3) {
			tile.setOrientation(ForgeDirection.WEST);
		}
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	// / CREATIVE INVENTORY
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 16; i++)
			if (GadgetManager.hasMachinePackage(i)) {
				itemList.add(new ItemStack(this, 1, i));
			}
	}

	// / REDSTONE SIGNALS

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean isProvidingWeakPower(IBlockAccess world, int i, int j, int k, int l) {
		return ((TileMachine) world.getBlockTileEntity(i, j, k)).isIndirectlyPoweringTo(world, i, j, k, l);
	}

	@Override
	public boolean isProvidingStrongPower(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return ((TileMachine) iblockaccess.getBlockTileEntity(i, j, k)).isPoweringTo(iblockaccess, i, j, k, l);
	}

	// / TEXTURES AND RENDERING
	/*
	 * public void randomDisplayTick(World world, int i, int j, int k, Random random) { ((TileMachine)world.getBlockTileEntity(i, j,
	 * k)).machine.randomDisplayTick(world, i, j, k, random); }
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
