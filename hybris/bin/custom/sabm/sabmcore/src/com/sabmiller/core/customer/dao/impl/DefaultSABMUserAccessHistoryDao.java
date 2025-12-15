/**
 *
 */
package com.sabmiller.core.customer.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.customer.dao.SABMUserAccessHistoryDao;
import com.sabmiller.core.model.SABMUserAccessHistoryModel;


/**
 * @author bonnie
 *
 */
public class DefaultSABMUserAccessHistoryDao extends AbstractItemDao implements SABMUserAccessHistoryDao
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMUserAccessHistoryDao.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.customer.dao.SABMUserAccessHistoryDao#findOldUserAccessHistory(java.util.Date, int)
	 */
	@Override
	public List<SABMUserAccessHistoryModel> findOldUserAccessHistory(final Date createdBefore, final int batchSize)
	{
		//Fetch user access history created before createdBefore date
		final String query = "SELECT {" + SABMUserAccessHistoryModel.PK + "} FROM {" + SABMUserAccessHistoryModel._TYPECODE
				+ "} WHERE {" + SABMUserAccessHistoryModel.CREATIONTIME + "} <= ?createdBefore ORDER BY {"
				+ SABMUserAccessHistoryModel.CREATIONTIME + "}";

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("createdBefore", createdBefore);
		return doSearch(query, params, batchSize, SABMUserAccessHistoryModel.class);
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final int batchSize,
			final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);

			//will fetch all history data if batch size is 0
			if (batchSize > 0)
			{
				LOG.debug("Fetch user access history by batch, batch size is {}", batchSize);
				//set paging data
				fQuery.setNeedTotal(Boolean.TRUE);
				fQuery.setStart(0);
				fQuery.setCount(batchSize);
			}
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));

		final SearchResult<T> searchResult = search(fQuery);
		return searchResult.getResult();
	}
}
