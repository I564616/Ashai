package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.notificationservices.enums.NotificationType;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.facades.notifications.data.NotificationData;
import com.sabmiller.facades.notifications.data.NotificationPreferenceData;


/**
 * Created by raul.b.abatol.jr on 07/06/2017.
 */
public class SabmNotificationPopulator implements Populator<SABMNotificationModel, NotificationData>
{
	private static final String ORDER_DURATION_LIST = "user.notification.order.duration.list";
	private static final String DEAL_DURATION_LIST = "user.notification.deal.duration.list";

	private ConfigurationService configurationService;

	@Override
	public void populate(final SABMNotificationModel sabmNotificationModel, final NotificationData notificationData)
			throws ConversionException
	{
		final Map<String, NotificationPreferenceData> notificationMap = new HashMap<>();

		// check if there notification model is not null, then replace default data with data from model.
		if (sabmNotificationModel != null)
		{
			notificationData.setUserId(sabmNotificationModel.getUser().getUid());
			final String mobile = sabmNotificationModel.getUser().getMobileContactNumber();
			notificationData.setMobileNumber(mobile != null ? mobile : StringUtils.stripToEmpty(mobile));

			for (final SABMNotificationPrefModel notifPref : sabmNotificationModel.getNotificationPreferences())
			{
				notificationMap.put(notifPref.getNotificationType().getCode(), createNotificationPrefData(notifPref));
			}

			if (!notificationMap.containsKey(NotificationType.ORDER.getCode()))
			{
				notificationMap.put(NotificationType.ORDER.getCode(), createNotificationDefPrefData(NotificationType.ORDER));
			}
			if (!notificationMap.containsKey(NotificationType.DELIVERY.getCode()))
			{
				notificationMap.put(NotificationType.DELIVERY.getCode(), createNotificationDefPrefData(NotificationType.DELIVERY));
			}
			if (!notificationMap.containsKey(NotificationType.DEAL.getCode()))
			{
				notificationMap.put(NotificationType.DEAL.getCode(), createNotificationDefPrefData(NotificationType.DEAL));
			}
			if (!notificationMap.containsKey(NotificationType.INTRANSIT.getCode()))
			{
				notificationMap.put(NotificationType.INTRANSIT.getCode(), createNotificationDefPrefData(NotificationType.INTRANSIT));
			}
			if (!notificationMap.containsKey(NotificationType.NEXT_IN_QUEUE.getCode()))
			{
				notificationMap.put(NotificationType.NEXT_IN_QUEUE.getCode(),
						createNotificationDefPrefData(NotificationType.NEXT_IN_QUEUE));
			}
			if (!notificationMap.containsKey(NotificationType.UPDATE_FOR_ETA.getCode()))
			{
				notificationMap.put(NotificationType.UPDATE_FOR_ETA.getCode(),
						createNotificationDefPrefData(NotificationType.UPDATE_FOR_ETA));
			}
			if (!notificationMap.containsKey(NotificationType.DELIVERED.getCode()))
			{
				notificationMap.put(NotificationType.DELIVERED.getCode(), createNotificationDefPrefData(NotificationType.DELIVERED));
			}
			if (!notificationMap.containsKey(NotificationType.CREDITPROCESSED.getCode()))
			{
				notificationMap.put(NotificationType.CREDITPROCESSED.getCode(),
						createNotificationDefPrefData(NotificationType.CREDITPROCESSED));
			}



		}
		else
		{
			// Put Default data in the notification map.
			notificationMap.put(NotificationType.ORDER.getCode(), createNotificationDefPrefData(NotificationType.ORDER));
			notificationMap.put(NotificationType.DELIVERY.getCode(), createNotificationDefPrefData(NotificationType.DELIVERY));
			notificationMap.put(NotificationType.DEAL.getCode(), createNotificationDefPrefData(NotificationType.DEAL));
			notificationMap.put(NotificationType.INTRANSIT.getCode(), createNotificationDefPrefData(NotificationType.INTRANSIT));
			notificationMap.put(NotificationType.NEXT_IN_QUEUE.getCode(),
					createNotificationDefPrefData(NotificationType.NEXT_IN_QUEUE));
			notificationMap.put(NotificationType.UPDATE_FOR_ETA.getCode(),
					createNotificationDefPrefData(NotificationType.UPDATE_FOR_ETA));
			notificationMap.put(NotificationType.DELIVERED.getCode(), createNotificationDefPrefData(NotificationType.DELIVERED));
			notificationMap.put(NotificationType.CREDITPROCESSED.getCode(),
					createNotificationDefPrefData(NotificationType.CREDITPROCESSED));
		}


		notificationData.setNotificationPrefMap(notificationMap);

	}

	/**
	 * This method will create a notification preference data
	 *
	 * @param notifPref
	 *           the notification pref model
	 * @return notificationPrefData the created notification preference data
	 *
	 */
	private NotificationPreferenceData createNotificationPrefData(final SABMNotificationPrefModel notifPref)
	{
		final NotificationPreferenceData notificationPrefData = new NotificationPreferenceData();

		notificationPrefData.setNotificationTypeEnabled(notifPref.getNotificationTypeEnabled());
		notificationPrefData.setNotificationType(notifPref.getNotificationType().getCode());
		notificationPrefData.setEmailEnabled(notifPref.getEmailEnabled());
		notificationPrefData.setSmsEnabled(notifPref.getSmsEnabled());
		if (notificationPrefData.isEmailEnabled())
		{
			notificationPrefData.setEmailDuration(notifPref.getEmailOptedTime());
			if (notifPref.getEmailOptedTimeUnit() != null)
			{
				notificationPrefData.setEmailDurationTimeUnit(notifPref.getEmailOptedTimeUnit().getCode());
			}
		}
		if (notificationPrefData.isSmsEnabled())
		{
			/*
			 * mobile number no longer from SABMNotificationPrefModel, it store in customerModel now as
			 * "mobileContactNumber"
			 */
			// notificationPrefData.setMobileNumber(notifPref.getMobileNumber());
			notificationPrefData.setSmsDuration(notifPref.getSmsOptedTime());
			if (notifPref.getSmsOptedTimeUnit() != null)
			{
				notificationPrefData.setSmsDurationTimeUnit(notifPref.getSmsOptedTimeUnit().getCode());
			}
		}

		if (notifPref.getEmailEnabled())
		{
			if (notifPref.getEmailOptedTimeUnit() != null && notifPref.getEmailOptedTime() != null
					&& notifPref.getEmailOptedTime() > 0)
			{
				notificationPrefData
						.setDuration(getDuration(notifPref.getEmailOptedTime(), notifPref.getEmailOptedTimeUnit().getCode()));
			}
			else if (notifPref.getEmailOptedDay() != null && notifPref.getEmailOptedDay() > 0)
			{
				notificationPrefData.setDuration(getDayDuration(notifPref.getEmailOptedDay()));
			}
			else
			{
				final List<String> list = getDurationList(notifPref.getNotificationType());

				notificationPrefData.setDuration(list.size() != 0 ? list.get(0) : "");
			}
		}

		if (notifPref.getSmsEnabled())
		{
			if (notifPref.getSmsOptedTimeUnit() != null && notifPref.getSmsOptedTime() != null && notifPref.getSmsOptedTime() > 0)
			{
				notificationPrefData.setDuration(getDuration(notifPref.getSmsOptedTime(), notifPref.getSmsOptedTimeUnit().getCode()));
			}
			else if (notifPref.getSmsOptedDay() != null && notifPref.getSmsOptedDay() > 0)
			{
				notificationPrefData.setDuration(getDayDuration(notifPref.getSmsOptedDay()));
			}
			else
			{
				final List<String> list = getDurationList(notifPref.getNotificationType());

				notificationPrefData.setDuration(list.size() != 0 ? list.get(0) : "");
			}
		}

		notificationPrefData.setDurationList(getDurationList(notifPref.getNotificationType()));

		return notificationPrefData;
	}

	/**
	 * This method will create a notification preference data
	 *
	 * @param type
	 *           the notification pref model
	 * @return notificationPrefData the created notification preference data
	 *
	 */
	private NotificationPreferenceData createNotificationDefPrefData(final NotificationType type)
	{
		final NotificationPreferenceData notificationPrefData = new NotificationPreferenceData();

		notificationPrefData.setEmailEnabled(false);
		notificationPrefData.setNotificationTypeEnabled(false);

		notificationPrefData.setSmsEnabled(false);
		notificationPrefData.setNotificationType(type.getCode());
		notificationPrefData.setDuration(getDurationList(type).size() != 0 ? getDurationList(type).get(0) : "");
		notificationPrefData.setDurationList(getDurationList(type));
		return notificationPrefData;
	}

	private String getDuration(final Integer durationTime, final String durationUnit)
	{
		final StringBuffer formattedDuration = new StringBuffer();
		if (durationTime != null && durationTime > 0 && StringUtils.isNotEmpty(durationUnit))
		{
			formattedDuration.append(durationTime);
			formattedDuration.append("-");
			formattedDuration.append(durationUnit.toLowerCase());
		}
		return formattedDuration.toString();
	}

	private String getDayDuration(final Integer durationTime)
	{
		String formattedDuration = new String();
		if (durationTime != null && durationTime > 0)
		{
			String dayOfWeek = DayOfWeek.of(durationTime - 1).toString().toLowerCase();
			dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
			formattedDuration = dayOfWeek;
		}
		return formattedDuration;
	}

	private List<String> getDurationList(final NotificationType notificationType)
	{
		String configuredList = "";
		if (NotificationType.ORDER.equals(notificationType))
		{
			configuredList = getConfigurationService().getConfiguration().getString(ORDER_DURATION_LIST, "");
		}

		else if (NotificationType.DEAL.equals(notificationType))
		{
			configuredList = getConfigurationService().getConfiguration().getString(DEAL_DURATION_LIST, "");
		}

		else
		{

			return Collections.EMPTY_LIST;
		}

		final String[] configuredArray = configuredList.split(",");

		return Arrays.asList(configuredArray);
	}


	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
