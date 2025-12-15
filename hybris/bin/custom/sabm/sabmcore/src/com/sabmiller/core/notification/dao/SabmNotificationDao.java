/**
 *
 */
package com.sabmiller.core.notification.dao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.notificationservices.enums.NotificationType;

import java.util.List;

import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.SABMNotificationModel;


/**
 * @author raul.b.abatol.jr
 *
 */
public interface SabmNotificationDao
{
	public List<SABMNotificationModel> getNotificationForUser(B2BCustomerModel customerModel, B2BUnitModel b2BUnitModel);

	public SABMNotificationModel getNotificationByID(String id);

	public List<SABMNotificationModel> getNotifications(NotificationType notificationType, Boolean notificationTypeEnabled);

	public List<SABMNotificationModel> getNotificationForUnit(NotificationType notificationType, Boolean notificationTypeEnabled,B2BUnitModel b2BUnitModel);

	/**
	 * @param currentUser
	 * @param selectedB2BUnit
	 * @return
	 */
	public List<AsahiNotificationModel> getNotificationForAsahiUser(B2BCustomerModel currentUser, B2BUnitModel selectedB2BUnit);

	/**
	 * @param customerModel
	 * @return
	 */
	List<SABMNotificationModel> getNotificationForAllUnits(B2BCustomerModel customerModel);


}
