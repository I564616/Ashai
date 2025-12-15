package com.apb.product.strategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.ApbProductModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.order.data.AsahiQuickOrderData;
import com.apb.integration.data.AsahiProductInfo;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderTemplateData;
import de.hybris.platform.commercefacades.order.data.OrderTemplateEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.session.SessionService;
import com.apb.facades.order.data.AsahiQuickOrderData;
import com.apb.facades.order.data.AsahiQuickOrderEntryData;

public class AsahiInclusionExclusionProductStrategy {

	@Resource
	private SessionService sessionService;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/**
	 * 
	 * @param productCode  String
	 * Method is used to check whether the product is included in the list of Products to be shown to  Customer 
	 * Product inclusion list is coming from Login interface
	 * @return true if that product is  included in the list of the products that are coming from Login interface
	 */
	public boolean isProductIncluded(final String productCode) {
		boolean isIncluded = false;
		final Map<String, AsahiProductInfo> mapProductData = (Map<String, AsahiProductInfo>) sessionService
				.getAttribute(ApbCoreConstants.CUSTOMER_SESSION_INCLUSION_LIST);
		if (null != mapProductData && mapProductData.keySet().contains(productCode)) {
			isIncluded = true;
		} else if(asahiCoreUtil.getShowProductWithoutPrice()) {
			isIncluded = true;
		}
		return isIncluded;
	}
   /**
    * 
    * @param orderData OrderData
    * Method to update the the included field in Product data and the error field in OrderData
    */
	public void updateProductData(final OrderData orderData) {
		 if(CollectionUtils.isNotEmpty(orderData.getUnconsignedEntries())){
				for (final OrderEntryData oe : orderData.getUnconsignedEntries()) {
				oe.getProduct().setIsExcluded(Boolean.FALSE);
				if (!oe.getProduct().getActive()
						|| !isProductIncluded(oe.getProduct().getCode())) {
					 oe.getProduct().setIsExcluded(Boolean.TRUE);
					 orderData.setShowExclusionError(Boolean.TRUE);
				}
			}
			orderData.setAllProductExcluded(allProductExcluded(orderData.getUnconsignedEntries()));
			
			}
	}
	/**
	 *    @param cartData CartData
	 *    Method to update the the included field in Product data and the error field in CartData
	 */
	public void updateProductCartData(final CartData cartData) {
		int unavProdCount = 0 ; 
		boolean sgaProductStatusAvailable = Boolean.valueOf(asahiConfigurationService.getString("sga.product.status.available", "false"));
		for (final OrderEntryData oe : emptyIfNull(cartData.getEntries())) {
			oe.getProduct().setIsExcluded(Boolean.FALSE);
			if (!oe.getProduct().getActive()
					|| !isProductIncluded(oe.getProduct().getCode())) {
				 unavProdCount++;
				 oe.getProduct().setIsExcluded(Boolean.TRUE);
				 cartData.setShowExclusionError(Boolean.TRUE);
			}
			
			if(sgaProductStatusAvailable && !BooleanUtils.isTrue(cartData.getOutofStockItemAvailable()) && 
					oe.getProduct().getStock()!=null && 
					StockLevelStatus.OUTOFSTOCK.equals(oe.getProduct().getStock().getStockLevelStatus()))
			{
				cartData.setOutofStockItemAvailable(Boolean.TRUE);
			}
		}
		cartData.setUnavProdCount(unavProdCount);
		cartData.setAllProductExcluded(allProductExcluded(cartData.getEntries()));
	}
    /**
     * 	
     * @param listOrderEntryData OrderEntryData
     * Method to check if all the products are excluded and not to be shown to client
     * @return true if all the products are excluded otherwise false
     */
	private boolean allProductExcluded(final List<OrderEntryData> listOrderEntryData) {
		boolean allProductExcluded = true;
		for (final OrderEntryData oe : emptyIfNull(listOrderEntryData)) {
				if (isProductIncluded(oe.getProduct().getCode()) && oe.getProduct().getActive().booleanValue()) {
					allProductExcluded = false;
					break;
				}
			}
		return allProductExcluded;
  }
	/**
     * 	
     * @param listOrderEntryData OrderEntryData
     * Method to check if all the products are excluded and not to be shown to client
     * @return true if all the products are excluded otherwise false
     */
	private boolean allProductExcludedTemplate(
			final List<OrderTemplateEntryData> listOrderTemplateEntryData) {
		boolean allProductExcluded = true;
		for (final OrderTemplateEntryData oe : emptyIfNull(listOrderTemplateEntryData)) {
			if (isProductIncluded(oe.getProduct().getCode()) && oe.getProduct().getActive().booleanValue()) {
				allProductExcluded = false;
				break;
			}
		}
		return allProductExcluded;
	}
		/**
		 * @param orderTemplateData
		 * Method to update the the included field in Product data and the error field in OrderTemplateData
		 */
		public void updateProductDataTemplate(final OrderTemplateData orderTemplateData) {
			for (final OrderTemplateEntryData oe : emptyIfNull(orderTemplateData.getTemplateEntry())) {
				oe.getProduct().setIsExcluded(Boolean.FALSE);
				if (!oe.getProduct().getActive()
						|| !isProductIncluded(oe.getProduct().getCode())) {
					 oe.getProduct().setIsExcluded(Boolean.TRUE);
					 orderTemplateData.setShowExclusionError(Boolean.TRUE);
				}
			}
			orderTemplateData.setAllProductExcluded(allProductExcludedTemplate(orderTemplateData.getTemplateEntry()));
		}
		
		public static <T> Iterable<T> emptyIfNull(final Iterable<T> iterable) {
		    return iterable == null ? Collections.<T>emptyList() : iterable;
		}
		
	    /**
	     * 	
	     * @param listOrderModel OrderModel
	     * Method to check if all the products are excluded and not to be shown to client
	     * @return true if all the products are excluded otherwise false
	     */
		public Boolean allProdExcl(final List<AbstractOrderEntryModel> listOrderEntryData) {
			Boolean allProductExcluded = Boolean.TRUE;
			for (final AbstractOrderEntryModel orderEntryModel : emptyIfNull(listOrderEntryData)) {
					if (null!=orderEntryModel.getProduct() && isProductIncluded(orderEntryModel.getProduct().getCode()) && ((ApbProductModel)orderEntryModel.getProduct()).isActive()) {
						allProductExcluded = Boolean.FALSE;
						break;
					}
				}
			return allProductExcluded;
	  }
		/***
		 * 
		 * @param quickOrderData
		 * Method to update the the excluded field in Product data and the error field in AsahiQuickOrderData 
		 */
		public void updateQuickOrderData(final AsahiQuickOrderData quickOrderData) {
			for (final AsahiQuickOrderEntryData quickOrderEntry : emptyIfNull(quickOrderData.getEntries())) {
				quickOrderEntry.setIsExcluded(Boolean.FALSE);
				if (!quickOrderEntry.isActive()
						|| !isProductIncluded(quickOrderEntry.getCode())) {
					 quickOrderEntry.setIsExcluded(Boolean.TRUE);
					 quickOrderData.setShowExclusionError(Boolean.TRUE);
				}
			}
			quickOrderData.setAllProductExcluded(allProdExclQuickOrder(quickOrderData.getEntries()));
		}
		/***
		 * 
		 * @param listQuickOrderEntryData
		 * @return true if all the product will be Excluded/Inactive
		 */
		private boolean allProdExclQuickOrder(final List<AsahiQuickOrderEntryData> listQuickOrderEntryData) {
			boolean allProductExcluded = true;
			for (final AsahiQuickOrderEntryData quickOrderEntry : emptyIfNull(listQuickOrderEntryData)) {
					if (isProductIncluded(quickOrderEntry.getCode()) && quickOrderEntry.isActive()) {
						allProductExcluded = false;
						break;
					}
				}
			return allProductExcluded;
	  }
}
