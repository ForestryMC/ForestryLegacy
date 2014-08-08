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
import net.minecraftforge.common.ForgeDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.utils.Vect;

public abstract class FarmLogic implements IFarmLogic {

	World world;
	IFarmHousing housing;

	boolean isManual;
	
	public FarmLogic(IFarmHousing housing) {
		this.housing = housing;
	}
	
	public FarmLogic setManual(boolean flag) {
		isManual = flag;
		return this;
	}
	
	protected final boolean isAirBlock(Vect position) {
		return world.isAirBlock(position.x, position.y, position.z);
	}
	
	protected final boolean isWoodBlock(Vect position) {
		int blockid = getBlockId(position);
		return Block.blocksList[blockid] != null
				&& Block.blocksList[blockid].isWood(world, position.x, position.y, position.z);
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
	
	protected final Vect translateWithOffset(int x, int y, int z, ForgeDirection direction, int step) {
		return new Vect(x + direction.offsetX*step, y + direction.offsetY*step, z + direction.offsetZ*step);
	}
	
	protected final void setBlock(Vect position, int id, int meta) {
		world.setBlockAndMetadataWithNotify(position.x, position.y, position.z, id, meta);
	}	

}
