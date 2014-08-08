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

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IFlowerProvider;
import forestry.api.genetics.IPollinatable;
import forestry.core.utils.StringUtil;

public class FlowerProviderMushroom implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IBeeGenome species, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);

		// Specific check for flower pots.
		if (blockid == Block.flowerPot.blockID)
			return checkFlowerPot(world.getBlockMetadata(x, y, z));

		return blockid == Block.mushroomBrown.blockID || blockid == Block.mushroomRed.blockID;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();
		return types.size() > 1 || !types.contains(EnumPlantType.Nether);
	}
	
	private boolean checkFlowerPot(int meta) {
		if (meta == 7 || meta == 8)
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

		if (groundid != Block.mycelium.blockID)
			return false;

		int mushroomId;
		if (world.rand.nextBoolean()) {
			mushroomId = Block.mushroomBrown.blockID;
		} else {
			mushroomId = Block.mushroomRed.blockID;
		}

		world.setBlockAndMetadataWithNotify(x, y, z, mushroomId, 0);
		return true;
	}

	private boolean growInPot(World world, int x, int y, int z) {
		if (world.rand.nextBoolean()) {
			world.setBlockAndMetadataWithNotify(x, y, z, Block.flowerPot.blockID, 7);
		} else {
			world.setBlockAndMetadataWithNotify(x, y, z, Block.flowerPot.blockID, 8);
		}
		return true;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("flowers.mushroom");
	}

	@Override
	public ItemStack[] affectProducts(World world, IBeeGenome genome, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return new ItemStack[] { new ItemStack(Block.mushroomBrown), new ItemStack(Block.mushroomRed) };
	}

}
