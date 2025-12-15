/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.OrderCutoffNotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderCutoffNotificationEmailEventListener extends AbstractSiteEventListener<OrderCutoffNotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final OrderCutoffNotificationEmailEvent event)
	{
		final OrderCutoffNotificationEmailProcessModel processModel = (OrderCutoffNotificationEmailProcessModel) businessProcessService
				.createProcess("orderCutoffNotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"orderCutoffNotificationEmailProcess");

		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());

		processModel.setNotificationID(event.getNotificationID());
		processModel.setNotificationType(event.getNotificationType());
		processModel.setCutoffDateTime(event.getCutoffDateTime());
		processModel.setDeliveryDate(event.getDeliveryDate());
		processModel.setServerTimeInBaseStoreTZ(event.getServerTimeInBaseStoreTZ());

		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final OrderCutoffNotificationEmailEvent event)
	{
		return true;
	}

}
