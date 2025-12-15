package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import com.apb.core.model.AlcoholTypeModel;
import com.apb.core.model.ApbProductModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.facades.product.data.AlcoholTypeData;
import com.apb.facades.product.data.PackageTypeData;
import com.apb.core.util.AsahiSiteUtil;


public class ApbProductAlcoholPopulator implements Populator<ProductModel, ProductData>
{
	private Converter<AlcoholTypeModel, AlcoholTypeData> apbAlcoholTypeConverter;

	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		if(source instanceof ApbProductModel)
		{
		
   		final ApbProductModel productModel = (ApbProductModel) source;
   
   		if (null != productModel.getAlcoholType())
   		{
   			target.setAlcoholType(getApbAlcoholTypeConverter().convert(productModel.getAlcoholType()));
   		}
   
   		if (null != productModel.getAlcoholPercent())
   		{
   			target.setAlcoholPercent(productModel.getAlcoholPercent());
   		}
		}

	}

	public Converter<AlcoholTypeModel, AlcoholTypeData> getApbAlcoholTypeConverter()
	{
		return apbAlcoholTypeConverter;
	}

	public void setApbAlcoholTypeConverter(final Converter<AlcoholTypeModel, AlcoholTypeData> apbAlcoholTypeConverter)
	{
		this.apbAlcoholTypeConverter = apbAlcoholTypeConverter;
	}
}
