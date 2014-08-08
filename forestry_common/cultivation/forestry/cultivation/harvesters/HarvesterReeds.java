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
import forestry.api.cultivation.CropProviders;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.core.utils.Vect;

public class HarvesterReeds extends Harvester {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new HarvesterReeds((TileMachine) tile);
		}
	}

	public HarvesterReeds(TileMachine machine) {
		super(machine, CropProviders.poaleCrops);
		this.area = new Vect(21, 6, 21);
		this.isSideSensitive = false;
	}

	@Override
	public String getName() {
		return "Sugar Cane Harvester";
	}

}
