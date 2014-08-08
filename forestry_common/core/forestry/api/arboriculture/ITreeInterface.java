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

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IIndividual;

public interface ITreeInterface {
	boolean isGermling(ItemStack itemstack);

	boolean isPollen(ItemStack itemstack);

	boolean isPollinated(ItemStack itemstack);

	ITree getTree(World world, int x, int y, int z);
	
	ITree getTree(ItemStack itemstack);

	ITree getTree(World world, ITreeGenome genome);

	ItemStack getGermlingStack(ITree tree, EnumGermlingType type);

	boolean plantSapling(World world, ITree tree, int x, int y, int z);

	IChromosome[] templateAsChromosomes(IAllele[] template);

	IChromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive);

	ITreeGenome templateAsGenome(IAllele[] template);

	ITreeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive);

	boolean setLeaves(World world, IIndividual tree, int x, int y, int z);
}
