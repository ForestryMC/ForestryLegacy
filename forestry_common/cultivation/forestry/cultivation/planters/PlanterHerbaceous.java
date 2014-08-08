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

public class PlanterHerbaceous extends Planter {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new PlanterHerbaceous((TileMachine) tile);
		}
	}

	public PlanterHerbaceous(TileMachine tile) {
		super(tile, CropProviders.herbaceousCrops);
		validSoil = new ItemStack(Block.dirt);
		validGround = new ItemStack(Block.tilledField);
		validWaste = new ItemStack(Block.dirt);
		validDisposal = new ItemStack(Block.sand);

		site = StructureBlueprint.getBlueprint("pumpkinArea");
		siteOffset = new Vect(-6, -1, -6);
		soil = StructureBlueprint.getBlueprint("pumpkinSoil");
		soilOffset = new Vect(-6, -1, -6);
		plantation = StructureBlueprint.getBlueprint("pumpkinFarm");
		plantationOffset = new Vect(-6, 0, -6);
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.planter.3");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.PumpkinFarmGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	public static final StructureBlueprint pumpkinArea = new StructureBlueprint("pumpkinArea", new Vect(13, 7, 13));
	public static final StructureBlueprint pumpkinSoil = new StructureBlueprint("pumpkinSoil", new Vect(13, 1, 13));
	public static final StructureBlueprint pumpkinFarm = new StructureBlueprint("pumpkinFarm", new Vect(13, 1, 13));

	static {
		int i = 0;
		int j = 0;
		i = Block.dirt.blockID;
		int[][] area = new int[][] { new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 1
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 2
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 3
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 4
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 5
				new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // 6
				new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // 7
				new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // 8
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 9
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 10
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 11
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 12
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 13
		};
		int[][] areaY = new int[][] { new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 4
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // 6
				new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // 7
				new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // 8
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 10
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 12
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
		};
		pumpkinArea.setPlane(0, area);
		pumpkinArea.setPlane(1, areaY);
		pumpkinArea.setPlane(2, areaY);
		pumpkinArea.setPlane(3, areaY);
		pumpkinArea.setPlane(4, areaY);
		pumpkinArea.setPlane(5, areaY);
		pumpkinArea.setPlane(6, areaY);

		i = Block.tilledField.blockID;
		j = Block.waterStill.blockID;
		int[][] soil = new int[][] { new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 1
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 2
				new int[] { i, i, i, i, j, i, i, i, i, j, i, i, i }, // 3
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 4
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 5
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 6
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 7
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 8
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 9
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 10
				new int[] { i, i, i, j, i, i, i, i, j, i, i, i, i }, // 11
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 12
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 13
		};
		pumpkinSoil.setPlane(0, soil);

		i = Block.sapling.blockID;
		int[][] plantation = new int[][] { new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 1
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2
				new int[] { 0, i, i, 0, 0, 0, i, i, 0, 0, 0, i, 0 }, // 3
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 4
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 5
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 6
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 7
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 8
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 5
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 10
				new int[] { 0, i, 0, 0, 0, i, i, 0, 0, 0, i, i, 0 }, // 11
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 12
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i }, // 13
		};
		pumpkinFarm.setPlane(0, plantation);

	}
}
