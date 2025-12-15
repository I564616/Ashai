package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.FlavourModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.core.model.ProductGroupModel;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.FlavourData;
import com.apb.facades.product.data.PackageTypeData;
import com.apb.facades.product.data.ProductGroupData;


public class ApbProductStandardPopulator implements Populator<ProductModel, ProductData>
{
	private Converter<PackageTypeModel, PackageTypeData> apbPackageTypeConverter;

	private Converter<ProductGroupModel, ProductGroupData> apbProductGroupConverter;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		if(source instanceof ApbProductModel)
		{
   		final ApbProductModel productModel = (ApbProductModel) source;
   
   		if (null != productModel.getPackageType())
   		{
   			target.setPackageType(getApbPackageTypeConverter().convert(productModel.getPackageType()));
   		}
		}


		/*
		 * if (null != productModel.getproduct()) {
		 * target.setProductGroup(apbFlavourConverter.convert(productModel.getproductg)); } }
		 */

	}

	public Converter<PackageTypeModel, PackageTypeData> getApbPackageTypeConverter()
	{
		return apbPackageTypeConverter;
	}


	public void setApbPackageTypeConverter(final Converter<PackageTypeModel, PackageTypeData> apbPackageTypeConverter)
	{
		this.apbPackageTypeConverter = apbPackageTypeConverter;
	}

	public Converter<ProductGroupModel, ProductGroupData> getApbProductGroupConverter()
	{
		return apbProductGroupConverter;
	}

	public void setApbProductGroupConverter(final Converter<ProductGroupModel, ProductGroupData> apbProductGroupConverter)
	{
		this.apbProductGroupConverter = apbProductGroupConverter;
	}
}
