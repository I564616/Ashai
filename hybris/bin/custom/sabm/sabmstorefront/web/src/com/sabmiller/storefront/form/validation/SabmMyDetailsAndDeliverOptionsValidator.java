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
import com.sabmiller.facades.businessenquiry.data.SabmMyDetailsAndDeliverOptionsData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmMyDetailsAndDeliverOptionsValidator")
public class SabmMyDetailsAndDeliverOptionsValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmMyDetailsAndDeliverOptionsData> sabmMyDetailsAndDeliverOptionsConverter;

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
		final SabmMyDetailsAndDeliverOptionsData sabmMyDetailsAndDeliverOptionsData = new SabmMyDetailsAndDeliverOptionsData();

		sabmMyDetailsAndDeliverOptionsConverter.convert(businessEnquiryData, sabmMyDetailsAndDeliverOptionsData);

		validateObjectFieldNotNull(sabmMyDetailsAndDeliverOptionsData.getChangeType(), SabmMyDetailsField.CHANGE_TYPE, errors);
		validateObjectFieldNotNull(sabmMyDetailsAndDeliverOptionsData.getCurrentDetails(), SabmMyDetailsField.CURRENT_DETAILS,
				errors);
		validateObjectFieldNotNull(sabmMyDetailsAndDeliverOptionsData.getNewDetails(), SabmMyDetailsField.NEW_DETAILS, errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmMyDetailsField fieldType, final List<String> errors)
	{
		if (StringUtils.isEmpty((String) field))
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmMyDetailsField
	{
		CHANGE_TYPE("enquiry.mydetails.changetype.invalid", "Change type is required."), CURRENT_DETAILS(
				"enquiry.mydetails.currentdetails.invalid",
				"Current details is required."), NEW_DETAILS("enquiry.mydetails.newdetails.invalid", "New details is required.");

		private final String property;
		private final String defaultMsg;

		private SabmMyDetailsField(final String property, final String defaultMsg)
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
