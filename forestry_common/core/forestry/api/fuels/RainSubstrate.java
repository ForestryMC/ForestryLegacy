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
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class RainSubstrate {
	/**
	 * Rain substrate capable of activating the rainmaker.
	 */
	public ItemStack item;
	/**
	 * Duration of the rain shower triggered by this substrate in Minecraft ticks.
	 */
	public int duration;
	/**
	 * Speed of activation sequence triggered.
	 */
	public float speed;

	public boolean reverse;

	public RainSubstrate(ItemStack item, int duration, float speed) {
		this(item, duration, speed, false);
	}

	public RainSubstrate(ItemStack item, float speed) {
		this(item, 0, speed, true);
	}

	public RainSubstrate(ItemStack item, int duration, float speed, boolean reverse) {
		this.item = item;
		this.duration = duration;
		this.speed = speed;
		this.reverse = reverse;
	}
}
