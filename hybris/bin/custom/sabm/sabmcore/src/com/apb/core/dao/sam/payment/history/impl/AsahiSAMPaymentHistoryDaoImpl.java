package com.apb.core.dao.sam.payment.history.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.dao.sam.payment.history.AsahiSAMPaymentHistoryDao;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * The Class AsahiSAMPaymentHistoryDaoImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMPaymentHistoryDaoImpl extends AbstractItemDao implements AsahiSAMPaymentHistoryDao{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(AsahiSAMPaymentHistoryDaoImpl.class);
	/** The Constant CLEARING_DOC_NUMBER. */
	private static final String CLEARING_DOC_NUMBER = "clrDocNumber";

	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	/** The asahi site util. */
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/** The search restriction service. */
	@Resource
	private SearchRestrictionService searchRestrictionService;


	/**
	 * Gets the payment history by receipt number.
	 *
	 * @param clrDocNumber the receipt number
	 * @return the payment history by receipt number
	 */
	public AsahiSAMPaymentModel getPaymentHistoryByClrDocNumber(final String clrDocNumber){
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_PAYMENT_BY_CLEARING_DOC_NUMBER);
		params.put(CLEARING_DOC_NUMBER, clrDocNumber);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AsahiSAMPaymentModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	@Override
	public List<AsahiSAMPaymentModel> getPaymentRecords(final String uid, final PageableData pageableData,
			final String fromDate, final String toDate, final String searchKeyword)
	{
		this.searchRestrictionService.disableSearchRestrictions();
		final StringBuilder stringQuery = new StringBuilder(ApbQueryConstant.GET_SAM_PAYMENT_RECORDS_QUERY);
		final Map<String,Object> params = new HashMap<>();
		params.put("uid", uid);
		List<AsahiSAMPaymentModel> returnList = new ArrayList<>();
		try
		{
   		appendQueryAndPopulateParams(stringQuery,params, fromDate,toDate,searchKeyword);
         final FlexibleSearchQuery query = new FlexibleSearchQuery(stringQuery);
         query.addQueryParameters(params);
   		query.setStart(((pageableData.getCurrentPage()) * pageableData.getPageSize()));
   		query.setCount(pageableData.getPageSize());
   		final SearchResult<AsahiSAMPaymentModel> result = getFlexibleSearchService().search(query);
   		returnList = result.getResult();
   		LOG.info("Payment records query " + query + " return list : " + returnList );
		}
		catch (final Exception e)
		{
			LOG.error("Exception while executing payment records query " + e.getMessage());
		}
		this.searchRestrictionService.enableSearchRestrictions();
		return returnList;
	}

	@Override
	public int getPaymentRecordsCount(final String uid,
			final String fromDate, final String toDate, final String searchKeyword)
	{
		this.searchRestrictionService.disableSearchRestrictions();
		final StringBuilder stringQuery = new StringBuilder(ApbQueryConstant.GET_TOTAL_COUNT_SAM_PAYMENT_RECORDS_QUERY);
		final Map<String,Object> params = new HashMap<>();
		params.put("uid", uid);
		int returnValue = 0;
		try {
			appendQueryForCountAndPopulateParams(stringQuery, params, fromDate, toDate, searchKeyword);
         final FlexibleSearchQuery query = new FlexibleSearchQuery(stringQuery);
         query.addQueryParameters(params);
   		final List<Class> resultClassList = new ArrayList<Class>();
   		resultClassList.add(Integer.class);
   		query.setResultClassList(resultClassList);
   		final Integer totalSize = (Integer) getFlexibleSearchService().search(query).getResult().iterator().next();
   		returnValue= totalSize.intValue();
   		LOG.info("Payment records count query " + query +" and return count : " + returnValue );
		}catch(final Exception ex) {
			LOG.error("Exception while executing payment records count query " + ex.getMessage());
		}
		this.searchRestrictionService.enableSearchRestrictions();
		return returnValue;
	}

	/**
	 * Populate params and append where clause
	 *
	 * @param stringQuery
	 * @param params
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @throws ParseException
	 */
	private void appendQueryAndPopulateParams(final StringBuilder stringQuery,final Map<String,Object> params, final String fromDate,
			final String toDate, final String searchKeyword) throws ParseException
	{
		if(!this.asahiConfigurationService.getBoolean("populate.sql.parameter",Boolean.TRUE)) {
			return;
		}

		final String requiredDateFormat = this.asahiConfigurationService.getString(
				ApbCoreConstants.ASAHI_QUERY_DATE_FORMAT_KEY + asahiSiteUtil.getCurrentSite().getUid(),
				ApbCoreConstants.QUERY_DATE_FORMAT);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		if(StringUtils.isNotEmpty(fromDate)) {
			stringQuery.append(" AND {transactionDate}>=?fromDate ");
			params.put("fromDate", dateFormat.parse(fromDate.replaceAll("-", "/")));
		}
      if(StringUtils.isNotEmpty(toDate)) {
			stringQuery.append(" AND {transactionDate}<=?toDate ");
      	params.put("toDate", dateFormat.parse(toDate.replaceAll("-", "/")));
      }
      if(StringUtils.isNotEmpty(searchKeyword)) {
      	stringQuery.append(" AND ({receiptNumber} like '%" + searchKeyword
      			+ "%'  OR {paymentReference} like '%" + searchKeyword
      			+ "%' OR {amount} like '%" + searchKeyword
      			+ "%' OR {clrDocNumber} like '%" + searchKeyword
      			+ "%' OR {paymentType} in ({{ Select {PK} from {AsahiSAMPaymentType} where lower({name}) like '%" + searchKeyword.toLowerCase() + "%' }}))");
      }

      stringQuery.append("GROUP BY {pk},{asp.transactionDate} ORDER BY {asp.transactionDate} desc");
	}

	/**
	 * Populate params and append where clause
	 *
	 * @param stringQuery
	 * @param params
	 * @param fromDate
	 * @param toDate
	 * @param searchKeyword
	 * @throws ParseException
	 */
	private void appendQueryForCountAndPopulateParams(final StringBuilder stringQuery,final Map<String,Object> params, final String fromDate,
			final String toDate, final String searchKeyword) throws ParseException
	{
		if(!this.asahiConfigurationService.getBoolean("populate.sql.parameter",Boolean.TRUE)) {
			return;
		}

		final String requiredDateFormat = this.asahiConfigurationService.getString(
				ApbCoreConstants.ASAHI_QUERY_DATE_FORMAT_KEY + asahiSiteUtil.getCurrentSite().getUid(),
				ApbCoreConstants.QUERY_DATE_FORMAT);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		if(StringUtils.isNotEmpty(fromDate)) {
			stringQuery.append(" AND {transactionDate}>=?fromDate ");
			params.put("fromDate", dateFormat.parse(fromDate.replaceAll("-", "/")));
		}
      if(StringUtils.isNotEmpty(toDate)) {
			stringQuery.append(" AND {transactionDate}<=?toDate ");
      	params.put("toDate", dateFormat.parse(toDate.replaceAll("-", "/")));
      }
      if(StringUtils.isNotEmpty(searchKeyword)) {
      	stringQuery.append(" AND ({receiptNumber} like '%" + searchKeyword
      			+ "%'  OR {paymentReference} like '%" + searchKeyword
      			+ "%' OR {amount} like '%" + searchKeyword
      			+ "%' OR {clrDocNumber} like '%" + searchKeyword
      			+ "%' OR {paymentType} in ({{ Select {PK} from {AsahiSAMPaymentType} where lower({name}) like '%" + searchKeyword.toLowerCase() + "%' }}))");
      }
	}

	/**
	 * Find direct debit entry for user.
	 *
	 * @param payer the payer
	 * @return the asahi SAM direct debit model
	 */
	@Override
	public AsahiSAMDirectDebitModel findDirectDebitEntryForUser(final String payer) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_USER_DIRECT_DEBIT_BY_PAYER);
		params.put("payer", payer);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AsahiSAMDirectDebitModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}
}
