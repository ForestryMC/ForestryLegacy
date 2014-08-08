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
import net.minecraft.block.BlockFence;
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
import forestry.plugins.PluginForestryArboriculture;

public class BlockArbFence extends BlockFence implements IWoodTyped {

	public BlockArbFence(int id) {
		super(id, 0, Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundWoodFootstep);
		setTextureFile(Defaults.TEXTURE_ARBORICULTURE);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 16; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
		return true;
	}
	
	@Override
	public boolean canConnectFenceTo(IBlockAccess world, int x, int y, int z) {
		if (!isFence(world, x, y, z)) {
			int blockid = world.getBlockId(x, y, z);
			Block block = Block.blocksList[blockid];
			return block != null && block.blockMaterial.isOpaque() && block.renderAsNormalBlock() ? block.blockMaterial != Material.pumpkin : false;
		} else
			return true;
	}

	@Override
	public int getRenderType() {
		return PluginForestryArboriculture.modelIdFences;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return meta;
	}

	public boolean isFence(IBlockAccess world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		if (blockid == this.blockID || blockid == Block.fence.blockID || blockid == Block.fenceGate.blockID || blockid == Block.netherFence.blockID)
			return true;

		return false;
	}

	/* PROPERTIES */
	@Override
	public boolean isWood(World world, int x, int y, int z) {
		return true;
	}

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
		return WoodType.values()[meta];
	}

}
