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
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;


/**
 *
 */
public class SabmKegIssuePopulator implements Populator<AbstractBusinessEnquiryData, SabmKegIssueData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmKegIssueData target) throws ConversionException
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

			// specific to keg issue
			final Map<String, Object> data = source.getData();

			target.setKegBrand((String) data.get("brand"));
			target.setKegNumber(Integer.parseInt((String) data.get("number")));

			final DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			final DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

			final String orderDateString = (String) data.get("bestBeforeDate");

			try {
				Date orderDate = inputFormat.parse(orderDateString);
				target.setBestBeforeDate(orderDate); // store as Date
				target.setBestBeforeDateString(outputFormat.format(orderDate)); // store as String
			} catch (final Exception e) {
				target.setBestBeforeDate(null);
				target.setBestBeforeDateString(null);
			}

            target.setTimecode((String) data.get("timecode"));
			target.setBestBeforeDateAvailable((String) data.get("bestBeforeDateAvailable"));
			target.setPlantcode(data.get("plantcode") != null ? data.get("plantcode").toString() : null);
			target.setSku(data.get("sku") != null ? data.get("sku").toString() : null);
			target.setUserPk(data.get("userPk") != null ? data.get("userPk").toString() : null);
			target.setReasonCode(data.get("reasonCode") != null ? (String) data.get("reasonCode") : null);

			if ((String) data.get("kegProblem") == null)
			{
				target.setKegProblem(StringUtils.EMPTY);
			}
			else
			{
				target.setKegProblem((String) data.get("kegProblem"));
			}

		}
	}

}
