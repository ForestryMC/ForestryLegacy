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
package forestry.core;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.PowerFramework;

public class BioPowerFramework extends PowerFramework {
	@Override
	public IPowerProvider createPowerProvider() {
		return new BioPowerProvider();
	}
}
