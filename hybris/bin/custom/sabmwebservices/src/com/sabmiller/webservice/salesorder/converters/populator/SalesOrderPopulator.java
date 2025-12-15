/**
 *
 */
package com.sabmiller.webservice.salesorder.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.product.data.UomData;
import com.sabmiller.webservice.importer.DataImportValidationException;
import com.sabmiller.webservice.product.util.SapHybrisUnitOfMeasureMapper;
import com.sabmiller.webservice.salesorder.SalesOrder;
import com.sabmiller.webservice.salesorder.SalesOrder.Item;



/**
 * @author joshua.a.antony
 *
 */
public class SalesOrderPopulator implements Populator<SalesOrder, OrderData>
{

	private static final Logger LOG = LoggerFactory.getLogger(SalesOrderPopulator.class.getName());

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bUnitFacade;

	@Resource(name = "sapHybrisUnitOfMeasureMapper")
	private SapHybrisUnitOfMeasureMapper mapper;

	private static final String YSOR = "YSOR";
	private static final String YSTF = "YSTF";
	private static final String YR44 = "YR44";
	private static final String YR12 = "YR12";
	private static final String YSR1 = "YSR1";
	private static final String YR08 = "YR08";
	private static final String YG45 = "YG45";
	private static final String YR32 = "YR32";

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SalesOrder source, final OrderData target) throws ConversionException
	{
		validate(source);

		final String salesOrderNumber = source.getSalesOrderNumber();
		final String processingTypeCode = source.getProcessingTypeCode();
		final String soldTo = source.getBuyerParty() != null ? source.getBuyerParty().getSoldTo() : null;
		final String poNumber = source.getBuyerDocument() != null ? source.getBuyerDocument().getPONumber().getValue() : null;
		final String dataOriginCategoryCode = source.getDataOriginCategoryCode() != null
				? source.getDataOriginCategoryCode().getValue() : null;

		final List<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		for (final Item eachItem : ListUtils.emptyIfNull(source.getItem()))
		{
			final OrderEntryData orderEntryData = new OrderEntryData();
			orderEntryData.setSapLineNumber(eachItem.getItemNumber());
			orderEntryData.setProduct(createProduct(eachItem.getProduct()));
			orderEntryData.setQuantity(getQuantity(eachItem.getScheduleLine()));
			orderEntryData.setUnit(createUnit(eachItem.getScheduleLine(), null));
			orderEntryData
					.setIsFreeGood(YSTF.equals(eachItem.getFreeGoodsIdentifier()) || YG45.equals(eachItem.getFreeGoodsIdentifier()));
			orderEntryData.setRejected(isRejected(eachItem, processingTypeCode));

			//SAP can send same products across multiple lines. This needs to be merged into a single line item in Hybris
			final OrderEntryData existingEntry = find(orderEntries, orderEntryData);
			final boolean entryExist = (existingEntry != null);
			if (entryExist)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Duplicate product {} in the entry , merging the quantities", orderEntryData.getProduct());
				}
				existingEntry.setQuantity(existingEntry.getQuantity() + orderEntryData.getQuantity());
			}
			else
			{
				orderEntries.add(orderEntryData);
			}
		}

		//Setting values in target
		target.setRequestedDeliveryDate(deriveRequestedDeliveryDate(source));
		target.setEntries(orderEntries);
		target.setPurchaseOrderNumber(poNumber);
		target.setSapSalesOrderNumber(salesOrderNumber);
		target.setSoldTo(soldTo);
		target.setProcessingTypeCode(processingTypeCode);
		target.setDataOriginCategoryCode(dataOriginCategoryCode);

		if (source.getCreationDate() != null)
		{
			target.setCreated(SabmDateUtils.toDate(source.getCreationDate()));
		}


		LOG.debug("After Populating. Order Data : " + target);
	}

	private boolean isRejected(final Item item, final String processingTypeCode)
	{
		return item.getSalesTerms() != null && !StringUtils.isBlank(item.getSalesTerms().getRejectionReason())
				|| (YSOR.equals(processingTypeCode) && (YR44.equals(item.getFreeGoodsIdentifier())
						|| YR12.equals(item.getFreeGoodsIdentifier()) || YR32.equals(item.getFreeGoodsIdentifier())))
				|| (YSR1.equals(processingTypeCode)
						&& (YR08.equals(item.getFreeGoodsIdentifier()) || YR32.equals(item.getFreeGoodsIdentifier())));
	}

	private OrderEntryData find(final List<OrderEntryData> entries, final OrderEntryData tocheck)
	{
		for (final OrderEntryData eachEntry : entries)
		{
			if (eachEntry.getProduct().getCode().equals(tocheck.getProduct().getCode())
					&& eachEntry.isIsFreeGood() == tocheck.isIsFreeGood() && eachEntry.isRejected() == tocheck.isRejected())
			{
				return eachEntry;
			}
		}
		return null;
	}

	private Date deriveRequestedDeliveryDate(final SalesOrder salesOrder)
	{
		try
		{
			XMLGregorianCalendar cal = null;
			if (salesOrder.getDateTerms() != null && (cal = salesOrder.getDateTerms().getRequestedDeliveryDate()) != null)
			{
				return SabmDateUtils.toDate(cal);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error occured calculating the requested delivery date. Returning null ", e);
		}
		LOG.error("Requested delivery date is empty in the source. Returning null!");
		return null;
	}

	private Long getQuantity(final List<SalesOrder.Item.ScheduleLine> scheduleLines)
	{
		if (CollectionUtils.isNotEmpty(scheduleLines) && scheduleLines.get(0).getConfirmedMaterialQuantity() != null)
		{
			return SabmStringUtils.doubleStringToLongWrapper(scheduleLines.get(0).getConfirmedMaterialQuantity().getValue());
		}

		LOG.error("Error reading quantity from sales order, schedule line is null");
		return 0L;
	}

	private ProductData createProduct(final SalesOrder.Item.Product material)
	{
		final ProductData productData = new ProductData();
		if (material != null && material.getMaterialNumber() != null)
		{
			productData.setCode(material.getMaterialNumber().getValue());
		}
		else
		{
			LOG.error("Material is null, not able to convert Sales order properly");
		}
		return productData;
	}

	private UomData createUnit(final List<SalesOrder.Item.ScheduleLine> scheduleLines, final String name)
	{
		final UomData uomData = new UomData();
		if (CollectionUtils.isNotEmpty(scheduleLines) && scheduleLines.get(0).getConfirmedMaterialQuantity() != null)
		{
			uomData.setCode(mapper.getHybrisUomCode(scheduleLines.get(0).getConfirmedMaterialQuantity().getUnitCode()));
			uomData.setName(name);
		}
		return uomData;
	}

	private void validate(final SalesOrder salesOrder)
	{
		final String soldTo = salesOrder.getBuyerParty().getSoldTo();
		final boolean soldToExist = b2bUnitFacade.b2bUnitExist(soldTo);
		if (!soldToExist)
		{
			throw new DataImportValidationException("Invalid Sold To " + soldTo + ". This customer does not exist in Hybris");
		}
	}

}
