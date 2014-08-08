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

import java.io.InputStream;
import java.util.Properties;

import net.minecraft.client.texturepacks.ITexturePack;

public class FontColour {

	private Properties defaultMappings = new Properties();
	private Properties mappings = new Properties();

	public FontColour(ITexturePack texturepack) {
		load(texturepack);
	}

	public synchronized int get(String key) {
		return Integer.parseInt(mappings.getProperty(key, defaultMappings.getProperty(key, "d67fff")), 16);
	}

	public void load(ITexturePack texturepack) {
		try {
			InputStream fontStream = texturepack.getResourceAsStream("/config/forestry/colour.properties");
			InputStream defaultFontStream = FontColour.class.getResourceAsStream("/config/forestry/colour.properties");
			mappings.load((fontStream == null) ? defaultFontStream : fontStream);
			defaultMappings.load(defaultFontStream);

			if (fontStream != null) {
				fontStream.close();
			}
			defaultFontStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
