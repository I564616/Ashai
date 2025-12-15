package com.apb.facades.populators;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.apb.core.model.PackageTypeModel;
import com.apb.facades.product.data.PackageTypeData;


/**
 * <p>
 * class to populate packagetype of product
 * </p>
 */
public class ApbPackageTypePopulator implements Populator<PackageTypeModel, PackageTypeData>
{

	private CommerceCommonI18NService commerceCommonI18NService;

	public void populate(final PackageTypeModel source, final PackageTypeData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName(commerceCommonI18NService.getCurrentLocale()));

	}

	/**
	 * @return CommerceCommonI18NService
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
