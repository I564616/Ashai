/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmProductEnquiryData;


/**
 *
 */
public class SabmProductEnquiryPopulator implements Populator<AbstractBusinessEnquiryData, SabmProductEnquiryData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmProductEnquiryData target) throws ConversionException
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

			// specific to product enquiry
			final Map<String, Object> data = source.getData();

			target.setProduct((String) data.get("details"));

			final Object promo = data.get("promo");
			if (promo instanceof Boolean)
			{
				target.setPromotionalStock((Boolean) promo);
			}
			else if (promo instanceof String)
			{
				target.setPromotionalStock(Boolean.valueOf((String) promo));
			}
			else
			{
				target.setPromotionalStock(Boolean.FALSE);
			}

			//Non-mandatory field
			if ((String) data.get("otherinfo") == null)
			{
				target.setEnquiryInformation(StringUtils.EMPTY);
			}
			else
			{
				target.setEnquiryInformation((String) data.get("otherinfo"));
			}

		}

	}

}
