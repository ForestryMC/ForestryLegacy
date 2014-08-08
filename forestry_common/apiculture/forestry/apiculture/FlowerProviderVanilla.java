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

import java.util.Collections;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IFlowerProvider;
import forestry.api.genetics.IPollinatable;
import forestry.core.utils.StringUtil;

public class FlowerProviderVanilla implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IBeeGenome species, int x, int y, int z) {

		int blockid = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		// Specific check for flower pots.
		if (blockid == Block.flowerPot.blockID)
			return checkFlowerPot(meta);

		ItemStack flower = new ItemStack(blockid, 1, meta);

		for (ItemStack stack : FlowerManager.plainFlowers)
			if (flower.isItemEqual(stack))
				return true;

		return false;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();
		return types.size() > 1 || !types.contains(EnumPlantType.Nether);
	}
	
	private boolean checkFlowerPot(int meta) {
		if (meta == 1 || meta == 2)
			return true;
		else
			return false;
	}

	@Override
	public boolean growFlower(World world, IBeeGenome species, int x, int y, int z) {

		int blockid = world.getBlockId(x, y, z);

		if (blockid != 0) {
			if (blockid == Block.flowerPot.blockID)
				return growInPot(world, x, y, z);
			else
				return false;
		}

		// Check ground
		int groundid = world.getBlockId(x, y - 1, z);

		if (groundid != Block.dirt.blockID && groundid != Block.grass.blockID)
			return false;

		// Determine flower to plant
		Collections.shuffle(FlowerManager.plainFlowers);
		ItemStack flower = FlowerManager.plainFlowers.get(world.rand.nextInt(FlowerManager.plainFlowers.size() - 1));
		world.setBlockAndMetadataWithNotify(x, y, z, flower.itemID, flower.getItemDamage());
		return true;
	}

	private boolean growInPot(World world, int x, int y, int z) {
		if (world.rand.nextBoolean()) {
			world.setBlockAndMetadataWithNotify(x, y, z, Block.flowerPot.blockID, 1);
		} else {
			world.setBlockAndMetadataWithNotify(x, y, z, Block.flowerPot.blockID, 2);
		}
		return true;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("flowers.vanilla");
	}

	@Override
	public ItemStack[] affectProducts(World world, IBeeGenome genome, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return FlowerManager.plainFlowers.toArray(new ItemStack[0]);
	}

}
