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
package forestry.cultivation.providers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.cultivation.ICropEntity;
import forestry.api.cultivation.ICropProvider;
import forestry.core.config.ForestryBlock;

public class CropProviderMushroom implements ICropProvider {

	@Override
	public boolean isGermling(ItemStack germling) {
		return germling.itemID == Block.mushroomBrown.blockID || germling.itemID == Block.mushroomRed.blockID;
	}

	@Override
	public boolean isCrop(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		return blockid == ForestryBlock.mushroom.blockID || blockid == Block.mushroomCapBrown.blockID || blockid == Block.mushroomCapRed.blockID;
	}

	@Override
	public ItemStack[] getWindfall() {
		return null;
	}

	@Override
	public boolean doPlant(ItemStack germling, World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);

		// Target block needs to be empty
		if (blockid != 0)
			return false;

		// Can only plant on mycelium
		int below = world.getBlockId(x, y - 1, z);
		if (below != Block.mycelium.blockID)
			return false;

		if (germling.itemID == Block.mushroomBrown.blockID) {
			world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.mushroom.blockID, 0);
		} else {
			world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.mushroom.blockID, 1);
		}
		return true;
	}

	@Override
	public ICropEntity getCrop(World world, int x, int y, int z) {
		return new CropMushroom(world, x, y, z);
	}

}
