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
package forestry.core;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IFuelHandler;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;

public class FuelHandler implements IFuelHandler {

	@Override
	public int getBurnTime(ItemStack fuel) {
		if (fuel.itemID == ForestryItem.peat.itemID)
			return 1600;
		else if(ForestryBlock.saplingGE != null && fuel.itemID == ForestryBlock.saplingGE.blockID)
			return 100;
		else
			return 0;
	}

}
