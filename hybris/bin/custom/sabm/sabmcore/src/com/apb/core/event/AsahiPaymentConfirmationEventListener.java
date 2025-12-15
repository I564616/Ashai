package com.apb.core.event;

import jakarta.annotation.Resource;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;

import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.AsahiPaymentConfirmationProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import com.apb.core.event.AsahiPaymentConfirmationEvent;

/**
 * The class works as a Listener for "Payment Confirmation Email Event" 
 */

public class AsahiPaymentConfirmationEventListener extends AbstractAcceleratorSiteEventListener<AsahiPaymentConfirmationEvent>  {

	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

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
	
	@Override
	protected SiteChannel getSiteChannelForEvent(final AsahiPaymentConfirmationEvent event) {
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}

	@Override
	protected void onSiteEvent(final AsahiPaymentConfirmationEvent event) {
		
		String businessProcess = ApbCoreConstants.PAYMENT_CONFIRMATION_EMAIL_PROCESS;;
		
		final AsahiPaymentConfirmationProcessModel asahiPaymentConfirmationProcessModel = (AsahiPaymentConfirmationProcessModel) getBusinessProcessService()
				.createProcess(businessProcess+"-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						businessProcess);
		asahiPaymentConfirmationProcessModel.setSite(event.getSite());
		asahiPaymentConfirmationProcessModel.setLanguage(event.getLanguage());
		asahiPaymentConfirmationProcessModel.setCurrency(event.getBaseStore().getDefaultCurrency());
		asahiPaymentConfirmationProcessModel.setStore(event.getBaseStore());
		asahiPaymentConfirmationProcessModel.setAmountPaid(event.getAmountPaid());
		asahiPaymentConfirmationProcessModel.setPaymentDate(event.getPaymentDate());
		asahiPaymentConfirmationProcessModel.setReferenceNo(event.getReferenceNo());
		asahiPaymentConfirmationProcessModel.setPaymentReference(event.getPaymentReference());
		asahiPaymentConfirmationProcessModel.setPaymentMethod(event.getPaymentMethod());
		asahiPaymentConfirmationProcessModel.setCustomer(event.getCustomer());
		asahiPaymentConfirmationProcessModel.setAsahiSAMInvoices(event.getAsahiSAMInvoices());
		getModelService().save(asahiPaymentConfirmationProcessModel);
		getBusinessProcessService().startProcess(asahiPaymentConfirmationProcessModel);
	}

}
