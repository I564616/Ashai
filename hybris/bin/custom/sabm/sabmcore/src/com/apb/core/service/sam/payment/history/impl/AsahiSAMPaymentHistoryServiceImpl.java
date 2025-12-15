package com.apb.core.service.sam.payment.history.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.List;

import jakarta.annotation.Resource;

import com.apb.core.dao.sam.payment.history.AsahiSAMPaymentHistoryDao;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;
import com.apb.core.service.sam.payment.history.AsahiSAMPaymentHistoryService;

/**
 * The Class AsahiSAMPaymentHistoryServiceImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiSAMPaymentHistoryServiceImpl implements AsahiSAMPaymentHistoryService{

	/** The asahi SAM payment history dao. */
	@Resource
	private AsahiSAMPaymentHistoryDao asahiSAMPaymentHistoryDao;
	
	/**
	 * Gets the payment history by clr doc number.
	 *
	 * @param clrDocNumber the clr doc number
	 * @return the payment history by clr doc number
	 */
	@Override
	public AsahiSAMPaymentModel getPaymentHistoryByClrDocNumber(
			String clrDocNumber) {
		return this.asahiSAMPaymentHistoryDao.getPaymentHistoryByClrDocNumber(clrDocNumber);
	}

	@Override
	public List<AsahiSAMPaymentModel> getPaymentRecords(final String uid, final PageableData pageableData, final String fromDate, final String toDate, final String searchKeyword)
	{
		return asahiSAMPaymentHistoryDao.getPaymentRecords(uid, pageableData, fromDate, toDate, searchKeyword);
	}

	@Override
	public int getPaymentRecordsCount(final String uid, final String fromDate, final String toDate, final String searchKeyword)
	{
		return asahiSAMPaymentHistoryDao.getPaymentRecordsCount(uid, fromDate, toDate, searchKeyword);
	}

	/**
	 * Gets the direct debit entry for user.
	 *
	 * @param payer the payer
	 * @return the direct debit entry for user
	 */
	@Override
	public AsahiSAMDirectDebitModel getDirectDebitEntryForUser(String payer) {
		return this.asahiSAMPaymentHistoryDao.findDirectDebitEntryForUser(payer);
	}
	
}
