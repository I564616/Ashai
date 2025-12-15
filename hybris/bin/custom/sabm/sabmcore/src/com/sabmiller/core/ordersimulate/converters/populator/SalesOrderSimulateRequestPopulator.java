package com.sabmiller.core.ordersimulate.converters.populator;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest.DealCondition;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest.SalesOrderReqHeader;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest.SalesOrderReqPartner;



/**
 *
 */
public class SalesOrderSimulateRequestPopulator implements Populator<AbstractOrderModel, SalesOrderSimulateRequest>
{
	private Converter<AbstractOrderModel, SalesOrderReqHeader> salesOrderSimulateReqHeaderConverter;
	private Converter<AbstractOrderModel, SalesOrderReqPartner> salesOrderSimulateReqPartnerConverter;
	private Converter<AbstractOrderEntryModel, SalesOrderSimulateRequest.SalesOrderReqItem> salesOrderSimulateReqItemConverter;



	@SuppressWarnings("boxing")
	@Override
	public void populate(final AbstractOrderModel cart, final SalesOrderSimulateRequest target)
	{
		target.setSalesOrderReqHeader(salesOrderSimulateReqHeaderConverter.convert(cart));
		target.setSalesOrderReqPartner(salesOrderSimulateReqPartnerConverter.convert(cart));
		target.getSalesOrderReqItem().addAll(Converters.convertAll(cart.getEntries(), salesOrderSimulateReqItemConverter));

		if (!CollectionUtils.isEmpty(cart.getComplexDealConditions()))
		{
			target.getDealCondition().addAll(transform(cart.getComplexDealConditions()));
		}
	}

	private List<DealCondition> transform(final List<CartDealConditionModel> dealConditions)
	{
		final List<DealCondition> dcns = new ArrayList<DealCondition>();
		for (final CartDealConditionModel eachModel : org.apache.commons.collections4.CollectionUtils.emptyIfNull(dealConditions))
		{
			if (eachModel != null && eachModel.getDeal() != null)
			{
				final DealCondition dcd = new DealCondition();
				if (DealTypeEnum.COMPLEX.equals(eachModel.getDeal().getDealType())
						&& !DealConditionStatus.REJECTED.equals(eachModel.getStatus()))
				{
					dcd.setDealConditionNumber(eachModel.getDeal().getCode());
					dcns.add(dcd);
				}
			}
		}
		return dcns;
	}



	public Converter<AbstractOrderModel, SalesOrderReqHeader> getSalesOrderSimulateReqHeaderConverter()
	{
		return salesOrderSimulateReqHeaderConverter;
	}

	public Converter<AbstractOrderModel, SalesOrderReqPartner> getSalesOrderSimulateReqPartnerConverter()
	{
		return salesOrderSimulateReqPartnerConverter;
	}


	public Converter<AbstractOrderEntryModel, SalesOrderSimulateRequest.SalesOrderReqItem> getSalesOrderSimulateReqItemConverter()
	{
		return salesOrderSimulateReqItemConverter;
	}


	public void setSalesOrderSimulateReqHeaderConverter(
			final Converter<AbstractOrderModel, SalesOrderReqHeader> salesOrderSimulateReqHeaderConverter)
	{
		this.salesOrderSimulateReqHeaderConverter = salesOrderSimulateReqHeaderConverter;
	}


	public void setSalesOrderSimulateReqPartnerConverter(
			final Converter<AbstractOrderModel, SalesOrderReqPartner> salesOrderSimulateReqPartnerConverter)
	{
		this.salesOrderSimulateReqPartnerConverter = salesOrderSimulateReqPartnerConverter;
	}


	public void setSalesOrderSimulateReqItemConverter(
			final Converter<AbstractOrderEntryModel, SalesOrderSimulateRequest.SalesOrderReqItem> salesOrderSimulateReqItemConverter)
	{
		this.salesOrderSimulateReqItemConverter = salesOrderSimulateReqItemConverter;
	}
}