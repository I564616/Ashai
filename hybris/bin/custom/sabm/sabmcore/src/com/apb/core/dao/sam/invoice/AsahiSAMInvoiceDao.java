package com.apb.core.dao.sam.invoice;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;


/**
 * The Interface AsahiSAMInvoiceDao.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMInvoiceDao
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
	 * Gets the invoice data for the invoice listing.
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
	List<AsahiSAMInvoiceModel> getSamInvoiceList(final String status, final String payerUid, final PageableData pageableData,
			final String documentType, final String dueStatus, final String keyword, final String cofoDate);

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
	 * This Method will fetch all the open due now invoices
	 *
	 * @param uid
	 * @param puid
	 * @return
	 */
	List<AsahiSAMInvoiceModel> getAllDueNowOpenInvoices(String puid);

	/**
	 * Gets the invoicet based on date.
	 *
	 * @param startDate
	 *           the start date
	 * @param currentDate
	 *           the current date
	 * @return the invoicet based on date
	 */
	List<AsahiSAMInvoiceModel> getInvoiceBasedOnDate(Date startDate, Date currentDate);

	/**
	 * Gets the invoice payment based on date.
	 *
	 * @param currentDate
	 *           the current date
	 * @param currentDate2
	 *           the current date 2
	 * @return the invoice payment based on date
	 */
	List<AsahiSAMPaymentModel> getInvoicePaymentBasedOnDate(Date startDate, Date currentDate);

	/**
	 * Gets the invoice by delivery number.
	 *
	 * @param deliveryNumber
	 *           the delivery number
	 * @return the invoice by delivery number
	 */
	AsahiSAMInvoiceModel getInvoiceByDeliveryNumber(String deliveryNumber);
}
