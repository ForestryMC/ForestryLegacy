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

import java.util.ArrayList;

public class TreeManager {
	public static int treeSpeciesCount = 0;
	public static ITreeInterface treeInterface;
	public static ITreeBreedingManager breedingManager;
	
	/**
	 * List of possible mutations on fruit alleles.
	 */
	public static ArrayList<ITreeMutation> treeMutations = new ArrayList<ITreeMutation>();

}
