package com.sabmiller.core.ordercreate.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import org.apache.commons.lang3.BooleanUtils;


public class SalesOrderCreateReqItemPopulator
		implements Populator<AbstractOrderEntryModel, SalesOrderCreateRequest.SalesOrderReqItem>
{

	@Override
	public void populate(final AbstractOrderEntryModel orderEntry,
			final SalesOrderCreateRequest.SalesOrderReqItem salesOrderReqItem)
	{


		if (orderEntry != null && BooleanUtils.isNotTrue(orderEntry.getIsFreeGood()))
		{
			salesOrderReqItem.setMaterialNumber(orderEntry.getProduct().getCode());
			salesOrderReqItem.setLineItemNumber(orderEntry.getSapLineNumber());
			salesOrderReqItem.setQuantity(String.valueOf(orderEntry.getQuantity()));
			if (orderEntry.getUnit() != null)
			{
				salesOrderReqItem.setUnitOfMeasure(orderEntry.getUnit().getCode());
			}
		}
	}
}