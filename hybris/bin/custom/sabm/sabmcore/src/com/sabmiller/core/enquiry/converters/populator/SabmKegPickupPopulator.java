/**
 *
 */
package com.sabmiller.core.enquiry.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmKegPickupData;


/**
 *
 */
public class SabmKegPickupPopulator implements Populator<AbstractBusinessEnquiryData, SabmKegPickupData>
{
	private static int MAX_EMPTY_ALLOWED = 20;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmKegPickupData target) throws ConversionException
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

			// specific to keg pickup
			final Map<String, Object> data = source.getData();

			final Object numberOfEmptyKegs = data.get("empty");
			if (numberOfEmptyKegs instanceof Integer)
			{
				final Integer numberOfEmptyKegsFinal = (Integer) numberOfEmptyKegs;
				//INC0600331
				/*
				 * if (numberOfEmptyKegsFinal.intValue() > SabmKegPickupPopulator.MAX_EMPTY_ALLOWED) {
				 * target.setNumberOfEmptyKegs(null); } else { target.setNumberOfEmptyKegs(numberOfEmptyKegsFinal); }
				 */
				target.setNumberOfEmptyKegs(numberOfEmptyKegsFinal);
			}
			else if (numberOfEmptyKegs instanceof String)
			{
				final String numberOfEmptyKegsFinal = (String) numberOfEmptyKegs;
				//INC0600331
				/*
				 * if (Integer.valueOf(numberOfEmptyKegsFinal).intValue() > SabmKegPickupPopulator.MAX_EMPTY_ALLOWED) {
				 * target.setNumberOfEmptyKegs(null); } else {
				 * target.setNumberOfEmptyKegs(Integer.valueOf(numberOfEmptyKegsFinal)); }
				 */
				target.setNumberOfEmptyKegs(Integer.valueOf(numberOfEmptyKegsFinal));
			}
			else
			{
				target.setNumberOfEmptyKegs(null);
			}

			final Object numberOfPartFullKegs = data.get("part");
			if (numberOfPartFullKegs instanceof Integer)
			{
				final Integer numberOfPartFullKegsFinal = (Integer) numberOfPartFullKegs;
				//INC0600331
				/*
				 * if (numberOfPartFullKegsFinal != null && numberOfPartFullKegsFinal.intValue() >
				 * SabmKegPickupPopulator.MAX_EMPTY_ALLOWED) { target.setNumberOfPartFullKegs(null); } else {
				 * target.setNumberOfPartFullKegs(numberOfPartFullKegsFinal); }
				 */
				target.setNumberOfPartFullKegs(numberOfPartFullKegsFinal);
			}
			else if (numberOfPartFullKegs instanceof String)
			{
				final String numberOfPartFullKegsFinal = (String) numberOfPartFullKegs;
				//INC0600331
				/*
				 * if (numberOfPartFullKegsFinal != null && Integer.valueOf(numberOfPartFullKegsFinal).intValue() >
				 * SabmKegPickupPopulator.MAX_EMPTY_ALLOWED) { target.setNumberOfPartFullKegs(null); } else {
				 * target.setNumberOfPartFullKegs(Integer.valueOf(numberOfPartFullKegsFinal)); }
				 */
				target.setNumberOfPartFullKegs(Integer.valueOf(numberOfPartFullKegsFinal));
			}
			else
			{
				target.setNumberOfPartFullKegs(null);
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
