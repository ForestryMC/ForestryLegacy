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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.SlotClosed;
import forestry.core.gui.SlotCustom;
import forestry.core.gui.SlotLocked;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.EnumAddressee;
import forestry.mail.ILetter;
import forestry.mail.Letter;
import forestry.mail.MailAddress;
import forestry.mail.PostOffice;
import forestry.mail.TradeStation;
import forestry.mail.TradeStationInfo;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemLetter.LetterInventory;
import forestry.mail.items.ItemStamps;
import forestry.mail.network.PacketTradeInfo;

public class ContainerLetter extends ContainerItemInventory {

	private LetterInventory letterInventory;
	private EnumAddressee recipientType = EnumAddressee.PLAYER;
	private TradeStationInfo tradeInfo = null;

	public ContainerLetter(EntityPlayer player, LetterInventory inventory) {
		super(inventory);

		letterInventory = inventory;

		// Rip open delivered mails
		if (Proxies.common.isSimulating(player.worldObj) && letterInventory.getLetter().isProcessed() && inventory.parent != null
				&& ItemLetter.getState(inventory.parent.getItemDamage()) < 2) {
			inventory.parent.setItemDamage(ItemLetter.encodeMeta(2, ItemLetter.getSize(inventory.parent.getItemDamage())));
		}

		// Init slots
		Object[] validStamps = new Object[] { ItemStamps.class };
		if (letterInventory.getLetter().isProcessed()) {
			validStamps = new Object[] {};
		}

		// Stamps
		for (int i = 0; i < 4; i++) {
			addSlot(new SlotCustom(inventory, validStamps, Letter.SLOT_POSTAGE_1 + i, 150, 14 + i * 19).setStackLimit(1));
		}

		// Attachments
		if (!letterInventory.getLetter().isProcessed()) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 9; j++) {
					addSlot(new SlotCustom(inventory, new Object[] { ItemLetter.class }, Letter.SLOT_ATTACHMENT_1 + j + i * 9, 17 + j * 18, 98 + i * 18, true));
				}
			}
		} else {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 9; j++) {
					addSlot(new SlotClosed(inventory, Letter.SLOT_ATTACHMENT_1 + j + i * 9, 17 + j * 18, 98 + i * 18));
				}
			}
		}

		// Player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSecuredSlot(player.inventory, j + i * 9 + 9, 17 + j * 18, 145 + i * 18);
			}
		}
		// Player hotbar
		for (int i = 0; i < 9; i++) {
			addSecuredSlot(player.inventory, i, 17 + i * 18, 203);
		}

		// Set recipient type
		if (letterInventory.getLetter() != null)
			if (letterInventory.getLetter().getRecipients() != null)
				if (letterInventory.getLetter().getRecipients().length > 0) {
					this.recipientType = letterInventory.getLetter().getRecipients()[0].getType();
				}
	}

	private void addSecuredSlot(IInventory inventory, int slot, int x, int y) {
		if(inventory.getStackInSlot(slot) != null
				&& inventory.getStackInSlot(slot).getItem() instanceof ItemLetter)
			addSlot(new SlotLocked(inventory, slot, x, y));
		else
			addSlot(new Slot(inventory, slot, x, y));
	}
	
	@Override
	public void onCraftGuiClosed(EntityPlayer entityplayer) {

		if (Proxies.common.isSimulating(entityplayer.worldObj)) {
			ILetter letter = letterInventory.getLetter();
			if (!letter.isProcessed()) {
				letter.setSender(new MailAddress(entityplayer.username));
			}
		}

		super.onCraftGuiClosed(entityplayer);
	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		return true;
	}

	public ILetter getLetter() {
		return letterInventory.getLetter();
	}

	public void setRecipientType(EnumAddressee type) {
		this.recipientType = type;
	}

	public EnumAddressee getRecipientType() {
		return this.recipientType;
	}

	public void advanceRecipientType() {
		if (getRecipientType().ordinal() < EnumAddressee.values().length - 1) {
			setRecipientType(EnumAddressee.values()[getRecipientType().ordinal() + 1]);
		} else {
			setRecipientType(EnumAddressee.values()[0]);
		}
	}

	public void setRecipient(MailAddress address) {
		getLetter().setRecipient(address);

		// / Send to server
		PacketPayload payload = new PacketPayload(1, 0, 1);
		payload.intPayload[0] = this.recipientType.ordinal();
		payload.stringPayload[0] = this.getRecipient().getIdentifier();

		PacketUpdate packet = new PacketUpdate(PacketIds.LETTER_RECIPIENT, payload);
		Proxies.net.sendToServer(packet);
	}

	public void handleSetRecipient(EntityPlayer player, PacketUpdate packet) {
		MailAddress recipient = new MailAddress(packet.payload.stringPayload[0], EnumAddressee.values()[packet.payload.intPayload[0]]);
		getLetter().setRecipient(recipient);
		if (recipient.getType() == EnumAddressee.TRADER) {
			// Update the trading info
			updateTradeInfo(player.worldObj, recipient);
			// Update trade info on client
			Proxies.net.sendToPlayer(new PacketTradeInfo(PacketIds.TRADING_INFO, tradeInfo), player);
		}
	}

	public MailAddress getRecipient() {
		if (getLetter().getRecipients().length > 0)
			return getLetter().getRecipients()[0];
		else
			return null;
	}

	public String getText() {
		return getLetter().getText();
	}

	public void setText(String text) {
		getLetter().setText(text);

		// / Send to server
		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = text;

		PacketUpdate packet = new PacketUpdate(PacketIds.LETTER_TEXT, payload);
		Proxies.net.sendToServer(packet);

	}

	public void handleSetText(PacketUpdate packet) {
		getLetter().setText(packet.payload.stringPayload[0]);
	}

	// / Managing Trade info
	public void updateTradeInfo(World world, MailAddress address) {
		// Updating is done by the server.
		if (!Proxies.common.isSimulating(world))
			return;

		if (address.getType() != EnumAddressee.TRADER)
			return;

		TradeStation station = PostOffice.getTradeStation(world, address.getIdentifier());
		if (station == null)
			return;

		setTradeInfo(station.getTradeInfo());
	}

	public void handleTradeInfoUpdate(PacketTradeInfo packet) {
		this.setTradeInfo(packet.tradeInfo);
	}

	public TradeStationInfo getTradeInfo() {
		return this.tradeInfo;
	}

	private void setTradeInfo(TradeStationInfo info) {
		this.tradeInfo = info;
	}
}
