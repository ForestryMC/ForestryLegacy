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
package forestry.core.interfaces;

import net.minecraft.item.ItemStack;

public interface IGameMode {

	String getIdentifier();

	float getEnergyDemandModifier();

	ItemStack getRecipeFertilizerOutputApatite();

	ItemStack getRecipeFertilizerOutputAsh();

	ItemStack getRecipeCompostOutputWheat();

	ItemStack getRecipeCompostOutputAsh();

	ItemStack getRecipeHumusOutputFertilizer();

	ItemStack getRecipeHumusOutputCompost();

	ItemStack getRecipeBogEarthOutputBucket();

	ItemStack getRecipeBogEarthOutputCans();

	ItemStack getRecipeCanOutput();

	ItemStack getRecipeCapsuleOutput();

	ItemStack getRecipeRefractoryOutput();

	int getFermentedPerSapling();

	int getFermentedPerCacti();

	int getFermentedPerWheat();

	int getFermentedPerCane();

	int getFermentedPerMushroom();

	int getSqueezedLiquidPerSeed();

	int getFermentationCyclesFertilizer();

	int getFermentationCyclesCompost();

	int getFermentationPerCycleFertilizer();

	int getFermentationPerCycleCompost();

	int getFertilizerFarmValue();

}
