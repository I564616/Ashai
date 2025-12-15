/**
 *
 */
package com.sabmiller.core.b2b.dao;


import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerImportedModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.SabmMessageModel;
import com.sabmiller.core.model.SabmUserMessagesStatusModel;


/**
 * The Class DefaultSabmB2BCustomerDaoImpl.
 */
public class DefaultSabmB2BCustomerDaoImpl extends AbstractItemDao implements SabmB2BCustomerDao
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmB2BCustomerDaoImpl.class.getName());

	/** The Constant QUERY_Customer_BY_UIDS. */
	private static final String QUERY_Customer_BY_UIDS = "SELECT DISTINCT {bc:pk} FROM {" + B2BCustomerModel._TYPECODE
			+ " as bc JOIN PrincipalGroupRelation as pgr on {bc:pk} = {pgr:source} JOIN " + PrincipalGroupModel._TYPECODE
			+ " as pg on {pgr:target} = {pg:pk}} WHERE {pg:uid} in (?orUids)" + " AND {bc:pk} IN ({{SELECT DISTINCT {ibc:pk} FROM {"
			+ B2BCustomerModel._TYPECODE + " as ibc JOIN PrincipalGroupRelation as ipgr on {ibc:pk} = {ipgr:source} JOIN "
			+ PrincipalGroupModel._TYPECODE
			+ " as ipg on {ipgr:target} = {ipg:pk}} WHERE {ipg:uid} IN (?andUids)}}) AND {bc:active}=1";


	/** The Constant QUERY_SIMILAR_CUSTOMER_BY_UID. */
	private static final String QUERY_SIMILAR_CUSTOMER_BY_UID = "SELECT DISTINCT {bc:pk} FROM {" + B2BCustomerModel._TYPECODE
			+ " AS bc} WHERE {bc:" + B2BCustomerModel.UID + "} like ?uid";

	/** The Constant QUERY_SIMILAR_CUSTOMER_BY_UID. */
	private static final String QUERY_SIMILAR_BDE_CUSTOMER_BY_UID = "SELECT DISTINCT {bc:pk} FROM {" + BDECustomerModel._TYPECODE
			+ " AS bc} WHERE {bc:" + BDECustomerModel.UID + "} like ?uid";



	/** The Constant QUERY_CUSTOMER_BY_UID. */
	private static final String QUERY_CUSTOMER_BY_UID = "SELECT DISTINCT {" + B2BCustomerModel.PK + "} from {"
			+ B2BCustomerModel._TYPECODE + "!} WHERE LOWER({" + B2BCustomerModel.UID + "}) LIKE ?uid";

	private static final String QUERY_BDE_CUSTOMER_IMPORTED_ALL = "SELECT DISTINCT {bci:pk} FROM {" + BDECustomerImportedModel._TYPECODE
			+ " AS bci} ";

	private static final String QUERY_BDE_CUSTOMER_IMPORTED_BY_UID = "SELECT DISTINCT {bci:pk} FROM {" + BDECustomerImportedModel._TYPECODE
			+ " AS bci} WHERE {bci:" + BDECustomerImportedModel.UID + "} = ?uid";

	/** The Constant QUERY_CUSTOMER_BY_UID. */
	private static final String QUERY_DELETED_CUSTOMERS_BY_UID = "SELECT DISTINCT {bc:pk} from {" + B2BCustomerModel._TYPECODE
			+ "! as bc JOIN PrincipalGroupRelation as pgr on {bc:pk} = {pgr:source} JOIN " + PrincipalGroupModel._TYPECODE
			+ " as pg on {pgr:target} = {pg:pk}} WHERE {pg:uid} In (?inUids) AND LOWER({bc:" + B2BCustomerModel.UID + "}) LIKE ?uid";

	private static final String QUERY_VALID_MESSAGES = "SELECT {m:pk} FROM {" + SabmMessageModel._TYPECODE
			+ " AS m } WHERE {m:expiry}  > ?date AND ({m:b2bUnit} IS NULL OR {m:b2bUnit}=?b2bUnit) ORDER BY {m:modifiedtime} DESC";

	private static String QUERY_USER_READ_MESSAGES = "SELECT DISTINCT {stat:pk} FROM {" + SabmUserMessagesStatusModel._TYPECODE
			+ " AS stat} WHERE {stat:userId}= ?userId";

	private static String QUERY_ALL_USER_READ_MESSAGES = "SELECT DISTINCT {stat:pk} FROM {" + SabmUserMessagesStatusModel._TYPECODE
			+ " AS stat} WHERE {stat:messageCode}= ?messageCode";

	/** The Constant QUERY_SIMILAR_CUSTOMER_BY_UID. */
	private static final String QUERY_CUSTOMER_BY_LASTLOGIN_DATE = "SELECT DISTINCT {bc:pk} FROM {" + B2BCustomerModel._TYPECODE
			+ " AS bc} WHERE {bc:" + B2BCustomerModel.LASTLOGIN + "} >= ?lastweekDate AND {bc:" + B2BCustomerModel.LASTLOGIN
			+ "} < ?todayDate ";

	private static final String DELETED_CUSTOMER_GROUP = "deletedcustomergroup";

	private static final String QUERY_ALB_MODIFIED_CUSTOMERS= "SELECT DISTINCT {bc:pk} FROM {" + B2BCustomerModel._TYPECODE
			+ "! AS bc JOIN PrincipalGroupRelation as pgr on {bc:pk} = {pgr:source} JOIN " + AsahiB2BUnitModel._TYPECODE+" as abu on {pgr:target} = {abu:pk}} "
			+ "WHERE {abu:" + AsahiB2BUnitModel.COMPANYCODE + "} = 'sga' AND {bc:" + B2BCustomerModel.MODIFIEDTIME + "} >= ?lastModifiedTime";

	private static final String QUERY_ALB_ALL_CUSTOMERS= "SELECT DISTINCT {bc:pk} FROM {" + B2BCustomerModel._TYPECODE
			+ "! AS bc JOIN PrincipalGroupRelation as pgr on {bc:pk} = {pgr:source} JOIN " + AsahiB2BUnitModel._TYPECODE+" as abu on {pgr:target} = {abu:pk}} "
			+ "WHERE {abu:" + AsahiB2BUnitModel.COMPANYCODE + "} = 'sga'";



	/**
	 * Get the Customer by uids.
	 *
	 * @param orUids
	 *           the b2b unit ids
	 * @param andUids
	 *           the b2b unit ids
	 * @return List<B2BCustomerModel> the Customers
	 */
	@Override
	public List<B2BCustomerModel> getCustomerByUnits(final List<String> orUids, final List<String> andUids)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("orUids", orUids);
		params.put("andUids", andUids);

		LOG.debug("The search customer for update profile query [{}] - params [{}]", QUERY_Customer_BY_UIDS, params);
		return queryCustomers(QUERY_Customer_BY_UIDS, params);
	}

	/**
	 * Query customers.
	 *
	 * @param query
	 *           the query
	 * @param params
	 *           the params
	 * @return the list
	 */
	private List<B2BCustomerModel> queryCustomers(final String query, final Map<String, Object> params)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query, params);
		final SearchResult<B2BCustomerModel> result = getFlexibleSearchService().search(fsq);

		return result.getCount() > 0 ? result.getResult() : Collections.<B2BCustomerModel> emptyList();
	}

	/**
	 * get the user with similar uid.
	 *
	 * @param uid
	 *           the uid
	 * @return List<B2BCustomerModel>
	 */
	@Override
	public List<B2BCustomerModel> getSimilarB2BCustomer(final String uid)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);

		LOG.debug("The search customer for update profile query [{}] - params [{}]", QUERY_SIMILAR_CUSTOMER_BY_UID, params);
		return queryCustomers(QUERY_SIMILAR_CUSTOMER_BY_UID, params);
	}

	/**
	 * search for users with the inputted uid
	 *
	 * @param email
	 * @return List<B2bCustomerModel>
	 */
	@Override
	public List<B2BCustomerModel> searchB2BCustomerByEmail(final String email)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", "%" + StringUtils.lowerCase(email) + "%");
		params.put("uid", StringUtils.lowerCase(email));
		LOG.debug("The search for users with inputted uid [{}] - params [{}]", QUERY_CUSTOMER_BY_UID, params);
		return queryCustomers(QUERY_CUSTOMER_BY_UID, params);
	}

	/**
	 * search for users with the inputted uid
	 *
	 * @param Exact
	 *           email
	 * @return List<B2bCustomerModel>
	 */
	@Override
	public List<B2BCustomerModel> searchB2BCustomerByExactEmail(final String email)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		//      Restrict wildcards as part of Pentest recommendation.
		//		params.put("uid", "%" + StringUtils.lowerCase(email) + "%");
		params.put("uid", StringUtils.lowerCase(email));
		LOG.debug("The search for users with inputted uid [{}] - params [{}]", QUERY_CUSTOMER_BY_UID, params);
		return queryCustomers(QUERY_CUSTOMER_BY_UID, params);
	}

	/**
	 * search for users with the inputted uid
	 *
	 * @param email
	 * @return List<B2bCustomerModel>
	 */
	@Override
	public List<B2BCustomerModel> searchB2BDeletedCustomerByEmail(final String email)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", "%" + StringUtils.lowerCase(email) + "%");
		params.put("inUids", DELETED_CUSTOMER_GROUP);

		LOG.debug("The search for users with inputted uid [{}] - params [{}]", QUERY_DELETED_CUSTOMERS_BY_UID, params);
		return queryCustomers(QUERY_DELETED_CUSTOMERS_BY_UID, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SabmB2BCustomerDao#getBDECustomer(java.lang.String)
	 */
	@Override
	public BDECustomerModel getBDECustomer(final String uid)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_SIMILAR_BDE_CUSTOMER_BY_UID, params);
		final SearchResult<BDECustomerModel> result = getFlexibleSearchService().search(fsq);

		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	@Override
	public BDECustomerImportedModel getBDECustomerImported(final String uid)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid",StringUtils.lowerCase(uid));

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_BDE_CUSTOMER_IMPORTED_BY_UID, params);
		final SearchResult<BDECustomerImportedModel> result = getFlexibleSearchService().search(fsq);

		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	@Override
	public List<BDECustomerImportedModel> getBDECustomerImportedAll()
	{
		final Map<String, Object> params = new HashMap<String, Object>();


		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_BDE_CUSTOMER_IMPORTED_ALL, params);


		final SearchResult<BDECustomerImportedModel> result = getFlexibleSearchService().search(fsq);

		return result.getResult();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SabmB2BCustomerDao#getUserUnreadNotifications(de.hybris.platform.b2b.model.
	 * B2BUnitModel, java.lang.String)
	 */
	@Override
	public List<SabmMessageModel> getUserUnreadNotifications(final B2BUnitModel b2bUnit, final String userId)
	{
		final Map<String, Object> messageQueryParam = new HashMap<String, Object>();
		messageQueryParam.put("b2bUnit", b2bUnit);
		messageQueryParam.put("date", new Date());

		final FlexibleSearchQuery messageQuery = new FlexibleSearchQuery(QUERY_VALID_MESSAGES, messageQueryParam);

		final SearchResult<SabmMessageModel> allMessagesResult = getFlexibleSearchService().search(messageQuery);

		final List<String> userReadMessages = getAllUserReadMessages(userId);

		final List<SabmMessageModel> unReadMessages = new ArrayList<>();

		if (allMessagesResult.getCount() > 0)
		{
			final List<SabmMessageModel> allMessages = allMessagesResult.getResult();

			if (userReadMessages.size() > 0)
			{
				for (final SabmMessageModel message : allMessages)
				{
					if (!userReadMessages.contains(message.getCode()))
					{
						unReadMessages.add(message);
					}
				}
			}
			else
			{
				return allMessages;
			}
		}

		return unReadMessages;
	}

	/**
	 * @param userId
	 * @return
	 */
	private List<String> getAllUserReadMessages(final String userId)
	{
		final Map<String, Object> userReadMgQueryParam = new HashMap<String, Object>();
		userReadMgQueryParam.put("userId", userId);

		final FlexibleSearchQuery userReadMessagesQuery = new FlexibleSearchQuery(QUERY_USER_READ_MESSAGES, userReadMgQueryParam);

		final SearchResult<SabmUserMessagesStatusModel> userReadMessageResult = getFlexibleSearchService()
				.search(userReadMessagesQuery);

		final List<String> userReadMessages = new ArrayList<>();
		if (userReadMessageResult.getCount() > 0)
		{
			for (final SabmUserMessagesStatusModel model : userReadMessageResult.getResult())
			{
				userReadMessages.add(model.getMessageCode());
			}
		}

		return userReadMessages;
	}

	public List<SabmUserMessagesStatusModel> getAllUserMessageEntries(final String messageCode)
	{
		final Map<String, Object> userReadMgQueryParam = new HashMap<String, Object>();
		userReadMgQueryParam.put("messageCode", messageCode);

		final FlexibleSearchQuery userReadMessagesQuery = new FlexibleSearchQuery(QUERY_ALL_USER_READ_MESSAGES,
				userReadMgQueryParam);
		final SearchResult<SabmUserMessagesStatusModel> allUserReadMessageResult = getFlexibleSearchService()
				.search(userReadMessagesQuery);
		if (allUserReadMessageResult.getCount() > 0)
		{
			return allUserReadMessageResult.getResult();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SabmB2BCustomerDao#getCustomerByLastLogIn() Get the list of user who logedIn in
	 * past a week
	 */
	@Override
	public Set<B2BCustomerModel> getCustomerByLastLogIn()
	{
		final Map<String, Object> params = new HashMap<String, Object>();

		final DateTime toDay = new DateTime();
		final DateTime lastweekDate = toDay.minusWeeks(1);
		params.put("lastweekDate", lastweekDate.toDate());
		params.put("todayDate", new Date());

		LOG.debug("The search customer for update profile query [{}] - params [{}]", QUERY_CUSTOMER_BY_LASTLOGIN_DATE, params);
		final List<B2BCustomerModel> listOfCustomer = queryCustomers(QUERY_CUSTOMER_BY_LASTLOGIN_DATE, params);
		if (!listOfCustomer.isEmpty())
		{
			final Set<B2BCustomerModel> setOfCustomer = new HashSet<B2BCustomerModel>();
			setOfCustomer.addAll(listOfCustomer);
			return setOfCustomer;
		}
		else
		{
			return Collections.<B2BCustomerModel> emptySet();
		}

	}

	@Override
	public List<B2BCustomerModel> getModifiedALBCustomersForSF()
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		params.put("lastModifiedTime", cal.getTime());

		LOG.debug("ALB customer query with one day modified time [{}] - params [{}]", QUERY_ALB_MODIFIED_CUSTOMERS, params);
		return queryCustomers(QUERY_ALB_MODIFIED_CUSTOMERS, params);
	}

	@Override
	public List<B2BCustomerModel> getAllALBCustomersForSF()
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_ALB_ALL_CUSTOMERS);
		final SearchResult<B2BCustomerModel> result = getFlexibleSearchService().search(fsq);

		return result.getCount() > 0 ? result.getResult() : Collections.<B2BCustomerModel> emptyList();
	}

}
