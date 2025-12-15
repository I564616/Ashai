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
package com.apb.core.actions.quote;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.task.RetryLaterException;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;


/**
 * Checks the Seller approver's response on Quote and transitions the business process into appropriate actions.
 */
public class CheckSellerApproverResponseOnQuoteAction extends AbstractQuoteDecisionAction<QuoteProcessModel>
{
	private QuoteService quoteService;
	private static final Logger LOG = LoggerFactory.getLogger(CheckSellerApproverResponseOnQuoteAction.class);

	@Override
	public Transition executeAction(final QuoteProcessModel process) throws RetryLaterException, Exception
	{
		Transition result;

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("In CheckSellerApproverResponseOnQuoteAction for process code : [%s]", process.getCode()));
		}

		final QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(process.getQuoteCode());

		if (QuoteState.SELLERAPPROVER_APPROVED.equals(quoteModel.getState()))
		{
			result = Transition.OK;
		}
		else if (QuoteState.SELLERAPPROVER_REJECTED.equals(quoteModel.getState()))
		{
			result = Transition.NOK;
		}
		else
		{
			result = Transition.ERROR;
		}

		return result;
	}

	protected QuoteService getQuoteService()
	{
		return quoteService;
	}

	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

}
