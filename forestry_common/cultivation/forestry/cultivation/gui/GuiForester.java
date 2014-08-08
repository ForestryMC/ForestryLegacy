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
import forestry.core.gadgets.TileMill;
import forestry.core.gui.GuiForestry;

public class GuiForester extends GuiForestry {

	public GuiForester(InventoryPlayer inventory, TileMill arboretum) {
		super(Defaults.TEXTURE_PATH_GUI + "/forester.png", new ContainerForester(inventory, arboretum), arboretum);
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		this.fontRenderer.drawString(tile.getInvName(), getCenteredOffset(tile.getInvName()), 6, fontColor.get("gui.title"));
		this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {
		drawBackground();
	}
}
