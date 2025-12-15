package com.apb.facades.populators;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.apb.core.model.BrandModel;
import com.apb.facades.product.data.BrandData;


public class ApbBrandPopulator implements Populator<BrandModel, BrandData>
{

	private CommerceCommonI18NService commerceCommonI18NService;

	public void populate(final BrandModel source, final BrandData target) throws ConversionException
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
