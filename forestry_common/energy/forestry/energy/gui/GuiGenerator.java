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
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.LiquidTankSlot;
import forestry.energy.gadgets.MachineGenerator;

public class GuiGenerator extends GuiForestry {

	public GuiGenerator(InventoryPlayer inventory, MachineGenerator tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/generator.png", new ContainerGenerator(inventory, tile), tile);
		slotManager.add(new LiquidTankSlot(this.slotManager, 49, 17, tile, 0));
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		this.fontRenderer.drawString(tile.getInvName(), getCenteredOffset(tile.getInvName()), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {

		drawBackground();
		MachineGenerator boiler = (MachineGenerator) tile;

		int progress = boiler.getStoredScaled(49);
		if (progress > 0) {
			drawTexturedModalRect(guiLeft + 108, guiTop + 38, 176, 91, progress, 18);
		}
	}

}
