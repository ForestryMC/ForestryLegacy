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

public class FlowerProviderWheat implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IBeeGenome species, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		return blockid == Block.crops.blockID;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();
		return types.size() > 1 || !types.contains(EnumPlantType.Nether);
	}
	
	@Override
	public boolean growFlower(World world, IBeeGenome species, int x, int y, int z) {

		int blockid = world.getBlockId(x, y, z);

		// We only grow wheat, we don't plant it
		if (blockid != Block.crops.blockID)
			return false;

		int meta = world.getBlockMetadata(x, y, z);
		if (meta > 6)
			return false;

		if (meta < 6) {
			meta += 2;
		} else {
			meta = 7;
		}

		world.setBlockMetadataWithNotify(x, y, z, meta);
		return true;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("flowers.wheat");
	}

	@Override
	public ItemStack[] affectProducts(World world, IBeeGenome genome, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return new ItemStack[] { new ItemStack(Block.crops, 1, 8) };
	}

}
