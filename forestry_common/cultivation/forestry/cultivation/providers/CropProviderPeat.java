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

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.cultivation.ICropEntity;
import forestry.api.cultivation.ICropProvider;
import forestry.core.config.ForestryBlock;

public class CropProviderPeat implements ICropProvider {

	@Override
	public boolean isGermling(ItemStack germling) {
		return false;
	}

	@Override
	public boolean isCrop(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return blockid == ForestryBlock.soil.blockID && meta == 13;
	}

	@Override
	public ItemStack[] getWindfall() {
		return null;
	}

	@Override
	public boolean doPlant(ItemStack germling, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public ICropEntity getCrop(World world, int x, int y, int z) {
		return new CropPeat(world, x, y, z);
	}

}
