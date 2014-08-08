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
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.cultivation.ICropEntity;
import forestry.core.proxy.Proxies;

public class CropCacti implements ICropEntity {

	private World world;
	private int xCoord;
	private int yCoord;
	private int zCoord;

	public CropCacti(World world, int i, int j, int k) {
		this.world = world;
		this.xCoord = i;
		this.yCoord = j;
		this.zCoord = k;
	}

	@Override
	public boolean isHarvestable() {
		int blockMiddle = world.getBlockId(xCoord, yCoord - 1, zCoord);
		int blockBottom = world.getBlockId(xCoord, yCoord - 2, zCoord);
		return blockMiddle == Block.cactus.blockID && blockBottom == Block.cactus.blockID;
	}

	@Override
	public int[] getNextPosition() {
		return null;
	}

	@Override
	public ArrayList<ItemStack> doHarvest() {
		ArrayList<ItemStack> harvest = Block.cactus.getBlockDropped(world, xCoord, yCoord, zCoord, 0, 0);
		Proxies.common.addBlockDestroyEffects(world, xCoord, yCoord, zCoord, Block.cactus.blockID, 0);
		world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, 0, 0);
		return harvest;
	}

}
