package com.apb.core.order.history.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.OrderHistoryService;

public interface ApbOrderHistoryService extends OrderHistoryService
{
	/**
	 * This method is used to log changes/updates in order model.
	 * 
	 * @param order
	 * @param status
	 * @param description
	 */
	void createOrderHistoryEntry(OrderModel order, OrderModel snapshot, String description);
}
