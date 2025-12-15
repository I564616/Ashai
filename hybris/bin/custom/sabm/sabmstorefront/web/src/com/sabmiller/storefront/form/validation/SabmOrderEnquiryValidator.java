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
import com.sabmiller.facades.businessenquiry.data.SabmOrderEnquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;

/**
 * @author r.vinod.prasad.singh
 *
 */
@Component("sabmOrderEnquiryValidator")
public class SabmOrderEnquiryValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmOrderEnquiryData> sabmOrderEnquiryConverter;
	
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
		final SabmOrderEnquiryData sabmOrderEnquiryData = new SabmOrderEnquiryData();

		sabmOrderEnquiryConverter.convert(businessEnquiryData, sabmOrderEnquiryData);

		validateObjectFieldNotNull(sabmOrderEnquiryData.getYourMessage(), SabmOrderEnquiryField.YOUR_MESSAGE, errors);
	}
	
	private void validateObjectFieldNotNull(final Object field, final SabmOrderEnquiryField fieldType, final List<String> errors)
	{
		if (field == null)
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}
	
	private enum SabmOrderEnquiryField
	{
		YOUR_MESSAGE("enquiry.order.yourmessage.invalid", "Your message is required.");
		private final String property;
		private final String defaultMsg;

		private SabmOrderEnquiryField(final String property, final String defaultMsg)
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
