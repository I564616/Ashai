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

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCartPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateQuantityForm;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.commons.enumerations.LoginStatus;
import com.sabmiller.core.comparators.ShippingCarrierComparator;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.PackType;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.LostdealJson;
import com.sabmiller.facades.order.CheckoutTimeoutException;
import com.sabmiller.facades.order.CutoffTimeoutException;
import com.sabmiller.facades.order.OrderMessageData;
import com.sabmiller.facades.order.SABMCheckoutFacade;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.order.json.OrderHistoryJson;
import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.filters.XSSFilterUtil;
import com.sabmiller.storefront.form.SABMAddToCartForm;
import com.sabmiller.storefront.form.SABMUpdateOrderTemplateForm;
import com.sabmiller.storefront.form.SABMUpdateQuantityForm;

import com.apb.core.util.AsahiCoreUtil;


/**
 * Controller for cart page
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/cart")
public class CartPageController extends SabmAbstractCartPageController	
{
	private static final Logger LOG = LoggerFactory.getLogger(CartPageController.class);

	public static final String SHOW_CHECKOUT_STRATEGY_OPTIONS = "storefront.show.checkout.flows";
	public static final String ERROR_MSG_TYPE = "errorMsg";
	public static final String SUCCESSFUL_MODIFICATION_CODE = "success";
	private static final String EMPTY_CART_CMS_PAGE_LABEL = "empty-cart";
	/** The Constant VISITED_DEALS_PAGE. */
	private static final String VISITED_DEALS_PAGE = "visitedDealsPage";

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "sabmCartFacade")
	private SABMCartFacade cartFacade;

	@Resource(name = "sabmCheckoutFacade")
	private SABMCheckoutFacade checkoutFacade;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bCommerceUnitFacade;

	@Resource(name = "productModelUrlResolver")
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bUnitFacade;

	@Resource(name = "errorEventFacade")
	private ErrorEventFacade errorEventFacade;

	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "shippingCarrierComparator")
	private ShippingCarrierComparator shippingCarrierComparator;

	@Override
	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Resource(name = "orderFacade")
	private SABMOrderFacade orderFacade;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;


	@PostMapping("/showOtherFreeGoods")
	@ResponseBody
	public List<DealFreeProductJson> getFreeProducts(@RequestBody final SABMAddToCartForm form)
	{
		final List<DealJson> deals = sabmDealsSearchFacade.searchDeals(true);

		for (final DealJson dealJson : ListUtils.emptyIfNull(deals))
		{
			for (final DealFreeProductJson freeProduct : ListUtils.emptyIfNull(dealJson.getSelectableProducts()))
			{
				if (freeProduct.getCode().equals(form.getDealCode()))
				{
					cartFacade.checkFreeGoodsDealAppliedTimes(dealJson);
					return dealJson.getSelectableProducts();
				}
			}
		}

		LOG.warn("Wrong data in cart, the deal {} doesn't exist", form.getDealCode());
		return null;
	}

	public String showCart(final Model model) throws CMSItemNotFoundException, CommerceCartModificationException
	{
		return showCart(model, null, null);
	}

	/*
	 * Display the cart page
	 */
	@SuppressWarnings("boxing")
	@GetMapping
	public String showCart(final Model model, @RequestParam(value = "error", required = false) final String errors,
			@RequestParam(value = "checkoutStep", required = false) final Integer checkoutStep)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{
		//If cut off time is exceeded, refresh the core entities and add the delivery date to session
		if (asahiCoreUtil.isNAPUser()) {
			return FORWARD_PREFIX + "/404";
			// throw new CMSItemNotFoundException("Not allowed access since this user is a
			// part of NAP group");

		}
		if (b2bUnitFacade.isCutOffTimeExceeded())
		{
			final Date deliveryDate = ((SABMCustomerFacade) getCustomerFacade()).getNextDeliveryDateAndUpdateSession();
			model.addAttribute("cutoffTimeExceeded", true);
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.page.cutofftime.error.message", null);
			LOG.warn("Cut off Time exceeded. The next delivery date is {} ", deliveryDate);
		}

		model.addAttribute("requiresCalculation", cartFacade.requiresCalculation());
		// add by yuxiao.wang for SAB-568 -Validate the total price is correct - commenting for now since no calculation/ price verification should be done in hybris
		updateDefaultDeliveryDetails();
		cartFacade.removeRejectedDealIfNotQualify();
		// get the rejected deal from cart
		model.addAttribute("rejectedDeals", cartFacade.getRejectedDealFromCart());
		model.addAttribute("isCashOnlyCustomer", cartFacade.isCurrentUserCashOnlyCustomer());
		model.addAttribute(VISITED_DEALS_PAGE, sessionService.getAttribute(VISITED_DEALS_PAGE));
		Date currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
		if (currentDeliveryDate == null)
		{
			currentDeliveryDate = new Date();
		}
		final List<DealJson> deals = sabmDealsSearchFacade.searchDeals(currentDeliveryDate, Boolean.TRUE);
		if (deals.size() == 0)
		{
			model.addAttribute("hasUpcomingDeals", sabmDealsSearchFacade.hasUpcomingDeals());
		}
		model.addAttribute("deals", deals);
		model.addAttribute("cartDealsData", sabmDealsSearchFacade.getCartDealsData());
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.cart"));
		model.addAttribute("pageType", PageType.CART.name());
		model.addAttribute("cartUnChangeTime", getSiteConfigService().getProperty("sabmstorefront.cart.unchange.time.limitSecond"));
		model.addAttribute("checkoutStep", checkoutStep == null || checkoutStep.intValue() == 0 ? 1 : checkoutStep);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		prepareDataForPage(model);

		// SAB-567, cart entry is empty, forward to the empty page
		final Map<String, Object> mapModel = model.asMap();
		final CartData cartData = (CartData) mapModel.get("cartData");
		if (null != cartData && CollectionUtils.isEmpty(cartData.getEntries()))
		{
			// by SAB-564 get the list OrderHistoryJson
			final int MAX_ORDERHISTROY_COUNT = getSiteConfigService().getInt(SABMWebConstants.MAX_ORDERHISTROY_COUNT, 5);
			// by SAB-581 get the list OrderHistoryJson
			final List<OrderHistoryJson> orderHistoryList = orderFacade.getTopOrderHistory(MAX_ORDERHISTROY_COUNT);
			model.addAttribute("maxOrderHistoryCount", MAX_ORDERHISTROY_COUNT);
			model.addAttribute("orderHistoryList", orderHistoryList);

			//get top 5 order template for empty cart page
			model.addAttribute("maxOrderTemplateCount", getSiteConfigService().getLong(SABMWebConstants.MAX_ORDERTEMPLATE_COUNT, 5));
			model.addAttribute("orderTemplates", b2bCommerceUnitFacade.getB2BUnitOrderTemplates());

			storeCmsPageInModel(model, getContentPageForLabelOrId(EMPTY_CART_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(EMPTY_CART_CMS_PAGE_LABEL));
			return ControllerConstants.Views.Pages.Cart.EmptyCartPage;
		}

		//SAB-535
		if (cartData != null)
		{
			setSABMDelivery(model, cartData);
		}		
		
		if (LoginStatus.SAP_CHECKOUT_BLOCKED.equals(((SABMCustomerFacade) getCustomerFacade()).getLoginStatus()))
		{
			model.addAttribute("blockCheckout", true);
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.page.sap.checkout.blocked", null);
		}
		List<String> cartRuleErrors = cartFacade.validateCustomCartRules(cartData);
		if(cartRuleErrors.size()>0){
			checkoutFacade.markCartForRecalculation();
			model.addAttribute("requiresCalculation", true);
			for(String error: cartRuleErrors){
			GlobalMessages.addErrorMessage(model, error);
			}
		}

		return ControllerConstants.Views.Pages.Cart.CartPage;
	}

	public String showCartLight(final Model model) throws CMSItemNotFoundException
	{
		//If cut off time is exceeded, refresh the core entities and add the delivery date to session
		if (b2bUnitFacade.isCutOffTimeExceeded())
		{
			final Date deliveryDate = ((SABMCustomerFacade) getCustomerFacade()).getNextDeliveryDateAndUpdateSession();
			model.addAttribute("cutoffTimeExceeded", true);
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.page.cutofftime.error.message", null);
			LOG.warn("Cut off Time exceeded. The next delivery date is {} ", deliveryDate);
		}

		// add by yuxiao.wang for SAB-568 -Validate the total price is correct - commenting for now since no calculation/ price verification should be done in hybris
		updateDefaultDeliveryDetails();
		cartFacade.removeRejectedDealIfNotQualify();
		// get the rejected deal from cart
		final List<String> rejectedDealTitles = cartFacade.getRejectedDealFromCart();
		model.addAttribute("rejectedDeals", rejectedDealTitles);
		model.addAttribute("cartUnChangeTime", getSiteConfigService().getProperty("sabmstorefront.cart.unchange.time.limitSecond"));
		prepareDataForPage(model);
		// SAB-567, cart entry is empty, forward to the empty page
		final Map<String, Object> mapModel = model.asMap();
		final CartData cartData = (CartData) mapModel.get("cartData");

		//SAB-535
		if (cartData != null)
		{
			setSABMDelivery(model, cartData);
		}

		return ControllerConstants.Views.Pages.Cart.CartPage;
	}

	@PostMapping("/update")
	public String updateCartQuantities(@RequestParam("entryNumber") final long entryNumber, final Model model,
			@Valid final UpdateQuantityForm form, final BindingResult bindingResult, final HttpServletRequest request,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				if (("typeMismatch").equals(error.getCode()))
				{
					GlobalMessages.addErrorMessage(model, "basket.error.quantity.invalid");
				}
				else
				{
					GlobalMessages.addErrorMessage(model, error.getDefaultMessage());
				}
			}
		}
		else if (getCartFacade().hasEntries())
		{
			try
			{
				final CartModificationData cartModification = getCartFacade().updateCartEntry(entryNumber,
						form.getQuantity().longValue());
				if (cartModification.getQuantity() == form.getQuantity().longValue())
				{
					// Success

					if (cartModification.getQuantity() == 0)
					{
						// Success in removing entry
						GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
								"basket.page.message.remove");
					}
					else
					{
						// Success in update quantity
						GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
								"basket.page.message.update");
					}
				}
				else if (cartModification.getQuantity() > 0)
				{
					// Less than successful
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.page.message.update.reducedNumberOfItemsAdded.lowStock", new Object[]
							{ cartModification.getEntry().getProduct().getName(), cartModification.getQuantity(), form.getQuantity(), request.getRequestURL().append(cartModification.getEntry().getProduct().getUrl()) });
				}
				else
				{
					// No more stock available
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.page.message.update.reducedNumberOfItemsAdded.noStock", new Object[]
							{ cartModification.getEntry().getProduct().getName(), request.getRequestURL().append(cartModification.getEntry().getProduct().getUrl()) });
				}

				// Redirect to the cart page on update success so that the browser doesn't re-post again
				return REDIRECT_PREFIX + "/cart";
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
			}
		}

		prepareDataForPage(model);

		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.cart"));
		model.addAttribute("pageType", PageType.CART.name());

		return ControllerConstants.Views.Pages.Cart.CartPage;
	}

	@SuppressWarnings("boxing")
	protected OrderEntryData getOrderEntryData(final long quantity, final String productCode, final Integer entryNumber)
	{
		final OrderEntryData orderEntry = new OrderEntryData();
		orderEntry.setQuantity(quantity);
		orderEntry.setProduct(new ProductData());
		orderEntry.getProduct().setCode(productCode);
		orderEntry.setEntryNumber(entryNumber);
		return orderEntry;
	}

	/**
	 * remove cart item by entryNumber, Used to remove the cart Item for local refresh
	 *
	 * @param entryNumber
	 * @param model
	 * @param form
	 * @param bindingResult
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/remove")
	public String removeCartItem(@RequestParam("entryNumber") final long entryNumber, final Model model,
			@Valid final UpdateQuantityForm form, final BindingResult bindingResult) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			// page data binding error
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				if ("typeMismatch".equals(error.getCode()))
				{
					GlobalMessages.addErrorMessage(model, "basket.error.quantity.invalid");
				}
				else
				{
					GlobalMessages.addErrorMessage(model, error.getDefaultMessage());
				}
			}
		}
		else if (getCartFacade().hasEntries())
		{
			try
			{
				// call to hybris existed updateCartEntry method to remove cart item
				getCartFacade().updateCartEntry(entryNumber, form.getQuantity().longValue());
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
			}
		}

		// prepare the required data for the page
		prepareDataForPage(model);
		return ControllerConstants.Views.Fragments.Checkout.AjaxCartItemsBody;
	}

	/**
	 * clear cart
	 *
	 * @param model
	 * @return navigate page path
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/clear")
	public String clearCart(final Model model) throws CMSItemNotFoundException
	{
		if (getCartFacade().hasEntries())
		{
			//Clear only the cart entries
			cartFacade.clearCartEntries();
			// Redirect to the cart page on update success so that the browser doesn't re-post again
			return REDIRECT_PREFIX + "/cart";
		}

		// prepare the required data for the page
		prepareDataForPage(model);

		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.cart"));
		model.addAttribute("pageType", PageType.CART.name());

		return ControllerConstants.Views.Pages.Cart.CartPage;
	}

	/**
	 * SAB-574 The method invoke the order Simulation and
	 *
	 * @param model
	 * @return navigate page path
	 * @throws CMSItemNotFoundException
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "/orderSimulation")
	public String orderSimulation(final Model model) throws CMSItemNotFoundException, CommerceCartModificationException
	{

		try
		{
			if (cartFacade.isExistBaseProduct())
			{
				checkoutFacade.runOrderSimulate(false);
			}
			else
			{
				getCartFacade().removeSessionCart();
			}
		}
		catch (final CheckoutTimeoutException e)
		{
			LOG.error("Checkout timed out!!!! ", e);
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.page.checkout.timeout.error.message",
					null);
		}
		catch (final CutoffTimeoutException e)
		{
			LOG.error("Cutoff expired!!!! ", e);
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.page.cutofftime.error.message", null);
			model.addAttribute("cartThresholdError", true);
		}
		catch (final IllegalStateException | CalculationException ex)
		{
			final String code = errorEventFacade.createErrorEntry(ex, "sap", null, ErrorEventType.SAP, null);
			LOG.error("Exception occured while running order simulate ", ex);
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.page.salesordersimulate.error.message",
					new String[]
					{ code });

		}

		final List<OrderMessageData> messages = checkoutFacade.getSapCartChanges();

		if (CollectionUtils.isNotEmpty(messages))
		{
			for (final OrderMessageData errorMessage : messages)
			{
				GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, errorMessage.getCode(),
						errorMessage.getArguments() != null ? errorMessage.getArguments().toArray() : null);
			}

		}

		return showCartLight(model);
	}

	@PostMapping("/saveOrderTemplate")
	public String createOrderTemplate(@RequestBody @Valid final SABMUpdateOrderTemplateForm form,
			final RedirectAttributes redirectModel)
	{
		if (form != null && b2bCommerceUnitFacade.createOrderTemplateByCart(StringUtils.trim(form.getName())))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "create.order.template.success");
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "create.order.template.error");
		}

		return REDIRECT_PREFIX + "/cart";
	}

	@PostMapping("/addProductToTemplate")
	public String addProductToTemplate(@RequestParam(value = "orderCode", required = true) final String orderCode,
			@RequestParam(value = "productCode", required = true) final String productCode,
			@RequestParam(value = "fromUnit", required = false) final String fromUnit,
			@RequestParam(value = "quantity", required = false) final Long quantity, final RedirectAttributes redirectModel)
	{
		if (b2bCommerceUnitFacade.addProductToTemplate(XSSFilterUtil.filter(orderCode), XSSFilterUtil.filter(productCode), fromUnit, quantity))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "add.product.template.success");
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "add.product.template.error");
		}

		return REDIRECT_PREFIX + productModelUrlResolver.resolve(productService.getProductForCode(XSSFilterUtil.filter(productCode)));
	}

	/**
	 * create new orderTemplate and save product to the template
	 *
	 * @param orderName
	 * @param productCode
	 * @param fromUnit
	 * @param quantity
	 * @return String
	 */
	@PostMapping("/saveProductToNewTemplate")
	public String saveProductToNewTemplate(@RequestParam(value = "orderName", required = true) final String orderName,
			@RequestParam(value = "productCode", required = true) final String productCode,
			@RequestParam(value = "fromUnit", required = false) final String fromUnit,
			@RequestParam(value = "quantity", required = false) final Long quantity, final RedirectAttributes redirectModel)
	{
		//create new OrderTemplate by orderName
		final String returnCode = b2bCommerceUnitFacade.createEmptyOrderTemplateByName(XSSFilterUtil.filter(orderName));
		if (null == returnCode)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.product.create.template.error");
		}
		else
		{
			//Adds the product to new template.
			if (b2bCommerceUnitFacade.addProductToTemplate(returnCode, XSSFilterUtil.filter(productCode), fromUnit, quantity))
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "add.product.template.success");
			}
			else
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "add.product.template.error");
			}
		}

		return REDIRECT_PREFIX + productModelUrlResolver.resolve(productService.getProductForCode(productCode));
	}

	/**
	 * update cart entry quantity or unit
	 *
	 * @param form
	 * @return String
	 */
	@PostMapping("/updateQuantity")
	@ResponseBody
	public String ajaxUpdateCartQuantities(@Valid final SABMUpdateQuantityForm form)
	{
		//SAB-572 If has no entries will not update it
		if (getCartFacade().hasEntries() && form != null && form.getEntryNumber() != null && form.getQuantity() != null)
		{
			final LostdealJson lostDeal = sabmDealsSearchFacade.isLostDeal(form.getEntryNumber().toString(),
					form.getQuantity().intValue(), form.getUnit());

			if (lostDeal != null && lostDeal.isIsLost())
			{
				sabmDealsSearchFacade.deleteCartDeal(lostDeal.getCode());

				//SABMC-1648 add the lost deal to session, so we can skip to check it when we get the partially qualified.
				//this session will be used in the PartiallyQualifiedDealConverter
				sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_LOST_DEAL_CODES, lostDeal.getCode());
			}

			try
			{
				CartModificationData cartModification = new CartModificationData();
				if (form.getUnit() != null)
				{
					cartModification = cartFacade.updateCartEntry(form.getEntryNumber().longValue(), form.getQuantity().longValue(),
							form.getUnit());
				}
				else
				{
					cartModification = cartFacade.updateCartEntry(form.getEntryNumber().longValue(), form.getQuantity().longValue());
				}

				if(cartModification != null && cartModification.getStatusCode() != null && cartModification.getStatusCode().startsWith(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED)) {
					return cartModification.getStatusCode();
				}
				// return the quantity in the cartModification SAB-572
				if (cartModification != null && cartModification.getEntry() != null)
				{
					return cartModification.getEntry().getBaseQuantity() + "";
				}
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + form.getEntryNumber() + ".", ex);
			}
		}

		return null;
	}


	/**
	 * Method for update delivery of cart add by SAB-535
	 *
	 * @param delmodeCode
	 * @param carrierCode
	 * @return String
	 */
	@PostMapping("/updateSABMdelivery")
	public String ajaxUpdateSABMdelivery(@RequestParam(value = "delmodeCode", required = false) final String delmodeCode,
			@RequestParam(value = "carrierCode", required = false) final String carrierCode, final Model model)
	{

		checkoutFacade.markCartForRecalculation();

		if (StringUtils.isNotEmpty(delmodeCode))
		{
			getCheckoutFacade().setDeliveryMode(delmodeCode);
		}

		// if CUB arranged, cart carrier will be default shipping carrier
		if (StringUtils.equalsIgnoreCase(delmodeCode,
				getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, "")))

		{
			cartFacade.saveDefaultShippingCarriers();
		}
		else if (StringUtils.isNotEmpty(carrierCode))
		{
			cartFacade.saveShippingCarriers(carrierCode); 
		}

		final CartData cart = cartFacade.getSessionCart();
		model.addAttribute("cartData", cartFacade.getSessionCart());
		setSABMDelivery(model, cart);
		return ControllerConstants.Views.Pages.Cart.CartTopOptionAjax;
	}

	/**
	 * Method for update delivery Address
	 *
	 * @param addressId
	 * @param defaultAddress
	 * @return String
	 */
	@PostMapping("/updateSABMdeliveryAddress")
	public String ajaxUpdateSABMdeliveryAddress(@RequestBody final AddressForm address, final Model model)
	{
		boolean result = false;
		// determine the cart have entry
		if (getCartFacade().hasEntries() && StringUtils.isNotEmpty(address.getAddressId()))
		{
			// save the address to cart if the defaultAddress is true then save defaultShipmentAddress to user
			result = checkoutFacade.setDeliveryAddress(address.getAddressId(), address.getDefaultAddress());
			checkoutFacade.markCartForRecalculation();
		}
		//if them are true then set GlobalMessages  display the front
		if (result && BooleanUtils.isTrue(address.getDefaultAddress()))
		{
			GlobalMessages.addMessage(model, GlobalMessages.CONF_MESSAGES_HOLDER, "basket.page.savedefaultAddress.info.message",
					null);
		}
		final CartData cart = cartFacade.getSessionCart();
		model.addAttribute("cartData", cartFacade.getSessionCart());
		setSABMDelivery(model, cart);
		return ControllerConstants.Views.Pages.Cart.CartTopOptionAjax;
	}

	/**
	 * It is assumed that the invoking JS code will send the requested delivery date in dd/MM/yyyy format. First, the CUP
	 * and Deals are refreshed for the customer(B2BUnit) following which, the date is set in the session.
	 */
	@PostMapping("/updateDeliveryDate")
	@ResponseBody
	public String ajaxUpdateDeliveryDate(@RequestParam("deliveryDate") final String deliveryDate, @RequestParam("deliveryDatePackType") final String deliveryDatePackType) throws ParseException
	{
		LOG.debug("Requested Delivery Date in the ajax request : {} ", deliveryDate);

		//Get the date format for the Requested Delivery Date
		final Date date = SabmDateUtils.getDate(deliveryDate, SabmCoreConstants.DELIVERY_DATE_PATTERN);

		cartFacade.saveRequestedDeliveryDate(date, deliveryDatePackType);

		//Refresh CUP and Deals
		((SABMCustomerFacade) getCustomerFacade()).verifyAndUpdateCUPForRDD(date, deliveryDatePackType);

		return String.valueOf(Boolean.TRUE);
	}

	/**
	 * This method is for the cart page. When the customer select the conflict deal, this method will be call and then
	 * redirect to the cart page, to find if there still have conflict deals.
	 *
	 */
	@PostMapping("/selectConflictDeal")
	public String addManualConflictDealToCart(@RequestParam(value = "dealCode", required = true) final String dealCode,
			final RedirectAttributes redirectModel)
	{
		LOG.debug("Requested select conflict deal : {} ", dealCode);

		cartFacade.addApplyDealToCart(XSSFilterUtil.filter(dealCode), DealConditionStatus.MANUAL_CONFLICT);

		// This will be used for popup for select the second conflict deal
		redirectModel.addFlashAttribute("secondConflict", Boolean.TRUE.booleanValue());

		return REDIRECT_PREFIX + "/cart";
	}

	/**
	 * This method is for the cart page. When the customer select the conflict deal, this method will be call and then
	 * redirect to the cart page, to find if there still have conflict deals.
	 *
	 */
	@PostMapping("/selectDealCart")
	public String addManualConflictFreeDealToCart(@RequestBody final SABMAddToCartForm form)
	{
		LOG.debug("Requested select conflict deal : {} ", form.getDealCode());

		cartFacade.addApplyDealToCart(form.getDealCode(), DealConditionStatus.MANUAL_CONFLICT);

		return REDIRECT_PREFIX + "/cart";
	}

	/**
	 * Method for update DeliveryMode of cart add by SAB-535
	 *
	 * @param delmodeCode
	 * @return String
	 */
	protected boolean updateSABMDeliveryMode(final String delmodeCode)
	{
		if (delmodeCode.equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, "")))
		{
			cartFacade.saveDefaultShippingCarriers();
		}

		return getCheckoutFacade().setDeliveryMode(delmodeCode);
	}

	/**
	 * Method for update DeliveryMode,ShippingCarriers or DeliveryAddresses of cart add by SAB-535
	 *
	 * @param model
	 * @param cartData
	 */
	@SuppressWarnings("boxing")
	protected void setSABMDelivery(final Model model, final CartData cartData)
	{
		final CustomerData userData = getUser();
		B2BUnitData b2bUnitdata = null;
		if (userData.getUnit() != null)
		{
			//get B2bUnit of User
				b2bUnitdata = b2bCommerceUnitFacade.getB2bUnitData(userData.getUnit().getUid());

			if (CollectionUtils.isNotEmpty(b2bUnitdata.getShippingCarriers()))
			{
				final List<ShippingCarrier> shippingCarriers = new ArrayList<>();
				shippingCarriers.addAll(b2bUnitdata.getShippingCarriers());
				//SAB-1530 ordered on b2bunit shipping carrier
				Collections.sort(shippingCarriers, shippingCarrierComparator);
				model.addAttribute("shippingCarriers", shippingCarriers);
			}
		}
		//update DeliveryAddresses of cart
		setSABMDeliveryAddress(model, cartData, b2bUnitdata);
		//update DeliveryMode of cart
		setSABMDeliveryMode(model, cartData);

		model.addAttribute("cartData", cartData);
	}


	protected void updateDefaultDeliveryDetails()
	{
		final CartData cartData = cartFacade.getSessionCartWithEntryOrdering(false);

		if (cartData.getDeliveryMode() == null)
		{
			getCheckoutFacade().setDeliveryMode(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, ""));
			cartFacade.saveDefaultShippingCarriers();
		} else {
			cartFacade.validateShippingCarrier();
		}
		if (null == cartData.getDeliveryAddress())
		{
			setDefaultDeliveryAddress();
		}
	}

	/**
	 * Sets the default delivery address.
	 */
	private void setDefaultDeliveryAddress()
	{
		final List<AddressData> deliveryAddresses = (List<AddressData>) getCheckoutFacade().getSupportedDeliveryAddresses(true);

		if (CollectionUtils.isNotEmpty(deliveryAddresses))
		{
			AddressData defaultDeliveryAddress = deliveryAddresses.get(0);
			for (final Iterator<AddressData> addressIter = deliveryAddresses.iterator(); addressIter.hasNext();)
			{
				B2BUnitData b2bUnitdata = b2bCommerceUnitFacade.getB2bUnitData(getUser().getUnit().getUid());
				final AddressData address = addressIter.next();
				if (StringUtils.equalsIgnoreCase(b2bUnitdata.getDefaultShipTo(), address.getPartnerNumber()))
				{
					defaultDeliveryAddress = address;
				}
			}
			//Save the default address to cart
			getCheckoutFacade().setDeliveryAddress(defaultDeliveryAddress);
		}
	}

	/**
	 * Method for update DeliveryAddresses of cart add by SAB-535
	 *
	 * @param model
	 * @param cartData
	 * @param b2bUnitdata
	 */
	protected void setSABMDeliveryAddress(final Model model, final CartData cartData, final B2BUnitData b2bUnitdata)
	{
		final List<AddressData> deliveryAddresses = b2bUnitdata.getAddresses();
		//Judging  whether there are other addresses
		//Removed null != cartData.getDeliveryAddress() && null != getAddressOnList(deliveryAddresses, cartData.getDeliveryAddress().getId()) as per Incident:INC0217545 Fix.
		if (null != deliveryAddresses)
		{
			setSABMDeliveryAddressesModel(model, deliveryAddresses, b2bUnitdata);
		}

	}

	/**
	 * Method for update DeliveryMode of cart add by SAB-535
	 *
	 * @param model
	 * @param cartData
	 */
	@SuppressWarnings("boxing")
	protected void setSABMDeliveryMode(final Model model, final CartData cartData)
	{

		if (cartData.getDeliveryMode().getCode()
				.equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, "")))
		{
			model.addAttribute("cubArrangedFlag", true);
			model.addAttribute("customerArrangedFlag", false);
		}
		else if (cartData.getDeliveryMode().getCode()
				.equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, "")))
		{
			model.addAttribute("cubArrangedFlag", false);
			model.addAttribute("customerArrangedFlag", true);
			if (null != cartData.getDeliveryShippingCarrier()){
				model.addAttribute("selectDeliveryShippingCarrier", cartData.getDeliveryShippingCarrier().getDescription());
			} else {
				// Fixed as per RITM0555025
				model.addAttribute("selectdefaultShippingCarrierErrorMsg", "basket.page.selectdefaultShippingCarrier.info.message");
				LOG.error("Shipping carrier is null for B2bUnit!");
			}
		}
		setSABMDeliveryModeData(model);
	}

	@SuppressWarnings("boxing")
	protected void setSABMDeliveryModeData(final Model model)
	{
		final List<? extends DeliveryModeData> listZoneDeliveryModeData = getCheckoutFacade().getSupportedDeliveryModes();
		if (CollectionUtils.isNotEmpty(listZoneDeliveryModeData))
		{
			for (final DeliveryModeData del : listZoneDeliveryModeData)
			{
				if (del.getCode().equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, "")))
				{
					model.addAttribute("cubArranged", del);
				}
				if (del.getCode().equals(getSiteConfigService().getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, "")))

				{
					model.addAttribute("customerArranged", del);
				}
			}
		}
	}

	/**
	 * Method for set Model of cart add by SAB-535
	 *
	 * @param model
	 * @param anotherDeliveryAddresses
	 */
	@SuppressWarnings("boxing")
	protected void setSABMDeliveryAddressesModel(final Model model, final List<AddressData> anotherDeliveryAddresses,
			final B2BUnitData b2bUnitdata)
	{
		if (CollectionUtils.isNotEmpty(anotherDeliveryAddresses) && anotherDeliveryAddresses.size() > 1)
		{
			model.addAttribute("anotherDeliveryAddresses", getSelectAnotherDeliveryAddresses(anotherDeliveryAddresses, b2bUnitdata));
			model.addAttribute("ishasanother", true);
		}
		else
		{
			model.addAttribute("anotherDeliveryAddresses", null);
			model.addAttribute("ishasanother", false);
		}

	}


	/**
	 * Method for get SelectAnotherDeliveryAddresses from deliveryAddresses add by SAB-535
	 *
	 * @param customerDefaddress
	 * @return List<? extends AddressData>
	 */
	protected List<AddressData> getSelectAnotherDeliveryAddresses(final List<AddressData> deliveryAddresses,
			final B2BUnitData b2bUnitdata)
	{
		final List<AddressData> newdeliveryAddresses = deliveryAddresses;
		try
		{
			AddressData defaultAddress = cartFacade.getDeliveryDefaultAddress(b2bUnitdata.getUid());
			if (null == defaultAddress && CollectionUtils.isNotEmpty(b2bUnitdata.getAddresses()))
			{
				AddressData defaultShipTo = cartFacade.getDefaultShipTo(b2bUnitdata.getUid());
				if(null != defaultShipTo){
					defaultAddress = defaultShipTo;
				}else{
					defaultAddress = b2bUnitdata.getAddresses().get(0);
				}				
			}
			if (null != defaultAddress)
			{
				final AddressData address = isAddressOnUnitList(deliveryAddresses, defaultAddress);
				if (null != address)
				{
					address.setDefaultB2BunitAddress(Boolean.TRUE);
					newdeliveryAddresses.add(0, address);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("singletonList error", e);
		}

		return newdeliveryAddresses == null ? Collections.<AddressData> emptyList() : newdeliveryAddresses;
	}

	/**
	 * Method for To judge whether there is fund AddressData by deliveryAddressID add by SAB-535
	 *
	 * @param deliveryAddresses
	 * @param selectedAddressData
	 * @return boolean
	 */
	protected boolean isAddressOnList(final List<AddressData> deliveryAddresses, final AddressData selectedAddressData)
	{
		if (deliveryAddresses == null || selectedAddressData == null)
		{
			LOG.warn("One or more attributes (deliveryAddresses, deliveryAddressID) are null ");
			return false;
		}

		for (final AddressData address : deliveryAddresses)
		{
			if (address.getId().equals(selectedAddressData.getId()))
			{
				deliveryAddresses.remove(address);
				return true;
			}
		}
		LOG.warn("Unable to fund the  AddressData ");
		return false;
	}

	/**
	 * Method for To judge whether there is fund AddressData by defaultShipTo
	 *
	 * @param deliveryAddresses
	 * @return AddressData
	 */
	protected AddressData isAddressOnUnitList(final List<AddressData> deliveryAddresses, final AddressData defaultAddress)
	{
		for (final AddressData address : deliveryAddresses)
		{
			if (address.getId().equals(defaultAddress.getId()))
			{
				deliveryAddresses.remove(address);
				return address;
			}
		}
		LOG.warn("Unable to fund the  AddressData ");
		return null;
	}

	/**
	 * Method for get AddressData by deliveryAddressID or userdata add by SAB-535
	 *
	 * @param deliveryAddressID
	 * @return AddressData
	 */
	protected AddressData getSelectAddressData(final String deliveryAddressID)
	{
		if (deliveryAddressID.isEmpty())
		{
			LOG.warn("Attribute deliveryAddressID is null ");
			return null;
		}
		final List<? extends AddressData> deliveryAddresses = getCheckoutFacade().getSupportedDeliveryAddresses(true);
		if (CollectionUtils.isNotEmpty(deliveryAddresses))
		{
			return getAddressOnList(deliveryAddresses, deliveryAddressID);
		}
		LOG.warn("Unable to fund the right AddressData ");
		return null;
	}

	/**
	 * Method for get Address of List<AddressData> by deliveryAddressID add by SAB-535
	 *
	 * @param deliveryAddresses
	 * @param deliveryAddressID
	 * @return AddressData
	 */
	protected AddressData getAddressOnList(final List<? extends AddressData> deliveryAddresses, final String deliveryAddressID)
	{
		if (CollectionUtils.isEmpty(deliveryAddresses) || deliveryAddressID.isEmpty())
		{
			LOG.warn("One or more attributes (deliveryAddresses, deliveryAddressID) are null ");
			return null;
		}
		for (final AddressData address : deliveryAddresses)
		{
			if (address.getId().equals(deliveryAddressID))
			{
				return address;
			}
		}
		LOG.warn("Unable to fund the right AddressData ");
		return null;
	}

	@PostMapping("/isLostDeal")
	@ResponseBody
	public LostdealJson isLostDeal(@RequestParam(value = "entryNumber", required = true) final String entryNumber,
			@RequestParam(value = "quantity", required = true) final int quantity,
			@RequestParam(value = "uom", required = true) final String uom)
	{
		final LostdealJson lostDealJson = sabmDealsSearchFacade.isLostDeal(entryNumber, quantity, uom);

		if (!lostDealJson.isIsLost())
		{
			final SABMUpdateQuantityForm form = new SABMUpdateQuantityForm();
			LOG.info("Cart Error isLostDeal entryNumber : {} ", entryNumber);
			form.setEntryNumber(Long.valueOf(entryNumber));
			form.setQuantity(Long.valueOf(quantity));
			form.setUnit(uom);

			lostDealJson.setNewQty(ajaxUpdateCartQuantities(form));
		}

		return lostDealJson;
	}	
	
	@ModelAttribute("requestOrigin")
	protected String populateRequestOrigin(HttpServletRequest request) {
		return SabmUtils.getRequestOrigin(request.getHeader(SabmUtils.REFERER_KEY), SabmUtils.HOME);
	}
	
}
