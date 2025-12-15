package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.order.OrderModel;

import java.io.Serial;


/**
 * The Event Class represents Asahi Order Placed Event
 */
public class AsahiOrderPlacedEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private OrderModel orderModel;

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	/**
	 * @return
	 */
	public OrderModel getOrderModel()
	{
		return orderModel;
	}

	/**
	 * @param orderModel
	 */
	public void setOrderModel(final OrderModel orderModel)
	{
		this.orderModel = orderModel;
	}

}
