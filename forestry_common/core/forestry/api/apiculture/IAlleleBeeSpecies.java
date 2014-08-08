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
package forestry.api.apiculture;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import forestry.api.genetics.IAlleleSpecies;

public interface IAlleleBeeSpecies extends IAlleleSpecies {

	// / Products, Chance
	HashMap<ItemStack, Integer> getProducts();

	// / Specialty, Chance
	HashMap<ItemStack, Integer> getSpecialty();

	// / Only jubilant bees give their specialty product
	boolean isJubilant(IBeeGenome genome, IBeeHousing housing);

}
