/**
 *
 */
package com.sabmiller.facades.product.converters;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;

import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.facades.product.data.ProductUOMMappingData;


/**
 * @author joshua.a.antony
 *
 */

public class ProductEanReverseConverter implements Converter<ProductData, SABMAlcoholVariantProductEANModel>
{
	private static final Logger LOG = Logger.getLogger(ProductEanReverseConverter.class);

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Resource(name = "productUomReverseConverter")
	private Converter<ProductUOMMappingData, ProductUOMMappingModel> productUomReverseConverter;

	@Override
	public SABMAlcoholVariantProductEANModel convert(final ProductData productData) throws ConversionException
	{
		final String code = productData.getEan();
		final boolean productExist = productService.productExistInOfflineCatalog(code);

		final SABMAlcoholVariantProductEANModel productModel = productExist
				? (SABMAlcoholVariantProductEANModel) productService
						.getProductForCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(), code)
				: modelService.<SABMAlcoholVariantProductEANModel> create(SABMAlcoholVariantProductEANModel.class);

		return convert(productData, productModel);
	}



	@Override
	public SABMAlcoholVariantProductEANModel convert(final ProductData source, final SABMAlcoholVariantProductEANModel target)
			throws ConversionException
	{
		target.setCatalogVersion(catalogVersionDeterminationStrategy.offlineCatalogVersion());
		target.setCode(source.getEan());
		target.setContainer(source.getContainer());
		target.setCapacity(source.getCapacity());
		target.setPresentation(source.getPresentation());
		target.setLength(source.getLength());
		target.setWidth(source.getWidth());
		target.setHeight(source.getHeight());
		target.setWeight(source.getWeight());
		//target.setSizeUnit(source.getSizeUnit());
		//target.setDescription(source.getDescription());
		target.setEan(source.getEan());
		target.setName(source.getName());
		target.setUnit(unitService.getUnitForCode(source.getUnit()));
		target.setUomMappings(Converters.convertAll(source.getUomMappingList(), productUomReverseConverter));

		setProductInUomMappings(target);
		final String hierarchy = source.getHierarchy();

		target.setLevel4(hierarchy.substring(10, 11));
		target.setLevel5(hierarchy.substring(11, 15));
		target.setLevel6(hierarchy.substring(15, 18));

		target.setWetEligible(source.isWetEligible());
		LOG.debug("productCode : " + target.getCode() + " , ean : " + target.getEan());

		return target;

	}

	//TODO : This needs refactor later on, create a new converter instead!
	protected void setProductInUomMappings(final SABMAlcoholVariantProductEANModel eanProductModel)
	{
		for (final ProductUOMMappingModel model : ListUtils.emptyIfNull(eanProductModel.getUomMappings()))
		{
			model.setProduct(eanProductModel);
		}
	}

}
