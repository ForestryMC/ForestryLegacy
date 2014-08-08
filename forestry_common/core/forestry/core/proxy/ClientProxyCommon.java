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
package forestry.core.proxy;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.input.Keyboard;

import forestry.core.ForestryClient;
import forestry.core.config.Config;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.MachinePackage;
import forestry.core.render.BlockRenderingHandler;
import forestry.core.render.TextureHabitatLocatorFX;
import forestry.core.render.TileRendererIndex;

public class ClientProxyCommon extends ProxyCommon {

	@Override
	public void bindTexture(String textureFile) {
		ForgeHooksClient.bindTexture(textureFile, 0);
	}

	@Override
	public ITexturePack getSelectedTexturePack(Minecraft minecraft) {
		return minecraft.texturePackList.getSelectedTexturePack();
	}

	@Override
	public void setBiomeFinderCoordinates(EntityPlayer player, ChunkCoordinates coordinates) {
		if (isSimulating(player.worldObj)) {
			super.setBiomeFinderCoordinates(player, coordinates);
		} else {
			TextureHabitatLocatorFX.instance.setTargetCoordinates(coordinates);
		}
	}

	@Override
	public File getForestryRoot() {
		return Minecraft.getMinecraftDir();
	}

	@Override
	public World getRenderWorld() {
		return getClientInstance().theWorld;
	}

	@Override
	public int getBlockModelIdEngine() {
		return ForestryClient.blockModelIdEngine;
	}

	@Override
	public int getByBlockModelId() {
		return ForestryClient.byBlockModelId;
	}

	@Override
	public boolean isOp(EntityPlayer player) {
		return true;
	}

	@Override
	public double getBlockReachDistance(EntityPlayer entityplayer) {
		if (entityplayer instanceof EntityPlayerSP)
			return getClientInstance().playerController.getBlockReachDistance();
		else
			return 4f;
	}

	@Override
	public boolean isSimulating(World world) {
		return !world.isRemote;
	}

	@Override
	public boolean isShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@Override
	public String getItemDisplayName(Item item) {
		return Item.itemsList[item.itemID].getItemDisplayName(null);
	}

	@Override
	public String getItemDisplayName(ItemStack stack) {
		return stack.getItem().getItemDisplayName(stack);
	}

	@Override
	public String getCurrentLanguage() {
		return StringTranslate.getInstance().getCurrentLanguage();
	}

	@Override
	public String getDisplayName(ItemStack itemstack) {
		return itemstack.getItem().getItemDisplayName(itemstack);
	}

	@Override
	public void playSoundFX(World world, int x, int y, int z, Block block) {
		if(Proxies.common.isSimulating(world))
			super.playSoundFX(world, x, y, z, block);
		else
			playSoundFX(world, x, y, z, block.stepSound.getPlaceSound(), block.stepSound.getVolume(), block.stepSound.getPitch());
	}

	@Override
	public void playBlockBreakSoundFX(World world, int x, int y, int z, Block block) {
		if(Proxies.common.isSimulating(world))
			super.playSoundFX(world, x, y, z, block);
		else
			playSoundFX(world, x, y, z, block.stepSound.getBreakSound(), block.stepSound.getVolume() / 4, block.stepSound.getPitch());
	}

	@Override
	public void playBlockPlaceSoundFX(World world, int x, int y, int z, Block block) {
		if(Proxies.common.isSimulating(world))
			super.playSoundFX(world, x, y, z, block);
		else
			playSoundFX(world, x, y, z, block.stepSound.getPlaceSound(), block.stepSound.getVolume() / 4, block.stepSound.getPitch());
	}


	@Override
	public void playSoundFX(World world, int x, int y, int z, String sound, float volume, float pitch) {
		world.playSound((double)(x + 0.5), (double)(y + 0.5), (double)(z + 0.5), sound, volume, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f, false);
	}
	
	/**
	 * Renders a EntityBiodustFX on client.
	 * 
	 * @param world
	 * @param d1
	 * @param d2
	 * @param d3
	 * @param f1
	 * @param f2
	 * @param f3
	 */
	// FIXME: This is causing crashes.
	@Override
	public void addEntityBiodustFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
		if (!Config.enableParticleFX)
			return;

		// ModLoader.getMinecraftInstance().effectRenderer.addEffect(new EntityBiodustFX(world, d1, d2, d3, f1, f2, f3));
	}

	// FIXME: This is causing crashes.
	@Override
	public void addEntitySwarmFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
		if (!Config.enableParticleFX)
			return;

		// ModLoader.getMinecraftInstance().effectRenderer.addEffect(new EntityHoneydustFX(world, d1, d2, d3, f1, f2, f3));
	}

	@Override
	public void addEntityExplodeFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
		if (!Config.enableParticleFX)
			return;

		getClientInstance().effectRenderer.addEffect(new EntityExplodeFX(world, d1, d2, d3, f1, f2, f3));
	}

	@Override
	public void registerPlanterRenderer(int meta, MachinePackage pack) {
		pack.renderer.preloadTextures();
		BlockRenderingHandler.byBlockRenderer.put(new TileRendererIndex(ForestryBlock.planter, meta), pack.renderer);
	}

	@Override
	public void registerMachineRenderer(int meta, MachinePackage pack) {
		pack.renderer.preloadTextures();
		BlockRenderingHandler.byBlockRenderer.put(new TileRendererIndex(ForestryBlock.machine, meta), pack.renderer);
	}

	@Override
	public void registerMillRenderer(int meta, MachinePackage pack) {
		pack.renderer.preloadTextures();
		BlockRenderingHandler.byBlockRenderer.put(new TileRendererIndex(ForestryBlock.mill, meta), pack.renderer);
	}

	@Override
	public void addBlockDestroyEffects(World world, int xCoord, int yCoord, int zCoord, int blockid, int i) {
		if (!isSimulating(world)) {
			getClientInstance().effectRenderer.addBlockDestroyEffects(xCoord, yCoord, zCoord, blockid, i);
		} else {
			super.addBlockDestroyEffects(world, xCoord, yCoord, zCoord, blockid, i);
		}
	}

	public void addBlockPlaceEffects(World world, int xCoord, int yCoord, int zCoord, int blockid, int i) {
		if(!isSimulating(world))
			playBlockPlaceSoundFX(world, xCoord, yCoord, zCoord, Block.blocksList[blockid]);
		else
			super.addBlockPlaceEffects(world, xCoord, yCoord, zCoord, blockid, i);
	}

}
