/**
 *
 */
package com.sabmiller.core.deals.dao;

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

import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;


/**
 * @author bonnie
 *
 */
public class DefaultRepDrivenDealConditionStatusDao extends AbstractItemDao implements RepDrivenDealConditionStatusDao
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRepDrivenDealConditionStatusDao.class);

	private static final String REP_DRIVEN_DEAL_STATUS_QUERY = "SELECT {r:pk} FROM {RepDrivenDealConditionStatus AS r } WHERE {r:assignedTo}=?b2bUnit AND {r:dealConditionNumber}=?dealCode  ORDER BY {r:date} Desc";

	@Override
	public RepDrivenDealConditionStatusModel getRepDrivenDealCondition(final String dealCode, final String b2bUnitId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealCode", dealCode);
		params.put("b2bUnit", b2bUnitId);
		//params.put("uid", uid);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(REP_DRIVEN_DEAL_STATUS_QUERY, params);
		final SearchResult<RepDrivenDealConditionStatusModel> result = getFlexibleSearchService().search(fsq);

		return result.getCount() > 0 ? result.getResult().get(0) : null;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.dao.RepDrivenDealConditionStatusDao#findOldRepDrivenDealConditionStatus(java.util.Date,
	 * int)
	 */
	@Override
	public List<RepDrivenDealConditionStatusModel> findOldRepDrivenDealConditionStatus(final Date createdBefore,
			final int batchSize)
	{
		//Fetch repdriven deal condition status created before createdBefore date
		final String query = "SELECT {" + RepDrivenDealConditionStatusModel.PK + "} FROM {"
				+ RepDrivenDealConditionStatusModel._TYPECODE + "} WHERE {" + RepDrivenDealConditionStatusModel.CREATIONTIME
				+ "} <= ?createdBefore ORDER BY {" + RepDrivenDealConditionStatusModel.CREATIONTIME + "}";

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("createdBefore", createdBefore);
		return doSearch(query, params, batchSize, RepDrivenDealConditionStatusModel.class);
	}


	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final int batchSize,
			final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);

			//will fetch all RepDrivenDealConditionStatus data if batch size is 0
			if (batchSize > 0)
			{
				LOG.debug("Fetch RepDrivenDealConditionStatus by batch, batch size is {}", batchSize);
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
