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
package forestry.arboriculture.gadgets;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginForestryArboriculture;

public class BlockSapling extends BlockTreeContainer {

	public static TileSapling getSaplingTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileSapling))
			return null;

		return (TileSapling) tile;
	}

	public BlockSapling(int id) {
		super(id, Material.plants);

		float factor = 0.4F;
		setBlockBounds(0.5F - factor, 0.0F, 0.5F - factor, 0.5F + factor, factor * 2.0F, 0.5F + factor);
		setStepSound(soundGrassFootstep);
		setTextureFile(Defaults.TEXTURE_GERMLINGS);
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileSapling();
	}

	/* COLLISION BOX */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return PluginForestryArboriculture.modelIdSaplings;
	}

	@Override
	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileSapling sapling = getSaplingTile(world, x, y, z);
		if (sapling == null)
			return 0;

		if (sapling.getTree() == null)
			return 0;

		return sapling.getTree().getGenome().getPrimaryAsTree().getGermlingIconIndex(EnumGermlingType.SAPLING);
	}

	/* PLANTING */
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		TileSapling tile = getSaplingTile(world, x, y, z);
		if (tile == null)
			return false;
		if(tile.getTree() == null)
			return false;

		return tile.getTree().canStay(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighbourId) {
		if(Proxies.common.isSimulating(world) && !this.canBlockStay(world, x, y, z)) {
			dropAsSapling(world, x, y, z);
			world.setBlockWithNotify(x, y, z, 0);
		}

	}
	
	/* REMOVING */
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, world.getBlockMetadata(x, y, z))) {
			if(!player.capabilities.isCreativeMode)
				dropAsSapling(world, x, y, z);
		}

		return world.setBlockWithNotify(x, y, z, 0);
	}

	private void dropAsSapling(World world, int x, int y, int z) {
		if(!Proxies.common.isSimulating(world))
			return;
		

		TileSapling sapling = getSaplingTile(world, x, y, z);
		if (sapling != null && sapling.getTree() != null) {
			ItemStack saplingStack = TreeManager.treeInterface.getGermlingStack(sapling.getTree(), EnumGermlingType.SAPLING);
			StackUtils.dropItemStackAsEntity(saplingStack, world, x, y, z);
		}

	}
}
