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

/**
 * Should be extended for different types of alleles. ISpeciesAllele, IBiomeAllele, etc.
 */
public interface IAllele {
	
	/**
	 * @return A unique string identifier for this allele.
	 */
	String getUID();

	/**
	 * @return true if the allele is dominant, false otherwise.
	 */
	boolean isDominant();
}
