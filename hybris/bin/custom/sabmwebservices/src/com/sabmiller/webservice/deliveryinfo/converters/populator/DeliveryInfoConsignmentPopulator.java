/**
 *
 */
package com.sabmiller.webservice.deliveryinfo.converters.populator;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.webservice.deliveryinfo.Delivery;
import com.sabmiller.webservice.deliveryinfo.Delivery.Item;
import com.sabmiller.webservice.importer.DataImportValidationException;



/**
 * Populates the {@link ConsignmentData} with the data received from the Delivery Info service. The {@link Delivery}
 * object is the one received from SAP. The populator will also merge any duplicate line items that we may receive from
 * SAP. The status received from the Delivery Info webservice are PROCESSING and CANCELLED
 *
 * @author joshua.a.antony
 */
public class DeliveryInfoConsignmentPopulator implements Populator<Delivery, ConsignmentData>
{

	private static final Logger LOG = LoggerFactory.getLogger(DeliveryInfoConsignmentPopulator.class.getName());

	private static final String ONE = "01";
	private static final String TWO = "02";
	private static final String THREE = "03";
	private static final String YSOR = "YSOR";
	private static final String YSR1 = "YSR1";
	private static final String YSFR = "YSFR";
	private static final String YSFO = "YSFO";

	private static final String[] VALID_DELIVERY_TYPE =
	{ YSOR, YSFR, YSR1, YSFO };


	@Resource(name = "b2bOrderFacade")
	private SABMOrderFacade orderFacade;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Delivery source, final ConsignmentData target) throws ConversionException
	{
		final String deliveryActionCode = source.getDeliveryActionCode();
		final String deliveryNumber = source.getDeliveryNumber();
		final ConsignmentStatus consignmentStatus = determineConsignmentStatus(deliveryActionCode, source.getDeliveryType());
		//final XMLGregorianCalendar requestedDeliveryDate = source.getRequestedDeliveryDate().getValue();

		final List<ConsignmentEntryData> consignments = new ArrayList<ConsignmentEntryData>();
		final List<Item> items = source.getItem();
		for (final Item eachItem : ListUtils.emptyIfNull(items))
		{
			final ConsignmentEntryData consignmentEntryData = new ConsignmentEntryData();
			consignmentEntryData.setLineNumber(eachItem.getSalesOrderItemNumber());
			consignmentEntryData.setDeliveryItemNumber(eachItem.getDeliveryItemNumber());

			//consignmentEntryData.setQuantity(eachItem.get); => We do not get quantity from the service
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

			//	LOG.debug("Consignment Entry Data : " + ReflectionToStringBuilder.toString(consignmentEntryData));
		}

		//Finally set the values in the target object
		target.setCode(deliveryNumber);
		target.setTrackingID(deliveryNumber);
		target.setEntries(consignments);
		target.setStatus(consignmentStatus);
		target.setDeliveryActionCode(deliveryActionCode);

		validate(target);

		//LOG.debug("Consignment Data is " + ReflectionToStringBuilder.toString(target));
	}

	public ConsignmentStatus determineConsignmentStatus(final String deliveryActionCode, final String deliveryType)
	{
		if (Arrays.asList(VALID_DELIVERY_TYPE).contains(deliveryType))
		{
			if (ONE.equals(deliveryActionCode) || TWO.equals(deliveryActionCode))
			{
				return ConsignmentStatus.PROCESSING;
			}
			if (THREE.equals(deliveryActionCode))
			{
				return ConsignmentStatus.CREATED;
			}
		}
		LOG.error(
				"In determineConsignmentStatus() - Consignment status not found, returning null. deliveryActionCode = {}  , deliveryType = {}",
				deliveryActionCode, deliveryType);
		return null;
	}

	/**
	 * Validate for anything that would prevent delivery creation downstream.
	 */
	private void validate(final ConsignmentData consignmentData)
	{
		/**
		 * Validate the sales order number only if it exist in the request, as there are some situations (example :
		 * delivery cancellation) where the sales order number is not sent
		 */
		final String salesOrderNumber = consignmentData.getSalesOrderNumber();
		if (!StringUtils.isBlank(salesOrderNumber) && !orderFacade.orderExist(salesOrderNumber))
		{
			throw new DataImportValidationException("Sales Order " + salesOrderNumber
					+ " does not exist in Hybris. Delivery cannot be created without a valid sales order number!");
		}
	}
}
