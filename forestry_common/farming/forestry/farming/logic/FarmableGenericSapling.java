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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public class FarmableGenericSapling implements IFarmable {

	final int saplingId;
	final int saplingMeta;
	
	ItemStack[] windfall;
	
	public FarmableGenericSapling(int saplingId, int saplingMeta) {
		this(saplingId, saplingMeta, new ItemStack[0]);
	}

	public FarmableGenericSapling(int saplingId, int saplingMeta, ItemStack[] windfall) {
		this.saplingId = saplingId;
		this.saplingMeta = saplingMeta;
		this.windfall = windfall;
	}
	
	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		
		if(world.isAirBlock(x, y, z))
			return false;
		
		if(world.getBlockId(x, y, z) == saplingId)
			return true;
		
		if(saplingMeta >= 0)
			return world.getBlockMetadata(x, y, z) == saplingMeta;
		else
			return true;

	}
	
	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		if(Block.blocksList[blockid] == null)
			return null;
		if(!Block.blocksList[blockid].isWood(world, x, y, z))
			return null;
		
		return new CropBlock(world, blockid, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		
		if(itemstack.itemID != saplingId)
			return false;
		
		if(saplingMeta >= 0)
			return itemstack.getItemDamage() == saplingMeta;
		else
			return true;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for(ItemStack drop : windfall) {
			if(drop.isItemEqual(itemstack))
				return true;
		}
		return false;
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return germling.copy().tryPlaceItemIntoWorld(Utils.getForestryPlayer(world, x, y, z), world, x, y - 1, z, 1, 0, 0, 0);
	}

}
