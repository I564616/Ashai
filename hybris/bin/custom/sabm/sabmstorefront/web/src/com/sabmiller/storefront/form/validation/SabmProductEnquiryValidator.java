/**
 *
 */
package com.sabmiller.storefront.form.validation;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmProductEnquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


@Component("sabmProductEnquiryValidator")
public class SabmProductEnquiryValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmProductEnquiryData> sabmProductEnquiryConverter;

	@Resource
	private ConfigurationService configurationService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation#validate(com.sabmiller.facades.
	 * businessenquiry.data.AbstractBusinessEnquiryData, java.util.List)
	 */
	@Override
	public void validate(final AbstractBusinessEnquiryData businessEnquiryData, final List<String> errors)
	{
		final SabmProductEnquiryData sabmProductEnquiryData = new SabmProductEnquiryData();

		sabmProductEnquiryConverter.convert(businessEnquiryData, sabmProductEnquiryData);

		validateObjectFieldNotNull(sabmProductEnquiryData.getProduct(), SabmProductEnquiryField.PRODUCT, errors);
		validateObjectFieldNotNull(sabmProductEnquiryData.getPromotionalStock(), SabmProductEnquiryField.PROMOTIONAL_STOCK, errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmProductEnquiryField fieldType, final List<String> errors)
	{
		if (field instanceof String)
		{
			if (StringUtils.isEmpty((String) field))
			{
				errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
			}
		}
		else
		{
			if (field == null)
			{
				errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
			}
		}
	}

	private enum SabmProductEnquiryField
	{
		PRODUCT("enquiry.product.product.invalid", "Product is required."), PROMOTIONAL_STOCK(
				"enquiry.product.promotionalstock.invalid",
				"Promotional stock is required."), INFORMATION("enquiry.product.information.invalid", "Information is required.");

		private final String property;
		private final String defaultMsg;

		private SabmProductEnquiryField(final String property, final String defaultMsg)
		{
			this.property = property;
			this.defaultMsg = defaultMsg;
		}

		public String getProperty()
		{
			return property;
		}

		public String getDefaultMsg()
		{
			return defaultMsg;
		}
	}
}
