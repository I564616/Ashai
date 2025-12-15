/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.core.event;

import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.ApbContactUsEmailProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Listener for Contact Us events.
 */
public class ApbContactUsEventListener extends AbstractAcceleratorSiteEventListener<ApbContactUsEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;

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
	protected void onSiteEvent(final ApbContactUsEvent apbContactUsEvent)
	{
		final ApbContactUsEmailProcessModel contactUsEmailProcessModel = (ApbContactUsEmailProcessModel) getBusinessProcessService()
				.createProcess("contactUsEmailProcess-" + apbContactUsEvent.getContactUsQueryEmail().getCode() + "-"
						+ System.currentTimeMillis(), "contactUsEmailProcess");
		contactUsEmailProcessModel.setSite(apbContactUsEvent.getSite());
		contactUsEmailProcessModel.setContactUsQueryEmail(apbContactUsEvent.getContactUsQueryEmail());
		contactUsEmailProcessModel.setLanguage(apbContactUsEvent.getLanguage());
		contactUsEmailProcessModel.setCurrency(apbContactUsEvent.getCurrency());
		contactUsEmailProcessModel.setStore(apbContactUsEvent.getBaseStore());
		contactUsEmailProcessModel.setCustomer(apbContactUsEvent.getCustomer());
		getModelService().save(contactUsEmailProcessModel);
		getBusinessProcessService().startProcess(contactUsEmailProcessModel);
	}

	@Override
	protected SiteChannel getSiteChannelForEvent(final ApbContactUsEvent apbContactUsEvent)
	{
		final BaseSiteModel site = apbContactUsEvent.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}
}
