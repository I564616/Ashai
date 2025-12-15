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
import com.sabmiller.facades.businessenquiry.data.SabmUpdateExistingEnquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;

/**
 * @author r.vinod.prasad.singh
 *
 */
@Component("sabmUpdateExistingEnquiryValidator")
public class SabmUpdateExistingEnquiryValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmUpdateExistingEnquiryData> sabmUpdateExistingEnquiryConverter;
	
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
		final SabmUpdateExistingEnquiryData sabmUpdateExistingEnquiryData = new SabmUpdateExistingEnquiryData();
		sabmUpdateExistingEnquiryConverter.convert(businessEnquiryData, sabmUpdateExistingEnquiryData);
		validateObjectFieldNotNull(sabmUpdateExistingEnquiryData.getYourMessage(), SabmUpdateExistingEnquiryField.YOUR_MESSAGE, errors);
	}
	private void validateObjectFieldNotNull(final Object field, SabmUpdateExistingEnquiryField fieldType, final List<String> errors)
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
	private enum SabmUpdateExistingEnquiryField
	{
		YOUR_MESSAGE("enquiry.update.existing.yoourmessage.invalid", "Your message is Required.");
		private final String property;
		private final String defaultMsg;
		
		private SabmUpdateExistingEnquiryField(final String property, final String defaultMsg)
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
