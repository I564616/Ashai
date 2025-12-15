package com.apb.integration.users.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.apb.integration.users.dao.AsahiUsersIntegrationDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class AsahiUsersIntegrationDaoImpl extends AbstractItemDao implements AsahiUsersIntegrationDao {

	private static final String ALB_STORE = "sga";

	private static final String ONLINE_ORDER = "ONLINE";

	private static final String CALL_CENTER_ORDER = "CALLCENTERORDER";

	private static final String FIND_LAST_ORDER_BY_USER = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE" + "{" + OrderModel.USER + "} = ?user AND {" + OrderModel.ORDERTYPE
			+ "} IN ({{SELECT {PK} from {OrderType} where {code} IN (?orderType)}}) AND {" + OrderModel.SITE
			+ "} IN ({{SELECT {PK} from {" + CMSSiteModel._TYPECODE + "} where {" + CMSSiteModel.UID + "}=?siteUid }}) ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";

	private static final String FIND_FIRST_WEB_ORDER_BY_USER = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE" + "{" + OrderModel.USER + "} = ?user AND {" + OrderModel.ORDERTYPE
			+ "} = ({{SELECT {PK} from {OrderType} where {code} = ?orderType}}) AND {" + OrderModel.SITE
			+ "} IN ({{SELECT {PK} from {" + CMSSiteModel._TYPECODE + "} where {" + CMSSiteModel.UID + "}=?siteUid }}) ORDER BY {" + OrderModel.CREATIONTIME + "} ASC";

	private static final String FIND_LAST_WEB_ORDER_BY_USER = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE" + "{" + OrderModel.USER + "} = ?user AND {" + OrderModel.ORDERTYPE
			+ "} = ({{SELECT {PK} from {OrderType} where {code} = ?orderType}}) AND {" + OrderModel.SITE
			+ "} IN ({{SELECT {PK} from {" + CMSSiteModel._TYPECODE + "} where {" + CMSSiteModel.UID + "}=?siteUid }}) ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";


	@Override
	public OrderModel getALBFirstWebOrder(UserModel user) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		params.put("siteUid", ALB_STORE);
		params.put("orderType", ONLINE_ORDER);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_FIRST_WEB_ORDER_BY_USER, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult().size() > 0 ? result.getResult().get(0) : null;
	}


	@Override
	public OrderModel getALBLastOrder(UserModel user) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		params.put("siteUid", ALB_STORE);
		params.put("orderType", Arrays.asList(ONLINE_ORDER,CALL_CENTER_ORDER));
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_LAST_ORDER_BY_USER, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult().size() > 0 ? result.getResult().get(0) : null;
	}


	@Override
	public OrderModel getALBLastWebOrder(UserModel user) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		params.put("siteUid", ALB_STORE);
		params.put("orderType", ONLINE_ORDER);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_LAST_WEB_ORDER_BY_USER, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult().size() > 0 ? result.getResult().get(0) : null;
	}



}
