/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import jakarta.annotation.Resource;

import org.springframework.util.Assert;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.constants.SabmFacadesConstants;


/**
 * SABMSearchResultCustomerPopulator
 */
public class SABMSearchResultCustomerPopulator implements Populator<SearchResultValueData, CustomerData>
{

	/** The Service */
	private CommonI18NService commonI18NService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


	/**
	 * converter the SearchResultValueData to CustomerData
	 *
	 */
	@Override
	public void populate(final SearchResultValueData source, final CustomerData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setName(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_NAME));
		target.setUid(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_ID));
		target.setAddressStreetName(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_ARR_STREET));
		target.setAddressSuburb(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_ARR_SUBURB));
		target.setPostCode(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_ARR_POSTCODE));
		target.setIsocodeShort(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_ARR_ISOCODE));
		target.setAccountPayerNumber(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_ACC_PAY_NUMBER));

		final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(target.getUid());
		Boolean dealsExists = Boolean.FALSE;
		if (b2bUnit instanceof AsahiB2BUnitModel)
		{
			target.setPrimaryAdminStatus(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_PRIMARY_ADMIN_STATUS));
			dealsExists = (Boolean) source.getValues().get(SabmFacadesConstants.ASAHI_CUSTOMER_DEALS_EXISTS);
		}
		else
		{
			target.setPrimaryAdminStatus(b2bUnitService.findB2BUnitStatus(target.getUid()));
			dealsExists = (Boolean) source.getValues().get(SabmFacadesConstants.CUSTOMER_DEALS_EXISTS);
		}

		/*
		 * SABMC-1711
		 */
		target.setDealsExists(dealsExists);

		target.setOrderingStatus(this.<String> getValue(source, SabmFacadesConstants.CUSTOMER_ORDERING_STATUS));
	}



	protected <T> T getValue(final SearchResultValueData source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}

		// DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
		return (T) source.getValues().get(propertyName);
	}

}
