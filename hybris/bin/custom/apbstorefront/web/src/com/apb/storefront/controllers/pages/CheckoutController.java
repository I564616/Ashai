/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorfacades.flow.impl.SessionOverrideCheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestRegisterForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.GuestRegisterValidator;
import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.apb.facades.checkout.APBCheckoutFacade;
import com.apb.storefront.controllers.ControllerConstants;


/**
 * CheckoutController
 */
@Controller
@RequestMapping(value = "/checkout")
public class CheckoutController extends AbstractCheckoutController
{
	private static final Logger LOG = LoggerFactory.getLogger(CheckoutController.class);

	/** The Constant CRON_JOB_OPTION. */
	private static final String CRON_JOB_OPTION = "cron.job.option.apb";

	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";

	private static final String CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL = "orderConfirmation";
	private static final String CONTINUE_URL_KEY = "continueUrl";
	private static final String ADD_SURCHARGE = "isAddSurcharge";

	@Resource(name = "productFacade")
	private ProductFacade productFacade;

	/** The asahi order facade. */
	@Resource(name = "orderFacade")
	private SABMOrderFacade sabmOrderFacade;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "guestRegisterValidator")
	private GuestRegisterValidator guestRegisterValidator;

	@Resource(name = "autoLoginStrategy")
	private AutoLoginStrategy autoLoginStrategy;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
    SABMCartFacade sabmCartFacade;

	@Resource(name = "apbCheckoutFacade")
	private APBCheckoutFacade apbCheckoutFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private OrderFacade orderFacade;

	@Resource(name = "defaultModelService")
	private ModelService modelService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@ExceptionHandler(ModelNotFoundException.class)
	public String handleModelNotFoundException(final ModelNotFoundException exception, final HttpServletRequest request)
	{
		request.setAttribute("message", exception.getMessage());
		return FORWARD_PREFIX + "/404";
	}

	@GetMapping
	public String checkout(final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (getCheckoutFlowFacade().hasValidCart())
		{
			if (validateCart(redirectModel))
			{
				return REDIRECT_PREFIX + "/cart";
			}
			else
			{
				checkoutFacade.prepareCartForCheckout();
				return getCheckoutRedirectUrl();
			}
		}

		LOG.info("Missing, empty or unsupported cart");

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			redirectModel.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}

		// No session cart or empty session cart. Bounce back to the cart page.
		return REDIRECT_PREFIX + "/cart";
	}

	@GetMapping("/orderConfirmation/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
	@RequireHardLogIn
	public String orderConfirmation(@PathVariable("orderCode") final String orderCode, final HttpServletRequest request,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		SessionOverrideCheckoutFlowFacade.resetSessionOverrides();
		return processOrderCode(orderCode, model, request, redirectModel);
	}


	@PostMapping("/orderConfirmation/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
	public String orderConfirmation(final GuestRegisterForm form, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		getGuestRegisterValidator().validate(form, bindingResult);
		return processRegisterGuestUserRequest(form, bindingResult, model, request, response, redirectModel);
	}

	protected String processRegisterGuestUserRequest(final GuestRegisterForm form, final BindingResult bindingResult,
			final Model model, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return processOrderCode(form.getOrderCode(), model, request, redirectModel);
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

	protected String processOrderCode(final String orderCode, final Model model, final HttpServletRequest request,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
	    try {
            OrderData orderDetails;
            OrderModel orderModel = null;

            try {
                if (asahiSiteUtil.isApb() && this.asahiConfigurationService.getBoolean(CRON_JOB_OPTION, false)) {
                    orderDetails = this.orderFacade.getOrderDetailsForCode(orderCode);
                } else {
                    orderModel = this.sabmOrderFacade.getOrderDetails(orderCode);
                    orderDetails = this.sabmOrderFacade.getAsahiOrderDetailsForCode(orderModel);

                }
            } catch (final UnknownIdentifierException e) {
                LOG.warn("Attempted to load an order confirmation that does not exist or is not visible. Redirect to home page.");
                return REDIRECT_PREFIX + ROOT;
            }

            if (orderDetails.isGuestCustomer() && !StringUtils.substringBefore(orderDetails.getUser().getUid(), "|")
                    .equals(getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID))) {
                return getCheckoutRedirectUrl();
            }

            if (orderDetails.getEntries() != null && !orderDetails.getEntries().isEmpty()) {
                for (final OrderEntryData entry : orderDetails.getEntries()) {
                    final String productCode = entry.getProduct().getCode();
                    final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
                            Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES));
                    entry.setProduct(product);
                }
            }
            model.addAttribute("orderCode", orderCode);
            model.addAttribute("orderData", orderDetails);
            model.addAttribute("allItems", orderDetails.getEntries());
            model.addAttribute("deliveryAddress", orderDetails.getDeliveryAddress());
            model.addAttribute("deliveryMode", orderDetails.getDeliveryMode());
            model.addAttribute("paymentInfo", orderDetails.getPaymentInfo());
            model.addAttribute("pageType", PageType.ORDERCONFIRMATION.name());
            model.addAttribute(ADD_SURCHARGE, sabmCartFacade.isAddSurcharge());
            if (asahiSiteUtil.isSga()) {
                model.addAttribute("deliveryDateInvalid", apbCheckoutFacade
                        .isDeliveryDateInValid(orderDetails.getDeliveryAddress().getRecordId(), orderDetails.getDeliveryRequestDate()));
                
                model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUser());
    			
                final boolean isApprovalPending = asahiCoreUtil.isSAMAccessApprovalPending();
    			final String accessType = asahiCoreUtil.getCurrentUserAccessType();
    			model.addAttribute("isApprovalPending",isApprovalPending);
    			model.addAttribute("isAccessDenied",asahiCoreUtil.isSAMAccessDenied());
    			
    			model.addAttribute("sgaAccessType",accessType);
    			if(isApprovalPending)
				{
					model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
							&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
							asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
				}
            }

		/*
		 * final List<CouponData> giftCoupons = orderDetails.getAppliedOrderPromotions().stream() .filter(x ->
		 * CollectionUtils.isNotEmpty(x.getGiveAwayCouponCodes())).flatMap(p -> p.getGiveAwayCouponCodes().stream())
		 * .collect(Collectors.toList()); model.addAttribute("giftCoupons", giftCoupons);
		 */

            processEmailAddress(model, orderDetails);

            final String continueUrl = (String) getSessionService().getAttribute(WebConstants.CONTINUE_URL);
            model.addAttribute(CONTINUE_URL_KEY, (continueUrl != null && !continueUrl.isEmpty()) ? continueUrl : ROOT);

            final AbstractPageModel cmsPage = getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL);
            storeCmsPageInModel(model, cmsPage);
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL));

            if ((getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL)).getBackgroundImage() != null) {
                model.addAttribute("media",
                        (getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL)).getBackgroundImage().getURL());
            }


            model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

            if (null != orderModel) {
                try {
                    LOG.info("Order is going to be sent to backend :: Order - " + orderModel.getCode());
                    LOG.info(String.format("Status of order %s was %s", orderModel.getCode(), orderModel.getStatus()));
                    this.sabmOrderFacade.sendOrderToBackendSystem(orderModel);
                    LOG.info(String.format("Order %s sent to backend successfully and new status is %s", orderModel.getCode(), orderModel.getStatus()));
                } catch (Exception e) {
                    LOG.info("Order not sent to backend :: error - " + e.getMessage());
                }
            }

            if (ResponsiveUtils.isResponsive()) {
                return getViewForPage(model);
            }

            return ControllerConstants.Views.Pages.Checkout.CheckoutConfirmationPage;
        }catch (Exception e){
	        LOG.error("Error at confirmation page - ", e);
            return ControllerConstants.Views.Pages.Checkout.CheckoutConfirmationPage;
        }
	}

	protected void processEmailAddress(final Model model, final OrderData orderDetails)
	{
		final String uid;

		if (orderDetails.isGuestCustomer() && !model.containsAttribute("guestRegisterForm"))
		{
			final GuestRegisterForm guestRegisterForm = new GuestRegisterForm();
			guestRegisterForm.setOrderCode(orderDetails.getGuid());
			uid = orderDetails.getPaymentInfo().getBillingAddress().getEmail();
			guestRegisterForm.setUid(uid);
			model.addAttribute(guestRegisterForm);
		}
		else
		{
			uid = getCustomerFacade().getUserForUID(orderDetails.getUser().getUid()).getDisplayUid();
		}
		model.addAttribute("email", uid);
		if(BooleanUtils.isTrue(orderDetails.getBdeOrder())) {
			//resetting the customer email if it is BDE flow.
			BDECustomerModel bdeCustomer = (BDECustomerModel) userService.getCurrentUser();
			orderDetails.getB2bCustomerData().setUid(bdeCustomer.getEmail());
		}
	}

	protected GuestRegisterValidator getGuestRegisterValidator()
	{
		return guestRegisterValidator;
	}

	protected AutoLoginStrategy getAutoLoginStrategy()
	{
		return autoLoginStrategy;
	}

}
