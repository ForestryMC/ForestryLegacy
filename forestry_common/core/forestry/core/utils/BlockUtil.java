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
package forestry.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.Position;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeEntry;

public class BlockUtil {

	public static ArrayList<ItemStack> getBlockItemStack(World world, Vect posBlock) {
		Block block = Block.blocksList[world.getBlockId(posBlock.x, posBlock.y, posBlock.z)];

		if (block == null)
			return null;

		int meta = world.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);

		return block.getBlockDropped(world, posBlock.x, posBlock.y, posBlock.z, meta, 0);

	}

	/**
	 * Searches for inventories adjacent to block, excludes IPowerReceptor
	 * 
	 * @return
	 */
	public static IInventory[] getAdjacentInventories(World world, Vect blockPos, ForgeDirection from) {
		ArrayList<IInventory> inventories = new ArrayList<IInventory>();

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if(from != ForgeDirection.UNKNOWN && from != dir.getOpposite())
				continue;

			TileEntity entity = world.getBlockTileEntity(blockPos.x + dir.offsetX, blockPos.y + dir.offsetY, blockPos.z + dir.offsetZ);
			if (entity != null)
				if (entity instanceof IInventory)
					if (!(entity instanceof IPowerReceptor)) {
						inventories.add((IInventory) entity);
					}
		}

		return inventories.toArray(new IInventory[inventories.size()]);
	}

	/**
	 * Returns a list of adjacent pipes.
	 * 
	 * @param world
	 * @param blockPos
	 * @return
	 */
	public static ForgeDirection[] getPipeDirections(World world, Vect blockPos, ForgeDirection from) {
		LinkedList<ForgeDirection> possiblePipes = new LinkedList<ForgeDirection>();

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if(from != ForgeDirection.UNKNOWN && from != dir.getOpposite())
				continue;
			
			Position posPipe = new Position(blockPos.x, blockPos.y, blockPos.z, dir);
			posPipe.moveForwards(1.0);

			TileEntity pipeEntry = world.getBlockTileEntity((int) posPipe.x, (int) posPipe.y, (int) posPipe.z);

			if (pipeEntry instanceof IPipeEntry && ((IPipeEntry) pipeEntry).acceptItems()) {
				if(from != ForgeDirection.UNKNOWN && pipeEntry instanceof IPipeConnection) {
					if(((IPipeConnection)pipeEntry).isPipeConnected(from))
						possiblePipes.add(dir);
				} else
					possiblePipes.add(dir);
			}
		}

		return possiblePipes.toArray(new ForgeDirection[0]);

	}

	public static ForgeDirection[] filterPipeDirections(ForgeDirection[] all, ForgeDirection[] exclude) {
		LinkedList<ForgeDirection> filtered = new LinkedList<ForgeDirection>();
		ArrayList<ForgeDirection> excludeList = new ArrayList<ForgeDirection>(Arrays.asList(exclude));

		for (int i = 0; i < all.length; i++)
			if (!excludeList.contains(all[i])) {
				filtered.add(all[i]);
			}

		return filtered.toArray(new ForgeDirection[filtered.size()]);

	}

	public static void putFromStackIntoPipe(TileEntity tile, ForgeDirection[] pipes, ItemStack stack) {

		if (stack == null)
			return;
		if (stack.stackSize <= 0)
			return;
		if (pipes.length <= 0)
			return;

		int choice = tile.worldObj.rand.nextInt(pipes.length);

		Position itemPos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, pipes[choice]);

		itemPos.x += 0.5;
		itemPos.y += 0.25;
		itemPos.z += 0.5;
		itemPos.moveForwards(0.5);

		Position pipePos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, pipes[choice]);
		pipePos.moveForwards(1.0);

		IPipeEntry pipe = (IPipeEntry) tile.worldObj.getBlockTileEntity((int) pipePos.x, (int) pipePos.y, (int) pipePos.z);

		ItemStack payload = stack.splitStack(1);
		pipe.entityEntering(payload, itemPos.orientation);
	}

	public static boolean isPoweredTile(TileEntity tile) {
		if (tile == null)
			return false;

		if (!(tile instanceof IPowerReceptor))
			return false;

		IPowerReceptor receptor = (IPowerReceptor) tile;
		return receptor.getPowerProvider() != null;
	}


}
