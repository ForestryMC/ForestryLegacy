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

import net.minecraft.item.ItemStack;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.interfaces.IGameMode;

public class GameMode implements IGameMode {

	private static IGameMode activeMode;

	public static IGameMode getGameMode() {

		if (activeMode == null) {
			activeMode = new GameMode(Config.gameMode);
		}

		return activeMode;
	}

	private String identifier = "EASY";

	private float energyDemandModifier = 1.0f;

	private int fertilizerFarmValue = 2000;
	
	private ItemStack recipeFertilizerOutputApatite = new ItemStack(ForestryItem.fertilizerCompound, 8);
	private ItemStack recipeFertilizerOutputAsh = new ItemStack(ForestryItem.fertilizerCompound, 16);
	private ItemStack recipeCompostOutputWheat = new ItemStack(ForestryItem.fertilizerBio, 4);
	private ItemStack recipeCompostOutputAsh = new ItemStack(ForestryItem.fertilizerBio, 1);

	private ItemStack recipeHumusOutputFertilizer = new ItemStack(ForestryBlock.soil, 8, 0);
	private ItemStack recipeHumusOutputCompost = new ItemStack(ForestryBlock.soil, 8, 0);

	private ItemStack recipeBogEarthOutputBucket = new ItemStack(ForestryBlock.soil, 6, 1);
	private ItemStack recipeBogEarthOutputCans = new ItemStack(ForestryBlock.soil, 8, 1);

	private ItemStack recipeCanOutput = new ItemStack(ForestryItem.canEmpty, 12);
	private ItemStack recipeCapsuleOutput = new ItemStack(ForestryItem.waxCapsule, 4);
	private ItemStack recipeRefractoryOutput = new ItemStack(ForestryItem.refractoryEmpty, 4);

	private int fermentationCyclesFertilizer = 200;
	private int fermentationCyclesCompost = 250;

	private int fermentationPerCycleFertilizer = 56;
	private int fermentationPerCycleCompost = 48;

	private int fermentedPerSapling = 800;
	private int fermentedPerCacti = 100;
	private int fermentedPerWheat = 100;
	private int fermentedPerCane = 100;
	private int fermentedPerMushroom = 100;

	private int squeezedLiquidPerSeed = 20;

	public GameMode(String identifier) {

		this.identifier = identifier;
		String category = "gamemodes/" + identifier;

		Configuration config = Config.config;

		Property property = config.get("energy.demand.modifier", category, energyDemandModifier);
		property.Comment = "modifies the energy required to activate machines, as well as the max amount of energy stored and accepted.";
		energyDemandModifier = Float.parseFloat(property.Value);

		property = config.get("farms.fertilizer.value", category, fertilizerFarmValue);
		property.Comment = "modifies the time fertilizer lasts in a farm.";
		fertilizerFarmValue = Integer.parseInt(property.Value);

		property = config.get("recipe.output.fertilizer.apatite", category, recipeFertilizerOutputApatite.stackSize);
		property.Comment = "amount of fertilizer yielded by the recipe using apatite.";
		recipeFertilizerOutputApatite = new ItemStack(recipeFertilizerOutputApatite.itemID, Integer.parseInt(property.Value),
				recipeFertilizerOutputApatite.getItemDamage());

		property = config.get("recipe.output.fertilizer.ash", category, recipeFertilizerOutputAsh.stackSize);
		property.Comment = "amount of fertilizer yielded by the recipe using ash.";
		recipeFertilizerOutputAsh = new ItemStack(recipeFertilizerOutputAsh.itemID, Integer.parseInt(property.Value), recipeFertilizerOutputAsh.getItemDamage());

		property = config.get("recipe.output.compost.wheat", category, recipeCompostOutputWheat.stackSize);
		property.Comment = "amount of compost yielded by the recipe using wheat.";
		recipeCompostOutputWheat = new ItemStack(recipeCompostOutputWheat.itemID, Integer.parseInt(property.Value), recipeCompostOutputWheat.getItemDamage());

		property = config.get("recipe.output.compost.ash", category, recipeCompostOutputAsh.stackSize);
		property.Comment = "amount of compost yielded by the recipe using ash.";
		recipeCompostOutputAsh = new ItemStack(recipeCompostOutputAsh.itemID, Integer.parseInt(property.Value), recipeCompostOutputAsh.getItemDamage());

		property = config.get("recipe.output.humus.fertilizer", category, recipeHumusOutputFertilizer.stackSize);
		property.Comment = "amount of humus yielded by the recipe using fertilizer.";
		recipeHumusOutputFertilizer = new ItemStack(recipeHumusOutputFertilizer.itemID, Integer.parseInt(property.Value),
				recipeHumusOutputFertilizer.getItemDamage());

		property = config.get("recipe.output.humus.compost", category, recipeHumusOutputCompost.stackSize);
		property.Comment = "amount of humus yielded by the recipe using compost.";
		recipeHumusOutputCompost = new ItemStack(recipeHumusOutputCompost.itemID, Integer.parseInt(property.Value), recipeHumusOutputCompost.getItemDamage());

		property = config.get("recipe.output.bogearth.bucket", category, recipeBogEarthOutputBucket.stackSize);
		property.Comment = "amount of bog earth yielded by the recipe using buckets.";
		recipeBogEarthOutputBucket = new ItemStack(recipeBogEarthOutputBucket.itemID, Integer.parseInt(property.Value),
				recipeBogEarthOutputBucket.getItemDamage());

		property = config.get("recipe.output.bogearth.can", category, recipeBogEarthOutputCans.stackSize);
		property.Comment = "amount of bog earth yielded by the recipes using cans, cells or capsules.";
		recipeBogEarthOutputCans = new ItemStack(recipeBogEarthOutputCans.itemID, Integer.parseInt(property.Value), recipeBogEarthOutputCans.getItemDamage());

		property = config.get("recipe.output.can", category, recipeCanOutput.stackSize);
		property.Comment = "amount yielded by the recipe for tin cans.";
		recipeCanOutput = new ItemStack(recipeCanOutput.itemID, Integer.parseInt(property.Value), recipeCanOutput.getItemDamage());

		property = config.get("recipe.output.capsule", category, recipeCapsuleOutput.stackSize);
		property.Comment = "amount yielded by the recipe for wax capsules.";
		recipeCapsuleOutput = new ItemStack(recipeCapsuleOutput.itemID, Integer.parseInt(property.Value), recipeCapsuleOutput.getItemDamage());

		property = config.get("recipe.output.refractory", category, recipeRefractoryOutput.stackSize);
		property.Comment = "amount yielded by the recipe for refractory capsules.";
		recipeRefractoryOutput = new ItemStack(recipeRefractoryOutput.itemID, Integer.parseInt(property.Value), recipeRefractoryOutput.getItemDamage());

		property = config.get("fermenter.cycles.fertilizer", category, fermentationCyclesFertilizer);
		property.Comment = "modifies the amount of cycles a fertilizer can keep a fermenter going.";
		fermentationCyclesFertilizer = Integer.parseInt(property.Value);

		property = config.get("fermenter.cycles.compost", category, fermentationCyclesCompost);
		property.Comment = "modifies the amount of cycles compost can keep a fermenter going.";
		fermentationCyclesCompost = Integer.parseInt(property.Value);

		property = config.get("fermenter.value.fertilizer", category, fermentationPerCycleFertilizer);
		property.Comment = "modifies the amount of biomass per cycle a fermenter will produce using fertilizer.";
		fermentationPerCycleFertilizer = Integer.parseInt(property.Value);

		property = config.get("fermenter.value.compost", category, fermentationPerCycleCompost);
		property.Comment = "modifies the amount of biomass per cycle a fermenter will produce using compost.";
		fermentationPerCycleCompost = Integer.parseInt(property.Value);

		property = config.get("fermenter.yield.sapling", category, fermentedPerSapling);
		property.Comment = "modifies the amount of biomass a sapling will yield in a fermenter.";
		fermentedPerSapling = Integer.parseInt(property.Value);

		property = config.get("fermenter.yield.cactus", category, fermentedPerCacti);
		property.Comment = "modifies the amount of biomass a piece of cactus will yield in a fermenter.";
		fermentedPerCacti = Integer.parseInt(property.Value);

		property = config.get("fermenter.yield.wheat", category, fermentedPerWheat);
		property.Comment = "modifies the amount of biomass a piece of wheat will yield in a fermenter.";
		fermentedPerWheat = Integer.parseInt(property.Value);

		property = config.get("fermenter.yield.cane", category, fermentedPerCane);
		property.Comment = "modifies the amount of biomass a piece of sugar cane will yield in a fermenter.";
		fermentedPerCane = Integer.parseInt(property.Value);

		property = config.get("fermenter.yield.mushroom", category, fermentedPerMushroom);
		property.Comment = "modifies the amount of biomass a mushroom will yield in a fermenter.";
		fermentedPerMushroom = Integer.parseInt(property.Value);

		property = config.get("squeezer.liquid.seed", category, squeezedLiquidPerSeed);
		property.Comment = "modifies the amount of seed oil squeezed from a single seed.";
		squeezedLiquidPerSeed = Integer.parseInt(property.Value);

		config.save();

	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public float getEnergyDemandModifier() {
		return energyDemandModifier;
	}

	@Override
	public int getFertilizerFarmValue() {
		return fertilizerFarmValue;
	}

	@Override
	public ItemStack getRecipeFertilizerOutputApatite() {
		return recipeFertilizerOutputApatite;
	}

	@Override
	public ItemStack getRecipeFertilizerOutputAsh() {
		return recipeFertilizerOutputAsh;
	}

	@Override
	public ItemStack getRecipeCompostOutputWheat() {
		return recipeCompostOutputWheat;
	}

	@Override
	public ItemStack getRecipeCompostOutputAsh() {
		return recipeCompostOutputAsh;
	}

	@Override
	public ItemStack getRecipeHumusOutputFertilizer() {
		return recipeHumusOutputFertilizer;
	}

	@Override
	public ItemStack getRecipeHumusOutputCompost() {
		return recipeHumusOutputCompost;
	}

	@Override
	public ItemStack getRecipeBogEarthOutputBucket() {
		return recipeBogEarthOutputBucket;
	}

	@Override
	public ItemStack getRecipeBogEarthOutputCans() {
		return recipeBogEarthOutputCans;
	}

	@Override
	public ItemStack getRecipeCanOutput() {
		return recipeCanOutput;
	}

	@Override
	public ItemStack getRecipeCapsuleOutput() {
		return recipeCapsuleOutput;
	}

	@Override
	public ItemStack getRecipeRefractoryOutput() {
		return recipeRefractoryOutput;
	}

	@Override
	public int getFermentationCyclesFertilizer() {
		return fermentationCyclesFertilizer;
	}

	@Override
	public int getFermentationCyclesCompost() {
		return fermentationCyclesCompost;
	}

	@Override
	public int getFermentationPerCycleFertilizer() {
		return fermentationPerCycleFertilizer;
	}

	@Override
	public int getFermentationPerCycleCompost() {
		return fermentationPerCycleCompost;
	}

	@Override
	public int getFermentedPerSapling() {
		return fermentedPerSapling;
	}

	@Override
	public int getFermentedPerCacti() {
		return fermentedPerCacti;
	}

	@Override
	public int getFermentedPerWheat() {
		return fermentedPerWheat;
	}

	@Override
	public int getFermentedPerCane() {
		return fermentedPerCane;
	}

	@Override
	public int getFermentedPerMushroom() {
		return fermentedPerMushroom;
	}

	@Override
	public int getSqueezedLiquidPerSeed() {
		return squeezedLiquidPerSeed;
	}
}
