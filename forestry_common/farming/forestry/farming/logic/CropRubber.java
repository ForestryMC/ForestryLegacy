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
package forestry.farming.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;
import forestry.plugins.PluginIC2;

public class CropRubber extends CropBlock {

	public CropRubber(World world, int blockId, int meta, Vect position) {
		super(world, blockId, meta, position);
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		Collection<ItemStack> harvested = new ArrayList<ItemStack>();
		harvested.add(PluginIC2.resin.copy());
		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, blockId, 0);
		world.setBlockAndMetadataWithNotify(pos.x, pos.y, pos.z, blockId, meta + 6);
		return harvested;
	}

}
