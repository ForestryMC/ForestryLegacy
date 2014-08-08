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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.cultivation.ICropEntity;
import forestry.api.cultivation.ICropProvider;

public class CropProviderPotatoes implements ICropProvider {

	@Override
	public boolean isGermling(ItemStack germling) {
		return germling.itemID == Item.potato.itemID;
	}

	@Override
	public boolean isCrop(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		return blockid == Block.potato.blockID;
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

		// Can only plant on tilled fields
		int below = world.getBlockId(x, y - 1, z);
		if (below != Block.tilledField.blockID)
			return false;

		world.setBlockAndMetadataWithNotify(x, y, z, Block.potato.blockID, 0);
		return true;
	}

	@Override
	public ICropEntity getCrop(World world, int x, int y, int z) {
		return new CropPotatoe(world, x, y, z);
	}

}
