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

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.Vect;

public class FarmableStacked implements IFarmable {

	int blockid;
	int matureHeight;
	
	public FarmableStacked(int blockid, int matureHeight) {
		this.blockid = blockid;
		this.matureHeight = matureHeight;
	}
	
	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		return world.getBlockId(x, y, z) == blockid;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		if(world.getBlockId(x, y + (matureHeight - 1), z) != blockid)
			return null;
		
		return new CropBlock(world, blockid, 0, new Vect(x, y + (matureHeight - 1), z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return itemstack.itemID == blockid;
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return world.setBlockWithNotify(x, y, z, blockid);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
