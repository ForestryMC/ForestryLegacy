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
package forestry.core.triggers;

import forestry.core.config.Defaults;

public class ForestryTrigger {

	public static Trigger lowFuel25;
	public static Trigger lowFuel10;
	public static Trigger lowResource25;
	public static Trigger lowResource10;

	public static Trigger lowSoil25;
	public static Trigger lowSoil10;
	public static Trigger lowGermlings25;
	public static Trigger lowGermlings10;

	public static Trigger missingQueen;
	public static Trigger missingDrone;

	public static Trigger hasWork;

	public static void initialize() {
		lowFuel25 = new TriggerLowFuel(Defaults.ID_TRIGGER_LOWFUEL_25, 0.25f);
		lowFuel10 = new TriggerLowFuel(Defaults.ID_TRIGGER_LOWFUEL_10, 0.1f);
		lowResource25 = new TriggerLowResource(Defaults.ID_TRIGGER_LOWRESOURCE_25, 0.25f);
		lowResource10 = new TriggerLowResource(Defaults.ID_TRIGGER_LOWRESOURCE_10, 0.1f);

		lowSoil25 = new TriggerLowSoil(Defaults.ID_TRIGGER_LOWSOIL_25, 0.25f);
		lowSoil10 = new TriggerLowSoil(Defaults.ID_TRIGGER_LOWSOIL_10, 0.1f);
		lowGermlings25 = new TriggerLowGermlings(Defaults.ID_TRIGGER_LOWGERMLINGS_25, 0.25f);
		lowGermlings10 = new TriggerLowGermlings(Defaults.ID_TRIGGER_LOWGERMLINGS_10, 0.1f);

		missingQueen = new TriggerMissingQueen(Defaults.ID_TRIGGER_NOQUEEN);
		missingDrone = new TriggerMissingDrone(Defaults.ID_TRIGGER_NODRONE);

		hasWork = new TriggerHasWork(Defaults.ID_TRIGGER_HASWORK);

	}
}
