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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.cultivation.ICropEntity;

public class CropRubber implements ICropEntity {

	private World world;
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private int blockid;

	public CropRubber(World world, int i, int j, int k) {
		this.world = world;
		this.xCoord = i;
		this.yCoord = j;
		this.zCoord = k;
		this.blockid = world.getBlockId(i, j, k);
	}

	@Override
	public boolean isHarvestable() {
		return blockid == PluginIC2.rubberwood.itemID;
	}

	@Override
	public int[] getNextPosition() {
		int[] next = null;

		int count = 1;
		int blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		while (blockid == PluginIC2.rubberwood.itemID) {
			next = new int[] { xCoord, yCoord + count, zCoord };
			count++;
			blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		}

		if (next != null)
			return next;

		count = -1;
		blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		while (blockid == PluginIC2.rubberwood.itemID) {
			next = new int[] { xCoord, yCoord + count, zCoord };
			count--;
			blockid = world.getBlockId(xCoord, yCoord + count, zCoord);
		}

		return next;
	}

	@Override
	public ArrayList<ItemStack> doHarvest() {
		int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
		ArrayList<ItemStack> harvest = Block.blocksList[PluginIC2.rubberwood.itemID].getBlockDropped(world, xCoord, yCoord, zCoord, meta, 0);
		world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, 0, 0);
		return harvest;
	}

}
