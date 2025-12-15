/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.converters.populator.ProductGalleryImagesPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.VariantOptionDataPopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.apb.core.util.AsahiSiteUtil;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.cdlvalue.service.SabmCDLValueService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.facades.stock.populators.CUBStockInformationPopulator;
import com.sabmiller.facades.util.SavePriceUtil;


/**
 * Accelerator specific variant option data converter implementation.
 */
public class AcceleratorVariantOptionDataPopulator extends VariantOptionDataPopulator
{
	/** The price row service. */
	@Resource(name = "priceRowService")
	private SabmPriceRowService priceRowService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource
	private CUBStockInformationPopulator cubStockInformationPopulator;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Resource
	private CUBStockInformationService cubStockInformationService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private SabmCDLValueService sabmCDLValueService;
	@Resource
	private ConfigurationService configurationService;


	private TypeService typeService;
	private MediaService mediaService;
	private MediaContainerService mediaContainerService;
	private ImageFormatMapping imageFormatMapping;
	private Map<String, String> variantAttributeMapping;
	private static final Logger LOG = Logger.getLogger(AcceleratorVariantOptionDataPopulator.class);

	private ProductGalleryImagesPopulator<ProductModel, ProductData> productGalleryImagesPopulator;
	private ProductUOMPopulator productUOMPopulator;
	private static final String USERID_ANONYMOUS = "anonymous";


	@Override
	public void populate(final VariantProductModel source, final VariantOptionData target)
	{
		//ALB Code Start
		if(!asahiSiteUtil.isCub())
		{
			super.populate(source, target);

			final MediaContainerModel mediaContainer = getPrimaryImageMediaContainer(source);
			if (mediaContainer != null)
			{
				final ComposedTypeModel productType = getTypeService().getComposedTypeForClass(source.getClass());
				for (final VariantOptionQualifierData variantOptionQualifier : target.getVariantOptionQualifiers())
				{
					final MediaModel media = getMediaWithImageFormat(mediaContainer, lookupImageFormat(productType, variantOptionQualifier.getQualifier()));
					if (media != null)
					{
						variantOptionQualifier.setImage(getImageConverter().convert(media));
					}
				}
			}
		}
		//ALB Code END

		else
		{
   		populateVariantDetails(source,target);

   		target.setPurchasable(source.getPurchasable());

   		final MediaContainerModel mediaContainer = getPrimaryImageMediaContainer(source);
   		if (mediaContainer != null)
   		{
   			final ComposedTypeModel productType = getTypeService().getComposedTypeForClass(source.getClass());
   			for (final VariantOptionQualifierData variantOptionQualifier : target.getVariantOptionQualifiers())
   			{
   				final MediaModel media = getMediaWithImageFormat(mediaContainer,
   						lookupImageFormat(productType, variantOptionQualifier.getQualifier()));
   				if (media != null)
   				{
   					variantOptionQualifier.setImage(getImageConverter().convert(media));
   				}
   			}
   		}

   		//add by xiaowu for other packages attributes.
   		if (source.getBaseProduct() != null)
   		{
   			//Override productData name if sellingName is not empty
   			if (StringUtils.isNotEmpty(source.getSellingName()) && StringUtils.isNotEmpty(source.getPackConfiguration()))
   			{
   				target.setName(source.getSellingName());
   				target.setPackConfiguration(source.getPackConfiguration());
   			}
   			else
   			{
   				target.setName(source.getName());
   			}
   			populateVariantProductData(source, target);
   			//populating cub Stock information
   			populateCUBStockInformation(source, target);
   		}

   		try
   		{
   			populatePriceData(source, target);
   		}
   		catch (final Exception e)
   		{
   			LOG.debug("Unable to fetch the savings");
   		}
		}
	}

	/**
	 * This is almost the replication of the parent super.populate but removed unnecessary pricing call
	 * @param source
	 * @param target
	 */
	protected void populateVariantDetails(final VariantProductModel source, final VariantOptionData target) {
		if (source.getBaseProduct() != null) {
			final List<VariantAttributeDescriptorModel> descriptorModels = getVariantsService().getVariantAttributesForVariantType(
					source.getBaseProduct().getVariantType());

			final Collection<VariantOptionQualifierData> variantOptionQualifiers = new ArrayList<VariantOptionQualifierData>();
			for (final VariantAttributeDescriptorModel descriptorModel : descriptorModels) {
				// Create the variant qualifier
				final VariantOptionQualifierData variantOptionQualifier = new VariantOptionQualifierData();
				final String qualifier = descriptorModel.getQualifier();
				variantOptionQualifier.setQualifier(qualifier);
				variantOptionQualifier.setName(descriptorModel.getName());
				// Lookup the value
				final Object variantAttributeValue = lookupVariantAttributeName(source, qualifier);
				variantOptionQualifier.setValue(variantAttributeValue == null ? "" : variantAttributeValue.toString());

				// Add to list of variants
				variantOptionQualifiers.add(variantOptionQualifier);
			}
			target.setVariantOptionQualifiers(variantOptionQualifiers);
			target.setCode(source.getCode());
			target.setUrl(getProductModelUrlResolver().resolve(source));
			target.setStock(getStockConverter().convert(source));

		}
	}

	protected void populatePriceData(final VariantProductModel source, final VariantOptionData target)
	{

		if (!getUserService().getCurrentUser().getUid().equals(USERID_ANONYMOUS)
				&& source.getClass().equals(SABMAlcoholVariantProductEANModel.class))
		{
			//According to the productModel achieve PriceRowModel
			final PriceRowModel priceRow = priceRowService.getPriceRowByProduct(source);
			final PriceDataType priceType = PriceDataType.BUY;

			if (null != priceRow)
			{
				BigDecimal cdlPrice = null;
				BigDecimal netPrice = BigDecimal.valueOf(SavePriceUtil.checkDoubleEmpty(priceRow.getPrice()));
				final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) source;

				if (null != eanProduct.getLevel4()
						&& ("C".equalsIgnoreCase(eanProduct.getLevel4()) || "N".equalsIgnoreCase(eanProduct.getLevel4()) || "P".equalsIgnoreCase(eanProduct.getLevel4())))
				{
					final String presentation = eanProduct.getPresentation();
					cdlPrice = sabmCDLValueService.getCDLPrice(eanProduct.getLevel4(), presentation);
					netPrice = null != cdlPrice ? netPrice.add(cdlPrice) : netPrice;
				}

				if (eanProduct.getWetEligible() != null && eanProduct.getWetEligible())
				{

					final BigDecimal wetPercentage = this.configurationService.getConfiguration()
							.getBigDecimal(SabmCoreConstants.CUB_WET_PRICE_PERCENTAGE);
					netPrice = null != wetPercentage ? (netPrice.multiply(wetPercentage)).setScale(2, BigDecimal.ROUND_HALF_UP)
							: netPrice;

				}
				//  populate the attribute priceRow.price to productData.price
				target.setPriceData(getPriceData(priceType, netPrice, priceRow.getCurrency()));

				// calculated  the SavingsPrice by BasePrice subtract Price
				target.setSavingsPrice(SavePriceUtil.getSavingsPrice(priceType, priceRow, commonI18NService, getPriceDataFactory()));
			}
			else
			{
				LOG.warn("Unable to find priceRowModel for product: while getting variant [" + source.getCode() + "]");
			}
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.warn("The user not logged in ");
			}
		}

	}

	@SuppressWarnings("unused")
	private PriceData getPriceData(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currencyModel)
	{
		return getPriceDataFactory().create(priceType, value, currencyModel);
	}


	protected MediaModel getMediaWithImageFormat(final MediaContainerModel mediaContainer, final String imageFormat)
	{
		if (mediaContainer != null && imageFormat != null)
		{
			final String mediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat);
			if (mediaFormatQualifier != null)
			{
				final MediaFormatModel mediaFormat = getMediaService().getFormat(mediaFormatQualifier);
				if (mediaFormat != null)
				{
					return getMediaContainerService().getMediaForFormat(mediaContainer, mediaFormat);
				}
			}
		}
		return null;
	}

	protected String lookupImageFormat(final ComposedTypeModel productType, final String attributeQualifier)
	{
		if (productType == null)
		{
			return null;
		}

		// Lookup the image format mapping
		final String key = productType.getCode() + "." + attributeQualifier;
		final String imageFormat = getVariantAttributeMapping().get(key);

		// Try super type of not found for this type
		return imageFormat != null ? imageFormat : lookupImageFormat(productType.getSuperType(), attributeQualifier);
	}

	protected MediaContainerModel getPrimaryImageMediaContainer(final VariantProductModel variantProductModel)
	{
		final MediaModel picture = variantProductModel.getPicture();
		if (picture != null)
		{
			return picture.getMediaContainer();
		}
		return null;
	}

	protected void populateVariantProductData(final VariantProductModel source, final VariantOptionData target)
	{
		final ProductData productData = new ProductData();
		getProductGalleryImagesPopulator().populate(source, productData);
		target.setImages(productData.getImages());
		getProductUOMPopulator().populate(source, productData);
		target.setUomList(productData.getUomList());
	}


	protected void populateVariantProductImages(final VariantProductModel source, final VariantOptionData target)
	{
		final ProductData productData = new ProductData();
		getProductGalleryImagesPopulator().populate(source, productData);
		target.setImages(productData.getImages());
	}

	protected void populateCUBStockInformation(final VariantProductModel source, final VariantOptionData target)
	{
		SABMAlcoholVariantProductEANModel eanProduct = null;
		if (source.getClass().equals(SABMAlcoholVariantProductEANModel.class))
		{
			eanProduct = (SABMAlcoholVariantProductEANModel) source;
			try
			{
				final String productSku = productService.getMaterialCodeFromEan(source.getCode());
				//Set CUB Stock Status
				final B2BUnitModel parentB2bUnit = b2bCommerceUnitService.getRootUnit();
				final PlantModel plant = parentB2bUnit.getPlant();
				if (plant != null)
				{
					final CUBStockInformationModel cubStockInformationModel = cubStockInformationService
							.getCUBStockInformationForProductAndPlant(productSku, plant);

					if (cubStockInformationModel != null)
					{
						setStockStatus(cubStockInformationModel, target);
					}
				}
			}
			catch (final Exception e)
			{
				LOG.info("Attribute sku in not set: {}" + source);
			}
		}
	}

	private void setStockStatus(final CUBStockInformationModel cubStockInformationModel, final VariantOptionData target)
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


	protected TypeService getTypeService()
	{
		return typeService;
	}

	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected MediaContainerService getMediaContainerService()
	{
		return mediaContainerService;
	}

	public void setMediaContainerService(final MediaContainerService mediaContainerService)
	{
		this.mediaContainerService = mediaContainerService;
	}

	protected ImageFormatMapping getImageFormatMapping()
	{
		return imageFormatMapping;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	public void setImageFormatMapping(final ImageFormatMapping imageFormatMapping)
	{
		this.imageFormatMapping = imageFormatMapping;
	}

	protected Map<String, String> getVariantAttributeMapping()
	{
		return variantAttributeMapping;
	}

	public void setVariantAttributeMapping(final Map<String, String> variantAttributeMapping)
	{
		this.variantAttributeMapping = variantAttributeMapping;
	}

	public ProductGalleryImagesPopulator<ProductModel, ProductData> getProductGalleryImagesPopulator()
	{
		return productGalleryImagesPopulator;
	}

	public void setProductGalleryImagesPopulator(
			final ProductGalleryImagesPopulator<ProductModel, ProductData> productGalleryImagesPopulator)
	{
		this.productGalleryImagesPopulator = productGalleryImagesPopulator;
	}

	public ProductUOMPopulator getProductUOMPopulator()
	{
		return productUOMPopulator;
	}

	public void setProductUOMPopulator(final ProductUOMPopulator productUOMPopulator)
	{
		this.productUOMPopulator = productUOMPopulator;
	}

}
