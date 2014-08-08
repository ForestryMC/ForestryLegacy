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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.core.config.Defaults;

public class BlockLog extends Block implements IWoodTyped {

	public enum LogCat {
		CAT0, CAT1, CAT2, CAT3
	}

	private LogCat cat;

	public BlockLog(int id, LogCat cat) {

		super(id, Material.wood);
		this.cat = cat;

		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundWoodFootstep);
		setBlockName("wood");
		setRequiresSelfNotify();
		setTextureFile(Defaults.TEXTURE_ARBORICULTURE);
		setCreativeTab(Tabs.tabArboriculture);

	}

	public static int getTypeFromMeta(int damage) {
		return damage & 3;
	}

	@Override
	public int getRenderType() {
		return Block.wood.getRenderType();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		
        byte radius = 4;
        int boundary = radius + 1;
 
        if (world.checkChunksExist(x - boundary, y - boundary, z - boundary, x + boundary, y + boundary, z + boundary)) {
        	
            for (int i = -radius; i <= radius; ++i) {
                for (int j = -radius; j <= radius; ++j) {
                    for (int k = -radius; k <= radius; ++k) {
                        int blockid = world.getBlockId(x + i, y + j, z + k);
 
                        if (Block.blocksList[blockid] != null)
                            Block.blocksList[blockid].beginLeavesDecay(world, x + i, y + j, z + k);
                    }
                }
            }
        }
    }
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) {

		int type = getTypeFromMeta(world.getBlockMetadata(x, y, z));
		int orientation = BlockPistonBase.determineOrientation(world, x, y, z, (EntityPlayer) player);
		byte oriented = 0;

		switch (orientation) {

		case 0:
		case 1:
			oriented = 0;
			break;
		case 2:
		case 3:
			oriented = 8;
			break;
		case 4:
		case 5:
			oriented = 4;
		}

		world.setBlockMetadataWithNotify(x, y, z, type | oriented);
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 4; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {

		int oriented = meta & 12;

		int iconIndex = 4 * cat.ordinal();

		switch (oriented) {
		case 0:
			if (side < 2) {
				iconIndex += 32;
			} else {
				iconIndex += 16;
			}
			break;
		case 4:
			if (side > 3) {
				iconIndex += 32;
			} else {
				iconIndex += 16;
			}
			break;
		case 8:
			if (side == 2 || side == 3) {
				iconIndex += 32;
			} else {
				iconIndex += 16;
			}
			break;
		}

		return iconIndex + getTypeFromMeta(meta);
	}

	@Override
	public int damageDropped(int meta) {
		return getTypeFromMeta(meta);
	}

	@Override
	protected ItemStack createStackedBlock(int meta) {
		return new ItemStack(this.blockID, 1, getTypeFromMeta(meta));
	}

	/* PROPERTIES */
	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return 20;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face) {
		if (face == ForgeDirection.DOWN)
			return 20;
		else if (face != ForgeDirection.UP)
			return 10;
		else
			return 5;
	}

	@Override
	public boolean canSustainLeaves(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean isWood(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public WoodType getWoodType(int meta) {
		return WoodType.values()[meta + cat.ordinal() * 4];
	}

}
