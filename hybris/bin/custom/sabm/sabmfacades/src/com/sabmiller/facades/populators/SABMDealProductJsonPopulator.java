/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaService;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealProductJson;


/**
 * The Class SABMDealProductJsonPopulator.
 */
public class SABMDealProductJsonPopulator implements Populator<ProductModel, DealProductJson>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDealProductJsonPopulator.class);

	/** The media service. */
	@Resource
	private MediaService mediaService;

	@Resource
	private UrlResolver<ProductModel> productModelUrlResolver;

	/** The media format string. */
	@Value(value = "${product.deal.media.format:SABMNormalFormat}")
	private String mediaFormatString;

	/** The media format. */
	private MediaFormatModel format;

	@Resource
	private Converter<ProductModel, ProductData> dealProductJsonConverter;

	@Resource
	private CUBStockInformationService cubStockInformationService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ProductModel source, final DealProductJson target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		LOG.debug("Populating product json: [{}]", source);

		SABMAlcoholVariantProductEANModel eanProduct = null;

		if (source instanceof SABMAlcoholVariantProductMaterialModel
				&& ((SABMAlcoholVariantProductMaterialModel) source).getBaseProduct() instanceof SABMAlcoholVariantProductEANModel)
		{
			eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) source).getBaseProduct();
		}

		if (!eanProduct.getPurchasable())
		{
			throw new ConversionException("Product[" + source.getCode() + "] can not be purchased.");
		}

		//Populating Stock Status
		populateStockStatus(source.getCode(), target);

		//Override productData name if sellingName is not empty
		if (StringUtils.isNotEmpty(eanProduct.getSellingName()) && StringUtils.isNotEmpty(eanProduct.getPackConfiguration()))
		{
			target.setTitle(eanProduct.getSellingName());
			target.setPackConfig(eanProduct.getPackConfiguration());
		}
		else
		{
			target.setTitle(eanProduct.getName());
		}

		if (CollectionUtils.isNotEmpty(eanProduct.getGalleryImages()) && getFormat() != null)
		{
			try
			{
				final MediaModel mediaByFormat = mediaService.getMediaByFormat(eanProduct.getGalleryImages().get(0), getFormat());

				if (mediaByFormat != null)
				{
					target.setImage(mediaByFormat.getURL());
				}
			}
			catch (ModelNotFoundException | IllegalArgumentException e)
			{
				LOG.debug("Image with format: [{}] not found in container: {}", getFormat(), eanProduct.getGalleryImages().get(0), e);
			}
		}

		target.setUrl(productModelUrlResolver.resolve(eanProduct));

		if (target instanceof DealBaseProductJson)
		{
			final DealBaseProductJson targetDealBaseProductJson = (DealBaseProductJson) target;
			targetDealBaseProductJson.setProductCode(source.getCode());

			if (eanProduct.getUnit() != null)
			{
				targetDealBaseProductJson.setUomP(eanProduct.getUnit().getPluralName());
				targetDealBaseProductJson.setUomS(eanProduct.getUnit().getName());
			}

			final ProductData productData = dealProductJsonConverter.convert(source);
			targetDealBaseProductJson.setBrand(productData.getBrand());
			targetDealBaseProductJson.setCategories(productData.getCategories());
			targetDealBaseProductJson.setPrice(productData.getPrice());
		}
	}

	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	protected MediaFormatModel getFormat()
	{
		if (format == null)
		{
			format = mediaService.getFormat(mediaFormatString);
		}
		return format;
	}


	public Converter<ProductModel, ProductData> getDealProductJsonConverter()
	{
		return dealProductJsonConverter;
	}


	public void setProductConverter(final Converter<ProductModel, ProductData> dealProductJsonConverter)
	{
		this.dealProductJsonConverter = dealProductJsonConverter;
	}

	private void populateStockStatus(final String productSKU, final DealProductJson target)
	{
		try
		{
			final B2BUnitModel parentB2bUnit = b2bCommerceUnitService.getParentUnit();
			final PlantModel plant = parentB2bUnit.getPlant();
			if (plant != null)
			{
				CUBStockInformationModel cubStockInformationModel = cubStockInformationService
						.getCUBStockInformationForProductAndPlant(productSKU, plant);

				if (cubStockInformationModel != null)
				{
					/*if (plant.getFallbackPlant() != null
							&& cubStockInformationModel.getStockStatus().equals(CUBStockStatus.OUTOFSTOCK))
					{
						cubStockInformationModel = cubStockInformationService.getCUBStockInformationForProductAndPlant(productSKU,
								plant.getFallbackPlant());
					}*/
					setStockStatus(cubStockInformationModel, target);
				}
			}
		}
		catch (final ModelNotFoundException e)
		{
			LOG.warn("Model not found for EAN product or Plant:");
		}
		catch (final Exception e)
		{
			LOG.warn(e.getMessage());
		}
	}

	private void setStockStatus(final CUBStockInformationModel cubStockInformationModel, final DealProductJson target)
	{
		if (cubStockInformationModel != null)
		{
			if (cubStockInformationModel.getStockStatus().equals(CUBStockStatus.OUTOFSTOCK))
			{
				if (sabmConfigurationService.isLowStockFlagEnforced())
				{
					target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
				}
				else
				{
					target.setCubStockStatus(StockLevelStatus.OUTOFSTOCK);
				}
			}
			else if (cubStockInformationModel.getStockStatus().equals(CUBStockStatus.LOWSTOCK))
			{
				target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
			}
		}
	}

}
