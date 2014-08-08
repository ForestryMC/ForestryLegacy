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

import net.minecraft.world.World;

public interface IGrowthProvider {
	
	/**
	 * Check to see whether a sapling at the given location with the given genome can grow into a tree. 
	 * @param genome
	 * @param world
	 * @param xPos
	 * @param yPos
	 * @param zPos
	 * @param expectedGirth
	 * @param expectedHeight
	 * @return
	 */
	boolean canGrow(ITreeGenome genome, World world, int xPos, int yPos, int zPos, int expectedGirth, int expectedHeight);
	
	EnumGrowthConditions getGrowthConditions(ITreeGenome genome, World world, int xPos, int yPos, int zPos);

	/**
	 * @return Short, human-readable identifier used in the treealyzer.
	 */
	String getDescription();

	/**
	 * @return Detailed description of growth behaviour used in the treealyzer.
	 */
	String[] getInfo();


}
