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

public class CropSapling implements ICropEntity {

	private World world;
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private int blockid;
	private int meta;

	public CropSapling(World world, int i, int j, int k) {
		this.world = world;
		this.xCoord = i;
		this.yCoord = j;
		this.zCoord = k;
		this.blockid = world.getBlockId(i, j, k);
		this.meta = world.getBlockMetadata(i, j, k);
	}

	@Override
	public boolean isHarvestable() {
		return blockid == Block.wood.blockID;
	}

	@Override
	public int[] getNextPosition() {
		int[] next = null;

		int count = 1;
		int blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		while (blockid == Block.wood.blockID) {
			next = new int[] { xCoord, yCoord + count, zCoord };
			count++;
			blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		}

		if (next != null)
			return next;

		count = -1;
		blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		while (blockid == Block.wood.blockID) {
			next = new int[] { xCoord, yCoord + count, zCoord };
			count--;
			blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		}

		return next;
	}

	@Override
	public ArrayList<ItemStack> doHarvest() {
		ArrayList<ItemStack> harvest = Block.wood.getBlockDropped(world, xCoord, yCoord, zCoord, meta, 0);
		Proxies.common.addBlockDestroyEffects(world, xCoord, yCoord, zCoord, blockid, 0);
		world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, 0, 0);
		return harvest;
	}

}
