package com.sabmiller.core.handlers;

import de.hybris.platform.notificationservices.enums.NotificationType;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by zhuo.a.jiang on 21/8/18.
 */
public class NotificationTypeInvoiceDiscrepancyHandler implements DynamicAttributeHandler<Boolean, SABMNotificationModel> {
    @Override
    public Boolean get(final SABMNotificationModel notification) {
        Boolean returnBoolean = Boolean.FALSE;
        if (Objects.nonNull(notification) && CollectionUtils.isNotEmpty(notification.getNotificationPreferences())) {
            Optional<SABMNotificationPrefModel> found = notification.getNotificationPreferences().stream().
                    filter(pref -> pref.getNotificationType().equals(NotificationType.CREDITPROCESSED)).findAny();
            if (found.isPresent() && Objects.nonNull(found.get().getEmailEnabled()) && found.get().getEmailEnabled()) {
                returnBoolean = Boolean.TRUE;
            }
        }
        return returnBoolean;
    }

    @Override
    public void set(final SABMNotificationModel model, final Boolean aBoolean) {
        throw new UnsupportedOperationException(
                "Set of dynamic attribute 'isTypeNextInQueuePref' of SABMNotification is disabled!");
    }
}
