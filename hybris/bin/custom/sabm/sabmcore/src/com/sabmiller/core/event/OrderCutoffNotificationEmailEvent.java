/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import de.hybris.platform.notificationservices.enums.NotificationType;

import java.io.Serial;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderCutoffNotificationEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private String notificationID;
	private NotificationType notificationType;
	private String cutoffDateTime;
	private String deliveryDate;
	private String serverTimeInBaseStoreTZ;

	public OrderCutoffNotificationEmailEvent()
	{
		super();
	}

	/**
	 * @return the notificationID
	 */
	public String getNotificationID()
	{
		return notificationID;
	}

	/**
	 * @param notificationID
	 *           the notificationID to set
	 */
	public void setNotificationID(final String notificationID)
	{
		this.notificationID = notificationID;
	}

	/**
	 * @return the notificationType
	 */
	public NotificationType getNotificationType()
	{
		return notificationType;
	}

	/**
	 * @param notificationType
	 *           the notificationType to set
	 */
	public void setNotificationType(final NotificationType notificationType)
	{
		this.notificationType = notificationType;
	}

	/**
	 * @return the cutoffDateTime
	 */
	public String getCutoffDateTime()
	{
		return cutoffDateTime;
	}

	/**
	 * @param cutoffDateTime
	 *           the cutoffDateTime to set
	 */
	public void setCutoffDateTime(final String cutoffDateTime)
	{
		this.cutoffDateTime = cutoffDateTime;
	}

	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate()
	{
		return deliveryDate;
	}

	/**
	 * @param deliveryDate
	 *           the deliveryDate to set
	 */
	public void setDeliveryDate(final String deliveryDate)
	{
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the serverTimeInBaseStoreTZ
	 */
	public String getServerTimeInBaseStoreTZ()
	{
		return serverTimeInBaseStoreTZ;
	}

	/**
	 * @param serverTimeInBaseStoreTZ
	 *           the serverTimeInBaseStoreTZ to set
	 */
	public void setServerTimeInBaseStoreTZ(final String serverTimeInBaseStoreTZ)
	{
		this.serverTimeInBaseStoreTZ = serverTimeInBaseStoreTZ;
	}

}
