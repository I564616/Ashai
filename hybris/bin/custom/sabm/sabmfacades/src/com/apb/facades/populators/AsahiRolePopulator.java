package com.apb.facades.populators;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.enums.AsahiRole;
import com.apb.facades.product.data.AsahiRoleData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class AsahiRolePopulator implements Populator<AsahiRole, AsahiRoleData> {

	private static final String OWNER = "Owner";
	private static final String AREAMANAGER = "Area Manager";
	private static final String VENUEOUTLETMANAGER = "Venue / Outlet Manager";
	private static final String OTHER = "Other";

	@Override
	public void populate(AsahiRole asahiRole, AsahiRoleData asahiRoleData) throws ConversionException {

		if (null != asahiRole) {
			asahiRoleData.setCode(asahiRole.getCode());
			asahiRoleData.setName(getRoleName(asahiRole));
		}

	}

	private String getRoleName(AsahiRole asahiRole) {

		String asahiRoleName = StringUtils.EMPTY;
		if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.OWNER.toString())) {
			asahiRoleName = OWNER;
		} else if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.AREAMANAGER.toString())) {
			asahiRoleName = AREAMANAGER;
		} else if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.VENUEOUTLETMANAGER.toString())) {
			asahiRoleName = VENUEOUTLETMANAGER;
		} else if (asahiRole.getCode().equalsIgnoreCase(AsahiRole.OTHER.toString())) {
			asahiRoleName = OTHER;
		}

		return asahiRoleName;
	}

}
