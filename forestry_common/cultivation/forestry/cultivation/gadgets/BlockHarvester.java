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

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.core.config.Defaults;
import forestry.core.gadgets.BlockForestry;
import forestry.core.gadgets.GadgetManager;
import forestry.core.gadgets.MachinePackage;
import forestry.core.proxy.Proxies;

public class BlockHarvester extends BlockForestry {

	public BlockHarvester(int i) {
		super(i, Material.rock);
		setHardness(1.5f);
		setTextureFile(Defaults.TEXTURE_BLOCKS);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileHarvester();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float facingX, float facingY, float facingZ) {
		return false;
	}

	/**
	 * Determines direction block is facing when placed by a player
	 */
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		if (!Proxies.common.isSimulating(world))
			return;

		TileHarvester tile = (TileHarvester) world.getBlockTileEntity(i, j, k);
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
			if (GadgetManager.hasHarvesterPackage(i)) {
				itemList.add(new ItemStack(this, 1, i));
			}
	}

	/**
	 * Tells MC what texture to use
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		MachinePackage pack = GadgetManager.getHarvesterPackage(j);

		if (pack == null)
			pack = GadgetManager.getHarvesterPackage(0);

		if (i == 0)
			return pack.textures.bottom;
		else if (i == 1)
			return pack.textures.top;
		else if (i == 2)
			return pack.textures.left;
		else if (i == 3)
			return pack.textures.right;
		else if (i == 4)
			return pack.textures.front;
		else
			return pack.textures.back;
	}

}
