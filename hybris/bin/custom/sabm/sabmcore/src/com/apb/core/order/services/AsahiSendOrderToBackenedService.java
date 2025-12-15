package com.apb.core.order.services;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * Interface for setup and sending of orders to backend system.
 */
public interface AsahiSendOrderToBackenedService
{

	/**
	 * This method will setup and send order to Backened system.
	 * 
	 * @param orderModel
	 */
	void sendOrderToBackendSystem(OrderModel orderModel);

}
