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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Defaults;
import forestry.core.gui.GfxSlot;
import forestry.core.gui.GfxSlotManager;
import forestry.core.gui.LiquidTankSlot;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.energy.gadgets.EngineBronze;

public class GuiEngineBronze extends GuiEngine {

	protected class BiogasSlot extends GfxSlot {

		EngineBronze engine;

		public BiogasSlot(GfxSlotManager manager, int xPos, int yPos, EngineBronze engine) {
			super(manager, xPos, yPos);
			this.engine = engine;
			this.height = 16;
		}

		@Override
		public void draw(int startX, int startY) {

			if (engine == null || engine.totalTime <= 0)
				return;

			Object content = null;
			int liquidImgIndex = 0;
			int squaled = (engine.burnTime * height) / engine.totalTime;
			if (squaled > height) {
				squaled = height;
			}

			if (engine.currentLiquidId <= 0)
				return;

			int liquidId = engine.currentLiquidId;
			int liquidMeta = engine.currentLiquidMeta;
			if (liquidId < Block.blocksList.length && Block.blocksList[liquidId] != null) {
				content = Block.blocksList[liquidId];
				liquidImgIndex = Block.blocksList[liquidId].blockIndexInTexture;
				Proxies.common.bindTexture(((Block) content).getTextureFile());
			} else {
				content = Item.itemsList[liquidId];
				liquidImgIndex = Item.itemsList[liquidId].getIconFromDamage(liquidMeta);
				Proxies.common.bindTexture(((Item) content).getTextureFile());
			}

			int imgLine = liquidImgIndex / 16;
			int imgColumn = liquidImgIndex - imgLine * 16;

			int start = 0;

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			while (true) {
				int x = 0;

				if (squaled > 16) {
					x = 16;
					squaled -= 16;
				} else {
					x = squaled;
					squaled = 0;
				}

				drawTexturedModalRect(startX + xPos, startY + yPos + height - x - start, imgColumn * 16, imgLine * 16, 16, 16 - (16 - x));
				start = start + 16;

				if (x == 0 || squaled == 0) {
					break;
				}
			}

			int tex = mc.renderEngine.getTexture(textureFile);
			mc.renderEngine.bindTexture(tex);
		}

		@Override
		public String getTooltip(EntityPlayer player) {
			if (engine.currentLiquidId <= 0)
				return StringUtil.localize("gui.empty");
			else {
				String tooltip = Item.itemsList[engine.currentLiquidId].getItemDisplayName(
						new ItemStack(engine.currentLiquidId, 1, engine.currentLiquidMeta));
				return tooltip;
			}
		}
	}

	public GuiEngineBronze(InventoryPlayer inventory, EngineBronze tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/bioengine.png", new ContainerEngineBronze(inventory, tile), tile);
		slotManager.add(new LiquidTankSlot(this.slotManager, 89, 19, tile, 0));
		slotManager.add(new LiquidTankSlot(this.slotManager, 107, 19, tile, 1));

		slotManager.add(new BiogasSlot(this.slotManager, 30, 47, tile));
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = StringUtil.localize("tile.engine.0");
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {

		drawBackground();
		EngineBronze engine = (EngineBronze) tile;

		int temp = engine.getOperatingTemperatureScaled(16);
		if (temp > 16) {
			temp = 16;
		}
		if (temp > 0) {
			drawTexturedModalRect(guiLeft + 53, guiTop + 47 + 16 - temp, 176, 60 + 16 - temp, 4, temp);
		}

	}

}
