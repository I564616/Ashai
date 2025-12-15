package com.apb.core.service.sam.invoice.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.List;

import jakarta.annotation.Resource;

import com.apb.core.dao.sam.invoice.AsahiSAMInvoiceDao;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;


/**
 * The Class AsahiSAMInvoiceServiceImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMInvoiceServiceImpl implements AsahiSAMInvoiceService
{

	/** The asahi SAM invoice dao. */
	@Resource
	private AsahiSAMInvoiceDao asahiSAMInvoiceDao;

	/**
	 * Gets the invoice by document number.
	 *
	 * @param documentNumber
	 *           the document number
	 * @return the invoice by document number
	 */
	@Override
	public AsahiSAMInvoiceModel getInvoiceByDocumentNumber(final String documentNumber, final String lineNumber)
	{
		return this.asahiSAMInvoiceDao.getInvoiceByDocumentNumber(documentNumber, lineNumber);
	}

	/**
	 * Gets All the invoice for the invoice listing.
	 *
	 * @param status
	 *           the status of invoice
	 * @param uid
	 *           the b2bunit uid
	 *
	 * @return the List of Invoices
	 */
	@Override
	public List<AsahiSAMInvoiceModel> getSAMInvoiceList(final String status, final String payerUid,
			final PageableData pageableData, final String documentType, final String dueStatus, final String keyword,
			final String cofoDate)
	{
		return this.asahiSAMInvoiceDao.getSamInvoiceList(status, payerUid, pageableData, documentType, dueStatus, keyword,
				cofoDate);
	}

	/**
	 * Gets Invoice Count for the invoice listing.
	 *
	 * @param status
	 *           the status of invoice
	 * @param uid
	 *           the b2bunit uid
	 *
	 * @return the Invoices count
	 */
	@Override
	public Integer getSAMInvoiceCount(final String status, final String payerUid, final String documentType,
			final String dueStatus, final String keyword, final String cofoDate)
	{
		return this.asahiSAMInvoiceDao.getSAMInvoiceCount(status, payerUid, documentType, dueStatus, keyword, cofoDate);
	}

	/**
	 * Gets Invoice Sum for the invoice listing.
	 *
	 * @param status
	 *           the status of invoice
	 * @param uid
	 *           the b2bunit uid
	 *
	 * @return the Invoices sum
	 */
	@Override
	public Double getSAMInvoiceSum(final String status, final String payerUid, final String documentType, final String dueStatus,
			final String keyword, final String cofoDate)
	{
		return this.asahiSAMInvoiceDao.getSAMInvoiceSum(status, payerUid, documentType, dueStatus, keyword, cofoDate);
	}

	/*
	 * This method will fetch all the due now invoices
	 *
	 * @see com.apb.core.service.sam.invoice.AsahiSAMInvoiceService#getAllDueNowOpenInvoices(java.lang.String)
	 */
	@Override
	public List<AsahiSAMInvoiceModel> getAllDueNowOpenInvoices(final String puid)
	{
		return this.asahiSAMInvoiceDao.getAllDueNowOpenInvoices(puid);
	}

	/**
	 * Gets the invoice by document number.
	 *
	 * @param deliveryNumber
	 *           the delivery number
	 * @return the invoice by delivery number
	 */
	@Override
	public AsahiSAMInvoiceModel getInvoiceByDeliveryNumber(final String deliveryNumber)
	{
		return this.asahiSAMInvoiceDao.getInvoiceByDeliveryNumber(deliveryNumber);
	}
}
