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
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public interface IFarmLogic {

	int getFertilizerConsumption();
	int getWaterConsumption(float hydrationModifier);
	boolean isAcceptedResource(ItemStack itemstack);
	boolean isAcceptedGermling(ItemStack itemstack);

	Collection<ItemStack> collect();

	boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent);

	Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent);
	
	int getIconIndex();
	String getTextureFile();

	String getName();
}
