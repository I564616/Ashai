/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class DefaultSabmOrderTemplateDao.
 */
public class DefaultSabmOrderTemplateDao extends DefaultGenericDao<SABMOrderTemplateModel>implements SabmOrderTemplateDao
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmOrderTemplateDao.class);

	/** The Constant FIND_ORDER_TEMPLATE_BY_B2B_UNIT. */
	private static final String FIND_ORDER_TEMPLATE_BY_B2B_UNIT = "SELECT {" + SABMOrderTemplateModel.PK + "} " + "FROM {"
			+ SABMOrderTemplateModel._TYPECODE + "} WHERE {" + SABMOrderTemplateModel.UNIT + "}=?b2bUnit order by {"
			+ SABMOrderTemplateModel.SEQUENCE + "} desc";


	/**
	 * Instantiates a new default sabm order template dao.
	 */
	public DefaultSabmOrderTemplateDao()
	{
		super("SABMOrderTemplate");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SabmOrderTemplateDao#findOrderTemplateByB2BUnit(de.hybris.platform.b2b.model.
	 * B2BUnitModel)
	 */
	@Override
	public List<SABMOrderTemplateModel> findOrderTemplateByB2BUnit(final B2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", b2bUnit);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDER_TEMPLATE_BY_B2B_UNIT, params);
		final SearchResult<SABMOrderTemplateModel> result = getFlexibleSearchService().search(fsq);

		LOG.debug("Order Template found: {}, with parameters: {}", result.getResult(), params);

		return ListUtils.emptyIfNull(result.getResult());
	}

}
