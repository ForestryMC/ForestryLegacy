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
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import forestry.core.config.Defaults;
import forestry.core.gadgets.BlockStructure;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.farming.gadgets.TileFarm.EnumFarmBlock;

public class BlockFarm extends BlockStructure {

	public BlockFarm(int id) {
		super(id, Material.rock);
		setHardness(1.0f);
		setTextureFile(Defaults.TEXTURE_FARM);
	}

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List list) {
    	for(int i = 0; i < 6; i++) {
    		if(i == 1)
    			continue;
    		
    		for(EnumFarmBlock block : EnumFarmBlock.values()) {
    			ItemStack stack = new ItemStack(id, 1, i);
    			NBTTagCompound compound = new NBTTagCompound("tag");
    			block.saveToCompound(compound);
    			stack.setTagCompound(compound);
    			list.add(stack);
    		}
    	}
    }

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, meta)) {
			// Handle TE'd beehives
			TileEntity tile = world.getBlockTileEntity(x, y, z);

			if (tile instanceof TileFarm) {
				TileFarm farm = (TileFarm) tile;
				
				if(meta == 1)
					meta = 0;
				
    			ItemStack stack = new ItemStack(blockID, 1, meta);
    			NBTTagCompound compound = new NBTTagCompound("tag");
    			farm.getFarmBlock().saveToCompound(compound);
    			stack.setTagCompound(compound);
    			StackUtils.dropItemStackAsEntity(stack, world, x, y, z);
			}
		}

		return world.setBlockWithNotify(x, y, z, 0);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch(metadata) {
		case 2:  return new TileGearbox();
		case 3:  return new TileHatch();
		case 4:  return new TileValve();
		case 5:  return new TileControl();
		default: return new TileFarmPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return createNewTileEntity(world, 0);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		int textureShift = 0;
		
		int sideShift = 0;
		if(metadata == 0 && side == 2)
			sideShift = 16;
		else if(metadata == 0 && (side == 0 || side == 1))
			sideShift = 32;
		
		switch(metadata) {
		case 2:
			textureShift = TileGearbox.TEXTURE_SHIFT;
			break;
		case 3:
			textureShift = TileHatch.TEXTURE_SHIFT;
			break;
		case 4:
			textureShift = TileValve.TEXTURE_SHIFT;
			break;
		case 5:
			textureShift = TileControl.TEXTURE_SHIFT;
			break;
		}
		
		return textureShift + sideShift;
	}
	
	@Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
    	if(world.getBlockMetadata(x, y, z) == 5)
    		return true;
    	else
    		return false;
    }

	@Override
    public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile == null || !(tile instanceof TileFarm))
			return Block.brick.getBlockTextureFromSideAndMetadata(side, 0);
		
		return ((TileFarm)tile).getBlockTexture(side, world.getBlockMetadata(x, y, z));
    }

}
