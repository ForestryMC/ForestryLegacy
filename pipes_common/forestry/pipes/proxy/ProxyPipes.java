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
package forestry.pipes.proxy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import buildcraft.BuildCraftTransport;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.pipes.PipeItemsPropolis;
import forestry.plugins.PluginPropolisPipe;

public class ProxyPipes {

	public void registerCustomItemRenderer(int itemID, IItemRenderer basemod) {
	}

	public void initPropolisPipe() {
		PluginPropolisPipe.pipeItemsPropolis = createPipe(PluginPropolisPipe.propolisPipeItemId, PipeItemsPropolis.class, "Apiarist's Pipe");
	}

	public Item createPipe(int id, Class<? extends Pipe> clas, String description) {

		Item pipe = BlockGenericPipe.registerPipe(id, clas);
		pipe.setItemName(clas.getSimpleName());

		return pipe;
	}

	public void registerCraftingPropolis(ItemStack resource) {
		GameRegistry.addRecipe(new ItemStack(PluginPropolisPipe.pipeItemsPropolis),
				new Object[] { "#X#", Character.valueOf('#'), resource, Character.valueOf('X'), BuildCraftTransport.pipeItemsDiamond });

	}

	public void addLocalizations() {
	}
}
