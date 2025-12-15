package com.sabmiller.core.notification.service;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.notificationservices.enums.NotificationType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.AsahiNotificationPrefModel;
import com.sabmiller.core.notification.dao.SabmNotificationDao;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSabmNotificationServiceTest {


    @InjectMocks
    DefaultSabmNotificationService notificationService = new DefaultSabmNotificationService();

    private static final String DATE_PATTERN = "dd/MM/yyyy";



	 private NotificationType notificationType;
	 @Mock
	 private B2BCustomerModel member;
	 @Mock
	 private B2BUnitModel asahiB2BUnitModel;

	 @Mock
	 private AsahiNotificationModel notification;
	 @Mock
	 private SabmNotificationDao notificationDao;
	 @Mock
	 private AsahiNotificationPrefModel pref;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isOKToSendTheNotificationNotSuccessWithParseException() {


        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

        final Integer duration = Integer.valueOf(120);
        final String theServerTimeInBaseStoreTZ = "24th October 2018 at 01:29 PM";
        final String theCutoffDateTime = "24/10/2018 12:00 PM";
        Date lastSendDate = new Date();

        try {
            lastSendDate = sdf.parse("06/05/2018");
        } catch (final ParseException e) {
            e.printStackTrace();
        }


        boolean result = false;
        try {
            result = notificationService.isOKToSendTheNotification(duration, theServerTimeInBaseStoreTZ, theCutoffDateTime, lastSendDate);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        Assert.assertFalse(result);

    }

    @Test
    public void isOKToSendTheNotificationSuccess() {


        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

        final Integer duration = Integer.valueOf(120);
        final String theServerTimeInBaseStoreTZ = "24/10/2018 10:00 PM";
        final String theCutoffDateTime = "24/10/2018 11:00 PM";
        Date lastSendDate = new Date();

        try {
            lastSendDate = sdf.parse("06/05/2018");
        } catch (final ParseException e) {
            e.printStackTrace();
        }


        boolean result = false;
        try {
            result = notificationService.isOKToSendTheNotification(duration, theServerTimeInBaseStoreTZ, theCutoffDateTime, lastSendDate);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(result);

    }

    @Test
	 public void getEmailPreferenceForNotificationTypeTrueTest()
	 {

		 when(notificationDao.getNotificationForAsahiUser(member, asahiB2BUnitModel))
				 .thenReturn(Collections.singletonList(notification));
   	 when(notification.getAsahiNotificationPreferences()).thenReturn(Collections.singletonList(pref));
		 when(pref.getNotificationType()).thenReturn(NotificationType.DELIVERY);
   	 when(pref.getEmailEnabled()).thenReturn(true);
   	 Assert.assertTrue(notificationService.getEmailPreferenceForNotificationType(NotificationType.DELIVERY, member, asahiB2BUnitModel));
	 }




}