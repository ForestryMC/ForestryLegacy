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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;

public class FarmLogicShroom extends FarmLogicArboreal {

	public FarmLogicShroom(IFarmHousing housing) {
		super(housing, new ItemStack[] { new ItemStack(Block.mycelium) }, new ItemStack[] { new ItemStack(Block.mycelium) },
				new ItemStack[] { new ItemStack(Block.dirt) }, Farmables.farmables.get("farmShroom").toArray(new IFarmable[0]));
		yOffset = -1;
	}

	@Override
	public String getName() {
		if(isManual)
			return "Manual Shroom Farm";
		else
			return "Managed Shroom Farm";
	}

	@Override
	public int getIconIndex() {
		return Block.mushroomRed.getBlockTextureFromSide(0);
	}

	@Override
	public int getFertilizerConsumption() {
		return 20;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int)(80 * hydrationModifier);
	}
	
	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

}
