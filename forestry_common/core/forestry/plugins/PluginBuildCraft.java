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

import java.util.LinkedList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.api.fuels.IronEngineCoolant;
import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.recipes.RefineryRecipe;
import buildcraft.api.transport.IPipe;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.GlobalManager;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.IPacketHandler;
import forestry.api.core.IPickupHandler;
import forestry.api.core.IPlugin;
import forestry.api.core.IResupplyHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.gadgets.TileForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnergyConfiguration;
import forestry.core.utils.LiquidHelper;

@PluginInfo(pluginID = "BC3", name = "BuildCraft 3", author = "SirSengir", url = Defaults.URL, description = "Compatibility plugin for BuildCraft 3.")
public class PluginBuildCraft implements IPlugin, ITriggerProvider {

	public static PluginBuildCraft instance;

	public static Configuration config;

	// Ignore Buildcraft?
	public static boolean ignore;

	public static int engineId;
	public static int pipeId;

	public static Item wrench;
	public static Item stoneGear;
	public static Item pipeWaterproof;

	public static Block oilStill;
	public static Block oilMoving;
	public static Item fuel;

	public PluginBuildCraft() {
		if (PluginBuildCraft.instance == null) {
			PluginBuildCraft.instance = this;
		}
	}

	/**
	 * @return true if BuildCraftCore is installed.
	 */
	@Override
	public boolean isAvailable() {
		return (Proxies.common.isModLoaded("BuildCraft|Core") && Proxies.common.isModLoaded("BuildCraft|Transport"));
	}

	@Override
	public void doInit() {
		config = Config.config;

		Property buildcraftignore = config.get("buildcraft.ignore", Config.CATEGORY_COMMON, false);
		buildcraftignore.Comment = "set to true to ignore buildcraft";
		PluginBuildCraft.ignore = Boolean.parseBoolean(buildcraftignore.Value);

		Property buildcraftengine = config.get("buildcraft.blockid.engine", Config.CATEGORY_COMMON, Defaults.BUILDCRAFT_BLOCKID_ENGINE);
		buildcraftengine.Comment = "necessary to have the arboretum not clear buildcraft engines.";
		engineId = Integer.parseInt(buildcraftengine.Value);
		Property buildcraftpipe = config.get("buildcraft.blockid.pipe", Config.CATEGORY_COMMON, Defaults.BUILDCRAFT_BLOCKID_PIPE);
		buildcraftpipe.Comment = "necessary to have the arboretum not clear buildcraft pipes.";
		pipeId = Integer.parseInt(buildcraftpipe.Value);

		GlobalManager.holyBlockIds.add(engineId);
		GlobalManager.holyBlockIds.add(pipeId);

		IronEngineCoolant.coolants.add(new IronEngineCoolant(new LiquidStack(ForestryItem.liquidIce, 1), 10.0f));
		addIronEngineFuel(new LiquidStack(ForestryItem.liquidBiofuel, 1), 5, 40000);

		// Add recipe for biofuel
		addRefineryRecipe(new LiquidStack(ForestryItem.liquidBiomass, 4), null, new LiquidStack(ForestryItem.liquidBiofuel, 1), 10, 1);

		// Add custom trigger handler
		ActionManager.registerTriggerProvider(this);

		initStoneGear();
		initWaterproof();
		initLiquids();
	}

	@Override
	public void postInit() {
	}

	private void initLiquids() {
		try {
			oilStill = (Block) Class.forName("buildcraft.BuildCraftEnergy").getField("oilStill").get(null);
			oilMoving = (Block) Class.forName("buildcraft.BuildCraftEnergy").getField("oilMoving").get(null);
			fuel = (Item) Class.forName("buildcraft.BuildCraftEnergy").getField("fuel").get(null);
		} catch (Exception ex) {
			Proxies.log.fine("Oil or fuel not found.");
			return;
		}

		LiquidHelper.injectWaxContainer(new LiquidContainerData(new LiquidStack(oilStill, Defaults.BUCKET_VOLUME), new ItemStack(ForestryItem.waxCapsuleOil),
				new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectWaxContainer(new LiquidContainerData(new LiquidStack(fuel, Defaults.BUCKET_VOLUME), new ItemStack(ForestryItem.waxCapsuleFuel),
				new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(new LiquidContainerData(new LiquidStack(oilStill, Defaults.BUCKET_VOLUME), new ItemStack(
				ForestryItem.refractoryOil), new ItemStack(ForestryItem.refractoryEmpty)));
		LiquidHelper.injectRefractoryContainer(new LiquidContainerData(new LiquidStack(fuel, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.refractoryFuel), new ItemStack(ForestryItem.refractoryEmpty)));
		LiquidHelper.injectTinContainer(new LiquidContainerData(new LiquidStack(oilStill, Defaults.BUCKET_VOLUME), new ItemStack(ForestryItem.canOil),
				new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectTinContainer(new LiquidContainerData(new LiquidStack(fuel, Defaults.BUCKET_VOLUME), new ItemStack(ForestryItem.canFuel),
				new ItemStack(ForestryItem.canEmpty)));

	}

	private void addIronEngineFuel(LiquidStack fuel, int powerPerCycle, int totalBurningTime) {
		IronEngineFuel.fuels.add(new buildcraft.api.fuels.IronEngineFuel(fuel, powerPerCycle, totalBurningTime));
	}

	private void addRefineryRecipe(LiquidStack ingredient1, LiquidStack ingredient2, LiquidStack result, int energy, int delay) {
		RefineryRecipe.registerRefineryRecipe(new buildcraft.api.recipes.RefineryRecipe(ingredient1, ingredient2, result, energy, delay));

	}

	private void initStoneGear() {
		try {
			stoneGear = (Item) Class.forName("buildcraft.BuildCraftCore").getField("stoneGearItem").get(null);
		} catch (Exception ex) {
			Proxies.log.fine("No BuildCraft stone gear found.");
			return;
		}
	}

	private void initWaterproof() {
		try {
			pipeWaterproof = (Item) Class.forName("buildcraft.BuildCraftTransport").getField("pipeWaterproof").get(null);
		} catch (Exception ex) {
			Proxies.log.fine("No BuildCraft pipe waterproof found.");
			return;
		}

		Proxies.common.addRecipe(new ItemStack(pipeWaterproof), new Object[] { "#", Character.valueOf('#'), ForestryItem.beeswax });
	}

	/**
	 * Configures a BC power provider according to {@link EnergyConfiguration}
	 * 
	 * @param powerProvider
	 * @param energyConfig
	 */
	public void configurePowerProvider(IPowerProvider powerProvider, EnergyConfiguration energyConfig) {
		powerProvider.configure(energyConfig.latency, energyConfig.minEnergyReceived, energyConfig.maxEnergyReceived, energyConfig.minActivationEnergy,
				energyConfig.maxEnergy);
		powerProvider.configurePowerPerdition(energyConfig.powerLoss, energyConfig.powerLossRegularity);
	}

	public float invokeUseEnergyMethod(IPowerProvider powerProvider, float min, float max, boolean doUse) {
		return powerProvider.useEnergy(min, max, doUse);
	}

	public void invokeReceiveEnergyMethod(IPowerProvider powerProvider, float quantity) {
		powerProvider.receiveEnergy(quantity, ForgeDirection.WEST);
	}

	// / ITRIGGERPROVIDER

	@Override
	public LinkedList<ITrigger> getPipeTriggers(IPipe pipe) {
		return null;
	}

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		if (tile instanceof TileForestry)
			return ((TileForestry) tile).getCustomTriggers();

		return null;
	}

	@Override
	public String getDescription() {
		return "BuildCraft3";
	}

	@Override
	public void preInit() {
	}

	@Override
	public void generateSurface(World world, Random rand, int chunkX, int chunkZ) {
	};

	@Override
	public IGuiHandler getGuiHandler() {
		return null;
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return null;
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return null;
	}

	@Override
	public IResupplyHandler getResupplyHandler() {
		return null;
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
		return null;
	}
}
