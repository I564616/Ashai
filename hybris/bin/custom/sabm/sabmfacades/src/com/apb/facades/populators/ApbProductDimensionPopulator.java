package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.apb.core.model.ApbProductModel;


public class ApbProductDimensionPopulator implements Populator<ProductModel, ProductData>
{

	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		if(source instanceof ApbProductModel)
		{
   		final ApbProductModel productModel = (ApbProductModel) source;
   
   		if (null != productModel.getDepth())
   		{
   			target.setDepth(productModel.getDepth());
   
   		}
   
   		if (null != productModel.getWidth())
   		{
   			target.setApbWidth(productModel.getWidth());
   		}
   
   		if (null != productModel.getHeight())
   		{
   			target.setApbHeight(productModel.getHeight());
   		}
		}
	}
}
