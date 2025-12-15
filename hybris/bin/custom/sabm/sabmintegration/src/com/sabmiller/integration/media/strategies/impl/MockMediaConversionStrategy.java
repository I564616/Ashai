/**
 *
 */
package com.sabmiller.integration.media.strategies.impl;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.conversion.MediaConversionException;
import de.hybris.platform.mediaconversion.conversion.MediaConversionStrategy;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;


/**
 * The Class MockMediaConversionStrategy. Generate clones of the images with a new code
 * "{inputmediacode}/{newformatqualifier}"
 */
public class MockMediaConversionStrategy implements MediaConversionStrategy
{

	/** The model service. */
	@Resource
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.mediaconversion.conversion.MediaConversionStrategy#convert(de.hybris.platform.mediaconversion.
	 * MediaConversionService, de.hybris.platform.core.model.media.MediaModel,
	 * de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel)
	 */
	@Override
	public MediaModel convert(final MediaConversionService paramMediaConversionService, final MediaModel paramMediaModel,
			final ConversionMediaFormatModel paramConversionMediaFormatModel) throws MediaConversionException
	{
		final MediaModel clonedMediaModel = modelService.clone(paramMediaModel);

		clonedMediaModel.setCode(paramMediaModel.getCode() + "/" + paramConversionMediaFormatModel.getQualifier());
		clonedMediaModel.setMediaFormat(paramConversionMediaFormatModel);
		clonedMediaModel.setOriginal(paramMediaModel);
		modelService.save(clonedMediaModel);
		return clonedMediaModel;
	}

}
