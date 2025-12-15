package com.apb.integration.order.service;

import com.apb.integration.data.ApbOrderResponseData;

import de.hybris.platform.core.model.order.OrderModel;



/**
 * The Interface AsahiOrderIntegrationService.
 */
@FunctionalInterface
public interface AsahiOrderIntegrationService {

	/**
	 * Send order.
	 *
	 * @param order the order
	 * @return the apb order response data
	 */
	ApbOrderResponseData sendOrder(OrderModel order);
}
