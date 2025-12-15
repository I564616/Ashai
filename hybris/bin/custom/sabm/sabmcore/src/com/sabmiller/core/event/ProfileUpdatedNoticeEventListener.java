/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.model.process.SgaProfileUpdatedNoticeProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;


/**
 *
 */
public class ProfileUpdatedNoticeEventListener extends AbstractSiteEventListener<ProfileUpdatedNoticeEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	protected void onSiteEvent(final ProfileUpdatedNoticeEvent event)
	{
		if (asahiSiteUtil.isSga())
		{
			final SgaProfileUpdatedNoticeProcessModel storeFrontCustomerProcess = (SgaProfileUpdatedNoticeProcessModel) businessProcessService
					.createProcess(
							"sgaProfileUpdatedNoticeProcess-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
							"sgaProfileUpdatedNoticeProcess");
			storeFrontCustomerProcess.setAsahiB2bUnit(event.getAsahiB2bUnit());
			storeFrontCustomerProcess.setSite(event.getSite());
			storeFrontCustomerProcess.setLanguage(event.getLanguage());
			storeFrontCustomerProcess.setCurrency(event.getCurrency());
			storeFrontCustomerProcess.setStore(event.getBaseStore());
			storeFrontCustomerProcess.setCustomer(event.getCustomer());

			modelService.save(storeFrontCustomerProcess);
			businessProcessService.startProcess(storeFrontCustomerProcess);
		}
		else
		{
			final StoreFrontCustomerProcessModel storeFrontCustomerProcess = (StoreFrontCustomerProcessModel) businessProcessService
					.createProcess("profileUpdatedNoticeProcess-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
							"profileUpdatedNoticeProcess");
			storeFrontCustomerProcess.setUser(event.getFromUser());
			storeFrontCustomerProcess.setSite(event.getSite());
			storeFrontCustomerProcess.setLanguage(event.getLanguage());
			storeFrontCustomerProcess.setCurrency(event.getCurrency());
			storeFrontCustomerProcess.setStore(event.getBaseStore());
			storeFrontCustomerProcess.setCustomer(event.getCustomer());

			modelService.save(storeFrontCustomerProcess);
			businessProcessService.startProcess(storeFrontCustomerProcess);
		}
	}

	@Override
	protected boolean shouldHandleEvent(final ProfileUpdatedNoticeEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.customer.site", site);
		return true;
	}

}
