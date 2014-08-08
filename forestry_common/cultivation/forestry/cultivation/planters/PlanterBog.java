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

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.ITrigger;
import forestry.api.core.ForestryAPI;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.network.GuiId;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.StringUtil;
import forestry.core.utils.StructureBlueprint;
import forestry.core.utils.Vect;

public class PlanterBog extends Planter {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new PlanterBog((TileMachine) tile);
		}
	}

	public PlanterBog(TileMachine tile) {

		super(tile);
		validSoil = new ItemStack(ForestryBlock.soil, 1, 1);
		validGround = new ItemStack(ForestryBlock.soil, 1, 1);
		validWaste = new ItemStack(Block.dirt);
		validDisposal = new ItemStack(Block.dirt);

		site = StructureBlueprint.getBlueprint("defaultFarm");
		siteOffset = new Vect(-7, 0, -7);
		soil = StructureBlueprint.getBlueprint("bogEarth");
		soilOffset = new Vect(-7, 0, -7);

		requiresGermling = false;
	}

	@Override
	public String getName() {
		return StringUtil.localize("tile.planter.4");
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		player.openGui(ForestryAPI.instance, GuiId.PeatBogGUI.ordinal(), player.worldObj, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
	}

	public static final StructureBlueprint bogEarth = new StructureBlueprint("bogEarth", new Vect(15, 1, 15));

	static {
		int i = 0;
		int j = 0;

		i = ForestryBlock.soil.blockID;
		j = Block.waterStill.blockID;
		int[][] bogSoil = new int[][] { new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 1
				new int[] { i, j, i, i, j, i, i, j, i, i, j, i, i, j, i }, // 1
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 2
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 3
				new int[] { i, j, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, j, i }, // 4
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 5
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 6
				new int[] { i, j, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, j, i }, // 7
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 8
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 9
				new int[] { i, j, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, j, i }, // 10
				new int[] { i, i, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, i, i, i }, // 11
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 12
				new int[] { i, j, i, i, j, i, i, j, i, i, j, i, i, j, i }, // 13
				new int[] { i, i, i, i, i, i, i, i, i, i, i, i, i, i, i }, // 13
		};
		bogEarth.setPlane(0, bogSoil);
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.lowSoil25);
		res.add(ForestryTrigger.lowSoil10);
		return res;
	}
}
