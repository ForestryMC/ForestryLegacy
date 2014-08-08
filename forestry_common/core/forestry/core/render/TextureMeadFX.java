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

public class TextureMeadFX extends TextureLiquidsFX {

	public TextureMeadFX() {
		super(210, 230, 180, 200, 110, 120, ForestryItem.liquidMead.getIconFromDamage(0), ForestryItem.liquidMead.getTextureFile());
	}

}
