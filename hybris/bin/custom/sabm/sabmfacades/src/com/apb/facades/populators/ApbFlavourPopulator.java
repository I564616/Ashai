package com.apb.facades.populators;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.apb.core.model.FlavourModel;
import com.apb.facades.product.data.FlavourData;


public class ApbFlavourPopulator implements Populator<FlavourModel, FlavourData>
{

	private CommerceCommonI18NService commerceCommonI18NService;

	public void populate(final FlavourModel source, final FlavourData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName(commerceCommonI18NService.getCurrentLocale()));

	}

	public CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

}
