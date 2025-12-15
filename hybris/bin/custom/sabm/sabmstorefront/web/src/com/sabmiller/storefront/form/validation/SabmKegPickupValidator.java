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
import com.sabmiller.facades.businessenquiry.data.SabmKegPickupData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmKegPickupValidator")
public class SabmKegPickupValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmKegPickupData> sabmKegPickupConverter;

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
		final SabmKegPickupData sabmKegPickupData = new SabmKegPickupData();

		sabmKegPickupConverter.convert(businessEnquiryData, sabmKegPickupData);

		validateObjectFieldNotNull(sabmKegPickupData.getNumberOfEmptyKegs(), SabmKegPickupField.NUM_EMPTY_KEGS, errors);
		validateObjectFieldNotNull(sabmKegPickupData.getNumberOfPartFullKegs(), SabmKegPickupField.NUM_PART_FULL_KEGS, errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmKegPickupField fieldType, final List<String> errors)
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

	private enum SabmKegPickupField
	{
		NUM_EMPTY_KEGS("enquiry.kegpickup.numempty.invalid", "Invalid number of empty kegs"), NUM_PART_FULL_KEGS(
				"enquiry.kegpickup.numpartfull.invalid", "Invalid number of part full kegs");

		private final String property;
		private final String defaultMsg;

		private SabmKegPickupField(final String property, final String defaultMsg)
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
