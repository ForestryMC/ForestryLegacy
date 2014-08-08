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
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLog;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.Vect;

public class FarmableCocoa implements IFarmable {

	public static final int COCOA_PLANT_ID = Block.cocoaPlant.blockID;
	public static final int COCOA_SEED_ID = Item.dyePowder.itemID;
	public static final int COCOA_META = 3; 
	
	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		return world.getBlockId(x, y, z) == COCOA_PLANT_ID;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		if(blockid != COCOA_PLANT_ID)
			return null;
		int meta = world.getBlockMetadata(x, y, z);
		if(BlockCocoa.func_72219_c(meta) < 2)
			return null;
		
		return new CropBlock(world, blockid, meta, new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return itemstack.itemID == COCOA_SEED_ID && itemstack.getItemDamage() == COCOA_META;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {		
		for(int i = 0; i < 4; i++) {
			if(!isValidPot(world, x, y, z, i))
				continue;
			
			world.setBlockAndMetadataWithNotify(x, y, z, COCOA_PLANT_ID, i);
			break;
		}
		return true;
	}

	private boolean isValidPot(World world, int x, int y, int z, int notchDirection) {
        x += Direction.offsetX[notchDirection];
        z += Direction.offsetZ[notchDirection];
        int blockid = world.getBlockId(x, y, z);
        return blockid == Block.wood.blockID && BlockLog.limitToValidMetadata(world.getBlockMetadata(x, y, z)) == 3;

	}
}
