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
import com.sabmiller.facades.businessenquiry.data.SabmWebsiteErrorsData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmWebsiteErrorsValidator")
public class SabmWebsiteErrorsValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmWebsiteErrorsData> sabmWebsiteErrorsConverter;

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
		final SabmWebsiteErrorsData sabmWebsiteErrorsData = new SabmWebsiteErrorsData();

		sabmWebsiteErrorsConverter.convert(businessEnquiryData, sabmWebsiteErrorsData);

		validateObjectFieldNotNull(sabmWebsiteErrorsData.getIssueDescription(), SabmWebsiteErrorsField.DESCRIPTION, errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmWebsiteErrorsField fieldType, final List<String> errors)
	{
		if (StringUtils.isEmpty((String) field))
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmWebsiteErrorsField
	{
		DESCRIPTION("enquiry.website.errors.description.invalid", "Issue description required.");

		private final String property;
		private final String defaultMsg;

		private SabmWebsiteErrorsField(final String property, final String defaultMsg)
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
