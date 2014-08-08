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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.circuits.ChipsetManager;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.core.CommandForestry;
import forestry.core.CreativeTabForestry;
import forestry.core.GameMode;
import forestry.core.SaveEventHandlerCore;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.circuits.ItemSolderingIron;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineAnalyzer;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.genetics.Allele;
import forestry.core.genetics.AlleleRegistry;
import forestry.core.items.ItemAssemblyKit;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemForestryPickaxe;
import forestry.core.items.ItemForestryShovel;
import forestry.core.items.ItemFruit;
import forestry.core.items.ItemLiquidContainer;
import forestry.core.items.ItemLiquids;
import forestry.core.items.ItemMisc;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.items.ItemPipette;
import forestry.core.items.ItemWrench;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ShapedRecipeCustom;

@PluginInfo(pluginID = "Core", name = "Core", author = "SirSengir", url = Defaults.URL, description = "Core mechanics for Forestry. Required by all other plugins.")
public class PluginForestryCore extends NativePlugin implements IFuelHandler {

	public static MachineDefinition definitionAnalyzer;
	
	@Override
	public void preInit() {
		super.preInit();

		int blockid = Config.getOrCreateBlockIdProperty("core", Defaults.ID_BLOCK_CORE);
		
		definitionAnalyzer = new MachineDefinition(blockid, Defaults.DEFINITION_ANALYZER_META, "forestry.Analyzer", MachineAnalyzer.class,
				PluginForestryApiculture.proxy.getRendererAnalyzer(Defaults.TEXTURE_PATH_BLOCKS + "/analyzer_"),
				getAnalyzerRecipes(blockid, Defaults.DEFINITION_ANALYZER_META));
		
		ForestryBlock.core = new BlockBase(blockid,
				Material.iron, new MachineDefinition[] { definitionAnalyzer }, true).setBlockName("for.core");
		Item.itemsList[ForestryBlock.core.blockID] = null;
		Item.itemsList[ForestryBlock.core.blockID] = new ItemForestryBlock(ForestryBlock.core.blockID - 256, "for.core");

		ChipsetManager.solderManager = new ItemSolderingIron.SolderManager();
		
		CircuitRegistry circuitRegistry = new CircuitRegistry();
		ChipsetManager.circuitRegistry = circuitRegistry;
		circuitRegistry.initialize();
		
		AlleleRegistry alleleRegistry = new AlleleRegistry();
		AlleleManager.alleleRegistry = alleleRegistry;
		alleleRegistry.initialize();
		
		Allele.initialize();

		GameRegistry.registerFuelHandler(this);
		
	}

	public void doInit() {
		super.doInit();
		definitionAnalyzer.register();
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Core";
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return null;
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	protected void registerPackages() {
	}

	@Override
	protected void registerItems() {

		// / FERTILIZERS
		ForestryItem.fertilizerBio = (new ItemForestry(Config.getOrCreateItemIdProperty("fertilizerBio", Defaults.ID_ITEM_FERTILIZER_BIO)))
				.setItemName("fertilizerBio").setIconIndex(19);
		ForestryItem.fertilizerCompound = (new ItemForestry(Config.getOrCreateItemIdProperty("fertilizerCompound", Defaults.ID_ITEM_FERTILIZER_COMPOUND)))
				.setItemName("fertilizerCompound").setIconIndex(1);

		// / GEMS
		ForestryItem.apatite = (new ItemForestry(Config.getOrCreateItemIdProperty("apatite", Defaults.ID_ITEM_APATITE))).setItemName("apatite").setIconIndex(0);
		OreDictionary.registerOre("gemApatite", new ItemStack(ForestryItem.apatite));

		// / INGOTS
		Item copper = (new ItemForestry(Config.getOrCreateItemIdProperty("ingotCopper", Defaults.ID_ITEM_COPPER))).setItemName("ingotCopper").setIconIndex(3);
		ForestryItem.ingotCopper = new ItemStack(copper);
		FurnaceRecipes.smelting().addSmelting(ForestryBlock.resources.blockID, 1, ForestryItem.ingotCopper, 0.5f);

		Item tin = (new ItemForestry(Config.getOrCreateItemIdProperty("ingotTin", Defaults.ID_ITEM_TIN))).setItemName("ingotTin").setIconIndex(2);
		ForestryItem.ingotTin = new ItemStack(tin);
		FurnaceRecipes.smelting().addSmelting(ForestryBlock.resources.blockID, 2, ForestryItem.ingotTin, 0.5f);

		Item bronze = (new ItemForestry(Config.getOrCreateItemIdProperty("ingotBronze", Defaults.ID_ITEM_BRONZE))).setItemName("ingotBronze").setIconIndex(4);
		ForestryItem.ingotBronze = new ItemStack(bronze);

		OreDictionary.registerOre("ingotCopper", ForestryItem.ingotCopper);
		OreDictionary.registerOre("ingotTin", ForestryItem.ingotTin);
		OreDictionary.registerOre("ingotBronze", ForestryItem.ingotBronze);

		// / TOOLS
		ForestryItem.wrench = (new ItemWrench(Config.getOrCreateItemIdProperty("wrench", Defaults.ID_ITEM_WRENCH))).setItemName("wrench").setIconIndex(6);
		ForestryItem.pipette = new ItemPipette(Config.getOrCreateItemIdProperty("pipette", Defaults.ID_ITEM_PIPETTE)).setItemName("pipette").setIconIndex(20)
				.setFull3D();

		// / MACHINES
		ForestryItem.sturdyCasing = (new ItemForestry(Config.getOrCreateItemIdProperty("sturdyMachine", Defaults.ID_ITEM_STURDY_CASING)))
				.setItemName("sturdyMachine").setIconIndex(9);
		ForestryItem.hardenedCasing = (new ItemForestry(Config.getOrCreateItemIdProperty("hardenedMachine", Defaults.ID_ITEM_HARDENED_MACHINE)))
				.setItemName("hardenedMachine").setIconIndex(39);
		ForestryItem.impregnatedCasing = (new ItemForestry(Config.getOrCreateItemIdProperty("impregnatedCasing", Defaults.ID_ITEM_IMPREGNATED_CASING)))
				.setItemName("impregnatedCasing").setIconIndex(61);

		ForestryItem.craftingMaterial = new ItemMisc(Config.getOrCreateItemIdProperty("craftingMaterial", Defaults.ID_ITEM_CRAFTING))
				.setItemName("craftingMaterial");

		// / DISCONTINUED
		// ForestryItem.vialEmpty = (new
		// ItemForestry(Config.getOrCreateIntProperty("vialEmpty",
		// Config.CATEGORY_ITEM, Defaults.ID_ITEM_VIAL_EMPTY)))
		// .setItemName("vialEmpty").setIconIndex(10);
		ForestryItem.vialCatalyst = (new ItemForestry(Config.getOrCreateItemIdProperty("vialCatalyst", Defaults.ID_ITEM_VIAL_CATALYST))).setItemName(
				"vialCatalyst").setIconIndex(58);

		// / PEAT PRODUCTION
		ForestryItem.peat = (new ItemForestry(Config.getOrCreateItemIdProperty("peat", Defaults.ID_ITEM_PEAT))).setItemName("peat").setIconIndex(16);
		OreDictionary.registerOre("brickPeat", new ItemStack(ForestryItem.peat));

		ForestryItem.ash = (new ItemForestry(Config.getOrCreateItemIdProperty("ash", Defaults.ID_ITEM_ASH))).setItemName("ash").setIconIndex(17);
		OreDictionary.registerOre("dustAsh", new ItemStack(ForestryItem.ash));

		Proxies.common.addSmelting(new ItemStack(ForestryItem.peat), new ItemStack(ForestryItem.ash));
		ForestryItem.bituminousPeat = new ItemForestry(Config.getOrCreateItemIdProperty("bituminousPeat", Defaults.ID_ITEM_BITUMINOUS_PEAT))
				.setItemName("bituminousPeat").setIconIndex(59);

		// / GEARS
		ForestryItem.gearBronze = (new ItemForestry(Config.getOrCreateItemIdProperty("gearBronze", Defaults.ID_ITEM_BRONZE_GEAR))).setItemName("gearBronze")
				.setIconIndex(7);
		OreDictionary.registerOre("gearBronze", new ItemStack(ForestryItem.gearBronze));
		ForestryItem.gearCopper = (new ItemForestry(Config.getOrCreateItemIdProperty("gearCopper", Defaults.ID_ITEM_COPPER_GEAR))).setItemName("gearCopper")
				.setIconIndex(18);
		OreDictionary.registerOre("gearCopper", new ItemStack(ForestryItem.gearCopper));
		ForestryItem.gearTin = (new ItemForestry(Config.getOrCreateItemIdProperty("gearTin", Defaults.ID_ITEM_TIN_GEAR))).setItemName("gearTin")
				.setIconIndex(38);
		OreDictionary.registerOre("gearTin", new ItemStack(ForestryItem.gearTin));

		// / CIRCUIT BOARDS
		ForestryItem.circuitboards = new ItemCircuitBoard(Config.getOrCreateItemIdProperty("chipsets", Defaults.ID_ITEM_CHIPSETS)).setItemName("chipsets");
		ForestryItem.solderingIron = new ItemSolderingIron(Config.getOrCreateItemIdProperty("solderingIron", Defaults.ID_ITEM_SOLDERING_IRON)).setIconIndex(11)
				.setItemName("solderingIron");
		ForestryItem.tubes = new ItemOverlay(Config.getOrCreateItemIdProperty("thermionicTubes", Defaults.ID_ITEM_THERMIONIC_TUBES), CreativeTabForestry.tabForestry,
				new OverlayInfo[] { new OverlayInfo("ex-0", 0xffffff, 0xe3b78e), new OverlayInfo("ex-1", 0xffffff, 0xe1eef4),
						new OverlayInfo("ex-2", 0xffffff, 0xddc276), new OverlayInfo("ex-3", 0xffffff, 0xd8d8d8), new OverlayInfo("ex-4", 0xffffff, 0xffff8b),
						new OverlayInfo("ex-5", 0xffffff, 0x7bd1b8), new OverlayInfo("ex-6", 0xffffff, 0x866bc0), new OverlayInfo("ex-7", 0xfff87e, 0xd96600),
						new OverlayInfo("ex-8", 0xffffff, 0x444444), new OverlayInfo("ex-9", 0xffffff, 0xbfffdd), new OverlayInfo("ex-10", 0xffffff, 0x68ccee),
						new OverlayInfo("ex-11", 0xffffff, 0x1c57c6)}).setIcons(41, 42).setItemName("thermionicTubes");

		// / CRATES AND CARTONS
		ForestryItem.carton = (new ItemForestry(Config.getOrCreateItemIdProperty("carton", Defaults.ID_ITEM_CARTON))).setItemName("carton").setIconIndex(24);
		ForestryItem.crate = (new ItemForestry(Config.getOrCreateItemIdProperty("crate", Defaults.ID_ITEM_CRATE))).setItemName("crate").setIconIndex(32);

		// / CRAFTING CARPENTER
		ForestryItem.stickImpregnated = (new ItemForestry(Config.getOrCreateItemIdProperty("oakStick", Defaults.ID_ITEM_OAKSTICK))).setItemName("oakStick")
				.setIconIndex(25);
		ForestryItem.woodPulp = (new ItemForestry(Config.getOrCreateItemIdProperty("woodPulp", Defaults.ID_ITEM_WOODPULP))).setItemName("woodPulp")
				.setIconIndex(33);
		OreDictionary.registerOre("pulpWood", new ItemStack(ForestryItem.woodPulp));

		// / RECLAMATION
		ForestryItem.brokenBronzePickaxe = (new ItemForestry(Config.getOrCreateItemIdProperty("brokenBronzePickaxe", Defaults.ID_ITEM_PICKAXE_BRONZE_BROKEN)))
				.setItemName("brokenBronzePickaxe").setIconIndex(28);
		ForestryItem.brokenBronzeShovel = (new ItemForestry(Config.getOrCreateItemIdProperty("brokenBronzeShovel", Defaults.ID_ITEM_SHOVEL_BRONZE_BROKEN)))
				.setItemName("brokenBronzeShovel").setIconIndex(31);

		// / TOOLS
		ForestryItem.bronzePickaxe = (new ItemForestryPickaxe(Config.getOrCreateItemIdProperty("bronzePickaxe", Defaults.ID_ITEM_PICKAXE_BRONZE),
				new ItemStack(ForestryItem.brokenBronzePickaxe))).setItemName("bronzePickaxe").setIconIndex(27);
		MinecraftForge.setToolClass(ForestryItem.bronzePickaxe, "pickaxe", 3);
		MinecraftForge.EVENT_BUS.register(ForestryItem.bronzePickaxe);
		ForestryItem.bronzeShovel = (new ItemForestryShovel(Config.getOrCreateItemIdProperty("bronzeShovel", Defaults.ID_ITEM_SHOVEL_BRONZE), new ItemStack(
				ForestryItem.brokenBronzeShovel))).setItemName("bronzeShovel").setIconIndex(30);
		MinecraftForge.setToolClass(ForestryItem.bronzeShovel, "shovel", 3);
		MinecraftForge.EVENT_BUS.register(ForestryItem.bronzeShovel);

		// / ASSEMBLY KITS
		ForestryItem.kitShovel = (new ItemAssemblyKit(Config.getOrCreateItemIdProperty("kitShovel", Defaults.ID_ITEM_TOOLKIT_SHOVEL), new ItemStack(
				ForestryItem.bronzeShovel))).setItemName("kitShovel").setIconIndex(29);
		ForestryItem.kitPickaxe = (new ItemAssemblyKit(Config.getOrCreateItemIdProperty("kitPickaxe", Defaults.ID_ITEM_TOOLKIT_PICKAXE), new ItemStack(
				ForestryItem.bronzePickaxe))).setItemName("kitPickaxe").setIconIndex(26);

		// / MOISTENER RESOURCES
		ForestryItem.mouldyWheat = (new ItemForestry(Config.getOrCreateItemIdProperty("mouldyWheat", Defaults.ID_ITEM_WHEAT_MOULDY)))
				.setItemName("mouldyWheat").setIconIndex(35);
		ForestryItem.decayingWheat = (new ItemForestry(Config.getOrCreateItemIdProperty("decayingWheat", Defaults.ID_ITEM_WHEAT_DECAYING))).setItemName(
				"decayingWheat").setIconIndex(36);
		ForestryItem.mulch = (new ItemForestry(Config.getOrCreateItemIdProperty("mulch", Defaults.ID_ITEM_MULCH))).setItemName("mulch").setIconIndex(34);

		// / RAINMAKER SUBSTRATES
		ForestryItem.iodineCharge = (new ItemForestry(Config.getOrCreateItemIdProperty("iodineCapsule", Defaults.ID_ITEM_IODINE_CAPSULE)))
				.setItemName("iodineCapsule").setIconIndex(40);

		ForestryItem.phosphor = (new ItemForestry(Config.getOrCreateItemIdProperty("phosphor", Defaults.ID_ITEM_PHOSPHOR))).setItemName("phosphor")
				.setIconIndex(78);

		// / BEE RESOURCES
		ForestryItem.beeswax = (new ItemForestry(Config.getOrCreateItemIdProperty("beeswax", Defaults.ID_ITEM_BEESWAX))).setItemName("beeswax")
				.setIconIndex(44).setCreativeTab(Tabs.tabApiculture);
		OreDictionary.registerOre("itemBeeswax", new ItemStack(ForestryItem.beeswax));

		ForestryItem.refractoryWax = (new ItemForestry(Config.getOrCreateItemIdProperty("refractoryWax", Defaults.ID_ITEM_REFRACTORY_WAX)))
				.setItemName("refractoryWax").setIconIndex(79);
		
		// FRUITS
		ForestryItem.fruits = new ItemFruit(Config.getOrCreateItemIdProperty("fruits", Defaults.ID_ITEM_FRUITS))
				.setItemName("fruits");
		
		// / EMPTY LIQUID CONTAINERS
		ForestryItem.waxCapsule = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsule", Defaults.ID_ITEM_WAX_CAPSULE), 1))
				.setItemName("waxCapsule");
		ForestryItem.canEmpty = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canEmpty", Defaults.ID_ITEM_CAN_EMPTY), 0)).setItemName("canEmpty");
		ForestryItem.refractoryEmpty = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryEmpty", Defaults.ID_ITEM_REFRACTORY_EMPTY), 7))
				.setItemName("refractoryEmpty");

		// / BUCKETS
		ForestryItem.bucketBiomass = (new ItemForestry(Config.getOrCreateItemIdProperty("bucketBiomass", Defaults.ID_ITEM_BUCKET_BIOMASS)))
				.setItemName("bucketBiomass").setIconIndex(8).setContainerItem(Item.bucketEmpty).setMaxStackSize(1);
		ForestryItem.bucketBiofuel = (new ItemForestry(Config.getOrCreateItemIdProperty("bucketBiofuel", Defaults.ID_ITEM_BUCKET_BIOFUEL)))
				.setItemName("bucketBiofuel").setIconIndex(14).setContainerItem(Item.bucketEmpty).setMaxStackSize(1);

		// / WAX CAPSULES
		ForestryItem.waxCapsuleWater = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleWater", Defaults.ID_ITEM_WAX_CAPSULE_WATER), 17))
				.setItemName("waxCapsuleWater");
		ForestryItem.waxCapsuleBiomass = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleBiomass", Defaults.ID_ITEM_WAX_CAPSULE_BIOMASS),
				33)).setItemName("waxCapsuleBiomass");
		ForestryItem.waxCapsuleBiofuel = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleBiofuel", Defaults.ID_ITEM_WAX_CAPSULE_BIOFUEL),
				49)).setItemName("waxCapsuleBiofuel");
		ForestryItem.waxCapsuleOil = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleOil", Defaults.ID_ITEM_WAX_CAPSULE_OIL), 97))
				.setItemName("waxCapsuleOil");
		ForestryItem.waxCapsuleFuel = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleFuel", Defaults.ID_ITEM_WAX_CAPSULE_FUEL), 113))
				.setItemName("waxCapsuleFuel");
		ForestryItem.waxCapsuleSeedOil = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleSeedOil", Defaults.ID_ITEM_WAX_CAPSULE_SEED_OIL),
				145)).setItemName("waxCapsuleSeedOil");
		ForestryItem.waxCapsuleHoney = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleHoney", Defaults.ID_ITEM_WAX_CAPSULE_HONEY), 161))
				.setDrink(Defaults.FOOD_HONEY_HEAL, Defaults.FOOD_HONEY_SATURATION).setItemName("waxCapsuleHoney");
		ForestryItem.waxCapsuleJuice = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleJuice", Defaults.ID_ITEM_WAX_CAPSULE_JUICE), 177))
				.setDrink(Defaults.FOOD_JUICE_HEAL, Defaults.FOOD_JUICE_SATURATION).setItemName("waxCapsuleJuice");
		ForestryItem.waxCapsuleIce = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waxCapsuleIce", Defaults.ID_ITEM_WAX_CAPSULE_ICE), 193))
				.setItemName("waxCapsuleIce");

		// / CANS
		ForestryItem.canWater = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("waterCan", Defaults.ID_ITEM_WATERCAN), 16)).setItemName("waterCan");
		ForestryItem.canBiomass = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("biomassCan", Defaults.ID_ITEM_BIOMASSCAN), 32))
				.setItemName("biomassCan");
		ForestryItem.canBiofuel = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("biofuelCan", Defaults.ID_ITEM_BIOFUELCAN), 48))
				.setItemName("biofuelCan");
		ForestryItem.canOil = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canOil", Defaults.ID_ITEM_CAN_OIL), 96)).setItemName("canOil");
		ForestryItem.canFuel = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canFuel", Defaults.ID_ITEM_CAN_FUEL), 112)).setItemName("canFuel");
		ForestryItem.canLava = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canLava", Defaults.ID_ITEM_CAN_LAVA), 128)).setItemName("canLava");
		ForestryItem.canSeedOil = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canSeedOil", Defaults.ID_ITEM_CAN_SEED_OIL), 144))
				.setItemName("canSeedOil");
		ForestryItem.canHoney = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canHoney", Defaults.ID_ITEM_CAN_HONEY), 160)).setDrink(
				Defaults.FOOD_HONEY_HEAL, Defaults.FOOD_HONEY_SATURATION).setItemName("canHoney");
		ForestryItem.canJuice = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canJuice", Defaults.ID_ITEM_CAN_JUICE), 176)).setDrink(
				Defaults.FOOD_JUICE_HEAL, Defaults.FOOD_JUICE_SATURATION).setItemName("canJuice");
		ForestryItem.canIce = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("canIce", Defaults.ID_ITEM_CAN_ICE), 192)).setItemName("canIce");

		// / REFRACTORY CAPSULES
		ForestryItem.refractoryWater = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryWater", Defaults.ID_ITEM_REFRACTORY_WATER), 23))
				.setItemName("refractoryWater");
		ForestryItem.refractoryBiomass = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryBiomass", Defaults.ID_ITEM_REFRACTORY_BIOMASS),
				39)).setItemName("refractoryBiomass");
		ForestryItem.refractoryBiofuel = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryBiofuel", Defaults.ID_ITEM_REFRACTORY_BIOFUEL),
				55)).setItemName("refractoryBiofuel");
		ForestryItem.refractoryOil = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryOil", Defaults.ID_ITEM_REFRACTORY_OIL), 103))
				.setItemName("refractoryOil");
		ForestryItem.refractoryFuel = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryFuel", Defaults.ID_ITEM_REFRACTORY_FUEL), 119))
				.setItemName("refractoryFuel");
		ForestryItem.refractoryLava = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryLava", Defaults.ID_ITEM_REFRACTORY_LAVA), 135))
				.setItemName("refractoryLava");
		ForestryItem.refractorySeedOil = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractorySeedOil", Defaults.ID_ITEM_REFRACTORY_SEED_OIL),
				151)).setItemName("refractorySeedOil");
		ForestryItem.refractoryHoney = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryHoney", Defaults.ID_ITEM_REFRACTORY_HONEY), 167))
				.setDrink(Defaults.FOOD_HONEY_HEAL, Defaults.FOOD_HONEY_SATURATION).setItemName("refractoryHoney");
		ForestryItem.refractoryJuice = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryJuice", Defaults.ID_ITEM_REFRACTORY_JUICE), 183))
				.setDrink(Defaults.FOOD_JUICE_HEAL, Defaults.FOOD_JUICE_SATURATION).setItemName("refractoryJuice");
		ForestryItem.refractoryIce = (new ItemLiquidContainer(Config.getOrCreateItemIdProperty("refractoryIce", Defaults.ID_ITEM_REFRACTORY_ICE), 199))
				.setItemName("refractoryIce");

		// / LIQUIDS
		ForestryItem.liquidMilk = (new ItemLiquids(Config.getOrCreateItemIdProperty("liquidMilk", Defaults.ID_ITEM_MILK))).setItemName("liquidMilk")
				.setIconIndex(15);
		ForestryItem.liquidBiofuel = (new ItemLiquids(Config.getOrCreateItemIdProperty("bioFuel", Defaults.ID_ITEM_BIOFUEL))).setItemName("bioFuel")
				.setIconIndex(12);
		ForestryItem.liquidBiomass = (new ItemLiquids(Config.getOrCreateItemIdProperty("bioMass", Defaults.ID_ITEM_BIOMASS))).setItemName("bioMass")
				.setIconIndex(13);
		ForestryItem.liquidSeedOil = (new ItemLiquids(Config.getOrCreateItemIdProperty("liquidSeedOil", Defaults.ID_ITEM_SEED_OIL))).setItemName(
				"liquidSeedOil").setIconIndex(74);
		ForestryItem.liquidJuice = (new ItemLiquids(Config.getOrCreateItemIdProperty("appleJuice", Defaults.ID_ITEM_APPLE_JUICE))).setItemName("appleJuice")
				.setIconIndex(75);
		ForestryItem.liquidHoney = (new ItemLiquids(Config.getOrCreateItemIdProperty("liquidHoney", Defaults.ID_ITEM_LIQUIDS))).setItemName("liquidHoney")
				.setIconIndex(76);
		ForestryItem.liquidMead = (new ItemLiquids(Config.getOrCreateItemIdProperty("liquidMead", Defaults.ID_ITEM_MEAD))).setItemName("liquidMead")
				.setIconIndex(77);
		ForestryItem.liquidGlass = (new ItemLiquids(Config.getOrCreateItemIdProperty("liquidGlass", Defaults.ID_ITEM_MOLTEN_GLASS))).setItemName("liquidGlass")
				.setIconIndex(49);
		ForestryItem.liquidIce = (new ItemLiquids(Config.getOrCreateItemIdProperty("liquidIce", Defaults.ID_ITEM_CRUSHED_ICE))).setItemName("liquidIce")
				.setIconIndex(73);

		// / CRATES
		ForestryItem.cratedWood = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedWood", Defaults.ID_ITEM_CRATED_WOOD), new ItemStack(
				Block.wood))).setItemName("cratedWood").setIconIndex(1);
		ForestryItem.cratedCobblestone = (ItemCrated) (new ItemCrated(
				Config.getOrCreateItemIdProperty("cratedCobblestone", Defaults.ID_ITEM_CRATED_COBBLESTONE), new ItemStack(Block.cobblestone))).setItemName(
				"cratedCobblestone").setIconIndex(2);
		ForestryItem.cratedDirt = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedDirt", Defaults.ID_ITEM_CRATED_DIRT), new ItemStack(
				Block.dirt))).setItemName("cratedDirt").setIconIndex(3);
		ForestryItem.cratedStone = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedStone", Defaults.ID_ITEM_CRATED_STONE), new ItemStack(
				Block.stone))).setItemName("cratedStone").setIconIndex(4);
		ForestryItem.cratedBrick = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedBrick", Defaults.ID_ITEM_CRATED_BRICK), new ItemStack(
				Block.brick))).setItemName("cratedBrick").setIconIndex(5);
		ForestryItem.cratedCacti = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedCacti", Defaults.ID_ITEM_CRATED_CACTI), new ItemStack(
				Block.cactus))).setItemName("cratedCacti").setIconIndex(6);
		ForestryItem.cratedSand = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedSand", Defaults.ID_ITEM_CRATED_SAND), new ItemStack(
				Block.sand))).setItemName("cratedSand").setIconIndex(7);
		ForestryItem.cratedObsidian = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedObsidian", Defaults.ID_ITEM_CRATED_OBSIDIAN),
				new ItemStack(Block.obsidian))).setItemName("cratedObsidian").setIconIndex(8);
		ForestryItem.cratedNetherrack = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedNetherrack", Defaults.ID_ITEM_CRATED_NETHERRACK),
				new ItemStack(Block.netherrack))).setItemName("cratedNetherrack").setIconIndex(9);
		ForestryItem.cratedSoulsand = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedSoulsand", Defaults.ID_ITEM_CRATED_SOULSAND),
				new ItemStack(Block.slowSand))).setItemName("cratedSoulsand").setIconIndex(10);
		ForestryItem.cratedSandstone = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedSandstone", Defaults.ID_ITEM_CRATED_SANDSTONE),
				new ItemStack(Block.sandStone))).setItemName("cratedSandstone").setIconIndex(11);
		ForestryItem.cratedBogearth = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedBogearth", Defaults.ID_ITEM_CRATED_BOGEARTH),
				new ItemStack(ForestryBlock.soil, 1, 1))).setItemName("cratedBogearth").setIconIndex(12);
		ForestryItem.cratedHumus = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedHumus", Defaults.ID_ITEM_CRATED_HUMUS), new ItemStack(
				ForestryBlock.soil, 1, 0))).setItemName("cratedHumus").setIconIndex(13);
		ForestryItem.cratedNetherbrick = (ItemCrated) (new ItemCrated(
				Config.getOrCreateItemIdProperty("cratedNetherbrick", Defaults.ID_ITEM_CRATED_NETHERBRICK), new ItemStack(Block.netherBrick))).setItemName(
				"cratedNetherbrick").setIconIndex(14);
		ForestryItem.cratedPeat = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedPeat", Defaults.ID_ITEM_CRATED_PEAT), new ItemStack(
				ForestryItem.peat))).setItemName("cratedPeat").setIconIndex(17);
		ForestryItem.cratedApatite = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedApatite", Defaults.ID_ITEM_CRATED_APATITE),
				new ItemStack(ForestryItem.apatite))).setItemName("cratedApatite").setIconIndex(18);
		ForestryItem.cratedFertilizer = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedFertilizer", Defaults.ID_ITEM_CRATED_FERTILIZER),
				new ItemStack(ForestryItem.fertilizerCompound))).setItemName("cratedFertilizer").setIconIndex(19);
		ForestryItem.cratedTin = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedTin", Defaults.ID_ITEM_CRATED_TIN), ForestryItem.ingotTin))
				.setItemName("cratedTin").setIconIndex(20);
		ForestryItem.cratedCopper = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedCopper", Defaults.ID_ITEM_CRATED_COPPER),
				ForestryItem.ingotCopper)).setItemName("cratedCopper").setIconIndex(21);
		ForestryItem.cratedBronze = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedBronze", Defaults.ID_ITEM_CRATED_BRONZE),
				ForestryItem.ingotBronze)).setItemName("cratedBronze").setIconIndex(22);
		ForestryItem.cratedWheat = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedWheat", Defaults.ID_ITEM_CRATED_WHEAT), new ItemStack(
				Item.wheat))).setItemName("cratedWheat").setIconIndex(23);
		ForestryItem.cratedMycelium = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedMycelium", Defaults.ID_ITEM_CRATED_MYCELIUM),
				new ItemStack(Block.mycelium))).setItemName("cratedMycelium").setIconIndex(15);
		ForestryItem.cratedMulch = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedMulch", Defaults.ID_ITEM_CRATED_MULCH), new ItemStack(
				ForestryItem.mulch))).setItemName("cratedMulch").setIconIndex(24);
		ForestryItem.cratedSilver = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedSilver", Defaults.ID_ITEM_CRATED_SILVER)))
				.setItemName("cratedSilver").setIconIndex(25);
		ForestryItem.cratedBrass = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedBrass", Defaults.ID_ITEM_CRATED_BRASS))).setItemName(
				"cratedBrass").setIconIndex(26);
		ForestryItem.cratedNikolite = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedNikolite", Defaults.ID_ITEM_CRATED_NIKOLITE)))
				.setItemName("cratedNikolite").setIconIndex(27);
		ForestryItem.cratedCookies = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedCookies", Defaults.ID_ITEM_CRATED_COOKIES),
				new ItemStack(Item.cookie))).setItemName("cratedCookies").setIconIndex(28);
		ForestryItem.cratedRedstone = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedRedstone", Defaults.ID_ITEM_CRATED_REDSTONE),
				new ItemStack(Item.redstone))).setItemName("cratedRedstone").setIconIndex(36);
		ForestryItem.cratedLapis = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedLapis", Defaults.ID_ITEM_CRATED_LAPIS), new ItemStack(
				Item.dyePowder, 1, 4))).setItemName("cratedLapis").setIconIndex(37);
		ForestryItem.cratedReeds = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedReeds", Defaults.ID_ITEM_CRATED_REEDS), new ItemStack(
				Item.reed))).setItemName("cratedReeds").setIconIndex(38);
		ForestryItem.cratedClay = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedClay", Defaults.ID_ITEM_CRATED_CLAY), new ItemStack(
				Item.clay))).setItemName("cratedClay").setIconIndex(39);
		ForestryItem.cratedGlowstone = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedGlowstone", Defaults.ID_ITEM_CRATED_GLOWSTONE),
				new ItemStack(Item.lightStoneDust))).setItemName("cratedGlowstone").setIconIndex(40);
		ForestryItem.cratedApples = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedApples", Defaults.ID_ITEM_CRATED_APPLES),
				new ItemStack(Item.appleRed))).setItemName("cratedApples").setIconIndex(41);
		ForestryItem.cratedNetherwart = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedNetherwart", Defaults.ID_ITEM_CRATED_NETHERWART),
				new ItemStack(Item.netherStalkSeeds))).setItemName("cratedNetherwart").setIconIndex(42);
		ForestryItem.cratedResin = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedResin", Defaults.ID_ITEM_CRATED_RESIN))).setItemName(
				"cratedResin").setIconIndex(43);
		ForestryItem.cratedRubber = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedRubber", Defaults.ID_ITEM_CRATED_RUBBER)))
				.setItemName("cratedRubber").setIconIndex(44);
		ForestryItem.cratedScrap = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedScrap", Defaults.ID_ITEM_CRATED_SCRAP))).setItemName(
				"cratedScrap").setIconIndex(45);
		ForestryItem.cratedUUM = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedUUM", Defaults.ID_ITEM_CRATED_UUM))).setItemName(
				"cratedUUM").setIconIndex(46);

		ForestryItem.cratedPhosphor = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedPhosphor", Defaults.ID_ITEM_CRATED_PHOSPHOR),
				new ItemStack(ForestryItem.phosphor))).setItemName("cratedPhosphor").setIconIndex(52);
		ForestryItem.cratedAsh = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedAsh", Defaults.ID_ITEM_CRATED_ASH), new ItemStack(
				ForestryItem.ash))).setItemName("cratedAsh").setIconIndex(53);
		ForestryItem.cratedCharcoal = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedCharcoal", Defaults.ID_ITEM_CRATED_CHARCOAL),
				new ItemStack(Item.coal, 1, 1))).setItemName("cratedCharcoal").setIconIndex(54);
		ForestryItem.cratedGravel = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedGravel", Defaults.ID_ITEM_CRATED_GRAVEL),
				new ItemStack(Block.gravel))).setItemName("cratedGravel").setIconIndex(55);
		ForestryItem.cratedCoal = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedCoal", Defaults.ID_ITEM_CRATED_COAL), new ItemStack(
				Item.coal, 1, 0))).setItemName("cratedCoal").setIconIndex(54);
		ForestryItem.cratedSeeds = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedSeeds", Defaults.ID_ITEM_CRATED_SEEDS), new ItemStack(
				Item.seeds))).setItemName("cratedSeeds").setIconIndex(56);
		ForestryItem.cratedSaplings = (ItemCrated) (new ItemCrated(Config.getOrCreateItemIdProperty("cratedSaplings", Defaults.ID_ITEM_CRATED_SAPLINGS),
				new ItemStack(Block.sapling))).setItemName("cratedSaplings").setIconIndex(57);

	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {

		// / BRONZE INGOTS
		if (Config.craftingBronzeEnabled) {
			Proxies.common.addRecipe(new ItemStack(ForestryItem.ingotBronze.itemID, 4, ForestryItem.ingotBronze.getItemDamage()), new Object[] { "##", "#X",
					Character.valueOf('#'), "ingotCopper", Character.valueOf('X'), "ingotTin" });
		}

		// / STURDY MACHINE
		Proxies.common.addRecipe(new ItemStack(ForestryItem.sturdyCasing, 1), new Object[] { "###", "# #", "###", Character.valueOf('#'), "ingotBronze" });

		// / EMPTY CANS
		Proxies.common.addRecipe(GameMode.getGameMode().getRecipeCanOutput(), new Object[] { " # ", "# #", Character.valueOf('#'), "ingotTin" });

		// / GEARS
		if (PluginBuildCraft.stoneGear != null) {

			Proxies.common.addRecipe(new ItemStack(ForestryItem.gearBronze, 1), new Object[] { " # ", "#Y#", " # ", Character.valueOf('#'), "ingotBronze",
					Character.valueOf('Y'), PluginBuildCraft.stoneGear });
			Proxies.common.addRecipe(new ItemStack(ForestryItem.gearCopper, 1), new Object[] { " # ", "#Y#", " # ", Character.valueOf('#'), "ingotCopper",
					Character.valueOf('Y'), PluginBuildCraft.stoneGear });
			Proxies.common.addRecipe(new ItemStack(ForestryItem.gearTin, 1),
					new Object[] { " # ", "#Y#", " # ", Character.valueOf('#'), "ingotTin", Character.valueOf('Y'), PluginBuildCraft.stoneGear });

		} else {

			Proxies.common.addRecipe(new ItemStack(ForestryItem.gearBronze, 1), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), "ingotBronze",
					Character.valueOf('X'), "ingotCopper" });
			Proxies.common.addRecipe(new ItemStack(ForestryItem.gearCopper, 1), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), "ingotCopper",
					Character.valueOf('X'), "ingotCopper" });
			Proxies.common.addRecipe(new ItemStack(ForestryItem.gearTin, 1),
					new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), "ingotTin", Character.valueOf('X'), "ingotCopper" });
		}

		// / SURVIVALIST TOOLS
		Proxies.common.addRecipe(new ItemStack(ForestryItem.bronzePickaxe), new Object[] { " X ", " X ", "###", '#', "ingotBronze", 'X', "stickWood" });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.bronzeShovel), new Object[] { " X ", " X ", " # ", '#', "ingotBronze", 'X', "stickWood" });
		Proxies.common.addShapelessRecipe(new ItemStack(ForestryItem.kitPickaxe), new Object[] { ForestryItem.bronzePickaxe, ForestryItem.carton });
		Proxies.common.addShapelessRecipe(new ItemStack(ForestryItem.kitShovel), new Object[] { ForestryItem.bronzeShovel, ForestryItem.carton });

		// / WRENCH
		Proxies.common.addRecipe(new ItemStack(ForestryItem.wrench, 1), new Object[] { "# #", " # ", " # ", Character.valueOf('#'), "ingotBronze" });

		// Manure and Fertilizer
		if (GameMode.getGameMode().getRecipeCompostOutputWheat().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeCompostOutputWheat(), new Object[] { " X ", "X#X", " X ", Character.valueOf('#'),
					Block.dirt, Character.valueOf('X'), Item.wheat });
		}
		if (GameMode.getGameMode().getRecipeCompostOutputAsh().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeCompostOutputAsh(), new Object[] { " X ", "X#X", " X ", Character.valueOf('#'),
					Block.dirt, Character.valueOf('X'), "dustAsh" });
		}
		if (GameMode.getGameMode().getRecipeFertilizerOutputApatite().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeFertilizerOutputApatite(), new Object[] { " # ", " X ", " # ", Character.valueOf('#'),
					Block.sand, Character.valueOf('X'), ForestryItem.apatite });
		}
		if (GameMode.getGameMode().getRecipeFertilizerOutputAsh().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeFertilizerOutputAsh(), new Object[] { "###", "#X#", "###", '#', "dustAsh", 'X', ForestryItem.apatite });
		}

		// Humus
		if (GameMode.getGameMode().getRecipeHumusOutputCompost().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeHumusOutputCompost(),
					new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.dirt, Character.valueOf('X'), ForestryItem.fertilizerBio });
		}
		if (GameMode.getGameMode().getRecipeHumusOutputFertilizer().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeHumusOutputFertilizer(),
					new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.dirt, Character.valueOf('X'), ForestryItem.fertilizerCompound });
		}

		// Bog earth
		if (GameMode.getGameMode().getRecipeBogEarthOutputBucket().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeBogEarthOutputBucket(), new Object[] { "#Y#", "YXY", "#Y#", Character.valueOf('#'),
					Block.dirt, Character.valueOf('X'), Item.bucketWater, Character.valueOf('Y'), Block.sand });
		}

		if (GameMode.getGameMode().getRecipeBogEarthOutputCans().stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeBogEarthOutputCans(), new Object[] { "#Y#", "YXY", "#Y#", Character.valueOf('#'),
					Block.dirt, Character.valueOf('X'), ForestryItem.canWater, Character.valueOf('Y'), Block.sand });
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeBogEarthOutputCans(), new Object[] { "#Y#", "YXY", "#Y#", Character.valueOf('#'),
					Block.dirt, Character.valueOf('X'), ForestryItem.waxCapsuleWater, Character.valueOf('Y'), Block.sand });
			Proxies.common.addRecipe(GameMode.getGameMode().getRecipeBogEarthOutputCans(), new Object[] { "#Y#", "YXY", "#Y#", Character.valueOf('#'),
					Block.dirt, Character.valueOf('X'), ForestryItem.refractoryWater, Character.valueOf('Y'), Block.sand });
		}

		// Vials and catalyst
		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst, 3), new Object[] { "###", "YXY", Character.valueOf('#'), ForestryItem.waxCapsule,
				Character.valueOf('X'), Item.bone, Character.valueOf('Y'), ForestryItem.fertilizerCompound });
		Proxies.common.addRecipe(new ItemStack(ForestryItem.vialCatalyst, 3), new Object[] { "###", "YXY", Character.valueOf('#'), ForestryItem.canEmpty,
				Character.valueOf('X'), Item.bone, Character.valueOf('Y'), ForestryItem.fertilizerCompound });

		// Crafting Material
		Proxies.common.addRecipe(new ItemStack(Item.silk), new Object[] { "#", "#", "#", Character.valueOf('#'),
				new ItemStack(ForestryItem.craftingMaterial, 1, 2) });

		// / Pipette
		Proxies.common.addRecipe(new ItemStack(ForestryItem.pipette),
				new Object[] { "  #", " X ", "X  ", Character.valueOf('X'), Block.thinGlass, Character.valueOf('#'), new ItemStack(Block.cloth, 1, -1) });
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[] { new CommandForestry() };
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		if (fuel != null && fuel.itemID == ForestryItem.peat.itemID)
			return 2000;
		if (fuel != null && fuel.itemID == ForestryItem.bituminousPeat.itemID)
			return 4200;

		return 0;
	}
	
	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if(message.key.equals("securityViolation")) {
			Config.invalidFingerprint = true;
		}
		return false;
	}

	/* PACKAGE DEFINITION */
	public static IRecipe[] getAnalyzerRecipes(int blockid, int meta) {
		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
		if(ForestryItem.beealyzer != null)
			recipes.add(ShapedRecipeCustom.createShapedRecipe(new Object[] { "XTX", " Y ", "X X", Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('T'),
				ForestryItem.beealyzer, Character.valueOf('X'), "ingotBronze" },
				new ItemStack(blockid, 1, meta)
			));
		
		if(ForestryItem.treealyzer != null)
			recipes.add(ShapedRecipeCustom.createShapedRecipe(new Object[] { "XTX", " Y ", "X X", Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('T'),
				ForestryItem.treealyzer, Character.valueOf('X'), "ingotBronze" },
				new ItemStack(blockid, 1, meta)
				));

		return recipes.toArray(new IRecipe[0]);
	}

}
