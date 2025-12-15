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
public class OrderDeliveredNotificationEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private AbstractOrderModel order;

	private String deliveryAddress;

	private String signature;


	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress()
	{
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress
	 *           the deliveryAddress to set
	 */
	public void setDeliveryAddress(final String deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp()
	{
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *           the timeStamp to set
	 */
	public void setTimeStamp(final String timeStamp)
	{
		this.timeStamp = timeStamp;
	}
	private String timeStamp;

	public AbstractOrderModel getOrder() {
		return order;
	}

	public void setOrder(final AbstractOrderModel order) {
		this.order = order;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(final String signature) {
		this.signature = signature;
	}
}
