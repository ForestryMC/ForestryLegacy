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

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.ILegacyHandler;
import forestry.core.config.Defaults;
import forestry.core.utils.Vect;

public class Allele implements IAllele {

	String uid;
	boolean isDominant;

	public Allele(String uid, boolean isDominant) {
		this(uid, isDominant, false);
	}

	protected Allele(String uid, boolean isDominant, boolean skipRegister) {
		this.uid = uid;
		this.isDominant = isDominant;

		if (!skipRegister) {
			AlleleManager.alleleRegistry.registerAllele(this);
		}
	}

	@Override
	public String getUID() {
		return "forestry." + this.uid;
	}

	@Override
	public boolean isDominant() {
		return isDominant;
	}

	// / BEES // SPECIES
	// Common Branch
	public static AlleleSpecies speciesForest;
	public static AlleleSpecies speciesMeadows;
	public static AlleleSpecies speciesCommon;
	public static AlleleSpecies speciesCultivated;

	// Noble Branch
	public static AlleleSpecies speciesNoble;
	public static AlleleSpecies speciesMajestic;
	public static AlleleSpecies speciesImperial;

	// Industrious Branch
	public static AlleleSpecies speciesDiligent;
	public static AlleleSpecies speciesUnweary;
	public static AlleleSpecies speciesIndustrious;

	// Heroic Branch
	public static AlleleSpecies speciesSteadfast;
	public static AlleleSpecies speciesValiant;
	public static AlleleSpecies speciesHeroic;

	// Infernal Branch
	public static AlleleSpecies speciesSinister;
	public static AlleleSpecies speciesFiendish;
	public static AlleleSpecies speciesDemonic;

	// Austere Branch
	public static AlleleSpecies speciesModest;
	public static AlleleSpecies speciesFrugal;
	public static AlleleSpecies speciesAustere;

	// / Tropical Branch
	public static AlleleSpecies speciesTropical;
	public static AlleleSpecies speciesExotic;
	public static AlleleSpecies speciesEdenic;

	// End Branch
	public static AlleleSpecies speciesEnded;
	public static AlleleSpecies speciesSpectral;
	public static AlleleSpecies speciesPhantasmal;

	// Frozen Branch
	public static AlleleSpecies speciesWintry;
	public static AlleleSpecies speciesIcy;
	public static AlleleSpecies speciesGlacial;

	// Vengeful Branch
	public static AlleleSpecies speciesVindictive;
	public static AlleleSpecies speciesVengeful;
	public static AlleleSpecies speciesAvenging;

	// Reddened Branch (EE)
	public static AlleleSpecies speciesDarkened;
	public static AlleleSpecies speciesReddened;
	public static AlleleSpecies speciesOmega;

	// Festive branch
	public static AlleleSpecies speciesLeporine; // Easter
	public static AlleleSpecies speciesMerry; // Christmas
	public static AlleleSpecies speciesTipsy; // New Year
	// 35 Solstice
	// 36 Halloween
	// 37 Thanksgiving
	// 39 New Year

	// Agrarian branch
	public static AlleleSpecies speciesRural;
	// 41 Farmerly
	// 42 Agrarian

	// Boggy branch
	public static AlleleSpecies speciesMarshy;
	// 44 speciesMiry
	// 45 speciesBoggy

	// Monastic branch
	public static AlleleSpecies speciesMonastic;
	public static AlleleSpecies speciesSecluded;
	public static AlleleSpecies speciesHermitic;
	
	// / TREES // SPECIES 512 - 1023
	public static AlleleSpecies treeOak;
	public static AlleleSpecies treeBirch;
	public static AlleleSpecies treeSpruce;
	public static AlleleSpecies treePine;
	public static AlleleSpecies treeJungle;

	public static AlleleSpecies treeLarch;
	public static AlleleSpecies treeTeak;
	public static AlleleSpecies treeAcacia;
	public static AlleleSpecies treeLime;
	public static AlleleSpecies treeChestnut;
	public static AlleleSpecies treeWenge;
	public static AlleleSpecies treeBaobab;
	public static AlleleSpecies treeSequioa;

	public static AlleleSpecies treeKapok;
	public static AlleleSpecies treeEbony;
	public static AlleleSpecies treeMahogany;
	public static AlleleSpecies treeBalsa;
	public static AlleleSpecies treePalm;
	public static AlleleSpecies treeWalnut;
	public static AlleleSpecies treeBoojum;
	public static AlleleSpecies treeCherry;
	
	public static AlleleSpecies treeWillow;
	public static AlleleSpecies treeSipiri;

	// / ALL // GENERIC
	public static Allele boolFalse;
	public static Allele boolTrue;

	// / BEES // SPEED 1100 - 1199
	public static Allele speedSlowest;
	public static Allele speedSlower;
	public static Allele speedSlow;
	public static Allele speedNorm;
	public static Allele speedFast;
	public static Allele speedFaster;
	public static Allele speedFastest;

	// / BEES // LIFESPAN 1200 - 1299
	public static Allele lifespanShortest;
	public static Allele lifespanShorter;
	public static Allele lifespanShort;
	public static Allele lifespanShortened;
	public static Allele lifespanNormal;
	public static Allele lifespanElongated;
	public static Allele lifespanLong;
	public static Allele lifespanLonger;
	public static Allele lifespanLongest;

	// / BEES // FERTILITY 1300 - 1349
	public static Allele fertilityLow;
	public static Allele fertilityNormal;
	public static Allele fertilityHigh;
	public static Allele fertilityMaximum;

	// / TREES // GROWTH PROVIDER 1350 - 1399
	public static Allele growthLightlevel;
	public static Allele growthAcacia;
	public static Allele growthTropical;

	// TREES FRUIT PROVIDERS
	public static Allele fruitNone;
	public static Allele fruitApple;
	public static Allele fruitCocoa;
	public static Allele fruitChestnut;
	public static Allele fruitPalm;
	public static Allele fruitWalnut;
	public static Allele fruitCherry;

	// / TREES // HEIGHT 1400 - 1449
	public static Allele heightSmallest;
	public static Allele heightSmaller;
	public static Allele heightSmall;
	public static Allele heightAverage;
	public static Allele heightLarge;
	public static Allele heightLarger;
	public static Allele heightLargest;
	public static Allele heightGigantic;

	// / BOTH // TOLERANCE 1450 - 1499
	public static Allele toleranceNone;
	public static Allele toleranceBoth1;
	public static Allele toleranceBoth2;
	public static Allele toleranceBoth3;
	public static Allele toleranceBoth4;
	public static Allele toleranceBoth5;
	public static Allele toleranceUp1;
	public static Allele toleranceUp2;
	public static Allele toleranceUp3;
	public static Allele toleranceUp4;
	public static Allele toleranceUp5;
	public static Allele toleranceDown1;
	public static Allele toleranceDown2;
	public static Allele toleranceDown3;
	public static Allele toleranceDown4;
	public static Allele toleranceDown5;

	// / BEES // FLOWER PROVIDERS 1500 - 1599
	public static Allele flowersVanilla;
	public static Allele flowersNether;
	public static Allele flowersCacti;
	public static Allele flowersMushrooms;
	public static Allele flowersEnd;
	public static Allele flowersJungle;
	public static Allele flowersSnow;
	public static Allele flowersWheat;

	// / TREES // FERTILITY 1600 - 1649
	public static Allele saplingsLower;
	public static Allele saplingsLow;
	public static Allele saplingsDefault;
	public static Allele saplingsDouble;
	public static Allele saplingsTriple;

	// / TREES // YIELD 1650 - 1699
	public static Allele yieldLowest;
	public static Allele yieldLower;
	public static Allele yieldLow;
	public static Allele yieldAverage;

	// TREES // SAPPINESS
	public static Allele sappinessLowest;
	public static Allele sappinessLower;
	public static Allele sappinessLow;
	public static Allele sappinessAverage;
	public static Allele sappinessHigh;
	public static Allele sappinessHigher;
	public static Allele sappinessHighest;
	
	// TREES // MATURATION TIME
	public static Allele maturationSlowest;
	public static Allele maturationSlower;
	public static Allele maturationSlow;
	public static Allele maturationAverage;
	public static Allele maturationFast;
	public static Allele maturationFaster;
	public static Allele maturationFastest;
	
	// / BEES // FLOWER GROWTH 1700 - 1749
	public static Allele floweringSlowest;
	public static Allele floweringSlower;
	public static Allele floweringSlow;
	public static Allele floweringAverage;
	public static Allele floweringFast;
	public static Allele floweringFaster;
	public static Allele floweringFastest;
	public static Allele floweringMaximum;

	// / BOTH // TERRITORY 1750 - 1799
	public static Allele territoryDefault;
	public static Allele territoryLarge;
	public static Allele territoryLarger;
	public static Allele territoryLargest;

	// / BEES // EFFECTS 1800 - 1899
	public static Allele effectNone;
	public static Allele effectAggressive;
	public static Allele effectHeroic;
	public static Allele effectBeatific;
	public static Allele effectMiasmic;
	public static Allele effectMisanthrope;
	public static Allele effectGlacial;
	public static Allele effectRadioactive;
	public static Allele effectCreeper;
	public static Allele effectIgnition;
	public static Allele effectExploration;
	public static Allele effectFestiveEaster;
	public static Allele effectSnowing;
	public static Allele effectDrunkard;
	public static Allele effectResurrection;

	// / TREES // EFFECTS 1900 - 1999
	public static Allele leavesNone;

	// These are "secondary" plant attributes, i.e. the tree can double as one.
	public static Allele plantTypeNone;
	public static Allele plantTypePlains;
	public static Allele plantTypeDesert;
	public static Allele plantTypeBeach;
	public static Allele plantTypeCave;
	public static Allele plantTypeWater;
	public static Allele plantTypeNether;
	public static Allele plantTypeCrop;
	
	public static void initialize() {
		// ALL // GENERIC
		boolFalse = new AlleleBoolean("boolFalse", false);
		boolTrue = new AlleleBoolean("boolTrue", true);

		// BEES // SPEED
		speedSlowest = new AlleleFloat("speedSlowest", 0.3f, true);
		speedSlower = new AlleleFloat("speedSlower", 0.6f, true);
		speedSlow = new AlleleFloat("speedSlow", 0.8f, true);
		speedNorm = new AlleleFloat("speedNorm", 1.0f);
		speedFast = new AlleleFloat("speedFast", 1.2f, true);
		speedFaster = new AlleleFloat("speedFaster", 1.4f);
		speedFastest = new AlleleFloat("speedFastest", 1.7f);

		// BEES // LIFESPAN
		lifespanShortest = new AlleleInteger("lifespanShortest", 10, false);
		lifespanShorter = new AlleleInteger("lifespanShorter", 20, true);
		lifespanShort = new AlleleInteger("lifespanShort", 30, true);
		lifespanShortened = new AlleleInteger("lifespanShortened", 35, true);
		lifespanNormal = new AlleleInteger("lifespanNormal", 40);
		lifespanElongated = new AlleleInteger("lifespanElongated", 45, true);
		lifespanLong = new AlleleInteger("lifespanLong", 50);
		lifespanLonger = new AlleleInteger("lifespanLonger", 60);
		lifespanLongest = new AlleleInteger("lifespanLongest", 70);

		// BEES // FERTILITY
		fertilityLow = new AlleleInteger("fertilityLow", 1, true);
		fertilityNormal = new AlleleInteger("fertilityNormal", 2, true);
		fertilityHigh = new AlleleInteger("fertilityHigh", 3);
		fertilityMaximum = new AlleleInteger("fertilityMaximum", 4);

		// TREES // HEIGHT
		heightSmallest = new AlleleFloat("heightSmallest", 0.25f);
		heightSmaller = new AlleleFloat("heightSmaller", 0.5f);
		heightSmall = new AlleleFloat("heightSmall", 0.75f);
		heightAverage = new AlleleFloat("heightMax10", 1.0f);
		heightLarge = new AlleleFloat("heightLarge", 1.25f);
		heightLarger = new AlleleFloat("heightLarger", 1.5f);
		heightLargest = new AlleleFloat("heightLargest", 1.75f);
		heightGigantic = new AlleleFloat("heightGigantic", 2.0f);

		// BEES // TOLERANCE
		toleranceNone = new AlleleTolerance("toleranceNone", EnumTolerance.NONE);
		toleranceBoth1 = new AlleleTolerance("toleranceBoth1", EnumTolerance.BOTH_1, true);
		toleranceBoth2 = new AlleleTolerance("toleranceBoth2", EnumTolerance.BOTH_2);
		toleranceBoth3 = new AlleleTolerance("toleranceBoth3", EnumTolerance.BOTH_3);
		toleranceBoth4 = new AlleleTolerance("toleranceBoth4", EnumTolerance.BOTH_4);
		toleranceBoth5 = new AlleleTolerance("toleranceBoth5", EnumTolerance.BOTH_5);
		toleranceUp1 = new AlleleTolerance("toleranceUp1", EnumTolerance.UP_1, true);
		toleranceUp2 = new AlleleTolerance("toleranceUp2", EnumTolerance.UP_2);
		toleranceUp3 = new AlleleTolerance("toleranceUp3", EnumTolerance.UP_3);
		toleranceUp4 = new AlleleTolerance("toleranceUp4", EnumTolerance.UP_4);
		toleranceUp5 = new AlleleTolerance("toleranceUp5", EnumTolerance.UP_5);
		toleranceDown1 = new AlleleTolerance("toleranceDown1", EnumTolerance.DOWN_1, true);
		toleranceDown2 = new AlleleTolerance("toleranceDown2", EnumTolerance.DOWN_2);
		toleranceDown3 = new AlleleTolerance("toleranceDown3", EnumTolerance.DOWN_3);
		toleranceDown4 = new AlleleTolerance("toleranceDown4", EnumTolerance.DOWN_4);
		toleranceDown5 = new AlleleTolerance("toleranceDown5", EnumTolerance.DOWN_5);

		// TREES // FERTILITY
		saplingsLower = new AlleleFloat("saplingsLower", 0.01f, true);
		saplingsLow = new AlleleFloat("saplingsLow", 0.025f, true);
		saplingsDefault = new AlleleFloat("saplingsDefault", 0.05f, true);
		saplingsDouble = new AlleleFloat("saplingsDouble", 0.15f, true);
		saplingsTriple = new AlleleFloat("saplingsTriple", 0.3f, true);

		// TREES // YIELD
		yieldLowest = new AlleleFloat("yieldLowest", 0.025f, true);
		yieldLower = new AlleleFloat("yieldDefault", 0.05f, true);
		yieldLow = new AlleleFloat("yieldDefault", 0.1f, true);
		yieldAverage = new AlleleFloat("yieldDefault", 0.2f, true);

		// TREES // SAPPINESS
		sappinessLowest = new AlleleFloat("sappinessLowest", 0.1f, true);
		sappinessLower = new AlleleFloat("sappinessLower", 0.2f, true);
		sappinessLow = new AlleleFloat("sappinessLow", 0.4f, true);
		sappinessAverage = new AlleleFloat("sappinessAverage", 0.5f, true);
		sappinessHigh = new AlleleFloat("sappinessHigh", 0.6f, true);
		sappinessHigher = new AlleleFloat("sappinessHigher", 0.8f, false);
		sappinessHighest = new AlleleFloat("sappinessHighest", 1.0f, false);
		
		// TREES // MATURATION TIME
		maturationSlowest = new AlleleInteger("maturationSlowest", 7, true);
		maturationSlower = new AlleleInteger("maturationSlower", 6);
		maturationSlow = new AlleleInteger("maturationSlow", 5, true);
		maturationAverage = new AlleleInteger("maturationAverage", 4);
		maturationFast = new AlleleInteger("maturationFast", 3);
		maturationFaster = new AlleleInteger("maturationFaster", 2);
		maturationFastest = new AlleleInteger("maturationFastest", 1);

		// BEES // FLOWER GROWTH
		floweringSlowest = new AlleleInteger("floweringSlowest", 5, true);
		floweringSlower = new AlleleInteger("floweringSlower", 10);
		floweringSlow = new AlleleInteger("floweringSlow", 15);
		floweringAverage = new AlleleInteger("floweringAverage", 20);
		floweringFast = new AlleleInteger("floweringFast", 25);
		floweringFaster = new AlleleInteger("floweringFaster", 30);
		floweringFastest = new AlleleInteger("floweringFastest", 35);
		floweringMaximum = new AlleleInteger("floweringMaximum", 99, true);

		// BOTH // TERRITORY
		territoryDefault = new AlleleArea("territoryDefault", new Vect(9, 6, 9));
		territoryLarge = new AlleleArea("territoryLarge", new Vect(11, 8, 11));
		territoryLarger = new AlleleArea("territoryLarger", new Vect(13, 12, 13));
		territoryLargest = new AlleleArea("territoryLargest", new Vect(15, 13, 15));

		// TREES // PLANTS
		plantTypeNone = new AllelePlantType("plantTypeNone", EnumSet.noneOf(EnumPlantType.class), true);
		plantTypePlains = new AllelePlantType("plantTypePlains", EnumPlantType.Plains);;
		plantTypeDesert = new AllelePlantType("plantTypeDesert", EnumPlantType.Desert);
		plantTypeBeach = new AllelePlantType("plantTypeBeach", EnumPlantType.Beach);
		plantTypeCave = new AllelePlantType("plantTypeCave", EnumPlantType.Cave);
		plantTypeWater = new AllelePlantType("plantTypeWater", EnumPlantType.Water);
		plantTypeNether = new AllelePlantType("plantTypeNether", EnumPlantType.Nether);
		plantTypeCrop = new AllelePlantType("plantTypeCrop", EnumPlantType.Crop);

		// LEGACY MAPPINGS
		ILegacyHandler legacy = (ILegacyHandler) AlleleManager.alleleRegistry;

		legacy.registerLegacyMapping(0, "forestry.speciesForest");
		legacy.registerLegacyMapping(1, "forestry.speciesMeadows");
		legacy.registerLegacyMapping(2, "forestry.speciesCommon");
		legacy.registerLegacyMapping(3, "forestry.speciesCultivated");

		legacy.registerLegacyMapping(4, "forestry.speciesNoble");
		legacy.registerLegacyMapping(5, "forestry.speciesMajestic");
		legacy.registerLegacyMapping(6, "forestry.speciesImperial");

		legacy.registerLegacyMapping(7, "forestry.speciesDiligent");
		legacy.registerLegacyMapping(8, "forestry.speciesUnweary");
		legacy.registerLegacyMapping(9, "forestry.speciesIndustrious");

		legacy.registerLegacyMapping(10, "forestry.speciesSteadfast");
		legacy.registerLegacyMapping(11, "forestry.speciesValiant");
		legacy.registerLegacyMapping(12, "forestry.speciesHeroic");

		legacy.registerLegacyMapping(13, "forestry.speciesSinister");
		legacy.registerLegacyMapping(14, "forestry.speciesFiendish");
		legacy.registerLegacyMapping(15, "forestry.speciesDemonic");

		legacy.registerLegacyMapping(16, "forestry.speciesModest");
		legacy.registerLegacyMapping(17, "forestry.speciesFrugal");
		legacy.registerLegacyMapping(18, "forestry.speciesAustere");

		legacy.registerLegacyMapping(19, "forestry.speciesTropical");
		legacy.registerLegacyMapping(20, "forestry.speciesExotic");
		legacy.registerLegacyMapping(21, "forestry.speciesEdenic");

		legacy.registerLegacyMapping(22, "forestry.speciesEnded");

		legacy.registerLegacyMapping(25, "forestry.speciesWintry");

		legacy.registerLegacyMapping(28, "forestry.speciesVindictive");
		legacy.registerLegacyMapping(29, "forestry.speciesVengeful");
		legacy.registerLegacyMapping(30, "forestry.speciesAvenging");

		legacy.registerLegacyMapping(Defaults.ID_BEE_SPECIES_DARKENED, "forestry.speciesDarkened");
		legacy.registerLegacyMapping(Defaults.ID_BEE_SPECIES_REDDENED, "forestry.speciesReddened");
		legacy.registerLegacyMapping(Defaults.ID_BEE_SPECIES_OMEGA, "forestry.speciesOmega");

		legacy.registerLegacyMapping(34, "forestry.speciesLeporine");

		legacy.registerLegacyMapping(40, "forestry.speciesRural");

		legacy.registerLegacyMapping(43, "forestry.speciesMarshy");

		// Flowers
		legacy.registerLegacyMapping(1500, "forestry.flowersVanilla");
		legacy.registerLegacyMapping(1501, "forestry.flowersNether");
		legacy.registerLegacyMapping(1502, "forestry.flowersCacti");
		legacy.registerLegacyMapping(1503, "forestry.flowersMushrooms");
		legacy.registerLegacyMapping(1504, "forestry.flowersEnd");
		legacy.registerLegacyMapping(1505, "forestry.flowersJungle");
		legacy.registerLegacyMapping(1506, "forestry.flowersSnow");
		legacy.registerLegacyMapping(1507, "forestry.flowersWheat");

		// Effects
		legacy.registerLegacyMapping(1800, "forestry.effectNone");
		legacy.registerLegacyMapping(1801, "forestry.effectAggressive");
		legacy.registerLegacyMapping(1802, "forestry.effectHeroic");
		legacy.registerLegacyMapping(1803, "forestry.effectBeatific");
		legacy.registerLegacyMapping(1804, "forestry.effectMiasmic");
		legacy.registerLegacyMapping(1805, "forestry.effectMisanthrope");
		legacy.registerLegacyMapping(1806, "forestry.effectGlacial");
		legacy.registerLegacyMapping(1807, "forestry.effectRadioactive");
		legacy.registerLegacyMapping(1808, "forestry.effectCreeper");
		legacy.registerLegacyMapping(1809, "forestry.effectIgnition");
		legacy.registerLegacyMapping(1810, "forestry.effectExploration");
		legacy.registerLegacyMapping(1811, "forestry.effectFestiveEaster");

		// Generic
		legacy.registerLegacyMapping(1024, "forestry.boolFalse");
		legacy.registerLegacyMapping(1025, "forestry.boolTrue");

		// Speed
		legacy.registerLegacyMapping(1100, "forestry.speedSlowest");
		legacy.registerLegacyMapping(1101, "forestry.speedSlower");
		legacy.registerLegacyMapping(1102, "forestry.speedSlow");
		legacy.registerLegacyMapping(1103, "forestry.speedNorm");
		legacy.registerLegacyMapping(1104, "forestry.speedFast");
		legacy.registerLegacyMapping(1105, "forestry.speedFaster");
		legacy.registerLegacyMapping(1106, "forestry.speedFastest");

		// Lifespan
		legacy.registerLegacyMapping(1200, "forestry.lifespanShortest");
		legacy.registerLegacyMapping(1201, "forestry.lifespanShorter");
		legacy.registerLegacyMapping(1202, "forestry.lifespanShort");
		legacy.registerLegacyMapping(1203, "forestry.lifespanShortened");
		legacy.registerLegacyMapping(1204, "forestry.lifespanNormal");
		legacy.registerLegacyMapping(1205, "forestry.lifespanElongated");
		legacy.registerLegacyMapping(1206, "forestry.lifespanLong");
		legacy.registerLegacyMapping(1207, "forestry.lifespanLonger");
		legacy.registerLegacyMapping(1208, "forestry.lifespanLongest");

		// Fertility
		legacy.registerLegacyMapping(1300, "forestry.fertilityLow");
		legacy.registerLegacyMapping(1301, "forestry.fertilityNormal");
		legacy.registerLegacyMapping(1302, "forestry.fertilityHigh");
		legacy.registerLegacyMapping(1303, "forestry.fertilityMaximum");

		// Tolerance
		legacy.registerLegacyMapping(1450, "forestry.toleranceNone");
		legacy.registerLegacyMapping(1451, "forestry.toleranceBoth1");
		legacy.registerLegacyMapping(1452, "forestry.toleranceBoth2");
		legacy.registerLegacyMapping(1453, "forestry.toleranceBoth3");
		legacy.registerLegacyMapping(1454, "forestry.toleranceBoth4");
		legacy.registerLegacyMapping(1455, "forestry.toleranceBoth5");
		legacy.registerLegacyMapping(1456, "forestry.toleranceUp1");
		legacy.registerLegacyMapping(1457, "forestry.toleranceUp2");
		legacy.registerLegacyMapping(1458, "forestry.toleranceUp3");
		legacy.registerLegacyMapping(1459, "forestry.toleranceUp4");
		legacy.registerLegacyMapping(1460, "forestry.toleranceUp5");
		legacy.registerLegacyMapping(1461, "forestry.toleranceDown1");
		legacy.registerLegacyMapping(1462, "forestry.toleranceDown2");
		legacy.registerLegacyMapping(1463, "forestry.toleranceDown3");
		legacy.registerLegacyMapping(1464, "forestry.toleranceDown4");
		legacy.registerLegacyMapping(1465, "forestry.toleranceDown5");

		// Flower growth
		legacy.registerLegacyMapping(1700, "forestry.floweringSlowest");
		legacy.registerLegacyMapping(1701, "forestry.floweringSlower");
		legacy.registerLegacyMapping(1702, "forestry.floweringSlow");
		legacy.registerLegacyMapping(1710, "forestry.floweringMaximum");

		// Territory
		legacy.registerLegacyMapping(1750, "forestry.territoryDefault");
		legacy.registerLegacyMapping(1751, "forestry.territoryLarge");
		legacy.registerLegacyMapping(1752, "forestry.territoryLarger");
		legacy.registerLegacyMapping(1753, "forestry.territoryLargest");

	}
}
