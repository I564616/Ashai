package com.apb.dao.contactus.impl;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.model.ContactUsQueryTypeModel;
import com.apb.dao.contactus.ApbContactUsDao;


/**
 * ApbContactUsDaoImpl implementation of {@link AbstractItemDao}
 *
 * @author c5252631
 */
public class ApbContactUsDaoImpl extends AbstractItemDao implements ApbContactUsDao
{
	@Override
	public List<ContactUsQueryTypeModel> getSubject(final CMSSiteModel cmsSite)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_CONTACTUS_QUERYTYPE_BY_SITE);
		params.put(ApbCoreConstants.SITE, cmsSite.getPk());

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<ContactUsQueryTypeModel> result = getFlexibleSearchService().search(query);
		if (result.getResult().size() > 0)
		{
			return result.getResult();
		}
		return null;
	}
}
