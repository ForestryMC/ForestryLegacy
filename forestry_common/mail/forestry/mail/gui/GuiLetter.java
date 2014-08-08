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
package forestry.mail.gui;

import java.util.Locale;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import forestry.core.config.Defaults;
import forestry.core.gui.GfxSlot;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.mail.EnumAddressee;
import forestry.mail.MailAddress;
import forestry.mail.items.ItemLetter.LetterInventory;

public class GuiLetter extends GuiForestry {

	protected class AddresseeSlot extends GfxSlot {

		public AddresseeSlot(int xPos, int yPos) {
			super(slotManager, xPos, yPos);
			this.width = 26;
			this.height = 15;
		}

		@Override
		public void draw(int startX, int startY) {

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			int tex = mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/letter.png");
			mc.renderEngine.bindTexture(tex);
			drawTexturedModalRect(startX + xPos, startY + yPos, 194, container.getRecipientType().ordinal() * 15, 26, 15);

		}

		@Override
		protected String getTooltip(EntityPlayer player) {
			return StringUtil.localize("gui.addressee." + container.getRecipientType().toString().toLowerCase(Locale.ENGLISH));
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			container.advanceRecipientType();
		}

	}

	private boolean isProcessedLetter;

	private GuiTextField address;
	private GuiTextField text;

	boolean addressFocus;
	boolean textFocus;

	private ContainerLetter container;

	public GuiLetter(EntityPlayer player, LetterInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/letter.png", new ContainerLetter(player, inventory), inventory, 1, inventory.getSizeInventory());
		this.xSize = 194;
		this.ySize = 222;

		this.container = (ContainerLetter) inventorySlots;
		this.isProcessedLetter = container.getLetter().isProcessed();
		this.slotManager.add(new AddresseeSlot(16, 12));
	}

	@Override
	public void initGui() {
		super.initGui();

		address = new GuiTextField(this.fontRenderer, guiLeft + 46, guiTop + 13, 93, 13);
		if (container.getRecipient() != null) {
			address.setText(container.getRecipient().getIdentifier());
			this.setRecipient(container.getRecipient().getIdentifier(), container.getRecipientType());
		}

		text = new GuiTextField(this.fontRenderer, guiLeft + 17, guiTop + 31, 122, 52);
		text.setMaxStringLength(64);
		if (!container.getText().isEmpty()) {
			text.setText(container.getText());
		}

	}

	@Override
	protected void keyTyped(char eventCharacter, int eventKey) {

		// Set focus or enter text into address
		if (this.address.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN) {
				this.address.setFocused(false);
			} else {
				this.address.textboxKeyTyped(eventCharacter, eventKey);
			}
			return;
		}

		if (this.text.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN) {
				this.text.setFocused(false);
			} else {
				this.text.textboxKeyTyped(eventCharacter, eventKey);
			}
			return;
		}

		super.keyTyped(eventCharacter, eventKey);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int mouseButton) {
		super.mouseClicked(par1, par2, mouseButton);

		this.address.mouseClicked(par1, par2, mouseButton);
		this.text.mouseClicked(par1, par2, mouseButton);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {

		// Check for focus changes
		if (addressFocus != address.isFocused()) {
			setRecipient(this.address.getText(), container.getRecipientType());
		}
		addressFocus = address.isFocused();
		if (textFocus != text.isFocused()) {
			setText();
		}
		textFocus = text.isFocused();

		drawBackground();

		if (this.isProcessedLetter) {
			fontRenderer.drawString(address.getText(), guiLeft + 49, guiTop + 16, fontColor.get("gui.mail.lettertext"));
			fontRenderer.drawSplitString(text.getText(), guiLeft + 20, guiTop + 34, 119, fontColor.get("gui.mail.lettertext"));
		} else {
			address.drawTextBox();
			if (container.getRecipientType() != EnumAddressee.TRADER) {
				text.drawTextBox();
			} else {
				drawTradePreview(guiLeft + 18, guiTop + 32);
			}
		}
	}

	private void drawTradePreview(int x, int y) {

		String infoString = null;
		if (container.getTradeInfo() == null) {
			infoString = "gui.mail.notrader";
		} else if (container.getTradeInfo().tradegood == null) {
			infoString = "gui.mail.nothingtotrade";
		} else if (!container.getTradeInfo().state.isOk()) {
			infoString = "chat.mail." + container.getTradeInfo().state.getIdentifier();
		}

		if (infoString != null) {
			fontRenderer.drawSplitString(StringUtil.localize(infoString), x, y, 119, fontColor.get("gui.mail.lettertext"));
			return;
		}

		fontRenderer.drawString(StringUtil.localize("gui.mail.pleasesend"), x, y, fontColor.get("gui.mail.lettertext"));

		itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, container.getTradeInfo().tradegood, x, y + 10);
		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, container.getTradeInfo().tradegood, x, y + 10);

		fontRenderer.drawString(StringUtil.localize("gui.mail.foreveryattached"), x, y + 28, fontColor.get("gui.mail.lettertext"));
		for (int i = 0; i < container.getTradeInfo().required.length; i++) {
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, container.getTradeInfo().required[i], x + i * 18, y + 38);
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, container.getTradeInfo().required[i], x + i * 18, y + 38);
		}
	}

	@Override
	public void onGuiClosed() {
		setRecipient(this.address.getText(), container.getRecipientType());
		setText();
		super.onGuiClosed();
	}

	private void setRecipient(String identifier, EnumAddressee type) {
		if (this.isProcessedLetter)
			return;

		MailAddress recipient = new MailAddress(identifier, type);
		container.setRecipient(recipient);
		container.updateTradeInfo(this.mc.theWorld, recipient);
	}

	private void setText() {
		if (this.isProcessedLetter)
			return;

		container.setText(this.text.getText());
	}
}
