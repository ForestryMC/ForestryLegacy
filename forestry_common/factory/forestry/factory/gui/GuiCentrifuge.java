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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileMachine;
import forestry.core.gui.GuiForestry;
import forestry.factory.gadgets.MachineCentrifuge;

public class GuiCentrifuge extends GuiForestry {

	public GuiCentrifuge(InventoryPlayer inventory, TileMachine tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/centrifuge.png", new ContainerCentrifuge(inventory, tile), tile);
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = tile.getInvName();
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		drawBackground();
		MachineCentrifuge machine = (MachineCentrifuge) tile.getMachine();

		int progress = 16 - machine.getProgressScaled(16);
		drawTexturedModalRect(guiLeft + 62, guiTop + 36 + 17 - progress, 176, 17 - progress, 4, progress);
	}
}
