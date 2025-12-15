package com.apb.facades.checkout.impl;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.AbstractOrderEntryTypeService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.card.payment.AsahiCreditCardPaymentService;
import com.apb.core.checkout.service.ApbCheckoutService;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.exception.AsahiPaymentException;
import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.apb.core.model.ApbProductModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.checkout.APBCheckoutFacade;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.delivery.data.DeliveryInfoData;
import com.apb.facades.stock.check.ApbStockOnHandFacade;
import com.apb.integration.data.ApbStockonHandProductData;
import com.apb.integration.data.AsahiCheckoutInclusionResponseDTO;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.integration.data.Error;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.data.ErrorDTO;
import com.apb.storefront.data.LoginValidateInclusionData;
import com.sabmiller.core.enums.OrderType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.order.impl.SabmCommerceAddToCartStrategy;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;
import com.sabmiller.facades.cart.SABMCartFacade;


@SuppressWarnings("unchecked")
public class ApbCheckoutFacadeImpl extends DefaultB2BCheckoutFacade implements APBCheckoutFacade
{

	@Autowired
	private SessionService sessionService;

	private ApbCheckoutService apbCheckoutService;

	private DeliveryService deliveryService;

	private CommerceCheckoutService commerceCheckoutService;

	private CartService cartService;

	private CartFacade cartFacade;

	@Resource
	private B2BCartService b2bCartService;

	@Resource
	private UserService userService;

	@Resource
	private EntryMergeStrategy entryMergeStrategy;

	@Resource
	private OrderService orderService;

	@Resource
	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

	@Resource
	private AbstractOrderEntryTypeService abstractOrderEntryTypeService;

	@Resource(name = "apbStockOnHandFacade")
	private ApbStockOnHandFacade apbStockOnHandFacade;

	@Resource(name = "sabmCartFacade")
	private SABMCartFacade sabmCartFacade;

	@Resource(name = "asahiCreditCardPaymentService")
	private AsahiCreditCardPaymentService asahiCreditCardPaymentService;


	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	@Resource
	private EnumerationService enumerationService;

	/** The apb B2B unit service. */
	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource(name = "sabmCommerceAddToCartStrategy")
	private SabmCommerceAddToCartStrategy sabmCommerceAddToCartStrategy;

	@Override
	public DeliveryInfoData getDeliveryInfo(final String addressRecordId)
	{
		return apbCheckoutService.getDeliveryInfo(addressRecordId);
	}

	private static final Logger LOG = Logger.getLogger(ApbCheckoutFacadeImpl.class);

	public static final String CREDIT_SURCHARGE_FOR_AMEX = "credit.surcharge.for.amex.card.";
	public static final String CREDIT_SURCHARGE_FOR_VISA = "credit.surcharge.for.visa.card.";
	public static final String CREDIT_SURCHARGE_FOR_MASTER = "credit.surcharge.for.master.card.";
	public static final String DELIVERYMODE_STANDARD_CONSTANT = "standard";
	private static final String CREDIT_BLOCKED_ERROR_CODE = "credit.block.error.codes";
    private static final String EXCLUDE_MATERIAL_ERROR_CODE = "excluded.product.error.codes";

	@Override
	public boolean setDeliveryAddressIfAvailable()
	{

		final CartModel cartModel = getCart();

		final List<AddressModel> supportedDeliveryAddresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel,
				true);

		if (CollectionUtils.isNotEmpty(supportedDeliveryAddresses))
		{
			final Optional<AddressModel> defaultAddress = supportedDeliveryAddresses.stream()
					.filter(address -> null != address.getDefaultAddress() && address.getDefaultAddress().equals(Boolean.TRUE))
					.findFirst();
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
			if (defaultAddress.isPresent())
			{
				parameter.setAddress(defaultAddress.get());
			}
			else
			{
				parameter.setAddress(supportedDeliveryAddresses.get(0));
			}
			parameter.setIsDeliveryAddress(false);
			return getCommerceCheckoutService().setDeliveryAddress(parameter);

		}
		return false;
	}

	@Override
	public void createCartFromOrder(final String orderCode, final boolean clearCart)
	{
		final OrderModel order = getB2BOrderService().getOrderForCode(orderCode);
		if (null == order)
		{
			throw new IllegalArgumentException("Cannot reorder because order does not exist.");
		}
		final boolean notOnlineOrder = null != order.getOnlineOrder() && order.getOnlineOrder().equals(Boolean.FALSE);
		if (notOnlineOrder && !order.getUnit().equals(getCurrentB2BUnit()))
		{
			throw new IllegalArgumentException(
					"Cannot reorder. Possible reasons for failure: 1. Order is not associated with current B2B unit. 2. Order is not an online order.");
		}
		final CartModel existingCart = getCartService().getSessionCart();
		getModelService().detach(order);
		order.setSchedulingCronJob(null);
		order.setOriginalVersion(null);
		order.setStatus(OrderStatus.CREATED);
		order.setPaymentAddress(null);
		order.setDeliveryAddress(null);
		order.setHistoryEntries(null);
		order.setPaymentInfo(null);
		order.setB2bcomments(Collections.<B2BCommentModel> emptyList());
		order.setWorkflow(null);
		order.setPermissionResults(Collections.<B2BPermissionResultModel> emptyList());
		order.setExhaustedApprovers(Collections.<B2BCustomerModel> emptySet());

		// reset quote related fields
		resetQuoteRelatedFields(order);

		//Reset- InvoiceGSTAmount
		order.setInvoiceAmountWithGST(null);
		order.setSalesOrderId(null);

		//setting bdeorder flag as false
		if (!asahiSiteUtil.isBDECustomer() && asahiSiteUtil.isSga())
		{
			order.setBdeOrder(Boolean.FALSE);
			order.setBdeOrderEmailText(StringUtils.EMPTY);
			order.setBdeOrderCustomerFirstName(StringUtils.EMPTY);
			order.setBdeOrderCustomerEmails(Collections.emptyList());
		}
			
		order.getEntries().stream().forEach(orderEntry -> clearEntryNumber(orderEntry));

		// create cart from the order object.
		final CartModel cart = b2bCartService.createCartFromAbstractOrder(order);
		cart.getEntries().removeIf(entry -> (null != entry.getIsBonusStock() && entry.getIsBonusStock()));
		cart.getEntries().removeIf(entry -> (BooleanUtils.isTrue(entry.getIsFreeGood())));
		cart.getEntries().removeIf(entry -> (null != entry.getProduct()
				&& !this.inclusionExclusionProductStrategy.isProductIncluded(entry.getProduct().getCode())));
		if (asahiSiteUtil.isSga())
		{
			cart.getEntries().stream().forEach(entry -> clearAsahiDealAttributes(entry));
		}
		cart.getEntries().stream().forEach(entry -> clearEntryInvoicedAttributes(entry));
		if (!clearCart)
		{
			addExistingItemsToReorderCart(cart, existingCart, order);
			updateCartForFreeDealProducts(cart);
			getModelService().saveAll(existingCart);
		}
		else
		{

			cart.setUser(userService.getCurrentUser());
			getModelService().save(cart);
			getCartService().removeSessionCart();
			getModelService().remove(existingCart);
			getModelService().refresh(cart);
			getCartService().setSessionCart(cart);

			updateCartForFreeDealProducts(cart);
		}
	}

	/**
	 * @param entry
	 * @return
	 */
	private void clearAsahiDealAttributes(final AbstractOrderEntryModel entry)
	{
		entry.setFreeGoodEntryNumber(null);
		entry.setFreeGoodsForDeal(null);
		entry.setAsahiDealCode(null);
	}

	/**
	 * @param cart
	 *
	 */
	private void updateCartForFreeDealProducts(final CartModel cart)
	{
		if (asahiSiteUtil.isSga())
		{
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				try
				{
					sabmCommerceAddToCartStrategy.removeOrUpdateFreeDealProductOnQtyUpdate(entry.getQuantity(),
							entry.getEntryNumber());
				}
				catch (final CommerceCartModificationException e)
				{
					LOG.error("Error while updating Cart for Free Deals Product from Re-Order");
				}
			}
		}

	}

	private void addExistingItemsToReorderCart(final CartModel cart, final CartModel existingCart, final OrderModel order)
	{

		final Collection<AbstractOrderEntryModel> entries = cloneAbstractOrderStrategy
				.cloneEntries(abstractOrderEntryTypeService.getAbstractOrderEntryType(cart), cart);
		if (CollectionUtils.isNotEmpty(entries))
		{
			final List<AbstractOrderEntryModel> entriesToAdd = new ArrayList<>(existingCart.getEntries());
			int entryIndex = entriesToAdd.size();
			for (final AbstractOrderEntryModel entry : entries)
			{
				final ApbProductModel product = (ApbProductModel) entry.getProduct();
				if (product.isActive() && product.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED))
				{
					final AbstractOrderEntryModel mergedEntry = entriesToAdd.stream()
							.filter(orderEntry -> orderEntry.getProduct().equals(entry.getProduct())).findFirst().orElse(null);
					if (null != mergedEntry)
					{

						//Update the allowed entry to be added upto the maximum limit of the product for SGA...
						final Long maxProductQty = asahiSiteUtil.getSgaGlobalMaxOrderQty();
						if(asahiSiteUtil.isSga() && (entry.getQuantity() > (maxProductQty - mergedEntry.getQuantity()))){
								mergedEntry.setQuantity(mergedEntry.getQuantity() + (maxProductQty - mergedEntry.getQuantity()));
						}else{
							mergedEntry.setQuantity(mergedEntry.getQuantity() + entry.getQuantity());
						}

						if (null == mergedEntry.getOrder())
						{
							mergedEntry.setOrder(order);
						}

						getModelService().save(mergedEntry);
					}
					else
					{
						entry.setEntryNumber(++entryIndex);
						entry.setOrder(existingCart);
						entriesToAdd.add(entry);
						getModelService().save(entry);
					}
				}
			}
			getModelService().saveAll(entriesToAdd);
			cart.setEntries(entriesToAdd);
		}
	}

	@Override
	protected CartModel getCart()
	{
		return hasCheckoutCart() ? getCartService().getSessionCart() : null;
	}

	@Override
	protected CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks)
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(enableHooks);
		parameter.setCart(cart);
		return parameter;
	}


	public ApbCheckoutService getApbCheckoutService()
	{
		return apbCheckoutService;
	}

	public void setApbCheckoutService(final ApbCheckoutService apbCheckoutService)
	{
		this.apbCheckoutService = apbCheckoutService;
	}

	@Override
	protected DeliveryService getDeliveryService()
	{
		return deliveryService;
	}

	@Override
	public void setDeliveryService(final DeliveryService deliveryService)
	{
		this.deliveryService = deliveryService;
	}

	@Override
	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	@Override
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	@Override
	protected <T extends CartService> T getCartService()
	{
		return (T) cartService;
	}

	@Override
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	@Override
	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Override
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	@Override
	public B2BUnitModel getB2BUnitForUid(final String b2bUnit)
	{
		return this.apbCheckoutService.getB2BUnitForUid(b2bUnit);
	}

	public boolean setPaymentTypeInfo(final String paymentType, final String poNumber)
	{
		final CartModel cartModel = getCart();
		if (cartModel == null)
		{
			return false;
		}
		if (poNumber != null)
		{
			cartModel.setPurchaseOrderNumber(poNumber);
		}
		if ("ACCOUNT".equalsIgnoreCase(paymentType))
		{
			cartModel.setIsPrepaid(false);
			cartModel.setPaymentType(CheckoutPaymentType.ACCOUNT);
		}else if ("DELIVERY".equalsIgnoreCase(paymentType))
		{
			cartModel.setIsPrepaid(false);
			cartModel.setPaymentType(CheckoutPaymentType.DELIVERY);
		}
		else
		{
			cartModel.setIsPrepaid(true);
		}
		cartModel.setOrderType(OrderType.ONLINE);
		getModelService().save(cartModel);
		return true;
	}

	private B2BUnitModel getCurrentB2BUnit()
	{
		final UserModel user = getUserService().getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !getUserService().isAnonymousUser(user))
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			return customer.getDefaultB2BUnit();
		}
		return null;
	}

	@Override
	public void updateStockEntry(final CartData cartData)
	{
		final List<String> productList = new ArrayList<>();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				productList.add(entry.getProduct().getCode());
			}
			final List<ApbStockonHandProductData> stockDataList = apbStockOnHandFacade.checkStock(cartData.getWarehouse(),
					productList);
			if (CollectionUtils.isNotEmpty(stockDataList))
			{
				for (final OrderEntryData entry : cartData.getEntries())
				{
					for (final ApbStockonHandProductData stockData : stockDataList)
					{

						if (stockData.getProductId().equalsIgnoreCase(entry.getProduct().getCode())
								&& stockData.getAvailablePhysical() < entry.getQuantity())
						{
							entry.setProductOutOfStock(true);
						}
					}
				}
			}
		}
		Collections.sort(cartData.getEntries(),
				(entry1, entry2) -> Boolean.compare(entry2.isProductOutOfStock(), entry1.isProductOutOfStock()));
	}

	@Override
	public CartData getCheckoutCart()
	{
		boolean requireCalculation = Boolean.FALSE;

		/*
		 * For SGA, cart is validated against the ECC interface call for prices, creditblock, inclusion products So, once
		 * the session is updated with latest list during proceed to checkout, cart needs to be updated.
		 */
		if (asahiSiteUtil.isSga())
		{
			requireCalculation = Boolean.TRUE;
		}
		final CartData cartData = sabmCartFacade.getSessionCartPage(requireCalculation);
		if (cartData != null)
		{
			cartData.setDeliveryAddress(getDeliveryAddress());
			cartData.setDeliveryMode(getDeliveryMode());
			cartData.setPaymentInfo(getPaymentDetails());
			if (asahiSiteUtil.isSga())
			{
				inclusionExclusionProductStrategy.updateProductCartData(cartData);
			}
			sabmCartFacade.updateProductEntries(cartData);
		}
		return cartData;
	}

	/**
	 * @param asahiPaymentDetailsData
	 * @param cartData
	 */
	public void makeCreditCardPayment(final AsahiPaymentDetailsData asahiPaymentDetailsData) throws AsahiPaymentException
	{

		if (null != asahiPaymentDetailsData)
		{
			asahiCreditCardPaymentService.makeCreditCardPaymentRequest(asahiPaymentDetailsData);
		}


	}

	@Override
	public CartData updateTotalwithCreditSurcharge(final String cardType, final String paymentMethod)
	{
		CreditCardType creditCardType = null;
		if (StringUtils.isNotEmpty(cardType))
		{
			if ("MASTERCARD".equalsIgnoreCase(cardType))
			{
				creditCardType = CreditCardType.valueOf("MASTER");
			}
			else
			{
				creditCardType = CreditCardType.valueOf(cardType.toUpperCase());
			}
		}
		return sabmCartFacade.getSessionCartWithCreditSurcharge(creditCardType, paymentMethod);
	}

	@Override
	public OrderData placeOrder(final String cardType) throws InvalidCartException
	{
		final CartModel cartModel = getCart();


		if (cartModel != null
				&& (cartModel.getUser().equals(getCurrentUserForCheckout()) || getCheckoutCustomerStrategy().isAnonymousCheckout()))
		{
			final boolean isCreditPayment = cartModel.getIsPrepaid();

			if (asahiSiteUtil.isSga())
			{
				this.apbCheckoutService.setDeviceType(cartModel);
				this.apbCheckoutService.setCustomFields(cartModel);
				// Added to Remove the Excluded and Inactive product from Cart
				final List<AbstractOrderEntryModel> entries = cartModel.getEntries().stream()
						.filter(entry -> ((ApbProductModel) entry.getProduct()).isActive()
								&& inclusionExclusionProductStrategy.isProductIncluded(entry.getProduct().getCode()))
						.collect(Collectors.toList());
				cartModel.setEntries(entries);
			}

			beforePlaceOrder(cartModel);
			if(cartModel.getIsPrepaid() && CollectionUtils.isEmpty(cartModel.getPaymentTransactions())) {
				LOG.error("cart issues with payment transactions cart id: {}"+ cartModel.getCode());
					return null;
			}

			final OrderModel orderModel = placeOrder(cartModel);
			orderModel.setPaymentType(cartModel.getPaymentType());
			afterPlaceOrder(cartModel, orderModel);

			if (orderModel != null) {
				if (isCreditPayment) {
					String surcharge = null;
					if (!sabmCartFacade.isAddSurcharge()) {
						surcharge = "0";
					} else if (cardType.equalsIgnoreCase(CreditCardType.AMEX.toString())) {
						surcharge = asahiConfigurationService
								.getString(CREDIT_SURCHARGE_FOR_AMEX + cmsSiteService.getCurrentSite().getUid(), "10");
					} else if (cardType.equalsIgnoreCase(CreditCardType.VISA.toString())) {
						surcharge = asahiConfigurationService
								.getString(CREDIT_SURCHARGE_FOR_VISA + cmsSiteService.getCurrentSite().getUid(), "10");
					} else {
						surcharge = asahiConfigurationService
								.getString(CREDIT_SURCHARGE_FOR_MASTER + cmsSiteService.getCurrentSite().getUid(), "10");
					}
					final double totalPrice = orderModel.getTotalPrice();
					final double surchargeValue = Double.parseDouble(surcharge);
					orderModel.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
					double total = 0;

					if (null != orderModel.getTotalPrice()) {
						total = totalPrice;
					}

					if (orderModel.getOrderGST() != null) {
						total = totalPrice + orderModel.getOrderGST();
					}
					final double creditSurcharge = (total * surchargeValue) / 100;
					final BigDecimal creditSurchargeVal = BigDecimal.valueOf(creditSurcharge);
					final double creditValue = creditSurchargeVal.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
					orderModel.setCreditSurCharge(creditValue);
					orderModel.setTotalPrice(totalPrice + creditValue);
				} else {
					orderModel.setStatus(OrderStatus.PAYMENT_APPROVED);
				}
				orderModel.setUnit(getCurrentB2BUnit());
				this.setOrderPlacedBy(orderModel);
				getModelService().save(orderModel);
				getModelService().refresh(orderModel);

				return getOrderConverter().convert(orderModel);
			}

		}
		return null;
	}

	private void setOrderPlacedBy(final OrderModel orderModel)
	{
		if (asahiSiteUtil.isSga() && asahiSiteUtil.isBDECustomer())
		{
			final UserModel user = getUserService().getCurrentUser();
			final UserModel bde = userService.getUserForUID(((BDECustomerModel) user).getEmail());
			orderModel.setPlacedBy(bde);
		}
	}

	@Override
	public boolean setDeliveryTypeDetails(final String deliveryMode, final String deferredDeliveryDate)
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			if (asahiSiteUtil.isSga())
			{
				return this.setDeliveryDetails(deferredDeliveryDate, cartModel);
			}
			else
			{
				final DeliveryModeModel deliveryModel = new DeliveryModeModel();
				deliveryModel.setCode(deliveryMode);
				cartModel.setDeliveryMode(flexibleSearchService.getModelByExample(deliveryModel));

				if (!"standard".equals(deliveryMode))
				{
					final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

					try
					{
						final Date deliveryDate = dateFormat.parse(deferredDeliveryDate);
						cartModel.setDeliveryRequestDate(deliveryDate);
						cartModel.setDeferredDelivery(true);
					}
					catch (final ParseException e)
					{
						LOG.error(
								"Could not parse deferred delivery date. Please set deferred delivery date in correct format i.e. dd/MM/yyyy",
								e);
						return false;
					}
				}
				else
				{
					cartModel.setDeferredDelivery(false);
				}
				getModelService().save(cartModel);
				return true;
			}
		}

		return false;
	}

	private boolean setDeliveryDetails(final String reqDeliveryDate, final CartModel cartModel)
	{
		final DeliveryModeModel deliveryModel = new DeliveryModeModel();
		deliveryModel.setCode(DELIVERYMODE_STANDARD_CONSTANT);
		cartModel.setDeliveryMode(flexibleSearchService.getModelByExample(deliveryModel));

		final SimpleDateFormat dateFormat = new SimpleDateFormat(ApbFacadesConstants.DATEFORMAT_DDMMYYYY);

		try
		{
			final Date deliveryDate = dateFormat.parse(reqDeliveryDate);
			cartModel.setDeliveryRequestDate(deliveryDate);
			cartModel.setDeferredDelivery(false);
		}
		catch (final ParseException e)
		{
			LOG.error("Could not parse selected delivery date. Please set deferred delivery date in correct format i.e. dd/MM/yyyy",
					e);
			return false;
		}

		getModelService().save(cartModel);
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.apb.facades.checkout.APBCheckoutFacade#getPaymentTypesForUser(com.sabmiller.core.model.AsahiB2BUnitModel) call
	 * to service to validate the SGA payment type.. if payment term IS C00C, then only i am allowed to show Account
	 * Payment Type
	 */
	@Override
	public List<B2BPaymentTypeData> getPaymentTypesForCustomer(final AsahiB2BUnitModel asahiB2BUnitModel)
	{

		return apbCheckoutService.getPaymentTypesForCustomer(asahiB2BUnitModel, super.getPaymentTypes());
	}

	/**
	 * Clear entry number.
	 *
	 * @param entry the entry
	 */
	private void clearEntryNumber(final AbstractOrderEntryModel entry)
	{
		entry.setEntryNumber(null);
	}

	/**
	 * Clear entry invoiced attributes.
	 *
	 * @param entry the entry
	 */
	private void clearEntryInvoicedAttributes(final AbstractOrderEntryModel entry)
	{
		entry.setInvoicedQty(null);
		entry.setStatus(null);
		entry.setBackendUid(null);
		entry.setPickinglistQty(null);
		entry.setInventoryTransId(null);
		entry.setIsBonusStock(false);
		entry.setTaxValues(null);
		entry.setNetUnitPrice(null);
		entry.setBasePrice(null);
		entry.setNetLineOrderAmount(null);
		entry.setTotalPrice(null);
		entry.setNetLineInvoiceAmount(null);
		entry.setOrderEntryWET(null);
		entry.setOrderEntryGST(null);
		entry.setLineNum(null);
		getModelService().save(entry);
	}

	@Override
	public LoginValidateInclusionData updateCartWithInclusionList(final boolean updateCart, final long formQty)
	{
	    LOG.info(String.format("Config value for config key %s is %s and for %s is %s", CREDIT_BLOCKED_ERROR_CODE, asahiConfigurationService.getString(CREDIT_BLOCKED_ERROR_CODE, StringUtils.EMPTY), EXCLUDE_MATERIAL_ERROR_CODE, asahiConfigurationService.getString(EXCLUDE_MATERIAL_ERROR_CODE, StringUtils.EMPTY)));
		asahiCoreUtil.removeSessionAttributesBeforeCheckout();


		final AsahiCheckoutInclusionResponseDTO serviceResponse = asahiIntegrationPointsService
				.getCheckoutInclusionResponse(updateCart, formQty);
		final List<ErrorDTO> errorList = new ArrayList<>();
		final LoginValidateInclusionData responseDTO = new LoginValidateInclusionData();
		responseDTO.setIsCreditBlock(false);
		final ErrorDTO error = new ErrorDTO();
        final List<String> creditBlockErrorCodes = Arrays.asList(asahiConfigurationService.getString(CREDIT_BLOCKED_ERROR_CODE, StringUtils.EMPTY).split(", "));

		if (null == serviceResponse || null == serviceResponse.getCheckoutResponse())
		{
			//1. if response is null due to any error user will see previously(login) listed products
			LOG.info("ECC did not responded or some error encountered");
			error.setError("inclusion.product.not.found");
            error.setErrorCode(ApbCoreConstants.PRODUCTS_BLOCK_CODE);
		}

		else if (serviceResponse.getCheckoutResponse().getIsBlocked() || (CollectionUtils.isNotEmpty(serviceResponse.getCheckoutResponse().getErrorText())
                && creditBlockErrorCodes.contains(serviceResponse.getCheckoutResponse().getErrorText().get(0).getMessageCode())))
		{
			// 3. if user is blocked --> Confirm and pay would be disabled
			LOG.info("User is blocked at ECC");
			if (null != serviceResponse.getCheckoutResponse().getItems()
					&& !serviceResponse.getCheckoutResponse().getItems().isEmpty())
			{
				final Map<String, AsahiProductInfo> inclusionMap = serviceResponse.getCheckoutResponse().getItems().stream()
						.filter(item -> !item.getIsExcluded() && !ApbCoreConstants.FREE_ITEM_CATEGORY.equals(item.getItemcat()))
						.collect(Collectors.toMap(AsahiProductInfo::getMaterialNumber, obj -> obj));
				asahiCoreUtil.setInclusionMapInSession(inclusionMap);
			}

			String accessType = null;
			final UserModel user = getUserService().getCurrentUser();
			if (null != user && user instanceof B2BCustomerModel)
			{
				accessType = apbB2BUnitService.getSamAccessTypeForCustomer((B2BCustomerModel)user);
			}

			asahiCoreUtil.setSessionUserCreditBlock(true);
			if (ApbCoreConstants.PAY_AND_ORDER_ACCESS.equalsIgnoreCase(accessType))
			{
				error.setError("sga.order.and.pay.user.credit.block.message");
			}
			else if (ApbCoreConstants.ORDER_ACCESS.equalsIgnoreCase(accessType))
			{
				error.setError("sga.order.only.user.credit.block.message");
			}
			else if (ApbCoreConstants.PAY_ACCESS.equalsIgnoreCase(accessType))
			{
				error.setError("sga.pay.only.user.credit.block.message");
			}
			else
			{
				error.setError("sga.user.credit.block.message");
			}
			error.setErrorCode(ApbCoreConstants.CREDIT_BLOCK_CODE);
			responseDTO.setIsCreditBlock(true);
		}

        else if (CollectionUtils.isNotEmpty(serviceResponse.getCheckoutResponse().getErrorText())
                && !isErrorCodesEmpty(serviceResponse.getCheckoutResponse().getErrorText()))
        {
            // 2. if any error is received from ECC it will block "Confirm & Pay" button with error message
            LOG.info("ECC responded with some error --- customer : " + serviceResponse.getCheckoutResponse().getCustomerNumber());
            final List<String> excludeMeterialErrorCodes;
            excludeMeterialErrorCodes=Arrays.asList(asahiConfigurationService.getString(EXCLUDE_MATERIAL_ERROR_CODE, StringUtils.EMPTY).split(", "));
            if(excludeMeterialErrorCodes.contains(serviceResponse.getCheckoutResponse().getErrorText().get(0).getMessageCode())){
                final String[] strArr = serviceResponse.getCheckoutResponse().getErrorText().get(0).getMessageText().split("\\s");
                final String errorMsg = asahiConfigurationService.getString("exclude.product.error.msg", StringUtils.EMPTY);
                final Set<String> exclusionSet = new HashSet<>();
                exclusionSet.add(strArr[1]);
                updateSessionInclusionMap(serviceResponse, exclusionSet, false);
                error.setError(String.format(errorMsg, strArr[1]));
                error.setErrorCode("exclude_product_error");
            }
        }

		else if (!serviceResponse.getCheckoutResponse().getIsBlocked() && (null == serviceResponse.getCheckoutResponse().getItems()
				|| serviceResponse.getCheckoutResponse().getItems().isEmpty()))
		{
			//4. if user is not blocked but inclusion error comes --> user will be notified and would be able to place order
			LOG.info("Empty List sent by ECC");
			if(getCartFacade().hasEntries()){
				error.setError("sga.user.inclusion.pricing.error.message");
			}
		}
		else{
		    updateSessionInclusionMap(serviceResponse, new HashSet<String>(), true);
        }

		if (StringUtils.isNotEmpty(error.getError()))
		{
			errorList.add(error);
		}

		if (!errorList.isEmpty())
		{ // If there is no error, dont add any empty list to the response
			responseDTO.setErrors(errorList);
		}


		return responseDTO;
	}

	/**
	 * Find out if the list is empty
	 *
	 * @param errors
	 * @return
	 */
	private boolean isErrorCodesEmpty(final List<Error> errors)
	{
		final AtomicBoolean isEmpty = new AtomicBoolean(Boolean.TRUE);
		errors.stream().forEach(error -> {
			if (StringUtils.isNotEmpty(error.getMessageCode()) && StringUtils.isNotBlank(error.getMessageCode()))
			{
				isEmpty.set(Boolean.FALSE);
			}
		});
		return isEmpty.get();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.apb.facades.checkout.APBCheckoutFacade#isDeliveryDateInValid(java.lang.String, java.lang.String) This
	 * method evaluates for the fringe case delivery scenario while placing the order.
	 */
	@Override
	public boolean isDeliveryDateInValid(final String recordId, final String deliveryDate)
	{
		return apbCheckoutService.isDeliveryDateInValid(recordId, deliveryDate);
	}

	/**
	 * @param cartData
	 * @return true if Any of the product is Excluded in Cart
	 */
	@Override
	public boolean isAnyProdExcl(final CartData cartData)
	{
		boolean isExcluded = false;
		for (final OrderEntryData orderEntryData : cartData.getEntries())
		{
			if (!inclusionExclusionProductStrategy.isProductIncluded(orderEntryData.getProduct().getCode()))
			{
				isExcluded = true;
				break;
			}
		}
		return isExcluded;
	}

	@Override
	public List<AddressData> getSupportedDeliveryAddresses(final boolean visibleAddressesOnly)
	{
		final CartModel cartModel = getCart();
		final String paymentType = cartModel.getPaymentType().getCode();
		cartModel.setPaymentType(CheckoutPaymentType.DELIVERY);

		final List<AddressData> addresses =  (List<AddressData>) (cartModel == null ? Collections.emptyList() : getAddressConverter().convertAll(
				getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel, visibleAddressesOnly)));

		cartModel.setPaymentType(enumerationService.getEnumerationValue(CheckoutPaymentType.class, paymentType));

		return addresses;
	}

	private void updateSessionInclusionMap(final AsahiCheckoutInclusionResponseDTO serviceResponse, final Set<String> exclusionSet, final boolean checkExclusion){
        {
			/*
			 * Converting the response to Map and removing the duplicate items along with the excluded one
			 */
            LOG.info("ECC responded with products list");
            final Map<String, AsahiProductInfo> inclusionMap = new HashMap<>();
            serviceResponse.getCheckoutResponse().getItems().stream().forEach(item -> {
                if (null != item.getIsExcluded() && !item.getIsExcluded() && !ApbCoreConstants.FREE_ITEM_CATEGORY.equals(item.getItemcat()) && !inclusionMap.containsKey(item.getMaterialNumber()))
                {
                    inclusionMap.put(item.getMaterialNumber(), item);
                }
                else if (checkExclusion && null != item.getIsExcluded() && item.getIsExcluded())
                {
                    exclusionSet.add(item.getMaterialNumber());
                }
            });
			/*
			 * if all the items in response are excluded and no product stands eligible and user will not see any product
			 */
            if (MapUtils.isEmpty(inclusionMap))
            {
                LOG.info("Product list is empty. Products sent by ECC were excluded");
                final Map<String, AsahiProductInfo> previousProducts = asahiCoreUtil.getSessionInclusionMap();
                if (MapUtils.isNotEmpty(previousProducts) && !exclusionSet.isEmpty())
                {
                    final Map<String, AsahiProductInfo> checkoutMap = new HashMap<>();
                    checkoutMap.putAll(previousProducts);
                    exclusionSet.stream().forEach(key -> {
                        checkoutMap.remove(key);
                    });
                    asahiCoreUtil.setInclusionMapInSession(checkoutMap);
                }
                asahiCoreUtil.setSessionProductBlock(Boolean.TRUE);
            }
            else
            {
                //5. successfully get inclusion list without any error -- > no message displayed. Cart would be recalculated and products will be updated
                LOG.info("ECC data is now being replaced by previous session data");
                final Map<String, AsahiProductInfo> previousProducts = asahiCoreUtil.getSessionInclusionMap();
                if (MapUtils.isNotEmpty(previousProducts))
                {
                    final Map<String, AsahiProductInfo> checkoutMap = new HashMap<>();
                    checkoutMap.putAll(previousProducts); // setting the previous map products
                    checkoutMap.putAll(inclusionMap); // updating checkout cart request updated products
                    if (!exclusionSet.isEmpty())
                    {
                        exclusionSet.stream().forEach(key -> {
                            checkoutMap.remove(key);
                        });
                    }
                    asahiCoreUtil.setInclusionMapInSession(checkoutMap);
                }
                else
                {
                    asahiCoreUtil.setInclusionMapInSession(inclusionMap);
                }
                asahiCoreUtil.setSessionCheckoutFlag(true);
            }
        }
    }

	@Override
	public Set<String> getCustomerEmailIds()
	{
		// YTODO Auto-generated method stub
		return apbCheckoutService.getCustomerEmailIds();
	}

	@Override
	public EmployeeModel searchBDEByName(final String name)
	{
		return apbCheckoutService.searchBDEByName(name);
	}

	@Override
	public void saveBDEOrderDetails(final BdeOrderDetailsForm bdeCheckoutForm)
	{
		apbCheckoutService.saveBDEOrderDetails(bdeCheckoutForm);
	}
}
