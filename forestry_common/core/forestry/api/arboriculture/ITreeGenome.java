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
package forestry.api.arboriculture;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;
import forestry.api.genetics.IGenome;

public interface ITreeGenome extends IGenome {

	IAlleleTreeSpecies getPrimaryAsTree();

	IAlleleTreeSpecies getSecondaryAsTree();

	IFruitProvider getFruitProvider();

	IGrowthProvider getGrowthProvider();

	float getHeight();

	float getFertility();

	/**
	 * @return Determines either a) how many fruit leaves there are or b) the chance for any fruit leave to drop a sapling. Exact usage determined by the IFruitProvider
	 */
	float getYield();

	float getSappiness();
	
	EnumSet<EnumPlantType> getPlantTypes();

	/**
	 * @return Amount of random block ticks required for a sapling to mature into a fully grown tree.
	 */
	int getMaturationTime();

}
