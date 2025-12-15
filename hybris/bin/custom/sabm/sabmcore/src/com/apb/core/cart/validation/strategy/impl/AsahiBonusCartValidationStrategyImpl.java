package com.apb.core.cart.validation.strategy.impl;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.cart.validation.strategy.AsahiBonusCartValidationStrategy;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;

public class AsahiBonusCartValidationStrategyImpl implements AsahiBonusCartValidationStrategy{

	@Resource
	private CartService cartService;

	@Resource
	private ProductService productService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	@Autowired
	private CMSSiteService cmsSiteService;

	/* (non-Javadoc)
	 * This method gets the bonus products that can be added to the cart.
	 */
	@Override
	public long getAllowedBonusQuantity(final String productCode) {

		final long allowedGlobalBonusProductsInCart = this.getAllowedBonusQuantityForCart();
		final long allowedBonusProductsInCart = this.getAllowedBonusQuantityForProduct(productCode);

		if(allowedGlobalBonusProductsInCart <= allowedBonusProductsInCart){
			return allowedGlobalBonusProductsInCart;
		}
		return allowedBonusProductsInCart;
	}

	/* (non-Javadoc)
	 * This method gets the bonus products that can be added to the cart based on the global max bonus products configuration.
	 */
	private long getAllowedBonusQuantityForCart() {
		final CartModel cartModel = cartService.getSessionCart();
		final String siteId = cmsSiteService.getCurrentSite().getUid();
		return Long.parseLong(asahiConfigurationService.getString(ApbCoreConstants.BONUS_STOCK_GLOBAL_MAX_QUANTITY + siteId, "10"))
				- getTotalBonusQtyInCart(cartModel).longValue();
	}

	/* (non-Javadoc)
	 * This method gets the additional specific bonus products that can be added to the cart.
	 */
	private long getAllowedBonusQuantityForProduct(final String productCode) {
		final CartModel cartModel = cartService.getSessionCart();
		final String siteId = cmsSiteService.getCurrentSite().getUid();
		final Long maxBonusQty = Long
				.parseLong(asahiConfigurationService.getString(ApbCoreConstants.BONUS_STOCK__MAX_QTY_QUANTITY + siteId, "5"));
		return maxBonusQty - getProductBonusQtyInCart(cartModel, productCode);
	}

	/* (non-Javadoc)
	 * This method gets the total bonus products quantity in cart.
	 */
	private Long getTotalBonusQtyInCart(final CartModel cartModel) {
		Long bonusQtyInCart = 0L;
			if (null != cartModel && CollectionUtils.isNotEmpty(cartModel.getEntries()))
			{
				for (final AbstractOrderEntryModel orderEntryModel : cartModel.getEntries())
				{
					final Boolean isBonusStock = orderEntryModel.getIsBonusStock();
					if(null != isBonusStock && isBonusStock.booleanValue()){
						bonusQtyInCart = bonusQtyInCart + orderEntryModel.getQuantity();
					}

				}
			}
		return bonusQtyInCart;
	}

	/* (non-Javadoc)
	 * This method gets the product bonus quantity in cart.
	 */
	private Long getProductBonusQtyInCart(final CartModel cartModel, final String productCode) {
		Long bonusQtyInCart = 0L;
			if (null != cartModel && CollectionUtils.isNotEmpty(cartModel.getEntries()))
			{
				for (final AbstractOrderEntryModel orderEntryModel : cartModel.getEntries())
				{
					final Boolean isBonusStock = orderEntryModel.getIsBonusStock();
					if(null != isBonusStock && isBonusStock.booleanValue() && productCode.equals(orderEntryModel.getProduct().getCode())){
						bonusQtyInCart = bonusQtyInCart + orderEntryModel.getQuantity();
					}

				}
			}
		return bonusQtyInCart;
	}

}