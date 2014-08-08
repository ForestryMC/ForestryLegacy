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
package forestry.api.recipes;

/**
 * Contains all available recipe managers for Forestry machines and items.
 * 
 * @author SirSengir
 */
public class RecipeManagers {

	/**
	 * Allows you to add recipes to the bottler. See {@link IBottlerManager} for details.
	 */
	public static IBottlerManager bottlerManager;
	/**
	 * Allows you to add recipes to the carpenter. See {@link ICarpenterManager} for details.
	 */
	public static ICarpenterManager carpenterManager;
	/**
	 * Allows you to add recipes to the centrifuge. See {@link ICentrifugeManager} for details.
	 */
	public static ICentrifugeManager centrifugeManager;
	/**
	 * Allows you to add recipes to the fermenter. See {@link IFermenterManager} for details.
	 */
	public static IFermenterManager fermenterManager;
	/**
	 * Allows you to add recipes to the moistener. See {@link IMoistenerManager} for details.
	 */
	public static IMoistenerManager moistenerManager;
	/**
	 * Allows you to add recipes to the squeezer. See {@link ISqueezerManager} for details.
	 */
	public static ISqueezerManager squeezerManager;
	/**
	 * Allows you to add recipes to the still. See {@link IStillManager} for details.
	 */
	public static IStillManager stillManager;

	public static IFabricatorManager fabricatorManager;
}
