/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmDeliveryEnquiryData;


/**
 *
 */
public class SabmDeliveryEnquiryPopulator implements Populator<AbstractBusinessEnquiryData, SabmDeliveryEnquiryData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmDeliveryEnquiryData target) throws ConversionException
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

			// specific to delivery enquiry
			final Map<String, Object> data = source.getData();

			target.setOrderNumber((String) data.get("ordernumber"));
			target.setOtherInformation((String) data.get("message"));

			final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			final String orderDateString = (String) data.get("date");
			Date orderDate;
			try
			{
				orderDate = df.parse(orderDateString);
				target.setOrderDate(orderDate);
			}
			catch (final Exception e)
			{
				target.setOrderDate(null);
			}

			final String expectedDeliveryDateString = (String) data.get("expected");
			Date expectedDeliveryDate;
			try
			{
				expectedDeliveryDate = df.parse(expectedDeliveryDateString);
				target.setExpectedDeliveryDate(expectedDeliveryDate);
			}
			catch (final Exception e)
			{
				target.setExpectedDeliveryDate(null);
			}

			//Non-mandatory field
			if ((String) data.get("message") == null)
			{
				target.setOtherInformation(StringUtils.EMPTY);
			}
			else
			{
				target.setOtherInformation((String) data.get("message"));
			}
		}
	}

}
