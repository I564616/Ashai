/**
 *
 */
package com.sabmiller.core.strategy.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.strategy.SABMUpdateOrderTemplateEntryStrategy;


/**
 * @author xiaowu.a.zhang
 * @date 01/06/2016
 */
public class DefaultSABMUpdateOrderTemplateEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy
		implements SABMUpdateOrderTemplateEntryStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMUpdateOrderTemplateEntryStrategy.class);
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	@Resource(name = "cartService")
	private SABMCartService sabmCartService;
	/**
	 * Override the OOTB logic of modify entry. If the cartModel is type of SABMOrderTemplateModel, will not remove the
	 * entry which quantity is 0 SABMC-904
	 *
	 */
	@Override
	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, Integer maxOrderQuantity)
	{
		Map<String, Object> maxOrderQuantityMap = Collections.emptyMap();
		SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = null;
		final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM");
		if(asahiSiteUtil.isCub()) {
			sabmAlcoholVariantProductEANModel = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel)entryToUpdate.getProduct()).getBaseProduct();
			maxOrderQuantityMap = sabmCartService.getFinalMaxOrderQty(entryToUpdate.getProduct(), cartModel.getRequestedDeliveryDate());
			maxOrderQuantity = (Integer) maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY);
		}
		// Now work out how many that leaves us with on this entry
		long entryNewQuantity = entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange;

		if (entryNewQuantity < 0)
		{
			entryNewQuantity = 0;
		}

		// Adjust the entry quantity to the new value
		entryToUpdate.setQuantity(Long.valueOf(entryNewQuantity));
		getModelService().save(entryToUpdate);
		getModelService().refresh(cartModel);
		getModelService().refresh(entryToUpdate);

		// Return the modification data
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setQuantityAdded(actualAllowedQuantityChange);
		modification.setEntry(entryToUpdate);
		modification.setQuantity(entryNewQuantity);

		if (isMaxOrderQuantitySet(maxOrderQuantity) && entryNewQuantity == maxOrderQuantity.longValue())
		{
			if(asahiSiteUtil.isCub() && maxOrderQuantityMap.containsKey(SabmCoreConstants.TOTAL_ORDERED_QTY)) {
				if ((Integer) maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) > 0)
				{
					modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
							+ sabmAlcoholVariantProductEANModel.getSellingName() + " "
							+ sabmAlcoholVariantProductEANModel.getPackConfiguration() + " product currently has a "
							+ maxOrderQuantityMap.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS) + "-day maximum order quantity of "
							+ maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
							+ ". For your selected dispatch date you have already ordered "
							+ maxOrderQuantityMap.get(SabmCoreConstants.TOTAL_ORDERED_QTY) + ", therefore only "
							+ maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) + " more have been added to your cart.");
				} else {
					modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
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
			} else {
				modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
			}
		}
		else if (newQuantity == entryNewQuantity)
		{
			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		}
		else
		{
			modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		}

		return modification;
	}

	/**
	 * Strategy for remove the order template entry SABMC-904
	 *
	 * @param cartModel
	 *           the cart Model
	 * @param entryToUpdate
	 *           the entry To Update
	 * @return CommerceCartModification
	 */
	@Override
	public CommerceCartModification removeEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate)
	{
		final long quantityAdded = -entryToUpdate.getQuantity().longValue();
		final CartEntryModel entry = new CartEntryModel();
		entry.setProduct(entryToUpdate.getProduct());

		if (LOG.isDebugEnabled())
		{
			LOG.info("Remove Order Template entry:{} for Order Template{}", entryToUpdate, cartModel);
		}

		// The allowed new entry quantity is zero or negative
		// just remove the entry
		getModelService().remove(entryToUpdate);
		getModelService().refresh(cartModel);

		// Return an empty modification
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setEntry(entry);
		modification.setQuantity(0);
		// We removed all the quantity from this row
		modification.setQuantityAdded(quantityAdded);

		modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);

		return modification;
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
