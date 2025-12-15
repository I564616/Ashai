/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.controllers.pages;

import com.sabmiller.facades.recommendation.SABMRecommendationFacade;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestRegisterForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.GuestRegisterValidator;
import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils2.BeanComparator;
import org.apache.commons.collections4.comparators.ComparableComparator;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.SabmB2BEmployeeService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.errors.exceptions.CartThresholdExceededException;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.facades.order.CartStateException;
import com.sabmiller.facades.order.CheckoutTimeoutException;
import com.sabmiller.facades.order.CutoffTimeoutException;
import com.sabmiller.facades.order.SABMCheckoutFacade;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteCartTotalException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteInvalidInvoiceDataException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenException;
import com.sabmiller.merchantsuiteservices.exception.SABMSurchargeCalculationException;
import com.sabmiller.merchantsuiteservices.facade.impl.SABMMerchantSuitePaymentFacadeImpl;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.filters.XSSFilterUtil;
import com.sabmiller.storefront.controllers.pages.SabmAbstractCheckoutController;

/**
 * CheckoutController
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/checkout")
public class CheckoutController extends SabmAbstractCheckoutController
{
	protected static final Logger LOG = LoggerFactory.getLogger(CheckoutController.class.getName());
	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";

	private static final String CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL = "orderConfirmation";
	private static final String CONTINUE_URL_KEY = "continueUrl";

	protected static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";

	protected static final String ALL_RECOMMENDED_PRODUCTS_IN_CART = "allRecommendedProductsInCart";

	@Resource(name = "productFacade")
	private ProductFacade productFacade;

	@Resource(name = "orderFacade")
	private SABMOrderFacade orderFacade;

	@Resource(name = "sabmCheckoutFacade")
	private SABMCheckoutFacade checkoutFacade;

	@Resource(name = "guestRegisterValidator")
	private GuestRegisterValidator guestRegisterValidator;

	@Resource(name = "autoLoginStrategy")
	private AutoLoginStrategy autoLoginStrategy;

	@Resource(name = "cartFacade")
	private SABMCartFacade cartFacade;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bCommerceUnitFacade;

	@Resource(name = "errorEventFacade")
	private ErrorEventFacade errorEventFacade;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "sabmB2BEmployeeService")
	private SabmB2BEmployeeService sabmB2BEmployeeService;


	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bUnitFacade;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private SABMMerchantSuitePaymentFacadeImpl sabmMerchantSuitePaymentFacade;

	@Resource(name = "sabmRecommendationFacade")
	private SABMRecommendationFacade sabmRecommendationFacade;

	private  static final String TEST_MODE="merchant.suite.test.mode";

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;
	
	@Resource(name = "modelService")
	private ModelService modelService;


	@ExceptionHandler(ModelNotFoundException.class)
	public String handleModelNotFoundException(final ModelNotFoundException exception, final HttpServletRequest request)
	{
		request.setAttribute("message", exception.getMessage());
		return FORWARD_PREFIX + "/404";
	}


	@ResponseBody
	@PostMapping("/payByCard")
	public SABMCreditCardTransactionData payByCard(
			@RequestParam(value = "cardType", required = true) final String cardType,
			@RequestParam(value = "poNumber", required = false) final String poNumber)
					throws CMSItemNotFoundException {
		cartFacade.savePurchaseOrderNumber(XSSFilterUtil.filter(poNumber));
		SABMCreditCardTransactionData creditCardTransactionData = null;
		try {
			//force run an additional order calculate for poNumber payments
			checkoutFacade.validateCutoffForCheckout();

			CartModel cartModel = getCheckoutFacade().hasCheckoutCart() ? cartService.getSessionCart() : null;
			getSessionService().setAttribute(SabmCoreConstants.SESSION_CART_PROCESSING_TIME + cartModel.getCode(),new Date());
			creditCardTransactionData = this.sabmMerchantSuitePaymentFacade.initiateCheckoutCCTxn(cardType);
			return creditCardTransactionData;
		}
		catch (SABMMerchantSuiteTokenAPIException e) {
			LOG.error("Error processing Invoice Payment: Integration Exception", e);
			creditCardTransactionData = new SABMCreditCardTransactionData();
			creditCardTransactionData.setError("tokenError=true" );
		}
		catch (SABMSurchargeCalculationException | SABMMerchantSuiteConfigurationException | SABMMerchantSuiteMissingBankDetailsException e) {
			LOG.error("Error processing Invoice Payment: Data Exception", e);
			creditCardTransactionData = new SABMCreditCardTransactionData();
			creditCardTransactionData.setError("invalid=true" );
		}
		catch (final CutoffTimeoutException e)
		{
			LOG.error("Failed to place Order, cutoff exceeded");
			LOG.debug("Failed to place Order", e);
			creditCardTransactionData = new SABMCreditCardTransactionData();
			creditCardTransactionData.setError("cutoffTimeoutError=true" );

		}
		catch (Exception e) {
			LOG.error("Error processing Invoice Payment: General Exception", e);
			creditCardTransactionData = new SABMCreditCardTransactionData();
			creditCardTransactionData.setError("invalid=true" );
		}
		return new SABMCreditCardTransactionData();
	}


	@RequestMapping(method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String sabmcheckout(final RedirectAttributes redirectModel, final Model model,
			@RequestParam(value = "cartdDeliveryInstructions", required = false) final String cartdDeliveryInstructions,
			@RequestParam(value = "error", required = false) final String errors) throws CMSItemNotFoundException
	{

		final int checkoutRefreshMinutes = Config.getInt("checkout.page.refresh.time.minutes", 5);

		if (getCheckoutFlowFacade().hasValidCart())
		{
			if (getCheckoutFacade().hasCheckoutCart())
			{
				final CartModel cartModel = cartService.getSessionCart();
				checkoutFacade.validateDealconditions(cartModel);
				validateCartEntries(cartModel);
			}
			final CartData cartData = getCheckoutFacade().getCheckoutCart();

			try
			{
				if (validateCart(redirectModel))
				{
					LOG.warn("Cart is invalid. Redirecting to cart page");
					return REDIRECT_PREFIX + "/cart";
				}

				// save/update the cart deliveryInstructions   by SAB-535
				cartFacade.saveDeliveryInstructions(StringUtils.trimToEmpty(cartdDeliveryInstructions));
				getCheckoutFacade().setDeliveryAddressIfAvailable();
				final CustomerData userData = getUser();
				cartData.setB2bUnit(userData.getUnit());
				final Map<String, DeliveryModeData> deliveryMap = getSABMDeliveryMode(model);
				populateCommonModelAttributes(model, cartData, new AddressForm());

				// add by 532
				if (null == cartData.getDeliveryMode())
				{
					LOG.warn("Cart is invalid. Redirecting to cart page");
					return REDIRECT_PREFIX + "/cart";
				}

				// save/update the cart deliveryInstructions   by SAB-535
				cartFacade.saveDeliveryInstructions(StringUtils.trimToEmpty(cartdDeliveryInstructions));
				cartData.setDeliveryInstructions(StringUtils.trimToEmpty(cartdDeliveryInstructions));
				//Perform necessary checks on cart (cutoff, order simulate and checkout countdown)
				checkoutFacade.prepareCartForCheckout();

				getCheckoutFacade().setDeliveryAddressIfAvailable();
				cartData.setB2bUnit(userData.getUnit());
				populateCommonModelAttributes(model, cartData, new AddressForm());

				// add by 532
				if (null == cartData.getDeliveryMode())
				{
					if (null != deliveryMap.get("cubArranged"))
					{
						model.addAttribute("cubArrangedFlag", false);
						model.addAttribute("customerArrangedFlag", false);
					}
				}
				else
				{
					final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData b2bUnitdata = b2bCommerceUnitFacade.getB2bUnitData(userData.getUnit().getUid());
					final List<AddressData> deliveryAddresses = b2bUnitdata.getAddresses();
					if (CollectionUtils.isNotEmpty(deliveryAddresses) && deliveryAddresses.size() > 1)
					{
						model.addAttribute("ishasanother", true);
					}
					model.addAttribute("shippingCarriers", b2bUnitdata.getShippingCarriers());

					//START :: INC0570233(unable to do card payment):below conditions is moved out as per the this incident fix
					final boolean currentUserCashOnlyCustomer = cartFacade.isCurrentUserCashOnlyCustomer();
					model.addAttribute("isCashOnlyCustomer", currentUserCashOnlyCustomer);
					//END :: INC0570233(unable to do card payment):below conditions is moved out as per the this incident fix

					//Judging the current cart's DeliveryMode is  cubArranged
					if (cartData.getDeliveryMode().getCode()
							.equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, "")))
					{
						model.addAttribute("cubArrangedFlag", true);
						model.addAttribute("customerArrangedFlag", false);
					}
					//Judging the current cart's DeliveryMode is  customerArranged
					else if (cartData.getDeliveryMode().getCode()
							.equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, "")))
					{
						model.addAttribute("cubArrangedFlag", false);
						// Fixed as per RITM0555025
						if (null != cartData.getDeliveryShippingCarrier())
						{
							model.addAttribute("selectDeliveryShippingCarrier", cartData.getDeliveryShippingCarrier().getDescription());

						}
						else
						{
							LOG.error("Shipping carrier is missing for this cart. Redirecting to cart page");
							return REDIRECT_PREFIX + "/cart";
						}
						model.addAttribute("customerArrangedFlag", true);
					}
				}
				model.addAttribute("checkoutRefreshMinutes", checkoutRefreshMinutes);
				model.addAttribute("pageType", SABMWebConstants.PageType.CHECKOUT.name());
				model.addAttribute("checkoutStep", 2);
				model.addAttribute("isBdeOrderingEnabled",true);
                Gson gson = new Gson();
                String json = gson.toJson(sabmMerchantSuitePaymentFacade.fetchCreditCardValidationData());
                model.addAttribute("ccValidationData", json);
				model.addAttribute("paymentTestMode", configurationService.getConfiguration().getBoolean(TEST_MODE,false));
				if(userService.getCurrentUser() instanceof BDECustomerModel){

				B2BCustomerModel b2bCustomer=(B2BCustomerModel)userService.getCurrentUser();
				EmployeeModel bde = sabmB2BEmployeeService.searchBDEByUid(b2bCustomer.getEmail());
				model.addAttribute("bdeUserEmailId",bde!=null?bde.getUid():"");
				
				Set<String> customerEmailIds = new HashSet<String>();
				Collection<String> disabledList = b2bUnitService.getCUBDisabledList(userData.getUnit().getUid());
				List<B2BCustomerModel> customerModelList = b2bUnitService.getCustmoersExceptZADP(cartService.getSessionCart().getUnit());
				if(CollectionUtils.isNotEmpty(customerModelList)){
   				for(B2BCustomerModel customer:  customerModelList ){

   					if(BooleanUtils.isTrue(customer.getActive()) && customer.getEmail()!=null && (null== disabledList || !disabledList.contains(customer.getEmail()))){
   						customerEmailIds.add(customer.getEmail() + ":" + customer.getFirstName());
   					}
   				}
				}

				final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData rootB2bUnit = b2bUnitFacade.getRootB2bUnit();
				if(rootB2bUnit!=null){
				Collection<CustomerData> zadpCustomers = rootB2bUnit.getCustomers();

				if(CollectionUtils.isNotEmpty(zadpCustomers)){
					for(CustomerData customer:zadpCustomers){
						if(BooleanUtils.isTrue(customer.isActive()) && (null== disabledList || !disabledList.contains(customer.getEmail()))) {
							customerEmailIds.add(customer.getEmail() + ":" + customer.getFirstName());
						}
					
					}
				}
				}
				
				model.addAttribute("customerEmailIds",StringUtils.join(customerEmailIds,","));
				model.addAttribute("bdeOrderEmailText",cartData.getBdeOrderEmailText()!=null?cartData.getBdeOrderEmailText():"");
				model.addAttribute("isBdeOrderingEnabled",sabmConfigurationService.isBdeOrderingEnabled());

                }
				return ControllerConstants.Views.Pages.Checkout.CheckoutPage;
			}
			catch (final CheckoutTimeoutException e)
			{
				LOG.error("Checkout Timeout Error occurred for cart {}.", cartData.getCode());
				LOG.debug("Checkout Timeout Error occurred for cart {}.", cartData.getCode(), e);
				redirectModel.addAttribute("checkoutTimeoutError", true);
				return REDIRECT_PREFIX + "/cart";
			}
			catch (final CutoffTimeoutException e)
			{
				LOG.error("Cutoff Timeout Error occurred for cart {}.", cartData.getCode());
				LOG.debug("Cutoff Timeout Error occurred for cart {}.", cartData.getCode(), e);
				redirectModel.addAttribute("cutoffTimeoutError", true);
				return REDIRECT_PREFIX + "/cart";
			}
			catch (final CartStateException e)
			{
				LOG.error("Order simulate came back with changes for cart {}.", cartData.getCode());
				LOG.debug("Order simulate came back with changes for cart {}.", cartData.getCode(), e);
				redirectModel.addAttribute("invalidCart", true);
				return REDIRECT_PREFIX + "/cart";

			}
			catch (final IllegalStateException e)
			{
				LOG.error(
						"Exception occurred preparing cart for chekout. Cart is not valid for checkout, sending customer back to cart page ");
				if (e.getCause() instanceof CartThresholdExceededException)
				{
					redirectModel.addAttribute("cartThresholdError", true);
				}
				else
				{
					LOG.error("Exception occurred preparing cart for checkout.", e);
					final String code = errorEventFacade.createErrorEntry(e, "sap", null, ErrorEventType.SAP, null);
					redirectModel.addAttribute("calculationError", code);

				}
				return REDIRECT_PREFIX + "/cart";
			}
		}

		LOG.error("Missing, empty or unsupported cart");

		// No session cart or empty session cart. Bounce back to the cart page.
		return REDIRECT_PREFIX + "/cart";
	}

	@RequestMapping(value = "/placeOrder")
	@RequireHardLogIn
	public String placeOrder(final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException
	{
		return checkout(redirectModel);
	}

	@RequestMapping(value = "/placeOrderByAccount")
	@RequireHardLogIn
	public String placeOrderByAccount(final Model model, final RedirectAttributes redirectModel,
			@RequestParam(value = "poNumber", required = false) final String poNumber)
			throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException
	{
		cartFacade.savePurchaseOrderNumber(XSSFilterUtil.filter(poNumber));

		try
		{
			//force run an additional order calculate for poNumber payments
			checkoutFacade.validateCutoffForCheckout();
		}
		catch (final CutoffTimeoutException e)
		{
			LOG.error("Failed to place Order, cutoff exceeded");
			LOG.debug("Failed to place Order", e);
			redirectModel.addAttribute("cutoffTimeoutError", "true");
			return REDIRECT_PREFIX + "/cart";

		}

		return checkout(redirectModel);
	}

	protected String checkout(final RedirectAttributes redirectModel)
	{
		//Validate the cart
		if (validateCart(redirectModel))
		{
			// Invalid cart. Bounce back to the cart page.
			return REDIRECT_PREFIX + "/cart";
		}

		final OrderData orderData;
		final Map<String,String> recommendedProductsInCart = sabmRecommendationFacade.getAllProductRecommendationsInCart();
		try
		{
			orderData = getCheckoutFacade().placeOrder();
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);

			checkoutFacade.markCartForRecalculation();

			final String code = errorEventFacade.createErrorEntry(e, "sap", null, ErrorEventType.SAP, null);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"checkout.payaccount.ordersimulate.failed", new Object[]
					{ code });
			return REDIRECT_PREFIX + "/cart";
		}
		redirectModel.addFlashAttribute(ALL_RECOMMENDED_PRODUCTS_IN_CART,recommendedProductsInCart);
		return redirectToOrderConfirmationPage(orderData);
	}

	/**
	 * Method for get SABMDeliveryMode Contain(cub and customer)
	 *
	 * @param model
	 * @return Map<String,DeliveryModeData>
	 */
	@SuppressWarnings("boxing")
	protected Map<String, DeliveryModeData> getSABMDeliveryMode(final Model model)
	{
		final List<? extends DeliveryModeData> listZoneDeliveryModeData = getCheckoutFacade().getSupportedDeliveryModes();
		final Map<String, DeliveryModeData> deliverymap = new HashMap<String, DeliveryModeData>();
		if (CollectionUtils.isNotEmpty(listZoneDeliveryModeData))
		{
			for (final DeliveryModeData del : listZoneDeliveryModeData)
			{
				if (del.getCode().equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, ""))
						&& !getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, "").isEmpty())
				{
					model.addAttribute("cubArranged", del);
					deliverymap.put("cubArranged", del);
				}
				if (del.getCode().equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, ""))
						&& !getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, "").isEmpty())
				{
					model.addAttribute("customerArranged", del);
					deliverymap.put("customerArranged", del);
				}
			}
		}
		return deliverymap;
	}

	protected void populateCommonModelAttributes(final Model model, final CartData cartData, final AddressForm addressForm)
			throws CMSItemNotFoundException
	{
		model.addAttribute("cartData", cartData);
		model.addAttribute("addressForm", addressForm);
		//SAB-535 the DeliveryAddress check has been in CartPageController
		model.addAttribute("deliveryAddresses", cartData.getDeliveryAddress());
		model.addAttribute("noAddress", Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()));
		model.addAttribute("addressFormEnabled", Boolean.valueOf(getCheckoutFacade().isNewAddressEnabledForCart()));
		model.addAttribute("removeAddressEnabled", Boolean.valueOf(getCheckoutFacade().isRemoveAddressEnabledForCart()));
		model.addAttribute("showSaveToAddressBook", Boolean.TRUE);
		//model.addAttribute(WebConstants.BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(getBreadcrumbKey()));
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("isCashOnlyCustomer", cartFacade.isCurrentUserCashOnlyCustomer());
		if (StringUtils.isNotBlank(addressForm.getCountryIso()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
			model.addAttribute("country", addressForm.getCountryIso());
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
	}

	@GetMapping("/orderConfirmation/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
	@RequireHardLogIn
	public String orderConfirmation(@PathVariable("orderCode") final String orderCode, final Model model)
			throws CMSItemNotFoundException
	{
		return processOrderCode(orderCode, model);
	}


	@PostMapping("/orderConfirmation/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
	public String orderConfirmation(final GuestRegisterForm form, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		getGuestRegisterValidator().validate(form, bindingResult);
		return processRegisterGuestUserRequest(form, bindingResult, model, request, response, redirectModel);
	}

	/**
	 * Method used to determine the checkout redirect URL that will handle the checkout process.
	 *
	 * @return A <code>String</code> object of the URL to redirect to.
	 */
	protected String getCheckoutRedirectUrl()
	{
		if (getUserFacade().isAnonymousUser())
		{
			return REDIRECT_PREFIX + "/login/checkout";
		}

		// Default to the multi-step checkout
		return REDIRECT_PREFIX + "/checkout/multi";
	}

	@ResponseBody
	@PostMapping("/updateBDEOrderDetails")
	public boolean updateBDEOrderDetails(@RequestBody final BdeOrderDetailsForm form, final BindingResult bindingResult, final Model model)
			
	{
		checkoutFacade.saveBDEOrderingDetails(form);
		return true;
	}


	
	
	protected String processRegisterGuestUserRequest(final GuestRegisterForm form, final BindingResult bindingResult,
			final Model model, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return processOrderCode(form.getOrderCode(), model);
		}
		try
		{
			getCustomerFacade().changeGuestToCustomer(form.getPwd(), form.getOrderCode());
			getAutoLoginStrategy().login(getCustomerFacade().getCurrentCustomer().getUid(), form.getPwd(), request, response);
			getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
		}
		catch (final DuplicateUidException e)
		{
			// User already exists
			LOG.warn("guest registration failed: " + e);
			model.addAttribute(new GuestRegisterForm());
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"guest.checkout.existingaccount.register.error", new Object[]
					{ form.getUid() });
			return REDIRECT_URL_ORDER_CONFIRMATION + form.getOrderCode();
		}

		return REDIRECT_PREFIX + "/";
	}

	protected String processOrderCode(final String orderCode, final Model model) throws CMSItemNotFoundException
	{
		final String email = orderFacade.getEmailByOrder(orderCode);
		model.addAttribute("email", email);

		final String continueUrl = (String) getSessionService().getAttribute(WebConstants.CONTINUE_URL);
		model.addAttribute(CONTINUE_URL_KEY, (continueUrl != null && !continueUrl.isEmpty()) ? continueUrl : ROOT);

		final AbstractPageModel cmsPage = getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, cmsPage);
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("orderCode", orderCode);
		//New code for google analytics INC0180820
		model.addAttribute("pageType", PageType.ORDERCONFIRMATION.name());
		model.addAttribute("smartRecommendationGroup", sabmRecommendationFacade.getCurrentSmartRecommendationGroup());
		final OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
		
		if(orderDetails.getBdeOrderEmails()!= null && CollectionUtils.isNotEmpty(orderDetails.getBdeOrderEmails()))
		{
			final Set<String> uniqueEmailIds = orderDetails.getBdeOrderEmails().stream().collect(Collectors.toSet());
			List<String> listUniqueUserEmailId = uniqueEmailIds.stream().collect(Collectors.toList());
			orderDetails.setBdeOrderEmails(listUniqueUserEmailId);	
		}
		
		model.addAttribute("orderData", orderDetails);

		return ControllerConstants.Views.Pages.Checkout.CheckoutConfirmationPage;
	}

	protected GuestRegisterValidator getGuestRegisterValidator()
	{
		return guestRegisterValidator;
	}

	protected AutoLoginStrategy getAutoLoginStrategy()
	{
		return autoLoginStrategy;
	}
	
	@SuppressWarnings("unchecked")
	protected void validateCartEntries(CartModel cartModel)
	{
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>(cartModel.getEntries());
		Collections.sort(entries, new BeanComparator(AbstractOrderEntryModel.ENTRYNUMBER, new ComparableComparator()));
		Set<Integer> entryNumbers = new HashSet<Integer>();
		List<AbstractOrderEntryModel> duplicateEntryNoEntry = new ArrayList<AbstractOrderEntryModel>();
		
		for(AbstractOrderEntryModel entry : entries)
		{
			int entryNumber = entry.getEntryNumber();
			if(entryNumbers.contains(entryNumber)){
				duplicateEntryNoEntry.add(entry);
			}
			entryNumbers.add(entryNumber);
		}
		LOG.info("Cart Error validateCartEntries cart : {} duplicateEntryNoEntry : {} ", cartModel.getCode(), duplicateEntryNoEntry.size());
		if(duplicateEntryNoEntry.size() > 0)
		{	
			for (int i = 1; i <= duplicateEntryNoEntry.size(); i++)
			{
				(duplicateEntryNoEntry.get(i-1)).setEntryNumber(Integer.valueOf(entries.get(entries.size()-1).getEntryNumber() + i));				
				modelService.save(duplicateEntryNoEntry.get(i-1));
			}
			modelService.refresh(cartModel);
		}
		
	}


}
