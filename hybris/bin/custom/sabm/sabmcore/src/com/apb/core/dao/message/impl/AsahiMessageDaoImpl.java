package com.apb.core.dao.message.impl;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.dao.message.AsahiMessageDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * The Class AsahiMessageDaoImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiMessageDaoImpl extends AbstractItemDao implements AsahiMessageDao{
	
	/**
	 * Gets the message value for key.
	 *
	 * @param messageCode the message code
	 * @param catalogVersions the catalog versions
	 * @param language the language
	 * @return the message value for key
	 */
	@Override
	public String getMessageValueForKey(String messageCode,
			Set<CatalogVersionModel> catalogVersions, String language) {
		final String queryString = "SELECT {messageValue[" + language + "]:o} FROM {MessageItem} " +
				"WHERE {uid}=?messageCode AND {catalogVersion} IN (?catalogVersions) AND " +
				"{messageValue[" + language + "]:o} IS NOT NULL";
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("messageCode", messageCode);
		query.addQueryParameter("catalogVersions", catalogVersions);
		query.setResultClassList(Collections.singletonList(String.class));
		final SearchResult<String> searchResult = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(searchResult.getResult())){
			return searchResult.getResult().get(0);
		}
		return null;
	}
}
