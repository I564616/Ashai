/**
 *
 */
package com.sabm.core.webservicelog.dao;

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

import com.sabmiller.core.customer.dao.impl.DefaultSABMUserAccessHistoryDao;
import com.sabmiller.integration.model.WebServiceLogModel;


/**
 * @author zhangxiaowu
 * @date 09/12/2016
 */
public class DefaultSabmWebServiceLogDao extends AbstractItemDao implements SabmWebServiceLogDao
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMUserAccessHistoryDao.class);

	private static final String FIND_OLD_WEBSERVICELOG_QUERY = "SELECT {" + WebServiceLogModel.PK + "} FROM {"
			+ WebServiceLogModel._TYPECODE + "} WHERE {" + WebServiceLogModel.REQUESTDATE + "} <= ?createdBefore ORDER BY {"
			+ WebServiceLogModel.REQUESTDATE + "}";

	/**
	 * find the WebServiceLog by the create date and the batchSize
	 *
	 * @param requestedBefore
	 *           the date
	 * @param batchSize
	 *           the batch size
	 * @return list of @WebServiceLogModel
	 */
	@Override
	public List<WebServiceLogModel> findOldWebServiceLog(final Date requestedBefore, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("createdBefore", requestedBefore);
		return doSearch(FIND_OLD_WEBSERVICELOG_QUERY, params, batchSize, WebServiceLogModel.class);
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
