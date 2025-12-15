/**
 *
 */
package com.sabmiller.integration.media.services;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaContainerService;


/**
 * The Interface SABMMediaContainerService.
 */
public interface SABMMediaContainerService extends MediaContainerService
{

	/**
	 * Gets the media container for qualifier.
	 *
	 * @param paramString
	 *           the param string
	 * @param catalog
	 *           the catalog
	 * @return the media container for qualifier
	 * @throws UnknownIdentifierException
	 *            the unknown identifier exception
	 * @throws AmbiguousIdentifierException
	 *            the ambiguous identifier exception
	 */
	MediaContainerModel getMediaContainerForQualifier(String paramString, CatalogVersionModel catalog)
			throws UnknownIdentifierException, AmbiguousIdentifierException;
}
