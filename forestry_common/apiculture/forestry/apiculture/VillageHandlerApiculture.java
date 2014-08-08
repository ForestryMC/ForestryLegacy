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
package forestry.apiculture;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureVillagePieceWeight;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.apiculture.genetics.BeeTemplates;
import forestry.apiculture.items.ItemHoneycomb;
import forestry.apiculture.worldgen.ComponentVillageBeeHouse;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;

public class VillageHandlerApiculture implements IVillageCreationHandler, IVillageTradeHandler {

	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		recipeList.add(new MerchantRecipe(new ItemStack(ForestryItem.beePrincessGE, 1, -1), new ItemStack(Item.emerald, 1)));
		recipeList.add(new MerchantRecipe(new ItemStack(Item.wheat, 2), new ItemStack(ForestryItem.beeComb, 1, ((ItemHoneycomb) ForestryItem.beeComb)
				.getRandomCombType(random, false))));
		recipeList.add(new MerchantRecipe(new ItemStack(Block.wood, 24, -1), new ItemStack(ForestryBlock.machine, 1, Defaults.ID_PACKAGE_MACHINE_APIARY)));
		recipeList.add(new MerchantRecipe(new ItemStack(Item.emerald, 1), new ItemStack(ForestryItem.frameProven, 6)));
		recipeList.add(new MerchantRecipe(new ItemStack(Item.emerald, 12), new ItemStack(ForestryItem.beePrincessGE, 1, -1),
				BeeManager.beeInterface.getBeeStack(BeeManager.beeInterface.getBee(
						villager.worldObj, BeeManager.beeInterface.templateAsGenome(BeeTemplates.getMonasticTemplate())),
						EnumBeeType.DRONE)));
	}

	@Override
	public StructureVillagePieceWeight getVillagePieceWeight(Random random, int size) {
		return new StructureVillagePieceWeight(ComponentVillageBeeHouse.class, 15, MathHelper.getRandomIntegerInRange(random, 0 + size, 1 + size));
	}

	@Override
	public Class<?> getComponentClass() {
		return ComponentVillageBeeHouse.class;
	}

	@Override
	public Object buildComponent(StructureVillagePieceWeight villagePiece, ComponentVillageStartPiece startPiece, List pieces, Random random, int p1, int p2,
			int p3, int p4, int p5) {
		return ComponentVillageBeeHouse.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
	}

}
