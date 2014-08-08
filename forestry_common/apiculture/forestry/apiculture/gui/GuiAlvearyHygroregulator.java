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
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.LiquidTankSlot;
import forestry.core.utils.StringUtil;

public class GuiAlvearyHygroregulator extends GuiForestry {

	public GuiAlvearyHygroregulator(InventoryPlayer inventory, TileAlvearyHygroregulator tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/hygroregulator.png", new ContainerAlvearyHygroregulator(inventory, tile));

		slotManager.add(new LiquidTankSlot(this.slotManager, 104, 17, tile, 0));
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = StringUtil.localize("tile.alveary.5");
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		drawBackground();
	}

}
