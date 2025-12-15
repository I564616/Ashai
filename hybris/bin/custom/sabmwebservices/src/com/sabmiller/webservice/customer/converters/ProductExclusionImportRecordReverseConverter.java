/**
 *
 */
package com.sabmiller.webservice.customer.converters;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.springframework.util.Assert;

import com.sabmiller.webservice.model.ProductExclusionImportRecordModel;
import com.sabmiller.webservice.response.ProductExclusionImportResponse;


/**
 * The Class ProductExclusionImportRecordReverseConverter.
 */
public class ProductExclusionImportRecordReverseConverter
		implements Converter<ProductExclusionImportResponse, ProductExclusionImportRecordModel>
{

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public ProductExclusionImportRecordModel convert(final ProductExclusionImportResponse source) throws ConversionException
	{
		final ProductExclusionImportRecordModel model = modelService.create(ProductExclusionImportRecordModel.class);
		return convert(source, model);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public ProductExclusionImportRecordModel convert(final ProductExclusionImportResponse source,
			final ProductExclusionImportRecordModel target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCustomerId(source.getCustomerId());
		target.setProduct(source.getProduct());
		target.setError(source.getError());
		target.setOperation(source.getOperation());
		target.setStatus(source.getStatus());

		return target;
	}
}
