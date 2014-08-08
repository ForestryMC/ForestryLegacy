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
package forestry.arboriculture;

import java.util.ArrayList;

import net.minecraft.world.gen.feature.WorldGenerator;
import forestry.api.arboriculture.ITree;
import forestry.api.world.IWorldGenInterface;
import forestry.arboriculture.genetics.BreedingManager;

public class WorldGenHelper implements IWorldGenInterface {

	@Override
	public Class<? extends WorldGenerator>[] getTreeGenerators(String ident) {
		
		ArrayList<Class<? extends WorldGenerator>> generators = new ArrayList<Class<? extends WorldGenerator>>();
		for(ITree tree : BreedingManager.treeTemplates) {
			if(tree.getIdent().equals(ident)) {
				for(Class<?extends WorldGenerator> generator : tree.getGenome().getPrimaryAsTree().getGeneratorClasses())
					generators.add(generator);
			}
					
		}
		
		return generators.toArray(new Class[0]);
	}

}
