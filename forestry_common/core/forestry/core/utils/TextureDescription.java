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

public class TextureDescription {

	public final int top;
	public final int bottom;
	public final int left;
	public final int right;
	public final int front;
	public final int back;

	public TextureDescription(int top, int bottom, int sides) {
		this.top = top;
		this.bottom = bottom;
		this.left = sides;
		this.right = sides;
		this.front = sides;
		this.back = sides;
	}

	public TextureDescription(int top, int bottom, int left, int right, int front, int back) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.front = front;
		this.back = back;
	}

}
