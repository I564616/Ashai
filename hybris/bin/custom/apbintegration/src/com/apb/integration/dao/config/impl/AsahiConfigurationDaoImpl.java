package com.apb.integration.dao.config.impl;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.integration.dao.config.AsahiConfigurationDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

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

	@Override
	public String getConfigValueForKey(final String key) {
			
			final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {configValue} FROM {Configuration} WHERE {configKey}=?configKey AND {configValue} IS NOT NULL");
			query.addQueryParameter("configKey", key);
			query.setResultClassList(Collections.singletonList(String.class));
			final SearchResult<String> searchResult = getFlexibleSearchService().search(query);
			if(CollectionUtils.isNotEmpty(searchResult.getResult())){
				return searchResult.getResult().get(0);
			}
			return null;
	}
}
