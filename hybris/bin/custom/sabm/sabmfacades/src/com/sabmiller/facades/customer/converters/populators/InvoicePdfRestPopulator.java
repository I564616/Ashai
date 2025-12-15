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
package com.sabmiller.facades.customer.converters.populators;

import de.hybris.platform.converters.Populator;

import org.springframework.util.Assert;

import com.sabmiller.facades.invoice.SABMInvoicePDFData;
import com.sabmiller.integration.sap.invoices.pdf.response.InvoiceDataResponse.InvoiceData;


public class InvoicePdfRestPopulator implements Populator<InvoiceData, SABMInvoicePDFData>
{

	@Override
	public void populate(final InvoiceData source, final SABMInvoicePDFData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setBinaryData(source.getBinaryData());
		target.setDocumentNumber(source.getDocumentNumber());
	}
}
