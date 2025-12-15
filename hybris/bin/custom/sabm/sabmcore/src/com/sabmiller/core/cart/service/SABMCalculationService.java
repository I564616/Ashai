package com.sabmiller.core.cart.service;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CalculationService;


/**
 * The Interface SABMCalculationService.
 */
public interface SABMCalculationService extends CalculationService
{

	boolean updateOrderModel(final AbstractOrderModel order, final boolean isFreightIncluded, boolean isDeliveryChargeApplicable);

	void updatePriceForProduct(AbstractOrderEntryModel entry, CurrencyModel curr, long quantity);
}
