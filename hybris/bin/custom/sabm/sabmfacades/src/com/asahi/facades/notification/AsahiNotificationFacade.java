/**
 *
 */
package com.asahi.facades.notification;

import java.util.List;

import com.asahi.facades.notifications.data.AsahiNotificationData;
import com.asahi.facades.notifications.data.AsahiNotificationPreferenceData;
import com.sabmiller.core.model.AsahiNotificationModel;


/**
 *
 */
public interface AsahiNotificationFacade
{


	/**
	 * @return
	 */
	AsahiNotificationData getUserNotificationPreferences();

	/**
	 * @param preferences
	 */
	void saveNotificationPreferences(List<AsahiNotificationPreferenceData> preferences);


	/**
	 * @return
	 */
	AsahiNotificationModel getUserNotificationModel();


}
