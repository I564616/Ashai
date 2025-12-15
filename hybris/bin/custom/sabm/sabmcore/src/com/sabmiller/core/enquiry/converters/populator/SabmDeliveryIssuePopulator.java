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
import com.sabmiller.facades.businessenquiry.data.SabmDeliveryIssueData;



/**
 * The Class SabmDeliveryIssuePopulator.
 */
public class SabmDeliveryIssuePopulator implements Populator<AbstractBusinessEnquiryData, SabmDeliveryIssueData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractBusinessEnquiryData source, final SabmDeliveryIssueData target) throws ConversionException
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

			//	specific to delivery enquiry
			final Map<String, Object> data = source.getData();

			target.setInvoiceNumber(Long.valueOf((String) data.get("invnumber")));

			final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			final String orderDateString = (String) data.get("invdate");
			Date invoiceDate;
			try
			{
				invoiceDate = df.parse(orderDateString);
				target.setInvoiceDate(invoiceDate);
			}
			catch (final Exception e)
			{
				target.setInvoiceDate(null);
			}
			if ((String) data.get("product") == null)
			{
				target.setProduct(StringUtils.EMPTY);
			}
			else
			{
				target.setProduct((String) data.get("product"));
			}
			final Object quantity = data.get("qty");
			if (quantity instanceof Integer)
			{
				target.setQuantity((Integer) quantity);
			}
			else if (quantity instanceof String)
			{
				target.setQuantity(Integer.valueOf((String) quantity));
			}
			else
			{
				target.setQuantity(null);
			}

			if ((String) data.get("uom") == null)
			{
				target.setQuantityUOM(StringUtils.EMPTY);
			}
			else
			{
				target.setQuantityUOM((String) data.get("uom"));
			}

			//final Boolean isDamageStock = (Boolean) data.get("damage_stock");

			if ((Boolean) data.get("damage_stock") != null)
			{
				target.setDamageStock((Boolean) data.get("damage_stock"));
			}
			else
			{
				target.setDamageStock(false);
			}

			//final Boolean isDamagePremise = (Boolean) data.get("damage_premise");
			if ((Boolean) data.get("damage_premise") != null)
			{
				target.setDamagePremise((Boolean) data.get("damage_premise"));
			}
			else
			{
				target.setDamagePremise(false);
			}

			//final Boolean isDriveComplaint = (Boolean) data.get("driver_complaint");
			if ((Boolean) data.get("driver_complaint") != null)
			{
				target.setDriverComplaint((Boolean) data.get("driver_complaint"));
			}
			else
			{
				target.setDriverComplaint(false);
			}

			//final Boolean isNotCollected = (Boolean) data.get("not_collected");
			if ((Boolean) data.get("not_collected") != null)
			{
				target.setKegsNotCollected((Boolean) data.get("not_collected"));
			}
			else
			{
				target.setKegsNotCollected(false);
			}

			//final Boolean isNotComplete = (Boolean) data.get("not_complete");
			if ((Boolean) data.get("not_complete") != null)
			{
				target.setNotAllItemsDelivered((Boolean) data.get("not_complete"));
			}
			else
			{
				target.setNotAllItemsDelivered(false);
			}

			//final Boolean isPickingError = (Boolean) data.get("picking_error");
			if ((Boolean) data.get("picking_error") != null)
			{
				target.setPickingError((Boolean) data.get("picking_error"));
			}
			else
			{
				target.setPickingError(false);
			}

			//final Boolean isOther = (Boolean) data.get("other");
			if ((Boolean) data.get("other") != null)
			{
				target.setOther((Boolean) data.get("other"));
			}
			else
			{
				target.setOther(false);
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
