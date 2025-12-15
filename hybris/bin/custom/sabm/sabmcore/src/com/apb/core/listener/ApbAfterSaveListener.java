package com.apb.core.listener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.core.model.ApbProductModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;

	
public class ApbAfterSaveListener implements AfterSaveListener {
	@Resource
	private ModelService modelService;
	
	@Resource
	private MediaService mediaService;
	
	@Resource
	private MediaContainerService mediaContainerService;
	
	@Resource
	FlexibleSearchService flexibleSearchService;
	
	@Resource
	CatalogVersionService catalogVersionService;
	
	private ImageFormatMapping imageFormatMapping;
	
	private static final String MEDIA_FORMAT_THUMBNAIL = "thumbnail";
	private static final String MEDIA_FORMAT_PRODUCT = "product";
	
	private static final Logger LOG = LoggerFactory.getLogger(ApbAfterSaveListener.class);

	@Override
	public void afterSave(final Collection<AfterSaveEvent> events) {
		for (final AfterSaveEvent event : events)
		{
			final int type = event.getType();
			if (AfterSaveEvent.UPDATE == type || AfterSaveEvent.CREATE == type)
			{
				final PK pk = event.getPk();
				//The Product Model type code is "1"
				if (1 == pk.getTypeCode() && modelService.get(pk) instanceof ApbProductModel)
				{
					final ApbProductModel product = modelService.get(pk);
					if(null != product.getPicture()){
						setPrimaryMediaFromProductGallery(product, modelService.isModified(product.getPicture()), modelService.isModified(product.getThumbnail()));
					}
				}
				if( 50 == pk.getTypeCode())
				// The MediaContainer Model type code is "50"
				{
					List<ApbProductModel> products = getProductsAssociatedWithMediaContainer(pk.toString(), modelService.get(pk));
					for(ApbProductModel product : products)
					{
						setPrimaryMediaFromProductGallery(product, modelService.isModified(product.getPicture()), modelService.isModified(product.getThumbnail()));		
					}
				}
			}
		}					
	}

	private void setPrimaryMediaFromProductGallery(ApbProductModel product, boolean isPictureModified, boolean isThumbnailModified) 
	{
		List<MediaContainerModel> galleryImages = product.getGalleryImages();
		if(CollectionUtils.isNotEmpty(galleryImages))
		{
			MediaContainerModel primaryContainer = galleryImages.get(0);
			for(MediaContainerModel container : galleryImages)
			{	
				if(container.getOrder() < primaryContainer.getOrder())
				{
					primaryContainer = container;
				}	
			}
			if(!isPictureModified)
			{
				product.setPicture(getImageForFormat(MEDIA_FORMAT_PRODUCT, primaryContainer));
			}
			if(!isThumbnailModified)
			{
				product.setThumbnail(getImageForFormat(MEDIA_FORMAT_THUMBNAIL, primaryContainer));
			}
			modelService.save(product);

		}
	}
	
	private MediaModel getImageForFormat(String imageFormat, MediaContainerModel mediaContainer) 
	{

		final String mediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat);
		if (mediaFormatQualifier != null)
		{
			final MediaFormatModel mediaFormat = mediaService.getFormat(mediaFormatQualifier);
			if (mediaFormat != null)
			{
				try
				{
					return mediaContainerService.getMediaForFormat(mediaContainer, mediaFormat);
				}
				catch(ModelNotFoundException e)
				{
					LOG.debug("Could not find Media with format "+ mediaFormat.toString() + " in media container with qualifier: " + mediaContainer.getQualifier() + ". Setting media to null.", e);
				}
			}
		}
		
		return null;
		
	}
	
	private List<ApbProductModel> getProductsAssociatedWithMediaContainer(String mediaContainerPk, MediaContainerModel mediaContainer) 
	{
		if(null != mediaContainer.getCatalogVersion())
		{
			catalogVersionService.setSessionCatalogVersion(mediaContainer.getCatalogVersion().getCatalog().getId(), mediaContainer.getCatalogVersion().getVersion());
			StringBuilder query = new StringBuilder();
			query.append("SELECT {").append(ApbProductModel.PK).append("} FROM {").append(ApbProductModel._TYPECODE).append("} WHERE {").append(ApbProductModel.CATALOGVERSION).append("} = ?").append(ApbProductModel.CATALOGVERSION).append(" AND {").append(ApbProductModel.GALLERYIMAGES).append("} LIKE '%").append(mediaContainerPk).append("%'");
			final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query.toString()); 
			fQuery.addQueryParameter(ApbProductModel.CATALOGVERSION, mediaContainer.getCatalogVersion());
			final SearchResult<ApbProductModel> result = flexibleSearchService.search(fQuery);
			List<ApbProductModel> products = result.getResult();
			if(CollectionUtils.isNotEmpty(products))
			{
				return products;
			}
		}
		
		return Collections.emptyList();

	}

	
	protected ImageFormatMapping getImageFormatMapping()
	{
		return imageFormatMapping;
	}
	
	public void setImageFormatMapping(final ImageFormatMapping imageFormatMapping)
	{
		this.imageFormatMapping = imageFormatMapping;
	}

}
