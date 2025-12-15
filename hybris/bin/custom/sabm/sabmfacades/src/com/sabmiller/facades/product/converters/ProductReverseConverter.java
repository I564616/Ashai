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

import com.sabmiller.core.enums.AlcoholCategoryAttribute;
import com.sabmiller.core.enums.SAPAvailabilityStatus;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.product.strategy.HybrisCategoryInfoDeterminationStrategy;


/**
 * @author joshua.a.antony
 *
 */
public class ProductReverseConverter implements Converter<ProductData, SABMAlcoholProductModel>
{
	private static final Logger LOG = Logger.getLogger(ProductMaterialReverseConverter.class);


	@Resource(name = "hybrisCategoryInfoDeterminationStrategy")
	private HybrisCategoryInfoDeterminationStrategy hybrisCategoryInfoDeterminationStrategy;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;


	private SABMAlcoholProductModel lookup(final ProductData productData)
	{
		final String alcProductCode = productData.getHierarchy().substring(0, 10);

		final boolean alcProductExist = productService.productExistInOfflineCatalog(alcProductCode);

		if (alcProductExist)
		{
			return (SABMAlcoholProductModel) productService
					.getProductForCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(), alcProductCode);
		}

		// Below commented code is not required since we have to create new Alcohol product if it doesn't exists
		//and dont look for ean's base
		/*
		 * final boolean eanProductExist = productService.productExistInOfflineCatalog(productData.getEan());
		 *
		 * if (eanProductExist) { final SABMAlcoholVariantProductEANModel eanProductModel =
		 * (SABMAlcoholVariantProductEANModel) productService
		 * .getProductForCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(), productData.getEan()); return
		 * (SABMAlcoholProductModel) eanProductModel.getBaseProduct(); }
		 */
		return modelService.create(SABMAlcoholProductModel.class);
	}

	@Override
	public SABMAlcoholProductModel convert(final ProductData productData) throws ConversionException
	{
		return convert(productData, lookup(productData));
	}

	@Override
	public SABMAlcoholProductModel convert(final ProductData source, final SABMAlcoholProductModel target)
			throws ConversionException
	{
		final String alcProductCode = source.getHierarchy().substring(0, 10);

		target.setCatalogVersion(catalogVersionDeterminationStrategy.offlineCatalogVersion());
		target.setCode(alcProductCode);
		target.setSapAvailabilityStatus(SAPAvailabilityStatus.valueOf(source.getSapAvailability()));
		//		target.setDescription(source.getDescription());
		target.setPackConfiguration(source.getPackConfiguration());
		target.setAbv(source.getAbv());
		target.setCategoryAttribute(AlcoholCategoryAttribute
				.valueOf(hybrisCategoryInfoDeterminationStrategy.deriveCategoryName(source.getCategoryAttribute())));
		target.setStyle(source.getStyle());
		target.setCategoryVariety(source.getCategoryVariety());
		target.setBrand(source.getBrand());
		target.setSubBrand(source.getSubBrand());
		//target.setName(source.getName());
		target.setUnit(unitService.getUnitForCode(source.getUnit()));

		target.setLevel1(alcProductCode.substring(0, 4));
		target.setLevel2(alcProductCode.substring(4, 7));
		target.setLevel3(alcProductCode.substring(7, 10));

		LOG.debug("productCode : " + target.getCode() + " , ean : " + target.getEan());

		return target;
	}
}
