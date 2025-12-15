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
import com.sabmiller.facades.businessenquiry.data.SabmEmptyPalletPickupData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmEmptyPalletPickupValidator")
public class SabmEmptyPalletPickupValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmEmptyPalletPickupData> sabmEmptyPalletPickupConverter;

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
		final SabmEmptyPalletPickupData sabmEmptyPalletPickupData = new SabmEmptyPalletPickupData();

		sabmEmptyPalletPickupConverter.convert(businessEnquiryData, sabmEmptyPalletPickupData);

		validateObjectFieldNotNull(sabmEmptyPalletPickupData.getNumberOfEmptyPallets(),
				SabmEmptyPickupField.NUMBER_OF_EMPTY_PALLETS, errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmEmptyPickupField fieldType, final List<String> errors)
	{
		if (field == null)
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmEmptyPickupField
	{
		NUMBER_OF_EMPTY_PALLETS("enquiry.empty.pickup.numberofpallet.invalid", "Number of pallets is required.");

		private final String property;
		private final String defaultMsg;

		private SabmEmptyPickupField(final String property, final String defaultMsg)
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
