/**
 *
 */
package com.sabmiller.core.stock;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;


/**
 * @author joshua.a.antony
 *
 */
public class DefaultSabmCommerceStockService extends DefaultCommerceStockService
{

	private static final Logger LOG = Logger.getLogger(DefaultSabmCommerceStockService.class);
	protected static final int DEFAULT_FORCE_IN_STOCK_MAX_QUANTITY = 9999;
	private static final String VALIDATE_PRODUCT_NOT_NULL = "product cannot be null";
	
	@Resource
	CartService cartService;

	@Resource
	BaseSiteService baseSiteService;

	@Resource
	BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private UserService userService;
	
	@Resource
	private StockLevelDao stockLevelDao;
	
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Override
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		if(!asahiSiteUtil.isCub())
		{
			validateParameterNotNull(product, VALIDATE_PRODUCT_NOT_NULL);
			if(asahiSiteUtil.isSga() && Boolean.valueOf(asahiConfigurationService.getString("sga.product.status.available", "false")))
			{
				final StockLevelStatus status = getStockLevelForSGA(product);
				if(status!=null)
				{
					return status;
				}
			}
			
			long stock = getInStockMaxQuantity(product);
			return stock < 0L ? StockLevelStatus.OUTOFSTOCK : stock == 0L ? StockLevelStatus.NOSTOCK : StockLevelStatus.INSTOCK;
		}
		return StockLevelStatus.INSTOCK;
	}

	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		if(!asahiSiteUtil.isCub())
		{
			validateParameterNotNull(product, VALIDATE_PRODUCT_NOT_NULL);
			long stock =  getInStockMaxQuantity(product);
   	   return stock <= 0?0:stock;
		}
			return Long.MAX_VALUE;
		
	}

	@Override
	public StockLevelStatus getStockLevelStatusForProductAndPointOfService(final ProductModel product,
			final PointOfServiceModel pointOfService)
	{
		if(!asahiSiteUtil.isCub())
		{
			validateParameterNotNull(product, VALIDATE_PRODUCT_NOT_NULL);
			long stock = getInStockMaxQuantity(product);
			return stock < 0L ? StockLevelStatus.OUTOFSTOCK : stock == 0L ? StockLevelStatus.NOSTOCK : StockLevelStatus.INSTOCK;
		}
		
		return StockLevelStatus.INSTOCK;
		
	}
	
	@Override
	public Long getStockLevelForProductAndPointOfService(final ProductModel product, final PointOfServiceModel pointOfServiceModel)
	{
		if(!asahiSiteUtil.isCub())
		{
   		validateParameterNotNull(product, VALIDATE_PRODUCT_NOT_NULL);
   		long stock =  getInStockMaxQuantity(product);
   	   return stock <= 0?0:stock;
		}
		return super.getStockLevelForProductAndPointOfService(product, pointOfServiceModel);

	}
	
	@Override
	public Map<PointOfServiceModel, StockLevelStatus> getPosAndStockLevelStatusForProduct(final ProductModel product,
			final BaseStoreModel baseStore)
	{
		if(!asahiSiteUtil.isCub())
		{
		return null;
		}
		return super.getPosAndStockLevelStatusForProduct(product,baseStore);
	}
	
	@Override
	public boolean isStockSystemEnabled(final BaseStoreModel baseStore)
	{
		if(!asahiSiteUtil.isCub())
		{
		return null != baseStore.getStockCheckEnabled() ? baseStore.getStockCheckEnabled() : false;
		}
		return super.isStockSystemEnabled(baseStore);
	}

	

	private long getInStockMaxQuantity(final ProductModel product)
	{
		final CartModel cart = cartService.getSessionCart();
		final List<AbstractOrderEntryModel> productEntries = cart.getEntries().stream()
				.filter(entry -> entry.getProduct().equals(product)).collect(Collectors.toList());
		
		final long existingStocks = getStocksInCartForProduct(productEntries);
		long allowedStocks = 0L;
		if(asahiSiteUtil.isApb()){
			allowedStocks = null != product.getMaxOrderQuantity() ? product.getMaxOrderQuantity()
				: null != baseSiteService.getCurrentBaseSite().getMaxOrderQty()
						? baseSiteService.getCurrentBaseSite().getMaxOrderQty() : DEFAULT_FORCE_IN_STOCK_MAX_QUANTITY;
		} else {
			Integer maxConfigured = product.getMaxOrderQuantity();
			allowedStocks = null != maxConfigured && maxConfigured >0? (long)maxConfigured
					: (long)asahiSiteUtil.getSgaGlobalMaxOrderQty();
		}
		if(asahiSiteUtil.isSga()) {
			
			if (allowedStocks >= existingStocks)
   		{
   			LOG.info("product entry existing quantity:" +product.getCode()+ "existingStocks:" +existingStocks);
   			return 0 != existingStocks ? existingStocks : allowedStocks - existingStocks;
   		}
   		else
   		{
   			LOG.info("product entry allowed stocks:" +product.getCode()+ "allowedStocks:" +allowedStocks);
   			return allowedStocks;
   		}
			
		}
		else {
   		
			if (allowedStocks >= existingStocks)
			{
				return allowedStocks - existingStocks;
			}
		}
		return -1;
	}

	private long getStocksInCartForProduct(final List<AbstractOrderEntryModel> productEntries)
	{
		long totalStocks = 0L;
		for (final AbstractOrderEntryModel entry : productEntries)
		{
			totalStocks += entry.getQuantity();
		}
		return totalStocks;
	}
	
	public StockLevelStatus getStockLevelForSGA(final ProductModel product)
	{

		UserModel user = userService.getCurrentUser();
		if(user instanceof B2BCustomerModel)
		{
			AsahiB2BUnitModel unit = (AsahiB2BUnitModel)((B2BCustomerModel)user).getDefaultB2BUnit();
			if(unit.getWarehouse()!=null)
			{
				StockLevelModel stockLevel = stockLevelDao.findStockLevel(this.getStockLevelProductStrategy().convert(product), unit.getWarehouse());
				if(stockLevel!=null && stockLevel.getInStockStatus()!=null)
				{
					if(StockLevelStatus.LOWSTOCK.getCode().equalsIgnoreCase(stockLevel.getInStockStatus().getCode()))
					{
						return StockLevelStatus.LOWSTOCK;
					}
					else if(StockLevelStatus.OUTOFSTOCK.getCode().equalsIgnoreCase(stockLevel.getInStockStatus().getCode()))
					{
						return StockLevelStatus.OUTOFSTOCK;
					}
				}
			}
		}
		return null;
	}
	
}