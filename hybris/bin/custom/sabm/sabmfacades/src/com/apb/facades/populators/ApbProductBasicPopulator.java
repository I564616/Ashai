package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.FlavourModel;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.FlavourData;
import com.sabmiller.core.constants.SabmCoreConstants;


public class ApbProductBasicPopulator implements Populator<ProductModel, ProductData>
{
	private Converter<BrandModel, BrandData> apbBrandConverter;

	private Converter<FlavourModel, FlavourData> apbFlavourConverter;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		
		if(source instanceof ApbProductModel)
		{
   		final ApbProductModel productModel = (ApbProductModel) source;
   
   		if (null != productModel.getBrand())
   		{
   			target.setApbBrand(apbBrandConverter.convert(productModel.getBrand()));
   		}
   
   		if (null != productModel.getFlavour())
   		{
   			target.setFlavour(apbFlavourConverter.convert(productModel.getFlavour()));
   		}
		}

		//productModel.setma
	}


	public Converter<BrandModel, BrandData> getApbBrandConverter()
	{
		return apbBrandConverter;
	}

	public void setApbBrandConverter(final Converter<BrandModel, BrandData> apbBrandConverter)
	{
		this.apbBrandConverter = apbBrandConverter;
	}

	public Converter<FlavourModel, FlavourData> getApbFlavourConverter()
	{
		return apbFlavourConverter;
	}

	public void setApbFlavourConverter(final Converter<FlavourModel, FlavourData> apbFlavourConverter)
	{
		this.apbFlavourConverter = apbFlavourConverter;
	}



}
