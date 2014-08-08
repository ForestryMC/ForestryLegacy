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
package forestry.cultivation.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.cultivation.gadgets.TilePlanter;

public class GuiPlantation extends GuiPlanter {

	public GuiPlantation(InventoryPlayer inventory, TilePlanter arboretum) {
		super(Defaults.TEXTURE_PATH_GUI + "/plantation.png", new ContainerPlantation(inventory, arboretum), arboretum);
	}

}
