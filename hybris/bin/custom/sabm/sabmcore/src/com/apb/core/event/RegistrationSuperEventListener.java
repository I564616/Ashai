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
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.StoreFrontSuperCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Iterator;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * Listener for customer registration events.
 */
public class RegistrationSuperEventListener extends AbstractAcceleratorSiteEventListener<SuperRegisterEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

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

	@Override
	protected void onSiteEvent(final SuperRegisterEvent registerEvent)
	{
		if (asahiSiteUtil.isSga())
		{
			if(registerEvent.getCustomer() instanceof B2BCustomerModel) {
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) registerEvent.getCustomer();
				final Set<AsahiB2BUnitModel> asahiUnits = registerEvent.getAsahiUnits();

				if (CollectionUtils.isNotEmpty(asahiUnits))
				{
					final Iterator<AsahiB2BUnitModel> customerUnitsIterator = asahiUnits.iterator();
					AsahiB2BUnitModel unit = null;
					int counter = 0;
					while (customerUnitsIterator.hasNext())
					{
						unit = customerUnitsIterator.next();
						final StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel = (StoreFrontSuperCustomerProcessModel) getBusinessProcessService()
								.createProcess("customerSuperRegistrationEmailProcess-" + registerEvent.getCustomer().getUid() + "-"
										+ System.currentTimeMillis() + counter, "customerSuperRegistrationEmailProcess");
						storeFrontSuperCustomerProcessModel.setUnit(unit);
						populateAndInitiateProcess(storeFrontSuperCustomerProcessModel, registerEvent);
						counter++;
					}
				}
			}
		}
		else
		{
			final StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel = (StoreFrontSuperCustomerProcessModel) getBusinessProcessService()
					.createProcess("customerSuperRegistrationEmailProcess-" + registerEvent.getCustomer().getUid() + "-"
							+ System.currentTimeMillis(), "customerSuperRegistrationEmailProcess");
			populateAndInitiateProcess(storeFrontSuperCustomerProcessModel, registerEvent);
		}
	}

	private void populateAndInitiateProcess(final StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel,
			final SuperRegisterEvent registerEvent)
	{
		storeFrontSuperCustomerProcessModel.setSite(registerEvent.getSite());
		storeFrontSuperCustomerProcessModel.setCustomer(registerEvent.getCustomer());
		storeFrontSuperCustomerProcessModel.setLanguage(registerEvent.getLanguage());
		storeFrontSuperCustomerProcessModel.setCurrency(registerEvent.getCurrency());
		storeFrontSuperCustomerProcessModel.setStore(registerEvent.getBaseStore());
		storeFrontSuperCustomerProcessModel.setApbEmail(registerEvent.getApbEmail());

		getModelService().save(storeFrontSuperCustomerProcessModel);
		getBusinessProcessService().startProcess(storeFrontSuperCustomerProcessModel);
	}

	@Override
	protected SiteChannel getSiteChannelForEvent(final SuperRegisterEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}
}
