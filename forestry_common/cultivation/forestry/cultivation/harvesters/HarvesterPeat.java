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
package forestry.cultivation.harvesters;

import net.minecraft.tileentity.TileEntity;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.utils.Vect;
import forestry.cultivation.providers.CropProviderPeat;

public class HarvesterPeat extends Harvester {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new HarvesterPeat((TileMachine) tile);
		}
	}

	public HarvesterPeat(TileMachine tile) {
		super(tile, new CropProviderPeat());
		this.area = new Vect(21, 6, 21);
		this.isSideSensitive = false;
	}

	@Override
	public String getName() {
		return "Turbary";
	}

	public static String dir_51 = "nicl";

}
