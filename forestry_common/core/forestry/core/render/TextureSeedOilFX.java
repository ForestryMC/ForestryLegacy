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

public class TextureSeedOilFX extends TextureLiquidsFX {

	public TextureSeedOilFX() {
		super(215, 235, 215, 235, 145, 155, ForestryItem.liquidSeedOil.getIconFromDamage(0), ForestryItem.liquidSeedOil.getTextureFile());
	}

}
