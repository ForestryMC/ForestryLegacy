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
package forestry.core.genetics;

public enum EnumMutateChance {
	NONE, LOWEST, LOW, NORMAL, HIGH, HIGHER, HIGHEST;

	public static EnumMutateChance rateChance(int percent) {

		if (percent >= 20)
			return HIGHEST;
		else if (percent >= 15)
			return HIGHER;
		else if (percent >= 12)
			return HIGH;
		else if (percent >= 10)
			return NORMAL;
		else if (percent >= 5)
			return LOW;
		else
			return LOWEST;

	}
}
