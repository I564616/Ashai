package com.sabmiller.core.ordercreate.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.SalesOrderReqPartner;



public class SalesOrderCreateReqPartnerPopulator implements Populator<AbstractOrderModel, SalesOrderReqPartner>
{

	@Override
	public void populate(final AbstractOrderModel cart, final SalesOrderReqPartner target)
	{
		final B2BUnitModel b2bUnit = cart.getUnit();

		if (b2bUnit != null)
		{
			target.setSoldTo(b2bUnit.getSoldto());

			if (cart.getDeliveryAddress() != null && StringUtils.isNotEmpty(cart.getDeliveryAddress().getPartnerNumber()))
			{
				target.setShipTo(cart.getDeliveryAddress().getPartnerNumber());
			}
			else if (b2bUnit.getDefaultShipTo() != null)
			{
				target.setShipTo(b2bUnit.getDefaultShipTo().getPartnerNumber());
			}

			if (cart.getDeliveryShippingCarrier() != null)
			{
				target.setCarrier(cart.getDeliveryShippingCarrier().getCarrierCode());
			}
			else if (b2bUnit.getDefaultCarrier() != null)
			{
				target.setCarrier(b2bUnit.getDefaultCarrier().getCarrierCode());
			}

			target.setUnloadingPoint(
					b2bUnit.getDefaultUnloadingPoint() != null ? b2bUnit.getDefaultUnloadingPoint().getCode() : null);
		}
	}
}
