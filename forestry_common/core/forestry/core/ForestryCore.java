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
package forestry.core;

import java.io.File;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.api.power.PowerFramework;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import forestry.api.apiculture.FlowerManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.GlobalManager;
import forestry.api.core.IPlugin;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.EngineCopperFuel;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.fuels.RainSubstrate;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Version;
import forestry.core.gadgets.EntityHandler;
import forestry.core.gadgets.GadgetManager;
import forestry.core.gadgets.TileEngine;
import forestry.core.gadgets.TileMachine;
import forestry.core.gadgets.TileMill;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.plugins.NativePlugin;
import forestry.plugins.PluginManager;

public class ForestryCore {

	public EntityHandler entityHandler;

	public void preInit(File modLocation, Object basemod) {
		ForestryAPI.instance = basemod;

		PluginManager.loadPlugins(modLocation);

		// Register event handler
		MinecraftForge.EVENT_BUS.register(new EventHandlerCore());

	}

	public void init(Object basemod) {

		Config.load();
		if(!Config.disableVersionCheck)
			Version.versionCheck();

		for (IPlugin plugin : PluginManager.plugins)
			if (plugin.isAvailable()) {
				plugin.preInit();
			} else {
				Proxies.log.fine("Skipped plugin " + plugin.getClass() + " because preconditions were not met.");
			}

		// Register world generator
		GameRegistry.registerWorldGenerator(new WorldGenerator());

		// Register our own handler for tile entities.
		entityHandler = new EntityHandler();
		GadgetManager.registerEntityHandler(entityHandler);

		// Set fuels and resources for the fermenter
		FuelManager.fermenterFuel.put(new ItemStack(ForestryItem.fertilizerCompound), new FermenterFuel(new ItemStack(ForestryItem.fertilizerCompound),
				GameMode.getGameMode().getFermentationPerCycleFertilizer(), GameMode.getGameMode().getFermentationCyclesFertilizer()));
		FuelManager.fermenterFuel.put(new ItemStack(ForestryItem.fertilizerBio), new FermenterFuel(new ItemStack(ForestryItem.fertilizerBio),
				GameMode.getGameMode().getFermentationPerCycleCompost(), GameMode.getGameMode().getFermentationCyclesCompost()));
		FuelManager.fermenterFuel.put(new ItemStack(ForestryItem.mulch), new FermenterFuel(new ItemStack(ForestryItem.mulch),
				GameMode.getGameMode().getFermentationPerCycleCompost(), GameMode.getGameMode().getFermentationCyclesCompost()));

		// Add moistener resources
		FuelManager.moistenerResource.put(new ItemStack(Item.wheat),
				new MoistenerFuel(new ItemStack(Item.wheat), new ItemStack(ForestryItem.mouldyWheat), 0, 300));
		FuelManager.moistenerResource.put(new ItemStack(ForestryItem.mouldyWheat), new MoistenerFuel(new ItemStack(ForestryItem.mouldyWheat), new ItemStack(
				ForestryItem.decayingWheat), 1, 600));
		FuelManager.moistenerResource.put(new ItemStack(ForestryItem.decayingWheat), new MoistenerFuel(new ItemStack(ForestryItem.decayingWheat), new ItemStack(
				ForestryItem.mulch), 2, 900));

		// Set fuels for our own engines
		FuelManager.copperEngineFuel.put(new ItemStack(ForestryItem.peat), new EngineCopperFuel(new ItemStack(ForestryItem.peat),
				Defaults.ENGINE_COPPER_FUEL_VALUE_PEAT, Defaults.ENGINE_COPPER_CYCLE_DURATION_PEAT));
		FuelManager.copperEngineFuel.put(new ItemStack(ForestryItem.bituminousPeat), new EngineCopperFuel(new ItemStack(ForestryItem.bituminousPeat),
				Defaults.ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT, Defaults.ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT));
		FuelManager.bronzeEngineFuel.put(new ItemStack(ForestryItem.liquidBiomass), new EngineBronzeFuel(new ItemStack(ForestryItem.liquidBiomass),
				Defaults.ENGINE_FUEL_VALUE_BIOMASS, Defaults.ENGINE_CYCLE_DURATION_BIOMASS, 1));
		FuelManager.bronzeEngineFuel.put(new ItemStack(Block.waterStill), new EngineBronzeFuel(new ItemStack(Block.waterStill), Defaults.ENGINE_FUEL_VALUE_WATER,
				Defaults.ENGINE_CYCLE_DURATION_WATER, 3));
		FuelManager.bronzeEngineFuel.put(new ItemStack(ForestryItem.liquidMilk), new EngineBronzeFuel(new ItemStack(ForestryItem.liquidMilk),
				Defaults.ENGINE_FUEL_VALUE_MILK, Defaults.ENGINE_CYCLE_DURATION_MILK, 3));
		FuelManager.bronzeEngineFuel.put(new ItemStack(ForestryItem.liquidSeedOil), new EngineBronzeFuel(new ItemStack(ForestryItem.liquidSeedOil),
				Defaults.ENGINE_FUEL_VALUE_SEED_OIL, Defaults.ENGINE_CYCLE_DURATION_SEED_OIL, 1));
		FuelManager.bronzeEngineFuel.put(new ItemStack(ForestryItem.liquidHoney), new EngineBronzeFuel(new ItemStack(ForestryItem.liquidHoney),
				Defaults.ENGINE_FUEL_VALUE_HONEY, Defaults.ENGINE_CYCLE_DURATION_HONEY, 1));
		FuelManager.bronzeEngineFuel.put(new ItemStack(ForestryItem.liquidJuice), new EngineBronzeFuel(new ItemStack(ForestryItem.liquidJuice),
				Defaults.ENGINE_FUEL_VALUE_JUICE, Defaults.ENGINE_CYCLE_DURATION_JUICE, 1));

		// Set rain substrates
		FuelManager.rainSubstrate.put(new ItemStack(ForestryItem.iodineCharge), new RainSubstrate(new ItemStack(ForestryItem.iodineCharge),
				Defaults.RAINMAKER_RAIN_DURATION_IODINE, 0.01f));
		FuelManager.rainSubstrate.put(new ItemStack(ForestryItem.craftingMaterial, 1, 4), new RainSubstrate(new ItemStack(ForestryItem.craftingMaterial, 1, 4), 0.075f));

		// Set blocks the arboretum won't destroy when clearing a soil area
		GlobalManager.holyBlockIds.add(ForestryBlock.soil.blockID);
		if (ForestryBlock.planter != null) {
			GlobalManager.holyBlockIds.add(ForestryBlock.planter.blockID);
		}
		if (ForestryBlock.harvester != null) {
			GlobalManager.holyBlockIds.add(ForestryBlock.harvester.blockID);
		}
		if (ForestryBlock.engine != null) {
			GlobalManager.holyBlockIds.add(ForestryBlock.engine.blockID);
		}
		if (ForestryBlock.machine != null) {
			GlobalManager.holyBlockIds.add(ForestryBlock.machine.blockID);
		}
		if (ForestryBlock.mill != null) {
			GlobalManager.holyBlockIds.add(ForestryBlock.mill.blockID);
		}
		GlobalManager.holyBlockIds.add(Block.torchRedstoneActive.blockID);
		GlobalManager.holyBlockIds.add(Block.torchRedstoneIdle.blockID);
		GlobalManager.holyBlockIds.add(Block.brick.blockID);
		GlobalManager.holyBlockIds.add(Block.stoneBrick.blockID);
		GlobalManager.holyBlockIds.add(Block.lever.blockID);
		GlobalManager.holyBlockIds.add(Block.bedrock.blockID);
		GlobalManager.holyBlockIds.add(Block.chest.blockID);
		GlobalManager.holyBlockIds.add(Block.cobblestone.blockID);
		GlobalManager.holyBlockIds.add(Block.cobblestoneMossy.blockID);
		GlobalManager.holyBlockIds.add(Block.netherBrick.blockID);
		GlobalManager.holyBlockIds.add(Block.torchWood.blockID);
		GlobalManager.holyBlockIds.add(Block.planks.blockID);
		GlobalManager.holyBlockIds.add(Block.mycelium.blockID);

		// Set additional apiary flowers
		FlowerManager.plainFlowers.add(new ItemStack(Block.plantRed));
		FlowerManager.plainFlowers.add(new ItemStack(Block.plantYellow));

		// Register gui handler
		NetworkRegistry.instance().registerGuiHandler(basemod, new GuiHandler());

		// Register machines
		GameRegistry.registerTileEntity(TileMill.class, "forestry.Grower");
		GameRegistry.registerTileEntity(TileEngine.class, "forestry.Engine");
		GameRegistry.registerTileEntity(TileMachine.class, "forestry.Machine");

	}

	public void postInit() {

		for (IPlugin plugin : PluginManager.plugins)
			if (plugin.isAvailable()) {
				plugin.doInit();
			}

		// Add lava and water buckets to the API in case this has not been done yet.
		if (LiquidHelper.isEmptyLiquidData()) {
			LiquidHelper.injectLiquidContainer(new LiquidContainerData(new LiquidStack(Block.lavaStill, Defaults.BUCKET_VOLUME),
					new ItemStack(Item.bucketLava), new ItemStack(Item.bucketEmpty)));
			LiquidHelper.injectLiquidContainer(new LiquidContainerData(new LiquidStack(Block.waterStill, Defaults.BUCKET_VOLUME), new ItemStack(
					Item.bucketWater), new ItemStack(Item.bucketEmpty)));
		}

		// Set default lava, water and biofuel buckets
		LiquidHelper.injectLiquidContainer(LiquidHelper.createLiquidData("milk", new LiquidStack(ForestryItem.liquidMilk, Defaults.BUCKET_VOLUME),
				new ItemStack(Item.bucketMilk), new ItemStack(Item.bucketEmpty)));

		// Lava
		LiquidHelper.injectTinContainer(new LiquidContainerData(new LiquidStack(Block.lavaStill, Defaults.BUCKET_VOLUME), new ItemStack(ForestryItem.canLava),
				new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectRefractoryContainer(new LiquidContainerData(new LiquidStack(Block.lavaStill, Defaults.BUCKET_VOLUME), new ItemStack(
				ForestryItem.refractoryLava), new ItemStack(ForestryItem.refractoryEmpty)));

		// Water
		LiquidHelper.injectLiquidContainer(new LiquidContainerData(new LiquidStack(Block.waterStill, Defaults.BUCKET_VOLUME), new ItemStack(Item.potion, 1, 0),
				new ItemStack(Item.glassBottle)));
		LiquidHelper.injectTinContainer(new LiquidContainerData(new LiquidStack(Block.waterStill, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.canWater), new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectWaxContainer(new LiquidContainerData(new LiquidStack(Block.waterStill, Defaults.BUCKET_VOLUME), new ItemStack(
				ForestryItem.waxCapsuleWater), new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(new LiquidContainerData(new LiquidStack(Block.waterStill, Defaults.BUCKET_VOLUME), new ItemStack(
				ForestryItem.refractoryWater), new ItemStack(ForestryItem.refractoryEmpty)));

		LiquidHelper.injectLiquidContainer(LiquidHelper.createLiquidData("biomass", new LiquidStack(ForestryItem.liquidBiomass, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.bucketBiomass), new ItemStack(Item.bucketEmpty)));
		LiquidHelper.injectTinContainer(LiquidHelper.createLiquidData("biomass", new LiquidStack(ForestryItem.liquidBiomass, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.canBiomass), new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectWaxContainer(LiquidHelper.createLiquidData("biomass", new LiquidStack(ForestryItem.liquidBiomass, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.waxCapsuleBiomass), new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(LiquidHelper.createLiquidData("biomass", new LiquidStack(ForestryItem.liquidBiomass, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.refractoryBiomass), new ItemStack(ForestryItem.refractoryEmpty)));

		LiquidHelper.injectLiquidContainer(LiquidHelper.createLiquidData("biofuel", new LiquidStack(ForestryItem.liquidBiofuel, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.bucketBiofuel), new ItemStack(Item.bucketEmpty)));
		LiquidHelper.injectTinContainer(LiquidHelper.createLiquidData("biofuel", new LiquidStack(ForestryItem.liquidBiofuel, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.canBiofuel), new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectWaxContainer(LiquidHelper.createLiquidData("biofuel", new LiquidStack(ForestryItem.liquidBiofuel, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.waxCapsuleBiofuel), new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(LiquidHelper.createLiquidData("biofuel", new LiquidStack(ForestryItem.liquidBiofuel, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.refractoryBiofuel), new ItemStack(ForestryItem.refractoryEmpty)));

		LiquidHelper.injectTinContainer(LiquidHelper.createLiquidData("seedoil", new LiquidStack(ForestryItem.liquidSeedOil, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.canSeedOil), new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectWaxContainer(LiquidHelper.createLiquidData("seedoil", new LiquidStack(ForestryItem.liquidSeedOil, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.waxCapsuleSeedOil), new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(LiquidHelper.createLiquidData("seedoil", new LiquidStack(ForestryItem.liquidSeedOil, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.refractorySeedOil), new ItemStack(ForestryItem.refractoryEmpty)));

		LiquidHelper.injectTinContainer(LiquidHelper.createLiquidData("honey", new LiquidStack(ForestryItem.liquidHoney, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.canHoney), new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectWaxContainer(LiquidHelper.createLiquidData("honey", new LiquidStack(ForestryItem.liquidHoney, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.waxCapsuleHoney), new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(LiquidHelper.createLiquidData("honey", new LiquidStack(ForestryItem.liquidHoney, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.refractoryHoney), new ItemStack(ForestryItem.refractoryEmpty)));

		LiquidHelper.injectTinContainer(LiquidHelper.createLiquidData("juice", new LiquidStack(ForestryItem.liquidJuice, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.canJuice), new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectWaxContainer(LiquidHelper.createLiquidData("juice", new LiquidStack(ForestryItem.liquidJuice, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.waxCapsuleJuice), new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(LiquidHelper.createLiquidData("juice", new LiquidStack(ForestryItem.liquidJuice, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.refractoryJuice), new ItemStack(ForestryItem.refractoryEmpty)));

		LiquidHelper.injectTinContainer(LiquidHelper.createLiquidData("ice", new LiquidStack(ForestryItem.liquidIce, Defaults.BUCKET_VOLUME), new ItemStack(
				ForestryItem.canIce), new ItemStack(ForestryItem.canEmpty)));
		LiquidHelper.injectWaxContainer(LiquidHelper.createLiquidData("ice", new LiquidStack(ForestryItem.liquidIce, Defaults.BUCKET_VOLUME), new ItemStack(
				ForestryItem.waxCapsuleIce), new ItemStack(ForestryItem.waxCapsule)));
		LiquidHelper.injectRefractoryContainer(LiquidHelper.createLiquidData("ice", new LiquidStack(ForestryItem.liquidIce, Defaults.BUCKET_VOLUME),
				new ItemStack(ForestryItem.refractoryIce), new ItemStack(ForestryItem.refractoryEmpty)));

		Config.modsLoaded();

		Proxies.log.fine("Using " + PowerFramework.currentFramework.getClass().getName() + " as framework.");

		for (IPlugin plugin : PluginManager.plugins)
			if (plugin.isAvailable()) {
				plugin.postInit();
			}

		// AchievementManager.initialize();

		// Add names handled in packages
		GadgetManager.registerAllPackageNames();

		GameRegistry.registerFuelHandler(new FuelHandler());

		TickRegistry.registerTickHandler(new TickHandlerCoreServer(), Side.SERVER);
		TickRegistry.registerTickHandler(new TickHandlerCoreClient(), Side.CLIENT);

		// Handle IMC messages.
		processIMCMessages(FMLInterModComms.fetchRuntimeMessages(ForestryAPI.instance));
	}

	public void serverStarting(MinecraftServer server) {
		CommandHandler commandManager = (CommandHandler) server.getCommandManager();
		for (IPlugin plugin : PluginManager.plugins)
			if (plugin.isAvailable()) {
				ICommand[] commands = plugin.getConsoleCommands();
				if (commands == null) {
					continue;
				}
				for (ICommand command : commands) {
					commandManager.registerCommand(command);
				}
			}
	}

	public void processIMCMessages(ImmutableList<IMCMessage> messages) {
		for (IMCMessage message : messages) {
			for(IPlugin plugin : PluginManager.plugins) {
				if(!(plugin instanceof NativePlugin))
					continue;
				
				if(((NativePlugin)plugin).processIMCMessage(message))
					break;
			}
		}
	}
	
	public String getPriorities() {
		return "after:mod_IC2;after:mod_BuildCraftCore;after:mod_BuildCraftEnergy;after:mod_BuildCraftFactory;after:mod_BuildCraftSilicon;after:mod_BuildCraftTransport;after:mod_RedPowerWorld";
	}

}
