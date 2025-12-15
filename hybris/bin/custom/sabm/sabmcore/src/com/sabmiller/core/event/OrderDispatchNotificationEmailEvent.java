/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.order.OrderModel;

import java.io.Serial;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderDispatchNotificationEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private B2BUnitModel b2bUnit;
	private OrderModel order;

	public OrderDispatchNotificationEmailEvent()
	{
		super();
	}

	/**
	 * @return the b2bUnit
	 */
	public B2BUnitModel getB2bUnit()
	{
		return b2bUnit;
	}

	/**
	 * @param b2bUnit
	 *           the b2bUnit to set
	 */
	public void setB2bUnit(final B2BUnitModel b2bUnit)
	{
		this.b2bUnit = b2bUnit;
	}

	/**
	 * @return the order
	 */
	public OrderModel getOrder()
	{
		return order;
	}

	/**
	 * @param order
	 *           the order to set
	 */
	public void setOrder(final OrderModel order)
	{
		this.order = order;
	}

}
