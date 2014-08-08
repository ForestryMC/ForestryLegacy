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
package forestry.farming.items;

import java.util.List;

import forestry.core.utils.StringUtil;
import forestry.farming.gadgets.TileFarm;
import forestry.farming.gadgets.TileFarm.EnumFarmBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFarmBlock extends ItemBlock {

	protected String name;

	public ItemFarmBlock(int id, String name) {
		super(id);
		setHasSubtypes(true);
		this.name = name;
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List info, boolean par4) {
		if(!itemstack.hasTagCompound())
			return;
		
		info.add(EnumFarmBlock.getFromCompound(itemstack.getTagCompound()).getName());
	}

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {

    	if(!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
    		return false;
    	
    	if(!stack.hasTagCompound())
    		return true;
    	
    	TileFarm tile = (TileFarm)world.getBlockTileEntity(x, y, z);
    	tile.setFarmBlock(EnumFarmBlock.getFromCompound(stack.getTagCompound()));
    	
    	return true;
    }
    
	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getItemNameIS(itemstack));
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "tile." + name + "." + itemstack.getItemDamage();
	}

}
