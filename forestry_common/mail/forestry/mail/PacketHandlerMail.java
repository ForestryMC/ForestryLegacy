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
package forestry.mail;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;
import forestry.api.core.IPacketHandler;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.ContainerTradeName;
import forestry.mail.network.PacketPOBoxInfo;
import forestry.mail.network.PacketTradeInfo;
import forestry.plugins.PluginForestryMail;

public class PacketHandlerMail implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager network, int packetID, DataInputStream data, Player player) {

		try {

			PacketUpdate packet;
			switch (packetID) {
			case PacketIds.TRADING_INFO:
				PacketTradeInfo packetT = new PacketTradeInfo();
				packetT.readData(data);
				onTradeInfo(packetT);
				break;
			case PacketIds.POBOX_INFO:
				PacketPOBoxInfo packetP = new PacketPOBoxInfo();
				packetP.readData(data);
				onPOBoxInfo(packetP);
				break;
			case PacketIds.LETTER_RECIPIENT:
				packet = new PacketUpdate();
				packet.readData(data);
				onLetterRecipient((EntityPlayer) player, packet);
				break;
			case PacketIds.LETTER_TEXT:
				packet = new PacketUpdate();
				packet.readData(data);
				onLetterText((EntityPlayer) player, packet);
				break;
			case PacketIds.TRADING_MONIKER_SET:
				packet = new PacketUpdate();
				packet.readData(data);
				onMonikerSet((EntityPlayer) player, packet);
				break;
			case PacketIds.POBOX_INFO_REQUEST:
				onPOBoxInfoRequest((EntityPlayer) player);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onTradeInfo(PacketTradeInfo packet) {
		Container container = Proxies.common.getClientInstance().thePlayer.openContainer;
		if (!(container instanceof ContainerLetter))
			return;

		((ContainerLetter) container).handleTradeInfoUpdate(packet);
	}

	private void onPOBoxInfo(PacketPOBoxInfo packet) {
		PluginForestryMail.proxy.setPOBoxInfo(Proxies.common.getRenderWorld(), Proxies.common.getClientInstance().thePlayer.username, packet.poboxInfo);
	}

	private void onMonikerSet(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerTradeName))
			return;

		((ContainerTradeName) player.openContainer).handleSetMoniker(packet);
	}

	private void onLetterText(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerLetter))
			return;

		((ContainerLetter) player.openContainer).handleSetText(packet);
	}

	private void onLetterRecipient(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerLetter))
			return;

		((ContainerLetter) player.openContainer).handleSetRecipient(player, packet);
	}

	private void onPOBoxInfoRequest(EntityPlayer player) {
		POBox pobox = PostOffice.getPOBox(player.worldObj, player.username);
		if (pobox == null)
			return;

		Proxies.net.sendToPlayer(new PacketPOBoxInfo(PacketIds.POBOX_INFO, pobox.getPOBoxInfo()), player);
	}

}
