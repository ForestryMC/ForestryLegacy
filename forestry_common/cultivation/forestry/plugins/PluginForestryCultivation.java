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

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.cultivation.CropProviders;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.GadgetManager;
import forestry.cultivation.GuiHandlerCultivation;
import forestry.cultivation.PackagesCultivation;
import forestry.cultivation.gadgets.BlockFirSapling;
import forestry.cultivation.gadgets.BlockHarvester;
import forestry.cultivation.gadgets.BlockPlanter;
import forestry.cultivation.gadgets.BlockSaplings;
import forestry.cultivation.providers.CropProviderCacti;
import forestry.cultivation.providers.CropProviderCarrots;
import forestry.cultivation.providers.CropProviderHerbaceous;
import forestry.cultivation.providers.CropProviderMushroom;
import forestry.cultivation.providers.CropProviderNetherwart;
import forestry.cultivation.providers.CropProviderPotatoes;
import forestry.cultivation.providers.CropProviderReeds;
import forestry.cultivation.providers.CropProviderSapling;
import forestry.cultivation.providers.CropProviderSeeds;
import forestry.cultivation.proxy.ProxyCultivation;

@PluginInfo(pluginID = "Cultivation", name = "Cultivation", author = "SirSengir", url = Defaults.URL, description = "Adds automatic farms and harvesters for a wide variety of products.")
public class PluginForestryCultivation extends NativePlugin {

	@SidedProxy(clientSide = "forestry.cultivation.proxy.ClientProxyCultivation", serverSide = "forestry.cultivation.proxy.ProxyCultivation")
	public static ProxyCultivation proxy;

	@Override
	public boolean isAvailable() {
		return !Config.disableCultivation;
	}

	@Override
	public String getDescription() {
		return "Cultivation";
	}

	public void preInit() {
		super.preInit();

		ForestryBlock.firsapling = new BlockFirSapling(Config.getOrCreateBlockIdProperty("firsapling", Defaults.ID_BLOCK_FIRSAPLING), 15)
				.setBlockName("firsapling");
		ForestryBlock.sapling = new BlockSaplings(Config.getOrCreateBlockIdProperty("sapling", Defaults.ID_BLOCK_SAPLING), 0);
		ForestryBlock.planter = (new BlockPlanter(Config.getOrCreateBlockIdProperty("planter", Defaults.ID_BLOCK_PLANTER))).setBlockName("planter");
		ForestryBlock.harvester = (new BlockHarvester(Config.getOrCreateBlockIdProperty("harvester", Defaults.ID_BLOCK_HARVESTER))).setBlockName("harvester");

		// Proxies.common.registerBlock(ForestryBlock.firsapling, ItemFirSapling.class);
	}

	@Override
	public void doInit() {
		super.doInit();
		registerCropProviders();

		// Register Bonemeal handlers
		// FIXME: Reimplement bone meal handlers
		// MinecraftForge.registerBonemealHandler((BlockFirSapling) ForestryBlock.firsapling);
		// MinecraftForge.registerBonemealHandler((BlockSaplings) ForestryBlock.sapling);
		// MinecraftForge.registerBonemealHandler((BlockMushroom) ForestryBlock.mushroom);

		proxy.registerPlanterTE();
		proxy.registerHarvesterTE();
	}

	@Override
	protected void registerPackages() {
		// PLANTER BASED TILE ENTITIES
		GadgetManager.registerPlanterPackage(0, PackagesCultivation.getArboretumPackage());
		GadgetManager.registerPlanterPackage(1, PackagesCultivation.getFarmPackage());
		GadgetManager.registerPlanterPackage(2, PackagesCultivation.getPlantationPackage());
		GadgetManager.registerPlanterPackage(3, PackagesCultivation.getPumpkinFarmPackage());
		GadgetManager.registerPlanterPackage(4, PackagesCultivation.getBogPeatPackage());
		GadgetManager.registerPlanterPackage(5, PackagesCultivation.getMushroomFarmPackage());
		GadgetManager.registerPlanterPackage(6, PackagesCultivation.getNetherFarmPackage());

		// HARVESTER BASED TILE ENTITIES
		GadgetManager.registerHarvesterPackage(0, PackagesCultivation.getLoggerPackage());
		GadgetManager.registerHarvesterPackage(1, PackagesCultivation.getCombinePackage());
		GadgetManager.registerHarvesterPackage(2, PackagesCultivation.getRubberHarvesterPackage());
		GadgetManager.registerHarvesterPackage(3, PackagesCultivation.getPumpkinHarvesterPackage());
		GadgetManager.registerHarvesterPackage(4, PackagesCultivation.getTurbaryPackage());
		GadgetManager.registerHarvesterPackage(5, PackagesCultivation.getCactiHarvesterPackage());
		GadgetManager.registerHarvesterPackage(6, PackagesCultivation.getMushroomPickerPackage());
		GadgetManager.registerHarvesterPackage(7, PackagesCultivation.getReedHarvesterPackage());
		GadgetManager.registerHarvesterPackage(8, PackagesCultivation.getNetherCombinePackage());

		// MILL BASED TILE ENTITIES
		GadgetManager.registerMillPackage(0, PackagesCultivation.getForesterPackage());
		GadgetManager.registerMillPackage(2, PackagesCultivation.getTreetapPackage());
	}

	@Override
	protected void registerItems() {
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerRecipes() {
	}

	private void registerCropProviders() {
		CropProviders.arborealCrops.add(new CropProviderSapling());
		CropProviders.cerealCrops.add(new CropProviderSeeds());
		CropProviders.cerealCrops.add(new CropProviderPotatoes());
		CropProviders.cerealCrops.add(new CropProviderCarrots());
		CropProviders.infernalCrops.add(new CropProviderNetherwart());
		CropProviders.herbaceousCrops.add(new CropProviderHerbaceous());
		CropProviders.succulentCrops.add(new CropProviderCacti());
		CropProviders.poaleCrops.add(new CropProviderReeds());
		CropProviders.fungalCrops.add(new CropProviderMushroom());
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerCultivation();
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
