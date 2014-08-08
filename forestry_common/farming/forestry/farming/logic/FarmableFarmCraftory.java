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

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;
import forestry.plugins.PluginFarmCraftory;

public class FarmableFarmCraftory implements IFarmable {

	Collection<ItemStack> germlings;
	Collection<ItemStack> windfall;
	
	public FarmableFarmCraftory(Collection<ItemStack> germlings, Collection<ItemStack> windfall) {
		this.germlings = germlings;
		this.windfall = windfall;
	}
	
	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		
		if(world.isAirBlock(x, y, z))
			return false;

		int blockid = world.getBlockId(x, y, z);
		if(blockid != PluginFarmCraftory.blockSingle.blockID
				&& blockid != PluginFarmCraftory.blockMulti.blockID)
			return false;
		else
			return true;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		if(blockid != PluginFarmCraftory.blockSingle.blockID
				&& blockid != PluginFarmCraftory.blockMulti.blockID)
			return null;
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile == null)
			return null;
		if(PluginFarmCraftory.getGrowthStage(tile) < 2)
			return null;
		
		return new CropBlock(world, blockid, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		for(ItemStack seed : germlings)
			if(StackUtils.isIdenticalItem(seed, itemstack))
				return true;
		
		return false;
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return germling.copy().tryPlaceItemIntoWorld(Utils.getForestryPlayer(world, x, y, z), world, x, y - 1, z, 1, 0, 0, 0);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for(ItemStack fruit : windfall)
			if(StackUtils.isIdenticalItem(fruit, itemstack))
				return true;
		
		return false;
	}

}
