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
import com.sabmiller.facades.businessenquiry.data.SabmProductReturnData;


/**
 *
 */
public class SabmProductReturnPopulator implements Populator<AbstractBusinessEnquiryData, SabmProductReturnData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmProductReturnData target) throws ConversionException
	{
		// YTODO Auto-generated method stub
		// common fields
		target.setName(source.getName());
		target.setEmailAddress(source.getEmailAddress());
		target.setBusinessUnit(source.getBusinessUnit());
		target.setRequestType(source.getRequestType());
		target.setPreferredContactMethod(source.getPreferredContactMethod());
		target.setPhoneNumber(source.getPhoneNumber());

		// specific to product enquiry
		final Map<String, Object> data = source.getData();

		target.setInvoiceNumber(Long.valueOf((String) data.get("invoicenumber")));

		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		final String orderDateString = (String) data.get("invoicedate");
		Date orderDate;
		try
		{
			orderDate = df.parse(orderDateString);
			target.setInvoiceDate(orderDate);
		}
		catch (final Exception e)
		{
			target.setInvoiceDate(null);
		}

		target.setProductDescription((String) data.get("description"));

		final Object quantity = data.get("qty");
		if (quantity instanceof Integer)
		{
			target.setProductQuantity((Integer) quantity);
		}
		else if (quantity instanceof String)
		{
			target.setProductQuantity(Integer.valueOf((String) quantity));
		}
		else
		{
			target.setProductQuantity(null);
		}

		target.setProductQuantityUOM((String) data.get("uom"));
		target.setReturnReason((String) data.get("returnReason"));
		target.setStockReturned((Boolean) data.get("returned"));

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
