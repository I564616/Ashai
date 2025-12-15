package com.sabmiller.core.notification.service;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.SABMNotificationModel;


/**
 * Created by raul.b.abatol.jr on 04/07/2017.
 */
public interface NotificationService
{

	public SABMNotificationModel getNotificationForUserB2BUnit(UserModel user, B2BUnitModel b2bUnit);

	public SABMNotificationModel getNotificationByID(final String id);

	public List<SABMNotificationModel> getNotifications(NotificationType notificationType, Boolean notificationTypeEnabled);

	public List<SABMNotificationModel> getNotificationsForUnit(NotificationType notificationType, Boolean notificationTypeEnabled,B2BUnitModel b2bUnit);

	public void updateLastSendDateOfNotificationTypeDeliveryMode(final String notificationID,
			final NotificationType notificationType, final String notificationDeliveryMode, final Date lastSendDate);

	public void sendNotifications(NotificationType notificationType) throws Exception;

	public Date getSafeNextAvailableDeliveryDate(B2BUnitModel b2bUnit);

	/**
	 * @param consignmentModel
	 */
	public void sendOrderDeliveredNotification(ConsignmentModel consignmentModel);

	/**
	 * @param consignmentModel
	 */
	void sendOrderUnableToDeliverNotification(ConsignmentModel consignmentModel);

	/**
	 * @param consignmentModel
	 */
	public void sendOrderNextInQueueDeliveryNotification(ConsignmentModel consignmentModel);

	/**
	 * @param consignmentModel
	 */
	void sendOrderETAChangesNotification(ConsignmentModel consignmentModel);

	/**
	 * @param consignmentModel
	 */
	void sendOrderETANotification(ConsignmentModel consignmentModel);

	void sendOrderDispatchEmailOrSms(final NotificationType notificationType) ;

	void sendTrackOrderTimePassesETAEmailOrSms(final NotificationType notificationType) ;

	/**
	 * @param currentUser
	 * @param currentb2bunit
	 * @return
	 */
	public AsahiNotificationModel getNotificationForAsahiUserB2BUnit(UserModel currentUser, B2BUnitModel currentb2bunit);

	/**
	 * @param notificationType
	 * @param member
	 * @param asahiB2BUnitModel
	 * @return
	 */
	public boolean getEmailPreferenceForNotificationType(NotificationType notificationType, B2BCustomerModel member,
			B2BUnitModel asahiB2BUnitModel);


	List<SABMNotificationModel> getNotificationForAllUnits(B2BCustomerModel customerModel);

}
