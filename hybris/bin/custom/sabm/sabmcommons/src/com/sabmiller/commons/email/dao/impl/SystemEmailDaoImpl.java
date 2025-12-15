/**
 *
 */
package com.sabmiller.commons.email.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.email.dao.SystemEmailDao;
import com.sabmiller.commons.model.SystemEmailMessageModel;


/**
 * The dao of system email
 *
 */
public class SystemEmailDaoImpl extends DefaultGenericDao<SystemEmailMessageModel> implements SystemEmailDao
{
	private static final Logger LOG = LoggerFactory.getLogger(SystemEmailDaoImpl.class);

	private static final String DATE_FORMAT_ISO8601_EXTENDED_DATETIME_WITH_MILLIS = "yyyy-MM-dd'T'HH:mm:ss:S";

	/** The Constant QUERY_FETCH_UNSENT_EMAILS_OLDER_THAN_MINUTE. */
	private static final String QUERY_FETCH_UNSENT_EMAILS_OLDER_THAN_MINUTE = "SELECT {m:pk} FROM {SystemEmailMessage AS m} WHERE {m:sent}=?sent and CONVERT(VARCHAR(23),{m:creationtime},126)<=?creationtime";


	public SystemEmailDaoImpl()
	{
		super(SystemEmailMessageModel._TYPECODE);
	}

	/**
	 * Finds all Unsent System Emails.
	 *
	 * @param sent
	 *           flag to search for System Emails.
	 * @return a list of System Emails
	 */
	@Override
	public List<SystemEmailMessageModel> findSystemEmailsBySentStatus(final boolean sent)
	{
		final Map parameterMap = new HashMap();
		parameterMap.put(SystemEmailMessageModel.SENT, Boolean.valueOf(sent));
		//		INC0681430: Duplicate Order Confirmation emails
		/*
		 * System Email cronjob is picking the Message before it deleting from the business processes, hence not
		 * considering the recent messages which are having age less than a minute.
		 */
		final Calendar cal = Calendar.getInstance();
		//		LOG.info("Current Time::" + dateFormat.format(cal.getTime()));
		//		LOG.info("Current Time::" + cal.getTime().toString());
		cal.add(Calendar.MINUTE, -1);
		final String date = DateFormatUtils.format(cal.getTime(),DATE_FORMAT_ISO8601_EXTENDED_DATETIME_WITH_MILLIS);
		//		LOG.info("1 Minute back::" + date);
		//		LOG.info("1 Minute back::" + cal.getTime().toString());
		parameterMap.put("creationtime", date);
		parameterMap.put("modifiedtime", date);
		//		final List<SystemEmailMessageModel> systemEmailMessages = find(parameterMap);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_FETCH_UNSENT_EMAILS_OLDER_THAN_MINUTE);
		query.addQueryParameters(parameterMap);
		LOG.info("Flexi Query::" + query.getQuery());
		final List<SystemEmailMessageModel> systemEmailMessages = (List<SystemEmailMessageModel>) (Object) getFlexibleSearchService()
				.search(query).getResult();
		LOG.info("Unsent Emails which are having age more than a minute ::" + systemEmailMessages.size());
		return Collections.unmodifiableList(systemEmailMessages);
	}
}
