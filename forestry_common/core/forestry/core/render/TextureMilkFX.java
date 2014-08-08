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

public class TextureMilkFX extends TextureLiquidsFX {

	public TextureMilkFX() {
		super(235, 255, 235, 255, 235, 255, ForestryItem.liquidMilk.getIconFromDamage(0), ForestryItem.liquidMilk.getTextureFile());
	}

}
