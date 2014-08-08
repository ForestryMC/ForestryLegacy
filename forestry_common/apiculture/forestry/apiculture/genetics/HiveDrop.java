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
package forestry.apiculture.genetics;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.genetics.IAllele;

public class HiveDrop implements IHiveDrop {

	private IAllele[] template;
	private ArrayList<ItemStack> additional = new ArrayList<ItemStack>();
	private int chance;

	public HiveDrop(IAllele[] template, ItemStack[] bonus, int chance) {
		this.template = template;
		this.chance = chance;

		for (ItemStack stack : bonus) {
			this.additional.add(stack);
		}
	}

	@Override
	public ItemStack getPrincess(World world, int x, int y, int z, int fortune) {
		return BeeManager.beeInterface.getBeeStack(BeeManager.beeInterface.getBee(world, BeeManager.beeInterface.templateAsGenome(template)),
				EnumBeeType.PRINCESS);
	}

	@Override
	public ArrayList<ItemStack> getDrones(World world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(BeeManager.beeInterface.getBeeStack(BeeManager.beeInterface.getBee(world, BeeManager.beeInterface.templateAsGenome(template)),
				EnumBeeType.DRONE));
		return ret;
	}

	@Override
	public ArrayList<ItemStack> getAdditional(World world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for (ItemStack stack : additional) {
			ret.add(stack.copy());
		}

		return ret;
	}

	@Override
	public int getChance(World world, int x, int y, int z) {
		return chance;
	}

}
