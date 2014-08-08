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
package forestry.cultivation;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.MachinePackage;
import forestry.core.proxy.Proxies;
import forestry.core.utils.CraftingIngredients;
import forestry.core.utils.EnergyConfiguration;
import forestry.core.utils.StructureBlueprint;
import forestry.core.utils.TextureDescription;
import forestry.cultivation.gadgets.MillForester;
import forestry.cultivation.gadgets.MillTreetap;
import forestry.cultivation.harvesters.HarvesterCacti;
import forestry.cultivation.harvesters.HarvesterHerbaceous;
import forestry.cultivation.harvesters.HarvesterMushroom;
import forestry.cultivation.harvesters.HarvesterNetherwart;
import forestry.cultivation.harvesters.HarvesterPeat;
import forestry.cultivation.harvesters.HarvesterReeds;
import forestry.cultivation.harvesters.HarvesterRubber;
import forestry.cultivation.harvesters.HarvesterSapling;
import forestry.cultivation.harvesters.HarvesterSeeds;
import forestry.cultivation.planters.PlanterBog;
import forestry.cultivation.planters.PlanterHerbaceous;
import forestry.cultivation.planters.PlanterMushroom;
import forestry.cultivation.planters.PlanterNetherwarts;
import forestry.cultivation.planters.PlanterRubber;
import forestry.cultivation.planters.PlanterSaplings;
import forestry.cultivation.planters.PlanterSeeds;
import forestry.plugins.PluginForestryCultivation;
import forestry.plugins.PluginIC2;

public class PackagesCultivation {

	public static final EnergyConfiguration energyConfigDefaultPlanter = new EnergyConfiguration(Defaults.PLANTER_LATENCY,
			Defaults.PLANTER_MIN_ENERGY_RECEIVED, Defaults.PLANTER_MAX_ENERGY_RECEIVED, Defaults.PLANTER_MIN_ACTIVATION_ENERGY, Defaults.PLANTER_MAX_ENERGY);
	public static final EnergyConfiguration energyConfigDefaultHarvester = new EnergyConfiguration(Defaults.HARVESTER_LATENCY,
			Defaults.HARVESTER_MIN_ENERGY_RECEIVED, Defaults.HARVESTER_MAX_ENERGY_RECEIVED, Defaults.HARVESTER_MIN_ACTIVATION_ENERGY,
			Defaults.HARVESTER_MAX_ENERGY);

	public static final EnergyConfiguration energyConfigDefaultGrower = new EnergyConfiguration(Defaults.FORESTER_LATENCY,
			Defaults.FORESTER_MIN_ENERGY_RECEIVED, Defaults.FORESTER_MAX_ENERGY_RECEIVED, Defaults.FORESTER_MIN_ACTIVATION_ENERGY, Defaults.FORESTER_MAX_ENERGY);

	public static MachinePackage getArboretumPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "#X#", "XYX", "#Z#", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					new ItemStack(ForestryItem.tubes, 1, 4), Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'),
					new ItemStack(ForestryItem.circuitboards, 1, 0) });
		}
		pack = new MachinePackage(new PlanterSaplings.Factory(), "Arboretum",
				PluginForestryCultivation.proxy.getRenderDefaultPlanter(Defaults.TEXTURE_PATH_BLOCKS + "/arboretum_"), recipe);
		pack.blueprints.add(StructureBlueprint.defaultArboretum);
		pack.blueprints.add(PlanterSaplings.defaultSoil);
		pack.blueprints.add(PlanterSaplings.defaultPlantation);
		pack.energyConfig = PackagesCultivation.energyConfigDefaultPlanter;

		return pack;
	}

	public static MachinePackage getFarmPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "#X#", "XYX", "#Z#", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					new ItemStack(ForestryItem.tubes, 1, 2), Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'),
					new ItemStack(ForestryItem.circuitboards, 1, 0) });
		}
		pack = new MachinePackage(new PlanterSeeds.Factory(), "Farm", PluginForestryCultivation.proxy.getRenderDefaultPlanter(Defaults.TEXTURE_PATH_BLOCKS
				+ "/farm_"), recipe);
		pack.blueprints.add(StructureBlueprint.defaultFarm);
		pack.blueprints.add(PlanterSeeds.farmSoil);
		pack.blueprints.add(PlanterSeeds.wheatPlantation);
		pack.energyConfig = PackagesCultivation.energyConfigDefaultPlanter;

		return pack;
	}

	public static MachinePackage getPlantationPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		pack = new MachinePackage(new PlanterRubber.Factory(), "Rubber Plantation",
				PluginForestryCultivation.proxy.getRenderDefaultPlanter(Defaults.TEXTURE_PATH_BLOCKS + "/plantation_"), recipe);

		pack.blueprints.add(StructureBlueprint.defaultArboretum);
		pack.blueprints.add(PlanterRubber.rubberSoil);
		pack.blueprints.add(PlanterRubber.rubberPlantation);
		pack.energyConfig = PackagesCultivation.energyConfigDefaultPlanter;

		return pack;

	}

	public static MachinePackage getPumpkinFarmPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "Z#X", "#Y#", "X#Z", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Block.pumpkin,
					Character.valueOf('Z'), Block.melon, Character.valueOf('Y'), new ItemStack(ForestryBlock.planter, 1, 0) });
		}
		pack = new MachinePackage(new PlanterHerbaceous.Factory(), "Pumpkin Farm",
				PluginForestryCultivation.proxy.getRenderDefaultPlanter(Defaults.TEXTURE_PATH_BLOCKS + "/pumpkinfarm_"), recipe);
		pack.blueprints.add(PlanterHerbaceous.pumpkinArea);
		pack.blueprints.add(PlanterHerbaceous.pumpkinSoil);
		pack.blueprints.add(PlanterHerbaceous.pumpkinFarm);
		pack.energyConfig = PackagesCultivation.energyConfigDefaultPlanter;

		return pack;
	}

	public static MachinePackage getBogPeatPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "#X#", "XYX", "#Z#", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					new ItemStack(ForestryItem.tubes, 1, 0), Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'),
					new ItemStack(ForestryItem.circuitboards, 1, 0) });
		}
		pack = new MachinePackage(new PlanterBog.Factory(), "Peat Bog", PluginForestryCultivation.proxy.getRenderDefaultPlanter(Defaults.TEXTURE_PATH_BLOCKS
				+ "/peatbog_"), recipe);
		pack.blueprints.add(StructureBlueprint.defaultFarm);
		pack.blueprints.add(PlanterBog.bogEarth);
		pack.energyConfig = PackagesCultivation.energyConfigDefaultPlanter;

		return pack;
	}

	public static MachinePackage getMushroomFarmPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "X#Z", "#Y#", "Z#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					Block.mushroomBrown, Character.valueOf('Z'), Block.mushroomRed, Character.valueOf('Y'), new ItemStack(ForestryBlock.planter, 1, 0) });
		}
		pack = new MachinePackage(new PlanterMushroom.Factory(), "Mushroom Farm",
				PluginForestryCultivation.proxy.getRenderDefaultPlanter(Defaults.TEXTURE_PATH_BLOCKS + "/mushroomfarm_"), recipe);
		pack.blueprints.add(PlanterMushroom.defaultShroom);
		pack.blueprints.add(PlanterMushroom.defaultSoil);
		pack.blueprints.add(PlanterMushroom.defaultPlantation);
		pack.energyConfig = PackagesCultivation.energyConfigDefaultPlanter;

		return pack;
	}

	public static MachinePackage getNetherFarmPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					Item.netherStalkSeeds, Character.valueOf('Y'), new ItemStack(ForestryBlock.planter, 1, 0) });
		}
		pack = new MachinePackage(new PlanterNetherwarts.Factory(), "Netherwart Farm",
				PluginForestryCultivation.proxy.getRenderDefaultPlanter(Defaults.TEXTURE_PATH_BLOCKS + "/netherfarm_"), recipe);
		pack.blueprints.add(StructureBlueprint.defaultArboretum);
		pack.blueprints.add(PlanterNetherwarts.netherwartSoil);
		pack.blueprints.add(PlanterNetherwarts.netherwartPlantation);
		pack.energyConfig = PackagesCultivation.energyConfigDefaultPlanter;

		return pack;
	}

	/**
	 * @return {@link MachinePackage} for the logger
	 */
	public static MachinePackage getLoggerPackage() {

		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "#X#", "XYX", "#Z#", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					new ItemStack(ForestryItem.tubes, 1, 5), Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'),
					new ItemStack(ForestryItem.circuitboards, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterSapling.Factory(), "Logger", new TextureDescription(48, 49, 50, 51, 52, 53), recipe);

		// Configure energy
		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;

		return pack;
	}

	/**
	 * @return {@link MachinePackage} for the combine
	 */
	public static MachinePackage getCombinePackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "#X#", "XYX", "#Z#", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					new ItemStack(ForestryItem.tubes, 1, 3), Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'),
					new ItemStack(ForestryItem.circuitboards, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterSeeds.Factory(), "Combine", new TextureDescription(32, 33, 34, 35, 36, 37), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	public static MachinePackage getRubberHarvesterPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		pack = new MachinePackage(new HarvesterRubber.Factory(), "Rubber Harvester", new TextureDescription(64, 65, 66, 67, 68, 69), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	/**
	 * @return {@link MachinePackage} for pumpkin/melon harvester
	 */
	public static MachinePackage getPumpkinHarvesterPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "Z#X", "#Y#", "X#Z", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Block.pumpkin,
					Character.valueOf('Z'), Block.melon, Character.valueOf('Y'), new ItemStack(ForestryBlock.harvester, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterHerbaceous.Factory(), "Pumpkin Harvester", new TextureDescription(80, 81, 82, 83, 84, 85), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	/**
	 * @return {@link MachinePackage} for turbary
	 */
	public static MachinePackage getTurbaryPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "#X#", "XYX", "#Z#", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					new ItemStack(ForestryItem.tubes, 1, 1), Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'),
					new ItemStack(ForestryItem.circuitboards, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterPeat.Factory(), "Turbary", new TextureDescription(16, 17, 18, 19, 20, 21), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	/**
	 * @return {@link MachinePackage} for cacti collector
	 */
	public static MachinePackage getCactiHarvesterPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Block.cactus,
					Character.valueOf('Y'), new ItemStack(ForestryBlock.harvester, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterCacti.Factory(), "Cacti Harvester", new TextureDescription(112, 113, 114, 115, 116, 117), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	/**
	 * @return {@link MachinePackage} for mushroom picker
	 */
	public static MachinePackage getMushroomPickerPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "X#Z", "#Y#", "Z#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					Block.mushroomBrown, Character.valueOf('Z'), Block.mushroomRed, Character.valueOf('Y'), new ItemStack(ForestryBlock.harvester, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterMushroom.Factory(), "Mushroom Picker", new TextureDescription(128, 129, 130, 131, 132, 133), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	/**
	 * @return {@link MachinePackage} for mushroom picker
	 */
	public static MachinePackage getReedHarvesterPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Item.reed,
					Character.valueOf('Y'), new ItemStack(ForestryBlock.harvester, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterReeds.Factory(), "Sugar Cane Harvester", new TextureDescription(144, 145, 146, 147, 148, 149), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	/**
	 * @return {@link MachinePackage} for nether combine
	 */
	public static MachinePackage getNetherCombinePackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (Config.craftingFarmsEnabled) {
			recipe = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					Item.netherStalkSeeds, Character.valueOf('Y'), new ItemStack(ForestryBlock.harvester, 1, 0) });
		}
		pack = new MachinePackage(new HarvesterNetherwart.Factory(), "Infernal Combine", new TextureDescription(160, 161, 162, 163, 164, 165), recipe);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultHarvester;
		return pack;
	}

	/**
	 * @return {@link MachinePackage} for forester
	 */
	public static MachinePackage getForesterPackage() {
		MachinePackage pack;

		CraftingIngredients recipes[] = new CraftingIngredients[2];
		recipes[0] = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Item.diamond,
				Character.valueOf('Y'), ForestryBlock.planter });
		pack = new MachinePackage(new MillForester.Factory(), "Forester", Proxies.render.getRenderMill(Defaults.TEXTURE_PATH_BLOCKS + "/forester_"), recipes);

		pack.energyConfig = PackagesCultivation.energyConfigDefaultGrower;

		return pack;
	}

	public static MachinePackage getTreetapPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		if (PluginIC2.instance.isAvailable()) {
			recipe = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
					PluginIC2.treetap, Character.valueOf('Y'), ForestryItem.sturdyCasing });
		}

		byte charges = 8;
		pack = new MachinePackage(new MillTreetap.Factory(), "Treetap", Proxies.render.getRenderMill(Defaults.TEXTURE_PATH_BLOCKS + "/treetap_", charges),
				recipe);

		// Configure energy
		pack.energyConfig = PackagesCultivation.energyConfigDefaultGrower;

		return pack;
	}

}
