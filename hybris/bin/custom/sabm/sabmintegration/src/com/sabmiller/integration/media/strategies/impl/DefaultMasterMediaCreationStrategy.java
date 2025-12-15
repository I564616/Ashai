/**
 *
 */
package com.sabmiller.integration.media.strategies.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.integration.media.strategies.MasterMediaCreationStrategy;


/**
 * The Class DefaultMasterMediaCreationStrategy.
 */
public class DefaultMasterMediaCreationStrategy extends DefaultMediaCreationStrategy implements MasterMediaCreationStrategy
{

	/** The picture thumbnail format. */
	private String pictureThumbnailFormat = "SABMThumbnailFormat";

	/** The picture normal format. */
	private String pictureNormalFormat = "SABMNormalFormat";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMasterMediaCreationStrategy.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.integration.media.strategies.MasterMediaCreationStrategy#createMasterMediaProduct(de.hybris.platform
	 * .core.model.product.ProductModel, java.lang.String, java.lang.String)
	 */
	@Override
	public MediaContainerModel createMasterMediaProduct(final ProductModel product, final CatalogVersionModel catalogVersion,
			final String imageQualifier, final String imageUrl)
	{
		//Creating product container
		final MediaContainerModel containerModel = createMediaProduct(product, catalogVersion, imageQualifier, imageUrl);

		if (containerModel != null)
		{
			//Getting the right picture format and set it to the product in several attributes.
			setPictureImage(product, containerModel);
			setThumbnailImage(product, containerModel);
			getModelService().save(product);
		}
		return containerModel;
	}

	/**
	 * Sets the picture image.
	 *
	 * @param product
	 *           the product
	 * @param container
	 *           the container
	 */
	protected void setPictureImage(final ProductModel product, final MediaContainerModel container)
	{
		try
		{
			final MediaModel mediaNormal = getMediaContainerService().getMediaForFormat(container,
					getMediaService().getFormat(pictureNormalFormat));

			product.setPicture(mediaNormal);
		}
		catch (ModelNotFoundException | AmbiguousIdentifierException e)
		{
			LOG.error("Error searching media with format SABMNormalFormat", e);
		}
	}

	/**
	 * Sets the thumbnail image.
	 *
	 * @param product
	 *           the product
	 * @param container
	 *           the container
	 */
	protected void setThumbnailImage(final ProductModel product, final MediaContainerModel container)
	{
		try
		{
			final MediaModel mediaNormal = getMediaContainerService().getMediaForFormat(container,
					getMediaService().getFormat(pictureThumbnailFormat));

			product.setThumbnail(mediaNormal);
		}
		catch (ModelNotFoundException | AmbiguousIdentifierException e)
		{
			LOG.error("Error searching media with format SABMThumbnailFormat", e);
		}
	}

	/**
	 * Sets the picture thumbnail format.
	 *
	 * @param pictureThumbnailFormat
	 *           the pictureThumbnailFormat to set
	 */
	public void setPictureThumbnailFormat(final String pictureThumbnailFormat)
	{
		this.pictureThumbnailFormat = pictureThumbnailFormat;
	}

	/**
	 * Sets the picture normal format.
	 *
	 * @param pictureNormalFormat
	 *           the pictureNormalFormat to set
	 */
	public void setPictureNormalFormat(final String pictureNormalFormat)
	{
		this.pictureNormalFormat = pictureNormalFormat;
	}


}
