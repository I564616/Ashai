/**
 *
 */
package com.apb.facades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.asahi.facades.notification.impl.AsahiNotificationFacadeImpl;
import com.asahi.facades.notifications.data.AsahiNotificationData;
import com.asahi.facades.notifications.data.AsahiNotificationPreferenceData;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.AsahiNotificationPrefModel;
import com.sabmiller.core.notification.service.NotificationService;

@UnitTest
public class AsahiNotificationFacadeImplTest
{

	@InjectMocks
	private final AsahiNotificationFacadeImpl asahiNotificationFacade = new AsahiNotificationFacadeImpl();

	@Mock
	private NotificationService notificationService;

	@Mock
	private UnitService unitService;

	@Mock
	private ModelService modelService;

	@Mock
	private UserService userService;

	@Mock
	private SabmB2BUnitService b2bUnitService;

	@Mock
	private EnumerationService enumerationService;

	AsahiNotificationModel model;

	@Mock
	private Converter<AsahiNotificationModel, AsahiNotificationData> asahiNotificationConverter;

	final List<AsahiNotificationPrefModel> value = new ArrayList<AsahiNotificationPrefModel>();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(userService.getCurrentUser()).thenReturn(Mockito.mock(B2BCustomerModel.class));
		model = Mockito.mock(AsahiNotificationModel.class);
		final AsahiNotificationPrefModel notificationPref = Mockito.mock(AsahiNotificationPrefModel.class);
		Mockito.when(notificationPref.getNotificationType()).thenReturn(NotificationType.CONTACT_US);
		Mockito.when(notificationPref.getEmailEnabled()).thenReturn(Boolean.TRUE);
		value.add(notificationPref);
		Mockito.when(model.getAsahiNotificationPreferences()).thenReturn(value);
		Mockito.when(notificationService.getNotificationForAsahiUserB2BUnit(Mockito.any(), Mockito.any()))
				.thenReturn(model);

	}

	@Test
	public void testUserNotificationPreferences()
	{
		asahiNotificationFacade.setAsahiNotificationConverter(asahiNotificationConverter);
		final AsahiNotificationData notifData = new AsahiNotificationData();
		final AsahiNotificationPreferenceData preference = new AsahiNotificationPreferenceData();
		preference.setNotificationType(NotificationType.CONTACT_US.toString());
		preference.setEmailEnabled(Boolean.TRUE);
		final Map<String, AsahiNotificationPreferenceData> map = new HashMap<>();
		map.put(NotificationType.CONTACT_US.toString(), preference);
		notifData.setNotificationPreferences(map);
		Mockito.when(asahiNotificationConverter.convert(Mockito.any(), Mockito.any())).thenReturn(notifData);
		final AsahiNotificationData result = asahiNotificationFacade.getUserNotificationPreferences();
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getNotificationPreferences());
		Assert.assertEquals(true,
				result.getNotificationPreferences().containsKey(NotificationType.CONTACT_US.toString()));
	}

	@Test
	public void testUserNotificationModel()
	{
		final AsahiNotificationModel notification = asahiNotificationFacade.getUserNotificationModel();
		Assert.assertEquals(notification.getAsahiNotificationPreferences(), value);
	}

	@Test
	public void testSaveNotificationPreferences()
	{
		Mockito.when(enumerationService.getEnumerationValue(Mockito.anyString(), Mockito.any())).thenReturn(NotificationType.CONTACT_US);
		final List<AsahiNotificationPreferenceData> notifPreferences = new ArrayList<>();
		final AsahiNotificationPreferenceData pref1 = new AsahiNotificationPreferenceData();
		pref1.setNotificationType(NotificationType.CONTACT_US.toString());
		pref1.setEmailEnabled(true);
		notifPreferences.add(pref1);
		asahiNotificationFacade.saveNotificationPreferences(notifPreferences);
		Mockito.verify(modelService, Mockito.times(2)).save(Mockito.any());

	}
}


