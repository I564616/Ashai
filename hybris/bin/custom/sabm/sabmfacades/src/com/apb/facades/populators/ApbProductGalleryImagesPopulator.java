package com.apb.facades.populators;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import com.sabmiller.core.constants.SabmCoreConstants;

import de.hybris.platform.commercefacades.product.converters.populator.ProductGalleryImagesPopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;
import com.apb.core.util.AsahiSiteUtil;

public class ApbProductGalleryImagesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends ProductGalleryImagesPopulator<SOURCE, TARGET>
{
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException 
	{
		
		if(!asahiSiteUtil.isCub())
		{
   		// Collect the media containers on the product
   		final List<MediaContainerModel> mediaContainers = new ArrayList<>();
   		collectMediaContainers(productModel, mediaContainers);
   
   		if (!mediaContainers.isEmpty())
   		{
   			final List<ImageData> imageList = new ArrayList<>();
   
   			// fill our image list with the product's existing images
   			if (productData.getImages() != null)
   			{
   				imageList.addAll(productData.getImages());
   			}
   
   			// Use all the images as gallery images
   			for (final MediaContainerModel mediaContainer : mediaContainers)
   			{
   				addImagesInFormats(mediaContainer, ImageDataType.GALLERY, mediaContainer.getOrder(), imageList);
   			}
   
   			for (final ImageData imageData : imageList)
   			{
   				if (imageData.getAltText() == null)
   				{
   					imageData.setAltText(productModel.getName());
   				}
   			}
   
   			// Overwrite the existing list of images
   			productData.setImages(imageList);
   		}
		}
		else
		{
			super.populate(productModel, productData);
		}
	}

}
