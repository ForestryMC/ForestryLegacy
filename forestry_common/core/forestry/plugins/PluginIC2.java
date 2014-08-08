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

import ic2.api.Ic2Recipes;
import ic2.api.Items;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.GlobalManager;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.IPacketHandler;
import forestry.api.core.IPickupHandler;
import forestry.api.core.IPlugin;
import forestry.api.core.IResupplyHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.cultivation.CropProviders;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.RecipeUtil;

@PluginInfo(pluginID = "IC2", name = "IndustrialCraft2", author = "SirSengir", url = Defaults.URL, description = "Compatibility plugin for IC2.")
public class PluginIC2 implements IPlugin {

	public static PluginIC2 instance;

	public static Configuration config;

	// Ignore IC2?
	public static boolean ignore;

	// IC2 stuff
	public static ItemStack plantBall;
	public static ItemStack compressedPlantBall;

	public static ItemStack wrench;
	public static ItemStack treetap;
	public static ItemStack resin;
	public static ItemStack rubbersapling;
	public static ItemStack rubberwood;
	public static ItemStack rubberleaves;
	public static ItemStack fuelcanFilled;
	public static ItemStack fuelcanEmpty;
	public static ItemStack emptyCell;
	public static ItemStack lavaCell;
	public static ItemStack waterCell;

	public static ItemStack rubber;
	public static ItemStack scrap;
	public static ItemStack uum;

	public static int fuelcanMeta;

	public PluginIC2() {
		if (PluginIC2.instance == null) {
			PluginIC2.instance = this;
		}
	}

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("IC2");
	}

	@Override
	public void doInit() {

		config = Config.config;

		initLiquidContainers();
		initRubberChain();
		initFermentation();
		initCrates();

		// Don't clear IC2 blocks
		ItemStack personalSafe = Items.getItem("personalSafe");
		if(personalSafe != null)
			GlobalManager.holyBlockIds.add(personalSafe.itemID);

		ItemStack reinforcedStone = Items.getItem("reinforcedStone");
		GlobalManager.holyBlockIds.add(reinforcedStone.itemID);
		ItemStack reinforcedGlass = Items.getItem("reinforcedGlass");
		GlobalManager.holyBlockIds.add(reinforcedGlass.itemID);
		ItemStack reinforcedDoorBlock = Items.getItem("reinforcedDoorBlock");
		GlobalManager.holyBlockIds.add(reinforcedDoorBlock.itemID);

		ItemStack copperCableBlock = Items.getItem("copperCableBlock");
		GlobalManager.holyBlockIds.add(copperCableBlock.itemID);

		Ic2Recipes.addRecyclerBlacklistItem(ForestryItem.beeQueenGE);
		Ic2Recipes.addRecyclerBlacklistItem(ForestryItem.beePrincessGE);

		// Remove some items from the recycler
		registerBackpackItems();
		
		if(rubbersapling != null && resin != null) {
			String imc = String.format("farmArboreal@%s.%s.%s.%s", rubbersapling.itemID, rubbersapling.getItemDamage(), resin.itemID, resin.getItemDamage());
			Proxies.log.finest("Sending IMC '%s'.", imc);
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", imc);
		}
	}

	private void initFermentation() {
		plantBall = Items.getItem("plantBall");
		compressedPlantBall = Items.getItem("compressedPlantBall");
		if (plantBall == null || compressedPlantBall == null) {
			Proxies.log.fine("No IC2 plantballs found.");
			return;
		}

		// Add extra recipes
		RecipeUtil.injectLeveledRecipe(plantBall, GameMode.getGameMode().getFermentedPerWheat() * 4, new ItemStack(ForestryItem.liquidBiomass));
		RecipeUtil.injectLeveledRecipe(compressedPlantBall, GameMode.getGameMode().getFermentedPerWheat() * 5, new ItemStack(ForestryItem.liquidBiomass));
	}

	private void initLiquidContainers() {
		emptyCell = Items.getItem("cell");
		lavaCell = Items.getItem("lavaCell");
		waterCell = Items.getItem("waterCell");
		if (emptyCell == null || lavaCell == null || waterCell == null) {
			Proxies.log.fine("Any of the following IC2 items could not be found: empty cell, water cell, lava cell. Skipped adding IC2 liquid containers.");
			return;
		}

		LiquidHelper.injectTinContainer(new LiquidContainerData(new LiquidStack(Block.lavaStill, Defaults.BUCKET_VOLUME), lavaCell, emptyCell));
		LiquidHelper.injectTinContainer(new LiquidContainerData(new LiquidStack(Block.waterStill, Defaults.BUCKET_VOLUME), waterCell, emptyCell));
		Proxies.common
				.addRecipe(new ItemStack(ForestryBlock.soil, 8, 1),
						new Object[] { "#Y#", "YXY", "#Y#", Character.valueOf('#'), Block.dirt, Character.valueOf('X'), waterCell, Character.valueOf('Y'),
								Block.sand });

		// Add extra recipes
		RecipeManagers.bottlerManager.addRecipe(5, new LiquidStack(Block.waterStill, Defaults.BUCKET_VOLUME), emptyCell, waterCell);
		RecipeManagers.bottlerManager.addRecipe(5, new LiquidStack(Block.lavaStill, Defaults.BUCKET_VOLUME), emptyCell, lavaCell);
	}

	private void initRubberChain() {
		treetap = Items.getItem("treetap");
		resin = Items.getItem("resin");
		rubberwood = Items.getItem("rubberWood");
		rubbersapling = Items.getItem("rubberSapling");
		rubberleaves = Items.getItem("rubberLeaves");
		fuelcanFilled = Items.getItem("filledFuelCan");
		fuelcanEmpty = Items.getItem("fuelCan");
		if (treetap == null || resin == null || rubberwood == null || rubbersapling == null || rubberleaves == null || fuelcanFilled == null
				|| fuelcanEmpty == null) {
			Proxies.log.fine("Any of the following IC2 blocks and items could not be found: resin, rubber wood, saplings or leaves, filled fuel cans, empty fuel cans. Skipped adding rubber chain.");
			return;
		}

		// Add crop provider
		CropProviders.arborealCrops.add(new CropProviderRubber());

		// Add extra recipes
		RecipeManagers.centrifugeManager.addRecipe(20, new ItemStack(ForestryItem.propolis), resin);
		ItemStack fuelcanStack = new ItemStack(fuelcanFilled.itemID, 1, 0);
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("value", 15288);
		fuelcanStack.setTagCompound(compound);
		RecipeManagers.bottlerManager.addRecipe(20, new LiquidStack(ForestryItem.liquidBiofuel, Defaults.BOTTLER_FUELCAN_VOLUME), fuelcanEmpty, fuelcanStack);

		RecipeUtil.injectLeveledRecipe(rubbersapling, GameMode.getGameMode().getFermentedPerSapling(), new ItemStack(ForestryItem.liquidBiomass));

		// Add backpack items
		BackpackManager.backpackItems[2].add(rubbersapling);
		BackpackManager.backpackItems[2].add(rubberleaves);
		// Rubber wood is added via ore dictionary.
	}

	private void initCrates() {
		resin = Items.getItem("resin");
		rubber = Items.getItem("rubber");
		scrap = Items.getItem("scrap");
		uum = Items.getItem("matter");
		if (resin == null || rubber == null || scrap == null || uum == null) {
			Proxies.log.fine("Any of the following IC2 blocks and items could not be found: resin, rubber, scrap or uu matter. Skipped adding crates.");
			return;
		}

		ForestryItem.cratedResin.setContained(new ItemStack(ForestryItem.cratedResin), resin);
		ForestryItem.cratedRubber.setContained(new ItemStack(ForestryItem.cratedRubber), rubber);
		ForestryItem.cratedScrap.setContained(new ItemStack(ForestryItem.cratedScrap), scrap);
		ForestryItem.cratedUUM.setContained(new ItemStack(ForestryItem.cratedUUM), uum);
	}

	private void registerBackpackItems() {
		if (BackpackManager.backpackItems == null)
			return;

		if (resin != null) {
			BackpackManager.definitions.get("forester").addValidItem(resin);
		}
		if (rubber != null) {
			BackpackManager.definitions.get("forester").addValidItem(rubber);
		}
	}

	@Override
	public String getDescription() {
		return "IndustrialCraft2";
	}

	@Override
	public void preInit() {
	}

	@Override
	public void postInit() {
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
