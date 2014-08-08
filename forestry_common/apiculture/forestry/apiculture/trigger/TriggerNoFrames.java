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
package forestry.apiculture.trigger;

import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.ITriggerParameter;
import forestry.apiculture.gadgets.MachineApiary;
import forestry.core.gadgets.TileMachine;
import forestry.core.triggers.Trigger;
import forestry.core.utils.StringUtil;

public class TriggerNoFrames extends Trigger {

	public TriggerNoFrames(int id) {
		super(id, 20);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("trigger.noFrames");
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ITriggerParameter parameter) {

		if (!(tile instanceof TileMachine))
			return false;

		TileMachine machine = (TileMachine) tile;
		if (machine.machine == null)
			return false;

		for (int i = MachineApiary.SLOT_FRAMES_1; i < MachineApiary.SLOT_FRAMES_1 + MachineApiary.SLOT_FRAMES_COUNT; i++) {
			if (machine.getStackInSlot(i) == null) {
				continue;
			} else
				return false;
		}

		return true;

	}

}
