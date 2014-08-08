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
package forestry.mail.triggers;

import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.ITriggerParameter;
import forestry.core.gadgets.TileMachine;
import forestry.core.triggers.Trigger;
import forestry.core.utils.StringUtil;
import forestry.mail.IMailContainer;

public class TriggerHasMail extends Trigger {

	public TriggerHasMail(int id) {
		super(id, 11);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("trigger.hasMail");
	}

	@Override
	public boolean isTriggerActive(TileEntity tile, ITriggerParameter parameter) {

		if (!(tile instanceof TileMachine))
			return false;

		TileMachine machine = (TileMachine) tile;

		if (!(machine.machine instanceof IMailContainer))
			return false;

		return ((IMailContainer) machine.machine).hasMail();
	}

}
