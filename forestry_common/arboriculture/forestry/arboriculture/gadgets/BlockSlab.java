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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.core.config.Defaults;

public class BlockSlab extends BlockHalfSlab implements IWoodTyped {

	public static enum SlabCat {
		CAT_0, CAT_1
	}

	private SlabCat cat;

	public BlockSlab(int id, SlabCat cat) {
		super(id, false, Material.wood);
		this.cat = cat;
		setCreativeTab(Tabs.tabArboriculture);
		setTextureFile(Defaults.TEXTURE_BLOCKS);
		setLightOpacity(0);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundWoodFootstep);
		Block.useNeighborBrightness[id] = true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {

		int iconIndex = 8 * cat.ordinal() + 224;
		switch (meta & 7) {
		case 1:
			return iconIndex + 1;
		case 2:
			return iconIndex + 2;
		case 3:
			return iconIndex + 3;
		case 4:
			return iconIndex + 4;
		case 5:
			return iconIndex + 5;
		case 6:
			return iconIndex + 6;
		case 7:
			return iconIndex + 7;
		default:
			return iconIndex + 0;
		}
	}

	@Override
	public int getBlockTextureFromSide(int par1) {
		return this.getBlockTextureFromSideAndMetadata(par1, 0);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return blockID;
	}

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World world, int x, int y, int z) {
        //return isBlockSingleSlab(this.blockID) ? this.blockID : (this.blockID == Block.stoneDoubleSlab.blockID ? Block.stoneSingleSlab.blockID : (this.blockID == Block.woodDoubleSlab.blockID ? Block.woodSingleSlab.blockID : Block.stoneSingleSlab.blockID));
    	return blockID;
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World world, int x, int y, int z) {
        return super.getDamageValue(world, x, y, z) & 7;
    }

	@Override
	protected ItemStack createStackedBlock(int meta) {
		return new ItemStack(Block.woodSingleSlab.blockID, 2, meta & 7);
	}

	@Override
	public String getFullSlabName(int var1) {
		return "SomeSlab";
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 8; ++i) {
			itemList.add(new ItemStack(id, 1, i));
		}
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
		return 5;
	}

	@Override
	public WoodType getWoodType(int meta) {
		return WoodType.values()[meta + cat.ordinal() * 8];
	}

}
