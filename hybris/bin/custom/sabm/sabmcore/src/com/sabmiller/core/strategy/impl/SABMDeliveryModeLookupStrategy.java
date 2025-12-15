/**
 *
 */
package com.sabmiller.core.strategy.impl;

import de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryModeLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Strategy to getSelectableDeliveryModesForOrder for abstractOrderModel
 *
 * @author yaopeng
 *
 */
public class SABMDeliveryModeLookupStrategy extends DefaultDeliveryModeLookupStrategy
{

	/*
	 * Strategy to getSelectableDeliveryModesForOrder for abstractOrderModel.
	 *
	 * @see de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryModeLookupStrategy#
	 * getSelectableDeliveryModesForOrder(de.hybris.platform.core.model.order.AbstractOrderModel)
	 */
	@Override
	public List<DeliveryModeModel> getSelectableDeliveryModesForOrder(final AbstractOrderModel abstractOrderModel)
	{
		final List<DeliveryModeModel> deliveryModes = new ArrayList<DeliveryModeModel>();
		if (abstractOrderModel != null)
		{
			//Rewrite the Dao of findDeliveryModes
			deliveryModes.addAll(getCountryZoneDeliveryModeDao().findDeliveryModes(abstractOrderModel));
			return deliveryModes;
		}

		return deliveryModes;
	}



}
