/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;

import de.hybris.platform.variants.model.VariantProductModel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.deals.services.DealsService;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * This class SABMProductBasicPopulator
 *
 * @author xue.zeng
 *
 */
public class SABMProductBasicPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
		extends ProductBasicPopulator<SOURCE, TARGET>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMProductBasicPopulator.class);

	@Resource(name = "dealsService")
	private DealsService dealsService;
	
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator#populate(de.hybris.platform.
	 * core.model.product.ProductModel, de.hybris.platform.commercefacades.product.data.ProductData)
	 */
	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		if(!asahiSiteUtil.isCub())
		{
			super.populate(productModel, productData);
		}
		else
		{
		productData.setManufacturer((String) getProductAttribute(productModel, ProductModel.MANUFACTURERNAME));
		productData.setAverageRating(productModel.getAverageRating());

		if (productModel.getVariantType() != null)
		{
			productData.setVariantType(productModel.getVariantType().getCode());
		}

		if (productModel instanceof VariantProductModel)
		{
			final VariantProductModel variantProduct = (VariantProductModel) productModel;
			productData.setBaseProduct(variantProduct.getBaseProduct() != null ? variantProduct.getBaseProduct().getCode() : null);
		}

		//Override productData name if sellingName is not empty
		if (StringUtils.isNotEmpty(productModel.getSellingName()) && StringUtils.isNotEmpty(productModel.getPackConfiguration()))
		{
			productData.setName(productModel.getSellingName());
			productData.setPackConfiguration(productModel.getPackConfiguration());
		}
		else
		{
			productData.setName(productModel.getName());
		}

		// convert product lifeCycleStatus
		productData.setSearchable(BooleanUtils.isTrue(productModel.getSearchable()));
		productData.setPurchasable(BooleanUtils.isTrue(productModel.getPurchasable()));
		productData.setVisible(BooleanUtils.isTrue(productModel.getVisible()));

		//SAB-76 add newProductFlag to productData
		productData.setNewProductFlag(BooleanUtils.isTrue(productModel.getIsNewProduct()));
		}

	}

}
