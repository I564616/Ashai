/**
 *
 */
package com.asahi.facades.notification.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.asahi.facades.notification.AsahiNotificationFacade;
import com.asahi.facades.notifications.data.AsahiNotificationData;
import com.asahi.facades.notifications.data.AsahiNotificationPreferenceData;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.AsahiNotificationPrefModel;
import com.sabmiller.core.notification.service.NotificationService;

/**
 *
 */
public class AsahiNotificationFacadeImpl implements AsahiNotificationFacade
{
	@Resource(name = "notificationService")
	private NotificationService notificationService;

	@Resource(name = "asahiNotificationConverter")
	private Converter<AsahiNotificationModel, AsahiNotificationData> asahiNotificationConverter;

	@Resource(name = "unitService")
	private UnitService unitService;


	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Autowired
	private EnumerationService enumerationService;

	@Override
	public AsahiNotificationData getUserNotificationPreferences()
	{
		final AsahiNotificationData notificationData = new AsahiNotificationData();
		final UserModel currentUser = userService.getCurrentUser();
		if (currentUser instanceof B2BCustomerModel)
		{
			final B2BUnitModel currentb2bunit = ((B2BCustomerModel) currentUser).getDefaultB2BUnit();
			final AsahiNotificationModel notificationModel = notificationService.getNotificationForAsahiUserB2BUnit(currentUser,
					currentb2bunit);
			return asahiNotificationConverter.convert(notificationModel, notificationData);
		}
		return notificationData;
	}

	@Override
	public void saveNotificationPreferences(final List<AsahiNotificationPreferenceData> notifPreferences)
	{

		AsahiNotificationModel notification = getUserNotificationModel();

		final UserModel currentUser = userService.getCurrentUser();

		if (notification == null)
		{

			final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
			notification = modelService.create(AsahiNotificationModel.class);
			notification.setAsahiNotificationPreferences(new ArrayList<AsahiNotificationPrefModel>());
			notification.setB2bUnit(selectedB2BUnit);
			notification.setUser((B2BCustomerModel) currentUser);
		}


		for (final AsahiNotificationPreferenceData prefData : notifPreferences)
		{
			final String notificationType = prefData.getNotificationType();
			final boolean emailEnabled = prefData.isEmailEnabled();

			AsahiNotificationPrefModel notifPreference = null;
			final List<AsahiNotificationPrefModel> preferences = notification.getAsahiNotificationPreferences();
			final Optional<AsahiNotificationPrefModel> notifPreferenceOptional = preferences.stream()
					.filter(pref -> pref.getNotificationType().toString().equalsIgnoreCase(notificationType)).findFirst();

			if (notifPreferenceOptional.isPresent())
			{
				notifPreference = notifPreferenceOptional.get();
			}
			if (notifPreference == null)
			{
				notifPreference = modelService.create(AsahiNotificationPrefModel.class);
				notifPreference.setNotificationType(
						enumerationService.getEnumerationValue(NotificationType.class, prefData.getNotificationType()));
				final List<AsahiNotificationPrefModel> notificationTempList = new ArrayList<AsahiNotificationPrefModel>();
				notificationTempList.addAll(preferences);
				notificationTempList.add(notifPreference);
				notification.setAsahiNotificationPreferences(notificationTempList);
			}
			notifPreference.setEmailEnabled(emailEnabled);

			modelService.save(notifPreference);
		}

		modelService.save(notification);

	}

	/**
	 * @return
	 */
		@Override
	public AsahiNotificationModel getUserNotificationModel()
		{
			final UserModel currentUser = userService.getCurrentUser();
			final B2BUnitModel selectedB2BUnit = ((B2BCustomerModel) currentUser).getDefaultB2BUnit();
		return notificationService.getNotificationForAsahiUserB2BUnit(currentUser, selectedB2BUnit);
		}

	/**
	 * @return the asahiNotificationConverter
	 */
	public Converter<AsahiNotificationModel, AsahiNotificationData> getAsahiNotificationConverter()
	{
		return asahiNotificationConverter;
	}

	/**
	 * @param asahiNotificationConverter
	 *           the asahiNotificationConverter to set
	 */
	public void setAsahiNotificationConverter(
			final Converter<AsahiNotificationModel, AsahiNotificationData> asahiNotificationConverter)
	{
		this.asahiNotificationConverter = asahiNotificationConverter;
	}
	}


