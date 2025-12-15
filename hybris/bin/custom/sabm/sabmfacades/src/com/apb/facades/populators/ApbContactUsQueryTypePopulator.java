package com.apb.facades.populators;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.apb.core.model.ContactUsQueryTypeModel;
import com.apb.facades.contactust.data.ContactUsQueryTypeData;


/**
 * @author c5252631
 *
 *         ApbContactUsQueryTypePopulator implementation of {@link Populator}
 */
public class ApbContactUsQueryTypePopulator implements Populator<ContactUsQueryTypeModel, ContactUsQueryTypeData>
{
	private CommerceCommonI18NService commerceCommonI18NService;

	@Override
	public void populate(final ContactUsQueryTypeModel source, final ContactUsQueryTypeData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getContactUsQueryType((commerceCommonI18NService.getCurrentLocale())));
	}

	/**
	 * @return the commerceCommonI18NService
	 */
	public CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	/**
	 * @param commerceCommonI18NService
	 *           the commerceCommonI18NService to set
	 */
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

}
