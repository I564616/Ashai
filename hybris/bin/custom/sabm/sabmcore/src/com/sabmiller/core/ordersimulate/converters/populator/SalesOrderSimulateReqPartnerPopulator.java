/**
 *
 */
package com.sabmiller.core.ordersimulate.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest.SalesOrderReqPartner;



/**
 *
 */
public class SalesOrderSimulateReqPartnerPopulator implements Populator<AbstractOrderModel, SalesOrderReqPartner>
{

	@Override
	public void populate(final AbstractOrderModel cart, final SalesOrderReqPartner target)
	{
		final B2BUnitModel b2bUnitData = cart.getUnit();

		if (b2bUnitData != null)
		{
			target.setSoldTo(b2bUnitData.getSoldto());

			if (cart.getDeliveryAddress() != null && StringUtils.isNotEmpty(cart.getDeliveryAddress().getPartnerNumber()))
			{
				target.setShipTo(cart.getDeliveryAddress().getPartnerNumber());
			}
			else if (b2bUnitData.getDefaultShipTo() != null)
			{
				target.setShipTo(b2bUnitData.getDefaultShipTo().getPartnerNumber());
			}

			if (cart.getDeliveryShippingCarrier() != null)
			{
				target.setCarrier(cart.getDeliveryShippingCarrier().getCarrierCode());
			}
			else if (b2bUnitData.getDefaultCarrier() != null)
			{
				target.setCarrier(b2bUnitData.getDefaultCarrier().getCarrierCode());
			}

			target.setUnloadingPoint(
					b2bUnitData.getDefaultUnloadingPoint() != null ? b2bUnitData.getDefaultUnloadingPoint().getCode() : null);
		}
	}
}
