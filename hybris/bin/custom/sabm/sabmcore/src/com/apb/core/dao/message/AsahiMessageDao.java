package com.apb.core.dao.message;

import java.util.Set;

import de.hybris.platform.catalog.model.CatalogVersionModel;

/**
 * The Interface AsahiMessageDao.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiMessageDao {
	
		/**
		 * Gets the message value for key.
		 *
		 * @param messageCode the message code
		 * @param catalogVersions the catalog versions
		 * @param language the language
		 * @return the message value for key
		 */
		String getMessageValueForKey(final String messageCode,
		        Set<CatalogVersionModel> catalogVersions, final String language);
}
