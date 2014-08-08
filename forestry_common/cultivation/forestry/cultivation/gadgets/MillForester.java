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
package forestry.cultivation.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import forestry.api.core.ForestryAPI;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMill;
import forestry.core.network.GuiId;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Vect;
import forestry.cultivation.Overgrowth;
import forestry.cultivation.OvergrowthStrict;
import forestry.cultivation.OvergrowthTyped;
import forestry.plugins.PluginIC2;

public class MillForester extends MillGrower {

	public static class Factory extends MachineFactory {
		@Override
		public MillGrower createMachine(TileEntity tile) {
			return new MillForester((TileMill) tile);
		}
	}

	public MillForester(TileMill tile) {
		super(tile, new ItemStack(ForestryItem.vialCatalyst));
		putOvergrowth(new Overgrowth(new ItemStack(Block.sapling), new ItemStack(Block.wood)));
		putOvergrowth(new OvergrowthTyped(new ItemStack(ForestryBlock.sapling, 1, -1), new ItemStack(Block.wood))); // Normal
																													// saplings
		if (PluginIC2.instance.isAvailable()) {
			putOvergrowth(new OvergrowthTyped(new ItemStack(ForestryBlock.firsapling, 1, 1), PluginIC2.rubberwood)); // Rubber
		}
		// saplings
		putOvergrowth(new OvergrowthStrict(new ItemStack(Block.crops, 1, 0), new ItemStack(Block.crops, 1, 7))); // Wheat
		putOvergrowth(new OvergrowthStrict(new ItemStack(Block.crops, 1, 1), new ItemStack(Block.crops, 1, 7))); // Wheat
		putOvergrowth(new OvergrowthStrict(new ItemStack(Block.crops, 1, 2), new ItemStack(Block.crops, 1, 7))); // Wheat
		putOvergrowth(new OvergrowthStrict(new ItemStack(Block.crops, 1, 3), new ItemStack(Block.crops, 1, 7))); // Wheat
		putOvergrowth(new OvergrowthStrict(new ItemStack(Block.crops, 1, 4), new ItemStack(Block.crops, 1, 7))); // Wheat
		putOvergrowth(new OvergrowthStrict(new ItemStack(Block.crops, 1, 5), new ItemStack(Block.crops, 1, 7))); // Wheat
		putOvergrowth(new OvergrowthStrict(new ItemStack(Block.crops, 1, 6), new ItemStack(Block.crops, 1, 7))); // Wheat
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.mill.0");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.ForesterGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	@Override
	public void growCrop(World world, int cropId, Vect pos) {

		if (cropId == Block.sapling.blockID) {
			((BlockSapling) Block.sapling).growTree(world, pos.x, pos.y, pos.z, world.rand);
		}

		if (cropId == ForestryBlock.firsapling.blockID) {
			((BlockFirSapling) ForestryBlock.firsapling).growTree(world, pos.x, pos.y, pos.z, world.rand);
		}

		if (cropId == ForestryBlock.sapling.blockID) {
			((BlockSaplings) ForestryBlock.sapling).growTree(world, pos.x, pos.y, pos.z, world.rand);
		}

		if (cropId == Block.crops.blockID) {
			((BlockCrops) Block.crops).fertilize(world, pos.x, pos.y, pos.z);
		}

		if (cropId == Block.netherStalk.blockID) {
			world.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, Block.netherStalk.blockID, 3);
		}
	}
}
