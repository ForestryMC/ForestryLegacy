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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;

public class BlockResource extends Block {
	private int textureApatite = 0;
	private int textureCopper = 3;
	private int textureTin = 30;

	public BlockResource(int i) {
		super(i, Material.rock);
		setHardness(3F);
		setResistance(5F);
		setTextureFile(Defaults.TEXTURE_BLOCKS);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float par6, int par7) {
		super.dropBlockAsItemWithChance(world, x, y, z, metadata, par6, par7);

		if (metadata == 0) {
			this.dropXpOnBlockBreak(world, x, y, z, MathHelper.getRandomIntegerInRange(world.rand, 1, 4));
		}
	}

	@Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		
		if(metadata == 0) {
            int fortmod = world.rand.nextInt(fortune + 2) - 1;
            if (fortmod < 0)
            	fortmod = 0;

			int amount = (1 + world.rand.nextInt(5)) * (fortmod + 1);
			if(amount > 0)
				drops.add(new ItemStack(ForestryItem.apatite, amount));
		} else
			drops.add(new ItemStack(blockID, 1, metadata));
		
		return drops;
    }

	// / CREATIVE INVENTORY
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
	}

	/**
	 * Tells MC what texture to use
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		if (j == 0)
			return textureApatite;
		else if (j == 1)
			return textureCopper;
		else if (j == 2)
			return textureTin;
		else
			return 0;
	}

}
