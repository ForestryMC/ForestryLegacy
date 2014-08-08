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

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IMutation;
import forestry.core.genetics.Allele;
import forestry.core.genetics.Chromosome;

public class TreeTemplates {

	/* MUTATIONS */
	public static IMutation larchA;
	public static IMutation larchB;
	public static IMutation pineA;
	public static IMutation sequoiaA;
	
	public static IMutation limeA;
	
	public static IMutation cherryA;
	public static IMutation cherryB;
	
	public static IMutation walnutA;
	public static IMutation chestnutA;
	public static IMutation chestnutB;
	
	public static IMutation teakA;
	public static IMutation kapokA;
	public static IMutation ebonyA;
	public static IMutation mahoganyA;
	
	public static IMutation balsaA;
	public static IMutation acaciaA;
	public static IMutation wengeA;
	public static IMutation baobabA;
	
	public static IMutation willowA;
	public static IMutation willowB;
	public static IMutation willowC;
	
	public static IMutation sipiriA;
	
	/* TEMPLATES */
	public static IAllele[] getDefaultTemplate() {
		IAllele[] alleles = new IAllele[EnumTreeChromosome.values().length];

		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeOak;
		alleles[EnumTreeChromosome.FRUITS.ordinal()] = Allele.fruitNone;
		alleles[EnumTreeChromosome.GROWTH.ordinal()] = Allele.growthLightlevel;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightSmall;
		alleles[EnumTreeChromosome.FERTILITY.ordinal()] = Allele.saplingsDefault;
		alleles[EnumTreeChromosome.YIELD.ordinal()] = Allele.yieldLowest;
		alleles[EnumTreeChromosome.PLANT.ordinal()] = Allele.plantTypeNone;
		alleles[EnumTreeChromosome.SAPPINESS.ordinal()] = Allele.sappinessLowest;
		alleles[EnumTreeChromosome.TERRITORY.ordinal()] = Allele.territoryDefault;
		alleles[EnumTreeChromosome.EFFECT.ordinal()] = Allele.leavesNone;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationAverage;

		return alleles;
	}

	public static IAllele[] getOakTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.FRUITS.ordinal()] = Allele.fruitApple;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationFaster;
		return alleles;
	}

	public static IAllele[] getBirchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeBirch;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationFaster;
		return alleles;
	}

	public static IAllele[] getSpruceTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeSpruce;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightAverage;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationFaster;
		return alleles;
	}

	public static IAllele[] getJungleTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeJungle;
		alleles[EnumTreeChromosome.FRUITS.ordinal()] = Allele.fruitCocoa;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLarger;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationFast;
		return alleles;
	}

	public static IAllele[] getBalsaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeBalsa;
		alleles[EnumTreeChromosome.FERTILITY.ordinal()] = Allele.saplingsTriple;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLarge;
		return alleles;
	}

	public static IAllele[] getSequoiaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeSequioa;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLargest;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationSlower;
		return alleles;
	}

	public static IAllele[] getLarchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeLarch;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightAverage;
		return alleles;
	}

	public static IAllele[] getPineTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treePine;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightAverage;
		return alleles;
	}

	public static IAllele[] getCherryTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeCherry;
		alleles[EnumTreeChromosome.FRUITS.ordinal()] = Allele.fruitCherry;
		alleles[EnumTreeChromosome.YIELD.ordinal()] = Allele.yieldAverage;
		alleles[EnumTreeChromosome.SAPPINESS.ordinal()] = Allele.sappinessAverage;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightSmaller;
		return alleles;
	}

	public static IAllele[] getLimeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeLime;
		alleles[EnumTreeChromosome.YIELD.ordinal()] = Allele.yieldLower;
		return alleles;
	}

	public static IAllele[] getTeakTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeTeak;
		return alleles;
	}

	public static IAllele[] getKapokTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeKapok;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLarge;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationSlow;
		return alleles;
	}

	public static IAllele[] getEbonyTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeEbony;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightAverage;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationSlower;
		return alleles;
	}

	public static IAllele[] getMahoganyTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeMahogany;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLarge;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationSlow;
		return alleles;
	}

	public static IAllele[] getChestnutTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeChestnut;
		alleles[EnumTreeChromosome.FRUITS.ordinal()] = Allele.fruitChestnut;
		alleles[EnumTreeChromosome.YIELD.ordinal()] = Allele.yieldAverage;
		alleles[EnumTreeChromosome.SAPPINESS.ordinal()] = Allele.sappinessAverage;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLarge;
		return alleles;
	}

	public static IAllele[] getAcaciaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeAcacia;
		alleles[EnumTreeChromosome.PLANT.ordinal()] = Allele.plantTypeDesert;
		return alleles;
	}

	public static IAllele[] getWengeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeWenge;
		return alleles;
	}

	public static IAllele[] getBaobabTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeBaobab;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLarge;
		alleles[EnumTreeChromosome.PLANT.ordinal()] = Allele.plantTypeDesert;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationSlow;
		return alleles;
	}

	public static IAllele[] getWalnutTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeWalnut;
		alleles[EnumTreeChromosome.FRUITS.ordinal()] = Allele.fruitWalnut;
		alleles[EnumTreeChromosome.YIELD.ordinal()] = Allele.yieldAverage;
		alleles[EnumTreeChromosome.SAPPINESS.ordinal()] = Allele.sappinessAverage;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightAverage;
		return alleles;
	}

	public static IAllele[] getWillowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeWillow;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightAverage;
		alleles[EnumTreeChromosome.FERTILITY.ordinal()] = Allele.saplingsLow;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationFaster;
		return alleles;
	}

	public static IAllele[] getSipiriTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = Allele.treeSipiri;
		alleles[EnumTreeChromosome.GROWTH.ordinal()] = Allele.growthTropical;
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = Allele.heightLarge;
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = Allele.maturationSlow;
		return alleles;
	}

	// / HELPER FUNCTIONS

	public static Chromosome[] templateAsChromosomes(IAllele[] template) {
		Chromosome[] chromosomes = new Chromosome[template.length];
		for (int i = 0; i < template.length; i++)
			if (template[i] != null) {
				chromosomes[i] = new Chromosome(template[i]);
			}

		return chromosomes;
	}

	public static Chromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive) {
		Chromosome[] chromosomes = new Chromosome[templateActive.length];
		for (int i = 0; i < templateActive.length; i++)
			if (templateActive[i] != null) {
				chromosomes[i] = new Chromosome(templateActive[i], templateInactive[i]);
			}

		return chromosomes;
	}

	public static ITreeGenome templateAsGenome(IAllele[] template) {
		return new TreeGenome(templateAsChromosomes(template));
	}

	public static ITreeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new TreeGenome(templateAsChromosomes(templateActive, templateInactive));
	}
}
