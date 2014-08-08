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

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import forestry.core.config.Config;
import forestry.core.gadgets.TileForestry;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IEnergyConsumer;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.IOwnable;
import forestry.core.proxy.Proxies;
import forestry.core.utils.FontColour;

public abstract class GuiForestry extends GuiScreen {

	// / SLOTS
	protected GfxSlotManager slotManager;
	// / LEDGERS
	protected LedgerManager ledgerManager = new LedgerManager(this);

	protected TileForestry tile;
	protected String textureFile;

	/** Stacks renderer. Icons, stack size, health, etc... */
	protected static RenderItem itemRenderer = new RenderItem();

	/** The X size of the inventory window in pixels. */
	protected int xSize = 176;

	/** The Y size of the inventory window in pixels. */
	protected int ySize = 166;

	/** A list of the players inventory slots. */
	public ContainerForestry inventorySlots;

	/**
	 * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiLeft;

	/**
	 * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiTop;

	protected FontColour fontColor;

	// / PAGES
	protected int playerInventorySize;
	protected int pageCurrent = 0;
	protected int pageSize = 25;
	protected int pageMax = 1;

	public GuiForestry(ContainerForestry container) {
		this("", container, null, 1, 0);
	}

	public GuiForestry(String texture, ContainerForestry container) {
		this(texture, container, null, 1, 0);
	}

	public GuiForestry(String texture, ContainerForestry container, Object tile) {
		this(texture, container, tile, 1, 0);
	}

	public GuiForestry(String texture, ContainerForestry container, IInventory tile) {
		this(texture, container, tile, 1, tile.getSizeInventory());
	}

	public GuiForestry(String texture, ContainerForestry container, Object inventory, int pageMax, int pageSize) {
		this.slotManager = new GfxSlotManager(this);
		this.ledgerManager = new LedgerManager(this);

		this.textureFile = texture;
		this.inventorySlots = container;
		this.pageMax = pageMax;
		this.pageSize = pageSize;

		if (inventory instanceof TileForestry) {
			this.tile = (TileForestry) inventory;
		}

		fontColor = new FontColour(Proxies.common.getSelectedTexturePack(Proxies.common.getClientInstance()));
		initLedgers(inventory);
	}

	/* LEDGERS */
	protected void initLedgers(Object inventory) {

		if (inventory instanceof IErrorSource && ((IErrorSource) inventory).throwsErrors()) {
			ledgerManager.add(new ErrorLedger(ledgerManager, (IErrorSource) inventory));
		}

		if (inventory instanceof IClimatised && ((IClimatised) inventory).isClimatized()) {
			ledgerManager.add(new ClimateLedger(ledgerManager, (IClimatised) inventory));
		}

		if (!Config.disableEnergyStat && inventory instanceof IEnergyConsumer && ((IEnergyConsumer) inventory).consumesEnergy()) {
			ledgerManager.add(new PowerLedger(ledgerManager, (IEnergyConsumer) inventory));
		}

		if (!Config.disableHints && inventory instanceof IHintSource && ((IHintSource) inventory).hasHints()) {
			ledgerManager.add(new HintLedger(ledgerManager, (IHintSource) inventory));
		}

		if (inventory instanceof IOwnable && ((IOwnable) inventory).isOwnable()) {
			ledgerManager.add(new OwnerLedger(ledgerManager, (IOwnable) inventory));
		}

	}

	/* TEXT HELPER FUNCTIONS */
	private int column0;
	private int column1;
	private int column2;
	
	private int line;
	protected final float factor = 0.75f;
	

	protected final void startPage() {
		line = 12;
		GL11.glPushMatrix();
		GL11.glScalef(factor, factor, factor);
	}
	
	protected final void startPage(int column0, int column1, int column2) {
		
		this.column0 = column0;
		this.column1 = column1;
		this.column2 = column2;

		startPage();
	}
	
	protected final int adjustToFactor(int fixed) {
		return (int)(fixed*((float)1/factor));
	}
	
	protected final int getLineY() {
		return line;
	}
	
	protected final void newLine() {
		line += 12*factor;
	}
	
	protected final void newLine(int lineHeight) {
		line += lineHeight*factor;
	}
	
	protected final void endPage() {
		GL11.glPopMatrix();
	}
	
	protected final void drawRow(String text0, String text1, String text2, int colour0, int colour1, int colour2) {
		drawLine(text0, column0, colour0);
		drawLine(text1, column1, colour1);
		drawLine(text2, column2, colour2);
		newLine();
	}
	
	protected final void drawLine(String text, int x) {
		drawLine(text, x, fontColor.get("gui.screen"));
	}
	protected final void drawSplitLine(String text, int x, int maxWidth) {
		drawSplitLine(text, x, maxWidth, fontColor.get("gui.screen"));
	}
	
	protected final void drawCenteredLine(String text, int x, int width) {
		drawCenteredLine(text, x, width, fontColor.get("gui.screen"));
	}
	
	protected final void drawCenteredLine(String text, int x, int width, int color) {
		fontRenderer.drawString(text, 
				(int)((guiLeft + x)*(1/factor)) + (int)((adjustToFactor(width) - fontRenderer.getStringWidth(text))/2),
				(int)((guiTop + line)*(1/factor)),
				color);
	}
	
	protected final void drawLine(String text, int x, int color) {
		fontRenderer.drawString(text, (int)((guiLeft + x)*(1/factor)), (int)((guiTop + line)*(1/factor)), color);
	}
	
	protected final void drawSplitLine(String text, int x, int maxWidth, int color) {
		fontRenderer.drawSplitString(text, (int)((guiLeft + x)*(1/factor)), (int)((guiTop + line)*(1/factor)), (int)(maxWidth*(1/factor)), color);
	}
	
	/* CORE GUI HANDLING */
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		super.initGui();
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	protected void flipPage(int page) {
		pageCurrent = page;
	}

	protected int getCenteredOffset(String string) {
		return getCenteredOffset(string, xSize);
	}

	protected int getCenteredOffset(String string, int xWidth) {
		return (xWidth - fontRenderer.getStringWidth(string)) / 2;
	}

	protected void drawGuiContainerForegroundLayer() {
	}

	protected abstract void drawGuiContainerBackgroundLayer(float var1, int var2, int var3);

	@SuppressWarnings("rawtypes")
	protected void drawTooltip(int mouseX, int mouseY, float zLevel, List information, EnumRarity rarity) {

		if (information.size() > 0) {
			this.zLevel = 0f;
			itemRenderer.zLevel = 0f;

			int tooltipWidth = 0;

			for (int i = 0; i < information.size(); ++i) {
				int textWidth = this.fontRenderer.getStringWidth((String) information.get(i));

				if (textWidth > tooltipWidth) {
					tooltipWidth = textWidth;
				}
			}

			int xPos = mouseX - this.guiLeft + 12;
			int yPos = mouseY - this.guiTop - 12;
			int var14 = 8;

			if (information.size() > 1) {
				var14 += 2 + (information.size() - 1) * 10;
			}

			this.zLevel = zLevel;
			itemRenderer.zLevel = zLevel;
			int var15 = -267386864;
			this.drawGradientRect(xPos - 3, yPos - 4, xPos + tooltipWidth + 3, yPos - 3, var15, var15);
			this.drawGradientRect(xPos - 3, yPos + var14 + 3, xPos + tooltipWidth + 3, yPos + var14 + 4, var15, var15);
			this.drawGradientRect(xPos - 3, yPos - 3, xPos + tooltipWidth + 3, yPos + var14 + 3, var15, var15);
			this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + var14 + 3, var15, var15);
			this.drawGradientRect(xPos + tooltipWidth + 3, yPos - 3, xPos + tooltipWidth + 4, yPos + var14 + 3, var15, var15);
			int var16 = 1347420415;
			int var17 = (var16 & 16711422) >> 1 | var16 & -16777216;
			this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + var14 + 3 - 1, var16, var17);
			this.drawGradientRect(xPos + tooltipWidth + 2, yPos - 3 + 1, xPos + tooltipWidth + 3, yPos + var14 + 3 - 1, var16, var17);
			this.drawGradientRect(xPos - 3, yPos - 3, xPos + tooltipWidth + 3, yPos - 3 + 1, var16, var16);
			this.drawGradientRect(xPos - 3, yPos + var14 + 2, xPos + tooltipWidth + 3, yPos + var14 + 3, var17, var17);

			for (int i = 0; i < information.size(); ++i) {
				String line = (String) information.get(i);

				if (i == 0) {
					line = "\u00a7" + Integer.toHexString(rarity.rarityColor) + line;
				} else {
					line = "\u00a77" + line;
				}

				this.fontRenderer.drawStringWithShadow(line, xPos, yPos, -1);

				if (i == 0) {
					yPos += 2;
				}

				yPos += 10;
			}

			this.zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}

	}

	/**
	 * Draws the basic background texture centered on the screen and resets guiLeft and guiTop.
	 */
	protected void drawBackground() {

		guiLeft = (this.width - this.xSize) / 2;
		guiTop = (this.height - this.ySize) / 2;

		bindTexture();
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
	}

	protected void bindTexture() {
		bindTexture(textureFile);
	}

	protected void bindTexture(String texturePath) {
		int texture = this.mc.renderEngine.getTexture(texturePath);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texture);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		this.drawDefaultBackground();
		this.drawGuiContainerBackgroundLayer(par3, mouseX, mouseY);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef(this.guiLeft, this.guiTop, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		Slot mousedOverSlot = null;
		short var7 = 240;
		short var8 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var7 / 1.0F, var8 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Draw slots on the current page
		if (pageSize > 0) {
			for (int i = pageCurrent * pageSize; i < (pageCurrent + 1) * pageSize; ++i) {
				Slot mousedSlot = drawSlot(i, mouseX, mouseY);
				if (mousedSlot != null) {
					mousedOverSlot = mousedSlot;
				}
			}
		}

		// / Draw player inventory
		for (int i = pageMax * pageSize; i < this.inventorySlots.inventorySlots.size(); i++) {
			Slot mousedSlot = drawSlot(i, mouseX, mouseY);
			if (mousedSlot != null) {
				mousedOverSlot = mousedSlot;
			}
		}

		// Draw ledgers and gfx slots
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		ledgerManager.drawLedgers();
		slotManager.drawSlots();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this.drawGuiContainerForegroundLayer();

		InventoryPlayer playerInventory = this.mc.thePlayer.inventory;

		if (playerInventory.getItemStack() != null) {
			GL11.glTranslatef(0.0F, 0.0F, 32.0F);
			this.zLevel = 200.0F;
			itemRenderer.zLevel = 200.0F;
			itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, playerInventory.getItemStack(), mouseX - this.guiLeft - 8, mouseY
					- this.guiTop - 8);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, playerInventory.getItemStack(), mouseX - this.guiLeft - 8, mouseY
					- this.guiTop - 8);
			this.zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		if (playerInventory.getItemStack() == null && mousedOverSlot != null && mousedOverSlot.getHasStack()) {
			ItemStack slotStack = mousedOverSlot.getStack();
			drawTooltip(mouseX, mouseY, 300f, slotStack.getTooltip(Proxies.common.getClientInstance().thePlayer, false), slotStack.getRarity());
		}

		// Draw ledger and gfx slot tooltips
		ledgerManager.drawTooltips(mouseX, mouseY);
		slotManager.drawTooltips(mouseX, mouseY);

		GL11.glPopMatrix();
		super.drawScreen(mouseX, mouseY, par3);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	protected Slot drawSlot(int slotIndex, int mouseX, int mouseY) {

		Slot mousedOverSlot = null;
		Slot slot = (Slot) this.inventorySlots.inventorySlots.get(slotIndex);
		this.drawSlotInventory(slot);

		if (getIsMouseOverSlot(slot, mouseX, mouseY)) {
			mousedOverSlot = slot;
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int var9 = slot.xDisplayPosition;
			int var10 = slot.yDisplayPosition;
			this.drawGradientRect(var9, var10, var9 + 16, var10 + 16, -2130706433, -2130706433);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		return mousedOverSlot;
	}

	protected void drawSlotInventory(Slot slot) {

		int xPos = slot.xDisplayPosition;
		int yPos = slot.yDisplayPosition;
		ItemStack slotStack = slot.getStack();
		boolean backgroundDrawn = false;
		this.zLevel = 100.0F;
		itemRenderer.zLevel = 100.0F;

		if (slotStack == null) {
			int backgroundIndex = slot.getBackgroundIconIndex();

			if (backgroundIndex >= 0) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				if (slot instanceof ITextureSlot) {
					this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture(((ITextureSlot) slot).getTextureFile()));
				} else {
					this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/items.png"));
				}
				this.drawTexturedModalRect(xPos, yPos, backgroundIndex % 16 * 16, backgroundIndex / 16 * 16, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
				backgroundDrawn = true;
			}
		}

		if (!backgroundDrawn && slotStack != null) {
			itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, slotStack, xPos, yPos);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, slotStack, xPos, yPos);
		}

		itemRenderer.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	public void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
		super.drawGradientRect(par1, par2, par3, par4, par5, par6);
	}

	protected Slot getSlotAtPosition(int i, int j) {

		// Handle inventory slot click
		for (int k = pageCurrent * pageSize; k < (pageCurrent + 1) * pageSize; ++k) {
			Slot slot = (Slot) inventorySlots.inventorySlots.get(k);
			if (getIsMouseOverSlot(slot, i, j))
				return slot;
		}

		// Handle player inventory slot click
		for (int k = pageMax * pageSize; k < this.inventorySlots.inventorySlots.size(); k++) {
			Slot slot = (Slot) inventorySlots.inventorySlots.get(k);
			if (getIsMouseOverSlot(slot, i, j))
				return slot;
		}

		return null;
	}

	protected boolean getIsMouseOverSlot(Slot slot, int i, int j) {
		int k = guiLeft;
		int l = guiTop;
		i -= k;
		j -= l;
		return i >= slot.xDisplayPosition - 1 && i < slot.xDisplayPosition + 16 + 1 && j >= slot.yDisplayPosition - 1 && j < slot.yDisplayPosition + 16 + 1;
	}

	@Override
	protected void mouseClicked(int xPos, int yPos, int mouseButton) {

		super.mouseClicked(xPos, yPos, mouseButton);
		boolean var4 = mouseButton == this.mc.gameSettings.keyBindPickBlock.keyCode + 100;

		// / Handle slot clicks
		if (mouseButton == 0 || mouseButton == 1 || var4) {
			Slot slot = this.getSlotAtPosition(xPos, yPos);
			int xStart = this.guiLeft;
			int yStart = this.guiTop;
			boolean var7 = xPos < xStart || yPos < yStart || xPos >= xStart + this.xSize || yPos >= yStart + this.ySize;
			int slotNum = -1;

			if (slot != null) {
				slotNum = slot.slotNumber;
			}

			if (var7) {
				slotNum = -999;
			}

			if (slotNum != -1) {
				if (var4) {
					this.handleMouseClick(slot, slotNum, mouseButton, 3);
				} else {
					boolean var9 = slotNum != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
					this.handleMouseClick(slot, slotNum, mouseButton, var9 ? 1 : 0);
				}
			}
		}

		// / Handle ledger clicks
		ledgerManager.handleMouseClicked(xPos, yPos, mouseButton);
		slotManager.handleMouseClicked(xPos, yPos, mouseButton);
	}

	protected void handleMouseClick(Slot slot, int slotIndex, int mouseButton, int par4) {
		if (slot != null) {
			slotIndex = slot.slotNumber;
		}

		this.mc.playerController.windowClick(this.inventorySlots.windowId, slotIndex, mouseButton, par4, this.mc.thePlayer);
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	public void onGuiClosed() {
		if (this.mc.thePlayer != null) {
			this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
			this.mc.thePlayer.closeScreen();
		}
	}
}
