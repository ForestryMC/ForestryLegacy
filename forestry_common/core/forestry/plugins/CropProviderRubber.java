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
package forestry.plugins;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.cultivation.ICropEntity;
import forestry.api.cultivation.ICropProvider;
import forestry.core.config.ForestryBlock;

public class CropProviderRubber implements ICropProvider {

	@Override
	public boolean isGermling(ItemStack germling) {
		return germling.isItemEqual(PluginIC2.rubbersapling);
	}

	@Override
	public boolean isCrop(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return (blockid == ForestryBlock.firsapling.blockID && (meta & 0x03) == 1) || blockid == PluginIC2.rubberwood.itemID;
	}

	@Override
	public ItemStack[] getWindfall() {
		ArrayList<ItemStack> windfall = new ArrayList<ItemStack>();
		windfall.add(PluginIC2.rubbersapling);
		windfall.add(PluginIC2.resin);
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

		world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.firsapling.blockID, 1);
		return true;
	}

	@Override
	public ICropEntity getCrop(World world, int x, int y, int z) {
		return new CropRubber(world, x, y, z);
	}

}
