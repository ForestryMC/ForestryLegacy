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
package forestry.farming.logic;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class CropBlock extends Crop {

	int blockId;
	int meta;
	
	public CropBlock(World world, int blockId, int meta, Vect position) {
		super(world, position);
		this.blockId = blockId;
		this.meta = meta;
	}

	@Override
	protected boolean isCrop(Vect pos) {
		return getBlockId(pos) == blockId && getBlockMeta(pos) == meta;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		Collection<ItemStack> harvested = Block.blocksList[blockId].getBlockDropped(world, pos.x, pos.y, pos.z, meta, 0);
		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, blockId, 0);
		// Block.breakBlock() is called by vanilla itself, removing TEs.
		world.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, 0, 0);
		return harvested;
	}

	@Override
	public String toString() {
		return String.format("CropBlock [ position: [ %s ]; blockId: %s; meta: %s ]", position.toString(), blockId, meta);
	}
}
