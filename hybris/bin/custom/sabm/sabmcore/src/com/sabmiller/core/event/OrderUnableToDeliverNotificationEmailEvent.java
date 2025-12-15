/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.io.Serial;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderUnableToDeliverNotificationEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private String orderCode;


	/**
	 * @return the orderCode
	 */
	public String getOrderCode()
	{
		return orderCode;
	}

	/**
	 * @param orderCode
	 *           the orderCode to set
	 */
	public void setOrderCode(final String orderCode)
	{
		this.orderCode = orderCode;
	}



}
