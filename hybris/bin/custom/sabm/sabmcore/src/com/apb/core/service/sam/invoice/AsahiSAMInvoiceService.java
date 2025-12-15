package com.apb.core.service.sam.invoice;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;


/**
 * The Interface AsahiSAMInvoiceService.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMInvoiceService
{

	/**
	 * Gets the invoice by document number.
	 *
	 * @param documentNumber
	 *           the document number
	 * @param lineNumber
	 * @return the invoice by document number
	 */
	AsahiSAMInvoiceModel getInvoiceByDocumentNumber(String documentNumber, String lineNumber);

	/**
	 * Gets All the invoice for the invoice listing.
	 *
	 * @param status
	 *           the status of invoice
	 * @param uid
	 *           the b2bunit uid
	 * @param pageableData
	 * @param documentType
	 * @param dueStatus
	 * @param keyword
	 *
	 * @return the List of Invoices
	 */
	List<AsahiSAMInvoiceModel> getSAMInvoiceList(final String status, final String payerUid, PageableData pageableData, final String documentType, final String dueStatus, final String keyword, final String cofoDate);

	/**
	 * Gets Invoice Count for the invoice listing.
	 *
	 * @param status
	 *           the status of invoice
	 * @param uid
	 *           the b2bunit uid
	 * @param keyword
	 * @param dueStatus
	 * @param documentType
	 *
	 * @return the Invoices count
	 */
	Integer getSAMInvoiceCount(final String status, final String uid, final String documentType, final String dueStatus,
			final String keyword, final String cofoDate);

	/**
	 * Gets Invoice sum for the invoice listing.
	 *
	 * @param status
	 *           the status of invoice
	 * @param uid
	 *           the b2bunit uid
	 * @param keyword
	 * @param dueStatus
	 * @param documentType
	 *
	 * @return the Invoices sum
	 */
	Double getSAMInvoiceSum(final String status, final String uid, final String documentType, final String dueStatus,
			final String keyword, final String cofoDate);

	/**
	 * This method will get all open invoices.
	 *
	 * @param uid
	 * @param uid2
	 * @return
	 */
	List<AsahiSAMInvoiceModel> getAllDueNowOpenInvoices(String puid);

	/**
	 * Gets the invoice by delivery number.
	 *
	 * @param deliveryNumber
	 *           the delivery number
	 * @return the invoice by delivery number
	 */
	AsahiSAMInvoiceModel getInvoiceByDeliveryNumber(String deliveryNumber);

}
