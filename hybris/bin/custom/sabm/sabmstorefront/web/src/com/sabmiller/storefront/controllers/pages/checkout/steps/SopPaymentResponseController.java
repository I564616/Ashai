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
package com.sabmiller.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.order.InvalidCartException;

import java.util.Date;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMInvoiceFacade;
import com.sabmiller.facades.order.SABMCheckoutFacade;
import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.facade.impl.SABMMerchantSuitePaymentFacadeImpl;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteWebHookPaymentAlreadyDone;

import com.sabmiller.facades.order.SABMOrderFacade;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.Calendar;
import de.hybris.platform.util.Config;


@Controller
@RequestMapping(value = "/checkout/sop")
public class SopPaymentResponseController extends PaymentMethodCheckoutStepController
{
	private static final Logger LOG = LoggerFactory.getLogger(SopPaymentResponseController.class.getName());

	private static final String CHECKOUT_WAIT_CMS_PAGE = "PaymentWaitingPage";
	protected static final String REDIRECT_URL_ORDER_CONFIRMATION = "/checkout/orderConfirmation/";
	private static final String CHECKOUT_WebHook_Payement_Done_WAIT_CMS_PAGE = "WebHookPaymentDoneWaitingPage";

	private static final String REDIRECT_URL_CART =  "/cart?";

	@Resource(name = "sabmCheckoutFacade")
	private SABMCheckoutFacade checkoutFacade;

	@Resource(name = "errorEventFacade")
	private ErrorEventFacade errorEventFacade;

	@Resource(name = "sabmInvoiceFacade")
	protected SABMInvoiceFacade invoiceFacade;
	 
	@Resource(name = "orderFacade")
	private SABMOrderFacade orderFacade;
	
	@Resource(name = "sessionService")
	 private SessionService sessionService;
	 
	@Resource
	private SABMMerchantSuitePaymentFacadeImpl sabmMerchantSuitePaymentFacade;



	/**
	 * Endpoint to check if cart payment has being approved. This should only happen after westpac postback call
	 *
	 * @return true if approved
	 */
	@ResponseBody
	@GetMapping("/processingJson/{cartCode}")
	public String doHandleSopResponseJson(@PathVariable("cartCode") final String cartCode, final HttpServletRequest request)
	{

		try
		{
			//checks if postback arrived with confirmation, place the order if so
			final OrderData order = placeOrderIfReady();
			if (order != null)
			{
				return REDIRECT_URL_ORDER_CONFIRMATION + order.getCode();

			}
			else if (checkoutFacade.hasExceededWaitTimeout())
			{
				return "/checkout?paymentWaitError=" + cartCode;
			}

		}
		catch (final Exception e)
		{
			final String errorCode = errorEventFacade.createErrorEntry(e, "merchantSuite", null, ErrorEventType.HYBRIS,
					"Payment captured but order was not placed");
			return "/checkout?orderError=" + errorCode;
		}
		return null;
	}

	protected OrderData placeOrderIfReady() throws InvalidCartException
	{
		if (sabmMerchantSuitePaymentFacade.isCartPaymentApproved())
		{
			return checkoutFacade.placeOrder();
		}
		return null;
	}

	@GetMapping("/processing/{cartCode}")
	public String doHandleSopResponse(@PathVariable(value = "cartCode") final String cartCode, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{

		model.addAttribute("cartCode", cartCode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(CHECKOUT_WAIT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKOUT_WAIT_CMS_PAGE));

		return ControllerConstants.Views.Pages.Checkout.CheckoutPaymentWaitPage;
	}

	/**
	 * This is the redirect request returned for checkout payments which will approve the payment
	 */
	@GetMapping("/checkoutPaymentResponse")
	public String response(@RequestParam(value = "ResultKey", required = true)final String resultKey,
			@RequestParam(value = "ResponseCode", required = true)final String responseCode,
			@RequestParam(value = "ResponseText", required = true)final String responseText
	) throws CMSItemNotFoundException{


		if (getCartFacade().hasSessionCart()) {
			final String cartCode = ((SABMCartFacade) getCartFacade()).getCartCode();
			LOG.info("Merchant Suite redirect received for cart [{}]", cartCode);
			try {
				sabmMerchantSuitePaymentFacade.processCheckoutAuthKeyCCTxn(resultKey);
				return REDIRECT_PREFIX + "/checkout/sop/processing/" + cartCode;
			}
			catch (SABMMerchantSuitePaymentErrorException e) {
				LOG.error("Error processing Checkout Payment: Payment Declined :" + e.getErrorType() , e);
				return returnRedirectURLForException(e.getErrorType());
			}
			catch (SABMMerchantSuiteConfigurationException |  SABMMerchantSuiteAPIRequestInvalidException | InvalidCartException e) {
				LOG.error("Error processing Checkout Payment", e);
				return returnRedirectURLForException("paymentError");
			}
			catch (SABMMerchantSuiteWebHookPaymentAlreadyDone e){
				LOG.warn("##### Payment Done by Webhook Method ########");
				return REDIRECT_PREFIX + "/checkout/sop/postbackprocessing/" + cartCode;		
			}
		}
		return REDIRECT_PREFIX + "/cart";
	}
	
	@GetMapping("/postbackprocessing/{cartCode}")
	public String doHandleSopPostbackResponse(@PathVariable(value = "cartCode") final String cartCode, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{
		LOG.info("Entering into doHandleSopPostbackResponse()");
		model.addAttribute("cartCode", cartCode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(CHECKOUT_WebHook_Payement_Done_WAIT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKOUT_WebHook_Payement_Done_WAIT_CMS_PAGE));

		return ControllerConstants.Views.Pages.Checkout.CheckoutPaymentWaitPage;
	}
	
	@ResponseBody
	@GetMapping("/processingPostbackResult/{cartCode}")
	public String doHandleSopPostbackResponseResult(@PathVariable("cartCode") final String cartCode, final HttpServletRequest request)
	{
		
		LOG.info("Entering into doHandleSopPostbackResponseResult()");
		//checks if postback arrived with confirmation, place the order if so	
		
		OrderModel orderModel = orderFacade.getOrderByCartCode(cartCode);
		
		if (orderModel != null)
		{
			sessionService.removeAttribute(SabmCoreConstants.SESSION_CART_PROCESSING_TIME + cartCode);
			return REDIRECT_URL_ORDER_CONFIRMATION + orderModel.getCode();
		}
		else if (hasExceededPostbackWaitTimeout(cartCode))
		{
			sessionService.removeAttribute(SabmCoreConstants.SESSION_CART_PROCESSING_TIME + cartCode);
			Exception e = new InvalidCartException("Postback Order creation failed");
			final String errorCode = errorEventFacade.createErrorEntry(e, "merchantSuite", null, ErrorEventType.HYBRIS,
					"Payment captured but order was not placed");
			return "/checkout?orderError=" + errorCode;
		}
		
		return null;
	}

	private String returnRedirectURLForException(String errorType)
	{
		if (checkoutFacade.isCheckoutCountdownValid())
		{
			return REDIRECT_PREFIX + "/checkout?"+errorType+"=true";
		}
		return REDIRECT_PREFIX + "/cart?"+errorType;
	}

	@ModelAttribute("pageType")
	protected String getPageType()
	{
		return SABMWebConstants.PageType.SOP_PAYMENT_RESPONSE.name();
	}
	
	private boolean hasExceededPostbackWaitTimeout(String cartCode){
		Date cartProcessingTime = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_CART_PROCESSING_TIME + cartCode);
		
		if(cartProcessingTime != null){
			
			final Calendar cal = Calendar.getInstance();
			final Date now = cal.getTime();
			cal.setTime(cartProcessingTime);
			cal.add(Calendar.MILLISECOND, Config.getInt("merchantsuite.postback.payment.wait.timeout", 180000));

			return now.after(cal.getTime());
		}
		return false;
		
	}

}
