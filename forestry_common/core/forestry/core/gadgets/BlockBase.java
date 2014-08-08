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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockBase extends BlockForestry {

	private final MachineDefinition[] definitions;
	private final boolean hasTESR; 

	public BlockBase(int i, Material material, MachineDefinition[] definitions) {
		this(i, material, definitions, false);
	}
	
	public BlockBase(int id, Material material, MachineDefinition[] definitions, boolean hasTESR) {
		super(id, material);
		this.definitions = definitions;
		this.setTextureFile(Defaults.TEXTURE_BLOCKS);
		this.hasTESR = hasTESR;
		if(hasTESR)
			Block.useNeighborBrightness[id] = true;
	}

	@Override
	public boolean isOpaqueCube() {
		return !hasTESR;
	}

	public boolean renderAsNormalBlock() {
		return !hasTESR;
	}

	@Override
	public int getRenderType() {
		if(hasTESR)
			return Proxies.common.getByBlockModelId();
		else
			return 0;
	}

	private int getValidMeta(World world, int x, int y, int z) {
		
		int metadata = world.getBlockMetadata(x, y, z);
		if(metadata >= definitions.length || definitions[metadata] == null)
			metadata = 0;

		return metadata;
	}
	
	/* CREATIVE INVENTORY */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List list) {
    	for(MachineDefinition definition : definitions) {
    		if(definition == null)
    			continue;
    		definition.getSubBlocks(id, tab, list);
    	}
    }
    
    /* TILE ENTITY CREATION */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		if(metadata >= definitions.length || definitions[metadata] == null)
			metadata = 0;
		
		return definitions[metadata].createMachine();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return createNewTileEntity(world, 0);
	}

	/* INTERACTION */
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		int metadata = getValidMeta(world, x, y, z);
		return definitions[metadata].isBlockSolidOnSide(world, x, y, z, side);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {

		super.onBlockPlacedBy(world, i, j, k, entityliving);

		TileForestry tile = (TileForestry) world.getBlockTileEntity(i, j, k);
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {

		int metadata = getValidMeta(world, x, y, z);
		if(definitions[metadata].onBlockActivated(world, x, y, z, player, par6, par7, par8, par9))
			return true;

		if (player.isSneaking())
			return false;

		TileBase tile = (TileBase) world.getBlockTileEntity(x, y, z);
		if (!tile.isUseableByPlayer(player))
			return false;

		if (!Proxies.common.isSimulating(world))
			return true;

		if (tile.allowsInteraction(player)) {
			tile.openGui(player, tile);
		} else {
			player.addChatMessage("\u00A7c" + tile.getOwnerName() + " " + StringUtil.localize("chat.accesslocked"));
		}
		return true;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if(!super.removeBlockByPlayer(world, player, x, y, z))
			return false;
		
		int metadata = world.getBlockMetadata(x, y, z);
		if(metadata >= definitions.length || definitions[metadata] == null)
			metadata = 0;
		return definitions[metadata].removeBlockByPlayer(world, player, x, y, z);
	}
	
	@Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		int metadata = world.getBlockMetadata(x, y, z);
		if(metadata >= definitions.length || definitions[metadata] == null)
			metadata = 0;
		return definitions[metadata].canConnectRedstone(world, x, y, z, side);
	}
	
	/* TEXTURES */
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		if(metadata >= definitions.length || definitions[metadata] == null)
			metadata = 0;
		return definitions[metadata].getBlockTextureFromSideAndMetadata(side, metadata);
	}
	
	@Override
    public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		int metadata = world.getBlockMetadata(x, y, z);
		if(metadata >= definitions.length || definitions[metadata] == null)
			metadata = 0;
		return definitions[metadata].getBlockTexture(world, x, y, z, side, metadata);
	}
}
