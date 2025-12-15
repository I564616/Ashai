package com.sabmiller.facades.notification;

import de.hybris.platform.notificationservices.enums.NotificationType;

import java.util.List;

import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.facades.notifications.data.NotificationData;
import com.sabmiller.facades.notifications.data.NotificationPreferenceData;


/**
 * Created by evariz.d.paragoso on 6/6/17.
 */
public interface SABMNotificationFacade
{
	/**
	 * Gets list of all notificaiton of the current user
	 *
	 * @return NotificationData the retrieved NotificationData
	 */
	public NotificationData getUserNotification();

	/**
	 * Gets list of all Notification model of the current user
	 *
	 * @return SABMNotificationModel the retrieved SABMNotificationModel
	 */
	public SABMNotificationModel getUserNotificationModel();

	/**
	 * Gets list of all Notification model of the given user
	 *
	 * @return SABMNotificationModel the retrieved SABMNotificationModel
	 */
	public SABMNotificationModel getNotificationModel(String uid);

	/**
	 * Saves the given notification pref to the notification of the user
	 *
	 * @param notification
	 * @param notificationType
	 * @param notificationEnbled
	 * @param emailEnabled
	 * @param smsEnabled
	 * @param emailDuration
	 * @param smsDuration
	 * @param mobileNumber
	 */
	 void saveNotification(List<NotificationPreferenceData> notifPreferences,final String mobileNumber);


	 void sendNotifications(final NotificationType theNotificationType) throws Exception;

	 /**
	  * @param customerModel
	  * @return
	  */
	 List<SABMNotificationModel> getNotificationForAllUnits(String uid);

}
