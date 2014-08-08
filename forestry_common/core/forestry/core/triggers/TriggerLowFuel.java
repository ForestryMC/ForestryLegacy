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
package forestry.core.triggers;

import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.ITriggerParameter;
import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TileMachine;
import forestry.core.utils.StringUtil;

public class TriggerLowFuel extends Trigger {

	private float threshold = 0.25F;

	public TriggerLowFuel(int id, float threshold) {
		super(id, 0);
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("trigger.fuel") + " < " + threshold * 100 + "%";
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ITriggerParameter parameter) {

		if (tile instanceof TileMachine) {
			TileMachine machine = (TileMachine) tile;
			return !machine.machine.hasFuelMin(threshold);
		}

		if (tile instanceof Engine) {
			Engine engine = (Engine) tile;
			return !engine.hasFuelMin(threshold);
		}

		return false;
	}

}
