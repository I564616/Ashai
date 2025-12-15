package com.sabmiller.core.ordersimulate.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest;
import org.apache.commons.lang3.BooleanUtils;


public class SalesOrderSimulateReqItemPopulator
		implements Populator<AbstractOrderEntryModel, SalesOrderSimulateRequest.SalesOrderReqItem>
{

	@Override
	public void populate(final AbstractOrderEntryModel orderEntry,
			final SalesOrderSimulateRequest.SalesOrderReqItem salesOrderReqItem)
	{


		if (orderEntry != null && BooleanUtils.isNotTrue(orderEntry.getIsFreeGood()) && orderEntry.getProduct() != null)
		{
			salesOrderReqItem.setMaterialNumber(orderEntry.getProduct().getCode());
			salesOrderReqItem.setQuantity(String.valueOf(orderEntry.getQuantity()));
			if (orderEntry.getUnit() != null)
			{
				salesOrderReqItem.setUnitOfMeasure(orderEntry.getUnit().getCode());
			}
		}
	}
}