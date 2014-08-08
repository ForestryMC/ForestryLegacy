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
package forestry.apiculture;

import net.minecraft.entity.EntityFlying;
import net.minecraft.world.World;
import forestry.api.apiculture.IBeeGenome;
import forestry.core.config.Defaults;
import forestry.plugins.PluginForestryApiculture;

public class EntityBee extends EntityFlying {

	// private IBeeGenome genome;
	private int color;

	public EntityBee(World world, IBeeGenome genome) {
		super(world);
		// this.genome = genome;
		this.color = genome.getPrimaryAsBee().getPrimaryColor();

		this.setEntityHealth(getMaxHealth());
		this.setSize(1.0f, 1.0f);
		this.moveSpeed = 0.23F;
		// this.tasks.addTask(6, new EntityAIWander(this, this.moveSpeed));
		// this.tasks.addTask(7, new EntityAILookIdle(this));
		// this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	public int getColor() {
		return color;
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	// @Override
	// protected boolean isAIEnabled() {
	// return true;
	// }

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		PluginForestryApiculture.proxy.addBeeSwarmFX(Defaults.TEXTURE_PARTICLES_BEE, this.worldObj, this.posX, this.posY, this.posZ, this.color);
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	@Override
	protected String getLivingSound() {
		return "mob.zombie";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected String getHurtSound() {
		return "mob.zombiehurt";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected String getDeathSound() {
		return "mob.zombiedeath";
	}
}
