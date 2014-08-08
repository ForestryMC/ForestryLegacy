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
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IGenome;
import forestry.api.world.ITreeGenData;
import forestry.core.config.Defaults;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.Individual;
import forestry.core.utils.StringUtil;

public class Tree extends Individual implements ITree, ITreeGenData, IPlantable {

	private ITreeGenome genome;
	private ITreeGenome mate;

	// Possibly some kind of tick counter for growth.

	// / CONSTRUCTOR
	public Tree(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	public Tree(ITreeGenome genome) {
		this.genome = genome;
	}

	public Tree(World world, ITreeGenome genome) {
		this.genome = genome;

	}
	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		
		if (nbttagcompound.hasKey("Genome")) {
			genome = new TreeGenome(nbttagcompound.getCompoundTag("Genome"));
		}
		if (nbttagcompound.hasKey("Mate")) {
			mate = new TreeGenome(nbttagcompound.getCompoundTag("Mate"));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);
		
		if (genome != null) {
			NBTTagCompound NBTmachine = new NBTTagCompound();
			genome.writeToNBT(NBTmachine);
			nbttagcompound.setTag("Genome", NBTmachine);
		}
		if (mate != null) {
			NBTTagCompound NBTmachine = new NBTTagCompound();
			mate.writeToNBT(NBTmachine);
			nbttagcompound.setTag("Mate", NBTmachine);
		}

	}

	// / INTERACTION
	@Override
	public void mate(ITree other) {
		mate = new TreeGenome(other.getGenome().getChromosomes());
	}

	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, World world, int biomeid, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEffectData[] doFX(IEffectData[] storedData, World world, int biomeid, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	/* GROWTH */
	@Override
	public WorldGenerator getTreeGenerator(World world, int x, int y, int z, boolean wasBonemealed) {
		return genome.getPrimaryAsTree().getGenerator(this, world, x, y, z);
	}

	
	@Override
	public boolean canStay(World world, int x, int y, int z) {
		Block block = Block.blocksList[world.getBlockId(x, y - 1, z)];
		if(block == null)
			return false;
		
		for(EnumPlantType type : getPlantTypes()) {
			this.plantType = type;
			if(block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this))
				return true;
		}
		
		return false;
	}
	private EnumPlantType plantType;
	@Override public EnumPlantType getPlantType(World world, int x, int y, int z) { return plantType; }
	@Override public int getPlantID(World world, int x, int y, int z) { return 0; }
	@Override public int getPlantMetadata(World world, int x, int y, int z) { return 0; }

	@Override
	public boolean canGrow(World world, int x, int y, int z, int expectedGirth, int expectedHeight) {
		return genome.getGrowthProvider().canGrow(genome, world, x, y, z, expectedGirth, expectedHeight);
	}

	@Override
	public int getRequiredMaturity() {
		return genome.getMaturationTime();
	}
	
	@Override
	public EnumGrowthConditions getGrowthCondition(World world, int x, int y, int z) {
		return genome.getGrowthProvider().getGrowthConditions(getGenome(), world, x, y, z);
	}

	@Override
	public int getGirth(World world, int x, int y, int z) {
		return genome.getPrimaryAsTree().getGirth();
	}


	@Override
	public float getHeightModifier() {
		return genome.getHeight();
	}

	@Override
	public void setLeaves(World world, int x, int y, int z) {
		TreeManager.treeInterface.setLeaves(world, this, x, y, z);
	}

	@Override
	public String getIdent() {
		return genome.getPrimary().getUID();
	}

	@Override
	public String getDisplayName() {
		return genome.getPrimary().getName();
	}

	@Override
	public ITreeGenome getMate() {
		return this.mate;
	}

	@Override
	public ITreeGenome getGenome() {
		return this.genome;
	}

	@Override
	public boolean isPureBred(EnumTreeChromosome chromosome) {
		return genome.getActiveAllele(chromosome.ordinal()).getUID().equals(genome.getInactiveAllele(chromosome.ordinal()).getUID());
	}

	@Override
	public EnumSet<EnumPlantType> getPlantTypes() {
		EnumSet<EnumPlantType> tolerated = genome.getPlantTypes();
		tolerated.add(genome.getPrimaryAsTree().getPlantType());
		return tolerated;
	}
	
	@Override
	public void addTooltip(List<String> list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add("<" + StringUtil.localize("gui.unknown") + ">");
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleTreeSpecies primary = genome.getPrimaryAsTree();
		IAlleleTreeSpecies secondary = genome.getSecondaryAsTree();
		if (!isPureBred(EnumTreeChromosome.SPECIES)) {
			list.add("\u00A79" + primary.getName() + "-" + secondary.getName()
					+ " " + StringUtil.localize("gui.hybrid"));
		}
		list.add("H: " + rateHeight(genome.getHeight()));
		list.add("\u00A7aF: " + StringUtil.localize(genome.getFruitProvider().getDescription()));

	}

	/* REPRODUCTION */
	@Override
	public ITree[] getSaplings(World world, int x, int y, int z, float modifier) {
		ArrayList<ITree> prod = new ArrayList<ITree>();

		float chance = genome.getFertility() * modifier;
			
		if (Defaults.DEBUG || world.rand.nextFloat() <= chance) {
			if(this.getMate() == null)
				prod.add(TreeManager.treeInterface.getTree(world, new TreeGenome(genome.getChromosomes())));
			else
				prod.add(createOffspring(world, x, y, z));
		}

		return prod.toArray(new ITree[0]);
	}

	private ITree createOffspring(World world, int x, int y, int z) {

		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated = mutateSpecies(world, x, y, z, genome, mate);
		if(mutated == null)
			mutated = mutateSpecies(world, x, y, z, mate, genome);
		
		if (mutated != null)
			return new Tree(world, new TreeGenome(mutated));

		for (int i = 0; i < parent1.length; i++)
			if (parent1[i] != null && parent2[i] != null) {
				chromosomes[i] = Chromosome.inheritChromosome(world.rand, parent1[i], parent2[i]);
			}

		return new Tree(world, new TreeGenome(chromosomes));
	}

	private IChromosome[] mutateSpecies(World world, int x, int y, int z, IGenome genomeOne, IGenome genomeTwo) {

		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		IGenome genome0;
		IGenome genome1;
		IAllele allele0;
		IAllele allele1;

		if (world.rand.nextBoolean()) {
			allele0 = parent1[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent2[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = parent2[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent1[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		Collections.shuffle(TreeManager.treeMutations);
		for (ITreeMutation mutation : TreeManager.treeMutations) {
			int chance = 0;

			// Stop blacklisted species.
			//if (BeeManager.breedingManager.isBlacklisted(mutation.getTemplate()[0].getUID())) {
			//	continue;
			//}

			if ((chance = mutation.getChance(world, x, y, z, allele0, allele1, genome0, genome1)) > 0)
				if (world.rand.nextInt(100) < chance) {
					//IApiaristTracker breedingTracker = BeeManager.breedingManager.getApiaristTracker(world);
					//breedingTracker.registerMutation(mutation);
					return TreeManager.treeInterface.templateAsChromosomes(mutation.getTemplate());
				}
		}

		return null;
	}
	
	/* PRODUCTION */
	@Override
	public boolean canBearFruit() {
		return genome.getPrimaryAsTree().getSuitableFruit().contains(genome.getFruitProvider().getFamily());
	}
	
	@Override
	public ItemStack[] getProduceList() {
		return genome.getFruitProvider().getProducts();
	}

	@Override
	public ItemStack[] getSpecialtyList() {
		return genome.getFruitProvider().getSpecialty();
	}

	@Override
	public ItemStack[] produceStacks(World world, int x, int y, int z, int ripeningTime) {
		return genome.getFruitProvider().getFruits(genome, world, x, y, z, ripeningTime);
	}


	@Override
	public ITree copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Tree(nbttagcompound);
	}

	/* HELPER FUNCTIONS */
	public static String rateHeight(float height) {
		if(height >= 2.0f)
			return StringUtil.localize("gui.gigantic");
		else if (height >= 1.75f)
			return StringUtil.localize("gui.largest");
		else if (height >= 1.5f)
			return StringUtil.localize("gui.larger");
		else if (height >= 1.25f)
			return StringUtil.localize("gui.large");
		else if (height >= 1.0f)
			return StringUtil.localize("gui.average");
		else if (height >= 0.75f)
			return StringUtil.localize("gui.small");
		else if (height >= 0.5f)
			return StringUtil.localize("gui.smaller");
		else
			return StringUtil.localize("gui.smallest");
	}

	public static String rateFertility(float height) {
		if(height >= 0.3f)
			return StringUtil.localize("gui.triple");
		else if (height >= 0.2f)
			return StringUtil.localize("gui.double");
		else
			return StringUtil.localize("gui.normal");
	}

	public static String rateYield(float yield) {
		if(yield >= 0.35f)
			return StringUtil.localize("gui.largest");
		else if (yield >= 0.30f)
			return StringUtil.localize("gui.larger");
		else if (yield >= 0.25f)
			return StringUtil.localize("gui.large");
		else if(yield >= 0.2)
			return StringUtil.localize("gui.normal");
		else if(yield >= 0.15f)
			return StringUtil.localize("gui.low");
		else if(yield >= 0.1f)
			return StringUtil.localize("gui.lower");
		else
			return StringUtil.localize("gui.lowest");
			
	}

	public static String rateSappiness(float sappiness) {
		if(sappiness >= 0.35f)
			return StringUtil.localize("gui.highest");
		else if (sappiness >= 0.30f)
			return StringUtil.localize("gui.higher");
		else if (sappiness >= 0.25f)
			return StringUtil.localize("gui.high");
		else if(sappiness >= 0.2)
			return StringUtil.localize("gui.average");
		else if(sappiness >= 0.15f)
			return StringUtil.localize("gui.low");
		else if(sappiness >= 0.1f)
			return StringUtil.localize("gui.lower");
		else
			return StringUtil.localize("gui.lowest");
			
	}

	public static String rateMaturity(int maturity) {
		if (maturity >= 8)
			return StringUtil.localize("gui.slowestspeed");
		else if (maturity >= 7)
			return StringUtil.localize("gui.slowerspeed");
		else if (maturity >= 6)
			return StringUtil.localize("gui.slowspeed");
		else if (maturity >= 5)
			return StringUtil.localize("gui.normalspeed");
		else if (maturity >= 4)
			return StringUtil.localize("gui.fastspeed");
		else if (maturity >= 3)
			return StringUtil.localize("gui.fasterspeed");
		else
			return StringUtil.localize("gui.fastestspeed");
	}

}
