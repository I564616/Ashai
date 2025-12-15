/**
 *
 */
package com.sabmiller.integration.media.strategies.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.integration.media.services.ConversionGroupService;
import com.sabmiller.integration.media.services.SABMMediaContainerService;
import com.sabmiller.integration.media.strategies.MediaCreationStrategy;


/**
 * The Class DefaultMediaCreationStrategy.
 */
public class DefaultMediaCreationStrategy implements MediaCreationStrategy
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMediaCreationStrategy.class);

	/** The media service. */
	private MediaService mediaService;

	/** The media conversion service. */
	private MediaConversionService mediaConversionService;

	/** The conversion group service. */
	private ConversionGroupService conversionGroupService;

	/** The model service. */
	private ModelService modelService;

	/** The catalog version service. */
	private CatalogVersionService catalogVersionService;

	/** The media container service. */
	private SABMMediaContainerService mediaContainerService;

	/** The default conversion group. */
	private String defaultConversionGroup = "SABMConversionGroup";

	/** The default mime type. */
	private final String defaultMimeType = "image/jpeg";

	/** The media folder. */
	@Value(value = "${import.media.folder:root}")
	private String mediaFolder;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.integration.media.strategies.MediaCreationStrategy#createMediaProduct(de.hybris.platform.core.model.
	 * product.ProductModel, java.lang.String, java.lang.String)
	 */
	@Override
	public MediaContainerModel createMediaProduct(final ProductModel product, final CatalogVersionModel catalogVersion,
			final String imageQualifier, final String imageUrl)
	{

		final ConversionGroupModel conversionGroup = conversionGroupService.getConversionGroupByCode(defaultConversionGroup);

		MediaContainerModel mediaContainer = null;

		try
		{
			mediaContainer = mediaContainerService.getMediaContainerForQualifier(imageQualifier, catalogVersion);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug("Media not found with qualifier" + imageQualifier, e);
			mediaContainer = modelService.create(MediaContainerModel.class);
			mediaContainer.setCatalogVersion(catalogVersion);
			mediaContainer.setQualifier(imageQualifier);
			mediaContainer.setConversionGroup(conversionGroup);
		}
		catch (final AmbiguousIdentifierException e)
		{
			LOG.error("Ambiguous MediaContainer with code: " + imageQualifier + e, e);
			return null;
		}

		final MediaFormatModel format = mediaService.getFormat("SABMOriginalFormat");

		MediaModel media = null;

		try
		{
			media = mediaService.getMedia(catalogVersion, imageQualifier + "/" + format.getQualifier());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug("Media not found with qualifier" + imageQualifier + "/" + format.getQualifier(), e);
			media = createNewMedia(format, imageQualifier, imageUrl, catalogVersion);
		}
		catch (final AmbiguousIdentifierException e)
		{
			LOG.error("More than one Media found with qualifier: " + imageQualifier + "/" + format.getQualifier(), e);
			return null;
		}

		media.setMediaContainer(mediaContainer);

		modelService.save(mediaContainer);
		modelService.save(media);

		final List<MediaContainerModel> galleryImages = new ArrayList<>(product.getGalleryImages());
		galleryImages.add(mediaContainer);
		product.setGalleryImages(galleryImages);

		modelService.save(product);

		try
		{
			mediaService.setStreamForMedia(media, URI.create(imageUrl).toURL().openStream());
		}
		catch (MediaIOException | IllegalArgumentException | IOException e)
		{
			LOG.error("Unable to set stream in media", e);
		}

		mediaConversionService.convertMedias(mediaContainer);

		updateMime(mediaContainer);

		return mediaContainer;
	}

	/**
	 * Creates the new media.
	 *
	 * @param format
	 *           the format
	 * @param imageQualifier
	 *           the image qualifier
	 * @param imageUrl
	 *           the image url
	 * @param catalogVersion
	 *           the catalog version
	 * @return the media model
	 */
	protected MediaModel createNewMedia(final MediaFormatModel format, final String imageQualifier, final String imageUrl,
			final CatalogVersionModel catalogVersion)
	{

		//Getting MediaFolder by configuration property
		MediaFolderModel folder = null;

		if (StringUtils.isNotEmpty(mediaFolder))
		{
			try
			{
				folder = mediaService.getFolder(mediaFolder);
			}
			catch (IllegalArgumentException | UnknownIdentifierException | AmbiguousIdentifierException e)
			{
				LOG.debug("Unable to find media folder with qualifier: ", e);
			}
		}

		//Getting MimeType from inputstream
		String mimeType = null;

		try
		{
			mimeType = URLConnection.guessContentTypeFromStream(URI.create(imageUrl).toURL().openStream());
		}
		catch (final IOException e)
		{
			LOG.debug("Error getting MimeType from URL:" + imageUrl, e);

			mimeType = defaultMimeType;
		}

		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode(imageQualifier + "/" + format.getQualifier());
		media.setCatalogVersion(catalogVersion);

		media.setMime(StringUtils.defaultIfEmpty(mimeType, defaultMimeType));
		media.setMediaFormat(format);

		//In case of null folder hybris will use "root"
		media.setFolder(folder);

		return media;
	}

	/**
	 * Update mime.
	 *
	 * @param mediaContainer
	 *           the media container
	 */
	protected void updateMime(final MediaContainerModel mediaContainer)
	{

		if (mediaContainer != null)
		{
			for (final MediaModel media : mediaContainer.getMedias())
			{
				media.setMime("image/jpeg");
				getModelService().save(media);
			}
		}
	}

	/**
	 * Gets the media service.
	 *
	 * @return the mediaService
	 */
	public MediaService getMediaService()
	{
		return mediaService;
	}

	/**
	 * Sets the media service.
	 *
	 * @param mediaService
	 *           the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * Gets the media conversion service.
	 *
	 * @return the mediaConversionService
	 */
	public MediaConversionService getMediaConversionService()
	{
		return mediaConversionService;
	}

	/**
	 * Sets the media conversion service.
	 *
	 * @param mediaConversionService
	 *           the mediaConversionService to set
	 */
	public void setMediaConversionService(final MediaConversionService mediaConversionService)
	{
		this.mediaConversionService = mediaConversionService;
	}

	/**
	 * Gets the conversion group service.
	 *
	 * @return the conversionGroupService
	 */
	public ConversionGroupService getConversionGroupService()
	{
		return conversionGroupService;
	}

	/**
	 * Sets the conversion group service.
	 *
	 * @param conversionGroupService
	 *           the conversionGroupService to set
	 */
	public void setConversionGroupService(final ConversionGroupService conversionGroupService)
	{
		this.conversionGroupService = conversionGroupService;
	}

	/**
	 * Gets the model service.
	 *
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets the model service.
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets the catalog version service.
	 *
	 * @return the catalogVersionService
	 */
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * Sets the catalog version service.
	 *
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * Gets the media container service.
	 *
	 * @return the mediaContainerService
	 */
	public SABMMediaContainerService getMediaContainerService()
	{
		return mediaContainerService;
	}

	/**
	 * Sets the media container service.
	 *
	 * @param mediaContainerService
	 *           the mediaContainerService to set
	 */
	public void setMediaContainerService(final SABMMediaContainerService mediaContainerService)
	{
		this.mediaContainerService = mediaContainerService;
	}

	/**
	 * Sets the default conversion group.
	 *
	 * @param defaultConversionGroup
	 *           the defaultConversionGroup to set
	 */
	public void setDefaultConversionGroup(final String defaultConversionGroup)
	{
		this.defaultConversionGroup = defaultConversionGroup;
	}

	/**
	 * Gets the media folder.
	 *
	 * @return the mediaFolder
	 */
	public String getMediaFolder()
	{
		return mediaFolder;
	}

	/**
	 * Sets the media folder.
	 *
	 * @param mediaFolder
	 *           the mediaFolder to set
	 */
	public void setMediaFolder(final String mediaFolder)
	{
		this.mediaFolder = mediaFolder;
	}

	/**
	 * Gets the default conversion group.
	 *
	 * @return the defaultConversionGroup
	 */
	public String getDefaultConversionGroup()
	{
		return defaultConversionGroup;
	}

	/**
	 * Gets the default mime type.
	 *
	 * @return the defaultMimeType
	 */
	public String getDefaultMimeType()
	{
		return defaultMimeType;
	}

}
