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

public interface IAlleleEffect extends IAllele {
	/**
	 * @return true if this effect can combine with the effect on other allele (i.e. run before or after). combination can only occur if both effects are
	 *         combinable.
	 */
	boolean isCombinable();

	/**
	 * Returns the passed data storage if it is valid for this effect or a new one if the passed storage object was invalid for this effect.
	 * 
	 * @param storedData
	 * @return
	 */
	IEffectData validateStorage(IEffectData storedData);

	/**
	 * @return Short, human-readable identifier used in the beealyzer.
	 */
	String getIdentifier();

}
