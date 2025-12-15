/**
 *
 */
package com.sabmiller.core.strategy.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.beanutils2.BeanComparator;
import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;

/**
 * @author xiaowu.a.zhang
 * @date 05/19/2016
 */
public class DefaultSABMCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy
{
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	@Resource(name = "cartService")
	private SABMCartService sabmCartService;

	/**
	 * Disable the OOTB function of reorder the entry number, this method will do nothing. The reorder function will be
	 * done in the SalesOrderSimulateCartSyncHelper.reRankEntries(). SAB-2779
	 *
	 * @param cartModel
	 *           the cart
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void normalizeEntryNumbers(final CartModel cartModel)
	{
		if (asahiSiteUtil.isCub() || asahiSiteUtil.isSga())
		{

			final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>(cartModel.getEntries());
			Collections.sort(entries, new BeanComparator(AbstractOrderEntryModel.ENTRYNUMBER, new ComparableComparator()));
			for (int i = 0; i < entries.size(); i++)
			{
				entries.get(i).setEntryNumber(Integer.valueOf(i));
				// if the entry have the free good entry after the reorder. set the free good entry number to new entry number.
				if (StringUtils.isNotBlank(entries.get(i).getFreeGoodEntryNumber()) && i < entries.size() - 1)
				{
					entries.get(i).setFreeGoodEntryNumber(String.valueOf(i + 1));
				}
				getModelService().save(entries.get(i));
			}
		}
		else{
			super.normalizeEntryNumbers(cartModel);
		}
	}

	/**
	 * Add the logic of delete the free product first. And then delete the basic entry. SAB-2779
	 *
	 * @param cartModel
	 *           the cartModel
	 * @param entryToUpdate
	 *           the entryToUpdate
	 * @param actualAllowedQuantityChange
	 *           the actualAllowedQuantityChange
	 * @param newQuantity
	 *           the newQuantity
	 * @param maxOrderQuantity
	 *           the maxOrderQuantity
	 *
	 * @return the deal change message.CommerceCartModification
	 */
	@Override
	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, Integer maxOrderQuantity)
	{
		Map<String, Object> maxOrderQuantityMap = Collections.emptyMap();
		SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = null;
		final long entryNewQuantity = entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange;
		long absoluteMaxOrderQty = 0;
		final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM");
		if(asahiSiteUtil.isCub()) {
			sabmAlcoholVariantProductEANModel = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel)entryToUpdate.getProduct()).getBaseProduct();
			maxOrderQuantityMap = sabmCartService.getFinalMaxOrderQty(entryToUpdate.getProduct(), cartModel.getRequestedDeliveryDate());
			maxOrderQuantity = (Integer) maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY);
			absoluteMaxOrderQty = maxOrderQuantityMap.containsKey(SabmCoreConstants.TOTAL_ORDERED_QTY)
					? ((Integer) maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY)).longValue()
					: (maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY) != null
							? ((Integer) maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY)).longValue()
							: absoluteMaxOrderQty);
			// Now work out how many that leaves us with on this entry
			// If this entry have free product, will remove the free product first
			if (entryNewQuantity <= 0 && StringUtils.isNotBlank(entryToUpdate.getFreeGoodEntryNumber()))
			{
				final AbstractOrderEntryModel freeEntry = getEntryForNumber(cartModel,
						Integer.valueOf(entryToUpdate.getFreeGoodEntryNumber()));

				if (freeEntry != null)
				{
					// The allowed new entry quantity is zero or negative
					// just remove the entry
					getModelService().remove(freeEntry);
				}
			}
		}
		final CommerceCartModification cartModification = super.modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
		if (asahiSiteUtil.isCub() && isMaxOrderQuantitySet(maxOrderQuantity)
				&& newQuantity > absoluteMaxOrderQty)
		 {
			 cartModification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
			 if(maxOrderQuantityMap.containsKey(SabmCoreConstants.TOTAL_ORDERED_QTY)) {
				 if ((Integer) maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) > 0)
				 {
					 cartModification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
							 + sabmAlcoholVariantProductEANModel.getSellingName() + " "
							 + sabmAlcoholVariantProductEANModel.getPackConfiguration() + " product currently has a "
							 + maxOrderQuantityMap.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS) + "-day maximum order quantity of "
							 + maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
							 + ". For your selected dispatch date you have already ordered "
							 + maxOrderQuantityMap.get(SabmCoreConstants.TOTAL_ORDERED_QTY) + ", therefore only "
							 + maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) + " more have been added to your cart.");
				 } else
				 {
					 cartModification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
							 + sabmAlcoholVariantProductEANModel.getSellingName() + " "
							 + sabmAlcoholVariantProductEANModel.getPackConfiguration() + " product currently has a "
							 + maxOrderQuantityMap.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS) + "-day maximum order quantity of "
							 + maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
							 + ". For your selected dispatch date you have already ordered "
							 + maxOrderQuantityMap.get(SabmCoreConstants.TOTAL_ORDERED_QTY)
							 + ". Your next available order date will be from "
							 + dateFormat.format(maxOrderQuantityMap.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE))
							 + ", please select a different date from your dispatch calendar.");
				 }
			 }
			 else
			 {
				 cartModification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
						 + sabmAlcoholVariantProductEANModel.getSellingName() + " "
						 + sabmAlcoholVariantProductEANModel.getPackConfiguration() + " product currently has a "
						 + maxOrderQuantityMap.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS) + "-day maximum order quantity of "
						 + maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
						 + ". For your selected dispatch date only the maximum of "
						 + maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY) + " can be added to your cart.");
			 }
		 }

		return cartModification;
	}

	@Override
 	protected long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel,
			final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
	{
 		if(!asahiSiteUtil.isCub()) {
 			return super.getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd, pointOfServiceModel);
 		}
		final long cartLevel = checkCartLevel(productModel, cartModel, pointOfServiceModel);
		final long stockLevel = getAvailableStockLevel(productModel, pointOfServiceModel);

		// How many will we have in our cart if we add quantity
		final long newTotalQuantity = cartLevel + quantityToAdd;

		// Now limit that to the total available in stock
		final long newTotalQuantityAfterStockLimit = Math.min(newTotalQuantity, stockLevel);

		// So now work out what the maximum allowed to be added is (note that
		// this may be negative!)

		final Map<String, Object> maxOrderQuantityMap = sabmCartService.getFinalMaxOrderQty(productModel,
				cartModel.getRequestedDeliveryDate());
		final Integer maxOrderQuantity = (Integer) maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY);

		if (isMaxOrderQuantitySet(maxOrderQuantity))
		{
			final long newTotalQuantityAfterProductMaxOrder = Math
					.min(newTotalQuantityAfterStockLimit, maxOrderQuantity.longValue());
			return newTotalQuantityAfterProductMaxOrder - cartLevel;
		}
		return newTotalQuantityAfterStockLimit - cartLevel;
	}

}
