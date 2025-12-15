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
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import com.sabmiller.core.model.InvoicePaymentModel;



/**
 * Registration event, implementation of {@link AbstractCommerceUserEvent}
 */
public class PaymentConfirmationEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	private InvoicePaymentModel invoicePayment;



	/**
	 * @return the invoicePayment
	 */
	public InvoicePaymentModel getInvoicePayment()
	{
		return invoicePayment;
	}



	/**
	 * @param invoicePayment
	 *           the invoicePayment to set
	 */
	public void setInvoicePayment(final InvoicePaymentModel invoicePayment)
	{
		this.invoicePayment = invoicePayment;
	}



	public PaymentConfirmationEmailEvent()
	{
		super();
	}

}
