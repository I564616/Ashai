/**
 *
 */
package com.sabmiller.facades.product.converters;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;


/**
 * @author joshua.a.antony
 *
 */
public class ProductMaterialReverseConverter implements Converter<ProductData, SABMAlcoholVariantProductMaterialModel>
{

	private static final Logger LOG = Logger.getLogger(ProductMaterialReverseConverter.class);

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Override
	public SABMAlcoholVariantProductMaterialModel convert(final ProductData productData) throws ConversionException
	{
		final String code = productData.getInternalId();
		final boolean productExist = productService.productExistInOfflineCatalog(code);

		final SABMAlcoholVariantProductMaterialModel productModel = productExist ? (SABMAlcoholVariantProductMaterialModel) productService
				.getProductForCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(), code) : modelService
				.<SABMAlcoholVariantProductMaterialModel> create(SABMAlcoholVariantProductMaterialModel.class);

		return convert(productData, productModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public SABMAlcoholVariantProductMaterialModel convert(final ProductData source,
			final SABMAlcoholVariantProductMaterialModel target) throws ConversionException
	{
		target.setCatalogVersion(catalogVersionDeterminationStrategy.offlineCatalogVersion());
		target.setCode(source.getInternalId());
		target.setContainer(source.getContainer());
		target.setHierarchy(source.getHierarchy());
		target.setUnit(unitService.getUnitForCode(source.getUnit()));

		LOG.debug("productCode : " + target.getCode() + " , ean : " + target.getEan());

		return target;
	}

}
