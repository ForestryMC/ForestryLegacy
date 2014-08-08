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
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.core.Tabs;
import forestry.arboriculture.WoodType;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockArbStairs extends BlockStairs {

	public BlockArbStairs(int id, Block par2Block, int par3) {
		super(id, par2Block, par3);
		setTextureFile(Defaults.TEXTURE_ARBORICULTURE);
		setRequiresSelfNotify();
		Block.useNeighborBrightness[id] = true;
		setCreativeTab(Tabs.tabArboriculture);
		setHardness(2.0F);
		setResistance(5.0F);
	}

	public static TileStairs getStairTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileStairs))
			return null;

		return (TileStairs) tile;
	}

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List list) {
   		for(WoodType type : WoodType.values()) {
   			ItemStack stack = new ItemStack(id, 1, 0);
   			NBTTagCompound compound = new NBTTagCompound("tag");
   			type.saveToCompound(compound);
   			stack.setTagCompound(compound);
   			list.add(stack);
   		}
    }

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, meta)) {
			if(!player.capabilities.isCreativeMode) {
				// Handle TE'd beehives
				TileEntity tile = world.getBlockTileEntity(x, y, z);

				if (tile instanceof TileStairs) {
					TileStairs stairs = (TileStairs) tile;
				
					ItemStack stack = new ItemStack(blockID, 1, 0);
					NBTTagCompound compound = new NBTTagCompound("tag");
					stairs.getType().saveToCompound(compound);
					stack.setTagCompound(compound);
					StackUtils.dropItemStackAsEntity(stack, world, x, y, z);
				}
			}
		}

		return world.setBlockWithNotify(x, y, z, 0);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
        world.removeBlockTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	@Override
    public boolean hasTileEntity(int meta) {
		return true;
    }
    
	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileStairs();
	}

	@Override
	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileStairs stairs = getStairTile(world, x, y, z);
		if (stairs != null
				&& stairs.getType() != null)
			return stairs.getType().getPlankIndex();

		return 0;
	}


}
