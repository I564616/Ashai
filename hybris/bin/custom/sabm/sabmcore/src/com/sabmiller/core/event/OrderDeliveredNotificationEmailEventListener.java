/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.OrderDeliveredNotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderDeliveredNotificationEmailEventListener extends AbstractSiteEventListener<OrderDeliveredNotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final OrderDeliveredNotificationEmailEvent event)
	{
		final OrderDeliveredNotificationEmailProcessModel processModel = (OrderDeliveredNotificationEmailProcessModel) businessProcessService
				.createProcess("orderDeliveredNotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"orderDeliveredNotificationEmailProcess");

		processModel.setSignature(event.getSignature());
		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());
		processModel.setOrder(((OrderModel)event.getOrder()));
		processModel.setTimeStamp(event.getTimeStamp());
		processModel.setDeliveryAddress(event.getDeliveryAddress());
		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final OrderDeliveredNotificationEmailEvent event)
	{
		return true;
	}

}
