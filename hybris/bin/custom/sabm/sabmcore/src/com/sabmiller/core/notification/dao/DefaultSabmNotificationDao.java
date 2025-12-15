/**
 *
 */
package com.sabmiller.core.notification.dao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.model.AsahiNotificationModel;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;


/**
 * @author raul.b.abatol.jr
 *
 */
public class DefaultSabmNotificationDao extends AbstractItemDao implements SabmNotificationDao
{
	protected final static String SELECTCLAUSE = "SELECT {" + SABMNotificationModel.PK + "} FROM {"
			+ SABMNotificationModel._TYPECODE + "} ";

	protected final static String FIND_RECOMMENDATION_FOR_USER_B2BUNIT_ID = SELECTCLAUSE + "WHERE " + " {"
			+ SABMNotificationModel.USER + "}= ?userId " + "AND {" + SABMNotificationModel.B2BUNIT + "}= ?b2bUnit ";

	protected final static String FIND_RECOMMENDATION_FOR_USER = SELECTCLAUSE + "WHERE " + " {" + SABMNotificationModel.USER
			+ "}= ?userId ";


	protected final static String FIND_NOTIFICATION_FOR_ID = SELECTCLAUSE + "WHERE " + " {" + SABMNotificationModel.PK + "}= ?id";

	protected final static String FIND_NOTIFICATIONS_FOR_ENABLED_NOTIFICATIONTYPE = "SELECT {n." + SABMNotificationModel.PK
			+ "} FROM {" + SABMNotificationModel._TYPECODE + " AS n JOIN " + SABMNotificationPrefModel._TYPECODE + " AS np ON {n."
			+ SABMNotificationModel.NOTIFICATIONPREFERENCES + "} " + "LIKE CONCAT('%', CONCAT({np." + SABMNotificationPrefModel.PK
			+ "}, '%'))} WHERE {np." + SABMNotificationPrefModel.NOTIFICATIONTYPE + "}=?notificationType AND {np."
			+ SABMNotificationPrefModel.NOTIFICATIONTYPEENABLED + "}=?notificationTypeEnabled";

	protected final static String FIND_NOTIFICATIONS_FOR_UNIT_FOR_ENABLED_NOTIFICATIONTYPE = "SELECT {n." + SABMNotificationModel.PK
			+ "} FROM {" + SABMNotificationModel._TYPECODE + " AS n JOIN " + SABMNotificationPrefModel._TYPECODE + " AS np ON {n."
			+ SABMNotificationModel.NOTIFICATIONPREFERENCES + "} " + "LIKE CONCAT('%', CONCAT({np." + SABMNotificationPrefModel.PK
			+ "}, '%'))} WHERE {" + SABMNotificationModel.B2BUNIT + "}= ?b2bUnit AND {np." + SABMNotificationPrefModel.NOTIFICATIONTYPE + "}=?notificationType AND {np."
			+ SABMNotificationPrefModel.NOTIFICATIONTYPEENABLED + "}=?notificationTypeEnabled";

	protected final static String FIND_NOTIFICATION_FOR_ASAHI_USER_B2BUNIT_ID = "SELECT {" + AsahiNotificationModel.PK + "} FROM {"
			+ AsahiNotificationModel._TYPECODE + "} " + "WHERE " + " {"
			+ AsahiNotificationModel.USER + "}= ?userId " + "AND {" + AsahiNotificationModel.B2BUNIT + "}= ?b2bUnit ";

	@Override
	public List<SABMNotificationModel> getNotificationForUser(final B2BCustomerModel customerModel, final B2BUnitModel b2BUnitModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("userId", customerModel);
		params.put("b2bUnit", b2BUnitModel);
		return doSearch(FIND_RECOMMENDATION_FOR_USER_B2BUNIT_ID, params, SABMNotificationModel.class);
	}
	
	@Override
	public List<SABMNotificationModel> getNotificationForAllUnits(final B2BCustomerModel customerModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("userId", customerModel);
		return doSearch(FIND_RECOMMENDATION_FOR_USER, params, SABMNotificationModel.class);
	}
	
	

	@Override
	public List<SABMNotificationModel> getNotificationForUnit(final NotificationType notificationType,
															  final Boolean notificationTypeEnabled,final B2BUnitModel b2BUnitModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("notificationType", notificationType);
		params.put("notificationTypeEnabled", notificationTypeEnabled);
		params.put("b2bUnit", b2BUnitModel);
		return doSearch(FIND_NOTIFICATIONS_FOR_UNIT_FOR_ENABLED_NOTIFICATIONTYPE, params, SABMNotificationModel.class);
	}

	@Override
	public SABMNotificationModel getNotificationByID(final String id)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		final List<SABMNotificationModel> results = doSearch(FIND_NOTIFICATION_FOR_ID, params, SABMNotificationModel.class);
		return results != null && CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
	}

	@Override
	public List<SABMNotificationModel> getNotifications(final NotificationType notificationType,
			final Boolean notificationTypeEnabled)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("notificationType", notificationType);
		params.put("notificationTypeEnabled", notificationTypeEnabled);
		return doSearch(FIND_NOTIFICATIONS_FOR_ENABLED_NOTIFICATIONTYPE, params, SABMNotificationModel.class);
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));
		final SearchResult<T> searchResult = search(fQuery);
		return searchResult.getResult();
	}

	@Override
	public List<AsahiNotificationModel> getNotificationForAsahiUser(final B2BCustomerModel customerModel, final B2BUnitModel b2BUnitModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("userId", customerModel);
		params.put("b2bUnit", b2BUnitModel);
		return doSearch(FIND_NOTIFICATION_FOR_ASAHI_USER_B2BUNIT_ID, params, AsahiNotificationModel.class);
	}
}
