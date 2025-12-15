package com.apb.core.replacement.strategy;

import java.lang.reflect.Method;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.support.MethodReplacer;

import com.sabmiller.core.constants.SabmCoreConstants;

import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import com.apb.core.util.AsahiSiteUtil;

public class ApbReplacementCommerceCartStrategy implements MethodReplacer {

	protected static final int DEFAULT_FORCE_IN_STOCK_MAX_QUANTITY = 9999;
	
	protected long forceInStockMaxQuantity = DEFAULT_FORCE_IN_STOCK_MAX_QUANTITY;
	
	@Resource
	private BaseStoreService baseStoreService;
	
	@Resource
	private CommerceStockService commerceStockService;
	
	@Resource 
	BaseSiteService baseSiteService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Override
	public Object reimplement(Object o, Method m, Object[] args) throws Throwable {
		final ProductModel productModel = (ProductModel) args[0];
		final PointOfServiceModel pointOfServiceModel = (PointOfServiceModel) args[1];

		return getAvailableStockLevel(productModel, pointOfServiceModel);
	}

	protected long getAvailableStockLevel(final ProductModel productModel, final PointOfServiceModel pointOfServiceModel)
	{
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
		
		if(!asahiSiteUtil.isCub())
		{
   		if (!commerceStockService.isStockSystemEnabled(baseStore))
   		{
   			return null != productModel.getMaxOrderQuantity() ? productModel.getMaxOrderQuantity() : getMaxQuantityforSite();
   		}
   		else
   		{
   			return commerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStore).longValue();		
   		}
		}
		else
		{
   		if (!commerceStockService.isStockSystemEnabled(baseStore))
   		{
   			return DEFAULT_FORCE_IN_STOCK_MAX_QUANTITY;
   		}
   		else
   		{
   			final Long availableStockLevel;
   
   			if (pointOfServiceModel == null)
   			{
   				availableStockLevel = commerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStore);
   			}
   			else
   			{
   				availableStockLevel = commerceStockService.getStockLevelForProductAndPointOfService(productModel,
   						pointOfServiceModel);
   			}
   
   			if (availableStockLevel == null)
   			{
   				return DEFAULT_FORCE_IN_STOCK_MAX_QUANTITY;
   			}
   			else
   			{
   				return availableStockLevel.longValue();
   			}
   		}
		}
	}
	
	private long getMaxQuantityforSite()
	{
		return null != baseSiteService.getCurrentBaseSite().getMaxOrderQty() ? baseSiteService.getCurrentBaseSite().getMaxOrderQty() : DEFAULT_FORCE_IN_STOCK_MAX_QUANTITY;
	}
	
}
