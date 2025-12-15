/**
 * 
 */
package com.apb.core.event;

import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiPasswordResetEmailEventListener extends AbstractAcceleratorSiteEventListener<AsahiPasswordResetEmailEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	
	@Override
	protected SiteChannel getSiteChannelForEvent(AsahiPasswordResetEmailEvent welcomeEmailEvent)
	{
		final BaseSiteModel site = welcomeEmailEvent.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}

	@Override
	protected void onSiteEvent(AsahiPasswordResetEmailEvent event)
	{
		final ForgottenPasswordProcessModel asahiPasswordResetEmailProcessModel = (ForgottenPasswordProcessModel) getBusinessProcessService()
				.createProcess("asahiPasswordResetEmailProcess-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						"asahiPasswordResetEmailProcess");
		asahiPasswordResetEmailProcessModel.setSite(event.getSite());
		asahiPasswordResetEmailProcessModel.setCustomer(event.getCustomer());	
		asahiPasswordResetEmailProcessModel.setLanguage(event.getLanguage());
		asahiPasswordResetEmailProcessModel.setCurrency(event.getBaseStore().getDefaultCurrency());
		asahiPasswordResetEmailProcessModel.setStore(event.getBaseStore());
		asahiPasswordResetEmailProcessModel.setToken(event.getToken());
		asahiPasswordResetEmailProcessModel.setOrderAccess(event.getOrderAccess());
		asahiPasswordResetEmailProcessModel.setPayAccess(event.getPayAccess());
		asahiPasswordResetEmailProcessModel.setPayerEmail(event.getPayerEmail());
		asahiPasswordResetEmailProcessModel.setCustomerAccountName(event.getCustomerAccountName());	 
		getModelService().save(asahiPasswordResetEmailProcessModel);
		getBusinessProcessService().startProcess(asahiPasswordResetEmailProcessModel);
		
	}


	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
