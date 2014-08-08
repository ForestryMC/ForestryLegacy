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
import java.util.Random;

import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;

public class BlockStainedGlass extends BlockBreakable {

	public BlockStainedGlass(int id, int iconIndex) {
		super(id, iconIndex, Material.glass, true);
		setHardness(0.3F);
		setStepSound(soundGlassFootstep);
		setTextureFile(Defaults.TEXTURE_BLOCKS);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 16; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return 191 - meta;
	}

	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		return this.getBlockTextureFromSideAndMetadata(side, world.getBlockMetadata(x, y, z));
	}

	public int damageDropped(int meta) {
		return meta;
	}

	public int quantityDropped(Random par1Random) {
		return 0;
	}

	public int getRenderBlockPass() {
		return 1;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

}
