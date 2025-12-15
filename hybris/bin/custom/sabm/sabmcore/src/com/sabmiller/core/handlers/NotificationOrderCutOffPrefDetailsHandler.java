/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import de.hybris.platform.notificationservices.enums.NotificationType;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;


public class NotificationOrderCutOffPrefDetailsHandler implements DynamicAttributeHandler<String, SABMNotificationModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#get(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel)
	 */
	@Override
	public String get(final SABMNotificationModel notification)
	{
		final StringBuffer prefString = new StringBuffer("");
		if (notification != null && notification.getNotificationPreferences()!=null)
		{
		for (final SABMNotificationPrefModel pref : notification.getNotificationPreferences())
		{
				if (pref != null && NotificationType.ORDER.equals(pref.getNotificationType()))
			{
				if (pref.getEmailEnabled())
				{
						prefString.append(pref.getEmailOptedTime()).append(" ").append(pref.getEmailOptedTimeUnit());
				}
			}
		}
		}
		return prefString.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#set(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel, java.lang.Object)
	 */
	@Override
	public void set(final SABMNotificationModel notification, final String notificationPrefString)
	{
		throw new UnsupportedOperationException(
				"Set of dynamic attribute 'notificationPrefString' of SABMNotification is disabled!");

	}
}
