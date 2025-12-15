/**
 *
 */
package com.sabmiller.integration.media.dao.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.media.dao.impl.DefaultMediaContainerDao;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.integration.media.dao.SABMMediaContainerDao;


/**
 *
 */
public class DefaultSABMMediaContainerDao extends DefaultMediaContainerDao implements SABMMediaContainerDao
{
	private final String FIND_CONTAINERS_BY_CODE_CAT_QUERY = "SELECT {" + MediaContainerModel.PK + "} from {"
			+ MediaContainerModel._TYPECODE + "} WHERE {" + MediaContainerModel.QUALIFIER + "}=?qualifier AND {"
			+ MediaContainerModel.CATALOGVERSION + "}=?catalogVersion";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMMediaContainerDao.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.media.dao.SABMMediaContainerDao#findMediaContainersByQualifier(de.hybris.platform.
	 * catalog.model.CatalogVersionModel, java.lang.String)
	 */
	@Override
	public List<MediaContainerModel> findMediaContainersByQualifier(final CatalogVersionModel catalogVersion,
			final String qualifier)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("qualifier", qualifier);
		params.put("catalogVersion", catalogVersion);

		final SearchResult<MediaContainerModel> result = getFlexibleSearchService().search(FIND_CONTAINERS_BY_CODE_CAT_QUERY,
				params);

		LOG.debug("MediaContainer results with params [{}]: {}", params, result.getResult());

		return result.getResult();
	}

}
