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
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * The class works as a Listener for "Assisted forgotten password" 
 * functionality event for the backoffice user.
 */
public class AsahiAssistForgottenPwdEventListener extends AbstractAcceleratorSiteEventListener<AsahiAssistedForgottenPwdEvent>
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

	/**
     *  <p>This method publish the event to sent the reset password email to the user.
     *  The Backoffice admin once create the portal user, it will trigger the reset password
     *  email. </p>
     *  @param     event - The Assisted Forgotton password event
     *  @see       {@link #onSiteEvent(event)}
     */

	@Override
	protected void onSiteEvent(final AsahiAssistedForgottenPwdEvent event)
	{
		final ForgottenPasswordProcessModel assistedForgottenPasswordProcessModel = (ForgottenPasswordProcessModel) getBusinessProcessService()
				.createProcess("assistedForgottenPassword-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						"assistedForgottenPasswordEmailProcess");
		assistedForgottenPasswordProcessModel.setSite(event.getSite());
		assistedForgottenPasswordProcessModel.setCustomer(event.getCustomer());
		assistedForgottenPasswordProcessModel.setToken(event.getToken());
		assistedForgottenPasswordProcessModel.setLanguage(event.getLanguage());
		assistedForgottenPasswordProcessModel.setCurrency(event.getBaseStore().getDefaultCurrency());
		assistedForgottenPasswordProcessModel.setStore(event.getBaseStore());
		getModelService().save(assistedForgottenPasswordProcessModel);
		getBusinessProcessService().startProcess(assistedForgottenPasswordProcessModel);
	}

	@Override
	protected SiteChannel getSiteChannelForEvent(final AsahiAssistedForgottenPwdEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.site", site);
		return site.getChannel();
	}
}
