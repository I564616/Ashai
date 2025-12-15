package com.apb.core.dao.sam.payment.history;

import java.util.List;

import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * The Interface AsahiSAMPaymentHistoryDao.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMPaymentHistoryDao {

	/**
	 * Gets the payment history by clr doc number.
	 *
	 * @param clrDocNumber the clr doc number
	 * @return the payment history by clr doc number
	 */
	AsahiSAMPaymentModel getPaymentHistoryByClrDocNumber(String clrDocNumber);
	
	/**
	 * fetches payment history records
	 * @param uid
	 * @param pageableData
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @return list
	 */
	List<AsahiSAMPaymentModel> getPaymentRecords(final String uid , final PageableData pageableData, final String fromDate, final String toDate, final String searchKeyword);
	
	/**
	 * Get the total count of payment records for unit
	 * @param uid
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @return list
	 */
	int getPaymentRecordsCount(final String uid, final String fromDate, final String toDate, final String searchKeyword);

	/**
	 * Find direct debit entry for user.
	 *
	 * @param payer the payer
	 * @return the asahi SAM direct debit model
	 */
	AsahiSAMDirectDebitModel findDirectDebitEntryForUser(String payer);
}
