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
import java.util.HashMap;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.config.Defaults;
import forestry.core.utils.Vect;

public class FarmLogicCocoa extends FarmLogic {

	private final IFarmable cocoa = new FarmableCocoa();
	
	public FarmLogicCocoa(IFarmHousing housing) {
		super(housing);
	}

	@Override
	public int getIconIndex() {
		return Item.dyePowder.getIconFromDamage(3);
	}

	@Override
	public String getTextureFile() {
		return Defaults.TEXTURE_ICONS_MINECRAFT;
	}

	@Override
	public String getName() {
		return "Cocoa Plantation";
	}

	@Override
	public int getFertilizerConsumption() {
		return 120;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int)(20 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return cocoa.isGermling(itemstack);
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	private HashMap<Vect, Integer> lastExtentsCultivation = new HashMap<Vect, Integer>();
	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {
		
		world = housing.getWorld();

		Vect start = new Vect(x, y, z);
		if(!lastExtentsCultivation.containsKey(start)) {
			lastExtentsCultivation.put(start, 0);
		}
		
		int lastExtent = lastExtentsCultivation.get(start);
		if(lastExtent > extent)
			lastExtent = 0;

		Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		boolean result = tryPlantingCocoa(position);
		
		lastExtent++;
		lastExtentsCultivation.put(start, lastExtent);

		return result;
	}

	private HashMap<Vect, Integer> lastExtentsHarvest = new HashMap<Vect, Integer>();
	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
		
		world = housing.getWorld();

		Collection<ICrop> crops = null;
		
		Vect start = new Vect(x, y, z);
		if(!lastExtentsHarvest.containsKey(start)) {
			lastExtentsHarvest.put(start, 0);
		}
		
		int lastExtent = lastExtentsHarvest.get(start);
		if(lastExtent > extent)
			lastExtent = 0;

		Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtentsHarvest.put(start, lastExtent);
		
		return crops;
	}

	private boolean tryPlantingCocoa(Vect position) {
		
		Vect current = position;
		while(isWoodBlock(current) && BlockLog.limitToValidMetadata(getBlockMeta(current)) == 3) {
			
			for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
				if(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
					continue;
				
				Vect candidate = new Vect(current.x + direction.offsetX, current.y, current.z + direction.offsetZ);
				if(isAirBlock(candidate))
					return housing.plantGermling(cocoa, world, candidate.x, candidate.y, candidate.z);
			}
			
			current = current.add(new Vect(0, 1, 0));
			if(current.y - position.y > 1)
				break;
		}
		
		return false;
	}
	
	private Collection<ICrop> getHarvestBlocks(Vect position) {
		
		ArrayList<Vect> seen = new ArrayList<Vect>();
		Stack<ICrop> crops = new Stack<ICrop>();

		// Determine what type we want to harvest.
		int blockid = getBlockId(position);
		if(Block.blocksList[blockid] == null)
			return crops;
		
		ICrop crop = null;
		if(!Block.blocksList[blockid].isWood(world, position.x, position.y, position.z)) {
			crop = cocoa.getCropAt(world, position.x, position.y, position.z);
			if(crop == null)
				return crops;
		}
		
		if(crop != null)
			crops.add(crop);
		
		ArrayList<Vect> candidates = processHarvestBlock(crops, seen, position, position);
		ArrayList<Vect> temp = new ArrayList<Vect>();
		while(!candidates.isEmpty()  && crops.size() < 20) {
			for(Vect candidate : candidates) {
				temp.addAll(processHarvestBlock(crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}
		//Proxies.log.finest("Logic %s at %s/%s/%s has seen %s blocks.", getClass().getName(), position.x, position.y, position.z, seen.size());
		
		return crops;
	}
	
	private ArrayList<Vect> processHarvestBlock(Stack<ICrop> crops, Collection<Vect> seen, Vect start, Vect position) {

		ArrayList<Vect> candidates = new ArrayList<Vect>();
		
		// Get additional candidates to return
		for(int i = -1; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				for(int k = -1; k < 2; k++) {				
					Vect candidate = new Vect(position.x + i, position.y + j, position.z + k);
					if(candidate.equals(position))
						continue;
					if(Math.abs(candidate.x - start.x) > 5)
						continue;
					if(Math.abs(candidate.z - start.z) > 5)
						continue;
					
					// See whether the given position has already been processed
					boolean skip = false;
					for(Vect prcs : seen) {
						if(candidate.equals(prcs)) {
							skip = true;
							break;
						}
					}
					
					if(skip)
						continue;
					
					ICrop crop = cocoa.getCropAt(world, candidate.x, candidate.y, candidate.z);
					if(crop != null) {
						crops.push(crop);
						candidates.add(candidate);
						seen.add(candidate);
					} else if (isWoodBlock(candidate)) {
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}
		
		return candidates;
	}

}
