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
package forestry.apiculture.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.IFlowerProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IApiaristTracker;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.ClimateHelper;
import forestry.core.genetics.Individual;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Vect;

public class Bee extends Individual implements IBee {

	public IBeeGenome genome;
	public IBeeGenome mate;

	private int generation;
	private boolean isNatural;
	private boolean isIrregularMating;

	private int health;
	private int maxHealth;

	// / CONSTRUCTOR
	public Bee(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	public Bee(World world, IBeeGenome genome, IBee mate) {
		this(world, genome);
		this.mate = mate.getGenome();
		this.isIrregularMating = mate.isNatural() != this.isNatural;
	}

	public Bee(IBeeGenome genome, IBee mate) {
		this(genome);
		this.mate = mate.getGenome();
		this.isIrregularMating = mate.isNatural() != this.isNatural;
	}

	public Bee(World world, IBeeGenome genome) {
		this(world, genome, true, 0);
	}

	public Bee(IBeeGenome genome) {
		this(genome, true, 0);
	}

	public Bee(World world, IBeeGenome genome, boolean isNatural, int generation) {
		this.genome = genome;
		health = maxHealth = genome.getLifespan();
		this.isNatural = isNatural;
		this.generation = generation;
	}

	public Bee(IBeeGenome genome, boolean isNatural, int generation) {
		this.genome = genome;
		health = maxHealth = genome.getLifespan();
		this.isNatural = isNatural;
		this.generation = generation;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		if (nbttagcompound == null) {
			this.genome = BeeManager.beeInterface.templateAsGenome(BeeTemplates.getForestTemplate());
			return;
		}

		if (nbttagcompound.hasKey("NA")) {
			isNatural = nbttagcompound.getBoolean("NA");
		} else {
			isNatural = true;
		}
		isIrregularMating = nbttagcompound.getBoolean("IM");
		generation = nbttagcompound.getInteger("GEN");

		health = nbttagcompound.getInteger("Health");
		maxHealth = nbttagcompound.getInteger("MaxH");

		if (nbttagcompound.hasKey("Genome")) {
			genome = new BeeGenome(nbttagcompound.getCompoundTag("Genome"));
		} else {
			genome = BeeManager.beeInterface.templateAsGenome(BeeTemplates.getForestTemplate());
		}
		if (nbttagcompound.hasKey("Mate")) {
			mate = new BeeGenome(nbttagcompound.getCompoundTag("Mate"));
		}

		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("NA", isNatural);
		nbttagcompound.setBoolean("IM", isIrregularMating);
		nbttagcompound.setInteger("GEN", generation);

		nbttagcompound.setInteger("Health", health);
		nbttagcompound.setInteger("MaxH", maxHealth);

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

	/// GENERATION
	@Override
	public IBee setNatural(boolean flag) {
		this.isNatural = flag;
		return this;
	}

	@Override
	public boolean isIrregularMating() {
		return this.isIrregularMating;
	}

	@Override
	public boolean isNatural() {
		return this.isNatural;
	}

	@Override
	public int getGeneration() {
		return generation;
	}
	
	// / INTERACTION
	@Override
	public void age(World world, float lifespanModifier) {

		if (lifespanModifier < 0.001f) {
			this.health = 0;
			return;
		}

		float ageModifier = 1.0f / lifespanModifier;

		while (ageModifier > 1.0f) {
			decreaseHealth();
			ageModifier--;
		}
		if (world.rand.nextFloat() < ageModifier) {
			decreaseHealth();
		}

	}

	public void decreaseHealth() {
		if (health > 0) {
			health--;
		}
	}

	@Override
	public void mate(IBee drone) {
		mate = drone.getGenome();
		this.isIrregularMating = drone.isNatural() != this.isNatural;
	}

	public void setIsNatural(boolean flag) {
		this.isNatural = flag;
	}

	// / EFFECTS
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		if (effect == null)
			return null;

		storedData[0] = doEffect(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable())
			return storedData;

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(EnumBeeChromosome.EFFECT.ordinal());
		if (!secondary.isCombinable())
			return storedData;

		storedData[1] = doEffect(secondary, storedData[1], housing);

		return storedData;
	}

	private IEffectData doEffect(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(genome, storedData, housing);
	}

	@Override
	public IEffectData[] doFX(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		if (effect == null)
			return null;

		storedData[0] = doFX(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable())
			return storedData;

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(EnumBeeChromosome.EFFECT.ordinal());
		if (!secondary.isCombinable())
			return storedData;

		storedData[1] = doFX(secondary, storedData[1], housing);

		return storedData;
	}

	private IEffectData doFX(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		return effect.doFX(genome, storedData, housing);
	}

	// / INFORMATION
	@Override
	public IBeeGenome getGenome() {
		return genome;
	}

	@Override
	public IBeeGenome getMate() {
		return mate;
	}

	@Override
	public boolean isAlive() {
		return health > 0;
	}

	@Override
	public boolean isPureBred(EnumBeeChromosome chromosome) {
		return genome.getActiveAllele(chromosome.ordinal()).getUID().equals(genome.getInactiveAllele(chromosome.ordinal()).getUID());
	}

	@Override
	public boolean canSpawn() {
		return mate != null;
	}

	@Override
	public int isWorking(IBeeHousing housing) {

		World world = housing.getWorld();
		// / Rain needs tolerant flyers
		if (world.isRaining() && !genome.getTolerantFlyer() && housing.getHumidity() != EnumHumidity.ARID && !housing.isSealed())
			return EnumErrorCode.ISRAINING.ordinal();

		// / Night or darkness requires nocturnal species
		if (!world.isDaytime() && !genome.getNocturnal() && !housing.isSelfLighted())
			return EnumErrorCode.NOTDAY.ordinal();

		if (world.getBlockLightValue(housing.getXCoord(), housing.getYCoord() + 2, housing.getZCoord()) <= Defaults.APIARY_MIN_LEVEL_LIGHT
				&& !genome.getNocturnal() && !housing.isSelfLighted())
			return EnumErrorCode.NOTLUCID.ordinal();

		// / No sky, except if in hell
		if (housing.getBiomeId() != BiomeGenBase.hell.biomeID && !world.canBlockSeeTheSky(housing.getXCoord(), housing.getYCoord() + 3, housing.getZCoord())
				&& !genome.getCaveDwelling() && !housing.isSunlightSimulated())
			return EnumErrorCode.NOSKY.ordinal();

		// / And finally biome check
		if (!checkBiomeHazard(world, housing.getTemperature(), housing.getHumidity(), housing.getXCoord(), housing.getYCoord(), housing.getZCoord()))
			return EnumErrorCode.INVALIDBIOME.ordinal();

		return EnumErrorCode.OK.ordinal();
	}

	private boolean checkBiomeHazard(World world, EnumTemperature temperature, EnumHumidity humidity, int x, int y, int z) {

		EnumTemperature beeTemperature = genome.getPrimaryAsBee().getTemperature();
		EnumTolerance temperatureTolerance = genome.getToleranceTemp();

		ArrayList<EnumTemperature> toleratedTemperatures = ClimateHelper.getToleratedTemperature(beeTemperature, temperatureTolerance);
		boolean validTemp = false;

		validTemp = toleratedTemperatures.contains(temperature);

		if (!validTemp)
			return false;

		EnumHumidity beeHumidity = genome.getPrimaryAsBee().getHumidity();
		EnumTolerance humidityTolerance = genome.getToleranceHumid();

		ArrayList<EnumHumidity> toleratedHumidity = ClimateHelper.getToleratedHumidity(beeHumidity, humidityTolerance);

		boolean validHumidity = false;

		validHumidity = toleratedHumidity.contains(humidity);

		return validHumidity;
	}

	@Override
	public boolean hasFlower(IBeeHousing housing) {

		IFlowerProvider provider = genome.getFlowerProvider();

		Vect coords = new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord());
		Vect posCurrent = new Vect(0, 0, 0);
		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(housing.getTerritoryModifier(genome));

		if (area.x < 1) {
			area.x = 1;
		}
		if (area.y < 1) {
			area.y = 1;
		}
		if (area.z < 1) {
			area.z = 1;
		}

		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		boolean hasFlower = false;

		while (advancePosition(posCurrent, area)) {

			Vect posBlock = posCurrent.add(coords);
			posBlock = posBlock.add(offset);

			if (provider.isAcceptedFlower(housing.getWorld(), genome, posBlock.x, posBlock.y, posBlock.z)) {
				hasFlower = true;
				break;
			}

		}

		return hasFlower;
	}

	private boolean advancePosition(Vect posCurrent, Vect area) {
		// Increment z first until end reached
		if (posCurrent.z < area.z - 1) {
			posCurrent.z++;
		} else {
			posCurrent.z = 0;

			if (posCurrent.x < area.x - 1) {
				posCurrent.x++;
			} else {
				posCurrent.x = 0;

				if (posCurrent.y < area.y - 1) {
					posCurrent.y++;
				} else
					return false;
			}
		}

		return true;
	}

	@Override
	public ArrayList<Integer> getSuitableBiomeIds() {
		EnumTemperature temperature = genome.getPrimaryAsBee().getTemperature();
		EnumTolerance temperatureTolerance = genome.getToleranceTemp();

		ArrayList<EnumTemperature> toleratedTemperatures = ClimateHelper.getToleratedTemperature(temperature, temperatureTolerance);

		EnumHumidity humidity = genome.getPrimaryAsBee().getHumidity();
		EnumTolerance humidityTolerance = genome.getToleranceHumid();

		ArrayList<EnumHumidity> toleratedHumidities = ClimateHelper.getToleratedHumidity(humidity, humidityTolerance);

		ArrayList<Integer> biomeIdsTemp = new ArrayList<Integer>();
		for (EnumTemperature temp : toleratedTemperatures) {
			biomeIdsTemp.addAll(EnumTemperature.getBiomeIds(temp));
		}

		ArrayList<Integer> biomeIdsHumid = new ArrayList<Integer>();
		for (EnumHumidity humid : toleratedHumidities) {
			biomeIdsHumid.addAll(EnumHumidity.getBiomeIds(humid));
		}

		biomeIdsTemp.retainAll(biomeIdsHumid);

		return biomeIdsTemp;
	}

	@Override
	public String getIdent() {
		return genome.getPrimaryAsBee().getUID();
	}

	@Override
	public String getDisplayName() {
		return genome.getPrimaryAsBee().getName();
	}

	@Override
	public int getHealth() {
		return health;
	}

	@Override
	public int getMaxHealth() {
		return this.maxHealth;
	}

	@Override
	public void addTooltip(List list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add("<" + StringUtil.localize("gui.unknown") + ">");
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleBeeSpecies primary = genome.getPrimaryAsBee();
		IAlleleBeeSpecies secondary = genome.getSecondaryAsBee();
		if (!this.isPureBred(EnumBeeChromosome.SPECIES)) {
			list.add("\u00A79" + primary.getName() + StringUtil.localize("bees.hyphen.adj.add") + "-" + secondary.getName()
					+ StringUtil.localize("bees.hybrid.adj.add") + " " + StringUtil.localize("gui.hybrid"));
		}
		list.add(rateSpeed(genome.getSpeed()) + " " + StringUtil.localize("gui.worker"));
		list.add(rateLifespan(genome.getLifespan()) + " " + StringUtil.localize("gui.life"));
		list.add("\u00A7aT: " + ClimateHelper.toDisplay(genome.getPrimaryAsBee().getTemperature()) + " / "
				+ StringUtil.capitalize(genome.getToleranceTemp().name()));
		list.add("\u00A7aH: " + ClimateHelper.toDisplay(genome.getPrimaryAsBee().getHumidity()) + " / "
				+ StringUtil.capitalize(genome.getToleranceHumid().name()));
		list.add(StringUtil.localize(genome.getFlowerProvider().getDescription()));
		if (genome.getNocturnal()) {
			list.add("\u00A7c" + StringUtil.localize("gui.nocturnal"));
		}
	}

	// / PRODUCTION
	@Override
	public ItemStack[] getProduceList() {
		ArrayList<ItemStack> products = new ArrayList<ItemStack>();

		IAlleleBeeSpecies primary = genome.getPrimaryAsBee();
		IAlleleBeeSpecies secondary = genome.getSecondaryAsBee();

		products.addAll(primary.getProducts().keySet());

		Set<ItemStack> secondaryProducts = secondary.getProducts().keySet();
		// Remove duplicates
		for (ItemStack second : secondaryProducts) {
			boolean skip = false;

			for (ItemStack compare : products)
				if (second.isItemEqual(compare)) {
					skip = true;
					break;
				}

			if (!skip) {
				products.add(second);
			}

		}

		return products.toArray(new ItemStack[0]);
	}

	@Override
	public ItemStack[] getSpecialtyList() {
		return genome.getPrimaryAsBee().getSpecialty().keySet().toArray(new ItemStack[0]);
	}

	@Override
	public ItemStack[] produceStacks(IBeeHousing housing) {
		if (!hasFlower(housing))
			return null;
		
		if(housing == null) {
			Proxies.log.warning("Failed to produce in an apiary because the beehousing was null.");
			return null;
		}
		IBeekeepingMode mode = BeeManager.breedingManager.getBeekeepingMode(housing.getWorld());
		if(mode == null) {
			Proxies.log.warning("Failed to produce in an apiary because the beekeeping mode was null.");
			return null;
		}

		ArrayList<ItemStack> products = new ArrayList<ItemStack>();

		IAlleleBeeSpecies primary = genome.getPrimaryAsBee();
		IAlleleBeeSpecies secondary = genome.getSecondaryAsBee();
		// Bee genetic speed * beehousing * beekeeping mode
		float speed = genome.getSpeed() * housing.getProductionModifier(genome)
				* mode.getProductionModifier(genome);

		// / Primary Products
		for (Map.Entry<ItemStack, Integer> entry : primary.getProducts().entrySet())
			if (housing.getWorld().rand.nextInt(100) < entry.getValue() * speed) {
				products.add(entry.getKey().copy());
			}
		// / Secondary Products
		for (Map.Entry<ItemStack, Integer> entry : secondary.getProducts().entrySet())
			if (housing.getWorld().rand.nextInt(100) < Math.round(entry.getValue() / 2) * speed) {
				products.add(entry.getKey().copy());
			}

		// We are done if the we are not jubilant.
		if (!primary.isJubilant(genome, housing)
				|| !secondary.isJubilant(genome, housing))
			return products.toArray(new ItemStack[0]);

		// / Specialty products
		for (Map.Entry<ItemStack, Integer> entry : primary.getSpecialty().entrySet())
			if (housing.getWorld().rand.nextInt(100) < entry.getValue() * speed) {
				products.add(entry.getKey().copy());
			}

		return genome.getFlowerProvider().affectProducts(housing.getWorld(), genome, housing.getXCoord(), housing.getYCoord(), housing.getZCoord(),
				products.toArray(new ItemStack[0]));
	}

	// / REPRODUCTION
	@Override
	public IBee spawnPrincess(IBeeHousing housing) {

		// We need a mated queen to produce offspring.
		if (mate == null)
			return null;

		// Fatigued queens do not produce princesses.
		if (BeeManager.breedingManager.getBeekeepingMode(housing.getWorld()).isFatigued(this))
			return null;

		return createOffspring(housing, getGeneration() + 1);
	}

	@Override
	public IBee[] spawnDrones(IBeeHousing housing) {

		World world = housing.getWorld();

		// We need a mated queen to produce offspring.
		if (mate == null)
			return null;

		ArrayList<IBee> bees = new ArrayList<IBee>();

		int toCreate = BeeManager.breedingManager.getBeekeepingMode(world).getFinalFertility(this, world, housing.getXCoord(), housing.getYCoord(),
				housing.getZCoord());

		if (toCreate <= 0) {
			toCreate = 1;
		}

		for (int i = 0; i < toCreate; i++) {
			IBee offspring = createOffspring(housing, -1);
			if (offspring != null) {
				bees.add(offspring);
			}
		}

		if (bees.size() > 0)
			return bees.toArray(new IBee[0]);
		else
			return null;
	}

	private IBee createOffspring(IBeeHousing housing, int generation) {

		World world = housing.getWorld();

		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated1 = mutateSpecies(housing, genome, mate);
		if (mutated1 != null) {
			parent1 = mutated1;
		}
		IChromosome[] mutated2 = mutateSpecies(housing, mate, genome);
		if (mutated2 != null) {
			parent2 = mutated2;
		}

		for (int i = 0; i < parent1.length; i++)
			if (parent1[i] != null && parent2[i] != null) {
				chromosomes[i] = Chromosome.inheritChromosome(world.rand, parent1[i], parent2[i]);
			}

		return new Bee(world, new BeeGenome(chromosomes), BeeManager.breedingManager.getBeekeepingMode(world).isNaturalOffspring(this),
				generation);
	}

	private IChromosome[] mutateSpecies(IBeeHousing housing, IGenome genomeOne, IGenome genomeTwo) {

		World world = housing.getWorld();

		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		IGenome genome0;
		IGenome genome1;
		IAllele allele0;
		IAllele allele1;

		if (world.rand.nextBoolean()) {
			allele0 = parent1[EnumBeeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent2[EnumBeeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = parent2[EnumBeeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent1[EnumBeeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		for (IBeeMutation mutation : BeeManager.breedingManager.getMutations(true)) {
			int chance = 0;

			if ((chance = mutation.getChance(housing, allele0, allele1, genome0, genome1)) > 0)
				if (world.rand.nextInt(100) < chance) {
					IApiaristTracker breedingTracker = BeeManager.breedingManager.getApiaristTracker(world, housing.getOwnerName());
					breedingTracker.registerMutation(mutation);
					return BeeManager.beeInterface.templateAsChromosomes(mutation.getTemplate());
				}
		}

		return null;
	}

	// / FLOWERS
	@Override
	public IIndividual retrievePollen(IBeeHousing housing) {
		
		int chance = (int) (genome.getFlowering()*housing.getFloweringModifier(getGenome()));
		
		// Correct speed
		if (housing.getWorld().rand.nextInt(100) >= chance)
			return null;

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(housing.getTerritoryModifier(genome)).multiply(3.0f);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		if (area.x < 1) {
			area.x = 1;
		}
		if (area.y < 1) {
			area.y = 1;
		}
		if (area.z < 1) {
			area.z = 1;
		}

		// We have up to ten attempts
		for (int i = 0; i < 20; i++) {

			Vect randomPos = new Vect(housing.getWorld().rand.nextInt(area.x), housing.getWorld().rand.nextInt(area.y), housing.getWorld().rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord()));
			posBlock = posBlock.add(offset);
			
			TileEntity tile = housing.getWorld().getBlockTileEntity(posBlock.x, posBlock.y, posBlock.z);
			if(!(tile instanceof IPollinatable)) {
				if(housing.getWorld().isAirBlock(posBlock.x, posBlock.y, posBlock.z))
					continue;
				
				// Test for ersatz genomes
				for(Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSpecimen.entrySet()) {
					if(entry.getKey().itemID != housing.getWorld().getBlockId(posBlock.x, posBlock.y, posBlock.z))
						continue;
					if(entry.getKey().getItemDamage() != housing.getWorld().getBlockMetadata(posBlock.x, posBlock.y, posBlock.z))
						continue;
					
					// We matched, return ersatz genome
					return entry.getValue().copy();
				}
				
				continue;
			}
			
			IPollinatable pitcher = (IPollinatable)tile;
			if(!genome.getFlowerProvider().isAcceptedPollinatable(housing.getWorld(), pitcher))
				continue;
			
			IIndividual pollen = pitcher.getPollen();

			if(pollen != null)
				return pollen;
		}
		
		return null;
	}

	@Override
	public boolean pollinateRandom(IBeeHousing housing, IIndividual pollen) {
		
		int chance = (int) (genome.getFlowering()*housing.getFloweringModifier(getGenome()));
		
		// Correct speed
		if (housing.getWorld().rand.nextInt(100) >= chance)
			return false;

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(housing.getTerritoryModifier(genome)).multiply(3.0f);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 4), -Math.round(area.z / 2));

		if (area.x < 1) {
			area.x = 1;
		}
		if (area.y < 1) {
			area.y = 1;
		}
		if (area.z < 1) {
			area.z = 1;
		}
		
		// We have up to ten attempts
		for (int i = 0; i < 30; i++) {

			Vect randomPos = new Vect(housing.getWorld().rand.nextInt(area.x), housing.getWorld().rand.nextInt(area.y), housing.getWorld().rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord()));
			posBlock = posBlock.add(offset);
			
			TileEntity tile = housing.getWorld().getBlockTileEntity(posBlock.x, posBlock.y, posBlock.z);
			
			IPollinatable receiver = null;
			if(tile instanceof IPollinatable) {
				
				 receiver = (IPollinatable)tile;
				
			} else {
				
				if(housing.getWorld().isAirBlock(posBlock.x, posBlock.y, posBlock.z))
					continue;
				
				// Test for ersatz genomes
				for(Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSpecimen.entrySet()) {
					if(entry.getKey().itemID != housing.getWorld().getBlockId(posBlock.x, posBlock.y, posBlock.z))
						continue;
					if(entry.getKey().getItemDamage() != housing.getWorld().getBlockMetadata(posBlock.x, posBlock.y, posBlock.z))
						continue;
					
					// We matched, replace the leaf block with ours and set the ersatz genome
					TreeManager.treeInterface.setLeaves(housing.getWorld(), entry.getValue(), posBlock.x, posBlock.y, posBlock.z);
					// Now let's pollinate
					receiver = (IPollinatable)housing.getWorld().getBlockTileEntity(posBlock.x, posBlock.y, posBlock.z);
				}

				if(receiver == null)
					continue;
			}
			
			if(!genome.getFlowerProvider().isAcceptedPollinatable(housing.getWorld(), receiver))
				continue;
			if(!receiver.canMateWith(pollen))
				continue;
			
			receiver.mateWith(pollen);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void plantFlowerRandom(IBeeHousing housing) {

		int chance = (int) (genome.getFlowering()*housing.getFloweringModifier(getGenome()));
		
		// Correct speed
		if (housing.getWorld().rand.nextInt(100) >= chance)
			return;

		// Gather required info
		IFlowerProvider provider = genome.getFlowerProvider();
		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(housing.getTerritoryModifier(genome)).multiply(3.0f);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 4), -Math.round(area.z / 2));

		if (area.x < 1) {
			area.x = 1;
		}
		if (area.y < 1) {
			area.y = 1;
		}
		if (area.z < 1) {
			area.z = 1;
		}

		// We have up to ten attempts
		for (int i = 0; i < 10; i++) {

			Vect randomPos = new Vect(housing.getWorld().rand.nextInt(area.x), housing.getWorld().rand.nextInt(area.y), housing.getWorld().rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord()));
			posBlock = posBlock.add(offset);

			if (provider.growFlower(housing.getWorld(), genome, posBlock.x, posBlock.y, posBlock.z)) {
				break;
			}
		}
	}

	public IIndividual copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Bee(nbttagcompound);

	}
	
	public static String rateSpeed(float speed) {
		if (speed >= 1.7f)
			return StringUtil.localize("gui.fastestspeed");
		else if (speed >= 1.4f)
			return StringUtil.localize("gui.fasterspeed");
		else if (speed >= 1.2f)
			return StringUtil.localize("gui.fastspeed");
		else if (speed >= 1.0f)
			return StringUtil.localize("gui.normalspeed");
		else if (speed >= 0.8f)
			return StringUtil.localize("gui.slowspeed");
		else if (speed >= 0.6f)
			return StringUtil.localize("gui.slowerspeed");
		else
			return StringUtil.localize("gui.slowestspeed");
	}

	public static String rateLifespan(int life) {
		if (life >= 70)
			return StringUtil.localize("gui.longestlife");
		else if (life >= 60)
			return StringUtil.localize("gui.longerlife");
		else if (life >= 50)
			return StringUtil.localize("gui.longlife");
		else if (life >= 45)
			return StringUtil.localize("gui.elongatedlife");
		else if (life >= 40)
			return StringUtil.localize("gui.normallife");
		else if (life >= 35)
			return StringUtil.localize("gui.shortenedlife");
		else if (life >= 30)
			return StringUtil.localize("gui.shortlife");
		else if (life >= 20)
			return StringUtil.localize("gui.shorterlife");
		else
			return StringUtil.localize("gui.shortestlife");
	}
	
	public static String rateFlowering(int flowering) {
		if (flowering >= 99)
			return StringUtil.localize("gui.maxspeed");
		else if (flowering >= 35)
			return StringUtil.localize("gui.fastestspeed");
		else if (flowering >= 30)
			return StringUtil.localize("gui.fasterspeed");
		else if (flowering >= 25)
			return StringUtil.localize("gui.fastspeed");
		else if (flowering >= 20)
			return StringUtil.localize("gui.normalspeed");
		else if (flowering >= 15)
			return StringUtil.localize("gui.slowspeed");
		else if (flowering >= 10)
			return StringUtil.localize("gui.slowerspeed");
		else
			return StringUtil.localize("gui.slowestspeed");
	}
}
