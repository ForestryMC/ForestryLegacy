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

import forestry.core.config.Defaults;

public abstract class Trigger extends buildcraft.api.gates.Trigger {

	private int iconIndex;
	
	public Trigger(int id, int iconIndex) {
		super(id);
		this.iconIndex = iconIndex;
	}

	@Override
	public String getTextureFile() {
		return Defaults.TEXTURE_TRIGGERS;
	}

	@Override
	public int getIndexInTexture() {
		return iconIndex;
	}

}
