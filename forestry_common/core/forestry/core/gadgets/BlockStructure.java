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

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import forestry.api.core.ITileStructure;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public abstract class BlockStructure extends BlockForestry {

	public enum EnumStructureState {
		VALID, INVALID, INDETERMINATE
	}

	public BlockStructure(int i, Material material) {
		super(i, material);
		setHardness(1.0f);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {

		if (player.isSneaking())
			return false;

		TileForestry tile = (TileForestry) world.getBlockTileEntity(x, y, z);
		if (!tile.isUseableByPlayer(player))
			return false;

		if (!Proxies.common.isSimulating(world))
			return true;

		// GUIs can only be opened on integrated structure blocks.
		if (tile instanceof ITileStructure)
			if (!((ITileStructure) tile).isIntegratedIntoStructure())
				return false;

		if (tile.allowsInteraction(player)) {
			tile.openGui(player);
		} else {
			player.addChatMessage("\u00A7c" + tile.getOwnerName() + " " + StringUtil.localize("chat.accesslocked"));
		}
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighbourBlockId) {

		if (!Proxies.common.isSimulating(world))
			return;

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof ITileStructure))
			return;

		((ITileStructure) tile).validateStructure();
	}
}
