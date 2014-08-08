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
package forestry.core.render;

import forestry.core.config.ForestryItem;

public class TextureBiofuelFX extends TextureLiquidsFX {

	public TextureBiofuelFX() {
		super(220, 255, 120, 160, 10, 30, ForestryItem.liquidBiofuel.getIconFromDamage(0), ForestryItem.liquidBiofuel.getTextureFile());
	}
}
