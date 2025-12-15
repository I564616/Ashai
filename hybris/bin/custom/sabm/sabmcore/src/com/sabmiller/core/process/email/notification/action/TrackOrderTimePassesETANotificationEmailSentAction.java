/**
 *
 */
package com.sabmiller.core.process.email.notification.action;

import com.sabm.core.model.TrackOrderTimePassesETANotificationEmailProcessModel;
import com.sabmiller.core.order.SabmB2BOrderService;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;


/**
 * @author marc.f.l.bautista
 */
public class TrackOrderTimePassesETANotificationEmailSentAction
        extends AbstractProceduralAction<TrackOrderTimePassesETANotificationEmailProcessModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackOrderTimePassesETANotificationEmailSentAction.class);


    @Resource(name = "b2bOrderService")
    private SabmB2BOrderService b2bOrderService;

    @Override
    public void executeAction(final TrackOrderTimePassesETANotificationEmailProcessModel processModel)
            throws RetryLaterException, Exception {
        for (final EmailMessageModel emailMessage : processModel.getEmails()) {
            if (emailMessage.isSent()) {
                getModelService().remove(emailMessage);

                LOG.debug("inside TrackOrderTimePassesETANotificationEmailSentAction");
//				final OrderModel order = b2bOrderService.getOrderBySapSalesOrderNumber(processModel.getOrderCode());
//				order.setTrackOrderTimePassesETANotifEmailSent(Boolean.TRUE);
//				getModelService().save(order);
            }
        }
    }

}
