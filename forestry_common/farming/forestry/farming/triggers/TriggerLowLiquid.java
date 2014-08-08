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
package forestry.farming.triggers;

import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.ITriggerParameter;
import forestry.api.core.ITileStructure;
import forestry.core.triggers.Trigger;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TankSlot;
import forestry.farming.gadgets.TileFarmPlain;
import forestry.farming.gadgets.TileHatch;

public class TriggerLowLiquid extends Trigger {

	private float threshold = 0.25F;

	public TriggerLowLiquid(int id, float threshold) {
		super(id, 1);
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("trigger.lowLiquid") + " < " + threshold * 100 + "%";
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ITriggerParameter parameter) {
		if(!(tile instanceof TileHatch))
			return false;
		
		ITileStructure central = ((TileHatch)tile).getCentralTE();
		if(central == null || !(central instanceof TileFarmPlain))
			return false;
		
		TankSlot tank = ((TileFarmPlain)central).getTank();
		return ((float)tank.quantity / tank.capacity) <= threshold;
	}
	
}
