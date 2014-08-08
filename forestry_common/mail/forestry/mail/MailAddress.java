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

import net.minecraft.nbt.NBTTagCompound;
import forestry.api.core.INBTTagable;

public class MailAddress implements INBTTagable {
	private EnumAddressee type;
	private String identifier;

	private MailAddress() {
	}

	public MailAddress(String identifier) {
		this(identifier, EnumAddressee.PLAYER);
	}

	public MailAddress(String identifier, EnumAddressee type) {
		this.identifier = identifier;
		this.type = type;
	}

	public EnumAddressee getType() {
		return this.type;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public boolean isPlayer() {
		return this.type == EnumAddressee.PLAYER;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		this.type = EnumAddressee.values()[nbttagcompound.getShort("TYP")];
		this.identifier = nbttagcompound.getString("ID");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("TYP", (short) this.type.ordinal());
		nbttagcompound.setString("ID", this.identifier);
	}

	public static MailAddress loadFromNBT(NBTTagCompound nbttagcompound) {
		MailAddress address = new MailAddress();
		address.readFromNBT(nbttagcompound);
		return address;
	}
}
