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
package forestry.cultivation.gadgets;

import forestry.core.gadgets.GadgetManager;
import forestry.core.gadgets.MachinePackage;
import forestry.core.gadgets.TileMachine;

public class TilePlanter extends TileMachine {
	@Override
	protected MachinePackage getPackage(int meta) {
		return GadgetManager.getPlanterPackage(meta);
	}

	@Override
	protected boolean hasPackage(int meta) {
		return GadgetManager.hasPlanterPackage(meta);
	}

}
