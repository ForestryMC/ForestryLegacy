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
package forestry.mail.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.mail.POBoxInfo;

public class GuiMailboxInfo extends Gui {

	public static GuiMailboxInfo instance;

	private FontRenderer fontRenderer;
	private POBoxInfo poInfo;

	public GuiMailboxInfo() {
		fontRenderer = Proxies.common.getClientInstance().fontRenderer;
	}

	public void render(int x, int y) {
		if (poInfo == null)
			return;
		if (Proxies.common.getRenderWorld() == null)
			return;
		if (!Config.mailAlertEnabled)
			return;
		if (!poInfo.hasMail())
			return;

		GL11.glEnable(3042);
		GL11.glEnable(32826);

		int texture = Proxies.common.getClientInstance().renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/mailalert.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Proxies.common.getClientInstance().renderEngine.bindTexture(texture);

		this.drawTexturedModalRect(x, y, 0, 0, 98, 17);

		fontRenderer
				.drawString(Integer.toString(poInfo.playerLetters), x + 27 + getCenteredOffset(Integer.toString(poInfo.playerLetters), 22), y + 5, 0xffffff);
		fontRenderer.drawString(Integer.toString(poInfo.tradeLetters), x + 75 + getCenteredOffset(Integer.toString(poInfo.tradeLetters), 22), y + 5, 0xffffff);

		GL11.glDisable(32826);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	protected int getCenteredOffset(String string, int xWidth) {
		return (xWidth - fontRenderer.getStringWidth(string)) / 2;
	}

	public void setPOBoxInfo(POBoxInfo info) {
		boolean playJingle = false;

		if (info.hasMail()) {
			if (this.poInfo == null) {
				playJingle = true;
			} else if (this.poInfo.playerLetters != info.playerLetters || this.poInfo.tradeLetters != info.tradeLetters) {
				playJingle = true;
			}
		}

		if (playJingle) {
			Proxies.common.getRenderWorld().playSoundAtEntity(Proxies.common.getClientInstance().thePlayer, "random.levelup", 1.0f, 1.0f);
		}

		this.poInfo = info;
	}
}
