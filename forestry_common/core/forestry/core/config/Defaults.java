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
package forestry.core.config;

public class Defaults {
	// System
	public static final String MOD = "Forestry";
	public static final String URL = "http://forestry.sengir.net/";

	public static final int WORLD_HEIGHT = 256;
	public static final boolean DEBUG = false;

	public static final int NET_MAX_UPDATE_DISTANCE = 50;

	public static final String DEFAULT_POWER_FRAMEWORK = "forestry.energy.BioPowerFramework";
	public static final int BUCKET_VOLUME = 1000;

	// Textures
	public static final String TEXTURE_PATH_GUI = "/gfx/forestry/gui";
	public static final String TEXTURE_PATH_BLOCKS = "/gfx/forestry/blocks";
	public static final String TEXTURE_PATH_ITEMS = "/gfx/forestry/items";
	public static final String TEXTURE_PATH_ENTITIES = "/gfx/forestry/entities";

	public static final String TEXTURE_BLOCKS = TEXTURE_PATH_BLOCKS + "/blocks.png";
	public static final String TEXTURE_ARBORICULTURE = TEXTURE_PATH_BLOCKS + "/arboriculture.png";
	public static final String TEXTURE_FARM = TEXTURE_PATH_BLOCKS + "/farm.png";

	public static final String TEXTURE_ITEMS = TEXTURE_PATH_ITEMS + "/items.png";
	public static final String TEXTURE_CRATED = TEXTURE_PATH_ITEMS + "/crated.png";
	public static final String TEXTURE_BEES = TEXTURE_PATH_ITEMS + "/bees.png";
	public static final String TEXTURE_GERMLINGS = TEXTURE_PATH_ITEMS + "/germlings.png";
	public static final String TEXTURE_LIQUIDS = TEXTURE_PATH_ITEMS + "/liquids.png";
	public static final String TEXTURE_FLUIDS = TEXTURE_PATH_ITEMS + "/fluids.png";
	public static final String TEXTURE_APIARIST_ARMOR_PRIMARY = TEXTURE_PATH_ITEMS + "/apiarist_armor_1.png";
	public static final String TEXTURE_APIARIST_ARMOR_SECONDARY = TEXTURE_PATH_ITEMS + "/apiarist_armor_2.png";
	public static final String TEXTURE_SKIN_BEEKPEEPER = TEXTURE_PATH_ENTITIES + "/beekeeper.png";

	public static final String TEXTURE_PARTICLES_BEE = "/gfx/forestry/particles/swarm_bee.png";
	public static final String TEXTURE_PARTICLES_EMBER = "/gfx/forestry/particles/ember.png";
	public static final String TEXTURE_PARTICLES_POISON = "/gfx/forestry/particles/poison.png";

	public static final String TEXTURE_ICONS_MINECRAFT = "/gui/items.png";
	public static final String TEXTURE_BLOCKS_MINECRAFT = "/terrain.png";
	public static final String TEXTURE_ICONS_MISC = TEXTURE_PATH_GUI + "/misc.png";
	public static final String TEXTURE_HABITATS = TEXTURE_PATH_GUI + "/habitats.png";
	public static final String TEXTURE_ERRORS = TEXTURE_PATH_GUI + "/errors.png";
	public static final String TEXTURE_TRIGGERS = TEXTURE_PATH_GUI + "/triggers.png";

	// Villagers
	public static final int ID_VILLAGER_BEEKEEPER = 80;

	// Block Ids
	public static final int ID_BLOCK_MUSHROOM = 1375;
	public static final int ID_BLOCK_SAPLING = 1376;
	public static final int ID_BLOCK_RUBSAPLING = 1377;
	public static final int ID_BLOCK_CANDLE = 1378;
	public static final int ID_BLOCK_STUMP = 1379;
	public static final int ID_BLOCK_PLANKS = 1380;
	public static final int ID_BLOCK_GLASS = 1381;
	public static final int ID_BLOCK_ALVEARY = 1382;
	public static final int ID_BLOCK_ALVEARY_SWARMER = 1383;
	public static final int ID_BLOCK_ALVEARY_HEATER = 1384;
	public static final int ID_BLOCK_ALVEARY_FAN = 1385;

	public static final int ID_BLOCK_SLABS1 = 1386;
	public static final int ID_BLOCK_SLABS2 = 1387;

	public static final int ID_BLOCK_LOG1 = 1388;
	public static final int ID_BLOCK_LOG2 = 1389;
	public static final int ID_BLOCK_LOG3 = 1390;
	public static final int ID_BLOCK_LOG4 = 1391;

	public static final int ID_BLOCK_SAPLING_GE = 1392;
	public static final int ID_BLOCK_LEAVES = 1393;

	public static final int ID_BLOCK_FENCES = 1394;

	public static final int ID_BLOCK_FARM = 1395;
	
	public static final int ID_BLOCK_STAIRS = 1396;

	public static final int ID_BLOCK_SOIL = 1397;
	public static final int ID_BLOCK_RESOURCES = 1398;
	public static final int ID_BLOCK_BEEHIVES = 1399;
	public static final int ID_BLOCK_BUILDING = 1400;
	public static final int ID_BLOCK_PLANTER = 1401;
	public static final int ID_BLOCK_FIRSAPLING = 1402;
	public static final int ID_BLOCK_HARVESTER = 1403;
	public static final int ID_BLOCK_ENGINE = 1404;
	public static final int ID_BLOCK_MACHINE = 1405;
	public static final int ID_BLOCK_MILL = 1406;

	public static final int ID_BLOCK_MAIL = 1407;
	public static final int ID_BLOCK_APICULTURE = 1408;
	public static final int ID_BLOCK_CORE = 1409;
	
	// Definition IDs
	public static final int DEFINITION_ANALYZER_META = 0;
	
	public static final int DEFINITION_APIARY_META = 0;
	public static final int DEFINITION_APIARISTCHEST_META = 1;
	
	public static final int DEFINITION_ENGINETIN_META = 0;
	public static final int DEFINITION_ENGINECOPPER_META = 1;
	public static final int DEFINITION_ENGINEBRONZE_META = 2;
	public static final int DEFINITION_GENERATOR_META = 3;
	
	public static final int DEFINITION_MAILBOX_META = 0;
	public static final int DEFINITION_TRADESTATION_META = 1;
	public static final int DEFINITION_PHILATELIST_META = 2;
	
	// Package Ids
	public static final int ID_PACKAGE_MACHINE_FERMENTER = 0;
	public static final int ID_PACKAGE_MACHINE_STILL = 1;
	public static final int ID_PACKAGE_MACHINE_BOTTLER = 2;
	public static final int ID_PACKAGE_MACHINE_RAINTANK = 3;
	public static final int ID_PACKAGE_MACHINE_CARPENTER = 5;
	public static final int ID_PACKAGE_MACHINE_MOISTENER = 6;
	public static final int ID_PACKAGE_MACHINE_APIARY = 7;
	public static final int ID_PACKAGE_MACHINE_CENTRIFUGE = 8;
	public static final int ID_PACKAGE_MACHINE_SQUEEZER = 9;
	public static final int ID_PACKAGE_MACHINE_ALVEARY = 10;
	public static final int ID_PACKAGE_MACHINE_FABRICATOR = 11;

	public static final int ID_PACKAGE_MILL_RAINMAKER = 1;
	public static final int ID_PACKAGE_MILL_APIARIST_CHEST = 3;
	public static final int ID_PACKAGE_MILL_ANALYZER = 4;
	public static final int ID_PACKAGE_MILL_MAILBOX = 5;
	public static final int ID_PACKAGE_MILL_TRADER = 6;
	public static final int ID_PACKAGE_MILL_PHILATELIST = 7;

	// Item Ids
	public static final int ID_ITEM_FERTILIZER_BIO = 5000;
	public static final int ID_ITEM_FERTILIZER_COMPOUND = 5001;
	public static final int ID_ITEM_APATITE = 5002;
	public static final int ID_ITEM_COPPER = 5003;
	public static final int ID_ITEM_TIN = 5004;
	public static final int ID_ITEM_BRONZE = 5005;
	public static final int ID_ITEM_WRENCH = 5007;
	public static final int ID_ITEM_BRONZE_GEAR = 5008;
	public static final int ID_ITEM_BUCKET_BIOMASS = 5009;
	public static final int ID_ITEM_STURDY_CASING = 5010;
	public static final int ID_ITEM_VIAL_EMPTY = 5011;
	public static final int ID_ITEM_VIAL_CATALYST = 5012;
	public static final int ID_ITEM_BIOFUEL = 5013;
	public static final int ID_ITEM_BIOMASS = 5014;
	public static final int ID_ITEM_BUCKET_BIOFUEL = 5015;
	public static final int ID_ITEM_MILK = 5016;
	public static final int ID_ITEM_PEAT = 5017;
	public static final int ID_ITEM_ASH = 5018;
	public static final int ID_ITEM_COPPER_GEAR = 5019;
	public static final int ID_ITEM_WATERCAN = 5020;
	public static final int ID_ITEM_CAN_EMPTY = 5021;
	public static final int ID_ITEM_BIOMASSCAN = 5022;
	public static final int ID_ITEM_BIOFUELCAN = 5023;
	public static final int ID_ITEM_TIN_GEAR = 5024;
	public static final int ID_ITEM_HARDENED_MACHINE = 5025;
	public static final int ID_ITEM_IODINE_CAPSULE = 5026;

	public static final int ID_ITEM_CARTON = 13000;
	public static final int ID_ITEM_CRATE = 13001;
	public static final int ID_ITEM_OAKSTICK = 13002;
	public static final int ID_ITEM_WOODPULP = 13003;
	public static final int ID_ITEM_TOOLKIT_PICKAXE = 13004;
	public static final int ID_ITEM_PICKAXE_BRONZE = 13005;
	public static final int ID_ITEM_PICKAXE_BRONZE_BROKEN = 13006;
	public static final int ID_ITEM_TOOLKIT_SHOVEL = 13007;
	public static final int ID_ITEM_SHOVEL_BRONZE = 13008;
	public static final int ID_ITEM_SHOVEL_BRONZE_BROKEN = 13009;

	public static final int ID_ITEM_WHEAT_MOULDY = 13020;
	public static final int ID_ITEM_WHEAT_DECAYING = 13021;
	public static final int ID_ITEM_MULCH = 13022;
	public static final int ID_ITEM_BEE_QUEEN = 13023;
	public static final int ID_ITEM_BEE_DRONE = 13024;
	public static final int ID_ITEM_BEE_PRINCESS = 13025;
	// public static final int ID_ITEM_HONEY_COMB = 13026;
	public static final int ID_ITEM_HONEY_DROP = 13027;
	public static final int ID_ITEM_SCOOP = 13028;
	public static final int ID_ITEM_BEESWAX = 13029;
	public static final int ID_ITEM_POLLEN = 13030;
	public static final int ID_ITEM_PROPOLIS = 13031;
	public static final int ID_ITEM_ROYAL_JELLY = 13032;
	public static final int ID_ITEM_HONEYDEW = 13033;
	public static final int ID_ITEM_WAX_CAPSULE = 13034;
	public static final int ID_ITEM_HONEYED_SLICE = 13035;
	public static final int ID_ITEM_SHORT_MEAD = 13036;
	public static final int ID_ITEM_AMBROSIA = 13037;

	public static final int ID_ITEM_WAX_CAPSULE_WATER = 13038;
	public static final int ID_ITEM_WAX_CAPSULE_BIOMASS = 13039;
	public static final int ID_ITEM_WAX_CAPSULE_BIOFUEL = 13040;

	public static final int ID_ITEM_BITUMINOUS_PEAT = 13041;
	public static final int ID_ITEM_WAX_CAPSULE_OIL = 13042;
	public static final int ID_ITEM_WAX_CAPSULE_FUEL = 13043;
	public static final int ID_ITEM_CAN_OIL = 13044;
	public static final int ID_ITEM_CAN_FUEL = 13045;
	public static final int ID_ITEM_CAN_LAVA = 13046;

	// public static final int ID_ITEM_COCOA_COMB = 13047;

	public static final int SLOTS_BACKPACK_DEFAULT = 15;
	public static final int SLOTS_BACKPACK_T2 = 45;
	public static final int SLOTS_BACKPACK_APIARIST = 125;

	public static final int ID_ITEM_APIARIST_BACKPACK = 13048;
	public static final int ID_ITEM_MINER_BACKPACK = 13049;
	public static final int ID_ITEM_DIGGER_BACKPACK = 13050;
	public static final int ID_ITEM_FORESTER_BACKPACK = 13051;
	public static final int ID_ITEM_HUNTER_BACKPACK = 13052;
	public static final int ID_ITEM_MASON_BACKPACK = 13053;
	public static final int ID_ITEM_DYER_BACKPACK = 13054;
	public static final int ID_ITEM_RAILROADER_BACKPACK = 13055;

	public static final int ID_ITEM_HONEY_POT = 13056;
	public static final int ID_ITEM_SEED_OIL = 13057;
	public static final int ID_ITEM_APPLE_JUICE = 13058;
	public static final int ID_ITEM_LIQUIDS = 13059;

	public static final int ID_ITEM_TINKERER_BACKPACK = 13060;

	// public static final int ID_ITEM_SIMMERING_COMB = 13061;

	public static final int ID_ITEM_CAN_SEED_OIL = 13062;
	public static final int ID_ITEM_CAN_HONEY = 13063;
	public static final int ID_ITEM_CAN_JUICE = 13064;
	public static final int ID_ITEM_WAX_CAPSULE_SEED_OIL = 13065;
	public static final int ID_ITEM_WAX_CAPSULE_HONEY = 13066;
	public static final int ID_ITEM_WAX_CAPSULE_JUICE = 13067;

	public static final int ID_ITEM_PHOSPHOR = 13068;
	public static final int ID_ITEM_REFRACTORY_WAX = 13069;
	public static final int ID_ITEM_REFRACTORY_EMPTY = 13070;
	public static final int ID_ITEM_REFRACTORY_LAVA = 13071;

	/*
	 * public static final int ID_ITEM_STRINGY_COMB = 13072; public static final int ID_ITEM_FROZEN_COMB = 13073; public static final int ID_ITEM_DRIPPING_COMB
	 * = 13074;
	 */

	public static final int ID_ITEM_REFRACTORY_WATER = 13075;
	public static final int ID_ITEM_REFRACTORY_BIOMASS = 13076;
	public static final int ID_ITEM_REFRACTORY_BIOFUEL = 13077;
	public static final int ID_ITEM_REFRACTORY_OIL = 13078;
	public static final int ID_ITEM_REFRACTORY_FUEL = 13079;
	public static final int ID_ITEM_REFRACTORY_SEED_OIL = 13080;
	public static final int ID_ITEM_REFRACTORY_HONEY = 13081;
	public static final int ID_ITEM_REFRACTORY_JUICE = 13082;

	public static final int ID_ITEM_BEE_QUEEN_GE = 13083;
	public static final int ID_ITEM_BEE_DRONE_GE = 13084;
	public static final int ID_ITEM_BEE_PRINCESS_GE = 13085;

	public static final int ID_ITEM_BEEALYZER = 13086;

	// public static final int ID_ITEM_RIVET_COPPER = 13087;

	public static final int ID_ITEM_MINER_BACKPACK_T2 = 13088;
	public static final int ID_ITEM_DIGGER_BACKPACK_T2 = 13089;
	public static final int ID_ITEM_FORESTER_BACKPACK_T2 = 13090;
	public static final int ID_ITEM_HUNTER_BACKPACK_T2 = 13091;
	public static final int ID_ITEM_MASON_BACKPACK_T2 = 13092;
	public static final int ID_ITEM_DYER_BACKPACK_T2 = 13093;
	public static final int ID_ITEM_RAILROADER_BACKPACK_T2 = 13094;

	// public static final int ID_ITEM_KIT_RIVETS_COPPER = 13095;

	public static final int ID_ITEM_ADVENTURER_BACKPACK = 13096;
	public static final int ID_ITEM_ADVENTURER_BACKPACK_T2 = 13097;

	public static final int ID_ITEM_CRAFTING = 13098;
	// public static final int ID_ITEM_PARCHED_COMB = 13099;

	public static final int ID_ITEM_ARMOR_APIARIST_HELMET = 13100;
	public static final int ID_ITEM_ARMOR_APIARIST_CHEST = 13101;
	public static final int ID_ITEM_ARMOR_APIARIST_LEGS = 13102;
	public static final int ID_ITEM_ARMOR_APIARIST_BOOTS = 13103;

	public static final int ID_ITEM_BEE_COMBS = 13104;

	public static final int ID_ITEM_BIOME_FINDER = 13105;

	public static final int ID_ITEM_BUILDER_BACKPACK = 13106;
	public static final int ID_ITEM_BUILDER_BACKPACK_T2 = 13107;

	public static final int ID_ITEM_INFUSER = 13108;
	public static final int ID_ITEM_MEAD = 13109;
	public static final int ID_ITEM_IMPRINTER = 13110;
	public static final int ID_ITEM_PIPETTE = 13111;
	public static final int ID_ITEM_CHIPSETS = 13112;
	public static final int ID_ITEM_SOLDERING_IRON = 13113;
	public static final int ID_ITEM_THERMIONIC_TUBES = 13114;
	public static final int ID_ITEM_MOLTEN_GLASS = 13115;

	public static final int ID_ITEM_STAMPS = 13116;
	public static final int ID_ITEM_LETTERS = 13117;
	public static final int ID_ITEM_MAIL_INDICATOR = 13118;

	public static final int ID_ITEM_CRUSHED_ICE = 13119;
	public static final int ID_ITEM_CAN_ICE = 13120;
	public static final int ID_ITEM_WAX_CAPSULE_ICE = 13121;
	public static final int ID_ITEM_REFRACTORY_ICE = 13122;
	public static final int ID_ITEM_WAXCAST = 13123;
	public static final int ID_ITEM_IMPREGNATED_CASING = 13124;
	public static final int ID_ITEM_FRAME_UNTREATED = 13125;
	public static final int ID_ITEM_FRAME_IMPREGNATED = 13126;
	public static final int ID_ITEM_FRAME_PROVEN = 13127;

	public static final int ID_ITEM_SAPLING = 13128;
	public static final int ID_ITEM_TREEALYZER = 13129;
	public static final int ID_ITEM_FRUITS = 13130;
	public static final int ID_ITEM_GRAFTER = 13131;

	// / Item ID 13150 used by portal gun

	public static final int ID_ITEM_CRATED_WOOD = 13500;
	public static final int ID_ITEM_CRATED_COBBLESTONE = 13501;
	public static final int ID_ITEM_CRATED_DIRT = 13502;
	public static final int ID_ITEM_CRATED_STONE = 13503;
	public static final int ID_ITEM_CRATED_BRICK = 13504;
	public static final int ID_ITEM_CRATED_CACTI = 13505;
	public static final int ID_ITEM_CRATED_SAND = 13506;
	public static final int ID_ITEM_CRATED_OBSIDIAN = 13507;
	public static final int ID_ITEM_CRATED_NETHERRACK = 13508;
	public static final int ID_ITEM_CRATED_SOULSAND = 13509;
	public static final int ID_ITEM_CRATED_SANDSTONE = 13510;
	public static final int ID_ITEM_CRATED_BOGEARTH = 13511;
	public static final int ID_ITEM_CRATED_HUMUS = 13512;
	public static final int ID_ITEM_CRATED_NETHERBRICK = 13513;
	public static final int ID_ITEM_CRATED_PEAT = 13514;
	public static final int ID_ITEM_CRATED_APATITE = 13515;
	public static final int ID_ITEM_CRATED_FERTILIZER = 13516;
	public static final int ID_ITEM_CRATED_TIN = 13517;
	public static final int ID_ITEM_CRATED_COPPER = 13518;
	public static final int ID_ITEM_CRATED_BRONZE = 13519;
	public static final int ID_ITEM_CRATED_WHEAT = 13520;
	public static final int ID_ITEM_CRATED_MYCELIUM = 13521;
	public static final int ID_ITEM_CRATED_MULCH = 13522;
	public static final int ID_ITEM_CRATED_SILVER = 13523;
	public static final int ID_ITEM_CRATED_BRASS = 13524;
	public static final int ID_ITEM_CRATED_NIKOLITE = 13525;
	public static final int ID_ITEM_CRATED_COOKIES = 13526;
	public static final int ID_ITEM_CRATED_HONEYCOMBS = 13527;
	public static final int ID_ITEM_CRATED_BEESWAX = 13528;
	public static final int ID_ITEM_CRATED_POLLEN = 13529;
	public static final int ID_ITEM_CRATED_PROPOLIS = 13530;
	public static final int ID_ITEM_CRATED_HONEYDEW = 13531;
	public static final int ID_ITEM_CRATED_ROYAL_JELLY = 13532;
	public static final int ID_ITEM_CRATED_COCOACOMB = 13533;
	public static final int ID_ITEM_CRATED_REDSTONE = 13534;
	public static final int ID_ITEM_CRATED_LAPIS = 13535;
	public static final int ID_ITEM_CRATED_REEDS = 13536;
	public static final int ID_ITEM_CRATED_CLAY = 13537;
	public static final int ID_ITEM_CRATED_GLOWSTONE = 13538;
	public static final int ID_ITEM_CRATED_APPLES = 13539;
	public static final int ID_ITEM_CRATED_NETHERWART = 13540;
	public static final int ID_ITEM_CRATED_RESIN = 13541;
	public static final int ID_ITEM_CRATED_RUBBER = 13542;
	public static final int ID_ITEM_CRATED_SCRAP = 13543;
	public static final int ID_ITEM_CRATED_UUM = 13544;
	public static final int ID_ITEM_CRATED_SIMMERINGCOMBS = 13545;
	public static final int ID_ITEM_CRATED_STRINGYCOMBS = 13546;
	public static final int ID_ITEM_CRATED_FROZENCOMBS = 13547;
	public static final int ID_ITEM_CRATED_DRIPPINGCOMBS = 13548;
	public static final int ID_ITEM_CRATED_REFRACTORY_WAX = 13549;
	public static final int ID_ITEM_CRATED_PHOSPHOR = 13550;
	public static final int ID_ITEM_CRATED_ASH = 13551;
	public static final int ID_ITEM_CRATED_CHARCOAL = 13552;
	public static final int ID_ITEM_CRATED_GRAVEL = 13553;
	public static final int ID_ITEM_CRATED_COAL = 13554;
	public static final int ID_ITEM_CRATED_SEEDS = 13555;
	public static final int ID_ITEM_CRATED_SAPLINGS = 13556;

	// Bee ids
	public static final int ID_BEE_SPECIES_REDDENED = 31;
	public static final int ID_BEE_SPECIES_DARKENED = 32;
	public static final int ID_BEE_SPECIES_OMEGA = 33;

	// Food stuff
	public static final int FOOD_AMBROSIA_HEAL = 8;
	public static final int FOOD_JUICE_HEAL = 2;
	public static final float FOOD_JUICE_SATURATION = 0.2f;
	public static final int FOOD_HONEY_HEAL = 2;
	public static final float FOOD_HONEY_SATURATION = 0.2f;

	// IndustrialCraft 2
	public static final int ID_IC2_FUELCAN_DAMAGE = 6480;

	// BuildCraft
	public static final int BUILDCRAFT_BLOCKID_ENGINE = 161;
	public static final int BUILDCRAFT_BLOCKID_PIPE = 166;

	// Cultivation
	public static final int FIRSAPLING_GROW_CHANCE = 30; // Lower = better
															// chance, higher =
															// lower chance!
															// Default: 30

	public static final int APIARY_MIN_LEVEL_LIGHT = 11;
	public static final int APIARY_BREEDING_TIME = 100;
	public static final int APIARY_PRODUCT_BASE_CHANCE = 25;
	public static final int APIARY_SPECIALTY_BASE_CHANCE = 15;
	public static final int APIARY_TERTIARY_BASE_CHANCE = 8;

	public static final int ARBORETUM_TREE_HEIGHT_LIMIT = 20;
	public static final int PLANTER_PROCESSING_THROTTLE = 10;
	public static final int PLANTER_LATENCY = 500;
	public static final int PLANTER_MIN_ENERGY_RECEIVED = 10;
	public static final int PLANTER_MAX_ENERGY_RECEIVED = 30;
	public static final int PLANTER_MIN_ACTIVATION_ENERGY = 30;
	public static final int PLANTER_MAX_ENERGY = 500;
	public static final int FORESTER_LATENCY = 1000;
	public static final int FORESTER_MIN_ENERGY_RECEIVED = 10;
	public static final int FORESTER_MAX_ENERGY_RECEIVED = 200;
	public static final int FORESTER_MIN_ACTIVATION_ENERGY = 200;
	public static final int FORESTER_MAX_ENERGY = 500;
	public static final int HARVESTER_PROCESSING_THROTTLE = 200;
	public static final int HARVESTER_LATENCY = 500;
	public static final int HARVESTER_MIN_ENERGY_RECEIVED = 20;
	public static final int HARVESTER_MAX_ENERGY_RECEIVED = 40;
	public static final int HARVESTER_MIN_ACTIVATION_ENERGY = 40;
	public static final int HARVESTER_MAX_ENERGY = 500;

	// Energy
	public static final int ENGINE_TANK_CAPACITY = 10 * BUCKET_VOLUME;
	public static final int ENGINE_CYCLE_DURATION_WATER = 1000;
	public static final int ENGINE_CYCLE_DURATION_JUICE = 10000;
	public static final int ENGINE_CYCLE_DURATION_HONEY = 10000;
	public static final int ENGINE_CYCLE_DURATION_MILK = 40000;
	public static final int ENGINE_CYCLE_DURATION_SEED_OIL = 10000;
	public static final int ENGINE_CYCLE_DURATION_BIOMASS = 10000;
	public static final int ENGINE_FUEL_VALUE_WATER = 1;
	public static final int ENGINE_FUEL_VALUE_JUICE = 1;
	public static final int ENGINE_FUEL_VALUE_HONEY = 1;
	public static final int ENGINE_FUEL_VALUE_MILK = 1;
	public static final int ENGINE_FUEL_VALUE_SEED_OIL = 3;
	public static final int ENGINE_FUEL_VALUE_BIOMASS = 5;
	public static final int ENGINE_HEAT_VALUE_LAVA = 20;

	public static final int ENGINE_BRONZE_HEAT_MAX = 10000;
	public static final int ENGINE_BRONZE_HEAT_LOSS_COOL = 2;
	public static final int ENGINE_BRONZE_HEAT_LOSS_OPERATING = 1;
	public static final int ENGINE_BRONZE_HEAT_LOSS_OVERHEATING = 5;
	public static final int ENGINE_BRONZE_HEAT_GENERATION_ENERGY = 1;

	public static final int ENGINE_COPPER_CYCLE_DURATION_PEAT = 5000;
	public static final int ENGINE_COPPER_FUEL_VALUE_PEAT = 1;
	public static final int ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT = 6000;
	public static final int ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT = 2;
	public static final int ENGINE_COPPER_HEAT_MAX = 10000;
	public static final int ENGINE_COPPER_ASH_FOR_ITEM = 7500;

	public static final int ENGINE_TIN_HEAT_MAX = 3000;
	public static final int ENGINE_TIN_EU_FOR_CYCLE = 6;
	public static final int ENGINE_TIN_ENERGY_PER_CYCLE = 2;
	public static final int ENGINE_TIN_MAX_EU_STORED = 2 * ENGINE_TIN_EU_FOR_CYCLE;
	public static final int ENGINE_TIN_MAX_EU_BATTERY = 100;

	// Factory
	public static final int PROCESSOR_TANK_CAPACITY = 10 * BUCKET_VOLUME;

	public static final int MACHINE_LATENCY = 1000;
	public static final int MACHINE_MIN_ENERGY_RECEIVED = 5;
	public static final int MACHINE_MAX_ENERGY_RECEIVED = 40;
	public static final int MACHINE_MIN_ACTIVATION_ENERGY = 75;
	public static final int MACHINE_MAX_ENERGY = 500;

	public static final int RAINMAKER_RAIN_DURATION_IODINE = 10000;

	public static final int STILL_DESTILLATION_DURATION = 100;
	public static final int STILL_DESTILLATION_INPUT = 10;
	public static final int STILL_DESTILLATION_OUTPUT = 3;

	public static final int BOTTLER_FILLING_TIME = 20;
	public static final int BOTTLER_FUELCAN_VOLUME = 2000;

	// Storage
	public static final int RAINTANK_TANK_CAPACITY = 30 * BUCKET_VOLUME;
	public static final int RAINTANK_AMOUNT_PER_UPDATE = 10;
	public static final int RAINTANK_FILLING_TIME = 10;
	public static final int CARPENTER_CRATING_CYCLES = 5;
	public static final int CARPENTER_UNCRATING_CYCLES = 5;
	public static final int CARPENTER_CRATING_LIQUID_QUANTITY = 100;

	// SMP gui ids
	public static final int ID_GUI_ARBORETUM = 90;
	public static final int ID_GUI_BOTTLER = 91;
	public static final int ID_GUI_ENGINE_BRONZE = 92;
	public static final int ID_GUI_FARM = 93;
	public static final int ID_GUI_FERMENTER = 94;
	public static final int ID_GUI_FORESTER = 95;
	public static final int ID_GUI_PLANTATION = 96;
	public static final int ID_GUI_PUMPKIN_FARM = 97;
	public static final int ID_GUI_PEAT_BOG = 98;
	public static final int ID_GUI_STILL = 99;
	public static final int ID_GUI_ENGINE_COPPER = 89;
	public static final int ID_GUI_RAINTANK = 88;
	public static final int ID_GUI_GENERATOR = 87;
	public static final int ID_GUI_CARPENTER = 86;
	public static final int ID_GUI_MOISTENER = 85;
	public static final int ID_GUI_MUSHROOM_FARM = 84;
	public static final int ID_GUI_APIARY = 83;
	public static final int ID_GUI_CENTRIFUGE = 82;
	public static final int ID_GUI_APIARIST_INVENTORY = 81;
	public static final int ID_GUI_BACKPACK = 80;
	public static final int ID_GUI_SQUEEZER = 79;
	public static final int ID_GUI_NETHER_FARM = 78;
	public static final int ID_GUI_BEEALYZER = 100;
	public static final int ID_GUI_BACKPACK_T2 = 101;
	public static final int ID_GUI_BIOME_FINDER = 102;
	// ID 100 used by bucket filler.

	// BC Trigger ids
	public static final int ID_TRIGGER_LOWFUEL_25 = 600;
	public static final int ID_TRIGGER_LOWFUEL_10 = 601;
	public static final int ID_TRIGGER_LOWRESOURCE_25 = 602;
	public static final int ID_TRIGGER_LOWRESOURCE_10 = 603;
	public static final int ID_TRIGGER_NOQUEEN = 604;
	public static final int ID_TRIGGER_NODRONE = 605;
	public static final int ID_TRIGGER_HASWORK = 606;
	public static final int ID_TRIGGER_LOWSOIL_25 = 607;
	public static final int ID_TRIGGER_LOWSOIL_10 = 608;
	public static final int ID_TRIGGER_LOWGERMLINGS_25 = 609;
	public static final int ID_TRIGGER_LOWGERMLINGS_10 = 610;
	public static final int ID_TRIGGER_HASMAIL = 611;
	public static final int ID_TRIGGER_LOWPAPER_25 = 612;
	public static final int ID_TRIGGER_LOWPAPER_10 = 613;
	public static final int ID_TRIGGER_LOWSTAMPS_40 = 614;
	public static final int ID_TRIGGER_LOWSTAMPS_20 = 615;
	public static final int ID_TRIGGER_LOWINPUT_25 = 616;
	public static final int ID_TRIGGER_LOWINPUT_10 = 617;
	public static final int ID_TRIGGER_BUFFER_75 = 618;
	public static final int ID_TRIGGER_BUFFER_90 = 619;
	public static final int ID_TRIGGER_NOFRAMES = 620;
	
	public static final int ID_TRIGGER_LOWLIQUID_50 = 621;
	public static final int ID_TRIGGER_LOWLIQUID_25 = 622;
	public static final int ID_TRIGGER_LOWSOIL_128 = 623;
	public static final int ID_TRIGGER_LOWSOIL_64 = 624;
	public static final int ID_TRIGGER_LOWSOIL_32 = 625;
	public static final int ID_TRIGGER_LOWFERTILIZER_50 = 626;
	public static final int ID_TRIGGER_LOWFERTILIZER_25 = 627;

	// / BEES
	public static final int SPECIES_BEE_START = 0;
	public static final int SPECIES_BEE_LIMIT = 256;

	// / TREES
	public static final int SPECIES_TREE_START = 512;
	public static final int SPECIES_TREE_LIMIT = 640;

}
