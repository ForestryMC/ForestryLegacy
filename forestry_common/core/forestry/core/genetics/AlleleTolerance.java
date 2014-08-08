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

import forestry.api.genetics.EnumTolerance;

public class AlleleTolerance extends Allele {

	EnumTolerance value;

	public AlleleTolerance(String uid, EnumTolerance value) {
		this(uid, value, false);
	}

	public AlleleTolerance(String uid, EnumTolerance value, boolean isDominant) {
		super(uid, isDominant);
		this.value = value;
	}

	public EnumTolerance getValue() {
		return value;
	}

}
