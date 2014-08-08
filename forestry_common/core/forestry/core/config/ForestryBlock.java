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
package forestry.core.config;

import net.minecraft.block.Block;

/**
 * Allows direct access to Forestry's blocks. Will be populated during BaseMod.load().
 * 
 * All of this stuff is metadata sensitive which is not reflected here!
 * 
 * Make sure to only reference it in modsLoaded() or later.
 * 
 * @author SirSengir
 * 
 */
public class ForestryBlock {

	/**
	 * 0 - Humus 1 - Bog Earth
	 */
	public static Block soil;
	/**
	 * 0 - Apatite Ore 1 - Copper Ore 2 - Tin Ore
	 */
	public static Block resources;
	/**
	 * 0 - Legacy 1 - Forest Hive 2 - Meadows Hive
	 */
	public static Block beehives;

	public static Block building;
	/**
	 * All planter type machines
	 */
	public static Block planter;
	/**
	 * 0 - Oak saplings 1 - Rubber Tree Saplings 2 - Mushrooms
	 */
	public static Block firsapling;

	public static Block sapling;
	public static Block mushroom;
	public static Block candle;
	public static Block stump;
	public static Block glass;

	public static Block planks;
	public static Block slabs1;
	public static Block slabs2;
	public static Block log1;
	public static Block log2;
	public static Block log3;
	public static Block log4;
	public static Block fences;
	public static Block stairs;

	public static Block saplingGE;
	public static Block leaves;

	public static Block alveary;
	public static Block swarmer;
	public static Block heater;
	public static Block fan;
	public static Block farm;
	
	public static Block core;
	public static Block apiculture;
	public static Block mail;
	
	/**
	 * All harvester type machines
	 */
	public static Block harvester;
	/**
	 * 0 - Biogas Engine 1 - Peat-fired Engine 2 - Electrical Engine
	 */
	public static Block engine;
	/**
	 * Any machine not covered by other machine blocks
	 */
	public static Block machine;
	/**
	 * 0 - Forester 1 - Rainmaker 2 - Automatic Treetap
	 */
	public static Block mill;

}
