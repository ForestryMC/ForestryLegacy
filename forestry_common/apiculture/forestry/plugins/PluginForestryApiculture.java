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
package forestry.plugins;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.IPacketHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.CommandBeekeepingMode;
import forestry.apiculture.CommandGiveBee;
import forestry.apiculture.FlowerProviderCacti;
import forestry.apiculture.FlowerProviderEnd;
import forestry.apiculture.FlowerProviderJungle;
import forestry.apiculture.FlowerProviderMushroom;
import forestry.apiculture.FlowerProviderNetherwart;
import forestry.apiculture.FlowerProviderVanilla;
import forestry.apiculture.FlowerProviderWheat;
import forestry.apiculture.GuiHandlerApiculture;
import forestry.apiculture.PacketHandlerApiculture;
import forestry.apiculture.SaveEventHandlerApiculture;
import forestry.apiculture.VillageHandlerApiculture;
import forestry.apiculture.gadgets.BlockAlveary;
import forestry.apiculture.gadgets.BlockBeehives;
import forestry.apiculture.gadgets.BlockCandle;
import forestry.apiculture.gadgets.BlockStump;
import forestry.apiculture.gadgets.MachineApiary;
import forestry.apiculture.gadgets.MachineApiaristChest;
import forestry.apiculture.gadgets.TileAlvearyFan;
import forestry.apiculture.gadgets.TileAlvearyHeater;
import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.apiculture.gadgets.TileAlvearyPlain;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.apiculture.gadgets.TileSwarm;
import forestry.apiculture.genetics.AlleleBeeSpecies;
import forestry.apiculture.genetics.AlleleEffectAggressive;
import forestry.apiculture.genetics.AlleleEffectCreeper;
import forestry.apiculture.genetics.AlleleEffectExploration;
import forestry.apiculture.genetics.AlleleEffectGlacial;
import forestry.apiculture.genetics.AlleleEffectHeroic;
import forestry.apiculture.genetics.AlleleEffectIgnition;
import forestry.apiculture.genetics.AlleleEffectMiasmic;
import forestry.apiculture.genetics.AlleleEffectMisanthrope;
import forestry.apiculture.genetics.AlleleEffectNone;
import forestry.apiculture.genetics.AlleleEffectPotion;
import forestry.apiculture.genetics.AlleleEffectRadioactive;
import forestry.apiculture.genetics.AlleleEffectResurrection;
import forestry.apiculture.genetics.AlleleEffectSnowing;
import forestry.apiculture.genetics.AlleleFlowers;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.BeeHelper;
import forestry.apiculture.genetics.BeeTemplates;
import forestry.apiculture.genetics.BeekeepingMode;
import forestry.apiculture.genetics.BranchBees;
import forestry.apiculture.genetics.BreedingManager;
import forestry.apiculture.genetics.HiveDrop;
import forestry.apiculture.genetics.JubilanceNone;
import forestry.apiculture.genetics.JubilanceProviderHermit;
import forestry.apiculture.genetics.Mutation;
import forestry.apiculture.genetics.MutationTimeLimited;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBeealyzer;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.apiculture.items.ItemHiveFrame;
import forestry.apiculture.items.ItemHoneycomb;
import forestry.apiculture.items.ItemImprinter;
import forestry.apiculture.items.ItemScoop;
import forestry.apiculture.items.ItemWaxCast;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.trigger.TriggerNoFrames;
import forestry.apiculture.worldgen.WorldGenHiveEnd;
import forestry.apiculture.worldgen.WorldGenHiveForest;
import forestry.apiculture.worldgen.WorldGenHiveJungle;
import forestry.apiculture.worldgen.WorldGenHiveMeadows;
import forestry.apiculture.worldgen.WorldGenHiveParched;
import forestry.apiculture.worldgen.WorldGenHiveSnow;
import forestry.apiculture.worldgen.WorldGenHiveSwamp;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.genetics.Allele;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.Trigger;
import forestry.core.utils.ShapedRecipeCustom;

@PluginInfo(pluginID = "Apiculture", name = "Apiculture", author = "SirSengir", url = Defaults.URL, description = "Adds bees, beekeeping and bee products. Affects world generation.")
public class PluginForestryApiculture extends NativePlugin {

	@SidedProxy(clientSide = "forestry.apiculture.proxy.ClientProxyApiculture", serverSide = "forestry.apiculture.proxy.ProxyApiculture")
	public static ProxyApiculture proxy;

	private static final String CONFIG_CATEGORY = "apiculture";
	private Configuration apicultureConfig;

	public static String beekeepingMode = "NORMAL";
	public static int beeCycleTicks = 550;

	public static boolean apiarySideSensitive = false;

	public static Trigger triggerNoFrames;

	private ArrayList<IHiveDrop> forestDrops;
	private ArrayList<IHiveDrop> meadowsDrops;
	private ArrayList<IHiveDrop> desertDrops;
	private ArrayList<IHiveDrop> jungleDrops;
	private ArrayList<IHiveDrop> endDrops;
	private ArrayList<IHiveDrop> snowDrops;
	private ArrayList<IHiveDrop> swampDrops;
	private ArrayList<IHiveDrop> swarmDrops;
	
	public static MachineDefinition definitionApiary;
	public static MachineDefinition definitionChest;
	
	@Override
	public boolean isAvailable() {
		return !Config.disableApiculture;
	}

	@Override
	public String getDescription() {
		return "Apiculture";
	}

	@Override
	public void preInit() {
		super.preInit();

		createHiveDropArrays();

		int blockid = Config.getOrCreateBlockIdProperty("apiculture", Defaults.ID_BLOCK_APICULTURE);
		
		definitionApiary = new MachineDefinition(blockid, Defaults.DEFINITION_APIARY_META, "forestry.Apiary", MachineApiary.class,
				ShapedRecipeCustom.createShapedRecipe(
						new Object[] { "XXX", "#C#", "###", Character.valueOf('X'), "slabWood",
						Character.valueOf('#'), "plankWood", Character.valueOf('C'), ForestryItem.impregnatedCasing },
						new ItemStack(blockid, 1, Defaults.DEFINITION_APIARY_META))
				).setFaces(135, 137, 136, 136, 138, 138, 135, 137);
		
		definitionChest = new MachineDefinition(blockid, Defaults.DEFINITION_APIARISTCHEST_META, "forestry.ApiaristChest", MachineApiaristChest.class,
				ShapedRecipeCustom.createShapedRecipe(
						new Object[] { " # ", "XYX", "XXX", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
								"beeComb", Character.valueOf('Y'), Block.chest },
								new ItemStack(blockid, 1, Defaults.DEFINITION_APIARISTCHEST_META))
				).setFaces(119, 121, 122, 120, 122, 122, 119, 121);
		
		ForestryBlock.apiculture = new BlockBase(blockid,
				Material.iron, new MachineDefinition[] { definitionApiary, definitionChest }).setBlockName("for.apiculture").setCreativeTab(Tabs.tabApiculture);
		Item.itemsList[ForestryBlock.apiculture.blockID] = null;
		Item.itemsList[ForestryBlock.apiculture.blockID] = new ItemForestryBlock(ForestryBlock.apiculture.blockID - 256, "for.apiculture");

		ForestryBlock.beehives = (new BlockBeehives(Config.getOrCreateBlockIdProperty("beehives", Defaults.ID_BLOCK_BEEHIVES))).setBlockName("oreCopper");
		Item.itemsList[ForestryBlock.beehives.blockID] = null;
		Item.itemsList[ForestryBlock.beehives.blockID] = (new ItemForestryBlock(ForestryBlock.beehives.blockID - 256, "oreCopper"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 0, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 1, "scoop", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 2, "scoop", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 3, "scoop", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 4, "scoop", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 5, "scoop", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 6, "scoop", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 7, "scoop", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.beehives, 8, "scoop", 0);

		// Init bee interface
		BeeManager.beeInterface = new BeeHelper();
		BeeManager.villageBees = new ArrayList[] { new ArrayList<IBeeGenome>(), new ArrayList<IBeeGenome>() };

		// Candles
		ForestryBlock.stump = new BlockStump(Config.getOrCreateBlockIdProperty("stump", Defaults.ID_BLOCK_STUMP), 40).setBlockName("stump");
		Item.itemsList[ForestryBlock.stump.blockID] = null;
		Item.itemsList[ForestryBlock.stump.blockID] = (new ItemForestryBlock(ForestryBlock.stump.blockID - 256, "stump"));
		ForestryBlock.candle = new BlockCandle(Config.getOrCreateBlockIdProperty("candle", Defaults.ID_BLOCK_CANDLE), 39).setBlockName("candle");
		Item.itemsList[ForestryBlock.candle.blockID] = null;
		Item.itemsList[ForestryBlock.candle.blockID] = (new ItemForestryBlock(ForestryBlock.candle.blockID - 256, "candle"));

		// Alveary and Components
		ForestryBlock.alveary = new BlockAlveary(Config.getOrCreateBlockIdProperty("alveary", Defaults.ID_BLOCK_ALVEARY)).setBlockName("alveary");
		Item.itemsList[ForestryBlock.alveary.blockID] = null;
		Item.itemsList[ForestryBlock.alveary.blockID] = (new ItemForestryBlock(ForestryBlock.alveary.blockID - 256, "alveary"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.alveary, "axe", 0);

		ForestryBlock.swarmer = new BlockAlveary(Config.getOrCreateBlockIdProperty("swarmer", Defaults.ID_BLOCK_ALVEARY_SWARMER)).setBlockName("swarmer");
		Item.itemsList[ForestryBlock.swarmer.blockID] = null;
		Item.itemsList[ForestryBlock.swarmer.blockID] = (new ItemForestryBlock(ForestryBlock.swarmer.blockID - 256, "swarmer"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.swarmer, "axe", 0);

		ForestryBlock.heater = new BlockAlveary(Config.getOrCreateBlockIdProperty("heater", Defaults.ID_BLOCK_ALVEARY_HEATER))
				.setBlockName("alvearyHeater");
		Item.itemsList[ForestryBlock.heater.blockID] = null;
		Item.itemsList[ForestryBlock.heater.blockID] = (new ItemForestryBlock(ForestryBlock.heater.blockID - 256, "alvearyHeater"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.heater, "axe", 0);

		ForestryBlock.fan = new BlockAlveary(Config.getOrCreateBlockIdProperty("fan", Defaults.ID_BLOCK_ALVEARY_FAN))
				.setBlockName("alvearyFan");
		Item.itemsList[ForestryBlock.fan.blockID] = null;
		Item.itemsList[ForestryBlock.fan.blockID] = (new ItemForestryBlock(ForestryBlock.fan.blockID - 256, "alvearyFan"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.fan, "axe", 0);

		// Add triggers
		triggerNoFrames = new TriggerNoFrames(Defaults.ID_TRIGGER_NOFRAMES);
	}

	@Override
	public void doInit() {
		super.doInit();

		// Init breeding manager
		BeeManager.breedingManager = new BreedingManager();
		
		proxy.addLocalizations();
		
		apicultureConfig = new Configuration();

		Property apiarySideSense = apicultureConfig.get("apiary.sidesensitive", CONFIG_CATEGORY, false);
		apiarySideSense.Comment = "set to false if apiaries should output all items regardless of side a pipe is attached to";
		apiarySideSensitive = Boolean.parseBoolean(apiarySideSense.Value);

		Property breedingMode = apicultureConfig.get("beekeeping.mode", CONFIG_CATEGORY, "NORMAL");
		breedingMode.Comment = "change beekeeping modes here. possible values EASY, NORMAL, HARD, HARDCORE, INSANE. mods may add additional modes.";
		beekeepingMode = breedingMode.Value.trim();
		Proxies.log.finer("Beekeeping mode read from config: " + beekeepingMode);

		Property additionalFlowers = apicultureConfig.get("beekeeping.flowers.custom", CONFIG_CATEGORY, "");
		additionalFlowers.Comment = "add additional flower blocks for apiaries here in the format id:meta. separate blocks using ';'. will be treated like vanilla flowers. not recommended for flowers implemented as tile entities.";
		parseAdditionalFlowers(additionalFlowers.Value, FlowerManager.plainFlowers);

		Property beeBlacklist = apicultureConfig.get("species.blacklist", CONFIG_CATEGORY, "");
		beeBlacklist.Comment = "add species to blacklist identified by their uid and seperated with ';'.";
		parseBeeBlacklist(beeBlacklist.Value);

		apicultureConfig.save();

		createAlleles();
		createMutations();
		registerBeehiveDrops();

		BeeManager.breedingManager.registerBeekeepingMode(BeekeepingMode.easy);
		BeeManager.breedingManager.registerBeekeepingMode(BeekeepingMode.normal);
		BeeManager.breedingManager.registerBeekeepingMode(BeekeepingMode.hard);
		BeeManager.breedingManager.registerBeekeepingMode(BeekeepingMode.hardcore);
		BeeManager.breedingManager.registerBeekeepingMode(BeekeepingMode.insane);

		// Inducers for swarmer
		BeeManager.inducers.put(new ItemStack(ForestryItem.royalJelly), 10);

		registerTemplates();

		definitionApiary.register();
		definitionChest.register();
		GameRegistry.registerTileEntity(TileAlvearyPlain.class, "forestry.Alveary");
		GameRegistry.registerTileEntity(TileSwarm.class, "forestry.Swarm");
		GameRegistry.registerTileEntity(TileAlvearySwarmer.class, "forestry.AlvearySwarmer");
		GameRegistry.registerTileEntity(TileAlvearyHeater.class, "forestry.AlvearyHeater");
		GameRegistry.registerTileEntity(TileAlvearyFan.class, "forestry.AlvearyFan");
		GameRegistry.registerTileEntity(TileAlvearyHygroregulator.class, "forestry.AlvearyHygro");

		BeeManager.villageBees[0].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getForestTemplate()));
		BeeManager.villageBees[0].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getMeadowsTemplate()));
		BeeManager.villageBees[0].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getModestTemplate()));
		BeeManager.villageBees[0].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getMarshyTemplate()));
		BeeManager.villageBees[0].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getWintryTemplate()));
		BeeManager.villageBees[0].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getTropicalTemplate()));

		BeeManager.villageBees[1].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getForestRainResistTemplate()));
		BeeManager.villageBees[1].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getCommonTemplate()));
		BeeManager.villageBees[1].add(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getValiantTemplate()));

		// Register villager stuff
		VillageHandlerApiculture villageHandler = new VillageHandlerApiculture();
		VillagerRegistry.instance().registerVillageCreationHandler(villageHandler);
		VillagerRegistry.instance().registerVillagerType(Defaults.ID_VILLAGER_BEEKEEPER, Defaults.TEXTURE_SKIN_BEEKPEEPER);
		VillagerRegistry.instance().registerVillageTradeHandler(Defaults.ID_VILLAGER_BEEKEEPER, villageHandler);
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();

	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerApiculture();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerApiculture();
	}

	@Override
	protected void registerPackages() {
		//GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_APIARY, PackagesApiculture.getApiaryPackage());
		//GadgetManager.registerMillPackage(Defaults.ID_PACKAGE_MILL_APIARIST_CHEST, PackagesApiculture.getApiaristChestPackage());
	}

	@Override
	protected void registerItems() {

		// / BEES
		ForestryItem.beeQueenGE = (new ItemBeeGE(Config.getOrCreateItemIdProperty("beeQueenGE", Defaults.ID_ITEM_BEE_QUEEN_GE), EnumBeeType.QUEEN))
				.setItemName("beeQueenGE");
		ForestryItem.beeDroneGE = (new ItemBeeGE(Config.getOrCreateItemIdProperty("beeDroneGE", Defaults.ID_ITEM_BEE_DRONE_GE), EnumBeeType.DRONE))
				.setItemName("beeDroneGE");
		ForestryItem.beePrincessGE = (new ItemBeeGE(Config.getOrCreateItemIdProperty("beePrincessGE", Defaults.ID_ITEM_BEE_PRINCESS_GE), EnumBeeType.PRINCESS))
				.setItemName("beePrincessGE");

		ForestryItem.beealyzer = (new ItemBeealyzer(Config.getOrCreateItemIdProperty("beealyzer", Defaults.ID_ITEM_BEEALYZER))).setItemName("beealyzer")
				.setIconIndex(83);
		ForestryItem.biomeFinder = new ItemHabitatLocator(Config.getOrCreateItemIdProperty("biomeFinder", Defaults.ID_ITEM_BIOME_FINDER)).setItemName(
				"biomeFinder").setIconCoord(9, 6);
		ForestryItem.imprinter = (new ItemImprinter(Config.getOrCreateItemIdProperty("imprinter", Defaults.ID_ITEM_IMPRINTER))).setItemName("imprinter")
				.setIconIndex(82);

		// / COMB FRAMES
		ForestryItem.frameUntreated = new ItemHiveFrame(Config.getOrCreateItemIdProperty("frameUntreated", Defaults.ID_ITEM_FRAME_UNTREATED), 80).setItemName(
				"frameUntreated").setIconIndex(64);
		ForestryItem.frameImpregnated = new ItemHiveFrame(Config.getOrCreateItemIdProperty("frameImpregnated", Defaults.ID_ITEM_FRAME_IMPREGNATED), 240)
				.setItemName("frameImpregnated").setIconIndex(65);
		ForestryItem.frameProven = new ItemHiveFrame(Config.getOrCreateItemIdProperty("frameProven", Defaults.ID_ITEM_FRAME_PROVEN), 720).setItemName(
				"frameProven").setIconIndex(66);

		// / SCOOP
		ForestryItem.scoop = (new ItemScoop(Config.getOrCreateItemIdProperty("scoop", Defaults.ID_ITEM_SCOOP))).setItemName("scoop").setIconIndex(43)
				.setMaxStackSize(1);
		MinecraftForge.setToolClass(ForestryItem.scoop, "scoop", 3);

		// / BEE RESOURCES
		ForestryItem.honeyDrop = new ItemOverlay(Config.getOrCreateItemIdProperty("honeyDrop", Defaults.ID_ITEM_HONEY_DROP), Tabs.tabApiculture,
				new OverlayInfo[] { new OverlayInfo("honey", 0xecb42d, 0xe8c814), new OverlayInfo("charged", 0x800505, 0x9c0707).setIsSecret(),
						new OverlayInfo("omega", 0x191919, 0x4a8ca7).setIsSecret() }).setIcons(109, 110).setItemName("honeyDrop");
		OreDictionary.registerOre("dropHoney", new ItemStack(ForestryItem.honeyDrop, 1, 0));

		ForestryItem.pollen = new ItemOverlay(Config.getOrCreateItemIdProperty("pollen", Defaults.ID_ITEM_POLLEN), Tabs.tabApiculture,
				new OverlayInfo[] { new OverlayInfo("normal", 0xa28a25, 0xa28a25), new OverlayInfo("crystalline", 0xffffff, 0xc5feff) }).setIcons(46, 45)
				.setItemName("pollen");
		OreDictionary.registerOre("itemPollen", new ItemStack(ForestryItem.pollen));

		ForestryItem.propolis = new ItemOverlay(Config.getOrCreateItemIdProperty("propolis", Defaults.ID_ITEM_PROPOLIS), Tabs.tabApiculture,
				new OverlayInfo[] { new OverlayInfo("normal", 0xc5b24e), new OverlayInfo("sticky", 0xc68e57),
						new OverlayInfo("pulsating", 0x2ccdb1).setIsSecret(), new OverlayInfo("silky", 0xddff00) }).setIcons(98, 1).setItemName("propolis");

		ForestryItem.honeydew = (new ItemForestry(Config.getOrCreateItemIdProperty("honeydew", Defaults.ID_ITEM_HONEYDEW))).setItemName("honeydew")
				.setIconIndex(47).setCreativeTab(Tabs.tabApiculture);
		OreDictionary.registerOre("dropHoneydew", new ItemStack(ForestryItem.honeydew));

		ForestryItem.royalJelly = (new ItemForestry(Config.getOrCreateItemIdProperty("royalJelly", Defaults.ID_ITEM_ROYAL_JELLY))).setItemName("royalJelly")
				.setIconIndex(48).setCreativeTab(Tabs.tabApiculture);
		OreDictionary.registerOre("dropRoyalJelly", new ItemStack(ForestryItem.royalJelly));

		ForestryItem.waxCast = new ItemWaxCast(Config.getOrCreateItemIdProperty("waxCast", Defaults.ID_ITEM_WAXCAST)).setItemName("waxCast").setIconIndex(60)
				.setCreativeTab(Tabs.tabApiculture);

		// / BEE COMBS
		ForestryItem.beeComb = new ItemHoneycomb(Config.getOrCreateItemIdProperty("beeCombs", Defaults.ID_ITEM_BEE_COMBS)).setItemName("beeCombs");
		OreDictionary.registerOre("beeComb", new ItemStack(ForestryItem.beeComb, 1, -1));

		// / APIARIST'S CLOTHES
		ForestryItem.apiaristHat = new ItemArmorApiarist(Config.getOrCreateItemIdProperty("apiaristHelmet", Defaults.ID_ITEM_ARMOR_APIARIST_HELMET), 0)
				.setItemName("apiaristHelmet").setIconIndex(101);
		ForestryItem.apiaristChest = new ItemArmorApiarist(Config.getOrCreateItemIdProperty("apiaristChest", Defaults.ID_ITEM_ARMOR_APIARIST_CHEST), 1)
				.setItemName("apiaristChest").setIconIndex(102);
		ForestryItem.apiaristLegs = new ItemArmorApiarist(Config.getOrCreateItemIdProperty("apiaristLegs", Defaults.ID_ITEM_ARMOR_APIARIST_LEGS), 2)
				.setItemName("apiaristLegs").setIconIndex(103);
		ForestryItem.apiaristBoots = new ItemArmorApiarist(Config.getOrCreateItemIdProperty("apiaristBoots", Defaults.ID_ITEM_ARMOR_APIARIST_BOOTS), 3)
				.setItemName("apiaristBoots").setIconIndex(104);

	}

	@Override
	protected void registerBackpackItems() {}

	@Override
	protected void registerCrates() {
		ForestryItem.cratedBeeswax = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedBeeswax", Defaults.ID_ITEM_CRATED_BEESWAX),
				new ItemStack(ForestryItem.beeswax))).setItemName("cratedBeeswax").setIconIndex(30);
		ForestryItem.cratedPollen = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedPollen", Defaults.ID_ITEM_CRATED_POLLEN),
				new ItemStack(ForestryItem.pollen))).setItemName("cratedPollen").setIconIndex(31);
		ForestryItem.cratedPropolis = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedPropolis", Defaults.ID_ITEM_CRATED_PROPOLIS),
				new ItemStack(ForestryItem.propolis))).setItemName("cratedPropolis").setIconIndex(32);
		ForestryItem.cratedHoneydew = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedHoneydew", Defaults.ID_ITEM_CRATED_HONEYDEW),
				new ItemStack(ForestryItem.honeydew))).setItemName("cratedHoneydew").setIconIndex(33);
		ForestryItem.cratedRoyalJelly = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedRoyalJelly", Defaults.ID_ITEM_CRATED_ROYAL_JELLY),
				new ItemStack(ForestryItem.royalJelly))).setItemName("cratedRoyalJelly").setIconIndex(34);

		ForestryItem.cratedHoneycombs = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedHoneycombs", Defaults.ID_ITEM_CRATED_HONEYCOMBS),
				new ItemStack(ForestryItem.beeComb, 1, 0))).setItemName("cratedHoneycombs").setIconIndex(29);
		ForestryItem.cratedCocoaComb = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedCocoaComb", Defaults.ID_ITEM_CRATED_COCOACOMB),
				new ItemStack(ForestryItem.beeComb, 1, 1))).setItemName("cratedCocoaComb").setIconIndex(35);
		ForestryItem.cratedSimmeringCombs = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedSimmeringCombs",
				Defaults.ID_ITEM_CRATED_SIMMERINGCOMBS), new ItemStack(ForestryItem.beeComb, 1, 2))).setItemName("cratedSimmeringCombs").setIconIndex(47);
		ForestryItem.cratedStringyCombs = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedStringyCombs",
				Defaults.ID_ITEM_CRATED_STRINGYCOMBS), new ItemStack(ForestryItem.beeComb, 1, 3))).setItemName("cratedStringyCombs").setIconIndex(48);
		ForestryItem.cratedFrozenCombs = (ItemCrated) (new ItemCrated(
				Config.getOrCreateItemIdProperty("cratedFrozenCombs", Defaults.ID_ITEM_CRATED_FROZENCOMBS), new ItemStack(ForestryItem.beeComb, 1, 4)))
				.setItemName("cratedFrozenCombs").setIconIndex(49);
		ForestryItem.cratedDrippingCombs = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedDrippingCombs",
				Defaults.ID_ITEM_CRATED_DRIPPINGCOMBS), new ItemStack(ForestryItem.beeComb, 1, 5))).setItemName("cratedDrippingCombs").setIconIndex(50);

		ForestryItem.cratedRefractoryWax = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedRefractoryWax",
				Defaults.ID_ITEM_CRATED_REFRACTORY_WAX), new ItemStack(ForestryItem.refractoryWax))).setItemName("cratedRefractoryWax").setIconIndex(51);

	}

	@Override
	protected void registerRecipes() {

		// / APIARIST'S ARMOR
		Proxies.common.addRecipe(new ItemStack(ForestryItem.apiaristHat), new Object[] { "###", "# #", Character.valueOf('#'),
				new ItemStack(ForestryItem.craftingMaterial, 1, 3) });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.apiaristChest), new Object[] { "# #", "###", "###", Character.valueOf('#'),
				new ItemStack(ForestryItem.craftingMaterial, 1, 3) });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.apiaristLegs), new Object[] { "###", "# #", "# #", Character.valueOf('#'),
				new ItemStack(ForestryItem.craftingMaterial, 1, 3) });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.apiaristBoots), new Object[] { "# #", "# #", Character.valueOf('#'),
				new ItemStack(ForestryItem.craftingMaterial, 1, 3) });

		// / HABITAT LOCATOR
		Proxies.common.addRecipe(new ItemStack(ForestryItem.biomeFinder),
				new Object[] { " X ", "X#X", " X ", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), "ingotBronze" });

		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst, 3), new Object[] { "###", "YXY", Character.valueOf('#'), ForestryItem.waxCapsule,
				Character.valueOf('X'), Item.bone, Character.valueOf('Y'), ForestryItem.pollen });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst, 3), new Object[] { "###", "YXY", Character.valueOf('#'), ForestryItem.canEmpty,
				Character.valueOf('X'), Item.bone, Character.valueOf('Y'), ForestryItem.pollen });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst), new Object[] { "###", "YXY", "###", Character.valueOf('#'), ForestryItem.honeyDrop,
				Character.valueOf('Y'), ForestryItem.fertilizerCompound, Character.valueOf('X'), ForestryItem.waxCapsule });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst), new Object[] { "###", "YXY", "###", Character.valueOf('#'), ForestryItem.honeyDrop,
				Character.valueOf('Y'), ForestryItem.fertilizerCompound, Character.valueOf('X'), ForestryItem.canEmpty });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst), new Object[] { "###", "YXY", "###", Character.valueOf('#'), ForestryItem.honeyDrop,
				Character.valueOf('Y'), ForestryItem.pollen, Character.valueOf('X'), ForestryItem.waxCapsule });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst), new Object[] { "###", "YXY", "###", Character.valueOf('#'), ForestryItem.honeyDrop,
				Character.valueOf('Y'), ForestryItem.pollen, Character.valueOf('X'), ForestryItem.canEmpty });

		// Bees
		Proxies.common.addRecipe(new ItemStack(ForestryItem.scoop, 1),
				new Object[] { "#X#", "###", " # ", Character.valueOf('#'), "stickWood", Character.valueOf('X'), Block.cloth });
		Proxies.common.addRecipe(new ItemStack(Item.slimeBall),
				new Object[] { "#X#", "#X#", "#X#", Character.valueOf('#'), ForestryItem.propolis, Character.valueOf('X'), ForestryItem.pollen });
		Proxies.common.addRecipe(new ItemStack(Item.speckledMelon), new Object[] { "#X#", "#Y#", "#X#", Character.valueOf('#'), ForestryItem.honeyDrop,
				Character.valueOf('X'), ForestryItem.honeydew, Character.valueOf('Y'), Item.melon });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.frameUntreated),
				new Object[] { "###", "#S#", "###", Character.valueOf('#'), "stickWood", Character.valueOf('S'), Item.silk });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.frameImpregnated), new Object[] { "###", "#S#", "###", Character.valueOf('#'),
				ForestryItem.stickImpregnated, Character.valueOf('S'), Item.silk });

		// FOOD STUFF
		if (ForestryItem.honeyedSlice != null) {
			Proxies.common.addRecipe(new ItemStack(ForestryItem.honeyedSlice, 4), new Object[] { "###", "#X#", "###", Character.valueOf('#'),
					ForestryItem.honeyDrop, Character.valueOf('X'), Item.bread });
		}
		if (ForestryItem.honeyPot != null) {
			Proxies.common.addRecipe(new ItemStack(ForestryItem.honeyPot, 1), new Object[] { "# #", " X ", "# #", Character.valueOf('#'),
					ForestryItem.honeyDrop, Character.valueOf('X'), ForestryItem.waxCapsule });
		}
		if (ForestryItem.ambrosia != null) {
			Proxies.common.addRecipe(new ItemStack(ForestryItem.ambrosia), new Object[] { "#Y#", "XXX", "###", Character.valueOf('#'), ForestryItem.honeydew,
					Character.valueOf('X'), ForestryItem.royalJelly, Character.valueOf('Y'), ForestryItem.waxCapsule });
		}

		// / CAPSULES
		Proxies.common.addRecipe(GameMode.getGameMode().getRecipeCapsuleOutput(), new Object[] { "###", Character.valueOf('#'), ForestryItem.beeswax });
		Proxies.common
				.addRecipe(GameMode.getGameMode().getRecipeRefractoryOutput(), new Object[] { "###", Character.valueOf('#'), ForestryItem.refractoryWax });

		// / BITUMINOUS PEAT
		Proxies.common.addRecipe(new ItemStack(ForestryItem.bituminousPeat),
				new Object[] { " # ", "XYX", " # ", Character.valueOf('#'), "dustAsh", Character.valueOf('X'), ForestryItem.peat, Character.valueOf('Y'),
						ForestryItem.propolis });
		// / TORCHES
		Proxies.common.addRecipe(new ItemStack(Block.torchWood, 3),
				new Object[] { " # ", " # ", " Y ", Character.valueOf('#'), ForestryItem.beeswax, Character.valueOf('Y'), "stickWood" });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.craftingMaterial, 1, 1), new Object[] { "# #", " # ", "# #", Character.valueOf('#'),
				new ItemStack(ForestryItem.propolis, 1, 2) });

		// / CANDLES
		RecipeManagers.carpenterManager.addRecipe(30, new LiquidStack(Block.waterStill, 600), null, new ItemStack(ForestryBlock.candle, 24), new Object[] {
				" X ", "###", "###", '#', ForestryItem.beeswax, 'X', Item.silk });
		RecipeManagers.carpenterManager.addRecipe(10, new LiquidStack(Block.waterStill, 200), null, new ItemStack(ForestryBlock.candle, 6), new Object[] {
				"#X#", '#', ForestryItem.beeswax, 'X', new ItemStack(ForestryItem.craftingMaterial, 1, 2) });

		// / WAX CAST
		Proxies.common.addRecipe(new ItemStack(ForestryItem.waxCast), new Object[] { "###", "# #", "###", '#', ForestryItem.beeswax });

		// / ALVEARY
		Proxies.common.addRecipe(new ItemStack(ForestryBlock.alveary), new Object[] { "###", "#X#", "###", 'X',
				ForestryItem.impregnatedCasing, '#', new ItemStack(ForestryItem.craftingMaterial, 1, 6) });
		// SWARMER
		Proxies.common.addRecipe(new ItemStack(ForestryBlock.alveary, 1, 2), new Object[] { "#G#", " X ", "#G#", '#',
				new ItemStack(ForestryItem.tubes, 1, 5), 'X', ForestryBlock.alveary, 'G', Item.ingotGold });
		// FAN
		Proxies.common.addRecipe(new ItemStack(ForestryBlock.alveary, 1, 3), new Object[] { "I I", " X ", "I#I", '#',
				new ItemStack(ForestryItem.tubes, 1, 4), 'X', ForestryBlock.alveary, 'I', Item.ingotIron });
		// HEATER
		Proxies.common.addRecipe(new ItemStack(ForestryBlock.alveary, 1, 4),
				new Object[] { "#I#", " X ", "YYY", '#', new ItemStack(ForestryItem.tubes, 1, 4), 'X',
						ForestryBlock.alveary, 'I', Item.ingotIron, 'Y', Block.stone });
		// HYGROREGULATOR
		Proxies.common.addRecipe(new ItemStack(ForestryBlock.alveary, 1, 5),
				new Object[] { "GIG", "GXG", "GIG", 'X', ForestryBlock.alveary, 'I', Item.ingotIron, 'G', Block.glass });

		// / SQUEEZER
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.honeyDrop) }, new LiquidStack(ForestryItem.liquidHoney, 100),
				new ItemStack(ForestryItem.propolis), 5);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.honeydew) }, new LiquidStack(ForestryItem.liquidHoney, 100));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.phosphor, 2), new ItemStack(Block.sand) }, new LiquidStack(
				Block.lavaStill, 2000));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.phosphor, 2), new ItemStack(Block.dirt) }, new LiquidStack(
				Block.lavaStill, 1600));

		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(100, new LiquidStack(Block.waterStill.blockID, 2000), null, new ItemStack(ForestryItem.beealyzer),
				new Object[] { "X#X", "X#X", "RDR", Character.valueOf('#'), Block.thinGlass, Character.valueOf('X'), "ingotTin", Character.valueOf('R'),
						Item.redstone, Character.valueOf('D'), Item.diamond });
		RecipeManagers.carpenterManager.addRecipe(
				50,
				new LiquidStack(ForestryItem.liquidHoney, 500),
				null,
				new ItemStack(ForestryItem.craftingMaterial, 1, 6),
				new Object[] { " J ", "###", "WPW", Character.valueOf('#'), "plankWood", Character.valueOf('J'), ForestryItem.royalJelly,
						Character.valueOf('W'), ForestryItem.beeswax, Character.valueOf('P'), ForestryItem.pollen });

		// / CENTRIFUGE
		// Honey combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 0), new ItemStack(ForestryItem.beeswax), new ItemStack(
				ForestryItem.honeyDrop), 90);
		// Cocoa combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 1), new ItemStack(ForestryItem.beeswax), new ItemStack(
				Item.dyePowder, 1, 3), 50);
		// Simmering combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 2), new ItemStack(ForestryItem.refractoryWax), new ItemStack(
				ForestryItem.phosphor, 2), 70);
		// Stringy combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 3), new ItemStack(ForestryItem.propolis), new ItemStack(
				ForestryItem.honeyDrop), 40);
		// Drippig combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 5), new ItemStack(ForestryItem.honeydew), new ItemStack(
				ForestryItem.honeyDrop), 40);
		// Frozen combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 4), new ItemStack[] { new ItemStack(ForestryItem.beeswax),
				new ItemStack(ForestryItem.honeyDrop), new ItemStack(Item.snowball), new ItemStack(ForestryItem.pollen, 1, 1) }, new int[] { 80, 70, 40, 20 });
		// Silky combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 6), new ItemStack(ForestryItem.honeyDrop), new ItemStack(
				ForestryItem.propolis, 1, 3), 80);
		// Parched combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 7), new ItemStack(ForestryItem.beeswax), new ItemStack(
				ForestryItem.honeyDrop), 90);
		// Mysterious combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 8), new ItemStack[] { new ItemStack(ForestryItem.honeyDrop),
				new ItemStack(ForestryItem.propolis, 1, 2) }, new int[] { 40, 100 });
		// Irradiated combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 9), new ItemStack[] {}, new int[] {});
		// Powdery combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 10), new ItemStack[] { new ItemStack(ForestryItem.honeyDrop),
				new ItemStack(ForestryItem.beeswax), new ItemStack(Item.gunpowder) }, new int[] { 20, 20, 90 });
		// Reddened Combs
		RecipeManagers.centrifugeManager.addRecipe(80, new ItemStack(ForestryItem.beeComb, 1, 11),
				new ItemStack[] { new ItemStack(ForestryItem.honeyDrop, 2, 1) }, new int[] { 100 });
		// Darkened Combs
		RecipeManagers.centrifugeManager.addRecipe(80, new ItemStack(ForestryItem.beeComb, 1, 12),
				new ItemStack[] { new ItemStack(ForestryItem.honeyDrop, 1, 1) }, new int[] { 100 });
		// Omega Combs
		RecipeManagers.centrifugeManager.addRecipe(400, new ItemStack(ForestryItem.beeComb, 1, 13), new ItemStack[] { new ItemStack(ForestryItem.honeyDrop, 1,
				2) }, new int[] { 100 });
		// Wheaten Combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 14), new ItemStack[] { new ItemStack(ForestryItem.honeyDrop),
				new ItemStack(ForestryItem.beeswax), new ItemStack(Item.wheat) }, new int[] { 20, 20, 80 });
		// Mossy Combs
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.beeComb, 1, 15), new ItemStack(ForestryItem.beeswax), new ItemStack(
				ForestryItem.honeyDrop), 90);

		// Silk
		RecipeManagers.centrifugeManager.addRecipe(5, new ItemStack(ForestryItem.propolis, 1, 3), new ItemStack[] {
				new ItemStack(ForestryItem.craftingMaterial, 1, 2), new ItemStack(ForestryItem.propolis) }, new int[] { 60, 10 });

		// / FERMENTER
		RecipeManagers.fermenterManager.addRecipe(new ItemStack(ForestryItem.honeydew), 500, 1.0f, new LiquidStack(ForestryItem.liquidMead.itemID, 1),
				new LiquidStack(ForestryItem.liquidHoney.itemID, 1));

	}

	@Override
	public void generateSurface(World world, Random rand, int chunkX, int chunkZ) {

		// / BEEHIVES
		if (Config.generateBeehives) {
			for (int i = 0; i < 3; i++) {
				int randPosX = chunkX + rand.nextInt(16);
				int randPosY = rand.nextInt(50) + 42;
				int randPosZ = chunkZ + rand.nextInt(16);
				if (new WorldGenHiveForest().generate(world, rand, randPosX, randPosY, randPosZ))
					return;
			}
			for (int i = 0; i < 3; i++) {
				int randPosX = chunkX + rand.nextInt(16);
				int randPosY = rand.nextInt(50) + 62;
				int randPosZ = chunkZ + rand.nextInt(16);
				if (new WorldGenHiveJungle().generate(world, rand, randPosX, randPosY, randPosZ))
					return;
			}
			for (int i = 0; i < 1; i++) {
				int randPosX = chunkX + rand.nextInt(16);
				int randPosY = rand.nextInt(50) + 42;
				int randPosZ = chunkZ + rand.nextInt(16);
				if (new WorldGenHiveMeadows().generate(world, rand, randPosX, randPosY, randPosZ))
					return;
			}
			for (int i = 0; i < 2; i++) {
				int randPosX = chunkX + rand.nextInt(16);
				int randPosY = rand.nextInt(50) + 42;
				int randPosZ = chunkZ + rand.nextInt(16);
				if (new WorldGenHiveParched().generate(world, rand, randPosX, randPosY, randPosZ))
					return;
			}
			for (int i = 0; i < 2; i++) {
				int randPosX = chunkX + rand.nextInt(16);
				int randPosY = rand.nextInt(50) + 42;
				int randPosZ = chunkZ + rand.nextInt(16);
				(new WorldGenHiveEnd()).generate(world, rand, randPosX, randPosY, randPosZ);
			}
			for (int i = 0; i < 2; i++) {
				int randPosX = chunkX + rand.nextInt(16);
				int randPosY = rand.nextInt(50) + 42;
				int randPosZ = chunkZ + rand.nextInt(16);
				if (new WorldGenHiveSnow().generate(world, rand, randPosX, randPosY, randPosZ))
					return;
			}
			for (int i = 0; i < 2; i++) {
				int randPosX = chunkX + rand.nextInt(16);
				int randPosY = rand.nextInt(50) + 42;
				int randPosZ = chunkZ + rand.nextInt(16);
				if (new WorldGenHiveSwamp().generate(world, rand, randPosX, randPosY, randPosZ))
					return;
			}
		}

	}

	private void registerBeehiveDrops() {
		forestDrops.add(new HiveDrop(BeeTemplates.getForestTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 0) }, 80));
		forestDrops.add(new HiveDrop(BeeTemplates.getForestRainResistTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 0) }, 8));
		forestDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 0) }, 3));

		meadowsDrops.add(new HiveDrop(BeeTemplates.getMeadowsTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 0) }, 80));
		meadowsDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 0) }, 3));

		desertDrops.add(new HiveDrop(BeeTemplates.getModestTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 7) }, 80));
		desertDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 7) }, 3));

		jungleDrops.add(new HiveDrop(BeeTemplates.getTropicalTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 6) }, 80));
		jungleDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 6) }, 3));

		endDrops.add(new HiveDrop(BeeTemplates.getEnderTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 8) }, 90));

		snowDrops.add(new HiveDrop(BeeTemplates.getWintryTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 4) }, 80));
		snowDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 4) }, 3));

		swampDrops.add(new HiveDrop(BeeTemplates.getMarshyTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 15) }, 80));
		swampDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[] { new ItemStack(ForestryItem.beeComb, 1, 15) }, 3));
	}

	private void registerDungeonLoot() {
		int rarity;
		if (Config.dungeonLootRare) {
			rarity = 5;
		} else {
			rarity = 10;
		}

		IBee bee = new Bee(BeeManager.beeInterface.templateAsGenome(BeeTemplates.getSteadfastTemplate()));
		ItemStack beeItem = new ItemStack(ForestryItem.beeDroneGE);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		bee.writeToNBT(nbttagcompound);
		beeItem.setTagCompound(nbttagcompound);
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(beeItem, 1, 1, rarity));
	}

	private void createHiveDropArrays() {

		BeeManager.hiveDrops = new ArrayList[8];

		forestDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[0] = forestDrops;

		meadowsDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[1] = meadowsDrops;

		desertDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[2] = desertDrops;

		jungleDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[3] = jungleDrops;

		endDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[4] = endDrops;

		snowDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[5] = snowDrops;

		swampDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[6] = swampDrops;

		swarmDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[7] = swarmDrops;
	}

	private void createAlleles() {
		
		IClassification hymnoptera = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "hymnoptera", "Hymnoptera");
		AlleleManager.alleleRegistry.getClassification("class.insecta").addMemberGroup(hymnoptera);
		
		IClassification apidae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "apidae", "Apidae");
		hymnoptera.addMemberGroup(apidae);

		IClassification honey = new BranchBees("honey", "Apis");
		apidae.addMemberGroup(honey);
		IClassification noble = new BranchBees("noble", "Probapis");
		apidae.addMemberGroup(noble);
		IClassification industrious = new BranchBees("industrious", "Industrapis");
		apidae.addMemberGroup(industrious);
		IClassification heroic = new BranchBees("heroic", "Herapis");
		apidae.addMemberGroup(heroic);
		IClassification infernal = new BranchBees("infernal", "Diapis");
		apidae.addMemberGroup(infernal);
		IClassification austere = new BranchBees("austere", "Modapis");
		apidae.addMemberGroup(austere);
		IClassification end = new BranchBees("end", "Finapis");
		apidae.addMemberGroup(end);
		IClassification vengeful = new BranchBees("vengeful", "Punapis");
		apidae.addMemberGroup(vengeful);
		IClassification tropical = new BranchBees("tropical", "Caldapis");
		apidae.addMemberGroup(tropical);
		IClassification frozen = new BranchBees("frozen", "Coagapis");
		apidae.addMemberGroup(frozen);
		IClassification reddened = new BranchBees("reddened", "Rubapis");
		apidae.addMemberGroup(reddened);
		IClassification festive = new BranchBees("festive", "Festapis");
		apidae.addMemberGroup(festive);
		IClassification agrarian = new BranchBees("agrarian", "Rustapis");
		apidae.addMemberGroup(agrarian);
		IClassification boggy = new BranchBees("boggy", "Paludapis");
		apidae.addMemberGroup(boggy);
		IClassification monastic = new BranchBees("monastic", "Monapis");
		apidae.addMemberGroup(monastic);

		// / BEES // SPECIES
		// Common Branch
		Allele.speciesForest = new AlleleBeeSpecies("speciesForest", true, "bees.species.forest", honey, "nigrocincta", 0x19d0ec, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 0), 30);
		Allele.speciesMeadows = new AlleleBeeSpecies("speciesMeadows", true, "bees.species.meadows", honey, "florea", 0xef131e, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 0), 30);
		Allele.speciesCommon = new AlleleBeeSpecies("speciesCommon", true, "bees.species.common", honey, "cerana", 0xb2b2b2, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 0), 35).setIsSecret();
		Allele.speciesCultivated = new AlleleBeeSpecies("speciesCultivated", true, "bees.species.cultivated", honey, "mellifera", 0x5734ec, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 0), 40).setIsSecret();

		// Noble Branch
		Allele.speciesNoble = new AlleleBeeSpecies("speciesNoble", false, "bees.species.noble", noble, "nobilis", 0xec9a19, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 5), 20).setIsSecret();
		Allele.speciesMajestic = new AlleleBeeSpecies("speciesMajestic", true, "bees.species.majestic", noble, "regalis", 0x7f0000, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 5), 30).setIsSecret();
		Allele.speciesImperial = new AlleleBeeSpecies("speciesImperial", false, "bees.species.imperial", noble, "imperatorius", 0xa3e02f, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 5), 20).addProduct(new ItemStack(ForestryItem.royalJelly), 15).setHasEffect().setIsSecret();

		// Industrious Branch
		Allele.speciesDiligent = new AlleleBeeSpecies("speciesDiligent", false, "bees.species.diligent", industrious, "sedulus", 0xc219ec, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 3), 20).setIsSecret();
		Allele.speciesUnweary = new AlleleBeeSpecies("speciesUnweary", true, "bees.species.unweary", industrious, "assiduus", 0x19ec5a, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 3), 30).setIsSecret();
		Allele.speciesIndustrious = new AlleleBeeSpecies("speciesIndustrious", false, "bees.species.industrious", industrious, "industria", 0xffffff, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 3), 20).addProduct(new ItemStack(ForestryItem.pollen), 15).setHasEffect().setIsSecret();

		// Heroic Branch
		Allele.speciesSteadfast = new AlleleBeeSpecies("speciesSteadfast", false, "bees.species.steadfast", heroic, "legio", 0x4d2b15, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 1), 20).setIsSecret().setHasEffect();
		Allele.speciesValiant = new AlleleBeeSpecies("speciesValiant", true, "bees.species.valiant", heroic, "centurio", 0x626bdd, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 1), 30).addSpecialty(new ItemStack(Item.sugar), 15).setIsSecret();
		Allele.speciesHeroic = new AlleleBeeSpecies("speciesHeroic", false, "bees.species.heroic", heroic, "kraphti", 0xb3d5e4, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 1), 40).setIsSecret().setHasEffect();

		// Infernal Branch
		Allele.speciesSinister = new AlleleBeeSpecies("speciesSinister", false, "bees.species.sinister", infernal, "caecus", 0xb3d5e4, 0x9a2323)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 2), 45).setIsSecret().setTemperature(EnumTemperature.HELLISH).setHumidity(EnumHumidity.ARID);
		Allele.speciesFiendish = new AlleleBeeSpecies("speciesFiendish", true, "bees.species.fiendish", infernal, "diabolus", 0xd7bee5, 0x9a2323)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 2), 55).addProduct(new ItemStack(ForestryItem.ash), 15).setIsSecret()
				.setTemperature(EnumTemperature.HELLISH).setHumidity(EnumHumidity.ARID);
		Allele.speciesDemonic = new AlleleBeeSpecies("speciesDemonic", false, "bees.species.demonic", infernal, "draco", 0xf4e400, 0x9a2323)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 2), 45).addProduct(new ItemStack(Item.lightStoneDust), 15).setHasEffect().setIsSecret()
				.setTemperature(EnumTemperature.HELLISH).setHumidity(EnumHumidity.ARID);

		// Austere Branch
		Allele.speciesModest = new AlleleBeeSpecies("speciesModest", false, "bees.species.modest", austere, "modicus", 0xc5be86, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 7), 20).setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.ARID);
		Allele.speciesFrugal = new AlleleBeeSpecies("speciesFrugal", true, "bees.species.frugal", austere, "permodestus", 0xe8dcb1, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 7), 30).setIsSecret().setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.ARID);
		Allele.speciesAustere = new AlleleBeeSpecies("speciesAustere", false, "bees.species.austere", austere, "correpere", 0xfffac2, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 7), 20).addSpecialty(new ItemStack(ForestryItem.beeComb, 1, 10), 50).setHasEffect()
				.setIsSecret().setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.ARID);

		// / Tropical Branch
		Allele.speciesTropical = new AlleleBeeSpecies("speciesTropical", false, "bees.species.tropical", tropical, "mendelia", 0x378020, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 6), 20).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		Allele.speciesExotic = new AlleleBeeSpecies("speciesExotic", true, "bees.species.exotic", tropical, "darwini", 0x304903, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 6), 30).setIsSecret().setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		Allele.speciesEdenic = new AlleleBeeSpecies("speciesEdenic", false, "bees.species.edenic", tropical, "humboldti", 0x393d0d, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 6), 20).setHasEffect().setIsSecret().setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);

		// End Branch
		Allele.speciesEnded = new AlleleBeeSpecies("speciesEnded", false, "bees.species.ender", end, "notchi", 0xed8bda, 0x31023a)
		.addProduct(new ItemStack(ForestryItem.beeComb, 1, 8), 30).setIsSecret().setTemperature(EnumTemperature.COLD);
		Allele.speciesSpectral = new AlleleBeeSpecies("speciesSpectral", true, "bees.species.spectral", end, "idolum", 0xa98bed, 0x31023a)
		.addProduct(new ItemStack(ForestryItem.beeComb, 1, 8), 50).setIsSecret().setTemperature(EnumTemperature.COLD);
		Allele.speciesPhantasmal = new AlleleBeeSpecies("speciesPhantasmal", false, "bees.species.phantasmal", end, "lemur", 0x8bc3ed, 0x31023a)
		.addProduct(new ItemStack(ForestryItem.beeComb, 1, 8), 40).setIsSecret().setHasEffect().setTemperature(EnumTemperature.COLD);

		// Frozen Branch
		Allele.speciesWintry = new AlleleBeeSpecies("speciesWintry", false, "bees.species.wintry", frozen, "brumalis", 0xa0ffc8, 0xdaf5f3).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 4), 30).setTemperature(EnumTemperature.ICY);
		Allele.speciesIcy = new AlleleBeeSpecies("speciesIcy", true, "bees.species.icy", frozen, "coagulis", 0xa0ffff, 0xdaf5f3)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 4), 20).addProduct(new ItemStack(ForestryItem.craftingMaterial, 1, 5), 20)
				.setTemperature(EnumTemperature.ICY).setIsSecret();
		Allele.speciesGlacial = new AlleleBeeSpecies("speciesGlacial", false, "bees.species.glacial", frozen, "glacialis", 0xefffff, 0xdaf5f3)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 4), 20).addProduct(new ItemStack(ForestryItem.craftingMaterial, 1, 5), 40)
				.setTemperature(EnumTemperature.ICY).setHasEffect().setIsSecret();

		// Vengeful Branch
		Allele.speciesVindictive = new AlleleBeeSpecies("speciesVindictive", false, "bees.species.vindictive", vengeful, "ultio", 0xeafff3, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 9), 25).setIsSecret().setIsNotCounted();
		Allele.speciesVengeful = new AlleleBeeSpecies("speciesVengeful", false, "bees.species.vengeful", vengeful, "punire", 0xc2de00, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 9), 40).setIsSecret().setIsNotCounted();
		Allele.speciesAvenging = new AlleleBeeSpecies("speciesAvenging", false, "bees.species.avenging", vengeful, "hostimentum", 0xddff00, 0xffdc16)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 9), 40).setIsSecret().setHasEffect().setIsNotCounted();

		// Reddened Branch (EE)
		Allele.speciesDarkened = new AlleleBeeSpecies("speciesDarkened", false, "bees.species.darkened", reddened, "pahimas", 0xd7bee5, 0x260f29)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 0), 100).addSpecialty(new ItemStack(ForestryItem.beeComb, 1, 12), 100)
				.setJubilanceProvider(new JubilanceNone()).setIsSecret().setIsNotCounted();
		AlleleManager.alleleRegistry.blacklistAllele(Allele.speciesDarkened.getUID());
		Allele.speciesReddened = new AlleleBeeSpecies("speciesReddened", false, "bees.species.reddened", reddened, "xenophos", 0xf8c1c1, 0x260f29)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 0), 100).addSpecialty(new ItemStack(ForestryItem.beeComb, 1, 11), 100)
				.setJubilanceProvider(new JubilanceNone()).setIsSecret().setIsNotCounted();
		AlleleManager.alleleRegistry.blacklistAllele(Allele.speciesReddened.getUID());
		Allele.speciesOmega = new AlleleBeeSpecies("speciesOmega", false, "bees.species.omega", reddened, "slopokis", 0xfeff8f, 0x260f29)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 0), 100).addSpecialty(new ItemStack(ForestryItem.beeComb, 1, 13), 100)
				.setJubilanceProvider(new JubilanceNone()).setIsSecret().setIsNotCounted();
		AlleleManager.alleleRegistry.blacklistAllele(Allele.speciesOmega.getUID());

		// Festive branch
		Allele.speciesLeporine = new AlleleBeeSpecies("speciesLeporine", false, "bees.species.leporine", festive, "lepus", 0xfeff8f, 0x3cd757)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 6), 30).addProduct(new ItemStack(Item.egg), 10).setIsSecret().setIsNotCounted()
				.setHasEffect();
		Allele.speciesMerry = new AlleleBeeSpecies("speciesMerry", false, "bees.species.merry", festive, "feliciter", 0xffffff, 0xd40000)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 4), 30).addProduct(new ItemStack(ForestryItem.craftingMaterial, 1, 5), 20)
				.setTemperature(EnumTemperature.ICY).setIsSecret().setIsNotCounted().setHasEffect().setBodyType(2);
		Allele.speciesTipsy = new AlleleBeeSpecies("speciesTipsy", false, "bees.species.tipsy", festive, "ebrius", 0xffffff, 0xc219ec)
				.addProduct(new ItemStack(ForestryItem.beeComb, 1, 4), 30).addProduct(new ItemStack(ForestryItem.craftingMaterial, 1, 5), 20)
				.setTemperature(EnumTemperature.ICY).setIsSecret().setIsNotCounted().setHasEffect();
		// 35 Solstice
		// 36 Halloween
		// 37 Thanksgiving

		// Agrarian branch
		Allele.speciesRural = new AlleleBeeSpecies("speciesRural", false, "bees.species.rural", agrarian, "rustico", 0xfeff8f, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 14), 20).setIsSecret();
		// 41 Farmerly
		// 42 Agrarian

		// Boggy branch
		Allele.speciesMarshy = new AlleleBeeSpecies("speciesMarshy", true, "bees.species.marshy", boggy, "adorasti", 0x546626, 0xffdc16).addProduct(
				new ItemStack(ForestryItem.beeComb, 1, 15), 30).setHumidity(EnumHumidity.DAMP);
		// 44 speciesMiry
		// 45 speciesBoggy

		// Monastic branch
		Allele.speciesMonastic = new AlleleBeeSpecies("speciesMonastic", false, "bees.species.monastic", monastic, "monachus", 0x42371c, 0xfff7b6)
		.addProduct(new ItemStack(ForestryItem.beeComb, 1, 14), 30).addSpecialty(new ItemStack(ForestryItem.beeComb, 1, 16), 10)
		.setJubilanceProvider(new JubilanceProviderHermit()).setIsSecret();
		Allele.speciesSecluded = new AlleleBeeSpecies("speciesSecluded", true, "bees.species.secluded", monastic, "contractus", 0x7b6634, 0xfff7b6)
		.addSpecialty(new ItemStack(ForestryItem.beeComb, 1, 16), 20).setJubilanceProvider(new JubilanceProviderHermit()).setIsSecret();
		Allele.speciesHermitic = new AlleleBeeSpecies("speciesHermitic", false, "bees.species.hermitic", monastic, "anachoreta", 0xffd46c, 0xfff7b6)
		.addSpecialty(new ItemStack(ForestryItem.beeComb, 1, 16), 20).setJubilanceProvider(new JubilanceProviderHermit()).setHasEffect().setIsSecret();
		
		// / BEES // FLOWER PROVIDERS 1500 - 1599
		Allele.flowersVanilla = new AlleleFlowers("flowersVanilla", new FlowerProviderVanilla(), true);
		Allele.flowersNether = new AlleleFlowers("flowersNether", new FlowerProviderNetherwart());
		Allele.flowersCacti = new AlleleFlowers("flowersCacti", new FlowerProviderCacti());
		Allele.flowersMushrooms = new AlleleFlowers("flowersMushrooms", new FlowerProviderMushroom());
		Allele.flowersEnd = new AlleleFlowers("flowersEnd", new FlowerProviderEnd());
		Allele.flowersJungle = new AlleleFlowers("flowersJungle", new FlowerProviderJungle());
		Allele.flowersSnow = new AlleleFlowers("flowersSnow", new FlowerProviderVanilla(), true);
		Allele.flowersWheat = new AlleleFlowers("flowersWheat", new FlowerProviderWheat(), true);

		// / BEES // EFFECTS 1800 - 1899
		Allele.effectNone = new AlleleEffectNone("effectNone");
		Allele.effectAggressive = new AlleleEffectAggressive("effectAggressive");
		Allele.effectHeroic = new AlleleEffectHeroic("effectHeroic");
		Allele.effectBeatific = new AlleleEffectPotion("effectBeatific", "beatific", false, Potion.regeneration, 100);
		Allele.effectMiasmic = new AlleleEffectMiasmic("effectMiasmic");
		Allele.effectMisanthrope = new AlleleEffectMisanthrope("effectMisanthrope");
		Allele.effectGlacial = new AlleleEffectGlacial("effectGlacial");
		Allele.effectRadioactive = new AlleleEffectRadioactive("effectRadioactive");
		Allele.effectCreeper = new AlleleEffectCreeper("effectCreeper");
		Allele.effectIgnition = new AlleleEffectIgnition("effectIgnition");
		Allele.effectExploration = new AlleleEffectExploration("effectExploration");
		Allele.effectFestiveEaster = new AlleleEffectNone("effectFestiveEaster");
		Allele.effectSnowing = new AlleleEffectSnowing("effectSnowing");
		Allele.effectDrunkard = new AlleleEffectPotion("effectDrunkard", "drunkard", false, Potion.confusion, 100);
		Allele.effectResurrection = new AlleleEffectResurrection("effectResurrection");

	}

	private void createMutations() {
		// / MUTATIONS
		BeeTemplates.commonA = new Mutation(Allele.speciesForest, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonB = new Mutation(Allele.speciesModest, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonC = new Mutation(Allele.speciesModest, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonD = new Mutation(Allele.speciesWintry, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonE = new Mutation(Allele.speciesWintry, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonF = new Mutation(Allele.speciesWintry, Allele.speciesModest, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonG = new Mutation(Allele.speciesTropical, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonH = new Mutation(Allele.speciesTropical, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonI = new Mutation(Allele.speciesTropical, Allele.speciesModest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonJ = new Mutation(Allele.speciesTropical, Allele.speciesWintry, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonK = new Mutation(Allele.speciesMarshy, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonL = new Mutation(Allele.speciesMarshy, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonM = new Mutation(Allele.speciesMarshy, Allele.speciesModest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonN = new Mutation(Allele.speciesMarshy, Allele.speciesWintry, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonO = new Mutation(Allele.speciesMarshy, Allele.speciesTropical, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.cultivatedA = new Mutation(Allele.speciesCommon, Allele.speciesForest, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedB = new Mutation(Allele.speciesCommon, Allele.speciesMeadows, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedC = new Mutation(Allele.speciesCommon, Allele.speciesModest, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedD = new Mutation(Allele.speciesCommon, Allele.speciesWintry, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedE = new Mutation(Allele.speciesCommon, Allele.speciesTropical, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedF = new Mutation(Allele.speciesCommon, Allele.speciesMarshy, BeeTemplates.getCultivatedTemplate(), 12);

		BeeTemplates.nobleA = new Mutation(Allele.speciesCommon, Allele.speciesCultivated, BeeTemplates.getNobleTemplate(), 10);
		BeeTemplates.majesticA = new Mutation(Allele.speciesNoble, Allele.speciesCultivated, BeeTemplates.getMajesticTemplate(), 8);
		BeeTemplates.imperialA = new Mutation(Allele.speciesNoble, Allele.speciesMajestic, BeeTemplates.getImperialTemplate(), 8);

		BeeTemplates.diligentA = new Mutation(Allele.speciesCommon, Allele.speciesCultivated, BeeTemplates.getDiligentTemplate(), 10);
		BeeTemplates.unwearyA = new Mutation(Allele.speciesDiligent, Allele.speciesCultivated, BeeTemplates.getUnwearyTemplate(), 8);
		BeeTemplates.industriousA = new Mutation(Allele.speciesDiligent, Allele.speciesUnweary, BeeTemplates.getIndustriousTemplate(), 8);

		BeeTemplates.heroicA = new Mutation(Allele.speciesSteadfast, Allele.speciesValiant, BeeTemplates.getHeroicTemplate(), 6).restrictBiome(
				BiomeGenBase.forest.biomeID).restrictBiome(BiomeGenBase.forestHills.biomeID);

		BeeTemplates.sinisterA = new Mutation(Allele.speciesModest, Allele.speciesCultivated, BeeTemplates.getSinisterTemplate(), 60)
				.restrictBiome(BiomeGenBase.hell.biomeID);
		BeeTemplates.sinisterB = new Mutation(Allele.speciesTropical, Allele.speciesCultivated, BeeTemplates.getSinisterTemplate(), 60)
				.restrictBiome(BiomeGenBase.hell.biomeID);
		BeeTemplates.fiendishA = new Mutation(Allele.speciesSinister, Allele.speciesCultivated, BeeTemplates.getFiendishTemplate(), 40)
				.restrictBiome(BiomeGenBase.hell.biomeID);
		BeeTemplates.fiendishB = new Mutation(Allele.speciesSinister, Allele.speciesModest, BeeTemplates.getFiendishTemplate(), 40)
				.restrictBiome(BiomeGenBase.hell.biomeID);
		BeeTemplates.fiendishC = new Mutation(Allele.speciesSinister, Allele.speciesTropical, BeeTemplates.getFiendishTemplate(), 40)
				.restrictBiome(BiomeGenBase.hell.biomeID);
		BeeTemplates.demonicA = new Mutation(Allele.speciesSinister, Allele.speciesFiendish, BeeTemplates.getDemonicTemplate(), 25)
				.restrictBiome(BiomeGenBase.hell.biomeID);

		// Austere branch
		BeeTemplates.frugalA = new Mutation(Allele.speciesModest, Allele.speciesSinister, BeeTemplates.getFrugalTemplate(), 16).setTemperatureRainfall(1.9f,
				2.0f, 0.0f, 0.1f);
		BeeTemplates.frugalB = new Mutation(Allele.speciesModest, Allele.speciesFiendish, BeeTemplates.getFrugalTemplate(), 10).setTemperatureRainfall(1.9f,
				2.0f, 0.0f, 0.1f);
		BeeTemplates.austereA = new Mutation(Allele.speciesModest, Allele.speciesFrugal, BeeTemplates.getAustereTemplate(), 8).setTemperatureRainfall(1.9f,
				2.0f, 0.0f, 0.1f);

		// Tropical branch
		BeeTemplates.exoticA = new Mutation(Allele.speciesAustere, Allele.speciesTropical, BeeTemplates.getExoticTemplate(), 12);
		BeeTemplates.edenicA = new Mutation(Allele.speciesExotic, Allele.speciesTropical, BeeTemplates.getEdenicTemplate(), 8);

		// Wintry branch
		BeeTemplates.icyA = new Mutation(Allele.speciesIndustrious, Allele.speciesWintry, BeeTemplates.getIcyTemplate(), 12);
		BeeTemplates.glacialA = new Mutation(Allele.speciesIcy, Allele.speciesWintry, BeeTemplates.getGlacialTemplate(), 8);

		// Festive branch
		BeeTemplates.leporineA = new MutationTimeLimited(Allele.speciesMeadows, Allele.speciesForest, BeeTemplates.getLeporineTemplate(), 10,
				new MutationTimeLimited.DayMonth(6, 4), new MutationTimeLimited.DayMonth(15, 4)).setIsSecret();
		BeeTemplates.merryA = new MutationTimeLimited(Allele.speciesWintry, Allele.speciesForest, BeeTemplates.getMerryTemplate(), 10,
				new MutationTimeLimited.DayMonth(21, 12), new MutationTimeLimited.DayMonth(27, 12)).setIsSecret();
		BeeTemplates.tipsyA = new MutationTimeLimited(Allele.speciesWintry, Allele.speciesMeadows, BeeTemplates.getTipsyTemplate(), 10,
				new MutationTimeLimited.DayMonth(27, 12), new MutationTimeLimited.DayMonth(2, 1)).setIsSecret();

		// Agrarian branch
		BeeTemplates.ruralA = new Mutation(Allele.speciesMeadows, Allele.speciesDiligent, BeeTemplates.getRuralTemplate(), 12)
				.restrictBiome(BiomeGenBase.plains.biomeID);

		// Monastic branch
		BeeTemplates.secludedA = new Mutation(Allele.speciesMonastic, Allele.speciesAustere, BeeTemplates.getSecludedTemplate(), 12);
		BeeTemplates.hermiticA = new Mutation(Allele.speciesMonastic, Allele.speciesSecluded, BeeTemplates.getSecludedTemplate(), 8);
		
		// End branch
		BeeTemplates.spectralA = new Mutation(Allele.speciesHermitic, Allele.speciesEnded, BeeTemplates.getSpectralTemplate(), 4);
		BeeTemplates.phantasmalA = new Mutation(Allele.speciesSpectral, Allele.speciesEnded, BeeTemplates.getPhantasmalTemplate(), 2);
		
		BeeTemplates.vengefulA = new Mutation(Allele.speciesCommon, Allele.speciesVindictive, BeeTemplates.getVengefulTemplate(), 8).setIsSecret();
		BeeTemplates.avengingA = new Mutation(Allele.speciesVengeful, Allele.speciesVindictive, BeeTemplates.getAvengingTemplate(), 4);

		if(BeeTemplates.hasGoodBreeder()) {
			AlleleManager.alleleRegistry.blacklistAllele("forestry.speciesVindictive");
			AlleleManager.alleleRegistry.blacklistAllele("forestry.speciesVengeful");
			AlleleManager.alleleRegistry.blacklistAllele("forestry.speciesAvenging");
		}
		
		BeeTemplates.vindictiveA = new Mutation(Allele.speciesCommon, Allele.speciesForest, BeeTemplates.getVindictiveTemplate(), 99).setIsSecret();
		BeeTemplates.vindictiveB = new Mutation(Allele.speciesCommon, Allele.speciesMeadows, BeeTemplates.getVindictiveTemplate(), 99).setIsSecret();
		BeeTemplates.vindictiveC = new Mutation(Allele.speciesCommon, Allele.speciesModest, BeeTemplates.getVindictiveTemplate(), 99).setIsSecret();
		BeeTemplates.vindictiveD = new Mutation(Allele.speciesCommon, Allele.speciesWintry, BeeTemplates.getVindictiveTemplate(), 99).setIsSecret();
		BeeTemplates.vindictiveE = new Mutation(Allele.speciesCommon, Allele.speciesTropical, BeeTemplates.getVindictiveTemplate(), 99).setIsSecret();
	}

	private void registerTemplates() {
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getForestTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getMeadowsTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getCommonTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getCultivatedTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getNobleTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getMajesticTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getImperialTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getDiligentTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getUnwearyTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getIndustriousTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getSteadfastTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getValiantTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getHeroicTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getSinisterTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getFiendishTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getDemonicTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getModestTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getFrugalTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getAustereTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getTropicalTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getExoticTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getEdenicTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getEnderTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getWintryTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getIcyTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getGlacialTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getVindictiveTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getVengefulTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getAvengingTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getDarkenedTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getReddenedTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getOmegaTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getRuralTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getLeporineTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getMerryTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getTipsyTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getMarshyTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getMonasticTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getSecludedTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getHermiticTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getSpectralTemplate());
		BeeManager.breedingManager.registerBeeTemplate(BeeTemplates.getPhantasmalTemplate());
	}

	private void parseAdditionalFlowers(String list, ArrayList<ItemStack> target) {
		String[] items = list.split("[;]+");

		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			String[] ident = item.split("[:]+");
			int id = 0;
			int meta = 0;
			if (ident.length > 1) {
				id = Integer.parseInt(ident[0]);
				meta = Integer.parseInt(ident[1]);
			} else {
				id = Integer.parseInt(ident[0]);
			}

			if (id > 0) {
				if ((id < Block.blocksList.length && Block.blocksList[id] != null) || Item.itemsList[id] != null) {
					Proxies.log.finer("Adding flower of (" + id + ":" + meta + ") to vanilla flower provider.");
					target.add(new ItemStack(id, 1, meta));
				} else {
					Proxies.log.warning("Failed to add flower of (" + id + ":" + meta + ") to vanilla flower provider since it was null.");
				}
			}
		}
	}

	private void parseBeeBlacklist(String list) {
		String[] items = list.split("[;]+");

		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			FMLCommonHandler.instance().getFMLLogger().finer("Blacklisting bee species identified by " + item);
			AlleleManager.alleleRegistry.blacklistAllele(item);
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[] { new CommandBeekeepingMode(), new CommandGiveBee(EnumBeeType.DRONE), new CommandGiveBee(EnumBeeType.PRINCESS),
				new CommandGiveBee(EnumBeeType.QUEEN), };
	}
}
