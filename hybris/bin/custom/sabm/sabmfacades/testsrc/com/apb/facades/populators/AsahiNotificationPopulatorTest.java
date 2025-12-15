/**
 *
 */
package com.apb.facades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.notificationservices.enums.NotificationType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.asahi.facades.notifications.data.AsahiNotificationData;
import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.AsahiNotificationPrefModel;


@UnitTest
public class AsahiNotificationPopulatorTest
{

	@InjectMocks
	private final AsahiNotificationPopulator asahiNotificationPopulator = new AsahiNotificationPopulator();;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final AsahiNotificationModel model = Mockito.mock(AsahiNotificationModel.class);
		final AsahiNotificationData target = new AsahiNotificationData();
		final List<AsahiNotificationPrefModel> value = new ArrayList<AsahiNotificationPrefModel>();
		final AsahiNotificationPrefModel notificationPref = Mockito.mock(AsahiNotificationPrefModel.class);
		Mockito.when(notificationPref.getNotificationType()).thenReturn(NotificationType.CONTACT_US);
		Mockito.when(notificationPref.getEmailEnabled()).thenReturn(Boolean.TRUE);
		value.add(notificationPref);
		Mockito.when(model.getAsahiNotificationPreferences()).thenReturn(value);
		asahiNotificationPopulator.populate(model, target);
		Assert.assertEquals(true,
				target.getNotificationPreferences().containsKey(NotificationType.CONTACT_US.toString()));
	}

	@Test
	public void testPopulateDefault()
	{
		final AsahiNotificationData target = new AsahiNotificationData();
		asahiNotificationPopulator.populate(null, target);
		Assert.assertEquals(6, target.getNotificationPreferences().size());
	}
}
