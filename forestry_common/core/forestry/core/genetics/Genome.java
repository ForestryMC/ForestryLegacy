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
import net.minecraft.nbt.NBTTagList;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IGenome;
import forestry.core.config.Config;

public abstract class Genome implements IGenome {

	private static final String SLOT_TAG = "Slot";
	
	private IChromosome[] chromosomes;
	protected IAllele[] defaultTemplate;

	// / CONSTRUCTOR
	public Genome(IAllele[] defaultTemplate, NBTTagCompound nbttagcompound) {
		this(defaultTemplate);
		this.chromosomes = new Chromosome[defaultTemplate.length];
		readFromNBT(nbttagcompound);
	}

	public Genome(IAllele[] defaultTemplate, IChromosome[] chromosomes) {
		this(defaultTemplate);
		this.chromosomes = chromosomes;
	}

	public Genome(IAllele[] defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		NBTTagList nbttaglist = nbttagcompound.getTagList("Chromosomes");
		chromosomes = new Chromosome[chromosomes.length];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound1.getByte(SLOT_TAG);
			if (byte0 >= 0 && byte0 < chromosomes.length) {
				Chromosome chromosome = Chromosome.loadChromosomeFromNBT(nbttagcompound1);
				chromosomes[byte0] = chromosome;
				if (Config.clearInvalidChromosomes) {
					chromosome.overrideInvalidAlleles(defaultTemplate[byte0]);
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < chromosomes.length; i++)
			if (chromosomes[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte(SLOT_TAG, (byte) i);
				chromosomes[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("Chromosomes", nbttaglist);

	}

	// / INFORMATION RETRIEVAL
	@Override
	public IChromosome[] getChromosomes() {
		return chromosomes;
	}

	@Override
	public IAllele getActiveAllele(int chromosome) {
		return chromosomes[chromosome].getActiveAllele();
	}

	@Override
	public IAllele getInactiveAllele(int chromosome) {
		return chromosomes[chromosome].getInactiveAllele();
	}

	@Override
	public boolean isGeneticEqual(IGenome other) {
		IChromosome[] genetics = other.getChromosomes();
		if(chromosomes.length != genetics.length)
			return false;
		
		for(int i = 0; i < chromosomes.length; i++) {
			IChromosome chromosome = chromosomes[i];
			if(chromosome == null && genetics[i] != null)
				return false;
			if(chromosome != null && genetics[i] == null)
				return false;
			if(chromosome == null && genetics[i] == null)
				continue;
			
			if(!chromosome.getPrimaryAllele().getUID().equals(genetics[i].getPrimaryAllele().getUID()))
				return false;
			if(!chromosome.getSecondaryAllele().getUID().equals(genetics[i].getSecondaryAllele().getUID()))
				return false;
		}
		
		return true;
	}
}
