package com.apb.integration.price.dao.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.model.OfflineProductPriceModel;
import com.apb.integration.price.dao.ApbProductOfflinePriceDao;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class ApbProductOfflinePriceDaoImpl extends AbstractItemDao implements ApbProductOfflinePriceDao{
	
	private static final String PRICE_ACC_NUMBER = "accNumber";
	private static final String PRICE_PRODUCTS ="productCodes";
	private static final String DELIVERY_PRODUCT ="productCode";
	
	/** The Constant GET_OFFLINE_PRODUCT_PRICES. */
	public static final String GET_OFFLINE_PRODUCT_PRICES = "SELECT {PK} FROM {OfflineProductPrice} WHERE {accNumber}=?accNumber AND {productCode} IN (?productCodes)";

	public static final String GET_DEFAULT_OFFLINE_PRODUCT_PRICE = "SELECT {PK} FROM {OfflineProductPrice} WHERE {productCode} = '*'";
	
	public static final String GET_DELIVERY_OFFLINE_PRODUCT_PRICE = "SELECT {PK} FROM {OfflineProductPrice} WHERE {productCode} = ?productCode";

	@Override
	public List<OfflineProductPriceModel> getProductPrices(List<String> productCodes, String accNum) 
	{
		if(CollectionUtils.isNotEmpty(productCodes))
		{
			final Map<String, Object> params = new HashMap<>();
			final StringBuilder builder = new StringBuilder(GET_OFFLINE_PRODUCT_PRICES);
			params.put(PRICE_ACC_NUMBER, accNum);
			params.put(PRICE_PRODUCTS, productCodes);

			final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
			query.addQueryParameters(params);

			final SearchResult<OfflineProductPriceModel> result = flexibleSearchService.search(query);
			List<OfflineProductPriceModel> prices = result.getResult();
			if(CollectionUtils.isNotEmpty(prices))
			{
				return prices;
			}
		}
		
		return Collections.emptyList();

	}
	
	@Override
	public OfflineProductPriceModel getDefaultPrice() 
	{
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_DEFAULT_OFFLINE_PRODUCT_PRICE);
		
		final SearchResult<OfflineProductPriceModel> result = flexibleSearchService.search(query);
		List<OfflineProductPriceModel> price = result.getResult();
		if(CollectionUtils.isNotEmpty(price))
		{
			return price.get(0);
		}
		
		return createEmptyOfflinePriceRow();
	}
	
	private OfflineProductPriceModel createEmptyOfflinePriceRow() 
	{
		OfflineProductPriceModel priceRow = new OfflineProductPriceModel();
		priceRow.setNetPrice(0D);
		priceRow.setListPrice(0D);
		priceRow.setGST(0D);
		priceRow.setDiscount(0D);
		priceRow.setWET(0D);
		priceRow.setFreight(0D);
		priceRow.setFreightGST(0D);
		return priceRow;
	}
	
	@Override
	public OfflineProductPriceModel getDeliveryPrice(String deliverySurchargeCode) 
	{
		final Map<String, Object> params = new HashMap<>();
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_DELIVERY_OFFLINE_PRODUCT_PRICE);
		params.put(DELIVERY_PRODUCT, deliverySurchargeCode);
		query.addQueryParameters(params);
		final SearchResult<OfflineProductPriceModel> result = flexibleSearchService.search(query);
		List<OfflineProductPriceModel> price = result.getResult();
		if(CollectionUtils.isNotEmpty(price))
		{
			return price.get(0);
		}
		
		return createEmptyOfflinePriceRow();
	}
}

