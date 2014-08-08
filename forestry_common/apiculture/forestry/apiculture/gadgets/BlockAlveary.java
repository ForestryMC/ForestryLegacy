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
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.core.config.Defaults;
import forestry.core.gadgets.BlockStructure;

public class BlockAlveary extends BlockStructure {

	public BlockAlveary(int id) {
		super(id, new MaterialBeehive(false));
		setHardness(1.0f);
		if(id == Defaults.ID_BLOCK_ALVEARY)
			setCreativeTab(Tabs.tabApiculture);
		setTextureFile(Defaults.TEXTURE_BLOCKS);
	}

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List list) {
		if(id != Defaults.ID_BLOCK_ALVEARY)
			return;

    	for(int i = 0; i < 6; i++) {
    		if(i == 1)
    			continue;
   			list.add(new ItemStack(blockID, 1, i));
    	}
    }

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drop = new ArrayList<ItemStack>();
		drop.add(new ItemStack(blockID, 1, metadata));
		return drop;
	}
	
	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch(metadata) {
		case 2:  return new TileAlvearySwarmer();
		case 3:  return new TileAlvearyFan();
		case 4:  return new TileAlvearyHeater();
		case 5:	 return new TileAlvearyHygroregulator();
		default: return new TileAlvearyPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return createNewTileEntity(world, 0);
	}

	/* TEXTURES */
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		if (metadata <= 1 && side == 1)
			return 27;
		
		switch(metadata) {
		case 1: 	return 26;
		case 2:		return 55;
		case 3:		return 71;
		case 4:		return 57;
		case 5:		return 73;
		default:	return 25;
		}
		
	}

	@Override
	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == 1)
			return this.getBlockTextureFromSideAndMetadata(side, meta);
		else if(meta > 1)
			return getBlockTextureFromSideAndTile(world, x, y, z, side);

		int idXP = world.getBlockId(x + 1, y, z);
		int idXM = world.getBlockId(x - 1, y, z);

		if (idXP == this.blockID && idXM != this.blockID) {

			if (world.getBlockMetadata(x + 1, y, z) == 1) {

				if (world.getBlockId(x, y, z + 1) != this.blockID)
					return switchForSide(42, side);
				else
					return switchForSide(41, side);

			} else
				return this.getBlockTextureFromSideAndMetadata(side, meta);

		} else if (idXP != this.blockID && idXM == this.blockID) {

			if (world.getBlockMetadata(x - 1, y, z) == 1) {

				if (world.getBlockId(x, y, z + 1) != this.blockID)
					return switchForSide(41, side);
				else
					return switchForSide(42, side);

			} else
				return this.getBlockTextureFromSideAndMetadata(side, meta);
		}

		return this.getBlockTextureFromSideAndMetadata(side, meta);
	}

	private int getBlockTextureFromSideAndTile(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile == null || !(tile instanceof TileAlveary))
			return getBlockTextureFromSideAndMetadata(side, 0);
		
		return ((TileAlveary)tile).getBlockTexture(side, world.getBlockMetadata(x, y, z));
	}
	
	private int switchForSide(int textureId, int side) {

		if (side == 4 || side == 5) {
			if (textureId == 41)
				return 41;
			else
				return 42;
		} else {
			if (textureId == 41)
				return 42;
			else
				return 41;
		}

	}
}
