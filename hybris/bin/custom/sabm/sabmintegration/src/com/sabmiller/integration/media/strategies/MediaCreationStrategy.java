/**
 *
 */
package com.sabmiller.integration.media.strategies;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * The Interface MediaCreationStrategy.
 */
public interface MediaCreationStrategy
{

	/**
	 * Creates the media container for the product and add it to the product's gallery images.
	 *
	 * @param product
	 *           the product
	 * @param catalogVersion
	 *           the catalog version
	 * @param imageQualifier
	 *           the image qualifier
	 * @param imageUrl
	 *           the image url
	 * @return the media container model
	 */
	MediaContainerModel createMediaProduct(ProductModel product, CatalogVersionModel catalogVersion, String imageQualifier,
			String imageUrl);
}
