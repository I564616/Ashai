/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.CategoryPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.util.AsahiSiteUtil;


/**
 * Converter implementation for {@link de.hybris.platform.category.model.CategoryModel} as source and
 * {@link de.hybris.platform.commercefacades.product.data.CategoryData} as target type.
 */
public class SABMCategoryPopulator extends CategoryPopulator
{
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;


	@Override
	public void populate(final CategoryModel source, final CategoryData target)
	{
		super.populate(source, target);
		if(asahiSiteUtil.isCub())
		{
   		final Collection<MediaModel> sourceImages = source.getNormal();
   
   		final Collection<ImageData> targetImages = new ArrayList<ImageData>();
   
   		if (CollectionUtils.isNotEmpty(sourceImages))
   		{
   
   			for (final MediaModel image : sourceImages)
   			{
   				if (image != null)
   				{
   					final ImageData imageData = getImageConverter().convert(image);
   					targetImages.add(imageData);
   				}
   			}
   			target.setImages(targetImages);
   		}
		}
	}

}
