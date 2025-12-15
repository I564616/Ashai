/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.OrderUnableToDeliverNotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderUnableToDeliverNotificationEmailEventListener extends AbstractSiteEventListener<OrderUnableToDeliverNotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final OrderUnableToDeliverNotificationEmailEvent event)
	{
		final OrderUnableToDeliverNotificationEmailProcessModel processModel = (OrderUnableToDeliverNotificationEmailProcessModel) businessProcessService
				.createProcess("orderUnableToDeliverNotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"orderUnableToDeliverNotificationEmailProcess");

		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());
		processModel.setOrderCode(event.getOrderCode());
		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final OrderUnableToDeliverNotificationEmailEvent event)
	{
		return true;
	}

}
