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

public interface IFruitFamily {
	
	/**
	 * @return Unique String identifier.
	 */
	String getUID();

	/**
	 * @return Localized family name for user display.
	 */
	String getName();

	/**
	 * A scientific-y name for this fruit family
	 * 
	 * @return flavour text (may be null)
	 */
	String getScientific();

	/**
	 * @return Localized description of this fruit family. (May be null.)
	 */
	String getDescription();

}
