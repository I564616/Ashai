package com.apb.facades.populators;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.apb.core.model.PackageSizeModel;
import com.apb.facades.product.data.PackageSizeData;


/**
 * package size populator.
 */
public class ApbProductPackagingPopulator implements Populator<PackageSizeModel, PackageSizeData>
{
	private CommerceCommonI18NService commerceCommonI18NService;

	@Override
	public void populate(final PackageSizeModel source, final PackageSizeData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName(commerceCommonI18NService.getCurrentLocale()));

	}

	/**
	 * @return commerceI18NService
	 */
	public CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	/**
	 * @param commerceCommonI18NService
	 */
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}


}
