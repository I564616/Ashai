/**
 *
 */
package com.sabmiller.core.process.email.notification.action;

import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

import com.sabm.core.model.OrderDispatchNotificationEmailProcessModel;
import org.apache.commons.lang3.BooleanUtils;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderDispatchNotificationEmailSentAction extends AbstractProceduralAction<OrderDispatchNotificationEmailProcessModel>
{

	@Override
	public void executeAction(final OrderDispatchNotificationEmailProcessModel processModel) throws RetryLaterException, Exception
	{
		for (final EmailMessageModel emailMessage : processModel.getEmails())
		{
			if (emailMessage.isSent())
			{
				getModelService().remove(emailMessage);

//				final OrderModel order = processModel.getOrder();
//				order.setDispatchNotifEmailSent(Boolean.TRUE);
//				for (final ConsignmentModel consigment : order.getConsignments())
//				{
//					if (ConsignmentStatus.SHIPPED.equals(consigment.getStatus()) && BooleanUtils.isNotTrue(consigment.getDispatchNotifEmailSent()))
//					{
//						consigment.setDispatchNotifEmailSent(true);
//						getModelService().save(consigment);
//					}
//				}
//				getModelService().save(order);
			}
		}
	}

}
