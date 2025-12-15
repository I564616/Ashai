/**
 *
 */
package com.sabmiller.core.event;

import com.sabm.core.model.DealNotificationEmailProcessModel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;


/**
 * @author raul.b.abatol.jr
 *
 */
public class DealNotificationEmailEventListener extends AbstractSiteEventListener<DealNotificationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final DealNotificationEmailEvent event)
	{
		final DealNotificationEmailProcessModel processModel = (DealNotificationEmailProcessModel) businessProcessService
				.createProcess("dealNotificationEmailProcess" + "-" + System.currentTimeMillis(),
						"dealNotificationEmailProcess");

		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());

		processModel.setB2bUnit(event.getB2bUnit());

		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final DealNotificationEmailEvent event)
	{
		return true;
	}

}
