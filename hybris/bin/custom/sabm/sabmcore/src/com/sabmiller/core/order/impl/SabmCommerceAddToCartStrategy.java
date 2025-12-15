package com.sabmiller.core.order.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;


/**
 * @author siddarth.p
 *
 */

public class SabmCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{


	protected static final Logger LOG = LoggerFactory.getLogger(SabmCommerceAddToCartStrategy.class);
	@Resource
	UnitService unitService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	@Resource(name = "productService")
	private SabmProductService productService;
	@Resource(name = "cartService")
	private SABMCartService sabmCartService;
	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	/**
	 * Adds an item to the cart for pickup in a given location
	 *
	 * @param parameter
	 *           Cart parameters
	 * @return Cart modification information
	 * @throws de.hybris.platform.commerceservices.order.CommerceCartModificationException
	 *
	 */
	@Override
	public CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		if (!asahiSiteUtil.isCub())
		{
			CartEntryModel entryToUpdate;
			CommerceCartModification modification;
			if (parameter.getBonusAction() != null)
			{
				entryToUpdate = getProductEntry(parameter);
				modification = createAddToCartResp(parameter, CommerceCartModificationStatus.SUCCESS, entryToUpdate,
						parameter.getQuantity());
				getCommerceCartCalculationStrategy().calculateCart(parameter);
			}
			else
			{
				boolean newProductAdded = false;
				final CartEntryModel cm = getEntrytoUpdate(
						getCartService().getEntriesForProduct(parameter.getCart(), parameter.getProduct()));
				if (null != cm)
				{
					// product already existing in cart added.
					entryToUpdate = cm;
				}
				else
				{
					// Product not existing in cart added
					newProductAdded = true;
					entryToUpdate = getModelService().create(CartEntryModel.class);
					entryToUpdate.setProduct(parameter.getProduct());
					entryToUpdate.setDeliveryPointOfService(parameter.getPointOfService());
					entryToUpdate.setOrder(getCartService().getSessionCart());
					entryToUpdate.setUnit(parameter.getUnit() != null ? parameter.getUnit() : unitService.getUnitForCode("pieces"));
					entryToUpdate.setQuantity(0L);
				}
				entryToUpdate.setCalculated(Boolean.FALSE);
				entryToUpdate.setPriceUpdated(Boolean.FALSE);

				modification = doAddToCart(parameter);
				afterAddToCart(parameter, modification);
				// Here the entry is fully populated, so we can search for a similar one and merge.
				mergeEntry(modification, parameter);

				CartEntryModel currentCartEntry = null;
				if (newProductAdded)
				{
					final List<CartEntryModel> cartEntries = getCartService().getEntriesForProduct(parameter.getCart(),
							parameter.getProduct());
					if (CollectionUtils.isNotEmpty(cartEntries))
					{
						currentCartEntry = cartEntries.stream().filter(e -> BooleanUtils.isFalse(e.getIsBonusStock())).findFirst()
								.orElse(null);
						setUpdatedPriceInCartEntry(entryToUpdate, currentCartEntry);
					}

				}
				else
				{
					currentCartEntry = cm;
				}

				//Add free product to cart if added product qualifies for a Deal
				if (modification.getQuantityAdded() > 0)
				{

					if (asahiSiteUtil.isSga())
					{
						removeOrUpdateFreeDealProductOnQtyUpdate(modification.getEntry().getQuantity(),
								BooleanUtils.isTrue(newProductAdded) ? currentCartEntry.getEntryNumber()
										: entryToUpdate.getEntryNumber());
					}
					}
				}

				if (modification.getQuantityAdded() > 0)
				{
					getCommerceCartCalculationStrategy().calculateCart(parameter);
				}
				//}
			return modification;

		}
		else
		{
			final CommerceCartModification modification = doAddToCart(parameter);
			final long endTime = System.currentTimeMillis();
			getCommerceCartCalculationStrategy().calculateCart(parameter);
			final long endcalTime = System.currentTimeMillis();
			afterAddToCart(parameter, modification);
			final long endafteraddtocartTime = System.currentTimeMillis();
			// Here the entry is fully populated, so we can search for a similar one and merge.
			mergeEntry(modification, parameter);
			final long endmergeEntryTime = System.currentTimeMillis();
			return modification;
		}
	}


	/**
	 * Removes or add/update free deal product on parent line item qty update in Cart.
	 *
	 * @param cartModification
	 *           the cart modification
	 * @param entryNumber
	 *           the entry number
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	public void removeOrUpdateFreeDealProductOnQtyUpdate(final long updatedQuantity, final long entryNumber)
			throws CommerceCartModificationException
	{
		final AsahiB2BUnitModel currentUnit = asahiCoreUtil.getDefaultB2BUnit();
		final CartModel cartModel = getCartService().getSessionCart();
		final CartEntryModel entry = getCartService().getEntryForNumber(cartModel, Integer.parseInt(String.valueOf(entryNumber)));
		if (BooleanUtils.isFalse(entry.getIsBonusStock()))
		{
			final List<AsahiDealModel> validProductDeals = dealsService.getSGADealsForProductAndUnit(entry.getProduct().getCode(),
					currentUnit);
			final List<AsahiDealModel> applicableDeals = validProductDeals.stream()
					.filter(deal -> ((AsahiProductDealConditionModel) deal.getDealCondition()).getQuantity() <= updatedQuantity)
					.sorted((d1, d2) -> ((AsahiProductDealConditionModel) d2.getDealCondition()).getQuantity()
							- ((AsahiProductDealConditionModel) d1.getDealCondition()).getQuantity())
					.collect(Collectors.toList());

			final AsahiDealModel appliedDeal = CollectionUtils.isNotEmpty(applicableDeals) ? applicableDeals.get(0) : null;

			if (StringUtils.isNotBlank(entry.getFreeGoodEntryNumber()) && null == appliedDeal)
			{
				final CommerceCartParameter freeEntryCartParameter = new CommerceCartParameter();
				freeEntryCartParameter.setEntryNumber(Long.valueOf(entry.getFreeGoodEntryNumber()));
				freeEntryCartParameter.setQuantity(0L);
				freeEntryCartParameter.setCart(cartModel);
				commerceCartService.updateQuantityForCartEntry(freeEntryCartParameter);
				entry.setFreeGoodEntryNumber(StringUtils.EMPTY);
				entry.setAsahiDealCode(StringUtils.EMPTY);

			}
			else if (StringUtils.isNotBlank(entry.getFreeGoodEntryNumber())
					&& (Integer.valueOf(entry.getFreeGoodEntryNumber()) <= (cartModel.getEntries().size() - 1)) && null != appliedDeal
					&& null != ((AsahiFreeGoodsDealBenefitModel) appliedDeal.getDealBenefit()).getProductCode())
			{
				final CartEntryModel freeCartEntry = getCartService().getEntryForNumber(cartModel,
						Integer.parseInt(String.valueOf(entry.getFreeGoodEntryNumber())));
				try
				{
					final ProductModel product = getProductService()
							.getProductForCode(((AsahiFreeGoodsDealBenefitModel) appliedDeal.getDealBenefit()).getProductCode());
					freeCartEntry.setProduct(product);

					final Integer qty = (int) updatedQuantity;
					if (qty.equals(((AsahiProductDealConditionModel) appliedDeal.getDealCondition()).getQuantity()))
					{
						freeCartEntry
								.setQuantity(Long.valueOf(((AsahiFreeGoodsDealBenefitModel) appliedDeal.getDealBenefit()).getQuantity()));
					}
					else
					{
						freeCartEntry
								.setQuantity(Long.valueOf(((AsahiFreeGoodsDealBenefitModel) appliedDeal.getDealBenefit()).getQuantity()
										* (qty / ((AsahiProductDealConditionModel) appliedDeal.getDealCondition()).getQuantity())));
					}
					freeCartEntry.setCalculated(true);
					getModelService().save(freeCartEntry);
					entry.setAsahiDealCode(appliedDeal.getCode());
					getModelService().save(entry);

				}
				catch (final Exception ex)
				{
					LOG.error("FreeDealBenefit Product not found for deal [{}]", appliedDeal.getCode());
					final CommerceCartParameter freeEntryCartParameter = new CommerceCartParameter();
					freeEntryCartParameter.setEntryNumber(Long.valueOf(entry.getFreeGoodEntryNumber()));
					freeEntryCartParameter.setQuantity(0L);
					freeEntryCartParameter.setCart(cartModel);
					commerceCartService.updateQuantityForCartEntry(freeEntryCartParameter);
					entry.setFreeGoodEntryNumber(StringUtils.EMPTY);
				}

			}
			else if (StringUtils.isBlank(entry.getFreeGoodEntryNumber()) && null != appliedDeal
					&& null != ((AsahiFreeGoodsDealBenefitModel) appliedDeal.getDealBenefit()).getProductCode())
			{
				try
				{
					final ProductModel product = getProductService()
							.getProductForCode(((AsahiFreeGoodsDealBenefitModel) appliedDeal.getDealBenefit()).getProductCode());
					final Long qtyToAdd = Long.valueOf(((AsahiFreeGoodsDealBenefitModel) appliedDeal.getDealBenefit()).getQuantity()
							* ((int) updatedQuantity / ((AsahiProductDealConditionModel) appliedDeal.getDealCondition()).getQuantity()));
					final CartEntryModel freeDealEntry = getCartService().addNewEntry(cartModel, product, qtyToAdd,
							unitService.getUnitForCode("pieces"), APPEND_AS_LAST, false);
					freeDealEntry.setIsFreeGood(true);
					freeDealEntry.setCalculated(true);
					freeDealEntry.setBasePrice(Double.valueOf(0));
					freeDealEntry.setTotalPrice(Double.valueOf(0));
					freeDealEntry.setAsahiDealCode(appliedDeal.getCode());
					getModelService().save(freeDealEntry);

					//Update freeDealProduct's entry number reference in parent entry
					entry.setFreeGoodEntryNumber(String.valueOf(freeDealEntry.getEntryNumber()));
					entry.setAsahiDealCode(appliedDeal.getCode());
					getModelService().save(entry);
				}
				catch (final Exception ex)
				{
					LOG.error("FreeDealBenefit Product not found for deal [{}]", appliedDeal.getCode());
				}
			}
		}
	}



	/**
	 * @param parameter
	 *//*
		 * private void removeAppliedDealIfAnyForProduct(final CommerceCartParameter parameter) { if (null !=
		 * parameter.getCart() && CollectionUtils.isNotEmpty(parameter.getCart().getEntries())) { final
		 * AbstractOrderEntryModel entry = parameter.getCart().getEntries().stream() .filter(e ->
		 * BooleanUtils.isTrue(e.getIsFreeGood()) && e.getProduct().equals(parameter.getFreeProduct()))
		 * .findFirst().orElse(null); if (null != entry) { try {
		 * commerceCartService.updateQuantityForCartEntry(parameter); getModelService().refresh(parameter.getCart()); }
		 * catch (final CommerceCartModificationException e1) { LOG.error(
		 * "Could not remove already applied free deal in cart for product :" + parameter.getFreeProduct().getCode()); } }
		 * }
		 *
		 * }
		 */

	/**
	 * Do add to cart.
	 *
	 * @param parameter
	 *           the parameter
	 * @return the commerce cart modification
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	@Override
	protected CommerceCartModification doAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		if (!asahiSiteUtil.isCub())
		{
			return super.doAddToCart(parameter);
		}
		CommerceCartModification modification;

		//Calling sabmaddtocartwebhook before quantityToAdd assignment to support unit conversion
		this.beforeAddToCart(parameter);
		final long endTime = System.currentTimeMillis();
		final CartModel cartModel = parameter.getCart();
		final ProductModel productModel = parameter.getProduct();
		final long quantityToAdd = parameter.getQuantity();
		final PointOfServiceModel deliveryPointOfService = parameter.getPointOfService();


		validateAddToCart(parameter);
		final long endvalidateTime = System.currentTimeMillis();

		if (isProductForCode(parameter).booleanValue())
		{
			// So now work out what the maximum allowed to be added is (note that this may be negative!)
			final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
					deliveryPointOfService);
			final Map<String, Object> maxOrderQuantity = sabmCartService.getFinalMaxOrderQty(productModel,
					cartModel.getRequestedDeliveryDate());
			final long cartLevel = checkCartLevel(productModel, cartModel, deliveryPointOfService);
			final long cartLevelAfterQuantityChange = actualAllowedQuantityChange + cartLevel;
			final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) productModel)
					.getBaseProduct();
			final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM");
			if (actualAllowedQuantityChange > 0)
			{
				// We are allowed to add items to the cart
				final CartEntryModel entryModel = addCartEntry(parameter, actualAllowedQuantityChange);
				getModelService().save(entryModel);
				String statusCode = getStatusCodeAllowedQuantityChange(actualAllowedQuantityChange,
						(Integer) maxOrderQuantity.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY), quantityToAdd,
						cartLevelAfterQuantityChange);
				if (CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED.equalsIgnoreCase(statusCode)
						&& maxOrderQuantity.containsKey(SabmCoreConstants.TOTAL_ORDERED_QTY))
				{
					if ((Integer) maxOrderQuantity.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) > 0)
					{
						statusCode = CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
								+ sabmAlcoholVariantProductEANModel.getSellingName() + " "
								+ sabmAlcoholVariantProductEANModel.getPackConfiguration()
								+ " product currently has a " + maxOrderQuantity.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS)
								+ "-day maximum order quantity of "
								+ maxOrderQuantity.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
								+ ". For your selected dispatch date you have already ordered "
								+ maxOrderQuantity.get(SabmCoreConstants.TOTAL_ORDERED_QTY) + ", therefore only "
								+ maxOrderQuantity.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) + " more have been added to your cart.";
					}
					else
					{
						statusCode = CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
								+ sabmAlcoholVariantProductEANModel.getSellingName() + " "
								+ sabmAlcoholVariantProductEANModel.getPackConfiguration()
								+ " product currently has a " + maxOrderQuantity.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS)
								+ "-day maximum order quantity of "
								+ maxOrderQuantity.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
								+ ". For your selected dispatch date you have already ordered "
								+ maxOrderQuantity.get(SabmCoreConstants.TOTAL_ORDERED_QTY)
								+ ". Your next available order date will be from "
								+ dateFormat.format(maxOrderQuantity.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE))
								+ ", please select a different date from your dispatch calendar.";
					}
				}
				modification = createAddToCartResp(parameter, statusCode, entryModel, actualAllowedQuantityChange);
			}
			else
			{
				// Not allowed to add any quantity, or maybe even asked to reduce the quantity
				// Do nothing!

				String status = getStatusCodeForNotAllowedQuantityChange(
						(Integer) maxOrderQuantity.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY),
						(Integer) maxOrderQuantity.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY));
				if (CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED.equalsIgnoreCase(status)
						&& maxOrderQuantity.containsKey(SabmCoreConstants.TOTAL_ORDERED_QTY))
				{
					if ((Integer) maxOrderQuantity.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) > 0)
					{
						status = CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
								+ sabmAlcoholVariantProductEANModel.getSellingName() + " "
								+ sabmAlcoholVariantProductEANModel.getPackConfiguration()
								+ " product currently has a " + maxOrderQuantity.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS)
								+ "-day maximum order quantity of "
								+ maxOrderQuantity.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
								+ ". For your selected dispatch date you have already ordered "
								+ maxOrderQuantity.get(SabmCoreConstants.TOTAL_ORDERED_QTY) + ", therefore only "
								+ maxOrderQuantity.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY) + " more have been added to your cart.";
					}
					else
					{
						status = CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
								+ sabmAlcoholVariantProductEANModel.getSellingName() + " "
								+ sabmAlcoholVariantProductEANModel.getPackConfiguration()
								+ " product currently has a " + maxOrderQuantity.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS)
								+ "-day maximum order quantity of "
								+ maxOrderQuantity.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
								+ ". For your selected dispatch date you have already ordered "
								+ maxOrderQuantity.get(SabmCoreConstants.TOTAL_ORDERED_QTY)
								+ ". Your next available order date will be from "
								+ dateFormat.format(maxOrderQuantity.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE))
								+ ", please select a different date from your dispatch calendar.";
					}
				}
				modification = createAddToCartResp(parameter, status, createEmptyCartEntry(parameter), 0);

			}

		}
		else
		{
			modification = createAddToCartResp(parameter, CommerceCartModificationStatus.UNAVAILABLE,
					createEmptyCartEntry(parameter), 0);
		}

		return modification;
	}

	private void setUpdatedPriceInCartEntry(final AbstractOrderEntryModel entry, final CartEntryModel cartEntryModel)
	{
		cartEntryModel.setBasePrice(entry.getBasePrice());
		cartEntryModel.setPriceUpdated(entry.getPriceUpdated());
		cartEntryModel.setTaxValues(entry.getTaxValues());
		getModelService().save(cartEntryModel);
	}

	/**
	 * This method is used to add the new line item in cart for bonus Stock
	 *
	 * @param parameter
	 *           , CommerceCartParameter containing the detail of productCode ,qty
	 * @return cartEntry Model
	 */
	private CartEntryModel getProductEntry(final CommerceCartParameter parameter)
	{
		final List<CartEntryModel> orderEntryList = getCartService().getEntriesForProduct(parameter.getCart(),
				parameter.getProduct());
		CartEntryModel entryToUpdate = null;
		boolean cond = false;
		// When the Set as Bonus is clicked on Cart page Same line item updated
		if (parameter.getBonusAction() != null && parameter.getBonusAction().equals("bonus"))
		{
			entryToUpdate = orderEntryList.get(0);
			entryToUpdate.setBasePrice(Double.valueOf(0));
			entryToUpdate.setTotalPrice(Double.valueOf(0));
			entryToUpdate.setIsBonusStock(Boolean.TRUE);
			getModelService().saveAll();
		}
		else
		{
			for (final AbstractOrderEntryModel oe : orderEntryList)
			{
				if (oe.getIsBonusStock() != null && oe.getIsBonusStock().booleanValue())
				{
					final long qty = oe.getQuantity().longValue() + parameter.getQuantity();
					oe.setQuantity(Long.valueOf(qty));
					entryToUpdate = (CartEntryModel) oe;
					entryToUpdate.setBasePrice(Double.valueOf(0));
					entryToUpdate.setTotalPrice(Double.valueOf(0));
					getModelService().saveAll();
					cond = true;
					break;
				}
			}
			if (!cond)
			{

				entryToUpdate = getModelService().create(CartEntryModel.class);
				entryToUpdate.setProduct(parameter.getProduct());
				entryToUpdate.setDeliveryPointOfService(parameter.getPointOfService());
				entryToUpdate.setOrder(getCartService().getSessionCart());
				entryToUpdate.setUnit(parameter.getUnit() != null ? parameter.getUnit() : unitService.getUnitForCode("pieces"));
				entryToUpdate.setQuantity(Long.valueOf(parameter.getQuantity()));
				entryToUpdate.setIsBonusStock(Boolean.TRUE);
				entryToUpdate.setBasePrice(Double.valueOf(0));
				entryToUpdate.setTotalPrice(Double.valueOf(0));
				getModelService().save(entryToUpdate);


			}
		}
		return entryToUpdate;
	}

	public CartEntryModel getEntrytoUpdate(final List<CartEntryModel> cartEntryList)
	{
		CartEntryModel ce = null;
		for (final CartEntryModel cartEntryModel : cartEntryList)
		{

			if (BooleanUtils.isFalse(cartEntryModel.getIsBonusStock()) && BooleanUtils.isFalse(cartEntryModel.getIsFreeGood()))
			{
				ce = cartEntryModel;
			}
		}
		return ce;
	}

	@Override
	protected long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel,
			final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
	{
		if (!asahiSiteUtil.isCub())
		{
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
			final long newTotalQuantityAfterProductMaxOrder = Math.min(newTotalQuantityAfterStockLimit,
					maxOrderQuantity.longValue());
			return newTotalQuantityAfterProductMaxOrder - cartLevel;
		}
		return newTotalQuantityAfterStockLimit - cartLevel;
	}


}
