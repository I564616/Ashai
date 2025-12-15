/**
 *
 */
package com.sabmiller.integration.media.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.media.dao.MediaContainerDao;


/**
 * The Interface SABMMediaContainerDao.
 */
public interface SABMMediaContainerDao extends MediaContainerDao
{

	/**
	 * Find media containers by qualifier.
	 *
	 * @param catalogVersion
	 *           the catalog version
	 * @param paramString
	 *           the param string
	 * @return the java.util. list
	 */
	java.util.List<MediaContainerModel> findMediaContainersByQualifier(CatalogVersionModel catalogVersion, String paramString);
}
