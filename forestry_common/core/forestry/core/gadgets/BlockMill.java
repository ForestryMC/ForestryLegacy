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

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class BlockMill extends BlockForestry {

	public BlockMill(int i, int j) {
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
		return new TileMill();
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
	 * Determines direction block is facing when placed by a player
	 */
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {

		super.onBlockPlacedBy(world, i, j, k, entityliving);

		if (!Proxies.common.isSimulating(world))
			return;

		TileMill tile = (TileMill) world.getBlockTileEntity(i, j, k);
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
			if (GadgetManager.hasGrowerPackage(i)) {
				itemList.add(new ItemStack(this, 1, i));
			}
	}

	/**
	 * Tells MC what texture to use
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		return 0;
	}
}
