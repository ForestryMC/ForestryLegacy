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

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.arboriculture.CommandSpawnForest;
import forestry.arboriculture.CommandSpawnTree;
import forestry.arboriculture.EventHandlerArboriculture;
import forestry.arboriculture.FruitProviderRandom;
import forestry.arboriculture.FruitProviderRipening;
import forestry.arboriculture.FruitProviderNone;
import forestry.arboriculture.GuiHandlerArboriculture;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.gadgets.BlockArbFence;
import forestry.arboriculture.gadgets.BlockArbStairs;
import forestry.arboriculture.gadgets.BlockLeaves;
import forestry.arboriculture.gadgets.BlockLog;
import forestry.arboriculture.gadgets.TileStairs;
import forestry.arboriculture.gadgets.BlockLog.LogCat;
import forestry.arboriculture.gadgets.BlockPlanks;
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.arboriculture.gadgets.BlockSlab;
import forestry.arboriculture.gadgets.BlockSlab.SlabCat;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.arboriculture.genetics.AlleleFruit;
import forestry.arboriculture.genetics.AlleleGrowth;
import forestry.arboriculture.genetics.AlleleLeafEffectNone;
import forestry.arboriculture.genetics.AlleleTreeSpecies;
import forestry.arboriculture.genetics.BranchTrees;
import forestry.arboriculture.genetics.BreedingManager;
import forestry.arboriculture.genetics.GrowthProvider;
import forestry.arboriculture.genetics.GrowthProviderTropical;
import forestry.arboriculture.genetics.Mutation;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.genetics.TreeTemplates;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemGrafter;
import forestry.arboriculture.items.ItemStairs;
import forestry.arboriculture.items.ItemTreealyzer;
import forestry.arboriculture.items.ItemWoodBlock;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.worldgen.WorldGenAcacia;
import forestry.arboriculture.worldgen.WorldGenBalsa;
import forestry.arboriculture.worldgen.WorldGenBaobab;
import forestry.arboriculture.worldgen.WorldGenBirch;
import forestry.arboriculture.worldgen.WorldGenCherry;
import forestry.arboriculture.worldgen.WorldGenChestnut;
import forestry.arboriculture.worldgen.WorldGenEbony;
import forestry.arboriculture.worldgen.WorldGenGreenheart;
import forestry.arboriculture.worldgen.WorldGenJungle;
import forestry.arboriculture.worldgen.WorldGenKapok;
import forestry.arboriculture.worldgen.WorldGenLarch;
import forestry.arboriculture.worldgen.WorldGenLime;
import forestry.arboriculture.worldgen.WorldGenMahogany;
import forestry.arboriculture.worldgen.WorldGenOak;
import forestry.arboriculture.worldgen.WorldGenPine;
import forestry.arboriculture.worldgen.WorldGenSequoia;
import forestry.arboriculture.worldgen.WorldGenSpruce;
import forestry.arboriculture.worldgen.WorldGenTeak;
import forestry.arboriculture.worldgen.WorldGenWalnut;
import forestry.arboriculture.worldgen.WorldGenWenge;
import forestry.arboriculture.worldgen.WorldGenWillow;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.Allele;
import forestry.core.genetics.FruitFamily;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;

@PluginInfo(pluginID = "Arboriculture", name = "Arboriculture", author = "Binnie & SirSengir", url = Defaults.URL, description = "Adds additional tree species and products.")
public class PluginForestryArboriculture extends NativePlugin {

	@SidedProxy(clientSide = "forestry.arboriculture.proxy.ClientProxyArboriculture", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;

	public static int modelIdSaplings;
	public static int modelIdLeaves;
	public static int modelIdFences;

	@Override
	public boolean isAvailable() {
		return !Config.disableArboriculture;
	}

	@Override
	public String getDescription() {
		return "Arboriculture";
	}

	@Override
	public void preInit() {
		super.preInit();

		ForestryBlock.log1 = new BlockLog(Config.getOrCreateBlockIdProperty("log1", Defaults.ID_BLOCK_LOG1), LogCat.CAT0).setBlockName("for.log1");
		Item.itemsList[ForestryBlock.log1.blockID] = null;
		Item.itemsList[ForestryBlock.log1.blockID] = (new ItemWoodBlock(ForestryBlock.log1.blockID - 256, "for.log1"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.log1, "axe", 0);

		// Send to BC for facades
		for(int i = 0; i < 4; i++)
			FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", ForestryBlock.log1.blockID + "@" + i);

		ForestryBlock.log2 = new BlockLog(Config.getOrCreateBlockIdProperty("log2", Defaults.ID_BLOCK_LOG2), LogCat.CAT1).setBlockName("for.log2");
		Item.itemsList[ForestryBlock.log2.blockID] = null;
		Item.itemsList[ForestryBlock.log2.blockID] = (new ItemWoodBlock(ForestryBlock.log2.blockID - 256, "for.log2"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.log2, "axe", 0);

		for(int i = 0; i < 4; i++)
			FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", ForestryBlock.log2.blockID + "@" + i);

		ForestryBlock.log3 = new BlockLog(Config.getOrCreateBlockIdProperty("log3", Defaults.ID_BLOCK_LOG3), LogCat.CAT2).setBlockName("for.log3");
		Item.itemsList[ForestryBlock.log3.blockID] = null;
		Item.itemsList[ForestryBlock.log3.blockID] = (new ItemWoodBlock(ForestryBlock.log3.blockID - 256, "for.log3"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.log3, "axe", 0);

		for(int i = 0; i < 4; i++)
			FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", ForestryBlock.log3.blockID + "@" + i);

		ForestryBlock.log4 = new BlockLog(Config.getOrCreateBlockIdProperty("log4", Defaults.ID_BLOCK_LOG4), LogCat.CAT3).setBlockName("for.log4");
		Item.itemsList[ForestryBlock.log4.blockID] = null;
		Item.itemsList[ForestryBlock.log4.blockID] = (new ItemWoodBlock(ForestryBlock.log4.blockID - 256, "for.log4"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.log4, "axe", 0);

		for(int i = 0; i < 4; i++)
			FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", ForestryBlock.log4.blockID + "@" + i);

		// Register as workableLogs
		OreDictionary.registerOre("logWood", new ItemStack(ForestryBlock.log1, 1, -1));
		OreDictionary.registerOre("logWood", new ItemStack(ForestryBlock.log2, 1, -1));
		OreDictionary.registerOre("logWood", new ItemStack(ForestryBlock.log3, 1, -1));
		OreDictionary.registerOre("logWood", new ItemStack(ForestryBlock.log4, 1, -1));

		// Register smelting
		FurnaceRecipes.smelting().addSmelting(ForestryBlock.log1.blockID, new ItemStack(Item.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().addSmelting(ForestryBlock.log2.blockID, new ItemStack(Item.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().addSmelting(ForestryBlock.log3.blockID, new ItemStack(Item.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().addSmelting(ForestryBlock.log4.blockID, new ItemStack(Item.coal, 1, 1), 0.15F);
		
		ForestryBlock.planks = new BlockPlanks(Config.getOrCreateBlockIdProperty("planks", Defaults.ID_BLOCK_PLANKS)).setBlockName("for.planks");
		Item.itemsList[ForestryBlock.planks.blockID] = null;
		Item.itemsList[ForestryBlock.planks.blockID] = (new ItemWoodBlock(ForestryBlock.planks.blockID - 256, "for.planks"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.planks, "axe", 0);

		// Register as craftablePlanks
		OreDictionary.registerOre("plankWood", new ItemStack(ForestryBlock.planks, 1, -1));

		// Send to BC for facades
		for(int i = 0; i < 16; i++)
			FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", ForestryBlock.planks.blockID + "@" + i);
		
		ForestryBlock.slabs1 = new BlockSlab(Config.getOrCreateBlockIdProperty("slabs1", Defaults.ID_BLOCK_SLABS1), SlabCat.CAT_0).setBlockName("for.slabs1");
		Item.itemsList[ForestryBlock.slabs1.blockID] = null;
		Item.itemsList[ForestryBlock.slabs1.blockID] = (new ItemWoodBlock(ForestryBlock.slabs1.blockID - 256, "for.slabs1"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.slabs1, "axe", 0);

		ForestryBlock.slabs2 = new BlockSlab(Config.getOrCreateBlockIdProperty("slabs2", Defaults.ID_BLOCK_SLABS2), SlabCat.CAT_1).setBlockName("for.slabs2");
		Item.itemsList[ForestryBlock.slabs2.blockID] = null;
		Item.itemsList[ForestryBlock.slabs2.blockID] = (new ItemWoodBlock(ForestryBlock.slabs2.blockID - 256, "for.slabs2"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.slabs2, "axe", 0);

		OreDictionary.registerOre("slabWood", new ItemStack(ForestryBlock.slabs1, 1, -1));
		OreDictionary.registerOre("slabWood", new ItemStack(ForestryBlock.slabs2, 1, -1));

		// Fences
		ForestryBlock.fences = new BlockArbFence(Config.getOrCreateBlockIdProperty("fences", Defaults.ID_BLOCK_FENCES)).setBlockName("for.fences");
		Item.itemsList[ForestryBlock.fences.blockID] = null;
		Item.itemsList[ForestryBlock.fences.blockID] = (new ItemWoodBlock(ForestryBlock.fences.blockID - 256, "for.fences"));
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.fences, "axe", 0);

		// Stairs
		ForestryBlock.stairs = new BlockArbStairs(Config.getOrCreateBlockIdProperty("stairs", Defaults.ID_BLOCK_STAIRS), ForestryBlock.planks, 0).setBlockName("for.stairs");
		Item.itemsList[ForestryBlock.stairs.blockID] = null;
		Item.itemsList[ForestryBlock.stairs.blockID] = new ItemStairs(ForestryBlock.stairs.blockID - 256, "for.stairs");
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.stairs, "axe", 0);
		
		// Saplings
		ForestryBlock.saplingGE = new BlockSapling(Config.getOrCreateBlockIdProperty("saplingGE", Defaults.ID_BLOCK_SAPLING_GE)).setBlockName("saplingGE");
		Item.itemsList[ForestryBlock.saplingGE.blockID] = null;
		Item.itemsList[ForestryBlock.saplingGE.blockID] = (new ItemForestryBlock(ForestryBlock.saplingGE.blockID - 256, "saplingGE"));

		// Leaves
		ForestryBlock.leaves = new BlockLeaves(Config.getOrCreateBlockIdProperty("leaves", Defaults.ID_BLOCK_LEAVES)).setBlockName("leaves");
		Item.itemsList[ForestryBlock.leaves.blockID] = null;
		Item.itemsList[ForestryBlock.leaves.blockID] = (new ItemForestryBlock(ForestryBlock.leaves.blockID - 256, "leaves"));
		
		GameRegistry.registerTileEntity(TileSapling.class, "forestry.Sapling");
		GameRegistry.registerTileEntity(TileLeaves.class, "forestry.Leaves");
		GameRegistry.registerTileEntity(TileStairs.class, "forestry.Stairs");

		// Init tree interface
		TreeManager.treeInterface = new TreeHelper();
		// Init breeding manager
		TreeManager.breedingManager = new BreedingManager();

		// Init rendering
		proxy.initializeRendering();
	}

	@Override
	public void doInit() {
		super.doInit();
		
		proxy.addLocalizations();

		createAlleles();
		createMutations();
		registerTemplates();
		registerErsatzGenomes();

		MinecraftForge.EVENT_BUS.register(new EventHandlerArboriculture());
	}

	@Override
	protected void registerPackages() {
	}

	@Override
	protected void registerItems() {

		ForestryItem.sapling = new ItemGermlingGE(Config.getOrCreateItemIdProperty("sapling", Defaults.ID_ITEM_SAPLING), EnumGermlingType.SAPLING);
		
		ForestryItem.treealyzer = (new ItemTreealyzer(Config.getOrCreateItemIdProperty("treealyzer", Defaults.ID_ITEM_TREEALYZER))).setItemName("treealyzer")
				.setIconIndex(81);
		ForestryItem.grafter = (new ItemGrafter(Config.getOrCreateItemIdProperty("grafter", Defaults.ID_ITEM_GRAFTER))).setItemName("grafter")
				.setIconIndex(63);

	}

	@Override
	protected void registerBackpackItems() {
		if (BackpackManager.backpackItems == null)
			return;

		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log1, 1, -1));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log2, 1, -1));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log3, 1, -1));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log4, 1, -1));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryItem.sapling, 1, -1));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryItem.fruits, 1, -1));
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {

		// / Plank recipes
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks, 4, i), new Object[] { new ItemStack(ForestryBlock.log1, 1, i) });
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks, 4, 4 + i), new Object[] { new ItemStack(ForestryBlock.log2, 1, i) });
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks, 4, 8 + i), new Object[] { new ItemStack(ForestryBlock.log3, 1, i) });
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks, 4, 12 + i), new Object[] { new ItemStack(ForestryBlock.log4, 1, i) });
		}
		
		// Slab recipes
		for(int i = 0; i < 8; i++)
			Proxies.common.addRecipe(new ItemStack(ForestryBlock.slabs1, 6, i), new Object[] {
				"###", '#', new ItemStack(ForestryBlock.planks, 1, i)
			});
		for(int i = 0; i < 8; i++)
			Proxies.common.addRecipe(new ItemStack(ForestryBlock.slabs2, 6, i), new Object[] {
				"###", '#', new ItemStack(ForestryBlock.planks, 1, 8 + i)
			});
		
		// Fence recipes
		for(int i = 0; i < 16; i++)
			Proxies.common.addRecipe(new ItemStack(ForestryBlock.fences, 4, i), new Object[] {
				"###", "# #", '#', new ItemStack(ForestryBlock.planks, 1, i)
			});
		
		// Treealyzer
		RecipeManagers.carpenterManager.addRecipe(100, new LiquidStack(Block.waterStill.blockID, 2000), null, new ItemStack(ForestryItem.treealyzer),
				new Object[] { "X#X", "X#X", "RDR", Character.valueOf('#'), Block.thinGlass, Character.valueOf('X'), "ingotCopper", Character.valueOf('R'),
						Item.redstone, Character.valueOf('D'), Item.diamond });
		
		// Squeezer
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.fruits, 1, EnumFruit.CHERRY.ordinal()) }, new LiquidStack(ForestryItem.liquidSeedOil, 3*GameMode.getGameMode().getSqueezedLiquidPerSeed()),
				new ItemStack(ForestryItem.mulch), 5);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.fruits, 1, EnumFruit.WALNUT.ordinal()) }, new LiquidStack(ForestryItem.liquidSeedOil, 10*GameMode.getGameMode().getSqueezedLiquidPerSeed()),
				new ItemStack(ForestryItem.mulch), 5);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.fruits, 1, EnumFruit.CHESTNUT.ordinal()) }, new LiquidStack(ForestryItem.liquidSeedOil, 12*GameMode.getGameMode().getSqueezedLiquidPerSeed()),
				new ItemStack(ForestryItem.mulch), 5);

		RecipeUtil.injectLeveledRecipe(new ItemStack(ForestryItem.sapling), GameMode.getGameMode().getFermentedPerSapling(), new ItemStack(
				ForestryItem.liquidBiomass));
		
		// Stairs
		for(int i = 0; i < 16; i++) {
			WoodType type = WoodType.values()[i];
			NBTTagCompound compound = new NBTTagCompound("tag");
			type.saveToCompound(compound);

			ItemStack stairs = new ItemStack(ForestryBlock.stairs, 4, 0);
			stairs.setTagCompound(compound);
			Proxies.common.addRecipe(stairs,  new Object[] {
					"#  ", "## ", "###", '#', new ItemStack(ForestryBlock.planks, 1, i)
			});
		}
		
		// Grafter
		Proxies.common.addRecipe(new ItemStack(ForestryItem.grafter), new Object[] {
			"  B", " # ", "#  ", 'B', "ingotBronze", '#', Item.stick
		});
	}

	private void createAlleles() {

		// Divisions
		IClassification angiosperms = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "angiosperms", "Angiosperms");
		AlleleManager.alleleRegistry.getClassification("kingdom.plantae").addMemberGroup(angiosperms);
		IClassification pinophyta = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "pinophyta", "Pinophyta");
		AlleleManager.alleleRegistry.getClassification("kingdom.plantae").addMemberGroup(pinophyta);
		IClassification magnoliophyta = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "magnoliophyta", "Magnoliophyta");
		AlleleManager.alleleRegistry.getClassification("kingdom.plantae").addMemberGroup(magnoliophyta);
		
		// Classes
		IClassification rosids = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "rosids", "Rosids");
		angiosperms.addMemberGroup(rosids);
		IClassification asterids = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "asterids", "Asterids");
		angiosperms.addMemberGroup(asterids);
		IClassification pinopsida = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "pinopsida", "Pinopsida");
		pinophyta.addMemberGroup(pinopsida);
		IClassification magnoliopsida = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "magnoliopsida", "Magnoliopsida");
		pinophyta.addMemberGroup(magnoliopsida);
		
		// Orders
		IClassification fabales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "fabales", "Fabales");
		rosids.addMemberGroup(fabales);
		IClassification rosales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "rosales", "Rosales");
		rosids.addMemberGroup(rosales);
		IClassification fagales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "fagales", "Fagales");
		rosids.addMemberGroup(fagales);
		IClassification malvales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "malvales", "Malvales");
		rosids.addMemberGroup(malvales);
		IClassification malpighiales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "malpighiales", "Malpighiales");
		rosids.addMemberGroup(malpighiales);
		
		IClassification ericales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "ericales", "Ericales");
		asterids.addMemberGroup(ericales);		
		IClassification lamiales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "lamiales", "Lamiales");
		asterids.addMemberGroup(lamiales);
		
		IClassification pinales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "pinales", "Pinales");
		pinopsida.addMemberGroup(pinales);
		
		IClassification laurales = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "laurales", "Laurales");
		rosids.addMemberGroup(laurales);

		// Families
		IClassification betulaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "betulaceae", "Betulaceae");
		fagales.addMemberGroup(betulaceae);
		IClassification fagaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "fagaceae", "Fagaceae");
		fagales.addMemberGroup(fagaceae);
		IClassification juglandaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "juglandaceae", "Juglandaceae");
		fagales.addMemberGroup(juglandaceae);
		
		IClassification malvaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "malvaceae", "Malvaceae");
		malvales.addMemberGroup(malvaceae);
		IClassification dipterocarpaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "dipterocarpaceae", "Dipterocarpaceae");
		malvales.addMemberGroup(dipterocarpaceae);
		
		IClassification pinaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "pinaceae", "Pinaceae");
		pinales.addMemberGroup(pinaceae);
		IClassification cupressaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "cupressaceae", "Cupressaceae");
		pinales.addMemberGroup(cupressaceae);
		
		IClassification lamiaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "lamiaceae", "Lamiaceae");
		lamiales.addMemberGroup(lamiaceae);
		
		IClassification ebenaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "ebenaceae", "Ebenaceae");
		ericales.addMemberGroup(ebenaceae);
		
		IClassification fabaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "fabaceae", "Fabaceae");
		ericales.addMemberGroup(fabaceae);
		
		IClassification rosaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "rosaceae", "Rosaceae");
		rosales.addMemberGroup(rosaceae);

		IClassification salicaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "salicaceae", "Salicaceae");
		malpighiales.addMemberGroup(salicaceae);
		
		IClassification lauraceae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "lauraceae", "Lauraceae");
		malpighiales.addMemberGroup(lauraceae);
		
		// Genii
		IClassification quercus = new BranchTrees("quercus", "Quercus");
		fagaceae.addMemberGroup(quercus);
		IClassification castanea = new BranchTrees("castanea", "Castanea");
		fagaceae.addMemberGroup(castanea);
		
		IClassification betula = new BranchTrees("betula", "Betula");
		betulaceae.addMemberGroup(betula);
		
		IClassification tilia = new BranchTrees("tilia", "Tilia");
		malvaceae.addMemberGroup(tilia);
		IClassification ceiba = new BranchTrees("ceiba", "Ceiba");
		malvaceae.addMemberGroup(ceiba);
		IClassification adansonia = new BranchTrees("adansonia", "Adansonia");
		malvaceae.addMemberGroup(adansonia);
		
		IClassification picea = new BranchTrees("picea", "Picea");
		pinaceae.addMemberGroup(picea);		
		IClassification pinus = new BranchTrees("pinus", "Pinus");
		pinaceae.addMemberGroup(pinus);
		IClassification larix = new BranchTrees("larix", "Larix");
		pinaceae.addMemberGroup(larix);
		
		IClassification juglans = new BranchTrees("juglans", "Juglans");
		juglandaceae.addMemberGroup(juglans);
		
		IClassification sequoia = new BranchTrees("sequoia", "Sequoia");
		cupressaceae.addMemberGroup(sequoia);
		
		IClassification tectona = new BranchTrees("tectona", "Tectona");
		lamiaceae.addMemberGroup(tectona);
		
		IClassification diospyros = new BranchTrees("ebony", "Diospyros");
		ebenaceae.addMemberGroup(diospyros);
		
		IClassification shorea = new BranchTrees("mahogany", "Shorea");
		dipterocarpaceae.addMemberGroup(shorea);
		
		IClassification acacia = new BranchTrees("acacia", "Acacia");
		fabaceae.addMemberGroup(acacia);
		IClassification millettia = new BranchTrees("millettia", "Millettia");
		fabaceae.addMemberGroup(millettia);
		
		IClassification ochroma = new BranchTrees("ochroma", "Ochroma");
		malvaceae.addMemberGroup(ochroma);

		IClassification prunus = new BranchTrees("prunus", "Prunus");
		rosaceae.addMemberGroup(prunus);
		
		IClassification salix = new BranchTrees("salix", "Salix");
		salicaceae.addMemberGroup(salix);
		
		IClassification chlorocardium = new BranchTrees("chlorocardium", "Chlorocardium");
		salicaceae.addMemberGroup(chlorocardium);
		
		IClassification tropical = new BranchTrees("Tropical", "");
		

		IFruitFamily prunes = new FruitFamily("prunes", "Prunus domestica");
		IFruitFamily pomes = new FruitFamily("pomes", "Pomum");
		IFruitFamily jungle = new FruitFamily("jungle", "Tropicus");
		IFruitFamily nux = new FruitFamily("nuts", "Nux");
		
		// Deciduous
		Allele.treeOak = new AlleleTreeSpecies("treeOak", false, "Apple Oak", quercus, "robur", proxy.getFoliageColorBasic(), 0x53b698, WorldGenOak.class)
		.addFruitFamily(pomes).setBodyType(0).setIsSecret();
		Allele.treeBirch = new AlleleTreeSpecies("treeBirch", false, "Silver Birch", betula, "pendula", proxy.getFoliageColorBirch(), 0xfeff8f, WorldGenBirch.class)
		.setBodyType(1).setIsSecret();
		Allele.treeLime = new AlleleTreeSpecies("treeLime", true, "Silver Lime", tilia, "tomentosa", 0x5ea107, 0x5ea18f, WorldGenLime.class)
		.addFruitFamily(nux).addFruitFamily(prunes).addFruitFamily(pomes).setBodyType(20);
		
		// Nucifera
		Allele.treeWalnut = new AlleleTreeSpecies("treeWalnut", true, "Common Walnut", juglans, "regia", 0x798c55, 0xb0c648, WorldGenWalnut.class)
		.addFruitFamily(nux).addFruitFamily(prunes).addFruitFamily(pomes).setGirth(2).setBodyType(24);
		Allele.treeChestnut = new AlleleTreeSpecies("treeChestnut", true, "Sweet Chestnut", castanea, "sativa", 0x5ea107, 0xb0c648, WorldGenChestnut.class)
		.addFruitFamily(nux).addFruitFamily(prunes).addFruitFamily(pomes).setGirth(2).setBodyType(22);
		
		// Fructifera
		// <CovertJaguar> fructifer, annifer, bifer, aurifer = bearing fruit, bearing fruit year round, bearing fruit twice a year, bearing golden fruit
		Allele.treeCherry = new AlleleTreeSpecies("treeCherry", true, "Hill Cherry", prunus, "serrulata", 0xe691da, 0xe63e59, WorldGenCherry.class)
		.addFruitFamily(prunes).addFruitFamily(pomes).setBodyType(19);

		// Coniferous
		
		Allele.treeSpruce = new AlleleTreeSpecies("treeSpruce", false, "Red Spruce", picea, "abies", proxy.getFoliageColorPine(), 0x539d12, WorldGenSpruce.class)
		.setLeafIndices(49, 65, 49).setBodyType(2).setIsSecret();
		Allele.treeLarch = new AlleleTreeSpecies("treeLarch", true, "Mundane Larch", larix, "decidua", 0x698f90, 0x569896, WorldGenLarch.class)
		.setLeafIndices(49, 65, 49).setBodyType(16);
		Allele.treePine = new AlleleTreeSpecies("treePine", true, "Bull Pine", pinus, "sabiniana", 0xfeff8f, 0xffd98f, WorldGenPine.class)
		.setLeafIndices(49, 65, 49).setBodyType(25);
		
		Allele.treeSequioa = new AlleleTreeSpecies("treeSequioa", false, "Sequoia", sequoia, "sempervirens", 0x418e71, 0x569896, WorldGenSequoia.class)
		.setLeafIndices(49, 65, 49).setGirth(3).setBodyType(17);

		// Jungle
		Allele.treeJungle = new AlleleTreeSpecies("treeJungle", false, "Jungle", tropical, "tectona", proxy.getFoliageColorBasic(), 0x539d12, WorldGenJungle.class)
		.addFruitFamily(jungle).setLeafIndices(50, 66, 50).setBodyType(3);
		Allele.treeTeak = new AlleleTreeSpecies("treeTeak", true, "Teak", tectona, "grandis", 0xfeff8f, 0xffd98f, WorldGenTeak.class)
		.addFruitFamily(jungle).setLeafIndices(50, 66, 50).setBodyType(21);
		Allele.treeKapok = new AlleleTreeSpecies("treeKapok", true, "Kapok", ceiba, "pentandra", 0x89987b, 0x89aa9e, WorldGenKapok.class)
		.addFruitFamily(jungle).addFruitFamily(prunes).setLeafIndices(50, 66, 50).setBodyType(28);

		// Ebony
		Allele.treeEbony = new AlleleTreeSpecies("treeEbony", true, "Myrtle Ebony", diospyros, "pentamera", 0xa2d24a, 0xc4d24a, WorldGenEbony.class)
		.addFruitFamily(jungle).addFruitFamily(prunes).setGirth(3).setLeafIndices(50, 66, 50).setBodyType(29);
		
		// Diospyros mespiliformis, the Jackalberry (also known as African Ebony
		// The Gaub Tree, Malabar ebony, Black-and-white Ebony or Pale Moon Ebony (Diospyros malabarica) 
		// Diospyros fasciculosa, is a rainforest tree in the Ebony family. Usually seen as a medium sized tree, but it may grow to 30 metres tall.
		// http://en.wikipedia.org/wiki/Diospyros_ebenum
		// http://en.wikipedia.org/wiki/Diospyros_crassiflora - "The wood this particular tree produces is believed to be the blackest of all timber-producing Diospyros species"
		// Coromandel Ebony or East Indian Ebony (Diospyros melanoxylon) - " locally it is known as temburini or by its Hindi name tendu. In Orissa and Jharkhand it known as kendu."
		
		// Mahogany
		Allele.treeMahogany = new AlleleTreeSpecies("treeMahogony", true, "Yellow Meranti", shorea, "gibbosa", 0x8ab154, 0xa9b154, WorldGenMahogany.class)
		.addFruitFamily(jungle).setGirth(2).setLeafIndices(50, 66, 50).setBodyType(30);
		
		// 80+ meters tall:
		// Shorea argentifolia (also called Dark Red Meranti)
		// Shorea gibbosa (also called Yellow Meranti)
		// Shorea smithiana (also called Light Red Meranti)
		// Shorea superba
		
		// Malva
		Allele.treeAcacia = new AlleleTreeSpecies("treeAcacia", true, "Desert Acacia", acacia, "erioloba", 0x616101, 0xb3b302, WorldGenAcacia.class)
		.addFruitFamily(jungle).addFruitFamily(nux).setBodyType(23);
		Allele.treeBalsa = new AlleleTreeSpecies("treeBalsa", true, "Balsa", ochroma, "pyramidale", 0x59ac00, 0xfeff8f, WorldGenBalsa.class)
		.addFruitFamily(jungle).addFruitFamily(nux).setBodyType(18);
		Allele.treeWenge = new AlleleTreeSpecies("treeWenge", true, "Wenge", millettia, "laurentii", 0xada157, 0xad8a57, WorldGenWenge.class)
		.addFruitFamily(jungle).addFruitFamily(nux).setGirth(2).setBodyType(26);
		Allele.treeBaobab = new AlleleTreeSpecies("treeBaobab", true, "Grandidier's Baobab", adansonia, "digitata", 0xfeff8f, 0xffd98f, WorldGenBaobab.class)
		.addFruitFamily(jungle).addFruitFamily(nux).setGirth(3).setBodyType(27);

		// Willows
		Allele.treeWillow = new AlleleTreeSpecies("treeWillow", true, "White Willow", salix, "alba", 0xa3b8a5, 0xa3b850, WorldGenWillow.class)
		.addFruitFamily(nux).addFruitFamily(prunes).addFruitFamily(pomes).setLeafIndices(51, 67, 50).setBodyType(31);
		
		// Lauraceae
		Allele.treeSipiri = new AlleleTreeSpecies("treeSipiri", true, "Sipiri", chlorocardium, "rodiei", 0x678911, 0x79a175, WorldGenGreenheart.class)
		.addFruitFamily(jungle).setBodyType(32);
		
		// Unclassified
		Allele.treePalm = new AlleleTreeSpecies("treePalm", true, "Palm", null, "Cocos nucifera", 0xfeff8f, 0x539d12);
		Allele.treeBoojum = new AlleleTreeSpecies("treeBoojum", true, "Boojum", null, "Fouquieria columnaris", 0xfeff8f, 0x539d12);

		// FRUITS
		Allele.fruitNone = new AlleleFruit("fruitNone", new FruitProviderNone());
		Allele.fruitApple = new AlleleFruit("fruitApple", new FruitProviderRandom("apple", pomes, new ItemStack(Item.appleRed), 1.0f, 97).setColour(0xff2e2e));
		Allele.fruitCocoa = new AlleleFruit("fruitCocoa", new FruitProviderRipening("cocoa", jungle, new ItemStack(Item.dyePowder, 1, 3), 0.5f, 98).setColours(0xecdca5, 0xc4d24a), true);
		Allele.fruitChestnut = new AlleleFruit("fruitChestnut", new FruitProviderRipening("chestnut", nux, new ItemStack(ForestryItem.fruits, 1, EnumFruit.CHESTNUT.ordinal()), 1.0f, 96).setRipeningPeriod(6).setColours(0x7f333d, 0xc4d24a), true);
		Allele.fruitPalm = new AlleleFruit("fruitPalm", new FruitProviderNone());
		Allele.fruitWalnut = new AlleleFruit("fruitWalnut", new FruitProviderRipening("walnut", nux, new ItemStack(ForestryItem.fruits, 1, EnumFruit.WALNUT.ordinal()), 1.0f, 96).setRipeningPeriod(8).setColours(0xfba248, 0xc4d24a), true);
		Allele.fruitCherry = new AlleleFruit("fruitCherry", new FruitProviderRipening("cherry", prunes, new ItemStack(ForestryItem.fruits, 1, EnumFruit.CHERRY.ordinal()), 1.0f, 96).setColours(0xff2e2e, 0xc4d24a), true);

		// / TREES // GROWTH PROVIDER 1350 - 1399
		Allele.growthLightlevel = new AlleleGrowth("growthLightlevel", new GrowthProvider());
		Allele.growthAcacia = new AlleleGrowth("growthAcacia", new GrowthProvider());
		Allele.growthTropical = new AlleleGrowth("growthTropical", new GrowthProviderTropical());

		// / TREES // EFFECTS 1900 - 1999
		Allele.leavesNone = new AlleleLeafEffectNone("leavesNone");

	}

	private void registerTemplates() {
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getOakTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getBirchTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getSpruceTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getJungleTemplate());

		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getLimeTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getCherryTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getChestnutTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getWalnutTemplate());

		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getLarchTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getPineTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getSequoiaTemplate());
		
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getBalsaTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getAcaciaTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getWengeTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getBaobabTemplate());
		
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getTeakTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getKapokTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getEbonyTemplate());
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getMahoganyTemplate());
		
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getWillowTemplate());
		
		TreeManager.breedingManager.registerTreeTemplate(TreeTemplates.getSipiriTemplate());

		/*
		int rarity = 20;
		if (Config.dungeonLootRare) {
			rarity = 5;
		}

		for (ITree tree : BreedingManager.treeTemplates) {
			DungeonHooks.addDungeonLoot(TreeManager.treeInterface.getGermlingStack(tree, EnumGermlingType.SAPLING), rarity);
		}
		*/
	}

	private void registerErsatzGenomes() {
		AlleleManager.ersatzSpecimen.put(new ItemStack(Block.leaves.blockID, 1, 0), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getOakTemplate())));
		AlleleManager.ersatzSpecimen.put(new ItemStack(Block.leaves.blockID, 1, 1), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getSpruceTemplate())));
		AlleleManager.ersatzSpecimen.put(new ItemStack(Block.leaves.blockID, 1, 2), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getBirchTemplate())));
		AlleleManager.ersatzSpecimen.put(new ItemStack(Block.leaves.blockID, 1, 3), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getJungleTemplate())));
		
		AlleleManager.ersatzSaplings.put(new ItemStack(Block.sapling.blockID, 1, 0), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getOakTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Block.sapling.blockID, 1, 1), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getSpruceTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Block.sapling.blockID, 1, 2), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getBirchTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Block.sapling.blockID, 1, 3), new Tree(TreeTemplates.templateAsGenome(TreeTemplates.getJungleTemplate())));
	}
	
	private void createMutations() {
		
		// Decidious
		TreeTemplates.limeA = new Mutation(Allele.treeBirch, Allele.treeOak, TreeTemplates.getLimeTemplate(), 15);

		// Fructifera
		TreeTemplates.cherryA = new Mutation(Allele.treeLime, Allele.treeOak, TreeTemplates.getCherryTemplate(), 10);
		TreeTemplates.cherryB = new Mutation(Allele.treeLime, Allele.treeBirch, TreeTemplates.getCherryTemplate(), 10);

		// Nucifera
		TreeTemplates.walnutA = new Mutation(Allele.treeLime, Allele.treeCherry, TreeTemplates.getWalnutTemplate(), 10);
		TreeTemplates.chestnutA = new Mutation(Allele.treeWalnut, Allele.treeLime, TreeTemplates.getChestnutTemplate(), 10);
		TreeTemplates.chestnutB = new Mutation(Allele.treeWalnut, Allele.treeCherry, TreeTemplates.getChestnutTemplate(), 10);
		
		// Conifera
		TreeTemplates.larchA = new Mutation(Allele.treeSpruce, Allele.treeBirch, TreeTemplates.getLarchTemplate(), 15);
		TreeTemplates.larchB = new Mutation(Allele.treeSpruce, Allele.treeOak, TreeTemplates.getLarchTemplate(), 15);
		TreeTemplates.pineA = new Mutation(Allele.treeSpruce, Allele.treeLarch, TreeTemplates.getPineTemplate(), 10);
		TreeTemplates.sequoiaA = new Mutation(Allele.treeLarch, Allele.treePine, TreeTemplates.getSequoiaTemplate(), 5);

		// Tropical
		TreeTemplates.teakA = new Mutation(Allele.treeLime, Allele.treeJungle, TreeTemplates.getTeakTemplate(), 10);
		TreeTemplates.kapokA = new Mutation(Allele.treeJungle, Allele.treeTeak, TreeTemplates.getKapokTemplate(), 10);
		TreeTemplates.ebonyA = new Mutation(Allele.treeKapok, Allele.treeTeak, TreeTemplates.getEbonyTemplate(), 10);
		TreeTemplates.mahoganyA = new Mutation(Allele.treeKapok, Allele.treeEbony, TreeTemplates.getMahoganyTemplate(), 10);
		
		// Malva
		TreeTemplates.balsaA = new Mutation(Allele.treeTeak, Allele.treeLime, TreeTemplates.getBalsaTemplate(), 10);
		TreeTemplates.acaciaA = new Mutation(Allele.treeTeak, Allele.treeBalsa, TreeTemplates.getAcaciaTemplate(), 10);
		TreeTemplates.wengeA = new Mutation(Allele.treeAcacia, Allele.treeBalsa, TreeTemplates.getWengeTemplate(), 10);
		TreeTemplates.baobabA = new Mutation(Allele.treeBalsa, Allele.treeWenge, TreeTemplates.getBaobabTemplate(), 10);
		
		TreeTemplates.willowA = new Mutation(Allele.treeOak, Allele.treeBirch, TreeTemplates.getWillowTemplate(), 10)
		.setTemperatureRainfall(0.7f, 1.5f, 0.9f, 2.0f);
		TreeTemplates.willowB = new Mutation(Allele.treeOak, Allele.treeLime, TreeTemplates.getWillowTemplate(), 10)
		.setTemperatureRainfall(0.7f, 1.5f, 0.9f, 2.0f);
		TreeTemplates.willowC = new Mutation(Allele.treeLime, Allele.treeBirch, TreeTemplates.getWillowTemplate(), 10)
		.setTemperatureRainfall(0.7f, 1.5f, 0.9f, 2.0f);

		TreeTemplates.sipiriA = new Mutation(Allele.treeKapok, Allele.treeMahogany, TreeTemplates.getSipiriTemplate(), 10)
		.setTemperatureRainfall(0.9f, 1.9f, 0.9f, 2.0f);

	}
	
	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerArboriculture();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[] { new CommandSpawnTree(), new CommandSpawnForest() };
	}

}
