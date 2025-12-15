/**
 *
 */
package com.sabmiller.webservice.product.converters;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabmiller.webservice.model.ProductImportRecordModel;
import com.sabmiller.webservice.response.ProductImportResponse;


/**
 * @author joshua.a.antony
 *
 */
public class ProductImportRecordReverseConverter implements Converter<ProductImportResponse, ProductImportRecordModel>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public ProductImportRecordModel convert(final ProductImportResponse source) throws ConversionException
	{

		final ProductImportRecordModel model = modelService.create(ProductImportRecordModel.class);
		return convert(source, model);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public ProductImportRecordModel convert(final ProductImportResponse source, final ProductImportRecordModel target)
			throws ConversionException
	{
		target.setHierarchy(source.getHierarchy());
		target.setMaterial(source.getMaterialId());
		target.setError(source.getError());
		target.setOperation(source.getOperation());
		target.setStatus(source.getStatus());
		return target;
	}

}
