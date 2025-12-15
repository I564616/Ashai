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
import de.hybris.platform.commerceservices.model.process.ApbCompanyDetailsProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Listener for request registration events.
 */
public class ApbCompanyDetailsEventListener extends AbstractAcceleratorSiteEventListener<ApbCompanyDetailsEvent>
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
	protected void onSiteEvent(final ApbCompanyDetailsEvent apbCompanyDetailsEvent)
	{
		final ApbCompanyDetailsProcessModel apbCompanyDetailsProcessModel = (ApbCompanyDetailsProcessModel) getBusinessProcessService()
				.createProcess("apbCompanyDetailsEmailProcess-" + apbCompanyDetailsEvent.getApbCompanyDetailsEmailModel().getCode()
						+ "-" + System.currentTimeMillis(), "apbCompanyDetailsEmailProcess");
		apbCompanyDetailsProcessModel.setSite(apbCompanyDetailsEvent.getSite());
		apbCompanyDetailsProcessModel.setApbCompanyDetailsEmail(apbCompanyDetailsEvent.getApbCompanyDetailsEmailModel());
		apbCompanyDetailsProcessModel.setLanguage(apbCompanyDetailsEvent.getLanguage());
		apbCompanyDetailsProcessModel.setCurrency(apbCompanyDetailsEvent.getCurrency());
		apbCompanyDetailsProcessModel.setStore(apbCompanyDetailsEvent.getBaseStore());
		apbCompanyDetailsProcessModel.setCustomer(apbCompanyDetailsEvent.getCustomer());
		getModelService().save(apbCompanyDetailsProcessModel);
		getBusinessProcessService().startProcess(apbCompanyDetailsProcessModel);
	}

	@Override
	protected SiteChannel getSiteChannelForEvent(final ApbCompanyDetailsEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}
}
