/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmEmptyPalletPickupData;


/**
 *
 */
public class SabmEmptyPalletPickupPopulator implements Populator<AbstractBusinessEnquiryData, SabmEmptyPalletPickupData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmEmptyPalletPickupData target)
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

			// specific to empty pallet pickup
			final Map<String, Object> data = source.getData();

			final Object empty = data.get("empty");
			if (empty instanceof Integer)
			{
				target.setNumberOfEmptyPallets((Integer) empty);
			}
			else if (empty instanceof String)
			{
				target.setNumberOfEmptyPallets(Integer.valueOf((String) empty));
			}
			else
			{
				target.setNumberOfEmptyPallets(null);
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

}
