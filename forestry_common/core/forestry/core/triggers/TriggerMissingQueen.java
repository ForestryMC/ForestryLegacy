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
import forestry.core.EnumErrorCode;
import forestry.core.interfaces.IErrorSource;
import forestry.core.utils.StringUtil;

public class TriggerMissingQueen extends Trigger {

	public TriggerMissingQueen(int id) {
		super(id, 4);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("trigger.missingQueen");
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ITriggerParameter parameter) {

		if (!(tile instanceof IErrorSource))
			return false;

		return ((IErrorSource) tile).getErrorState() == EnumErrorCode.NOQUEEN;
	}
}
