/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.OrderNextInQueueDeliveryNotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderNextInQueueDeliveryNotificationEmailEventListener
		extends AbstractSiteEventListener<OrderNextInQueueDeliveryNotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final OrderNextInQueueDeliveryNotificationEmailEvent event)
	{
		final OrderNextInQueueDeliveryNotificationEmailProcessModel processModel = (OrderNextInQueueDeliveryNotificationEmailProcessModel) businessProcessService
				.createProcess("orderNextInQueueDeliveryNotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"orderNextInQueueDeliveryNotificationEmailProcess");

		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());
		processModel.setEndETA(event.getEndETA());
		processModel.setStartETA(event.getStartETA());
		processModel.setOrder((OrderModel)event.getOrder());
		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final OrderNextInQueueDeliveryNotificationEmailEvent event)
	{
		return true;
	}

}
