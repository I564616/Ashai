/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmUpdateExistingEnquiryData;


/**
 * @author r.vinod.prasad.singh
 *
 */
public class SabmUpdateExistingEnquiryPopulator implements Populator<AbstractBusinessEnquiryData, SabmUpdateExistingEnquiryData>
{
	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmUpdateExistingEnquiryData target)
			throws ConversionException
	{
		validateParameterNotNull(source, String.format("Source [%s] cannot be null", source.getClass()));
		validateParameterNotNull(source, String.format("Target [%s] cannot be null", target.getClass()));

		// common fields
		target.setName(source.getName());
		target.setEmailAddress(source.getEmailAddress());
		target.setBusinessUnit(source.getBusinessUnit());
		target.setRequestType(source.getRequestType());
		target.setPreferredContactMethod(source.getPreferredContactMethod());
		target.setPhoneNumber(source.getPhoneNumber());

		// specific to Update Existing Enquiry
		final Map<String, Object> data = source.getData();


		if ((String) data.get("casenumber") == null)
		{
			target.setCaseNumber(StringUtils.EMPTY);
		}
		else
		{
			target.setCaseNumber((String) data.get("casenumber"));
		}

		if ((String) data.get("yourmsg") == null)
		{
			target.setYourMessage(StringUtils.EMPTY);
		}
		else
		{
			target.setYourMessage((String) data.get("yourmsg"));
		}
	}

}
