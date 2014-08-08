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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.core.ForestryAPI;
import forestry.api.cultivation.ICropEntity;
import forestry.api.cultivation.ICropProvider;
import forestry.core.config.Config;
import forestry.core.config.ForestryBlock;

public class CropProviderSapling implements ICropProvider {

	@Override
	public boolean isGermling(ItemStack germling) {
		return germling.itemID == Block.sapling.blockID;
	}

	@Override
	public boolean isCrop(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		return blockid == ForestryBlock.sapling.blockID || blockid == Block.sapling.blockID || blockid == Block.wood.blockID;
	}

	@Override
	public ItemStack[] getWindfall() {
		ArrayList<ItemStack> windfall = new ArrayList<ItemStack>();
		windfall.add(new ItemStack(Block.sapling, 1, 0));
		windfall.add(new ItemStack(Block.sapling, 1, 1));
		windfall.add(new ItemStack(Block.sapling, 1, 2));
		windfall.add(new ItemStack(Block.sapling, 1, 3));

		if (Config.applePickup) {
			windfall.add(new ItemStack(Item.appleRed));
			windfall.add(new ItemStack(Item.appleGold));
		}
		for (ItemStack fruit : ForestryAPI.loggerWindfall) {
			windfall.add(fruit);
		}

		return windfall.toArray(new ItemStack[0]);
	}

	@Override
	public boolean doPlant(ItemStack germling, World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);

		// Target block needs to be empty
		if (blockid != 0)
			return false;

		// Can only plant on soulsand
		int below = world.getBlockId(x, y - 1, z);
		int meta = world.getBlockMetadata(x, y - 1, z);
		if (below != ForestryBlock.soil.blockID || (meta & 0x03) != 0)
			return false;

		world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.sapling.blockID, germling.getItemDamage());
		return true;
	}

	@Override
	public ICropEntity getCrop(World world, int x, int y, int z) {
		return new CropSapling(world, x, y, z);
	}

}
