package com.apb.core.dao.config.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.dao.config.AsahiConfigurationDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;
import java.util.List;

/**
 * The Class AsahiConfigurationDaoImpl.
 */
public class AsahiConfigurationDaoImpl extends AbstractItemDao implements AsahiConfigurationDao{

	/**
	 * Fetch value for a given key in a catalog version.
	 *
	 * @param key the key
	 * @param catalogVersions the catalog versions
	 * @return the config value for key
	 */
	
	private FlexibleSearchService flexibleSearchService;
	private static final String QUERY_MEDIAS_FOR_CODE = "SELECT {PK} FROM {" + MediaModel._TYPECODE + "} WHERE {" + MediaModel.CODE + "} IN (?codes)";

	
	public List<MediaModel> findMedias(final String code)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_MEDIAS_FOR_CODE);
		fsq.addQueryParameter("codes", code);
		fsq.setResultClassList(Collections.singletonList(MediaModel.class));
		final SearchResult<MediaModel> resultSet = flexibleSearchService.search(fsq);
		return resultSet.getResult();
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public String getConfigValueForKey(final String key) {
			
			final FlexibleSearchQuery query = new FlexibleSearchQuery(ApbQueryConstant.GET_CONFIG_VALUE_FOR_KEY);
			query.addQueryParameter("configKey", key);
			query.setResultClassList(Collections.singletonList(String.class));
			final SearchResult<String> searchResult = getFlexibleSearchService().search(query);
			if(CollectionUtils.isNotEmpty(searchResult.getResult())){
				return searchResult.getResult().get(0);
			}
			return null;
	}
}
