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
package forestry.api.world;

import net.minecraft.world.gen.feature.WorldGenerator;

public interface IWorldGenInterface {
	
	/**
	 *  Retrieves generators for trees identified by a given string.
	 *  
	 *  Returned generator classes take an {@link ITreeGenData} in the constructor.
	 *  
	 * @param ident Unique identifier for tree type. Forestry's convention is 'treeSpecies', i.e. 'treeBaobab', 'treeSequoia'.
	 * @return All generators matching the given ident.
	 */
	Class<? extends WorldGenerator>[] getTreeGenerators(String ident);
}
