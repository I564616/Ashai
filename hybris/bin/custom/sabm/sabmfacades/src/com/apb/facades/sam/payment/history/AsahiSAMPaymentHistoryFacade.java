package com.apb.facades.sam.payment.history;

import java.util.List;

import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * The Interface AsahiSAMPaymentHistoryFacade.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMPaymentHistoryFacade {

	/**
	 * Import payment history.
	 *
	 * @param asahiSAMPaymentData the payment hist
	 */
	void importPaymentHistory(AsahiSAMInvoiceData asahiSAMPaymentData);
	
	/**
	 * Get the payment records for current session user
	 * @param pageableData
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @return list
	 */
	List<AsahiSAMPaymentData> getPaymentRecords(final PageableData pageableData, final String fromDate, final String toDate, final String searchKeyword);
	
	/**
	 * Get the total count of records
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @return list
	 */
	int getPaymentRecordsCount(final String fromDate, final String toDate, final String searchKeyword);

	/**
	 * Save direct debit.
	 *
	 * @param directDebitdata the direct debitdata
	 * @return the string
	 */
	AsahiDirectDebitData saveDirectDebit(AsahiDirectDebitData directDebitdata);

	/**
	 * Gets the direct debit entry for user.
	 *
	 * @param payer the payer
	 * @return the direct debit entry for user
	 */
	AsahiDirectDebitData getDirectDebitEntryForUser(String payer);
}
