/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.TrackOrderETAChangesNotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class TrackOrderETAChangesNotificationEmailEventListener
		extends AbstractSiteEventListener<TrackOrderETAChangesNotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final TrackOrderETAChangesNotificationEmailEvent event)
	{
		final TrackOrderETAChangesNotificationEmailProcessModel processModel = (TrackOrderETAChangesNotificationEmailProcessModel) businessProcessService
				.createProcess("trackOrderETAChangesNotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"trackOrderETAChangesNotificationEmailProcess");

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
	protected boolean shouldHandleEvent(final TrackOrderETAChangesNotificationEmailEvent event)
	{
		return true;
	}

}
