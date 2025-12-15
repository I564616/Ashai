/**
 * 
 */
package com.apb.core.event;

import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerWelcomeEmailProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiCustomerWelcomeEmailEventListener extends AbstractAcceleratorSiteEventListener<AsahiCustomerWelcomeEmailEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	
	@Override
	protected SiteChannel getSiteChannelForEvent(AsahiCustomerWelcomeEmailEvent welcomeEmailEvent)
	{
		final BaseSiteModel site = welcomeEmailEvent.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}

	@Override
	protected void onSiteEvent(AsahiCustomerWelcomeEmailEvent event)
	{
		final AsahiCustomerWelcomeEmailProcessModel asahiCustomerWelcomeEmailProcessModel = (AsahiCustomerWelcomeEmailProcessModel) getBusinessProcessService()
				.createProcess("asahiCustomerWelcomeEmailProcess-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						"asahiCustomerWelcomeEmailProcess");
		asahiCustomerWelcomeEmailProcessModel.setSite(event.getSite());
		asahiCustomerWelcomeEmailProcessModel.setCustomer(event.getCustomer());
		asahiCustomerWelcomeEmailProcessModel.setLanguage(event.getLanguage());
		asahiCustomerWelcomeEmailProcessModel.setCurrency(event.getBaseStore().getDefaultCurrency());
		asahiCustomerWelcomeEmailProcessModel.setStore(event.getBaseStore());
		asahiCustomerWelcomeEmailProcessModel.setOrderAccess(event.getOrderAccess());
		asahiCustomerWelcomeEmailProcessModel.setPayAccess(event.getPayAccess());
		asahiCustomerWelcomeEmailProcessModel.setPayerEmail(event.getPayerEmail());
		asahiCustomerWelcomeEmailProcessModel.setCustomerAccountName(event.getCustomerAccountName());	 
		getModelService().save(asahiCustomerWelcomeEmailProcessModel);
		getBusinessProcessService().startProcess(asahiCustomerWelcomeEmailProcessModel);
		
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
