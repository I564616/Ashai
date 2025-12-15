/**
 *
 */
package com.sabmiller.storefront.form.validation;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmPriceEnquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmPriceEnquiryValidator")
public class SabmPriceEnquiryValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmPriceEnquiryData> sabmPriceEnquiryConverter;

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
		final SabmPriceEnquiryData sabmPriceEnquiryData = new SabmPriceEnquiryData();

		sabmPriceEnquiryConverter.convert(businessEnquiryData, sabmPriceEnquiryData);

		validateObjectFieldNotNull(sabmPriceEnquiryData.getType(), SabmPriceEnquiryField.TYPE, errors);
		validateObjectFieldNotNull(sabmPriceEnquiryData.getProduct(), SabmPriceEnquiryField.PRODUCT, errors);
		validateObjectFieldNotNull(sabmPriceEnquiryData.getDiscountExpected(), SabmPriceEnquiryField.DISCOUNT_EXPECTED, errors);
		validateObjectFieldNotNull(sabmPriceEnquiryData.getDiscountDisplayed(), SabmPriceEnquiryField.DISCOUNT_DISPLAYED, errors);

		if (sabmPriceEnquiryData.getType().equalsIgnoreCase(configurationService.getConfiguration().getString(
				SabmPriceEnquiryField.PROMOTION_OR_DEAL.getProperty(), SabmPriceEnquiryField.PROMOTION_OR_DEAL.getDefaultMsg())))
		{
			validateObjectFieldNotNull(sabmPriceEnquiryData.getMinQuantity(), SabmPriceEnquiryField.MIN_QUANTITY, errors);
		}
	}

	private void validateObjectFieldNotNull(final String field, final SabmPriceEnquiryField fieldType, final List<String> errors)
	{
		if (field.isEmpty())
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmPriceEnquiryField
	{
		TYPE("enquiry.price.type.invalid", "Price enquiry type required."), PRODUCT("enquiry.price.product.invalid",
				"Product name required."), MIN_QUANTITY("enquiry.price.minqty.invalid",
						"Minimum quantity required."), DISCOUNT_EXPECTED("enquiry.price.discount.expected.invalid",
								"Discount expected required."), DISCOUNT_DISPLAYED("enquiry.price.discount.displayed.invalid",
										"Discount displayed required."), PROMOTION_OR_DEAL("enquiry.price.type.promotion",
												"Promotion or Deal");

		private final String property;
		private final String defaultMsg;

		private SabmPriceEnquiryField(final String property, final String defaultMsg)
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
