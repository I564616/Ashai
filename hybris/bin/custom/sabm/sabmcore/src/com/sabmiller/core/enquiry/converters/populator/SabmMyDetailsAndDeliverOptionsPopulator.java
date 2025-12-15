/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmMyDetailsAndDeliverOptionsData;


/**
 *
 */
public class SabmMyDetailsAndDeliverOptionsPopulator
		implements Populator<AbstractBusinessEnquiryData, SabmMyDetailsAndDeliverOptionsData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmMyDetailsAndDeliverOptionsData target)
			throws ConversionException
	{
		// common fields
		target.setName(source.getName());
		target.setEmailAddress(source.getEmailAddress());
		target.setBusinessUnit(source.getBusinessUnit());
		target.setRequestType(source.getRequestType());
		target.setPreferredContactMethod(source.getPreferredContactMethod());
		target.setPhoneNumber(source.getPhoneNumber());

		// specific to update my details and delivery options
		final Map<String, Object> data = source.getData();

		target.setChangeType((String) data.get("type"));
		try
		{
			target.setCurrentDetails((String) data.get("current"));
		}
		catch (final Exception e)
		{
			target.setCurrentDetails(null);
		}
		try
		{
			target.setNewDetails((String) data.get("new"));
		}
		catch (final Exception e)
		{
			target.setNewDetails(null);
		}

		// non-mandatory
		if ((String) data.get("otherinfo") == null)
		{
			target.setOtherInformation(StringUtils.EMPTY);
		}
		else
		{
			target.setOtherInformation((String) data.get("otherinfo"));
		}
	}

}
