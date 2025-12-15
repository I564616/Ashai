/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.apb.core.cart.validation.strategy.AsahiBonusCartValidationStrategy;
import com.apb.core.model.ApbProductModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.core.model.UnitVolumeModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.data.PackageSizeData;
import com.apb.facades.product.data.PackageTypeData;
import com.apb.facades.product.data.UnitVolumeData;
import com.apb.integration.data.AsahiProductInfo;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.apb.core.model.BrandModel;
import com.apb.facades.product.data.BrandData;

/**
 * The SABMAlcoholProductPopulator to populate the ProductData attributes from SABMAlcoholProductModel.
 */
public class SABMAlcoholProductPopulator extends ProductPopulator
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMAlcoholProductPopulator.class);

	@Resource(name = "dealsService")
	private DealsService dealsService;
	
	private Converter<PackageTypeModel, PackageTypeData> apbPackageTypeConverter;
	private Converter<PackageSizeModel, PackageSizeData> apbPackageSizeConverter;

	@Resource(name= "apbBrandConverter")
	private Converter<BrandModel, BrandData> apbBrandConverter;

	/** The asahi product unit volume converter. */
	private Converter<UnitVolumeModel, UnitVolumeData> asahiProductUnitVolumeConverter;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private PriceDataFactory priceDataFactory;

	@Resource(name = "asahiBonusCartValidationStrategy")
	private AsahiBonusCartValidationStrategy asahiBonusCartValidationStrategy;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	public static final String NON_ALCOHOLIC_TYPE = "product.code.non.alcoholic.product.apb";

	/**
	 * Populate the target instance from the source instance.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 * @throws ConversionException
	 *            the conversion exception
	 */
	@Override
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		if (source != null && target != null)
		{
			super.populate(source, target);
			
			if(!asahiSiteUtil.isCub())
			{
				apbProductPopulate(source,target);  
			}
			else
			{
   			ProductModel variant = source;
   			SABMAlcoholVariantProductEANModel eanProduct = null;
   			SABMAlcoholProductModel sabmAlcoholProductModel = null;
   
   			if (variant instanceof SABMAlcoholProductModel)
   			{
   				sabmAlcoholProductModel = (SABMAlcoholProductModel) variant;
   			}
   
   			while (variant instanceof VariantProductModel)
   			{
   				if (variant.getClass().equals(SABMAlcoholVariantProductEANModel.class))
   				{
   					eanProduct = (SABMAlcoholVariantProductEANModel) variant;
   					break;
   				}
   
   				variant = ((VariantProductModel) variant).getBaseProduct();
   			}
   
   			if (eanProduct != null)
   			{
   
   				if (StringUtils.isNotEmpty(eanProduct.getSellingName()) && StringUtils.isNotEmpty(eanProduct.getPackConfiguration()))
   				{
   					target.setName(eanProduct.getSellingName());
   					target.setPackConfiguration(eanProduct.getPackConfiguration());
   				}
   				else
   				{
   					target.setName(eanProduct.getName());
   				}
   
   				if (eanProduct.getBaseProduct() instanceof SABMAlcoholProductModel)
   				{
   					sabmAlcoholProductModel = (SABMAlcoholProductModel) eanProduct.getBaseProduct();
   				}
   			}
   
   			if (sabmAlcoholProductModel != null)
   			{
   				target.setBaseName(sabmAlcoholProductModel.getName());
   
   				target.setAbv(sabmAlcoholProductModel.getAbv());
   				target.setStyle(sabmAlcoholProductModel.getStyle());
   				target.setCategoryVariety(sabmAlcoholProductModel.getCategoryVariety());
   				target.setBrand(sabmAlcoholProductModel.getBrand());
   				target.setFindOutMore(sabmAlcoholProductModel.getFindOutMore());
   				target.setFoodMatch(sabmAlcoholProductModel.getFoodMatch());
   			}
   			else
   			{
   				LOG.info("Unable to populate the unkonw source");
   			}
			}
		}
		else
		{
			LOG.error("Unable to populate a null source or null target");
		}
		
		
	}
	
	private void apbProductPopulate(final ProductModel source, final ProductData target)
	{
		final ApbProductModel productModel = (ApbProductModel) source;
		if (null != productModel.getPackageType())
		{
			target.setPackageType(apbPackageTypeConverter.convert(productModel.getPackageType()));
		}

		if (null != productModel.getPackageSize())
		{
			target.setPackageSize(getApbPackageSizeConverter().convert(productModel.getPackageSize()));
		}
		if (null != productModel.getBrand())
		{
			target.setApbBrand(getApbBrandConverter().convert(productModel.getBrand()));
		}
		target.setBackendUnitPerCase(productModel.getBackendUnitPerCase());
		if (null != productModel.getMaxOrderQuantity())
		{
			target.setMaxQty(productModel.getMaxOrderQuantity());
		}

		if (null != productModel.getMinOrderQuantity())
		{
			target.setMinQty(productModel.getMinOrderQuantity());
		}

		if (null != productModel.getPortalUnitVolume())
		{
			target.setUnitVolume(asahiProductUnitVolumeConverter.convert(productModel.getPortalUnitVolume()));
		}
		target.setProductDetail(productModel.getProductDetail());
		setAlcohalType(productModel, target);

		target.setActive(productModel.isActive());
		target.setNewProduct(productModel.getNewProduct());
		if (asahiSiteUtil.isApb() || (asahiSiteUtil.isSga() && asahiSiteUtil.isBDECustomer()))
		{
			target.setAllowedBonusQty(Long.valueOf(asahiBonusCartValidationStrategy.getAllowedBonusQuantity(productModel.getCode())));
		}
		/*
		 * Setting up product price based on session list and promotion attributes as well.
		 */
		if (asahiSiteUtil.isSga())
		{
			final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(target.getCode());
			if (null != product)
			{
				LOG.info("Setting up product price based on session list"+product.getMaterialNumber());
				target.setPrice(
						priceDataFactory.create(PriceDataType.BUY,
								BigDecimal.valueOf((product.getListPrice() != null
										? product.getListPrice()
												+ (product.getContainerDepositLevy() != null ? product.getContainerDepositLevy() : 0.0D)
										: 0.0D)),
								asahiSiteUtil.getCurrency()));
				target.setDiscountPrice(
						priceDataFactory.create(PriceDataType.BUY,
								BigDecimal.valueOf((product.getNetPrice() != null
										? product.getNetPrice()
												+ (product.getContainerDepositLevy() != null ? product.getContainerDepositLevy() : 0.0D)
										: 0.0D)),
								asahiSiteUtil.getCurrency()));
				target.setIsPromotionActive(product.getIsPromoFlag());
				target.setPromotionMsg(product.getPromoText());
				target.setLineDiscount(product.getDiscount());
				target.setPriceError(false);
				LOG.info("Setting up product price based on session list"+target.getPrice().getFormattedValue());
			}
			else
			{
				target.setPrice(null);
				target.setIsPromotionActive(false);
				target.setPriceError(true);
			}
		}

		target.setRank(null != productModel.getRank() ? productModel.getRank() : 1000);
		target.setRecommendedRank(null != productModel.getRecommendedRank() ? productModel.getRecommendedRank() : 1000);
		target.setBagInBox((null == productModel.getBagInBox() ? false : productModel.getBagInBox()));
		
		if(ArticleApprovalStatus.APPROVED.equals(productModel.getApprovalStatus())){
			target.setApproved(true);
		}
	}
	private void setAlcohalType(final ApbProductModel product, final ProductData target)
	{
		final String nonAlcoholicType = asahiConfigurationService.getString(NON_ALCOHOLIC_TYPE, "10");
		final List<String> nonAlcoholicTypeList = new ArrayList<>(Arrays.asList(nonAlcoholicType.split(",")));
		target.setAlcoholic(true);
		if (product.getAlcoholType() != null && nonAlcoholicTypeList.contains(product.getAlcoholType().getCode()))
		{
			target.setAlcoholic(false);
		}
	}

	/**
	 * @return apbpackagesize converter
	 */
	public Converter<PackageSizeModel, PackageSizeData> getApbPackageSizeConverter()
	{
		return apbPackageSizeConverter;
	}

	/**
	 * @param apbPackageSizeConverter
	 */
	public void setApbPackageSizeConverter(final Converter<PackageSizeModel, PackageSizeData> apbPackageSizeConverter)
	{
		this.apbPackageSizeConverter = apbPackageSizeConverter;
	}

	/**
	 * @param apbPackageTypeConverter
	 */
	public void setApbPackageTypeConverter(final Converter<PackageTypeModel, PackageTypeData> apbPackageTypeConverter)
	{
		this.apbPackageTypeConverter = apbPackageTypeConverter;
	}

	/**
	 * @return return packagetype converter
	 */
	public Converter<PackageTypeModel, PackageTypeData> getApbPackageTypeConverter()
	{
		return apbPackageTypeConverter;
	}

	/**
	 * @return the asahiProductUnitVolumeConverter
	 */
	public Converter<UnitVolumeModel, UnitVolumeData> getAsahiProductUnitVolumeConverter()
	{
		return asahiProductUnitVolumeConverter;
	}

	/**
	 * @param asahiProductUnitVolumeConverter
	 *           the asahiProductUnitVolumeConverter to set
	 */
	public void setAsahiProductUnitVolumeConverter(
			final Converter<UnitVolumeModel, UnitVolumeData> asahiProductUnitVolumeConverter)
	{
		this.asahiProductUnitVolumeConverter = asahiProductUnitVolumeConverter;
	}

	public Converter<BrandModel, BrandData> getApbBrandConverter() {
		return apbBrandConverter;
	}

	public void setApbBrandConverter(Converter<BrandModel, BrandData> apbBrandConverter) {
		this.apbBrandConverter = apbBrandConverter;
	}
}
