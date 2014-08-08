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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.GadgetManager;
import forestry.core.proxy.Proxies;
import forestry.factory.GuiHandlerFactory;
import forestry.factory.PackagesFactory;
import forestry.factory.gadgets.MachineBottler;
import forestry.factory.gadgets.MachineCarpenter;
import forestry.factory.gadgets.MachineCentrifuge;
import forestry.factory.gadgets.MachineFabricator;
import forestry.factory.gadgets.MachineFermenter;
import forestry.factory.gadgets.MachineMoistener;
import forestry.factory.gadgets.MachineSqueezer;
import forestry.factory.gadgets.MachineStill;
import forestry.factory.recipes.CraftGuideIntegration;

@PluginInfo(pluginID = "Factory", name = "Factory", author = "SirSengir", url = Defaults.URL, description = "Adds a wide variety of machines to craft, produce and process products.")
public class PluginForestryFactory extends NativePlugin {

	@Override
	public boolean isAvailable() {
		return !Config.disableFactory;
	}

	@Override
	public String getDescription() {
		return "Factory";
	}

	@Override
	public void preInit() {
		super.preInit();

		// Init carpenter manager
		RecipeManagers.carpenterManager = new MachineCarpenter.RecipeManager();
		// Init moistener manager
		RecipeManagers.moistenerManager = new MachineMoistener.RecipeManager();
		// Init centrifuge manager
		RecipeManagers.centrifugeManager = new MachineCentrifuge.RecipeManager();
		// Init still manager
		RecipeManagers.stillManager = new MachineStill.RecipeManager();
		// Init bottler manager
		RecipeManagers.bottlerManager = new MachineBottler.RecipeManager();
		// Init squeezer manager
		RecipeManagers.squeezerManager = new MachineSqueezer.RecipeManager();
		// Init fermenter manager
		RecipeManagers.fermenterManager = new MachineFermenter.RecipeManager();
		// Init fabricator manager
		RecipeManagers.fabricatorManager = new MachineFabricator.RecipeManager();
	}

	@Override
	public void postInit() {
		super.postInit();

		MachineCarpenter.initialize();
		MachineMoistener.initialize();
		MachineStill.initialize();
		MachineBottler.initialize();
		MachineSqueezer.initialize();
		MachineFermenter.initialize();
		MachineFabricator.initialize();

		if(Proxies.common.isModLoaded("craftguide"))
			CraftGuideIntegration.register();
		else
			Proxies.log.info("Skipping CraftGuide integration.");
	}

	@Override
	protected void registerPackages() {
		// MACHINE BASED TILE ENTITIES
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_FERMENTER, PackagesFactory.getFermenterPackage());
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_STILL, PackagesFactory.getStillPackage());
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_BOTTLER, PackagesFactory.getBottlerPackage());
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_RAINTANK, PackagesFactory.getRaintankPackage());
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_MOISTENER, PackagesFactory.getMoistenerPackage());
		
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_CARPENTER, PackagesFactory.getCarpenterPackage());
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_CENTRIFUGE, PackagesFactory.getCentrifugePackage());
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_SQUEEZER, PackagesFactory.getSqueezerPackage());
		GadgetManager.registerMachinePackage(Defaults.ID_PACKAGE_MACHINE_FABRICATOR, PackagesFactory.getFabriactorPackage());

		// MILL BASED TILE ENTITIES
		GadgetManager.registerMillPackage(Defaults.ID_PACKAGE_MILL_RAINMAKER, PackagesFactory.getRainmakerPackage());
	}

	@Override
	protected void registerItems() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerBackpackItems() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerRecipes() {

		// / FABRICATOR
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 0), new Object[] {
				" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), "ingotCopper" });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 1), new Object[] {
				" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), "ingotTin" });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 2), new Object[] {
				" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), "ingotBronze" });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 3), new Object[] {
				" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), Item.ingotIron });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 4), new Object[] {
				" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), Item.ingotGold });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 5), new Object[] {
				" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), Item.diamond });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 6), new Object[] {
			" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), Block.obsidian });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 7), new Object[] {
			" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), Item.blazePowder });
		if(PluginIC2.rubber != null)
			RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 8), new Object[] {
				" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), PluginIC2.rubber });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 9), new Object[] {
			" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), Item.emerald });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 10), new Object[] {
			" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), ForestryItem.apatite });
		RecipeManagers.fabricatorManager.addRecipe(null, new LiquidStack(ForestryItem.liquidGlass, 500), new ItemStack(ForestryItem.tubes, 4, 11), new Object[] {
			" X ", "#X#", "XXX", Character.valueOf('#'), Item.redstone, Character.valueOf('X'), new ItemStack(Item.dyePowder, 1, 4) });

		String[] dyes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime",
				"dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };

		if (ForestryItem.propolis != null) {
			for (int i = 0; i < 16; i++) {
				RecipeManagers.fabricatorManager.addRecipe(new ItemStack(ForestryItem.waxCast, 1, -1), new LiquidStack(ForestryItem.liquidGlass,
						Defaults.BUCKET_VOLUME), new ItemStack(ForestryBlock.glass, 1, 15 - i), new Object[] { "#", "X", Character.valueOf('#'), dyes[i],
						Character.valueOf('X'), new ItemStack(ForestryItem.propolis, 1, -1) });
			}
		}

		// / SQUEEZER
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(Item.appleRed) }, new LiquidStack(ForestryItem.liquidJuice, 200),
				new ItemStack(ForestryItem.mulch), 40);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(Item.seeds) }, new LiquidStack(ForestryItem.liquidSeedOil, GameMode
				.getGameMode().getSqueezedLiquidPerSeed()));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(Item.pumpkinSeeds) }, new LiquidStack(ForestryItem.liquidSeedOil, GameMode
				.getGameMode().getSqueezedLiquidPerSeed()));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(Item.melonSeeds) }, new LiquidStack(ForestryItem.liquidSeedOil, GameMode
				.getGameMode().getSqueezedLiquidPerSeed()));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(ForestryItem.phosphor, 2), new ItemStack(Block.cobblestone) },
				new LiquidStack(Block.lavaStill, 1600));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(Block.cactus) }, new LiquidStack(Block.waterStill, 500));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { new ItemStack(Item.snowball), new ItemStack(ForestryItem.craftingMaterial, 4, 5) },
				new LiquidStack(ForestryItem.liquidIce, 4000));

		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(50, new LiquidStack(ForestryItem.liquidSeedOil, 250), null,
				new ItemStack(ForestryItem.impregnatedCasing, 1), new Object[] { "###", "# #", "###", Character.valueOf('#'), "logWood" });
	}

	@Override
	protected void registerCrates() {
		// TODO Auto-generated method stub

	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerFactory();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}	

}
