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
package forestry.core.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.IGuiSelectable;
import forestry.core.interfaces.ISocketable;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginManager;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player player) {

		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		PacketUpdate packetU;

		try {

			int packetId = data.readByte();

			switch (packetId) {

			case PacketIds.TILE_FORESTRY_UPDATE:
				PacketTileUpdate packetT = new PacketTileUpdate();
				packetT.readData(data);
				onTileUpdate(packetT);
				break;
			case PacketIds.TILE_UPDATE:
				PacketUpdate packetUpdate = new PacketUpdate();
				packetUpdate.readData(data);
				onTileUpdate(packetUpdate);
				break;
			case PacketIds.TILE_NBT:
				PacketTileNBT packetN = new PacketTileNBT();
				packetN.readData(data);
				onTileUpdate(packetN);
				break;
			case PacketIds.SOCKET_UPDATE:
				PacketSocketUpdate packetS = new PacketSocketUpdate();
				packetS.readData(data);
				onSocketUpdate(packetS);
				break;
			case PacketIds.IINVENTORY_STACK:
				PacketInventoryStack packetQ = new PacketInventoryStack();
				packetQ.readData(data);
				onInventoryStack(packetQ);
				break;
			case PacketIds.FX_SIGNAL:
				PacketFXSignal packetF = new PacketFXSignal();
				packetF.readData(data);
				packetF.executeFX();
				break;

			case PacketIds.PIPETTE_CLICK:
				packetU = new PacketUpdate();
				packetU.readData(data);
				onPipetteClick(packetU, (EntityPlayer) player);
				break;
			case PacketIds.SOLDERING_IRON_CLICK:
				packetU = new PacketUpdate();
				packetU.readData(data);
				onSolderingIronClick(packetU, (EntityPlayer) player);
				break;
			case PacketIds.CHIPSET_CLICK:
				packetU = new PacketUpdate();
				packetU.readData(data);
				onChipsetClick(packetU, (EntityPlayer) player);
				break;
			case PacketIds.ACCESS_SWITCH:
				PacketCoordinates packetC = new PacketCoordinates();
				packetC.readData(data);
				onAccessSwitch(packetC, (EntityPlayer) player);
				break;
			case PacketIds.GUI_SELECTION:
				PacketUpdate packetI = new PacketUpdate();
				packetI.readData(data);
				onGuiSelection((EntityPlayer) player, packetI);
				break;
			case PacketIds.GUI_SELECTION_CHANGE:
				PacketUpdate packetZ = new PacketUpdate();
				packetZ.readData(data);
				onGuiChange((EntityPlayer) player, packetZ);
				break;
			default:
				for (forestry.api.core.IPacketHandler handler : PluginManager.packetHandlers) {
					handler.onPacketData(network, packetId, data, player);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void onGuiChange(EntityPlayer player, PacketUpdate packet) {

		if (!(player.openContainer instanceof IGuiSelectable))
			return;

		((IGuiSelectable) player.openContainer).handleSelectionChange(player, packet);
	}

	private void onGuiSelection(EntityPlayer player, PacketUpdate packet) {

		Container container = player.openContainer;
		if (!(container instanceof IGuiSelectable))
			return;

		((IGuiSelectable) container).setSelection(packet);

	}

	private void onSocketUpdate(PacketSocketUpdate packet) {
		TileEntity tile = Proxies.common.getRenderWorld().getBlockTileEntity(packet.posX, packet.posY, packet.posZ);
		if (!(tile instanceof ISocketable))
			return;

		ISocketable socketable = (ISocketable) tile;
		for (int i = 0; i < packet.itemstacks.length; i++) {
			socketable.setSocket(i, packet.itemstacks[i]);
		}
	}

	private void onTileUpdate(ForestryPacket packet) {

		TileEntity tile = ((ILocatedPacket) packet).getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof INetworkedEntity) {
			((INetworkedEntity) tile).fromPacket(packet);
		}

	}

	private void onInventoryStack(PacketInventoryStack packet) {

		TileEntity tile = Proxies.common.getRenderWorld().getBlockTileEntity(packet.posX, packet.posY, packet.posZ);
		if (tile == null)
			return;
		if (!(tile instanceof IInventory))
			return;

		((IInventory) tile).setInventorySlotContents(packet.slotIndex, packet.itemstack);
	}

	private void onChipsetClick(PacketUpdate packet, EntityPlayer player) {
		if (!(player.openContainer instanceof ContainerSocketed))
			return;
		ItemStack itemstack = player.inventory.getItemStack();
		if (!(itemstack.getItem() instanceof ItemCircuitBoard))
			return;

		((ContainerSocketed) player.openContainer).handleChipsetClick(packet.payload.intPayload[0], player, itemstack);

	}

	private void onSolderingIronClick(PacketUpdate packet, EntityPlayer player) {
		if (!(player.openContainer instanceof ContainerSocketed))
			return;
		ItemStack itemstack = player.inventory.getItemStack();

		((ContainerSocketed) player.openContainer).handleSolderingIronClick(packet.payload.intPayload[0], player, itemstack);
	}

	private void onAccessSwitch(PacketCoordinates packet, EntityPlayer playerEntity) {

		TileForestry tile = (TileForestry) playerEntity.worldObj.getBlockTileEntity(packet.posX, packet.posY, packet.posZ);
		if (tile == null)
			return;

		tile.switchAccessRule(playerEntity);
	}

	private void onPipetteClick(PacketUpdate packet, EntityPlayer player) {

		if (!(player.openContainer instanceof ContainerLiquidTanks))
			return;

		((ContainerLiquidTanks) player.openContainer).handlePipetteClick(packet.payload.intPayload[0], player);
	}

}
