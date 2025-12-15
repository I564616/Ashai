/**
 *
 */
package com.sabmiller.facades.order.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.sabmiller.facades.order.json.OrderHistoryJson;
import com.sabmiller.facades.util.SabmFeatureUtil;


/**
 * The Class OrderHistoryJsonPopulator.
 */
public class OrderHistoryJsonPopulator implements Populator<OrderModel, OrderHistoryJson>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(OrderHistoryJsonPopulator.class);

	/** The date pattern. */
	@Value(value = "${table.date.pattern:dd-MM-yy}")
	private String datePattern;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final OrderModel source, final OrderHistoryJson target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		LOG.debug("Populating OrderHistoryJson from OrderModel: {} with code: {}", source, source.getCode());

		target.setSapOrderNo(source.getSapSalesOrderNumber());

		if (source.getTotalPrice() != null)
		{
			target.setAmount(source.getTotalPrice().toString());
		}

		if (source.getDate() != null)
		{
			target.setDate(DateFormatUtils.format(source.getDate(), datePattern));
			target.setDateStamp(source.getDate().getTime());
		}

		if (source.getRequestedDeliveryDate() != null)
		{
			target.setDeliveryDate(DateFormatUtils.format(source.getRequestedDeliveryDate(), datePattern));
			target.setDeliveryDateStamp(source.getRequestedDeliveryDate().getTime());
		}

		target.setOrderNo(source.getCode());

		target.setStatus(sabmFeatureUtil.displayTrackOrderStatus(source.getStatusDisplay()));

	}

}
