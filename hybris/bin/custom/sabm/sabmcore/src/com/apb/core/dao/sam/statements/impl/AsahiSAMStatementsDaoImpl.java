package com.apb.core.dao.sam.statements.impl;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.dao.sam.statements.AsahiSAMStatementsDao;
import com.sabmiller.core.model.AsahiSAMStatementsModel;


/**
 * The Class AsahiSAMStatementsDaoImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMStatementsDaoImpl extends AbstractItemDao implements AsahiSAMStatementsDao
{

	private static final Logger LOG = Logger.getLogger(AsahiSAMStatementsDaoImpl.class);

	/** The Constant STATEMENT_NUMBER. */
	private static final String STATEMENT_NUMBER = "statementNumber";

	/**
	 * Gets the statement by number.
	 *
	 * @param number
	 *           the number
	 * @return the invoice by document number
	 */
	@Override
	public AsahiSAMStatementsModel getStatementByNumber(final String number)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_STATEMENT_BY_NUMBER);
		params.put(STATEMENT_NUMBER, number);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AsahiSAMStatementsModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

}
