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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import forestry.core.proxy.Proxies;

public class ForestryPacket {

	protected int id;
	protected String channel = "FOR";
	protected boolean isChunkDataPacket = false;

	public ForestryPacket() {
	}

	public ForestryPacket(int id) {
		this.id = id;
	}

	public Packet getPacket() {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeByte(getID());
			writeData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;
		packet.isChunkDataPacket = this.isChunkDataPacket;
		return packet;
	}

	public int getID() {
		return id;
	}

	protected ItemStack readItemStack(DataInputStream data) throws IOException {

		ItemStack itemstack = null;
		short itemID = data.readShort();

		if (itemID >= 0) {

			byte stackSize = data.readByte();
			short meta = data.readShort();
			itemstack = new ItemStack(itemID, stackSize, meta);

			if (Item.itemsList[itemID].isDamageable() || Proxies.common.needsTagCompoundSynched(Item.itemsList[itemID])) {
				itemstack.stackTagCompound = this.readNBTTagCompound(data);
			}
		}

		return itemstack;
	}

	protected void writeItemStack(ItemStack itemstack, DataOutputStream data) throws IOException {

		if (itemstack == null) {
			data.writeShort(-1);
		} else {
			data.writeShort(itemstack.itemID);
			data.writeByte(itemstack.stackSize);
			data.writeShort(itemstack.getItemDamage());

			if (itemstack.getItem().isDamageable() || Proxies.common.needsTagCompoundSynched(itemstack.getItem())) {
				this.writeNBTTagCompound(itemstack.stackTagCompound, data);
			}
		}
	}

	protected NBTTagCompound readNBTTagCompound(DataInputStream data) throws IOException {

		short length = data.readShort();

		if (length < 0)
			return null;
		else {
			byte[] compressed = new byte[length];
			data.readFully(compressed);
			return CompressedStreamTools.decompress(compressed);
		}

	}

	protected void writeNBTTagCompound(NBTTagCompound nbttagcompound, DataOutputStream data) throws IOException {

		if (nbttagcompound == null) {
			data.writeShort(-1);
		} else {
			byte[] compressed = CompressedStreamTools.compress(nbttagcompound);
			data.writeShort((short) compressed.length);
			data.write(compressed);
		}

	}

	public void writeData(DataOutputStream data) throws IOException {
	}

	public void readData(DataInputStream data) throws IOException {
	}

}
