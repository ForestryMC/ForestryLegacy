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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import forestry.api.farming.IFarmHousing;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public abstract class FarmLogicWatered extends FarmLogic {

	protected ItemStack[] ground;
	private ItemStack[] resource;
	private ItemStack[] waste;

	ArrayList<ItemStack> produce = new ArrayList<ItemStack>();

	public FarmLogicWatered(IFarmHousing housing, ItemStack[] resource, ItemStack[] ground, ItemStack[] waste) {
		super(housing);
		this.ground = ground;
		this.resource = resource;
		this.waste = waste;
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int)(20 * hydrationModifier);
	}
	
	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource[0].isItemEqual(itemstack);
	}

	@Override
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = new ArrayList<ItemStack>();
		return products;
	}

	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {

		world = housing.getWorld();

		if(maintainSoil(x, y, z, direction, extent))
			return true;
		
		if(!isManual && maintainWater(x, y, z, direction, extent))
			return true;
		
		if(maintainCrops(x, y + 1, z, direction, extent))
			return true;
		
		return false;
	}

	private boolean isWaste(ItemStack stack) {
		for(ItemStack block : waste) {
			if(block.isItemEqual(stack))
				return true;
		}
		return false;
	}
	
	private boolean maintainSoil(int x, int y, int z, ForgeDirection direction, int extent) {
		
		for(int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			if(!isAirBlock(position) 
					&& !Utils.isReplaceableBlock(world, position.x, position.y, position.z)) {
				
				ItemStack block = getAsItemStack(position);
				if(isWaste(block) && housing.hasResources(resource)) {
					produce.addAll(Block.blocksList[block.itemID].getBlockDropped(world, x, y, z, block.itemID, 0));
					setBlock(position, 0, 0);
					return trySetSoil(position);
				}
				
				continue;
			}

			if(isManual)
				continue;
			
			if(i % 2 != 0) {
				ForgeDirection cclock = ForgeDirection.EAST;
				if(direction == ForgeDirection.EAST)
					cclock = ForgeDirection.SOUTH;
				else if(direction == ForgeDirection.SOUTH)
					cclock = ForgeDirection.EAST;
				else if(direction == ForgeDirection.WEST)
					cclock = ForgeDirection.SOUTH;					
				
				Vect previous = translateWithOffset(position.x, position.y, position.z, cclock, 1);
				ItemStack soil = getAsItemStack(previous);
				if(!ground[0].isItemEqual(soil))
					trySetSoil(position);
				continue;
			}
			
			return trySetSoil(position);
		}
		
		return false;
	}
	
	private boolean maintainWater(int x, int y, int z, ForgeDirection direction, int extent) {
		// Still not done, check water then
		for(int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			if(!isAirBlock(position)
					&& !Utils.isReplaceableBlock(world, position.x, position.y, position.z))
				continue;

			boolean isEnclosed = true;
			
			if(world.isAirBlock(position.x + 1, position.y, position.z))
				isEnclosed = false;
			else if(world.isAirBlock(position.x - 1, position.y, position.z))
				isEnclosed = false;
			else if(world.isAirBlock(position.x, position.y, position.z + 1))
				isEnclosed = false;
			else if(world.isAirBlock(position.x, position.y, position.z - 1))
				isEnclosed = false;
			
			if(isEnclosed) {
				return trySetWater(position);
			}
		}
		
		return false;
	}

	protected boolean maintainCrops(int x, int y, int z, ForgeDirection direction, int extent) {
		return false;
	}
	
	private boolean trySetSoil(Vect position) {
		if(!housing.hasResources(resource))
			return false;
		setBlock(position, ground[0].itemID, ground[0].getItemDamage());
		housing.removeResources(resource);
		return true;
	}
	
	private boolean trySetWater(Vect position) {
		LiquidStack liquid = new LiquidStack(Block.waterStill.blockID, 1000);
		if(!housing.hasLiquid(liquid))
			return false;
		
		setBlock(position, Block.waterStill.blockID, 0);
		housing.removeLiquid(liquid);
		return true;
	}
	
}
