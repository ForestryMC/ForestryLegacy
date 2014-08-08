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
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.HashMap;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeBreedingManager;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;

public class BreedingManager implements ITreeBreedingManager {

	// / TEMPLATES
	public static HashMap<String, IAllele[]> speciesTemplates = new HashMap<String, IAllele[]>();
	public static ArrayList<ITree> treeTemplates = new ArrayList<ITree>();

	@Override
	public void registerTreeTemplate(IAllele[] template) {
		registerTreeTemplate(template[0].getUID(), template);
	}

	@Override
	public void registerTreeTemplate(String identifier, IAllele[] template) {
		treeTemplates.add(new Tree(TreeManager.treeInterface.templateAsGenome(template)));
		speciesTemplates.put(identifier, template);
	}

	@Override
	public IAllele[] getTreeTemplate(String identifier) {
		return speciesTemplates.get(identifier);
	}

	@Override
	public IAllele[] getDefaultTreeTemplate() {
		return TreeTemplates.getDefaultTemplate();
	}

}
