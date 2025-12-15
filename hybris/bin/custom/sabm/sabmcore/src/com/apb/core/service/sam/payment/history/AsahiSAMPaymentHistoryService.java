package com.apb.core.service.sam.payment.history;

import java.util.List;

import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;


/**
 * The Interface AsahiSAMPaymentHistoryService.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMPaymentHistoryService {

	/**
	 * Gets the payment history by clr doc number.
	 *
	 * @param receiptNumber the receipt number
	 * @return the payment history by clr doc number
	 */
	AsahiSAMPaymentModel getPaymentHistoryByClrDocNumber(final String receiptNumber);

	/**
	 * Fetch user's payment records based on unit
	 * 
	 * @param uid
	 * @param pageableData
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @return paymentrecords
	 */
	List<AsahiSAMPaymentModel> getPaymentRecords(final String uid, PageableData pageableData, final String fromDate, final String toDate, final String searchKeyword);

	/**
	 * Get the total count
	 * @param uid
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @return paymentrecords
	 */
	int getPaymentRecordsCount(final String uid, final String fromDate, final String toDate, final String searchKeyword);

	/**
	 * Gets the direct debit entry for user.
	 *
	 * @param payer the payer
	 * @return the direct debit entry for user
	 */
	AsahiSAMDirectDebitModel getDirectDebitEntryForUser(String payer);
}
