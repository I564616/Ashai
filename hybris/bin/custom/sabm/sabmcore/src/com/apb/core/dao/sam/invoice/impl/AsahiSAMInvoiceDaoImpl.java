package com.apb.core.dao.sam.invoice.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.dao.sam.invoice.AsahiSAMInvoiceDao;
import com.apb.core.service.config.AsahiConfigurationService;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;


/**
 * The Class AsahiSAMInvoiceDaoImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMInvoiceDaoImpl extends AbstractItemDao implements AsahiSAMInvoiceDao
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMInvoiceDaoImpl.class);

	/** The Constant DOCUMENT_NUMBER. */
	private static final String DOCUMENT_NUMBER = "documentNumber";

	/** The Constant DELIVERY_NUMBER. */
	private static final String DELIVERY_NUMBER = "deliveryNumber";

	/** The Constant DOCUMENT_NUMBER. */
	private static final String B2BUNIT_UID = "uid";

	/** The Constant INVOICE_STATUS. */
	private static final String INVOICE_STATUS = "status";

	/** The Constant INVOICE_STATUS_OPEN. */
	private static final String INVOICE_STATUS_OPEN = "open";

	/** The Constant INVOICE_STATUS_CLOSED. */
	private static final String INVOICE_STATUS_CLOSED = "closed";

	/** The Constant DOCUMENT_TYPE. */
	private static final String DOCUMENT_TYPE = "documentType";

	/** The Constant DOCUMENT_TYPE. */
	private static final String DOCUMENT_TYPE_CREDIT = "credit";

	/** The Constant DOCUMENT_TYPE. */
	private static final String DOCUMENT_TYPE_INVOICE = "invoice";

	/** The Constant DOCUMENT_TYPE_PAYMENT. */
	private static final String DOCUMENT_TYPE_PAYMENT = "payment";

	/** The Constant DUE_NOW. */
	private static final String DUE_NOW = "dueNow";

	/** The Constant NOT_YET_DUE. */
	private static final String NOT_YET_DUE = "notYetDue";

	private static final String INVOICE_START_DATE = "previousDate";

	/** The Constant CURRENT_DATE. */
	private static final String CURRENT_DATE = "currentDate";

	/** The Constant KEYWORD. */
	private static final String KEYWORD = "keyword";

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The Constant INVOICE_STATUS. */
	private static final String DATE = "date";

	private static final String INVOICE_STATUS_CODE = "statusCode";

	private static String OPEN_INOICE_CODE = "11";

	private static final String CLOSED_INVOICE_STATUSCODE = "10";

	private static final String ASAHI_SAM_DOCUMENTTYPE_CREDIT = "10";

	private static final String ASAHI_SAM_DOCUMENTTYPE_INVOICE = "11";

	/** The Constant DOCUMENT_LINE_NUMBER. */
	private static final String DOCUMENT_LINE_NUMBER = "lineNumber";

	/** The search restriction service. */
	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

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
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_INVOICE_BY_DOCUMENT_NUMBER);
		params.put(DOCUMENT_NUMBER, documentNumber);
		params.put(DOCUMENT_LINE_NUMBER, lineNumber);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AsahiSAMInvoiceModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
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
	 *
	 *         Method IN DEV.
	 */
	@Override
	public List<AsahiSAMInvoiceModel> getSamInvoiceList(final String status, final String payerUid,
			final PageableData pageableData, final String documentType, final String dueStatus, final String keyword,
			final String cofoDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(
				null != cofoDate || StringUtils.isNotEmpty(cofoDate) ? ApbQueryConstant.GET_SAM_INVOICE_DATA_COFODATE
						: ApbQueryConstant.GET_SAM_INVOICE_DATA);

		appendSAMInvoiceBuilderParams(status, payerUid, documentType, dueStatus, keyword, params, builder, false, cofoDate);

		builder.append(ApbQueryConstant.GET_INVOICE_ORDERBY_INVOICEDATE);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		query.setStart(((pageableData.getCurrentPage()) * pageableData.getPageSize()));
		query.setCount(pageableData.getPageSize());

		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<AsahiSAMInvoiceModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();

		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
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
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(
				null != cofoDate || StringUtils.isNotEmpty(cofoDate) ? ApbQueryConstant.GET_SAM_INVOICE_COUNT_COFODATE
						: ApbQueryConstant.GET_SAM_INVOICE_COUNT);

		appendSAMInvoiceBuilderParams(status, payerUid, documentType, dueStatus, keyword, params, builder, false, cofoDate);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		query.setResultClassList(Collections.singletonList(Integer.class));
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<Integer> searchResult = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();

		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			final Integer invoiceCount = searchResult.getResult().get(0);
			if (null != invoiceCount)
			{
				return invoiceCount;
			}
		}
		return 0;
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
	public Double getSAMInvoiceSum(final String status, final String payerUid, final String documentType, final String dueStatus,
			final String keyword, final String cofoDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();

		final StringBuilder builder = new StringBuilder(
				null != cofoDate || StringUtils.isNotEmpty(cofoDate) ? ApbQueryConstant.GET_SAM_INVOICE_SUM_COFODATE
						: ApbQueryConstant.GET_SAM_INVOICE_SUM);

		appendSAMInvoiceBuilderParams(status, payerUid, documentType, dueStatus, keyword, params, builder, true, cofoDate);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		query.setResultClassList(Collections.singletonList(String.class));
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<String> searchResult = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();

		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			final List<String> invoicesAmount = searchResult.getResult();
			BigDecimal sumInvoice = BigDecimal.ZERO;
			for (final String invoiceAmount : invoicesAmount)
			{
				if (StringUtils.isNotBlank(invoiceAmount))
				{
					try
					{
						sumInvoice = sumInvoice.add(BigDecimal.valueOf(Double.parseDouble(invoiceAmount)));
					}
					catch (final NumberFormatException ex)
					{
						logger.error("Cannot parse the invoice sum for value " + invoiceAmount + " payer id is :" + payerUid);
					}
				}
			}

			return sumInvoice.doubleValue();

		}
		return 0.0;
	}

	/**
	 * @param status
	 * @param payerUid
	 * @param documentType
	 * @param dueStatus
	 * @param keyword
	 * @param params
	 * @param builder
	 *
	 *           This method adds corresponding parameters to the String builder.
	 */
	private void appendSAMInvoiceBuilderParams(final String status, final String payerUid, final String documentType,
			final String dueStatus, final String keyword, final Map<String, Object> params, final StringBuilder builder,
			final boolean paymentMade, final String cofoDate)
	{
		params.put(B2BUNIT_UID, payerUid);
		int invoiceHistoryYearFrame = 2;
		if (INVOICE_STATUS_OPEN.equalsIgnoreCase(status))
		{
			params.put(INVOICE_STATUS, OPEN_INOICE_CODE);
			builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_STATUS);
			if (paymentMade)
			{
				builder.append(ApbQueryConstant.APPEND_INVOICE_PAYMENT_TYPE);
			}


			invoiceHistoryYearFrame = Integer
					.parseInt(asahiConfigurationService.getString(ApbCoreConstants.SAM_OPEN_INVOICE_HISTORY_TIMEFRAME, "2"));
		}
		else if (INVOICE_STATUS_CLOSED.equalsIgnoreCase(status))
		{
			params.put(INVOICE_STATUS, CLOSED_INVOICE_STATUSCODE);
			builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_STATUS);

			invoiceHistoryYearFrame = Integer
					.parseInt(asahiConfigurationService.getString(ApbCoreConstants.SAM_CLOSED_INVOICE_HISTORY_TIMEFRAME, "2"));
		}
		else if (DOCUMENT_TYPE_PAYMENT.equalsIgnoreCase(status))
		{
			params.put(INVOICE_STATUS, OPEN_INOICE_CODE);
			params.put("docType", "12");
			builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_STATUS).append(ApbQueryConstant.APPEND_INVOICE_PAYMENT_TYPE)
					.append(ApbQueryConstant.APPEND_INVOICE_PAYMENT_TYPE2);

			invoiceHistoryYearFrame = Integer
					.parseInt(asahiConfigurationService.getString(ApbCoreConstants.SAM_OPEN_INVOICE_HISTORY_TIMEFRAME, "2"));
		}



		final int currentYear = Year.now().getValue();
		final DateFormat formatter = new SimpleDateFormat(ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN);

		if (cofoDate == null)
		{
			final String finYearStartDDMM = asahiConfigurationService.getString(ApbCoreConstants.SAM_INVOICE_FINANCIALYEAR_START,
					"01/07");

			try
			{
				if (formatter.parse(finYearStartDDMM + "/" + Integer.toString(currentYear)).after(new Date()))
				{
					invoiceHistoryYearFrame += 1;
				}

				params.put(INVOICE_START_DATE,
						formatter.parse(finYearStartDDMM + "/" + Integer.toString(currentYear - invoiceHistoryYearFrame)));
			}
			catch (final ParseException e)
			{
				logger.error("Parse Exception caught in converting invoice date pattern" + e.getMessage());
			}
		}

		else
		{
			final String strDate = cofoDate.replace("-", "/");
			logger.info("cofo date is {} and strDate is ", cofoDate, strDate);
			final String[] datearray = strDate.split("/");
			final String finYearStartDDMM = datearray[0] + "/" + datearray[1];
			final String yyyy = datearray[2];

			try
			{
				params.put(INVOICE_START_DATE, formatter.parse(finYearStartDDMM + "/" + yyyy));

			}
			catch (final ParseException e)
			{
				logger.error("Parse Exception caught in converting invoice date pattern" + e.getMessage());
			}
		}


		if (DOCUMENT_TYPE_CREDIT.equalsIgnoreCase(documentType))
		{
			params.put(DOCUMENT_TYPE, ASAHI_SAM_DOCUMENTTYPE_CREDIT);
			builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_DOCUMENTTYPE);
		}
		else if (DOCUMENT_TYPE_INVOICE.equalsIgnoreCase(documentType))
		{
			params.put(DOCUMENT_TYPE, ASAHI_SAM_DOCUMENTTYPE_INVOICE);
			builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_DOCUMENTTYPE);
		}
		else if (DOCUMENT_TYPE_PAYMENT.equalsIgnoreCase(documentType))
		{
			params.put(DOCUMENT_TYPE, "12");
			builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_DOCUMENTTYPE);
		}

		if (StringUtils.isNotEmpty(dueStatus))
		{

			final Calendar date = new GregorianCalendar();
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);

			date.add(Calendar.DAY_OF_MONTH, 1);

			params.put(CURRENT_DATE, date.getTime());

			if (DUE_NOW.equalsIgnoreCase(dueStatus))
			{
				builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_DUENOW);
			}
			else if (NOT_YET_DUE.equalsIgnoreCase(dueStatus))
			{
				builder.append(ApbQueryConstant.APPEND_INVOICE_DATA_NOTYETDUE);
			}
		}

		if (StringUtils.isNotEmpty(keyword))
		{
			params.put(KEYWORD, "%" + keyword.toUpperCase() + "%");
			builder.append(ApbQueryConstant.APPEND_INVOICE_KEYWORD);
		}
	}

	/*
	 * Method will fetch All Due Now invoices based on todays date and status.
	 *
	 * @see com.apb.core.dao.sam.invoice.AsahiSAMInvoiceDao#getAllDueNowInvoicesByStatus(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AsahiSAMInvoiceModel> getAllDueNowOpenInvoices(final String puid)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ALL_DUE_NOW_INVOICES_BY_PAYER_UID);
		params.put(B2BUNIT_UID, puid);
		params.put(INVOICE_STATUS_CODE, OPEN_INOICE_CODE);
		params.put(DATE, new Date());

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<AsahiSAMInvoiceModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	/**
	 * Gets the invoice based on date.
	 *
	 * @param startDate
	 *           the start date
	 * @param currentDate
	 *           the current date
	 * @return the invoice by document number
	 */
	@Override
	public List<AsahiSAMInvoiceModel> getInvoiceBasedOnDate(final Date startDate, final Date currentDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_INVOICE_BASED_ON_DATE);
		params.put("startDate", startDate);
		params.put("currentDate", new Date());

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<AsahiSAMInvoiceModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	/**
	 * Gets the invoice payment based on date.
	 *
	 * @param startDate
	 *           the start date
	 * @param currentDate
	 *           the current date
	 * @return the invoice by document number
	 */
	@Override
	public List<AsahiSAMPaymentModel> getInvoicePaymentBasedOnDate(final Date startDate, final Date currentDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_INVOICE_PAYMENT_BASED_ON_DATE);
		params.put("startDate", startDate);
		params.put("currentDate", new Date());

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<AsahiSAMPaymentModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	/**
	 * Gets the invoice by delivery number.
	 *
	 * @param deliveryNumber
	 *           the delivery number
	 * @return the invoice by delivery number
	 */
	@Override
	public AsahiSAMInvoiceModel getInvoiceByDeliveryNumber(final String deliveryNumber)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_INVOICE_BY_DELIVERY_NUMBER);
		params.put(DELIVERY_NUMBER, deliveryNumber);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AsahiSAMInvoiceModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}
}
