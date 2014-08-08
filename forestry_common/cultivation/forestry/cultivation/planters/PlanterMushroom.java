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

public class PlanterMushroom extends Planter {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new PlanterMushroom((TileMachine) tile);
		}
	}

	public PlanterMushroom(TileMachine tile) {
		super(tile, CropProviders.fungalCrops);

		validSoil = new ItemStack(Block.mycelium);
		validGround = new ItemStack(Block.mycelium);
		validWaste = new ItemStack(Block.dirt);
		validDisposal = new ItemStack(Block.dirt);

		site = StructureBlueprint.getBlueprint("mushroomFarm");
		siteOffset = new Vect(-8, 0, -8);
		soil = StructureBlueprint.getBlueprint("mushroomSoil");
		soilOffset = new Vect(-6, 0, -6);
		plantation = StructureBlueprint.getBlueprint("mushroomPlantation");
		plantationOffset = new Vect(-6, 1, -6);
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.planter.5");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.MushroomFarmGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	public static final StructureBlueprint defaultShroom = new StructureBlueprint("mushroomFarm", new Vect(17, 10, 17));
	public static final StructureBlueprint defaultSoil = new StructureBlueprint("mushroomSoil", new Vect(13, 1, 13));
	public static final StructureBlueprint defaultPlantation = new StructureBlueprint("mushroomPlantation", new Vect(13, 1, 13));

	static {
		int i = 0;

		i = Block.brick.blockID;
		int[][] farm0 = new int[][] { new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 1
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 3
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 4
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 5
				new int[] { 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0 }, // 6
				new int[] { 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0 }, // 7
				new int[] { 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0 }, // 8
				new int[] { 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0 }, // 9
				new int[] { 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0 }, // 10
				new int[] { 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0 }, // 11
				new int[] { 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0 }, // 12
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 13
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 14
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 15
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 16
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 17
		};
		defaultShroom.setPlane(0, farm0);
		defaultShroom.setPlane(1, farm0);
		defaultShroom.setPlane(2, farm0);
		defaultShroom.setPlane(3, farm0);
		defaultShroom.setPlane(4, farm0);
		defaultShroom.setPlane(5, farm0);
		defaultShroom.setPlane(6, farm0);
		defaultShroom.setPlane(7, farm0);
		defaultShroom.setPlane(8, farm0);
		defaultShroom.setPlane(9, farm0);

		i = Block.mycelium.blockID;
		int[][] soil = new int[][] { new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 1
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 2
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 3
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 4
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 5
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 6
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 7
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 8
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 9
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 10
				new int[] { i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i }, // 11
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 12
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 13
		};
		defaultSoil.setPlane(0, soil);

		i = Block.sapling.blockID;
		int[][] plantation = new int[][] { new int[] { i, 0, i, 0, i, 0, i, 0, i, 0, i, 0, i }, // 1
				new int[] { 0, i, 0, i, 0, i, 0, i, 0, i, 0, i, 0 }, // 2
				new int[] { i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, i }, // 3
				new int[] { 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0 }, // 4
				new int[] { i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, i }, // 5
				new int[] { 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0 }, // 6
				new int[] { i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, i }, // 7
				new int[] { 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0 }, // 8
				new int[] { i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, i }, // 9
				new int[] { 0, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, 0 }, // 10
				new int[] { i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, i }, // 11
				new int[] { 0, i, 0, i, 0, i, 0, i, 0, i, 0, i, 0 }, // 12
				new int[] { i, 0, i, 0, i, 0, i, 0, i, 0, i, 0, i } // 13
		};
		defaultPlantation.setPlane(0, plantation);

	}

}
