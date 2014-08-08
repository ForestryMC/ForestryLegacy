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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;
import forestry.plugins.PluginIC2;

public class FarmLogicRubber extends FarmLogic {

	private boolean inActive;
	
	public FarmLogicRubber(IFarmHousing housing) {
		super(housing);
		if(PluginIC2.rubberwood == null || PluginIC2.resin == null) {
			Proxies.log.warning("Failed to init a farm logic %s since IC2 was not found", getClass().getName());
			inActive = true;
		}
	}

	@Override
	public int getIconIndex() {
		if(!inActive)
			return PluginIC2.resin.getIconIndex();
		else
			return Item.gunpowder.getIconFromDamage(0);
	}

	@Override
	public String getTextureFile() {
		if(!inActive)
			return PluginIC2.resin.getItem().getTextureFile();
		else
			return Defaults.TEXTURE_ICONS_MINECRAFT;
	}

	@Override
	public String getName() {
		return "Rubber Plantation";
	}

	@Override
	public int getFertilizerConsumption() {
		return 40;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int)(5 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {
		return false;
	}

	private HashMap<Vect, Integer> lastExtents = new HashMap<Vect, Integer>();
	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
		if(inActive)
			return null;
		
		world = housing.getWorld();

		Collection<ICrop> crops = null;
		Vect start = new Vect(x, y, z);
		if(!lastExtents.containsKey(start)) {
			lastExtents.put(start, 0);
		}
		
		int lastExtent = lastExtents.get(start);
		if(lastExtent > extent)
			lastExtent = 0;

		//Proxies.log.finest("Logic %s is searching in direction %s at %s/%s/%s with extension %s.", getClass(), direction, x, y, z, lastExtent);

		Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtents.put(start, lastExtent);
		
		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(Vect position) {
		
		ArrayList<Vect> seen = new ArrayList<Vect>();
		Stack<ICrop> crops = new Stack<ICrop>();

		// Determine what type we want to harvest.
		int blockid = getBlockId(position);
		if(blockid != PluginIC2.rubberwood.itemID)
			return crops;

		int meta = this.getBlockMeta(position);
		if(meta >= 2 && meta <= 5)
			crops.push(new CropRubber(world, blockid, meta, position));

		ArrayList<Vect> candidates = processHarvestBlock(crops, seen, position);
		ArrayList<Vect> temp = new ArrayList<Vect>();
		while(!candidates.isEmpty()  && crops.size() < 100) {
			for(Vect candidate : candidates) {
				temp.addAll(processHarvestBlock(crops, seen, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}
		
		return crops;
	}

	private ArrayList<Vect> processHarvestBlock(Stack<ICrop> crops, Collection<Vect> seen, Vect position) {

		ArrayList<Vect> candidates = new ArrayList<Vect>();
		
		// Get additional candidates to return
		for(int i = -1; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				for(int k = -1; k < 2; k++) {					
					Vect candidate = new Vect(position.x + i, position.y + j, position.z + k);
					if(candidate.equals(position))
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
					
					int blockid = getBlockId(candidate);
					if(blockid == PluginIC2.rubberwood.itemID) {
						int meta = this.getBlockMeta(candidate);
						if(meta >= 2 && meta <= 5)
							crops.push(new CropRubber(world, blockid, meta, candidate));
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}
		
		return candidates;
	}

}
