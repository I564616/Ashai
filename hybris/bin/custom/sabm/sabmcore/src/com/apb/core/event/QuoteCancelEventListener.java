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


import de.hybris.platform.commerceservices.event.QuoteCancelEvent;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import com.apb.core.constants.ApbCoreConstants;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;


/**
 * Event Listener for {@link QuoteCancelEvent}. This Event Listener starts the quote post cancellation process.
 */
public class QuoteCancelEventListener extends AbstractEventListener<QuoteCancelEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = LoggerFactory.getLogger(QuoteCancelEventListener.class);

	@Override
	protected void onEvent(final QuoteCancelEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received QuoteCancelEvent..");
		}

		final Map<String, Object> contextParams = new HashMap<String, Object>();
		contextParams.put(ApbCoreConstants.QUOTE_USER_TYPE, event.getQuoteUserType());

		final QuoteProcessModel quotePostCancellationProcessModel = (QuoteProcessModel) getBusinessProcessService().createProcess(
				"quotePostCancellationProcess" + "-" + event.getQuote().getCode() + "-" + event.getQuote().getStore().getUid() + "-"
						+ System.currentTimeMillis(), ApbCoreConstants.QUOTE_POST_CANCELLATION_PROCESS, contextParams);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created business process for QuoteCancelEvent. Process code : [%s] ...",
					quotePostCancellationProcessModel.getCode()));
		}

		final QuoteModel quoteModel = event.getQuote();
		quotePostCancellationProcessModel.setQuoteCode(quoteModel.getCode());
		getModelService().save(quotePostCancellationProcessModel);
		//start the business process
		getBusinessProcessService().startProcess(quotePostCancellationProcessModel);

	}

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
}
