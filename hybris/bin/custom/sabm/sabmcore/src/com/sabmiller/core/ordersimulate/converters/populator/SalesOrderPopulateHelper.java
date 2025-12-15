/**
 *
 */
package com.sabmiller.core.ordersimulate.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.Config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.ShippingCarrierModel;


/**
 *
 */
public class SalesOrderPopulateHelper
{

	private static final String SHIPPING_CONDITION_TYPE_CUST_ARRANGED_B1 = "B1";


	/*
	 * This method implements the logic to decide shipping condition if type B1 customer selecting CUB arranged or AO/A5
	 * customer selecting customer arranged. if B1 customer selects customer arranged delivery or if A0/A5 customer
	 * selects CUB arranged, just pass the customer shipping condition
	 */
	public String getShippingCondition(final B2BUnitModel b2bUnit, final AbstractOrderModel cart)
	{
		String shippingCondition = null;
		if (b2bUnit.getSalesOrgData() != null && StringUtils.isNotEmpty(b2bUnit.getSalesOrgData().getShippingCondition()))
		{
			shippingCondition = b2bUnit.getSalesOrgData().getShippingCondition();
			if (StringUtils.equalsIgnoreCase(shippingCondition, SHIPPING_CONDITION_TYPE_CUST_ARRANGED_B1))
			{
				if (StringUtils.equalsIgnoreCase(Config.getString("sabmstorefront.cart.delivery.cubArranged", ""),
						cart.getDeliveryMode().getCode()))
				{
					shippingCondition = getCubShippingConditionType(b2bUnit);
				}
			}
			else
			{
				if (cart.getDeliveryMode() != null
						&& StringUtils.equalsIgnoreCase(Config.getString("sabmstorefront.cart.delivery.customerArranged", ""),
								cart.getDeliveryMode().getCode())
						&& (cart.getDeliveryShippingCarrier() != null
								&& cart.getDeliveryShippingCarrier().getShippingCondition() != null))
				{
					shippingCondition = cart.getDeliveryShippingCarrier().getShippingCondition();
				}
			}
		}

		return shippingCondition;

	}


	/**
	 * B1 type customer selecting cub arranged delivery, loop through all customer carriers and get the first one whose
	 * shipping condition is not equals to B1
	 *
	 */
	private String getCubShippingConditionType(final B2BUnitModel unit)
	{
		final List<ShippingCarrierModel> shippingCarriers = unit.getShippingCarriers();
		for (final ShippingCarrierModel carrier : shippingCarriers)
		{
			final String shippingCodition = carrier.getShippingCondition();
			if (!StringUtils.equalsIgnoreCase(shippingCodition, SHIPPING_CONDITION_TYPE_CUST_ARRANGED_B1))
			{
				return shippingCodition;
			}
		}
		return null;
	}
}
