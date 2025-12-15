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
import com.sabmiller.facades.businessenquiry.data.SabmWebsiteInquiryFeedbackData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmWebsiteInquiryFeedbackValidator")
public class SabmWebsiteInquiryFeedbackValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmWebsiteInquiryFeedbackData> sabmWebsiteInquiryFeedbackConverter;

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
		final SabmWebsiteInquiryFeedbackData sabmWebsiteInquiryFeedbackData = new SabmWebsiteInquiryFeedbackData();

		sabmWebsiteInquiryFeedbackConverter.convert(businessEnquiryData, sabmWebsiteInquiryFeedbackData);

		validateObjectFieldNotNull(sabmWebsiteInquiryFeedbackData.getWebsiteEnquiryFeedback(), SabmWebsiteFeedbackField.FEEDBACK,
				errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmWebsiteFeedbackField fieldType,
			final List<String> errors)
	{
		if (StringUtils.isEmpty((String) field))
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmWebsiteFeedbackField
	{
		FEEDBACK("enquiry.website.inquiry.feedback.invalid", "Website inquiry feedback required.");

		private final String property;
		private final String defaultMsg;

		private SabmWebsiteFeedbackField(final String property, final String defaultMsg)
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
