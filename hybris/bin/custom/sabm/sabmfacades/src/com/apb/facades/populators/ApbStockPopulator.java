package com.apb.facades.populators;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.converters.populator.StockPopulator;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.util.Config;
import com.apb.core.util.AsahiSiteUtil;
import jakarta.annotation.Resource;
import java.util.Locale;

public class ApbStockPopulator<SOURCE extends ProductModel, TARGET extends StockData> extends
StockPopulator<SOURCE, TARGET>
{
	private CommerceStockService commerceStockService;
	private BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private EnumerationService enumerationService;

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	@Override
	public void populate(final SOURCE productModel, final TARGET stockData) throws ConversionException
	{
		if(!asahiSiteUtil.isCub())
		{
   		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
   		StockLevelStatus status = getCommerceStockService().getStockLevelStatusForProductAndBaseStore(productModel,
					baseStore);
   		if(status!=null)
   		{
   		stockData.setStockLevelStatusName(enumerationService.getEnumerationName(status, Locale.ENGLISH));
   		stockData.setStockLevelStatus(status);
   		}
   		stockData.setStockLevel(getCommerceStockService().getStockLevelForProductAndBaseStore(productModel, baseStore));
		}
		else
		{
			super.populate(productModel,stockData);
		}
	}

	protected boolean isStockSystemEnabled()
	{
		return getCommerceStockService().isStockSystemEnabled(getBaseStoreService().getCurrentBaseStore());
	}

	protected boolean isStockSystemEnabled(final BaseStoreModel baseStore)
	{
		return getCommerceStockService().isStockSystemEnabled(baseStore);
	}
}
