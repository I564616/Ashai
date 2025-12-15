package com.sabmiller.core.enquiry.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmAutopayInquiryData;

/**
 * @author marc.f.l.bautista
 *
 */
public class SabmAutopayInquiryPopulator implements Populator<AbstractBusinessEnquiryData, SabmAutopayInquiryData>
{

	/* (non-Javadoc)
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(AbstractBusinessEnquiryData source, SabmAutopayInquiryData target) throws ConversionException
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

		// specific to autopay
		final Map<String, Object> data = source.getData();

		if ((String) data.get("otherinfo") == null)
		{
			target.setInquiryMessage(StringUtils.EMPTY);
		}
		else
		{
			target.setInquiryMessage((String) data.get("otherinfo"));
		}
	}

}
