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
package forestry.core.gadgets;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IOwnable;
import forestry.core.network.ForestryPacket;
import forestry.core.network.INetworkedEntity;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.Vect;

public abstract class TileForestry extends TileEntity implements INetworkedEntity, IOwnable, IErrorSource {

	protected boolean isInited = false;

	public EntityPackage pack;

	protected int energyConsumed;
	protected int energyLast;
	protected int energyReceived;

	public Vect Coords() {
		return new Vect(xCoord, yCoord, zCoord);
	}

	// / MACHINERY
	public abstract Gadget getMachine();

	public void openGui(EntityPlayer player) {
	}

	// / UPDATING
	@Override
	public void updateEntity() {
		if (!isInited) {
			initialize();
			isInited = true;
		}

		if (!Proxies.common.isSimulating(worldObj))
			return;

		if (this instanceof IPowerReceptor) {
			IPowerReceptor receptor = (IPowerReceptor) this;
			if (receptor.getPowerProvider() != null) {
				IPowerProvider powerProvider = receptor.getPowerProvider();
				powerProvider.update(receptor);
			}
		}
	}

	public abstract void initialize();

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("Access")) {
			access = EnumAccess.values()[nbttagcompound.getInteger("Access")];
		} else {
			access = EnumAccess.SHARED;
		}
		if (nbttagcompound.hasKey("Owner")) {
			owner = nbttagcompound.getString("Owner");
		}

		if (nbttagcompound.hasKey("Orientation")) {
			orientation = ForgeDirection.values()[nbttagcompound.getInteger("Orientation")];
		} else {
			orientation = ForgeDirection.WEST;
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Access", access.ordinal());
		if (owner != null) {
			nbttagcompound.setString("Owner", owner);
		}
		if (orientation != null) {
			nbttagcompound.setInteger("Orientation", orientation.ordinal());
		}
	}

	// / SMP
	@Override
	public void sendNetworkUpdate() {
		PacketTileUpdate packet = new PacketTileUpdate(this);
		Proxies.net.sendNetworkPacket(packet, xCoord, yCoord, zCoord);
	}

	@Override
	public Packet getDescriptionPacket() {
		PacketTileUpdate packet = new PacketTileUpdate(this);
		return packet.getPacket();
	}

	public abstract PacketPayload getPacketPayload();
	public abstract void fromPacketPayload(PacketPayload payload);

	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		PacketTileUpdate packet = (PacketTileUpdate) packetRaw;
		orientation = packet.getOrientation();
		errorState = packet.getErrorState();
		owner = packet.getOwner();
		access = packet.getAccess();
		fromPacketPayload(packet.payload);
	}

	public LinkedList<ITrigger> getCustomTriggers() {
		return null;
	}

	public void onRemoval() {
	}

	// / REDSTONE INFO
	/**
	 * @return true if tile is activated by redstone current.
	 */
	public boolean isActivated() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	// / ORIENTATION
	private ForgeDirection orientation = ForgeDirection.WEST;

	public ForgeDirection getOrientation() {
		return this.orientation;
	}

	public void setOrientation(ForgeDirection orientation) {
		if (this.orientation == orientation)
			return;
		this.orientation = orientation;
		this.sendNetworkUpdate();
	}

	// / ERROR HANDLING
	public EnumErrorCode errorState = EnumErrorCode.OK;

	public void setErrorState(EnumErrorCode state) {
		if (this.errorState == state)
			return;
		this.errorState = state;
		this.sendNetworkUpdate();
	}

	@Override
	public boolean throwsErrors() {
		return true;
	}

	@Override
	public EnumErrorCode getErrorState() {
		return errorState;
	}

	// / OWNERSHIP
	public String owner = null;
	private EnumAccess access = EnumAccess.SHARED;

	@Override
	public boolean allowsRemoval(EntityPlayer player) {
		if (!isOwnable())
			return true;
		if (!isOwned())
			return true;
		if (isOwner(player))
			return true;
		if (Proxies.common.isOp(player))
			return true;

		return getAccess() == EnumAccess.SHARED;
	}

	@Override
	public boolean allowsInteraction(EntityPlayer player) {
		if (Config.disablePermissions)
			return true;
		if (!isOwnable())
			return true;
		if (!isOwned())
			return true;
		if (isOwner(player))
			return true;
		if (Proxies.common.isOp(player))
			return true;

		return getAccess() == EnumAccess.SHARED || getAccess() == EnumAccess.VIEWABLE;
	}

	@Override
	public EnumAccess getAccess() {
		return access;
	}

	@Override
	public boolean isOwnable() {
		return false;
	}

	@Override
	public boolean isOwned() {
		return owner != null && !owner.isEmpty();
	}

	@Override
	public String getOwnerName() {
		return owner;
	}

	public EntityPlayer getOwnerEntity() {
		if (owner != null)
			return worldObj.getPlayerEntityByName(owner);
		else
			return null;
	}

	public void setOwner(EntityPlayer player) {
		this.owner = player.username;
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		if (owner != null)
			return owner.equals(player.username);
		else
			return false;
	}

	@Override
	public boolean switchAccessRule(EntityPlayer player) {
		if (owner != null && !owner.isEmpty() && !owner.equals(player.username))
			return false;

		if (access.ordinal() < EnumAccess.values().length - 1) {
			access = EnumAccess.values()[access.ordinal() + 1];
		} else {
			access = EnumAccess.values()[0];
		}

		return true;
	}

	/* NAME */
	public abstract String getInvName();

	/* ACCESS */
	public abstract boolean isUseableByPlayer(EntityPlayer player);
}
