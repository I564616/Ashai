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
import com.sabmiller.facades.businessenquiry.data.SabmAutopayInquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;

/**
 * @author marc.f.l.bautista
 *
 */
@Component("sabmAutopayInquiryValidator")
public class SabmAutopayInquiryValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmAutopayInquiryData> sabmAutopayInquiryConverter;

	@Resource
	private ConfigurationService configurationService;

	/* (non-Javadoc)
	 * @see com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation#validate(com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData, java.util.List)
	 */
	@Override
	public void validate(AbstractBusinessEnquiryData businessEnquiryData, List<String> errors)
	{
		final SabmAutopayInquiryData sabmAutopayInquiryData = new SabmAutopayInquiryData();

		sabmAutopayInquiryConverter.convert(businessEnquiryData, sabmAutopayInquiryData);

		validateObjectFieldNotNull(sabmAutopayInquiryData.getInquiryMessage(), SabmAutopayInquiryField.INQUIRY_MESSAGE, errors);
	}

	private void validateObjectFieldNotNull(final String field, final SabmAutopayInquiryField fieldType, final List<String> errors)
	{
		if (StringUtils.isBlank(field))
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmAutopayInquiryField
	{
		INQUIRY_MESSAGE("enquiry.autopay.message.invalid", "Inquiry message required.");

		private final String property;
		private final String defaultMsg;

		private SabmAutopayInquiryField(final String property, final String defaultMsg)
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
