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
package forestry.api.genetics;

import forestry.api.core.INBTTagable;

public interface IEffectData extends INBTTagable {
	void setInteger(int index, int val);

	void setFloat(int index, float val);

	void setBoolean(int index, boolean val);

	int getInteger(int index);

	float getFloat(int index);

	boolean getBoolean(int index);
}
