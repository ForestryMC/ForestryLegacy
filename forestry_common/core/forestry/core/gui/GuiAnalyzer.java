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
package forestry.core.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gadgets.MachineAnalyzer;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.Utils;

public class GuiAnalyzer extends GuiForestry {

	public GuiAnalyzer(InventoryPlayer inventory, MachineAnalyzer tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/alyzer.png", new ContainerAnalyzer(inventory, tile), tile);
		ySize = 176;
		slotManager.add(new LiquidTankSlot(this.slotManager, 95, 24, tile, 0));
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		this.fontRenderer.drawString(tile.getInvName(), getCenteredOffset(tile.getInvName()), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {

		drawBackground();
		MachineAnalyzer machine = (MachineAnalyzer) tile;
		drawAnalyzeMeter(guiLeft + 64, guiTop + 30, machine.getProgressScaled(46), Utils.rateTankLevel(machine.getProgressScaled(100)));

	}

	private void drawAnalyzeMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176;
		int k = 60;
		switch (rated) {
		case EMPTY:
			break;
		case LOW:
			i += 4;
			break;
		case MEDIUM:
			i += 8;
			break;
		case HIGH:
			i += 12;
			break;
		case MAXIMUM:
			i += 16;
			break;
		}

		drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

}
