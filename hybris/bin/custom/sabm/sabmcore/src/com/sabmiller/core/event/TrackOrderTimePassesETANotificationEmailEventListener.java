/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.TrackOrderTimePassesETANotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class TrackOrderTimePassesETANotificationEmailEventListener
		extends AbstractSiteEventListener<TrackOrderTimePassesETANotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final TrackOrderTimePassesETANotificationEmailEvent event)
	{
		final TrackOrderTimePassesETANotificationEmailProcessModel processModel = (TrackOrderTimePassesETANotificationEmailProcessModel) businessProcessService
				.createProcess("trackOrderTimePassesETANotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"trackOrderTimePassesETANotificationEmailProcess");

		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());

		processModel.setB2bUnit(event.getB2bUnit());
		processModel.setOrderCode(event.getOrderCode());

		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final TrackOrderTimePassesETANotificationEmailEvent event)
	{
		return true;
	}

}
