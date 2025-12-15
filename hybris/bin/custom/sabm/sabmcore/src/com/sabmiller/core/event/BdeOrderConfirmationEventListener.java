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
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


/**
 * Listener for order confirmation events.
 */
public class BdeOrderConfirmationEventListener extends AbstractSiteEventListener<BdeOrderPlacedEvent>
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected void onSiteEvent(final BdeOrderPlacedEvent bdeOrderPlacedEvent)
	{
		final OrderModel orderModel = bdeOrderPlacedEvent.getProcess().getOrder();
		final OrderProcessModel bdeOrderUserEmailProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
				"orderConfirmationEmailProcess-" + orderModel.getCode() + "-bdeusers-" + System.currentTimeMillis(),
				"orderConfirmationEmailProcess");
		bdeOrderUserEmailProcessModel.setOrder(orderModel);
		//bdeOrderUserEmailProcessModel.setToEmails(orderModel.getBdeOrderUserEmails());
		final Set<String> uniqueEmailIds = orderModel.getBdeOrderUserEmails().stream().collect(Collectors.toSet());
		bdeOrderUserEmailProcessModel.setToEmails(uniqueEmailIds.stream().collect(Collectors.toList()));
		bdeOrderUserEmailProcessModel.setBdeOrderEmailGroup("bdeusers");
		getModelService().save(bdeOrderUserEmailProcessModel);
		getBusinessProcessService().startProcess(bdeOrderUserEmailProcessModel);

		final OrderProcessModel bdeOrderCustomerEmailProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
				"orderConfirmationEmailProcess-" + orderModel.getCode() + "-bdecustomers-" + System.currentTimeMillis(),
				"orderConfirmationEmailProcess");
		bdeOrderCustomerEmailProcessModel.setOrder(orderModel);
		bdeOrderCustomerEmailProcessModel.setToEmails(orderModel.getBdeOrderCustomerEmails());
		if (orderModel.getBdeOrderCustomerFirstName() != null && StringUtils.isNotEmpty(orderModel.getBdeOrderCustomerFirstName()))
		{
			bdeOrderCustomerEmailProcessModel.setBdeOrderCustomerFirstName("Hi " + orderModel.getBdeOrderCustomerFirstName() + ",");
		}
		else
		{
			bdeOrderCustomerEmailProcessModel.setBdeOrderCustomerFirstName("Hi,");
		}
		bdeOrderCustomerEmailProcessModel.setBdeOrderEmailGroup("customers");
		getModelService().save(bdeOrderCustomerEmailProcessModel);
		getBusinessProcessService().startProcess(bdeOrderCustomerEmailProcessModel);


	}

	@Override
	protected boolean shouldHandleEvent(final BdeOrderPlacedEvent event)
	{
		final OrderModel order = event.getProcess().getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
		final BaseSiteModel site = order.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return SiteChannel.B2C.equals(site.getChannel());
	}
}
