/**
 *
 */
package com.sabmiller.webservice.dispatchinfo.converters.populator;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.webservice.dispatchinfo.Delivery;
import com.sabmiller.webservice.dispatchinfo.Delivery.Item;
import com.sabmiller.webservice.importer.DataImportValidationException;



/**
 * @author joshua.a.antony
 *
 */
public class DispatchInfoConsignmentPopulator implements Populator<Delivery, ConsignmentData>
{
	private static final Logger LOG = LoggerFactory.getLogger(DispatchInfoConsignmentPopulator.class.getName());


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Delivery delivery, final ConsignmentData target) throws ConversionException
	{
		final String deliveryActionCode = delivery.getDeliveryActionCode();
		final String deliveryNumber = delivery.getDeliveryNumber();
		final ConsignmentStatus consignmentStatus = determineConsignmentStatus(delivery);
		//final XMLGregorianCalendar requestedDeliveryDate = delivery.getRequestedDeliveryDate().getValue();

		final List<ConsignmentEntryData> consignments = new ArrayList<ConsignmentEntryData>();
		final List<Item> items = delivery.getItem();
		for (final Item eachItem : ListUtils.emptyIfNull(items))
		{
			final ConsignmentEntryData consignmentEntryData = new ConsignmentEntryData();
			consignmentEntryData.setDeliveryItemNumber(eachItem.getDeliveryItemNumber());
			consignmentEntryData.setLineNumber(eachItem.getSalesOrderItemNumber());

			if (eachItem.getConfirmedQuantity() != null && eachItem.getConfirmedQuantity().getValue() != null)
			{
				consignmentEntryData.setShippedQuantity(eachItem.getConfirmedQuantity().getValue().longValue());
			}
			else
			{
				LOG.error("SAP confirmed quantity is empty for delivery number [{}]", deliveryNumber);
				consignmentEntryData.setShippedQuantity(0L);
			}
			if (eachItem.getSalesOrderReference() != null)
			{
				consignmentEntryData.setSalesOrderNumber(eachItem.getSalesOrderReference().getSalesOrderNumber());
			}
			if (eachItem.getProduct() != null && eachItem.getProduct().getMaterialNumber() != null)
			{
				consignmentEntryData.setProductCode(eachItem.getProduct().getMaterialNumber().getValue());
			}
			else
			{
				LOG.error("Unable to read product code for consignment - delivery number [{}]", deliveryNumber);
			}
			if (eachItem.getSalesOrderReference() != null)
			{
				target.setSalesOrderNumber(eachItem.getSalesOrderReference().getSalesOrderNumber());
			}

			consignments.add(consignmentEntryData);
		}

		//Finally set the values in the target object
		target.setCode(deliveryNumber);
		target.setTrackingID(deliveryNumber);
		target.setEntries(consignments);
		target.setStatus(consignmentStatus);
		target.setDeliveryActionCode(deliveryActionCode);

		//	LOG.debug("After populating : " + ReflectionToStringBuilder.toString(target));
	}


	public ConsignmentStatus determineConsignmentStatus(final Delivery delivery)
	{
		if (delivery.getItem() != null && !delivery.getItem().isEmpty())
		{
			final Boolean completionIndicator = delivery.getItem().get(0).isCompletedIndicator();
			return Boolean.TRUE.equals(completionIndicator) ? ConsignmentStatus.SHIPPED : ConsignmentStatus.PROCESSING;
		}
		throw new DataImportValidationException(
				"Unable to determine the consignment status. The completion indicator flag is incorrect in the request.");
	}

}
