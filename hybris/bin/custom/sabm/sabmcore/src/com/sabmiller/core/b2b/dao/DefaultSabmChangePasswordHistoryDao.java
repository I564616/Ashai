/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.UserPasswordHistoryModel;


/**
 * The Class DefaultSabmChangePasswordHistoryDao.
 */
public class DefaultSabmChangePasswordHistoryDao extends DefaultGenericDao<UserPasswordHistoryModel>
		implements SabmChangePasswordHistoryDao
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmChangePasswordHistoryDao.class);

	/**
	 * Instantiates a new default sabm change password history dao.
	 */
	public DefaultSabmChangePasswordHistoryDao()
	{
		super("UserPasswordHistory");
	}

	/** The query. */
	private final String query = "SELECT {" + UserPasswordHistoryModel.PK + "} FROM {" + UserPasswordHistoryModel._TYPECODE
			+ "}  WHERE {" + UserPasswordHistoryModel.UID + "}=?employee ORDER BY {" + UserPasswordHistoryModel.CREATIONTIME
			+ "} DESC";


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.b2b.dao.SabmChangePasswordHistoryDao#findPreviousEncodedPasswords(java.lang.String, int)
	 */
	@Override
	public List<UserPasswordHistoryModel> findPreviousEncodedPasswords(final String uid, final int count)
	{

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", uid);

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query, params);
		fQuery.setCount(count);

		final SearchResult<UserPasswordHistoryModel> result = getFlexibleSearchService().search(fQuery);

		LOG.debug("Password History List found : {}, with parameters: {}", result.getResult(), params);

		return result.getResult();
	}

}
