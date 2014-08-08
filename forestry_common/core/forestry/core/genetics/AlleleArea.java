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

import forestry.core.utils.Vect;

public class AlleleArea extends Allele {

	private Vect area;

	public AlleleArea(String uid, Vect value) {
		this(uid, value, false);
	}

	public AlleleArea(String uid, Vect value, boolean isDominant) {
		super(uid, isDominant);
		this.area = value;
	}

	public Vect getArea() {
		return area;
	}

}
