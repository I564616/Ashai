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
import de.hybris.platform.commerceservices.model.process.AsahiPayerAccessProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;


/**
 * The class works as a Listener for "Assisted forgotten password" functionality event for the backoffice user.
 */
public class AsahiPayerAccessEventListener extends AbstractAcceleratorSiteEventListener<AsahiPayerAccessEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Resource
	private UserService userService;


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
	 * <p>
	 * This method publish the event to sent the reset password email to the user. The Backoffice admin once create the
	 * portal user, it will trigger the reset password email.
	 * </p>
	 *
	 * @param event
	 *           - The Assisted Forgotton password event
	 * @see {@link #onSiteEvent(event)}
	 */

	@Override
	protected void onSiteEvent(final AsahiPayerAccessEvent event)
	{
		final String notifyType = event.getEmailType();
		String businessProcess = StringUtils.EMPTY;
		if (notifyType.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_REQUEST))
		{
			businessProcess = ApbCoreConstants.PAYER_ACCESS_REQUEST_EMAIL_PROCESS;
		}
		else if (notifyType.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_APPROVE))
		{
			businessProcess = ApbCoreConstants.PAYER_ACCESS_APPROVE_EMAIL_PROCESS;
		}
		else if (notifyType.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_REJECT))
		{
			businessProcess = ApbCoreConstants.PAYER_ACCESS_REJECT_EMAIL_PROCESS;
		}
		else if (notifyType.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_EXPIRED))
		{
			businessProcess = ApbCoreConstants.PAYER_ACCESS_EXPIRED_EMAIL_PROCESS;
		}
		else if (notifyType.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_SUPERUSER_REQUEST))
		{
			businessProcess = ApbCoreConstants.PAYER_ACCESS_SUPERUSER_REQUEST_EMAIL_PROCESS;
		}
		final AsahiPayerAccessProcessModel payerAccessProcessModel = (AsahiPayerAccessProcessModel) getBusinessProcessService()
				.createProcess(businessProcess + "-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						businessProcess);
		payerAccessProcessModel.setSite(event.getSite());
		payerAccessProcessModel.setLanguage(event.getLanguage());
		payerAccessProcessModel.setCurrency(event.getBaseStore().getDefaultCurrency());
		payerAccessProcessModel.setStore(event.getBaseStore());
		payerAccessProcessModel.setCustomer(event.getCustomer());
		payerAccessProcessModel.setPayAccess(event.getAccess());
		payerAccessProcessModel.setUser(userService.getCurrentUser());
		getModelService().save(payerAccessProcessModel);
		getBusinessProcessService().startProcess(payerAccessProcessModel);
	}

	@Override
	protected SiteChannel getSiteChannelForEvent(final AsahiPayerAccessEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.site", site);
		return site.getChannel();
	}
}
