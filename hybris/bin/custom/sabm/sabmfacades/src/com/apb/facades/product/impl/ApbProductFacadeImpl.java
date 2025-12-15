/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.facades.product.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;

import com.sabmiller.core.constants.SabmCoreConstants;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.model.ApbProductModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.price.ApbPriceUpdateFacade;
import com.apb.facades.price.PriceInfoData;
import com.apb.facades.product.ApbProductFacade;


/**
 * The Class ApbProductFacadeImpl.
 */
public class ApbProductFacadeImpl extends DefaultProductFacade<ProductModel> implements ApbProductFacade
{
	private static final String ACTIVE = "ACTIVE";
	private static final String INACTIVE = "INACTIVE";

	/** The apb product basic reverse converter. */
	private Converter<ProductData, ApbProductModel> apbProductBasicReverseConverter;


	/** The product service. */
	@Resource(name = "apbProductReferenceService")
	private ApbProductReferenceService apbProductReferenceService;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Resource
	private ApbPriceUpdateFacade apbPriceUpdateFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;


	private static final String GET_PDP_PRICE_FROM_EXTERNAL_SYSTEM = "fetch.pdp.price.from.service";
	private static final String NEW_PRODUCT_DEFAULT_STATUS = "new.product.default.status";

	/**
	 * Import products.
	 *
	 * @param productData
	 *           the product data
	 */
	@Override
	public void importProducts(final ProductData productData, final String siteUid)
	{

		// Fetching Product based on code
		ApbProductModel apbProduct = (ApbProductModel) this.apbProductReferenceService.getProductForCode(
				this.catalogVersionService.getCatalogVersion(asahiSiteUtil.getCatalogId(productData.getCompanyCode()), "Staged"),
				productData.getCode());
		// Check if this product already exist in hybris if yes then update otherwise create new.
		if (null != apbProduct)
		{
			// update existing product
			apbProduct = this.apbProductBasicReverseConverter.convert(productData, apbProduct);

			// saving existing product into hybris database
			getModelService().save(apbProduct);
		}
		else
		{

			//create new product in hybris database
			ApbProductModel productModel = getModelService().create(ApbProductModel.class);

			//calling converter to populate the productModel
			productModel = this.apbProductBasicReverseConverter.convert(productData, productModel);

			if (!ACTIVE.equalsIgnoreCase(asahiConfigurationService.getString(NEW_PRODUCT_DEFAULT_STATUS, INACTIVE)))
			{
				productModel.setActive(Boolean.FALSE);
				productModel.setApprovalStatus(ArticleApprovalStatus.UNAPPROVED);
			}
			//saving new product into hybris database
			getModelService().save(productModel);
		}
	}

	@Override
	public ProductData getProductForCodeAndOptions(final String code, final Collection<ProductOption> options)
	{
		if(!asahiSiteUtil.isCub())
		{
   		final ProductModel productModel = getProductService().getProductForCode(code);
   		final Map<String, Long> productQuantityMap = createMapForProduct(productModel);
   		final ProductData productData = getProductForOptions(productModel, options);
   		if (isFetchPriceFromService())
   		{
   			final PriceInfoData productPriceInfo = apbPriceUpdateFacade.updatePriceInfoData(productQuantityMap, false);
   			if (null != productPriceInfo && null != productPriceInfo.getProductPriceInfo()
   					&& null != productPriceInfo.getProductPriceInfo().get(0))
   			{
   				productData.setPrice(productPriceInfo.getProductPriceInfo().get(0).getNetPrice());
   			}
   			isLicenseRequired(productData);
   		}
   		return productData;
		}
		else
		{
			return super.getProductForCodeAndOptions(code, options);
		}
	}

	private void isLicenseRequired(final ProductData productData)
	{

		final UserModel user = getUserService().getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !getUserService().isAnonymousUser(user))
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnitModel = (AsahiB2BUnitModel) b2bUnit;
				if (productData.getAlcoholic() && StringUtils.isEmpty(asahiB2BUnitModel.getLiquorLicensenumber()))
				{
					productData.setLicenseRequired(true);
				}

			}
		}
	}

	private Map<String, Long> createMapForProduct(final ProductModel product)
	{
		final Map<String, Long> productQuantityMap = new HashMap<>();
		productQuantityMap.put(product.getCode(), 1L);
		return productQuantityMap;
	}

	private boolean isFetchPriceFromService()
	{
		return Boolean.parseBoolean(asahiConfigurationService.getString(GET_PDP_PRICE_FROM_EXTERNAL_SYSTEM, "true"));
	}

	/**
	 * Gets the apb product basic reverse converter.
	 *
	 * @return the apbProductBasicReverseConverter
	 */
	public Converter<ProductData, ApbProductModel> getApbProductBasicReverseConverter()
	{
		return apbProductBasicReverseConverter;
	}

	/**
	 * Sets the apb product basic reverse converter.
	 *
	 * @param apbProductBasicReverseConverter
	 *           the apbProductBasicReverseConverter to set
	 */
	public void setApbProductBasicReverseConverter(final Converter<ProductData, ApbProductModel> apbProductBasicReverseConverter)
	{
		this.apbProductBasicReverseConverter = apbProductBasicReverseConverter;
	}

}
