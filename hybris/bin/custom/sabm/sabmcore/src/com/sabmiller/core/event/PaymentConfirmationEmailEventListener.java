/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import jakarta.annotation.Resource;

import com.sabm.core.model.PaymentConfirmationEmailProcessModel;


/**
 *
 */
public class PaymentConfirmationEmailEventListener extends AbstractSiteEventListener<PaymentConfirmationEmailEvent>
{
	@Resource(name = "modelService")
	private ModelService modelService;
	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;


	@Override
	protected void onSiteEvent(final PaymentConfirmationEmailEvent event)
	{
		final PaymentConfirmationEmailProcessModel processModel = (PaymentConfirmationEmailProcessModel) businessProcessService
				.createProcess("paymentConfirmationEmailProcess" + "-" + event.getInvoicePayment().getPaymentCode() + "-"
						+ System.currentTimeMillis(), "paymentConfirmationEmailProcess");
		processModel.setSite(event.getSite());
		processModel.setCustomer(event.getCustomer());
		processModel.setLanguage(event.getLanguage());
		processModel.setCurrency(event.getCurrency());
		processModel.setStore(event.getBaseStore());
		processModel.setInvoicePayment(event.getInvoicePayment());

		modelService.save(processModel);
		businessProcessService.startProcess(processModel);
	}

	@Override
	protected boolean shouldHandleEvent(final PaymentConfirmationEmailEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.customer.site", site);
		return true;
	}



}
