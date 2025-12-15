/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.io.Serial;

/**
 * @author marc.f.l.bautista
 *
 */
public class OrderNextInQueueDeliveryNotificationEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private AbstractOrderModel order;

	private String startETA;

	private String endETA;

	public String getStartETA() {
		return startETA;
	}

	public void setStartETA(final String startETA) {
		this.startETA = startETA;
	}

	public AbstractOrderModel getOrder() {
		return order;
	}

	public void setOrder(final AbstractOrderModel order) {
		this.order = order;
	}

	public String getEndETA() {
		return endETA;
	}

	public void setEndETA(final String endETA) {
		this.endETA = endETA;
	}
}
