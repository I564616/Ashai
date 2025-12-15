/**
 *
 */
package com.apb.core.user.daos.impl;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultTitleDao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;


/**
 * @author Varun.Goyal1
 *
 */
public class DefaultAsahiTitleDao extends DefaultTitleDao
{

	private static final String CUB_SITE_NAME = "sabmStore";
	private static final String CUB_BUSINESSCODE = "cub";
	private static final String ASAHI_BUSINESSCODE = "asahi";

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	FlexibleSearchService flexibleSearchService;

	@Override
	public Collection<TitleModel> findTitles()
	{

		final CMSSiteModel cmssiste = cmsSiteService.getCurrentSite();

		if (cmssiste != null)
		{
			String businessCode = ASAHI_BUSINESSCODE;
			if (CUB_SITE_NAME.equalsIgnoreCase(cmssiste.getUid()))
			{
				businessCode = CUB_BUSINESSCODE;
			}

			final Map<String, Object> params = new HashMap<>();
			final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_TITLES);

			params.put("businessCode", businessCode);

			final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
			query.addQueryParameters(params);

			final SearchResult<TitleModel> result = flexibleSearchService.search(query);

			if (null != result && CollectionUtils.isNotEmpty(result.getResult()))
			{
				return result.getResult();
			}
			return Collections.emptyList();
		}
		else
		{
			return this.find();
		}

	}


}
