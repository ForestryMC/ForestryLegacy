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
package forestry.api.core;

import java.util.ArrayList;

public enum EnumHumidity {
	ARID("Arid", 2), NORMAL("Normal", 1), DAMP("Damp", 4);

	/**
	 * Populated by Forestry with vanilla biomes. Add additional arid biomes here. (ex. desert)
	 */
	public static ArrayList<Integer> aridBiomeIds = new ArrayList<Integer>();
	/**
	 * Populated by Forestry with vanilla biomes. Add additional damp biomes here. (ex. jungle)
	 */
	public static ArrayList<Integer> dampBiomeIds = new ArrayList<Integer>();
	/**
	 * Populated by Forestry with vanilla biomes. Add additional normal biomes here.
	 */
	public static ArrayList<Integer> normalBiomeIds = new ArrayList<Integer>();

	public final String name;
	public final int iconIndex;

	private EnumHumidity(String name, int iconIndex) {
		this.name = name;
		this.iconIndex = iconIndex;
	}

	public String getName() {
		return this.name;
	}

	public int getIconIndex() {
		return this.iconIndex;
	}

	public static ArrayList<Integer> getBiomeIds(EnumHumidity humidity) {
		switch (humidity) {
		case ARID:
			return aridBiomeIds;
		case DAMP:
			return dampBiomeIds;
		case NORMAL:
		default:
			return normalBiomeIds;
		}
	}
}
