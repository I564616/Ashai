/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmWebsiteInquiryFeedbackData;


/**
 *
 */
public class SabmWebsiteInquiryFeedbackPopulator implements Populator<AbstractBusinessEnquiryData, SabmWebsiteInquiryFeedbackData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmWebsiteInquiryFeedbackData target)
			throws ConversionException
	{
		if (source != null)
		{
			// common fields
			target.setName(source.getName());
			target.setEmailAddress(source.getEmailAddress());
			target.setBusinessUnit(source.getBusinessUnit());
			target.setRequestType(source.getRequestType());
			target.setPreferredContactMethod(source.getPreferredContactMethod());
			target.setPhoneNumber(source.getPhoneNumber());

			// specific to website errors
			final Map<String, Object> data = source.getData();

			// non-mandatory
			if ((String) data.get("otherinfo") == null)
			{
				target.setWebsiteEnquiryFeedback(StringUtils.EMPTY);
			}
			else
			{
				target.setWebsiteEnquiryFeedback((String) data.get("otherinfo"));
			}
		}
	}

}
