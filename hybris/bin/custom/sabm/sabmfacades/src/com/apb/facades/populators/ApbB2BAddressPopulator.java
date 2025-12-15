package com.apb.facades.populators;

import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BAddressPopulator;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.constants.ApbFacadesConstants;
import com.sabmiller.core.constants.SabmCoreConstants;


public class ApbB2BAddressPopulator extends B2BAddressPopulator
{
	@Autowired
	private CMSSiteService cmsSiteService;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void populate(final AddressModel source, final AddressData target) throws ConversionException
	{
		super.populate(source, target);
		if(!asahiSiteUtil.isCub())
		{
   		if (source.getPk() != null)
   		{
   			target.setId(source.getPk().toString());
   		}
   		target.setDefaultAddress(source.getDefaultAddress());
   		target.setDeliveryInstruction(source.getEclDeliveryInstruction());
   		target.setRecordId(source.getAddressRecordid());
   		String firstName = StringUtils.EMPTY;
   		String lastName = StringUtils.EMPTY;
   		if (StringUtils.isNotEmpty(source.getFirstname()))
   		{
   			firstName = source.getFirstname();
   		}
   		if (StringUtils.isNotEmpty(source.getLastname()))
   		{
   			lastName = source.getLastname();
   		}
   		if (StringUtils.isNotEmpty(source.getAddressRecordid()))
   		{
   			target.setCode(source.getAddressRecordid());
   			final String addressFormat = asahiConfigurationService
   					.getString(ApbFacadesConstants.KEGRETURN_PICK_ADDRESS_FORMAT + cmsSiteService.getCurrentSite().getUid(), "");
   			target.setName(addressFormat + source.getAddressRecordid() + ", " + firstName + " " + lastName);
   		}
   		target.setFirstName(firstName);
   		target.setTown(source.getTown());
   		target.setCompanyName(source.getCompany());
   		if (source.getTitle() != null)
   		{
   			target.setTitle(source.getTitle().getName());
   		}
   		target.setLastName(source.getLastname());
   		target.setLine1(source.getLine1());
   		target.setLine2(source.getLine2());
   		target.setPostalCode(source.getPostalcode());
   		final RegionData regionData = new RegionData();
   		if (source.getRegion() != null)
   		{
   			regionData.setIsocode(source.getRegion().getIsocode());
   			regionData.setName(source.getRegion().getName());
   		}
   		target.setRegion(regionData);
   		final CountryData countryData = new CountryData();
   		if (source.getCountry() != null)
   		{
   			countryData.setIsocode(source.getCountry().getIsocode());
   			countryData.setName(source.getCountry().getName());
   		}
   		target.setCountry(countryData); 
		}
	}
}
