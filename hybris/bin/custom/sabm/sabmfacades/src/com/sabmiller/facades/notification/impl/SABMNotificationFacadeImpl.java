package com.sabmiller.facades.notification.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.enums.NotificationTimeUnit;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.core.notification.service.NotificationService;
import com.sabmiller.facades.notification.SABMNotificationFacade;
import com.sabmiller.facades.notifications.data.NotificationData;
import com.sabmiller.facades.notifications.data.NotificationPreferenceData;


/**
 * @author marc.f.l.bautista
 *
 */
public class SABMNotificationFacadeImpl implements SABMNotificationFacade
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMNotificationFacadeImpl.class);

	private static final String MINUTES = "minutes";

	@Resource(name = "notificationService")
	private NotificationService notificationService;

	@Resource(name = "sabmNotificationConverter")
	private Converter<SABMNotificationModel, NotificationData> notificationConverter;

	@Resource(name = "unitService")
	private UnitService unitService;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Override
	public NotificationData getUserNotification()
	{

		final UserModel currentUser = userService.getCurrentUser();
		final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		final SABMNotificationModel notificationModel = notificationService.getNotificationForUserB2BUnit(currentUser,selectedB2BUnit);
		final NotificationData notificationData = new NotificationData();
		notificationConverter.convert(notificationModel, notificationData);
		if (userService.getCurrentUser() != null && userService.getCurrentUser() instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) userService.getCurrentUser();
			notificationData.setMobileNumber(customer.getMobileContactNumber());
		}
		return notificationData;
	}

	@Override
	public SABMNotificationModel getUserNotificationModel()
	{
		final UserModel currentUser = userService.getCurrentUser();
		final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		return notificationService.getNotificationForUserB2BUnit(currentUser,selectedB2BUnit);
	}


	@Override
	public SABMNotificationModel getNotificationModel(final String uid)
	{
		final UserModel currentUser = userService.getUserForUID(uid);
		final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		return notificationService.getNotificationForUserB2BUnit(currentUser,selectedB2BUnit);
	}

	@Override
	public void saveNotification(final List<NotificationPreferenceData> notifPreferences,final String mobileNumber)
	{

		SABMNotificationModel notification = getUserNotificationModel();

		final UserModel currentUser = userService.getCurrentUser();

        if (StringUtils.isNotEmpty(mobileNumber) && currentUser instanceof  B2BCustomerModel){
	        final B2BCustomerModel customer = (B2BCustomerModel) currentUser;
			customer.setMobileContactNumber(StringUtils.deleteWhitespace(mobileNumber));
	        modelService.save(customer);
        }

		if (notification == null){

			final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
			notification = modelService.create(SABMNotificationModel.class);
			notification.setNotificationPreferences(new ArrayList<SABMNotificationPrefModel>());
			notification.setB2bUnit(selectedB2BUnit);
			notification.setUser((B2BCustomerModel)currentUser);
		}


		for(final NotificationPreferenceData prefData : notifPreferences){
			final String notificationType = prefData.getNotificationType();
			final boolean notificationEnabled = prefData.isNotificationTypeEnabled();
			final boolean emailEnabled = prefData.isEmailEnabled();
			final boolean smsEnabled = prefData.isSmsEnabled();
			final Integer emailDuration = prefData.getEmailDuration();
			final String emailDurationTimeUnit = prefData.getEmailDurationTimeUnit();
			final Integer smsDuration = prefData.getSmsDuration();
			final String smsDurationTimeUnit = prefData.getSmsDurationTimeUnit();


			SABMNotificationPrefModel notifPreference = null;
			for (final SABMNotificationPrefModel notifPref : notification.getNotificationPreferences())
			{
				if (notifPref.getNotificationType().getCode().equals(notificationType))
				{
					notifPreference = notifPref;
					break;
				}
			}

			if(notifPreference == null){
				notifPreference = modelService.create(SABMNotificationPrefModel.class);
				notifPreference.setNotificationType(NotificationType.valueOf(notificationType.toUpperCase()));
				final List<SABMNotificationPrefModel> notificationPrefList = notification.getNotificationPreferences();
				final List<SABMNotificationPrefModel> notificationTempList =new ArrayList<SABMNotificationPrefModel>();
				notificationTempList.addAll(notificationPrefList);
				notificationTempList.add(notifPreference);
				notification.setNotificationPreferences(notificationTempList);
			}
			notifPreference.setNotificationTypeEnabled(notificationEnabled);
			notifPreference.setEmailEnabled(emailEnabled);
			notifPreference.setSmsEnabled(smsEnabled);
			if(notificationType.equals(NotificationType.ORDER.getCode())){
				if(emailEnabled){
					notifPreference.setEmailOptedTime(emailDuration);
					notifPreference.setEmailOptedTimeUnit(getTimeUnit(emailDurationTimeUnit));
				}
				if(smsEnabled){
					notifPreference.setSmsOptedTime(smsDuration);
					notifPreference.setSmsOptedTimeUnit(getTimeUnit(smsDurationTimeUnit));
					// if (mobileNumber != null)
					// {
					// 	notifPreference.setMobileNumber(Integer.valueOf(mobileNumber));
					// }
				}

			} else if(notificationType.equals(NotificationType.DEAL.getCode())){
				if(emailEnabled){
					notifPreference.setEmailOptedDay(emailDuration);
				}
				if(smsEnabled){
					notifPreference.setSmsOptedDay(smsDuration);
					// if (mobileNumber != null)
					// {
					// 	notifPreference.setMobileNumber(Integer.valueOf(mobileNumber));
					// }
				}
			}

			modelService.save(notifPreference);
		}

		modelService.save(notification);
	}

	public NotificationTimeUnit getTimeUnit(final String time)
	{
		return NotificationTimeUnit.valueOf(time.toUpperCase());
	}

	public Integer getDay(final String dayString)
	{
		Integer day = 0;
		if (StringUtils.isNotEmpty(dayString) && !dayString.equals("0")) {
			day =  DayOfWeek.valueOf(StringUtils.upperCase(dayString)).getValue() + 1;
		}
		return day;
	}

	@Override
	public void sendNotifications(final NotificationType theNotificationType) throws Exception {
		notificationService.sendNotifications(theNotificationType);
	}

	@Override
	public List<SABMNotificationModel> getNotificationForAllUnits(final String uid)
	{
		return notificationService.getNotificationForAllUnits((B2BCustomerModel) userService.getUserForUID(uid));
	}




}
