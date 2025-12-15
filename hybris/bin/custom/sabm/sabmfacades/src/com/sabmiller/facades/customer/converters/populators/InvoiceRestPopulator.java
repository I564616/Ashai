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
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.sabmiller.core.model.InvoiceTypeModel;
import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.facades.invoice.SABMInvoiceData;
import com.sabmiller.integration.sap.invoices.customer.response.CustomerBillingDataResponse.Invoice;


/**
 * The Class InvoiceRestPopulator.
 */
public class InvoiceRestPopulator implements Populator<Invoice, SABMInvoiceData>
{

	/** The sab formatter util. */
	@Resource(name = "sabFormatterUtil")
	private SABMFormatterUtils formatterUtils;

	/** The sabm invoice type dao. */
	@Resource
	private GenericDao<InvoiceTypeModel> sabmInvoiceTypeDao;

	@Resource(name = "printableBillingTypeInvoice")
	private List<String> printableInvoices;

	/** The date pattern. */
	@Value(value = "${sap.invoice.date.pattern:yyyyMMdd}")
	private String datePatternInput;

	/** The date pattern output. */
	@Value(value = "${hybris.invoice.date.pattern:dd/MM/yy}")
	private String datePatternOutput;

	/** The decimal format. */
	private DecimalFormat decimalFormat;

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init()
	{
		decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setMinimumFractionDigits(2);
		decimalFormat.setGroupingUsed(false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Invoice source, final SABMInvoiceData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setBranch(StringUtils.trimToEmpty(source.getSoldTo()));

		target.setInvoiceNumber(StringUtils.trimToEmpty(source.getInvoiceNumber()));

		if (StringUtils.isNotEmpty(source.getDueDate()))
		{
			final Date parsedDate = formatterUtils.parseDate(source.getDueDate().trim(), datePatternInput);
			target.setDueDate(parsedDate != null ? DateFormatUtils.format(parsedDate, datePatternOutput) : null);
			target.setDueDateStamp(parsedDate != null ? parsedDate.getTime() : 0);
		}
		else
		{
			target.setDueDate(StringUtils.EMPTY);
		}

		if (StringUtils.isNotEmpty(source.getDocumentDate()))
		{
			final Date parsedDate = formatterUtils.parseDate(source.getDocumentDate().trim(), datePatternInput);
			target.setTransactionDate(parsedDate != null ? DateFormatUtils.format(parsedDate, datePatternOutput) : null);
			target.setTransactionDateStamp(parsedDate != null ? parsedDate.getTime() : 0);
		}
		else
		{
			target.setTransactionDate(StringUtils.EMPTY);
		}

		final BigDecimal bigDecimal = formatterUtils.parseSAPNumber(source.getValueLocalCurrency());

		target.setOpenAmount(bigDecimal == null ? StringUtils.EMPTY : decimalFormat.format(bigDecimal));

		target.setOrderNumber(StringUtils.trimToEmpty(source.getSalesOrderNumber()));
		target.setPurchaseOrderNumber(StringUtils.trimToEmpty(source.getPurchaseOrderNumber()));
		target.setStatus(StringUtils.trimToEmpty(source.getStatus()));

		if (StringUtils.isNotEmpty(source.getInvoiceType()))
		{
			final Map<String, Object> params = new HashMap<>();
			params.put(InvoiceTypeModel.ACCOUNTINGTYPE, source.getInvoiceType().trim());
			final List<InvoiceTypeModel> invoiceTypeList = sabmInvoiceTypeDao.find(params);

			if (CollectionUtils.isNotEmpty(invoiceTypeList))
			{
				target.setType(StringUtils.trimToEmpty(invoiceTypeList.get(0).getDisplayText()));
			}

			target.setPrintable(Boolean.FALSE);

			for (final String printableInvoice : printableInvoices)
			{
				if (StringUtils.equalsIgnoreCase(printableInvoice, source.getInvoiceType().trim()))
				{
					target.setPrintable(Boolean.TRUE);
					break;
				}
			}
		}
		else
		{
			target.setType(StringUtils.EMPTY);
		}
	}
}
