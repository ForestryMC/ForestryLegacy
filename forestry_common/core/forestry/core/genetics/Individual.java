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
package forestry.core.genetics;

import net.minecraft.nbt.NBTTagCompound;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IIndividual;

public abstract class Individual implements IIndividual {

	protected boolean isAnalyzed = false;

	@Override
	public boolean isAnalyzed() {
		return isAnalyzed;
	}

	@Override
	public boolean analyze() {
		if (isAnalyzed)
			return false;

		isAnalyzed = true;
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound == null)
			return;

		isAnalyzed = nbttagcompound.getBoolean("IsAnalyzed");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setBoolean("IsAnalyzed", isAnalyzed);
	}

	@Override
	public boolean hasEffect() {
		return getGenome().getPrimary().hasEffect();
	}

	@Override
	public boolean isSecret() {
		return getGenome().getPrimary().isSecret();
	}
	
	@Override
	public boolean isGeneticEqual(IIndividual other) {
		if(!(other instanceof ITree))
			return false;
		
		ITree tree = (ITree)other;
		return getGenome().isGeneticEqual(tree.getGenome());
	}

}
