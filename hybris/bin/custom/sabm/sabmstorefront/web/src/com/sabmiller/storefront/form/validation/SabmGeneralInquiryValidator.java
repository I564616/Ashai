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
import com.sabmiller.facades.businessenquiry.data.SabmGeneralInquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmGeneralInquiryValidator")
public class SabmGeneralInquiryValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmGeneralInquiryData> sabmGeneralInquiryConverter;

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
		final SabmGeneralInquiryData sabmGeneralInquiryData = new SabmGeneralInquiryData();

		sabmGeneralInquiryConverter.convert(businessEnquiryData, sabmGeneralInquiryData);

		validateObjectFieldNotNull(sabmGeneralInquiryData.getInquiryMessage(), SabmGeneralInquiryField.INQUIRY_MESSAGE, errors);
	}

	private void validateObjectFieldNotNull(final String field, final SabmGeneralInquiryField fieldType, final List<String> errors)
	{
		if (StringUtils.isBlank(field))
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmGeneralInquiryField
	{
		INQUIRY_MESSAGE("enquiry.general.message.invalid", "Inquiry message required.");

		private final String property;
		private final String defaultMsg;

		private SabmGeneralInquiryField(final String property, final String defaultMsg)
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
