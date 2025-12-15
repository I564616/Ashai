package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import de.hybris.platform.notificationservices.enums.NotificationType;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;

public class NotificationTypeUpdateForETAPrefHandler implements DynamicAttributeHandler<Boolean, SABMNotificationModel> {
    @Override
    public Boolean get(final SABMNotificationModel notification) {
        Boolean returnBoolean = Boolean.FALSE;
        if (Objects.nonNull(notification) && CollectionUtils.isNotEmpty(notification.getNotificationPreferences())) {
            Optional<SABMNotificationPrefModel> found = notification.getNotificationPreferences().stream().
                    filter(pref -> pref.getNotificationType().equals(NotificationType.UPDATE_FOR_ETA)).findAny();
            if (found.isPresent() && Objects.nonNull(found.get().getEmailEnabled()) && found.get().getEmailEnabled()) {
                returnBoolean = Boolean.TRUE;
            }
        }
        return returnBoolean;
    }

    @Override
    public void set(final SABMNotificationModel model, final Boolean aBoolean) {
        throw new UnsupportedOperationException(
                "Set of dynamic attribute 'isTypeUpdateForETAPref' of SABMNotification is disabled!");
    }
}