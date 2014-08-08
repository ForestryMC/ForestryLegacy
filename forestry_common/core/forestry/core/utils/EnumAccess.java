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
package forestry.core.utils;

public enum EnumAccess {
	SHARED("gui.rule.shared", 2), VIEWABLE("gui.rule.restricted", 3), PRIVATE("gui.rule.private", 4);

	private final String name;
	private final int iconIndex;

	private EnumAccess(String name, int iconIndex) {
		this.name = name;
		this.iconIndex = iconIndex;
	}

	public String getName() {
		return this.name;
	}

	public int getIconIndex() {
		return this.iconIndex;
	}
}
