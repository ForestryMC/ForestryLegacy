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
package forestry.core.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.ForestryItem;

public class LiquidHelper {

	public static boolean isEmptyLiquidData() {
		return LiquidContainerRegistry.getRegisteredLiquidContainerData().length <= 0;
	}

	public static boolean isEmptyContainer(ItemStack empty) {
		return LiquidContainerRegistry.isEmptyContainer(empty);
	}

	public static LiquidContainerData getEmptyContainer(ItemStack empty, LiquidStack liquid) {
		for (LiquidContainerData cont : LiquidContainerRegistry.getRegisteredLiquidContainerData())
			if (cont.stillLiquid.isLiquidEqual(liquid) && cont.container.isItemEqual(empty))
				return cont;

		return null;
	}

	public static LiquidContainerData getLiquidContainer(ItemStack container) {
		for (LiquidContainerData cont : LiquidContainerRegistry.getRegisteredLiquidContainerData())
			if (cont.filled.isItemEqual(container))
				return cont;
		return null;
	}

	public static LiquidContainerData createLiquidData(String ident, LiquidStack stillLiquid, ItemStack filled, ItemStack container) {
		LiquidStack still = LiquidDictionary.getOrCreateLiquid(ident, stillLiquid);
		return new LiquidContainerData(still, filled, container);
	}

	public static void injectLiquidContainer(LiquidContainerData container) {
		injectLiquidContainer(container, null, 0);
	}

	public static void injectWaxContainer(LiquidContainerData container) {
		injectLiquidContainer(container, new ItemStack(ForestryItem.beeswax), 10);
	}

	public static void injectRefractoryContainer(LiquidContainerData container) {
		injectLiquidContainer(container, new ItemStack(ForestryItem.refractoryWax), 10);
	}

	public static void injectTinContainer(LiquidContainerData container) {
		injectLiquidContainer(container, ForestryItem.ingotTin, 5);
	}

	public static void injectLiquidContainer(LiquidContainerData container, ItemStack remnant, int chance) {
		LiquidContainerRegistry.registerLiquid(container);

		if (RecipeManagers.squeezerManager != null) {
			if (!container.container.getItem().hasContainerItem())
				if (remnant != null) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filled }, container.stillLiquid, remnant, chance);
				} else {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filled }, container.stillLiquid);
				}
		}

		if (RecipeManagers.bottlerManager != null) {
			RecipeManagers.bottlerManager.addRecipe(5, container.stillLiquid, container.container, container.filled);
		}
	}

}
