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


import static com.apb.core.constants.ApbCoreConstants.QUOTE_TO_EXPIRE_SOON_EMAIL_PROCESS;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import de.hybris.platform.commerceservices.event.QuoteToExpireSoonEvent;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;


/**
 * Event Listener for {@link QuoteToExpireSoonEvent}. This Event Listener starts the quote to expire soon business
 * process.
 */
public class QuoteToExpireSoonEventListener extends AbstractEventListener<QuoteToExpireSoonEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = LoggerFactory.getLogger(QuoteToExpireSoonEventListener.class);

	@Override
	protected void onEvent(final QuoteToExpireSoonEvent event)
	{
		LOG.debug("Received QuoteToExpireSoonEvent..");

		final QuoteProcessModel quoteProcessModel = createQuoteProcessModel(event);

		getModelService().save(quoteProcessModel);

		getBusinessProcessService().startProcess(quoteProcessModel);
	}

	protected QuoteProcessModel createQuoteProcessModel(final QuoteToExpireSoonEvent event)
	{
		final QuoteModel quote = event.getQuote();

		final QuoteProcessModel quoteProcessModel = (QuoteProcessModel) getBusinessProcessService()
				.createProcess(String.format("quoteToExpireSoon-%s-%s-%s", quote.getCode(), quote.getStore().getUid(),
						Long.valueOf(System.currentTimeMillis())), QUOTE_TO_EXPIRE_SOON_EMAIL_PROCESS, Collections.emptyMap());

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created business process for QuoteToExpireSoonEvent. Process code : [%s] ...",
					quoteProcessModel.getCode()));
		}

		quoteProcessModel.setQuoteCode(quote.getCode());

		return quoteProcessModel;
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
