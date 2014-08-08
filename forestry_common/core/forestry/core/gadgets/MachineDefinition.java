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
package forestry.core.gadgets;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

/**
 * This will replace the whole {@link Gadget}-nonsense at some point, using the proper Forge hook. Will hopefully sort out any issues with interfaces. 
 */
public class MachineDefinition {
	
	public final Class<? extends TileEntity> teClass;
	
	public final String teIdent;
	public final int blockID;
	public final int meta;
	
	public final IBlockRenderer renderer;
	private int[] faces = new int[8];

	/* CRAFTING */
	public final IRecipe[] recipes;

	public MachineDefinition(int blockID, int meta, String teIdent, Class<? extends TileEntity> teClass, IRecipe... recipes) {
		this(blockID, meta, teIdent, teClass, null, recipes);
	}
	
	public MachineDefinition(int blockID, int meta, String teIdent, Class<? extends TileEntity> teClass, IBlockRenderer renderer, IRecipe... recipes) {
		this.blockID = blockID;
		this.meta = meta;
		this.teIdent = teIdent;
		this.teClass = teClass;
		this.renderer = renderer;
		this.recipes = recipes;
	}

	public void register() {
		registerTileEntity();
		registerCrafting();
		if(renderer != null)
			Proxies.render.registerTESR(this);
	}
	
	private void registerCrafting() {
		for(IRecipe recipe : recipes)
			CraftingManager.getInstance().getRecipeList().add(recipe);
	}
	
	/**
	 * Registers the tile entity with MC.
	 */
	private void registerTileEntity() {
		GameRegistry.registerTileEntity(teClass, teIdent);
	}
	
	/**
	 * 0 - Bottom
	 * 1 - Top
	 * 2 - Back
	 * 3 - Front
	 * 4,5 - Sides 
	 */
	public MachineDefinition setFaces(int... params) {
		for(int i = 0; i < faces.length; i++) {
			faces[i] = params[i];
		}
		return this;
	}
	
	public MachineDefinition setFace(int texture) {
		for(int i = 0; i < faces.length; i++) {
			faces[i] = texture;
		}
		return this;
	}
	
	public TileEntity createMachine() {
		try {
			return teClass.getConstructor().newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate tile entity of class " + teClass.getName());
		}
	}

	public void getSubBlocks(int id, CreativeTabs tab, List list) {
		list.add(new ItemStack(blockID, 1, meta));
	}

	/* INTERACTION */
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fXplayerClick, float fY, float fZ) {
		return false;
	}
	
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		return true;
	}

	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return false;
	}

	/* TEXTURES */
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		return faces[side];
	}

	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side, int metadata) {
		
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(!(tile instanceof TileForestry))
			return getBlockTextureFromSideAndMetadata(side, metadata);
		
		ForgeDirection dir = ((TileForestry)tile).getOrientation();
		switch(dir) {
		case WEST:
			side = side == 2 ? 4 : side == 3 ? 5 : side == 4 ? 3 : side == 5 ? 2 : side == 0 ? 6 : 7;
			break;
		case EAST:
			side = side == 2 ? 5 : side == 3 ? 4 : side == 4 ? 2 : side == 5 ? 3 : side == 0 ? 6 : 7;
			break;
		case SOUTH:
			break;
		case NORTH:
			side = side == 2 ? 3 : side == 3 ? 2 : side == 4 ? 5 : side == 5 ? 4 : side;
			break;
		default:
		}
		
		return getBlockTextureFromSideAndMetadata(side, metadata);
	}
}
