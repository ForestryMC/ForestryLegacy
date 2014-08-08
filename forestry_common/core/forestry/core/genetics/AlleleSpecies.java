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
package forestry.core.genetics;

import net.minecraft.stats.Achievement;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.core.utils.Localization;
import forestry.core.utils.StringUtil;

public abstract class AlleleSpecies extends Allele implements IAlleleSpecies {

	private String name;
	private String binomial;
	private String description = null;
	
	private int bodyType = 0;
	private boolean hasEffect = false;
	private boolean isSecret = false;
	private boolean isCounted = true;
	private int primaryColor;
	private int secondaryColor;
	private Achievement achievement = null;
	private IClassification branch = null;

	private EnumTemperature climate = EnumTemperature.NORMAL;
	private EnumHumidity humidity = EnumHumidity.NORMAL;

	public AlleleSpecies(String uid, boolean isDominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		super(uid, isDominant, true);

		this.branch = branch;
		this.name = name;
		this.binomial = binomial;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;

		if(Localization.instance.hasMapping("description." + uid))
			description = StringUtil.localize("description." + uid);
		
		AlleleManager.alleleRegistry.registerAllele(this);
	}

	public AlleleSpecies setBodyType(int bodyType) {
		this.bodyType = bodyType;
		return this;
	}

	@Override
	public String getName() {
		return StringUtil.localize(name);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getBodyType() {
		return bodyType;
	}

	@Override
	public int getPrimaryColor() {
		return primaryColor;
	}

	@Override
	public int getSecondaryColor() {
		return secondaryColor;
	}

	@Override
	public EnumTemperature getTemperature() {
		return climate;
	}

	@Override
	public EnumHumidity getHumidity() {
		return humidity;
	}

	@Override
	public boolean hasEffect() {
		return hasEffect;
	}

	@Override
	public boolean isSecret() {
		return isSecret;
	}

	@Override
	public boolean isCounted() {
		return isCounted;
	}

	@Override
	public String getBinomial() {
		return binomial;
	}

	@Override
	public String getAuthority() {
		return "Sengir";
	}

	@Override
	public Achievement getAchievement() {
		return achievement;
	}

	@Override
	public IClassification getBranch() {
		return this.branch;
	}

	public AlleleSpecies setTemperature(EnumTemperature temperature) {
		climate = temperature;
		return this;
	}

	public AlleleSpecies setHumidity(EnumHumidity humidity) {
		this.humidity = humidity;
		return this;
	}

	public AlleleSpecies setHasEffect() {
		hasEffect = true;
		return this;
	}

	public AlleleSpecies setIsSecret() {
		isSecret = true;
		return this;
	}

	public AlleleSpecies setIsNotCounted() {
		isCounted = false;
		return this;
	}

	public AlleleSpecies setAchievement(Achievement achievement) {
		this.achievement = achievement;
		return this;
	}
}
