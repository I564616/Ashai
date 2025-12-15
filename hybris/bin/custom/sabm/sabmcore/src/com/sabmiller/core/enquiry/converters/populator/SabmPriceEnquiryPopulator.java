/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmPriceEnquiryData;


/**
 *
 */
public class SabmPriceEnquiryPopulator implements Populator<AbstractBusinessEnquiryData, SabmPriceEnquiryData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmPriceEnquiryData target) throws ConversionException
	{
		// YTODO Auto-generated method stub
		if (source != null)
		{
			// common fields
			target.setName(source.getName());
			target.setEmailAddress(source.getEmailAddress());
			target.setBusinessUnit(source.getBusinessUnit());
			target.setRequestType(source.getRequestType());
			target.setPreferredContactMethod(source.getPreferredContactMethod());
			target.setPhoneNumber(source.getPhoneNumber());

			// specific to pricing enquiry
			final Map<String, Object> data = source.getData();

			target.setType((String) data.get("type"));
			target.setProduct((String) data.get("product"));

			target.setDiscountDisplayed((String) data.get("displayed"));
			target.setDiscountExpected((String) data.get("expected"));


			if (data.get("type").toString().equalsIgnoreCase("Promotion or Deal"))
			{
				target.setMinQuantity((String) data.get("min"));
			}
			else
			{
				target.setMinQuantity(StringUtils.EMPTY);
			}

			//Non-mandatory field
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

}
