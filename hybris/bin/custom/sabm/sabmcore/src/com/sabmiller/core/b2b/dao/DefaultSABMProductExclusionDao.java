/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.ProductExclusionModel;


/**
 * The Class DefaultSABMProductExclusionDao used to search the Product Exclusion.
 */
public class DefaultSABMProductExclusionDao extends DefaultGenericDao<ProductExclusionModel> implements SABMProductExclusionDao
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMProductExclusionDao.class);

	/** The Constant FIND_PRODUCT_EXCLUSION_FULL_QUERY. */
	private static final String FIND_PRODUCT_EXCLUSION_FULL_QUERY = "SELECT {" + ProductExclusionModel.PK + "} FROM {"
			+ ProductExclusionModel._TYPECODE + "} WHERE {" + ProductExclusionModel.CUSTOMER + "}=?customer AND {"
			+ ProductExclusionModel.PRODUCT + "}=?product AND {" + ProductExclusionModel.VALIDFROM + "}<?date AND {"
			+ ProductExclusionModel.VALIDTO + "}>?date";

	/** The Constant FIND_PRODUCT_EXC_CUSTOMER_DATE_QUERY. */
	private static final String FIND_PRODUCT_EXC_CUSTOMER_DATE_QUERY = "SELECT {" + ProductExclusionModel.PK + "} FROM {"
			+ ProductExclusionModel._TYPECODE + "} WHERE {" + ProductExclusionModel.CUSTOMER + "}=?customer AND {"
			+ ProductExclusionModel.VALIDFROM + "}<?date AND {" + ProductExclusionModel.VALIDTO + "}>?date";

	/** The Constant FIND_PRODUCT_EXC_CUSTOMER_DATE_QUERY. */
	private static final String FIND_PRODUCT_EXC_CUSTOMER_QUERY = "SELECT {" + ProductExclusionModel.PK + "} FROM {"
			+ ProductExclusionModel._TYPECODE + "} WHERE {" + ProductExclusionModel.CUSTOMER + "}=?customer";


	/**
	 * Instantiates a new product exclusion dao. Setting the ProductExclusion in the super constructor to use the
	 * DefaultGenericDao methods.
	 */
	public DefaultSABMProductExclusionDao()
	{
		super("ProductExclusion");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.dao.SABMProductExclusionDao#find(de.hybris.platform.core.model.security.PrincipalModel,
	 * java.lang.String, java.util.Date)
	 */
	@Override
	public List<ProductExclusionModel> find(final PrincipalModel customer, final String product, final Date date)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("customer", customer);
		params.put("product", product);
		params.put("date", date);

		return find(params, FIND_PRODUCT_EXCLUSION_FULL_QUERY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SABMProductExclusionDao#find(java.util.Date)
	 */
	@Override
	public List<ProductExclusionModel> find(final PrincipalModel customer, final Date date)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("customer", customer);
		params.put("date", date);

		return find(params, FIND_PRODUCT_EXC_CUSTOMER_DATE_QUERY);
	}


	@Override
	public List<ProductExclusionModel> findCustomerProductExcl(final PrincipalModel customer)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("customer", customer);

		return find(params, FIND_PRODUCT_EXC_CUSTOMER_QUERY);
	}

	/**
	 * Find.
	 *
	 * @param params
	 *           the params
	 * @param query
	 *           the query
	 * @return the list
	 */
	protected List<ProductExclusionModel> find(final Map<String, Object> params, final String query)
	{
		//Using OOB flexiblesearch with custom query. Return an empty list in case of no match
		final SearchResult<ProductExclusionModel> result = getFlexibleSearchService().search(query, params);

		LOG.debug("Product Exclusion found: {}, with parameters: {}", result.getResult(), params);

		return result.getResult();
	}
}
