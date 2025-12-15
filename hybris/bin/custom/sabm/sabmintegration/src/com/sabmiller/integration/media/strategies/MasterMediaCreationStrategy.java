/**
 *
 */
package com.sabmiller.integration.media.strategies;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * The Interface MasterMediaCreationStrategy.
 */
public interface MasterMediaCreationStrategy extends MediaCreationStrategy
{

	/**
	 * Creates the master media product. This method create a new gallery and fill the attributes: 'picture',
	 * 'thumbnail', 'detail', 'normal', 'others'
	 *
	 * @param product
	 *           the product
	 * @param catalogVersion
	 *           the catalog version
	 * @param imageQualifier
	 *           the image qualifier
	 * @param imageUrl
	 *           the url used to retrieve the image data
	 * @return the media container model
	 */
	MediaContainerModel createMasterMediaProduct(ProductModel product, CatalogVersionModel catalogVersion, String imageQualifier,
			String imageUrl);
}
