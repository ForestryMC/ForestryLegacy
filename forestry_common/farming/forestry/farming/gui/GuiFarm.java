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
package forestry.farming.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Defaults;
import forestry.core.gui.GfxSlot;
import forestry.core.gui.GfxSlotManager;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.Ledger;
import forestry.core.gui.LiquidTankSlot;
import forestry.core.gui.SocketSlot;
import forestry.core.utils.StringUtil;
import forestry.farming.gadgets.TileFarmPlain;

public class GuiFarm extends GuiForestry {

	protected class FarmLedger extends Ledger {
		
		private TileFarmPlain farm;

		public FarmLedger(TileFarmPlain farm) {
			super(ledgerManager);
			this.farm = farm;
			maxHeight = 118;
			overlayColor = fontColor.get("ledger.farm.background");
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			drawIcon(Defaults.TEXTURE_ICONS_MINECRAFT, Item.bucketWater.getIconFromDamage(0), x + 3, y + 4);

			if (!isFullyOpened())
				return;

			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.hydration"), x + 22, y + 8, fontColor.get("ledger.power.header"));
			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.hydr.heat") + ":", x + 22, y + 20, fontColor.get("ledger.power.subheader"));
			fontRenderer.drawString(StringUtil.floatAsPercent(farm.getHydrationTempModifier()), x + 22, y + 32, fontColor.get("ledger.power.text"));
			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.hydr.humid") + ":", x + 22, y + 44, fontColor.get("ledger.power.subheader"));
			fontRenderer.drawString(StringUtil.floatAsPercent(farm.getHydrationHumidModifier()), x + 22, y + 56, fontColor.get("ledger.power.text"));
			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.hydr.rainfall") + ":", x + 22, y + 68, fontColor.get("ledger.power.subheader"));
			fontRenderer.drawString(StringUtil.floatAsPercent(farm.getHydrationRainfallModifier()) + " (" + farm.getDrought() + " d)", x + 22, y + 80, fontColor.get("ledger.power.text"));
			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.hydr.overall") + ":", x + 22, y + 92, fontColor.get("ledger.power.subheader"));
			fontRenderer.drawString(StringUtil.floatAsPercent(farm.getHydrationModifier()), x + 22, y + 104, fontColor.get("ledger.power.text"));

		}

		@Override
		public String getTooltip() {
			return StringUtil.floatAsPercent(farm.getHydrationModifier()) + " " + StringUtil.localize("gui.hydration");
		}

	}
	
	private static class FarmLogicSlot extends GfxSlot {

		private TileFarmPlain tile;
		private int slot;
		
		public FarmLogicSlot(GfxSlotManager manager, int xPos, int yPos, TileFarmPlain tile, int slot) {
			super(manager, xPos, yPos);
			this.tile = tile;
			this.slot = slot;
		}

		private IFarmLogic getLogic() {
			return this.tile.getFarmLogics()[slot];
		}
		
		private int getIconIndex() {
			if(getLogic() == null)
				return 0;
			return getLogic().getIconIndex();
		}

		private String getTexture() {
			if(getLogic() == null)
				return "";
			
			return getLogic().getTextureFile();
		}

		@Override
		public void draw(int startX, int startY) {
			if(getLogic() == null)
				return;
			
			if (getTexture() != null && !getTexture().isEmpty()) {
				int texture = manager.minecraft.renderEngine.getTexture(getTexture());
				manager.minecraft.renderEngine.bindTexture(texture);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);

				int textureI = getIconIndex() >> 4;
				int textureJ = getIconIndex() - textureI * 16;

				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 16 * textureJ, 16 * textureI, 16, 16);
			}

		}

		@Override
		protected String getTooltip(EntityPlayer player) {
			if(getLogic() != null)
				return getLogic().getName();
			else
				return null;
		}

		@Override
		protected void addInformation(ArrayList list, EntityPlayer player) {
			if(getLogic() == null)
				return;
			list.add("Fertilizer: " + getLogic().getFertilizerConsumption());
			list.add("Water: " + getLogic().getWaterConsumption(tile.getHydrationModifier()));
		}

	}
	
	public GuiFarm(EntityPlayer player, TileFarmPlain tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/mfarm.png", new ContainerFarm(player.inventory, tile), tile);
		
		slotManager.add(new LiquidTankSlot(slotManager, 15, 19, tile.getTank(), 0).setOverlayOrigin(216, 18));

		slotManager.add(new SocketSlot(slotManager, 69, 40, tile, 0));

		slotManager.add(new FarmLogicSlot(slotManager, 69, 22, tile, 0));
		slotManager.add(new FarmLogicSlot(slotManager, 69, 58, tile, 1));
		slotManager.add(new FarmLogicSlot(slotManager, 51, 40, tile, 2));
		slotManager.add(new FarmLogicSlot(slotManager, 87, 40, tile, 3));
		
		this.xSize = 216;
		this.ySize = 220;
		this.tile = tile;
	}

	@Override
	protected void initLedgers(Object inventory) {
		super.initLedgers(inventory);
		ledgerManager.insert(new FarmLedger((TileFarmPlain)tile));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String title = StringUtil.localize("Farm");
		this.fontRenderer.drawString(title, getCenteredOffset(title), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		drawBackground();
		
		// Fuel remaining
		int fertilizerRemain = ((TileFarmPlain)tile).getStoredFertilizerScaled(16);
		if (fertilizerRemain > 0) {
			drawTexturedModalRect(guiLeft + 81, guiTop + 94 + 17 - fertilizerRemain, xSize, 17 - fertilizerRemain, 4, fertilizerRemain);
		}


	}

}
