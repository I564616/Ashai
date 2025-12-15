/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Listener for customer registration events.
 */
public class WelcomeEmailEventListener extends AbstractSiteEventListener<WelcomeEmailEvent>
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
	protected void onSiteEvent(final WelcomeEmailEvent welcomeEmailEvent)
	{
		final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = (StoreFrontCustomerProcessModel) getBusinessProcessService()
				.createProcess(
						"customerWelcomeEmailProcess-" + welcomeEmailEvent.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						"customerWelcomeEmailProcess");
		storeFrontCustomerProcessModel.setSite(welcomeEmailEvent.getSite());
		storeFrontCustomerProcessModel.setCustomer(welcomeEmailEvent.getCustomer());
		storeFrontCustomerProcessModel.setLanguage(welcomeEmailEvent.getLanguage());
		storeFrontCustomerProcessModel.setCurrency(welcomeEmailEvent.getCurrency());
		storeFrontCustomerProcessModel.setStore(welcomeEmailEvent.getBaseStore());
		getModelService().save(storeFrontCustomerProcessModel);
		getBusinessProcessService().startProcess(storeFrontCustomerProcessModel);
	}

	@Override
	protected boolean shouldHandleEvent(final WelcomeEmailEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.customer.site", site);
		return true;
	}
}
