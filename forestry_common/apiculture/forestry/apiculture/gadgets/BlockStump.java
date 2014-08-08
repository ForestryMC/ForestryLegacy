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

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.core.Tabs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;

public class BlockStump extends BlockTorch {

	public BlockStump(int id, int iconIndex) {
		super(id, iconIndex);
		this.setTextureFile(Defaults.TEXTURE_BLOCKS);
		this.setRequiresSelfNotify();
		this.setHardness(0.0F);
		this.setStepSound(soundWoodFootstep);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float facingX, float facingY, float facingZ) {

		ItemStack held = player.getCurrentEquippedItem();
		if (held != null && (held.itemID == Item.flintAndSteel.itemID || held.itemID == Item.flint.itemID)) {
			world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.candle.blockID, world.getBlockMetadata(x, y, z));
			return true;
		}

		return false;
	}

	@Override
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
	}
}
