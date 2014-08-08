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

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;

public class Mutation implements ITreeMutation {

	int chance;
	boolean isSecret = false;

	IAllele allele0;
	IAllele allele1;

	IAllele[] template;
	
	private float minTemperature = 0.0f;
	private float maxTemperature = 2.0f;
	private float minRainfall = 0.0f;
	private float maxRainfall = 2.0f;

	public Mutation(IAllele allele0, IAllele allele1, IAllele[] template, int chance) {
		this.allele0 = allele0;
		this.allele1 = allele1;
		this.template = template;
		this.chance = chance;

		TreeManager.treeMutations.add(this);
	}
	
	public Mutation setIsSecret() {
		isSecret = true;
		return this;
	}

	public Mutation setTemperature(float minTemperature, float maxTemperature) {
		this.minTemperature = minTemperature;
		this.maxTemperature = maxTemperature;
		return this;
	}

	public Mutation setRainfall(float minRainfall, float maxRainfall) {
		this.minRainfall = minRainfall;
		this.maxRainfall = maxRainfall;
		return this;
	}

	public Mutation setTemperatureRainfall(float minTemperature, float maxTemperature, float minRainfall, float maxRainfall) {
		this.minTemperature = minTemperature;
		this.maxTemperature = maxTemperature;
		this.minRainfall = minRainfall;
		this.maxRainfall = maxRainfall;
		return this;
	}

	@Override
	public IAllele getAllele0() {
		return allele0;
	}

	@Override
	public IAllele getAllele1() {
		return allele1;
	}

	@Override
	public int getBaseChance() {
		return chance;
	}

	@Override
	public IAllele[] getTemplate() {
		return template;
	}

	@Override
	public boolean isPartner(IAllele allele) {
		return allele0.getUID().equals(allele.getUID()) || allele1.getUID().equals(allele.getUID());
	}

	@Override
	public IAllele getPartner(IAllele allele) {
		if (allele0.getUID().equals(allele.getUID()))
			return allele1;
		else
			return allele0;
	}

	@Override
	public boolean isSecret() {
		return isSecret;
	}

	@Override
	public int getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		int processedChance = chance;
		
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);
		if (biome.temperature < minTemperature || biome.temperature > maxTemperature)
			return 0;
		if (biome.rainfall < minRainfall || biome.rainfall > maxRainfall)
			return 0;

		if (this.allele0.getUID().equals(allele0.getUID()) && this.allele1.getUID().equals(allele1.getUID()))
			return processedChance;
		if (this.allele1.getUID().equals(allele0.getUID()) && this.allele0.getUID().equals(allele1.getUID()))
			return processedChance;

		return 0;
	}

}
