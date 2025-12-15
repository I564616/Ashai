package com.apb.facades.product.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.price.ApbPriceUpdateFacade;
import com.apb.facades.price.PriceInfoData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;


public class ApbProductVariantFacadeImpl extends ApbProductFacadeImpl
{
	@Resource
	private ApbPriceUpdateFacade apbPriceUpdateFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource
	private SessionService sessionService;
	
	@Resource
	private PriceDataFactory priceDataFactory;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	private static final String GET_PDP_PRICE_FROM_EXTERNAL_SYSTEM = "fetch.pdp.price.from.service";

	@Override
	public ProductData getProductForCodeAndOptions(final String code, final Collection<ProductOption> options)
	{
		if(!asahiSiteUtil.isCub())
		{
		final ProductModel productModel = getProductService().getProductForCode(code);
		Map<String, Long> productQuantityMap = createMapForProduct(productModel);
		ProductData productData = getProductForOptions(productModel, options);
		if (isFetchPriceFromService() && !asahiSiteUtil.isSga())
		{
			PriceInfoData productPriceInfo = apbPriceUpdateFacade.updatePriceInfoData(productQuantityMap, false);
			if (null != productPriceInfo && null != productPriceInfo.getProductPriceInfo()
					&& null != productPriceInfo.getProductPriceInfo().get(0))
			{
				productData.setPrice(productPriceInfo.getProductPriceInfo().get(0).getNetPrice());
				productData.setPriceError(false);
			}
			else
			{
				productData.setPrice(null);
				productData.setPriceError(true);
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

	private void isLicenseRequired(ProductData productData)
	{
		productData.setLicenseRequired(false);
		final UserModel user = getUserService().getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !getUserService().isAnonymousUser(user))
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				AsahiB2BUnitModel asahiB2BUnitModel = (AsahiB2BUnitModel) b2bUnit;
				if (productData.getAlcoholic() && StringUtils.isEmpty(asahiB2BUnitModel.getLiquorLicensenumber()))
				{
					productData.setLicenseRequired(true);
				}
			}
		}
	}

	private Map<String, Long> createMapForProduct(ProductModel product)
	{
		Map<String, Long> productQuantityMap = new HashMap<>();
		productQuantityMap.put(product.getCode(), 1L);
		return productQuantityMap;
	}

	private boolean isFetchPriceFromService()
	{
		return Boolean.parseBoolean(asahiConfigurationService.getString(GET_PDP_PRICE_FROM_EXTERNAL_SYSTEM, "true"));
	}

}
