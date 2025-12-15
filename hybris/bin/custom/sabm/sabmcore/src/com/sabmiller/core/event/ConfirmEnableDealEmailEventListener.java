/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import com.sabm.core.model.ConfirmEnabledDealProcessModel;


/**
 *
 */
public class ConfirmEnableDealEmailEventListener extends AbstractSiteEventListener<ConfirmEnableDealEmailEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final ConfirmEnableDealEmailEvent event)
	{
		final ConfirmEnabledDealProcessModel confirmEnabledDealProcessModel = (ConfirmEnabledDealProcessModel) businessProcessService
				.createProcess("confirmEnabledDealProcess-" + event.getFromUser().getUid() + "-" + System.currentTimeMillis(),
						"confirmEnabledDealProcess");
		confirmEnabledDealProcessModel.setSite(event.getSite());
		confirmEnabledDealProcessModel.setLanguage(event.getLanguage());
		confirmEnabledDealProcessModel.setCurrency(event.getCurrency());
		confirmEnabledDealProcessModel.setStore(event.getBaseStore());
		confirmEnabledDealProcessModel.setBehaviourRequirements(event.getBehaviourRequirements());
		confirmEnabledDealProcessModel.setActivatedDealTitles(event.getActivatedDealTitles());
		confirmEnabledDealProcessModel.setDeactivatedDealTitles(event.getDeactivatedDealTitles());
		confirmEnabledDealProcessModel.setFromUser(event.getFromUser());
		confirmEnabledDealProcessModel.setCcEmails(event.getCcEmails());
		confirmEnabledDealProcessModel.setEmailUnit(event.getEmailUnit());
		confirmEnabledDealProcessModel.setToEmails(event.getToEmails());

		confirmEnabledDealProcessModel.setPrimaryAdminStatus(event.getPrimaryAdminStatus());

		modelService.save(confirmEnabledDealProcessModel);
		businessProcessService.startProcess(confirmEnabledDealProcessModel);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected boolean shouldHandleEvent(final ConfirmEnableDealEmailEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.customer.site", site);
		return true;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}


}
