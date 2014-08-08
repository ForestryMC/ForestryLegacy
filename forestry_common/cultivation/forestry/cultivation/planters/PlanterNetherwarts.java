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
package forestry.cultivation.planters;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import forestry.api.core.ForestryAPI;
import forestry.api.cultivation.CropProviders;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.network.GuiId;
import forestry.core.utils.StringUtil;
import forestry.core.utils.StructureBlueprint;
import forestry.core.utils.Vect;

public class PlanterNetherwarts extends Planter {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new PlanterNetherwarts((TileMachine) tile);
		}
	}

	public PlanterNetherwarts(TileMachine tile) {
		super(tile, CropProviders.infernalCrops);

		validSoil = new ItemStack(Block.slowSand);
		validGround = new ItemStack(Block.slowSand);
		validWaste = new ItemStack(Block.dirt);
		validDisposal = new ItemStack(Block.sand);

		site = StructureBlueprint.getBlueprint("defaultFarm");
		siteOffset = new Vect(-7, 0, -7);
		soil = StructureBlueprint.getBlueprint("netherwartSoil");
		soilOffset = new Vect(-7, 0, -7);
		plantation = StructureBlueprint.getBlueprint("netherwartPlantation");
		plantationOffset = new Vect(-7, 1, -7);

	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.planter.6");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.NetherFarmGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	public static final StructureBlueprint netherwartSoil = new StructureBlueprint("netherwartSoil", new Vect(15, 1, 15));
	public static final StructureBlueprint netherwartPlantation = new StructureBlueprint("netherwartPlantation", new Vect(15, 1, 15));

	static {
		int i = 0;

		i = Block.slowSand.blockID;
		int[][] farmedSoil = new int[][] { new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 1
				new int[] { i, 0, i, i, 0, i, i, 0, i, i, 0, i, i, 0, i }, // 2
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 3
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 4
				new int[] { i, 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0, i }, // 5
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 6
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 7
				new int[] { i, 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0, i }, // 8
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 9
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 10
				new int[] { i, 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0, i }, // 11
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 12
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 13
				new int[] { i, 0, i, i, 0, i, i, 0, i, i, 0, i, i, 0, i }, // 14
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 15
		};
		netherwartSoil.setPlane(0, farmedSoil);

		i = Block.sapling.blockID;
		int[][] wheatPlants = new int[][] { new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 1
				new int[] { i, 0, i, i, 0, i, i, 0, i, i, 0, i, i, 0, i }, // 2
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 3
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 4
				new int[] { i, 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0, i }, // 5
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 6
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 7
				new int[] { i, 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0, i }, // 8
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 9
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 10
				new int[] { i, 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0, i }, // 11
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 12
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 13
				new int[] { i, 0, i, i, 0, i, i, 0, i, i, 0, i, i, 0, i }, // 14
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 15
		};
		netherwartPlantation.setPlane(0, wheatPlants);
	}

}
