/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;

import com.asahi.facades.notifications.data.AsahiNotificationData;
import com.asahi.facades.notifications.data.AsahiNotificationPreferenceData;
import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.AsahiNotificationPrefModel;


/**
 *
 */
public class AsahiNotificationPopulator implements Populator<AsahiNotificationModel, AsahiNotificationData>
{

	@Override
	public void populate(final AsahiNotificationModel source, final AsahiNotificationData target) throws ConversionException
	{
		final Map<String, AsahiNotificationPreferenceData> notifications = new HashMap<>();

		if (null != source)
		{

			for (final AsahiNotificationPrefModel pref : source.getAsahiNotificationPreferences())
			{
				notifications.put(pref.getNotificationType().getCode(), populatePreference(pref));
			}
			target.setNotificationPreferences(notifications);
		}
		else
		{
			createDefaultNotificationData(target);
		}
	}

	/**
	 * @param pref
	 * @return
	 */
	private AsahiNotificationPreferenceData populatePreference(final AsahiNotificationPrefModel pref)
	{
		final AsahiNotificationPreferenceData preferenceData = new AsahiNotificationPreferenceData();
		preferenceData.setNotificationType(pref.getNotificationType().toString());
		preferenceData.setEmailEnabled(pref.getEmailEnabled());
		return preferenceData;
	}

	/**
	 * @param target
	 */
	private void createDefaultNotificationData(final AsahiNotificationData target)
	{
		final Map<String, AsahiNotificationPreferenceData> defNotifications = new HashMap<>();

		defNotifications.put(NotificationType.PUBLIC_HOLIDAY_NO_DELIVERY.getCode(),
				createDefaultNotificationPreferenceData(NotificationType.PUBLIC_HOLIDAY_NO_DELIVERY));
		defNotifications.put(NotificationType.PUBLIC_HOLIDAY_ALT_CALL_DELIVERY.getCode(),
				createDefaultNotificationPreferenceData(NotificationType.PUBLIC_HOLIDAY_ALT_CALL_DELIVERY));
		defNotifications.put(NotificationType.PUBLIC_HOLIDAY_ALT_DELIVERY.getCode(),
				createDefaultNotificationPreferenceData(NotificationType.PUBLIC_HOLIDAY_ALT_DELIVERY));
		defNotifications.put(NotificationType.CONTACT_US.getCode(),
				createDefaultNotificationPreferenceData(NotificationType.CONTACT_US));
		defNotifications.put(NotificationType.ORDER_CONFIRMATION.getCode(),
				createDefaultNotificationPreferenceData(NotificationType.ORDER_CONFIRMATION));
		defNotifications.put(NotificationType.PAYMENT_CONFIRMATION.getCode(),
				createDefaultNotificationPreferenceData(NotificationType.PAYMENT_CONFIRMATION));
		target.setNotificationPreferences(defNotifications);
	}

	/**
	 * @param notificationType
	 */
	private AsahiNotificationPreferenceData createDefaultNotificationPreferenceData(final NotificationType notificationType)
	{
		final AsahiNotificationPreferenceData preferenceData = new AsahiNotificationPreferenceData();
		preferenceData.setNotificationType(notificationType.toString());
		preferenceData.setEmailEnabled(Boolean.TRUE);
		return preferenceData;

	}

}
