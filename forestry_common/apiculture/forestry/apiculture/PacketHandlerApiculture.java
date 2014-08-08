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
package forestry.apiculture;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;
import forestry.api.apiculture.BeeManager;
import forestry.api.core.IPacketHandler;
import forestry.api.genetics.IApiaristTracker;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketNBT;
import forestry.core.proxy.Proxies;

public class PacketHandlerApiculture implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager network, int packetID, DataInputStream data, Player player) {

		try {

			switch (packetID) {
			case PacketIds.HABITAT_BIOME_POINTER:
				PacketCoordinates packetC = new PacketCoordinates();
				packetC.readData(data);
				Proxies.common.setBiomeFinderCoordinates(null, packetC.getCoordinates());
				break;
			case PacketIds.GENOME_TRACKER_UPDATE:
				PacketNBT packetN = new PacketNBT();
				packetN.readData(data);
				onGenomeTrackerUpdate((EntityPlayer) player, packetN);
				break;
			case PacketIds.IMPRINT_SELECTION_GET:
				onImprintSelectionGet((EntityPlayer) player);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onGenomeTrackerUpdate(EntityPlayer player, PacketNBT packet) {
		IApiaristTracker tracker = BeeManager.breedingManager.getApiaristTracker(Proxies.common.getRenderWorld(), player.username);
		tracker.decodeFromNBT(packet.getTagCompound());
	}

	private void onImprintSelectionGet(EntityPlayer playerEntity) {

		if (!(playerEntity.openContainer instanceof ContainerImprinter))
			return;

		((ContainerImprinter) playerEntity.openContainer).sendSelection(playerEntity);

	}

}
