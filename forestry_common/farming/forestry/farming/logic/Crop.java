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

import forestry.api.farming.ICrop;
import forestry.core.utils.Vect;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class Crop implements ICrop {

	protected World world;
	protected Vect position;
	
	public Crop(World world, Vect position) {
		this.world = world;
		this.position = position;
	}
	
	protected final void setBlock(Vect position, int id, int meta) {
		world.setBlockAndMetadataWithUpdate(position.x, position.y, position.z, id, meta, true);
	}
	
	protected final void clearBlock(Vect position) {
		world.setBlockAndMetadataWithUpdate(position.x, position.y, position.z, 0, 0, true);
		if(world.getBlockTileEntity(position.x, position.y, position.z) != null)
			world.setBlockTileEntity(position.x, position.y, position.z, null);
	}
	
	protected final int getBlockId(Vect position) {
		return world.getBlockId(position.x, position.y, position.z);
	}
	
	protected final int getBlockMeta(Vect position) {
		return world.getBlockMetadata(position.x, position.y, position.z);
	}
	
	protected final ItemStack getAsItemStack(Vect position) {
		return new ItemStack(getBlockId(position), 1, getBlockMeta(position));
	}
	
	protected abstract boolean isCrop(Vect pos);
	protected abstract Collection<ItemStack> harvestBlock(Vect pos);
	
	@Override
	public Collection<ItemStack> harvest() {
		if(!isCrop(position))
			return null;
		
		return harvestBlock(position);
	}
	
}
