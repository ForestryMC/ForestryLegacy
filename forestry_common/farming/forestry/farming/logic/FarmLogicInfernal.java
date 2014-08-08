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
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.config.Defaults;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public class FarmLogicInfernal extends FarmLogicHomogenous {

	public FarmLogicInfernal(IFarmHousing housing) {
		super(housing,
				new ItemStack[] { new ItemStack(Block.slowSand) },
				new ItemStack[] { new ItemStack(Block.slowSand) },
				new ItemStack[0],
				Farmables.farmables.get("farmInfernal").toArray(new IFarmable[0]));
	}

	@Override
	public String getName() {
		return "Managed Infernal Farm";
	}

	@Override
	public int getIconIndex() {
		return Item.netherStalkSeeds.getIconFromDamage(0);
	}

	@Override
	public String getTextureFile() {
		return Defaults.TEXTURE_ICONS_MINECRAFT;
	}

	@Override
	public int getFertilizerConsumption() {
		return 20;
	}
	
	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return 0;
	}
	
	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
		
		world = housing.getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for(int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y + 1, z, direction, i);
			for(IFarmable farmable : germlings) {
				ICrop crop = farmable.getCropAt(world, position.x, position.y, position.z);
				if(crop != null)
					crops.push(crop);
			}			
			
		}
		return crops;
		
	}

	@Override
	protected boolean maintainGermlings(int x, int y, int z, ForgeDirection direction, int extent) {
		
		for(int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			if(!isAirBlock(position)
					&& !Utils.isReplaceableBlock(world, position.x, position.y, position.z))
				continue;
			
			ItemStack below = getAsItemStack(position.add(new Vect(0, -1, 0)));
			if(ground[0].itemID != below.itemID)
				continue;
			
			return trySetCrop(position);
		}
		
		return false;
	}

	private boolean trySetCrop(Vect position) {
		
		for(IFarmable candidate : germlings) {			
			if(housing.plantGermling(candidate, world, position.x, position.y, position.z))
				return true;
		}
		
		return false;
	}
	

}
