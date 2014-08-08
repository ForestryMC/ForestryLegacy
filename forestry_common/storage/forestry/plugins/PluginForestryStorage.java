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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.IPickupHandler;
import forestry.api.core.IResupplyHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.proxy.Proxies;
import forestry.storage.BackpackDefinition;
import forestry.storage.BackpackHelper;
import forestry.storage.GuiHandlerStorage;
import forestry.storage.PickupHandlerStorage;
import forestry.storage.ResupplyHandler;
import forestry.storage.items.ItemApiaristBackpack;
import forestry.storage.items.ItemBackpack;

@PluginInfo(pluginID = "Storage", name = "Storage", author = "SirSengir", url = Defaults.URL, description = "Adds backpacks and crates.")
public class PluginForestryStorage extends NativePlugin implements IOreDictionaryHandler {

	private ArrayList<ItemStack> minerItems;
	private ArrayList<ItemStack> diggerItems;
	private ArrayList<ItemStack> foresterItems;
	private ArrayList<ItemStack> hunterItems;
	private ArrayList<ItemStack> adventurerItems;
	private ArrayList<ItemStack> builderItems;

	static String CONFIG_CATEGORY = "backpacks";
	Configuration config;

	@Override
	public boolean isAvailable() {
		return !Config.disableStorage;
	}

	@Override
	public String getDescription() {
		return "Storage";
	}

	@Override
	public void preInit() {
		BackpackManager.backpackInterface = new BackpackHelper();
		super.preInit();
		createBackpackArrays();
	}

	@Override
	public void postInit() {
		super.postInit();

		config = new Configuration();

		Property backpackConf = config.get("backpacks.miner.items", CONFIG_CATEGORY, "");
		backpackConf.Comment = "add additional blocks and items for the miner's backpack here in the format id:meta. separate blocks and items using ';'";
		parseBackpackItems("Miner's Backpack", backpackConf.Value, BackpackManager.definitions.get("miner"));
		backpackConf = config.get("backpacks.digger.items", CONFIG_CATEGORY, "");
		backpackConf.Comment = "add additional blocks and items for the digger's backpack here in the format id:meta. separate blocks and items using ';'";
		parseBackpackItems("Digger's Backpack", backpackConf.Value, BackpackManager.definitions.get("digger"));
		backpackConf = config.get("backpacks.forester.items", CONFIG_CATEGORY, "");
		backpackConf.Comment = "add additional blocks and items for the forester's backpack here in the format id:meta. separate blocks and items using ';'";
		parseBackpackItems("Forester's Backpack", backpackConf.Value, BackpackManager.definitions.get("forester"));
		backpackConf = config.get("backpacks.hunter.items", CONFIG_CATEGORY, "");
		backpackConf.Comment = "add additional blocks and items for the hunter's backpack here in the format id:meta. separate blocks and items using ';'";
		parseBackpackItems("Hunter's Backpack", backpackConf.Value, BackpackManager.definitions.get("hunter"));
		backpackConf = config.get("backpacks.adventurer.items", CONFIG_CATEGORY, "");
		backpackConf.Comment = "add blocks and items for the adventurer's backpack here in the format id:meta. separate blocks and items using ';'";
		parseBackpackItems("Adventurer's Backpack", backpackConf.Value, BackpackManager.definitions.get("adventurer"));
		backpackConf = config.get("backpacks.builder.items", CONFIG_CATEGORY, "");
		backpackConf.Comment = "add blocks and items for the builder's backpack here in the format id:meta. separate blocks and items using ';'";
		parseBackpackItems("Builder's Backpack", backpackConf.Value, BackpackManager.definitions.get("builder"));

		config.save();

	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if(message.key.equals("add-backpack-items")) {
			String[] tokens = message.getStringValue().split("@");
			if(tokens.length != 2) {
				Logger.getLogger("Forestry").log(Level.INFO, String.format("Received an invalid 'add-backpack-items' request %s from mod %s", message.getStringValue(), message.getSender()));
				return true;
			}
			
			if(!BackpackManager.definitions.containsKey(tokens[0])) {
				Logger.getLogger("Forestry").log(Level.INFO, String.format("Received an invalid 'add-backpack-items' request %s from mod %s for non-existent backpack %s.", message.getStringValue(), message.getSender(), tokens[0]));
				return true;
			}
			
			parseBackpackItems(tokens[0] + "'s Backpack", tokens[1], BackpackManager.definitions.get(tokens[0]));
			
			return true;
		}
		return false;
	}
	
	@Override
	public IPickupHandler getPickupHandler() {
		return new PickupHandlerStorage();
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerStorage();
	}

	@Override
	public IResupplyHandler getResupplyHandler() {
		return new ResupplyHandler();
	}

	@Override
	protected void registerPackages() {
	}

	@Override
	protected void registerItems() {
		// / BACKPACKS
		ForestryItem.apiaristBackpack = (new ItemApiaristBackpack(Config.getOrCreateItemIdProperty("apiaristBag", Defaults.ID_ITEM_APIARIST_BACKPACK)))
				.setItemName("apiaristBag");
		BackpackDefinition definition = (BackpackDefinition)((ItemBackpack)ForestryItem.apiaristBackpack).getDefinition();
		BackpackManager.definitions.put(definition.getKey(), definition);
		
		definition = new BackpackDefinition(
				Config.getOrCreateItemIdProperty("minerBag", Defaults.ID_ITEM_MINER_BACKPACK),
				Config.getOrCreateItemIdProperty("minerBagT2", Defaults.ID_ITEM_MINER_BACKPACK_T2),
				"miner", 0x36187d).setValidItems(BackpackManager.backpackItems[0]);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.minerBackpack = BackpackManager.backpackInterface.addBackpack(definition.idT1, definition, EnumBackpackType.T1);
		ForestryItem.minerBackpackT2 = BackpackManager.backpackInterface.addBackpack(definition.idT2, definition, EnumBackpackType.T2);
		
		definition = new BackpackDefinition(
				Config.getOrCreateItemIdProperty("diggerBag", Defaults.ID_ITEM_DIGGER_BACKPACK),
				Config.getOrCreateItemIdProperty("diggerBagT2", Defaults.ID_ITEM_DIGGER_BACKPACK_T2),
				"digger", 0x363cc5).setValidItems(BackpackManager.backpackItems[1]);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.diggerBackpack = BackpackManager.backpackInterface.addBackpack(definition.idT1, definition, EnumBackpackType.T1);
		ForestryItem.diggerBackpackT2 = BackpackManager.backpackInterface.addBackpack(definition.idT2, definition, EnumBackpackType.T2);

		definition = new BackpackDefinition(
				Config.getOrCreateItemIdProperty("foresterBag", Defaults.ID_ITEM_FORESTER_BACKPACK),
				Config.getOrCreateItemIdProperty("foresterBagT2", Defaults.ID_ITEM_FORESTER_BACKPACK_T2),
				"forester", 0x347427).setValidItems(BackpackManager.backpackItems[2]);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.foresterBackpack = BackpackManager.backpackInterface.addBackpack(definition.idT1, definition, EnumBackpackType.T1);
		ForestryItem.foresterBackpackT2 = BackpackManager.backpackInterface.addBackpack(definition.idT2, definition, EnumBackpackType.T2);

		definition = new BackpackDefinition(
				Config.getOrCreateItemIdProperty("hunterBag", Defaults.ID_ITEM_HUNTER_BACKPACK),
				Config.getOrCreateItemIdProperty("hunterBagT2", Defaults.ID_ITEM_HUNTER_BACKPACK_T2),
				"hunter", 0x412215).setValidItems(BackpackManager.backpackItems[3]);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.hunterBackpack = BackpackManager.backpackInterface.addBackpack(definition.idT1, definition, EnumBackpackType.T1);
		ForestryItem.hunterBackpackT2 = BackpackManager.backpackInterface.addBackpack(definition.idT2, definition, EnumBackpackType.T2);

		definition = new BackpackDefinition(
				Config.getOrCreateItemIdProperty("adventurerBackpack", Defaults.ID_ITEM_ADVENTURER_BACKPACK),
				Config.getOrCreateItemIdProperty("adventurerBackpackT2", Defaults.ID_ITEM_ADVENTURER_BACKPACK_T2),
				"adventurer", 0x7fb8c2).setValidItems(BackpackManager.backpackItems[4]);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.adventurerBackpack = BackpackManager.backpackInterface.addBackpack(definition.idT1, definition, EnumBackpackType.T1);
		ForestryItem.adventurerBackpackT2 = BackpackManager.backpackInterface.addBackpack(definition.idT2, definition, EnumBackpackType.T2);

		definition = new BackpackDefinition(
				Config.getOrCreateItemIdProperty("builderBackpack", Defaults.ID_ITEM_BUILDER_BACKPACK),
				Config.getOrCreateItemIdProperty("builderBackpackT2", Defaults.ID_ITEM_BUILDER_BACKPACK_T2),
				"builder", 0xdd3a3a).setValidItems(BackpackManager.backpackItems[5]);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.builderBackpack = BackpackManager.backpackInterface.addBackpack(definition.idT1, definition, EnumBackpackType.T1);
		ForestryItem.builderBackpackT2 = BackpackManager.backpackInterface.addBackpack(definition.idT2, definition, EnumBackpackType.T2);

	}

	@Override
	protected void registerBackpackItems() {

		// [0] Set valid items in miner's backpack
		minerItems.add(new ItemStack(Block.obsidian));
		minerItems.add(new ItemStack(Block.oreCoal));
		minerItems.add(new ItemStack(Item.coal));
		minerItems.add(new ItemStack(Block.oreDiamond));
		minerItems.add(new ItemStack(Item.diamond));
		minerItems.add(new ItemStack(Block.oreGold));
		minerItems.add(new ItemStack(Item.ingotGold));
		minerItems.add(new ItemStack(Block.oreIron));
		minerItems.add(new ItemStack(Item.ingotIron));
		minerItems.add(new ItemStack(Block.oreLapis));
		minerItems.add(new ItemStack(Block.oreRedstone));
		minerItems.add(new ItemStack(Item.redstone));
		minerItems.add(new ItemStack(Item.dyePowder, 1, 4));
		minerItems.add(new ItemStack(Item.lightStoneDust));
		minerItems.add(new ItemStack(Item.emerald));
		minerItems.add(new ItemStack(ForestryItem.bronzePickaxe));
		minerItems.add(new ItemStack(ForestryItem.kitPickaxe));
		minerItems.add(new ItemStack(ForestryItem.brokenBronzePickaxe));

		// [1] Set valid items in digger's backpack
		diggerItems.add(new ItemStack(Block.dirt));
		diggerItems.add(new ItemStack(Block.cobblestone));
		diggerItems.add(new ItemStack(Block.sand));
		diggerItems.add(new ItemStack(Block.sandStone));
		diggerItems.add(new ItemStack(Block.gravel));
		diggerItems.add(new ItemStack(Item.flint));
		diggerItems.add(new ItemStack(Block.netherrack));
		diggerItems.add(new ItemStack(Item.clay));
		diggerItems.add(new ItemStack(Block.slowSand));
		diggerItems.add(new ItemStack(ForestryItem.bronzeShovel));
		diggerItems.add(new ItemStack(ForestryItem.kitShovel));
		diggerItems.add(new ItemStack(ForestryItem.brokenBronzeShovel));

		// [2] Set valid items in forester's backpack
		foresterItems.add(new ItemStack(Block.sapling, 1, -1));
		foresterItems.add(new ItemStack(Block.mushroomRed));
		foresterItems.add(new ItemStack(Block.mushroomBrown));
		foresterItems.add(new ItemStack(Block.wood, 1, -1));
		foresterItems.add(new ItemStack(Item.seeds));
		foresterItems.add(new ItemStack(Block.plantRed));
		foresterItems.add(new ItemStack(Block.plantYellow));
		foresterItems.add(new ItemStack(Block.leaves, 1, -1));
		foresterItems.add(new ItemStack(Block.cactus));
		foresterItems.add(new ItemStack(Block.tallGrass, 1, -1));
		foresterItems.add(new ItemStack(Block.vine));
		foresterItems.add(new ItemStack(Item.appleRed));
		foresterItems.add(new ItemStack(Item.appleGold));
		foresterItems.add(new ItemStack(Item.netherStalkSeeds));
		foresterItems.add(new ItemStack(Item.pumpkinSeeds));
		foresterItems.add(new ItemStack(Item.melonSeeds));
		foresterItems.add(new ItemStack(ForestryBlock.saplingGE, 1));

		// [3] Set valid items in hunter's backpack
		hunterItems.add(new ItemStack(Item.feather));
		hunterItems.add(new ItemStack(Item.gunpowder));
		hunterItems.add(new ItemStack(Item.blazePowder));
		hunterItems.add(new ItemStack(Item.blazeRod));
		hunterItems.add(new ItemStack(Item.bone));
		hunterItems.add(new ItemStack(Item.silk));
		hunterItems.add(new ItemStack(Item.rottenFlesh));
		hunterItems.add(new ItemStack(Item.ghastTear));
		hunterItems.add(new ItemStack(Item.goldNugget));
		hunterItems.add(new ItemStack(Item.arrow));
		hunterItems.add(new ItemStack(Item.porkRaw));
		hunterItems.add(new ItemStack(Item.porkCooked));
		hunterItems.add(new ItemStack(Item.beefRaw));
		hunterItems.add(new ItemStack(Item.beefCooked));
		hunterItems.add(new ItemStack(Item.chickenRaw));
		hunterItems.add(new ItemStack(Item.chickenCooked));
		hunterItems.add(new ItemStack(Item.leather));
		hunterItems.add(new ItemStack(Item.egg));
		hunterItems.add(new ItemStack(Item.enderPearl));
		hunterItems.add(new ItemStack(Item.spiderEye));
		hunterItems.add(new ItemStack(Item.fermentedSpiderEye));
		hunterItems.add(new ItemStack(Item.slimeBall));
		hunterItems.add(new ItemStack(Item.dyePowder, 1, 0));
		hunterItems.add(new ItemStack(Block.cloth));
		hunterItems.add(new ItemStack(Block.cloth, 1, -1));
		hunterItems.add(new ItemStack(Item.eyeOfEnder));
		hunterItems.add(new ItemStack(Item.magmaCream));
		hunterItems.add(new ItemStack(Item.speckledMelon));
		hunterItems.add(new ItemStack(Item.fishRaw));
		hunterItems.add(new ItemStack(Item.fishCooked));

		// [4] Set valid items in adventurer's backpack

		// [5] Set valid items in builder's backpack
		builderItems.add(new ItemStack(Block.torchWood));
		builderItems.add(new ItemStack(Block.stoneBrick, 1, -1));
		builderItems.add(new ItemStack(Block.stone));
		builderItems.add(new ItemStack(Block.brick));
		builderItems.add(new ItemStack(Block.planks, 1, -1));
		builderItems.add(new ItemStack(Block.netherBrick));
		builderItems.add(new ItemStack(Block.netherFence));
		builderItems.add(new ItemStack(Block.stairCompactCobblestone));
		builderItems.add(new ItemStack(Block.stairCompactPlanks));
		builderItems.add(new ItemStack(Block.stairsBrick));
		builderItems.add(new ItemStack(Block.stairsNetherBrick));
		builderItems.add(new ItemStack(Block.stairsStoneBrickSmooth));
		builderItems.add(new ItemStack(Block.glass));
		builderItems.add(new ItemStack(Block.thinGlass));
		builderItems.add(new ItemStack(Block.fence));
		builderItems.add(new ItemStack(Block.fenceGate));
		builderItems.add(new ItemStack(Block.fenceIron));
	}

	@Override
	protected void registerRecipes() {

		// Apiarist's Backpack
		Proxies.common.addRecipe(new ItemStack(ForestryItem.apiaristBackpack), new Object[] { "X#X", "VYV", "X#X", Character.valueOf('#'), Block.cloth,
				Character.valueOf('X'), Item.silk, Character.valueOf('V'), "stickWood", Character.valueOf('Y'), new ItemStack(ForestryBlock.mill, 1, 3) });
		// Miner's Backpack
		Proxies.common.addRecipe(new ItemStack(ForestryItem.minerBackpack),
				new Object[] { "X#X", "VYV", "X#X", Character.valueOf('#'), Block.cloth, Character.valueOf('X'), Item.silk, Character.valueOf('V'),
						Item.ingotIron, Character.valueOf('Y'), Block.chest });
		// Digger's Backpack
		Proxies.common.addRecipe(new ItemStack(ForestryItem.diggerBackpack),
				new Object[] { "X#X", "VYV", "X#X", Character.valueOf('#'), Block.cloth, Character.valueOf('X'), Item.silk, Character.valueOf('V'),
						Block.stone, Character.valueOf('Y'), Block.chest });
		// Forester's Backpack
		Proxies.common.addRecipe(new ItemStack(ForestryItem.foresterBackpack), new Object[] { "X#X", "VYV", "X#X", Character.valueOf('#'), Block.cloth,
				Character.valueOf('X'), Item.silk, Character.valueOf('V'), "logWood", Character.valueOf('Y'), Block.chest });
		// Hunter's Backpack
		Proxies.common.addRecipe(new ItemStack(ForestryItem.hunterBackpack),
				new Object[] { "X#X", "VYV", "X#X", Character.valueOf('#'), Block.cloth, Character.valueOf('X'), Item.silk, Character.valueOf('V'),
						Item.feather, Character.valueOf('Y'), Block.chest });
		// Adventurer's Backpack
		Proxies.common.addRecipe(new ItemStack(ForestryItem.adventurerBackpack), new Object[] { "X#X", "VYV", "X#X", Character.valueOf('#'), Block.cloth,
				Character.valueOf('X'), Item.silk, Character.valueOf('V'), Item.bone, Character.valueOf('Y'), Block.chest });
		// Builder's Backpack
		Proxies.common.addRecipe(new ItemStack(ForestryItem.builderBackpack), new Object[] { "X#X", "VYV", "X#X", Character.valueOf('#'), Block.cloth,
				Character.valueOf('X'), Item.silk, Character.valueOf('V'), Item.clay, Character.valueOf('Y'), Block.chest });

		// / CARPENTER
		// / BACKPACKS T2
		RecipeManagers.carpenterManager.addRecipe(200, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.minerBackpackT2),
				new Object[] { "WXW", "WTW", "WWW", Character.valueOf('X'), Item.diamond, Character.valueOf('W'),
						new ItemStack(ForestryItem.craftingMaterial, 1, 3), Character.valueOf('T'), ForestryItem.minerBackpack });
		RecipeManagers.carpenterManager.addRecipe(200, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.diggerBackpackT2),
				new Object[] { "WXW", "WTW", "WWW", Character.valueOf('X'), Item.diamond, Character.valueOf('W'),
						new ItemStack(ForestryItem.craftingMaterial, 1, 3), Character.valueOf('T'), ForestryItem.diggerBackpack });
		RecipeManagers.carpenterManager.addRecipe(200, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.foresterBackpackT2),
				new Object[] { "WXW", "WTW", "WWW", Character.valueOf('X'), Item.diamond, Character.valueOf('W'),
						new ItemStack(ForestryItem.craftingMaterial, 1, 3), Character.valueOf('T'), ForestryItem.foresterBackpack });
		RecipeManagers.carpenterManager.addRecipe(200, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.hunterBackpackT2),
				new Object[] { "WXW", "WTW", "WWW", Character.valueOf('X'), Item.diamond, Character.valueOf('W'),
						new ItemStack(ForestryItem.craftingMaterial, 1, 3), Character.valueOf('T'), ForestryItem.hunterBackpack });
		RecipeManagers.carpenterManager.addRecipe(200, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.adventurerBackpackT2),
				new Object[] { "WXW", "WTW", "WWW", Character.valueOf('X'), Item.diamond, Character.valueOf('W'),
						new ItemStack(ForestryItem.craftingMaterial, 1, 3), Character.valueOf('T'), ForestryItem.adventurerBackpack });
		RecipeManagers.carpenterManager.addRecipe(200, new LiquidStack(Block.waterStill.blockID, 1000), null, new ItemStack(ForestryItem.builderBackpackT2),
				new Object[] { "WXW", "WTW", "WWW", Character.valueOf('X'), Item.diamond, Character.valueOf('W'),
						new ItemStack(ForestryItem.craftingMaterial, 1, 3), Character.valueOf('T'), ForestryItem.builderBackpack });

	}

	private void createBackpackArrays() {

		if (BackpackManager.backpackItems != null)
			return;

		BackpackManager.backpackItems = new ArrayList[6];

		minerItems = new ArrayList<ItemStack>();
		BackpackManager.backpackItems[0] = minerItems;

		diggerItems = new ArrayList<ItemStack>();
		BackpackManager.backpackItems[1] = diggerItems;

		foresterItems = new ArrayList<ItemStack>();
		BackpackManager.backpackItems[2] = foresterItems;

		hunterItems = new ArrayList<ItemStack>();
		BackpackManager.backpackItems[3] = hunterItems;

		adventurerItems = new ArrayList<ItemStack>();
		BackpackManager.backpackItems[4] = adventurerItems;

		builderItems = new ArrayList<ItemStack>();
		BackpackManager.backpackItems[5] = builderItems;

	}

	@Override
	protected void registerCrates() {
	}

	private static void parseBackpackItems(String backpackIdent, String list, IBackpackDefinition target) {
		String[] items = list.split("[;]+");

		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			String[] ident = item.split("[:]+");
			int id = 0;
			int meta = 0;
			if (ident.length > 1) {
				id = Integer.parseInt(ident[0].trim());
				meta = Integer.parseInt(ident[1].trim());
			} else {
				id = Integer.parseInt(ident[0].trim());
			}

			if (id > 0) {
				if ((id < Block.blocksList.length && Block.blocksList[id] != null) || Item.itemsList[id] != null) {
					FMLCommonHandler.instance().getFMLLogger().finer("Adding block/item of (" + id + ":" + meta + ") to " + backpackIdent);
					target.addValidItem(new ItemStack(id, 1, meta));
				} else {
					FMLCommonHandler.instance().getFMLLogger()
							.warning("Failed to add block/item of (" + id + ":" + meta + ") to " + backpackIdent + " since it was null.");
				}
			}
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return this;
	}

	@Override
	public void onOreRegistration(String name, ItemStack ore) {

		createBackpackArrays();

		if (name.startsWith("ingot")) {
			minerItems.add(ore);
		} else if (name.startsWith("ore")) {
			minerItems.add(ore);
		} else if (name.startsWith("gem")) {
			minerItems.add(ore);
		} else if (name.startsWith("dust")) {
			minerItems.add(ore);
		} else if (name.matches("dropUranium")) {
			minerItems.add(ore);
		} else if (name.equals("treeLeaves") || name.equals("treeSapling") || name.equals("logWood")) {
			foresterItems.add(ore);
		} else if (name.equals("stairWood") || name.equals("plankWood") || name.equals("slabWood")) {
			builderItems.add(ore);
		} else if (name.startsWith("wood")) {
			foresterItems.add(ore);
		}
	}

}
