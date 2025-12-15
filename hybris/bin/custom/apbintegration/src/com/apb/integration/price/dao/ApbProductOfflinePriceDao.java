package com.apb.integration.price.dao;

import java.util.List;

import com.apb.core.model.OfflineProductPriceModel;

public interface ApbProductOfflinePriceDao {

	List<OfflineProductPriceModel> getProductPrices(List<String> productCodes, String accNum);

	OfflineProductPriceModel getDefaultPrice();
	
	OfflineProductPriceModel getDeliveryPrice(String productCode);

}
