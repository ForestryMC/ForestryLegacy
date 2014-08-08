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

public enum EnumPostage {
	P_0(0), P_1(1), P_2(2), P_5(5), P_10(10);

	private final int value;

	private EnumPostage(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
