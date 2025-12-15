/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.OrderDispatchNotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderDispatchNotificationEmailEventListener extends AbstractSiteEventListener<OrderDispatchNotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final OrderDispatchNotificationEmailEvent event)
	{
		final OrderDispatchNotificationEmailProcessModel processModel = (OrderDispatchNotificationEmailProcessModel) businessProcessService
				.createProcess("orderDispatchNotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"orderDispatchNotificationEmailProcess");

		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());

		processModel.setB2bUnit(event.getB2bUnit());
		processModel.setOrder(event.getOrder());

		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final OrderDispatchNotificationEmailEvent event)
	{
		return true;
	}

}
