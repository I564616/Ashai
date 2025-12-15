package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.AsahiSearchProductData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import com.apb.core.cart.validation.strategy.AsahiBonusCartValidationStrategy;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.data.PackageSizeData;
import com.apb.facades.product.data.UnitVolumeData;

public class AsahiSearchProductPopulator implements Populator<ProductData, AsahiSearchProductData> {

	@Resource(name = "asahiBonusCartValidationStrategy")
	private AsahiBonusCartValidationStrategy asahiBonusCartValidationStrategy;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void populate(final ProductData source, final AsahiSearchProductData target) throws ConversionException {
		target.setUrl(source.getUrl());
		target.setPotentialPromotions(source.getPotentialPromotions());
		target.setName(source.getName());
		target.setBrand(source.getApbBrand());
		target.setVariantOptions(source.getVariantOptions());
		target.setAvailableForPickup(source.getAvailableForPickup());
		target.setCode(source.getCode());
		target.setMultidimensional(source.getMultidimensional());
		target.setPrice(source.getPrice());
		target.setPriceRange(source.getPriceRange());
		target.setImages(source.getImages());
		target.setMinQty(source.getMinQty());
		target.setMaxQty(source.getMaxQty());
		target.setStock(source.getStock());
		target.setAverageRating(source.getAverageRating());
		target.setNumberOfReviews(source.getNumberOfReviews());
		target.setLicenseRequired(source.getLicenseRequired());
		target.setNewProduct(source.getNewProduct());
		setPortalUnitVolume(source, target);
		setPackageSize(source, target);
		target.setIsPromotionActive(source.getIsPromotionActive());
		if(asahiSiteUtil.isApb() || (asahiSiteUtil.isSga() && asahiSiteUtil.isBDECustomer())){
			target.setAllowedBonusQty(Long.valueOf(asahiBonusCartValidationStrategy.getAllowedBonusQuantity(source.getCode())));
		}
		target.setDealsFlag(source.isDealsFlag());
		target.setDealsTitle(source.getDealsTitle());
	}

	private void setPackageSize(final ProductData source, final AsahiSearchProductData target) {
		// TODO Auto-generated method stub
		final PackageSizeData packageSizeData = source.getPackageSize();
		if(null != packageSizeData){
			target.setPackageSize(packageSizeData.getName());
		}

	}

	private void setPortalUnitVolume(final ProductData source, final AsahiSearchProductData target) {
		// TODO Auto-generated method stub
		final UnitVolumeData unitVolumeData = source.getUnitVolume();
		if(null != unitVolumeData){
			target.setPortalUnitVolume(unitVolumeData.getName());
		}
	}

}
