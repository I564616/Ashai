/**
 *
 */
package com.sabmiller.core.process.email.notification.action;

import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.Date;

import jakarta.annotation.Resource;

import com.sabm.core.model.OrderCutoffNotificationEmailProcessModel;
import com.sabmiller.core.notification.service.NotificationService;
import com.sabmiller.core.util.SabmDateUtils;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderCutoffNotificationEmailSentAction extends AbstractProceduralAction<OrderCutoffNotificationEmailProcessModel>
{
	@Resource(name = "notificationService")
	private NotificationService notificationService;

	@Override
	public void executeAction(final OrderCutoffNotificationEmailProcessModel processModel) throws RetryLaterException, Exception
	{
		for (final EmailMessageModel emailMessage : processModel.getEmails())
		{
			if (emailMessage.isSent())
			{
				getModelService().remove(emailMessage);

				final Date newEmailLastSendDate = SabmDateUtils.getOnlyDate(processModel.getServerTimeInBaseStoreTZ());

				notificationService.updateLastSendDateOfNotificationTypeDeliveryMode(processModel.getNotificationID(),
						processModel.getNotificationType(), "Email", newEmailLastSendDate);
			}
		}

	}

}
