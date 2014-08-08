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
package forestry.core.worldgen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class WorldGenBase extends WorldGenerator {

	public static class Vector {
		public Vector(float f, float h, float g) {
			this.x = f;
			this.y = h;
			this.z = g;
		}

		float x;
		float y;
		float z;

		public static double distance(Vector a, Vector b) {
			return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
		}
	}

	protected World world;
	protected Random rand;
	
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		this.world = world;
		this.rand = random;
		return true;
	}
	
	protected abstract void addBlock(int x, int y, int z, BlockType type, boolean doReplace);

	protected final void generateCuboid(Vector start, Vector area, BlockType block, boolean doReplace) {
		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {
					addBlock(x, y, z, block, doReplace);
				}
			}
		}
	}

	/*
	 * Center is the bottom middle of the cylinder
	 */
	protected final void generateCylinder(Vector center, float radius, int height, BlockType block, boolean doReplace) {
		Vector start = new Vector(center.x - radius, center.y, center.z - radius);
		Vector area = new Vector(radius * 2 + 1, height, radius * 2 + 1);
		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {
					if (Vector.distance(new Vector(x, y, z), new Vector(center.x, y, center.z)) <= (radius) + 0.01) {
						addBlock(x, y, z, block, doReplace);
					}
				}
			}
		}
	}

	protected final void generateCircle(Vector center, float radius, int width, int height, BlockType block, boolean doReplace) {
		generateCircle(center, radius, width, height, block, 1.0f, doReplace);
	}
	
	protected final void generateCircle(Vector center, float radius, int width, int height, BlockType block, float chance, boolean doReplace) {
		Vector start = new Vector(center.x - radius, center.y, center.z - radius);
		Vector area = new Vector(radius * 2 + 1, height, radius * 2 + 1);
		
		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {
					
					if(rand.nextFloat() > chance)
						continue;
					
					double distance = Vector.distance(new Vector(x, y, z), new Vector(center.x, y, center.z));
					if ((radius - width - 0.01 < distance && distance <= (radius) + 0.01)) {
						addBlock(x, y, z, block, doReplace);
					}
				}
			}
		}
	}
	
	protected final void generateSphere(Vector center, int radius, BlockType block, boolean doReplace) {
		Vector start = new Vector(center.x - radius, center.y - radius, center.z - radius);
		Vector area = new Vector(radius * 2 + 1, radius * 2 + 1, radius * 2 + 1);
		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {
					if (Vector.distance(new Vector(x, y, z), new Vector(center.x, center.y, center.z)) <= (radius) + 0.01) {
						addBlock(x, y, z, block, doReplace);
					}
				}
			}
		}
	}

}
