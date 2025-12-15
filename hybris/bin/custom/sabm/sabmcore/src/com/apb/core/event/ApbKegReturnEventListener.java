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
import de.hybris.platform.commerceservices.model.process.ApbKegReturnEmailProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Listener for Contact Us events.
 */
public class ApbKegReturnEventListener extends AbstractAcceleratorSiteEventListener<ApbKegReturnEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final ApbKegReturnEvent apbKegReturnEvent)
	{
		final ApbKegReturnEmailProcessModel kegReturnEmailProcessModel = (ApbKegReturnEmailProcessModel) getBusinessProcessService()
				.createProcess("kegReturnEmailProcess-" + apbKegReturnEvent.getApbKegReturnEmail().getCode() + "-"
						+ System.currentTimeMillis(), "kegReturnEmailProcess");
		kegReturnEmailProcessModel.setSite(apbKegReturnEvent.getSite());
		kegReturnEmailProcessModel.setApbKegReturnEmail(apbKegReturnEvent.getApbKegReturnEmail());
		kegReturnEmailProcessModel.setLanguage(apbKegReturnEvent.getLanguage());
		kegReturnEmailProcessModel.setCurrency(apbKegReturnEvent.getCurrency());
		kegReturnEmailProcessModel.setStore(apbKegReturnEvent.getBaseStore());
		kegReturnEmailProcessModel.setCustomer(apbKegReturnEvent.getCustomer());
		getModelService().save(kegReturnEmailProcessModel);
		getBusinessProcessService().startProcess(kegReturnEmailProcessModel);
	}

	@Override
	protected SiteChannel getSiteChannelForEvent(final ApbKegReturnEvent apbKegReturnEvent)
	{
		final BaseSiteModel site = apbKegReturnEvent.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 */
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
