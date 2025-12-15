/**
 *
 */
package com.sabmiller.core.strategy.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.util.AsahiSiteUtil;
import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * DefaultSABMCartValidationStrategy
 *
 * @author yaopeng
 *
 */
public class DefaultSABMCartValidationStrategy extends DefaultCartValidationStrategy
{
	/** The Constant LOG. */
	protected static final Logger LOG = LoggerFactory.getLogger(DefaultSABMCartValidationStrategy.class);

	/** The unit service. */
	private SabmB2BUnitService b2bUnitService;
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	@Resource
	private CommerceCartService commerceCartService;
	@Resource
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Resource(name = "cartService")
	private SABMCartService sabmCartService;

	@Override
	public List<CommerceCartModification> validateCart(final CommerceCartParameter parameter)
	{

		if(!asahiSiteUtil.isCub())
		{
			return super.validateCart(parameter);
		}
		final CartModel cartModel = parameter.getCart();
		cleanCart(cartModel);

		if (cartModel != null && cartModel.getEntries() != null && !cartModel.getEntries().isEmpty())
		{
			final List<CommerceCartModification> modifications = Lists.newArrayList();

			for (final AbstractOrderEntryModel orderEntryModel : cartModel.getEntries())
			{
				modifications.add(this.validateCartEntry(cartModel, (CartEntryModel) orderEntryModel));
			}

			return modifications;
		}
		return Collections.emptyList();
	}

	@Override
	protected CommerceCartModification validateCartEntry(final CartModel cartModel, final CartEntryModel cartEntryModel)
	{
		if(!asahiSiteUtil.isCub())
		{
			return super.validateCartEntry(cartModel, cartEntryModel);
		}
		final CommerceCartModification modification = new CommerceCartModification();
		final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel)cartEntryModel.getProduct()).getBaseProduct();
		final Map<String, Object> maxOrderQuantityMap = sabmCartService.getFinalMaxOrderQty(cartEntryModel.getProduct(),
				cartModel.getRequestedDeliveryDate());
		final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM");
		final Integer maxOrderQuantity = (Integer) maxOrderQuantityMap.get(SabmCoreConstants.FINAL_MAX_ORDER_QTY);
		boolean isMaxOrderQtyExeeded = false;
		 if(maxOrderQuantity != null && Math.subtractExact(maxOrderQuantity, cartEntryModel.getQuantity().intValue()) < 0) {
			 isMaxOrderQtyExeeded = true;
		}
		 if(isMaxOrderQtyExeeded){
			 if(maxOrderQuantityMap.containsKey(SabmCoreConstants.TOTAL_ORDERED_QTY)) {
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
				 }
				 else
				 {
					 modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
								+ sabmAlcoholVariantProductEANModel.getSellingName() + " "
								+ sabmAlcoholVariantProductEANModel.getPackConfiguration()
							 + " product currently has a " + maxOrderQuantityMap.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS)
								+ "-day maximum order quantity of "
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
				 modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED + ":"
						 + sabmAlcoholVariantProductEANModel.getSellingName() + " "
						 + sabmAlcoholVariantProductEANModel.getPackConfiguration() + " product currently has a "
						 + maxOrderQuantityMap.get(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS) + "-day maximum order quantity of "
						 + maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY)
						 + ". For your selected dispatch date only the maximum of "
						 + maxOrderQuantityMap.get(SabmCoreConstants.CONFIGURED_MAX_QTY) + " can be added to your cart.");
			 }
		 }
		else if (BooleanUtils.isNotTrue(cartEntryModel.getCalculated()))
		{
			modification.setStatusCode("notCalculated");
		}
		else
		{
			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);;
		}
		modification.setQuantityAdded(cartEntryModel.getQuantity());
		modification.setQuantity(cartEntryModel.getQuantity());
		modification.setEntry(cartEntryModel);
		return modification;
	}

	/*
	 * Rewrite validateDelivery Increase B2bUnit Address validate
	 *
	 * @see de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy#validateDelivery(de.hybris.
	 * platform.core.model.order.CartModel)
	 */
	@Override
	protected void validateDelivery(final CartModel cartModel)
	{

		if(!asahiSiteUtil.isCub())
		{
			final CheckoutPaymentType paymentType = cartModel.getPaymentType();

			if (paymentType == null || CheckoutPaymentType.CARD.equals(paymentType))
			{
				/* Need to be tracked in Pending Issue */
				//super.validateDelivery(cartModel);
			}
		}
		else
		{
		if (cartModel.getDeliveryAddress() != null)
   		{
   			if (!isGuestUserCart(cartModel) && !getUserService().getCurrentUser().equals(cartModel.getDeliveryAddress().getOwner()))
   			{
   				// if validateCustomerDelivery is false. then ValidCartDeliveryAddress from b2bUnit
   				if (cartModel.getUser() instanceof B2BCustomerModel)
   				{
   					final B2BUnitModel b2bUnitModel = b2bUnitService.getParent((B2BCustomerModel) cartModel.getUser());

   					LOG.debug("The DeliveryAddress not exists in User {}, then ValidCartDeliveryAddress from b2bUnit {}",
   							cartModel.getUser(), b2bUnitModel);

   					if (null != b2bUnitModel && CollectionUtils.isNotEmpty(b2bUnitModel.getShippingAddresses()))
   					{
   						//judge the DeliveryAddress exists in b2bUnit
   						if (!b2bUnitModel.getShippingAddresses().contains(cartModel.getDeliveryAddress()))
   						{
   							cartModel.setDeliveryAddress(null);
   							getModelService().save(cartModel);
   						}
   					}
   				}
   			}
   		}
		}
	}




	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
