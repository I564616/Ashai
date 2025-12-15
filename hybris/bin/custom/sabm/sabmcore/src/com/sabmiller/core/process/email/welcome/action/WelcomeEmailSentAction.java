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
package com.sabmiller.core.process.email.welcome.action;

import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Date;


/**
 * A process action to remove emails that were sent successfully.
 */
public class WelcomeEmailSentAction extends AbstractProceduralAction
{
	@Override
	public void executeAction(final BusinessProcessModel businessProcessModel)
	{
		final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = (StoreFrontCustomerProcessModel) businessProcessModel;

		for (final EmailMessageModel emailMessage : storeFrontCustomerProcessModel.getEmails())
		{
			if (emailMessage.isSent())
			{
				getModelService().remove(emailMessage);
				final CustomerModel customer = storeFrontCustomerProcessModel.getCustomer();
				customer.setWelcomeEmailStatus(Boolean.TRUE);
				customer.setWelcomeEmailSentDate(new Date());
				getModelService().save(customer);
			}
		}
	}
}
