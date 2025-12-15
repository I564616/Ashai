package com.apb.storefront.controllers.pages;


import com.apb.core.util.AsahiAdhocCoreUtil;
import com.sabmiller.core.model.BDECustomerModel;
import com.apb.core.util.AsahiCoreUtil;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.exception.AsahiPaymentException;
import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.card.payment.AsahiCreditCardTypeEnum;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.checkout.APBCheckoutFacade;
import com.apb.facades.credit.check.ApbCreditCheckFacade;
import com.apb.facades.delivery.data.DeliveryInfoData;
import com.apb.facades.kegreturn.data.KegSizeData;
import com.apb.facades.user.ApbUserFacade;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.checkout.form.AsahiPaymentDetailsForm;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;
import com.apb.storefront.checkout.form.CustomerCheckoutForm;
import com.apb.storefront.checkout.steps.validation.impl.CustomerCheckoutFormValidator;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.data.LoginValidateInclusionData;
import com.apb.storefront.forms.ApbKegReturnKegSizForm;
import com.apb.storefront.util.ApbKegReturnUtil;
import com.apb.storefront.util.AsahiPaymentIframeUrlUtil;
import org.springframework.web.util.HtmlUtils;


@Controller
@RequestMapping(value = "/checkout/single")
public class ApbCheckoutController extends AbstractCheckoutStepController
{
	protected static final String CHECKOUT_CMS_PAGE_LABEL = "checkoutPage";
	private static final String SINGLE = "singleStepCheckout";
	private static final String DELIVERY_INSTRUCTION_CONFIGURED_LENGTH = "checkout.deliveryinstruction.textarea.maxlength";
	private static final String CART_DATA = "cartData";
	private static final String CARD = "CARD";
	private static final String ACCOUNT = "ACCOUNT";
	private static final String ADDITIONAL_CENT_TO_FATZEBRA = "additional.cent.in.ordertotal";

	private static final Logger LOG = LoggerFactory.getLogger(ApbCheckoutController.class);

	@Resource(name = "addressDataUtil")
	private AddressDataUtil addressDataUtil;

	@Resource(name = "b2bCheckoutFacade")
	private CheckoutFacade b2bCheckoutFacade;

	@Resource(name = "customerCheckoutFormValidator")
	private CustomerCheckoutFormValidator customerCheckoutFormValidator;

	@Resource(name = "apbCheckoutFacade")
	private APBCheckoutFacade apbCheckoutFacade;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	UserService userService;

	@Resource(name = "apbCreditCheckFacade")
	ApbCreditCheckFacade apbCreditCheckFacade;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "apbKegReturnUtil")
	private ApbKegReturnUtil apbKegReturnUtil;

	@Resource(name = "asahiPaymentIframeUrlUtil")
	AsahiPaymentIframeUrlUtil asahiPaymentIframeUrlUtil;

	@Resource(name = "sabmCheckoutFacade")
	AcceleratorCheckoutFacade acceleratorCheckoutFacade;

	@Resource(name = "addressConverter")
	Converter<AddressModel, AddressData> apbB2bAddressConverter;

	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private SABMCartFacade sabmCartFacade;
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;
	
	@Resource
	private CartService cartService;
	
	@Resource
	private EnumerationService enumerationService;
	
	@Resource
	private ModelService modelService;
	
	@Resource
	private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

    @Resource
    private AsahiCoreUtil asahiCoreUtil;

    @Resource
    private AsahiAdhocCoreUtil adhocCoreUtil;
    
    @Resource(name = "configurationService")
    private ConfigurationService configurationService; 

	private static final String VISA_TYPE_LIST = "payment.integration.visa.card.list.";
	private static final String MASTER_TYPE_LIST = "payment.integration.master.card.list.";
	private static final String AMEX_TYPE_LIST = "payment.integration.amex.card.list.";
	private static final String MAX_CREDIT_CARDS_ALLOWED = "max.saved.cards.allowed.";
	public static final String CREDIT_SURCHARGE_FOR_AMEX = "credit.surcharge.for.amex.card.";
	public static final String CREDIT_SURCHARGE_FOR_VISA = "credit.surcharge.for.visa.card.";
	public static final String CREDIT_SURCHARGE_FOR_MASTER = "credit.surcharge.for.master.card.";
	public static final String VIEW_ALL_PRODUCTS_QUANTITY = "checkout.cart.summary.view.all.quantity";

	private static final String ADD_SURCHARGE = "isAddSurcharge";
	public static final String PAYMENTMODE_DELIVERY = "DELIVERY";

	@Override
	@GetMapping
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = SINGLE)
	@CacheControl(directive = {CacheControlDirective.NO_CACHE, CacheControlDirective.NO_STORE, CacheControlDirective.PRIVATE, CacheControlDirective.MUST_REVALIDATE}, maxAge = 0)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		final boolean isAddressAvailable = apbCheckoutFacade.setDeliveryAddressIfAvailable();
		final CustomerCheckoutForm customerCheckoutForm;
		if (!model.containsAttribute("customerCheckoutForm"))
		{
			customerCheckoutForm = new CustomerCheckoutForm();
		}
		else
		{
			customerCheckoutForm = (CustomerCheckoutForm) model.asMap().get("customerCheckoutForm");
		}
		customerCheckoutForm.setAsahiCreditCardType(AsahiCreditCardTypeEnum.VISA);

		final CartData cartData = populateCommonModelAttributes(model, customerCheckoutForm);
		if (!cartData.getPriceUpdated())
		{
			GlobalMessages.addErrorMessage(model, asahiSiteUtil.isApb()? "apb.price.not.fetched.services.msg":"price.not.fetched.services.msg");
			model.addAttribute("priceError", true);
		}

		if (asahiSiteUtil.isSga())
		{
			
			if (asahiCoreUtil.isNAPUser()) {
				return FORWARD_PREFIX + "/404";
			}
			
			model.addAttribute("isExcluded", apbCheckoutFacade.isAnyProdExcl(cartData));
			if (null != cartData.getShowExclusionError() && cartData.getShowExclusionError().booleanValue())
			{
				GlobalMessages.addInfoMessage(model, "sga.checkout.exclusion.error.message");
			}
			
			if (Boolean.valueOf(asahiConfigurationService.getString("sga.product.status.available", "false")) &&
					BooleanUtils.isTrue(cartData.getOutofStockItemAvailable()))
			{
				GlobalMessages.addErrorMessage(model, "sga.checkout.outofstock.error.message");
			}
			model.addAttribute("isBDEFlow",false);
			if(asahiSiteUtil.isBDECustomer()) {
				BDECustomerModel bdeCustomer = (BDECustomerModel) userService.getCurrentUser();
				Set<String> customerEmailIds = apbCheckoutFacade.getCustomerEmailIds();
				model.addAttribute("isBDEFlow",true);
				model.addAttribute("customerEmailIds",customerEmailIds);
				model.addAttribute("bdeUserEmailId",bdeCustomer !=null? bdeCustomer.getEmail():"");
				model.addAttribute("bdeOrderDetailsText", cartData.getBdeOrderEmailText()!=null?cartData.getBdeOrderEmailText():"");
				model.addAttribute("selectedCustomerEmailIds",cartData.getBdeOrderCustomerEmails());
				model.addAttribute("hasBonusStockProductOnly", sabmCartFacade.isBonusStockProductsInCart());
			}
			//model.addAttribute("isSGACreditCardEnable", this.asahiConfigurationService.getString("credit.card.enable.sga", "false"));
		}
		if (!asahiSiteUtil.isSga())
		{
			if (cartData.getLicenseRequired() != null && cartData.getLicenseRequired())
			{
				if (checkSuperUser())
				{
					GlobalMessages.addErrorMessage(model, "cart.liquore.license.required.super.user.msg");
				}
				else
				{
					GlobalMessages.addErrorMessage(model, "cart.liquore.license.required.msg");
				}

			}

			final boolean isBlockedCredit = apbCreditCheckFacade.getCreditCheck(apbB2BUnitService.getCurrentB2BUnit(),
					cartData.getTotalPriceWithTax().getValue().doubleValue());

			if (isBlockedCredit)
			{
				final List<CCPaymentInfoData> cardList = apbUserFacade.getCCPaymentInfos(true);
				String cardType = AsahiCreditCardTypeEnum.VISA.toString();
				if (CollectionUtils.isNotEmpty(cardList))
				{
					cardType = cardList.get(0).getCardType();
				}
				final CartData cartDataWithSurcharge = apbCheckoutFacade.updateTotalwithCreditSurcharge(cardType, "CREDITCARD");
				apbCheckoutFacade.updateStockEntry(cartDataWithSurcharge);

				model.addAttribute(CART_DATA, cartDataWithSurcharge);

				GlobalMessages.addErrorMessage(model, "checkout.error.credit.block");
			}
			
		}
		if (!isAddressAvailable)
		{
			GlobalMessages.addErrorMessage(model, "checkout.multi.no.delivery.address");
		}
		model.addAttribute("inputCommentsMaxSize", asahiConfigurationService.getString(
				ApbStoreFrontContants.KEGRETURN_COMMENTS_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "200"));

		if (asahiSiteUtil.isApb() && assistedServiceFacade.isAssistedServiceModeLaunched())
		{
			if (null != assistedServiceFacade.getAsmSession().getAgent()
					&& assistedServiceFacade.getAsmSession().getAgent().getAllGroups().stream().anyMatch(groupItem -> groupItem.getUid().equals(ApbStoreFrontContants.ASAHISALESREP))) {
				model.addAttribute("asmMode", true);
				model.addAttribute("hasBonusStockProductOnly", sabmCartFacade.isBonusStockProductsInCart());
			} else {
				model.addAttribute("asmMode", false);
			}
		}
		else
		{
			model.addAttribute("asmMode", false);
		}

		model.addAttribute("restrictedPattern",  adhocCoreUtil.getConfigValue("apb.delivery.instruction.restricted.pattern"));
		setAdditionalInfoForSga(model);
		return ControllerConstants.Views.Pages.SingleStepCheckout.AddDeliveryDataPage;
	}

	@PostMapping
	@RequireHardLogIn
	public String getCheckoutDetails(final CustomerCheckoutForm customerCheckoutForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request)
			throws CMSItemNotFoundException, AsahiPaymentException
	{
		UserModel currentUser = this.userService.getCurrentUser();
		LOG.info("single checkout user {} start:", currentUser);
		if (validateCart(redirectModel))
		{
			return REDIRECT_PREFIX + "/cart";
		}

		getCustomerCheckoutFormValidator().validate(customerCheckoutForm, bindingResult);
		CartData cartData = apbCheckoutFacade.getCheckoutCart();

		boolean isBlockedCredit = Boolean.FALSE;
		if (!asahiSiteUtil.isSga())
		{
			isBlockedCredit = apbCreditCheckFacade.getCreditCheck(apbB2BUnitService.getCurrentB2BUnit(),
					cartData.getTotalPriceWithTax().getValue().doubleValue());
		}else{
			//model.addAttribute("isSGACreditCardEnable", this.asahiConfigurationService.getString("credit.card.enable.sga", "false"));
		}

		if (isBlockedCredit && customerCheckoutForm.getPaymentMethod().equalsIgnoreCase(ACCOUNT))
		{
			populateCommonModelAttributes(model, customerCheckoutForm);
			apbCheckoutFacade.setDeliveryAddressIfAvailable();
			GlobalMessages.addErrorMessage(model, "checkout.error.credit.block");
			return ControllerConstants.Views.Pages.SingleStepCheckout.AddDeliveryDataPage;
		}

		if (bindingResult.hasErrors())
		{
			LOG.info("single checkout user {} has errors", currentUser);
			apbKegReturnUtil.validateKegReturnCustomerCheckoutForm(customerCheckoutForm);
			customerCheckoutForm.setKegReturnFlag((customerCheckoutForm.getKegReturnFlag()));
			apbCheckoutFacade.setDeliveryAddressIfAvailable();
			populateCommonModelAttributes(model, customerCheckoutForm);
			model.addAttribute("kegSizes", customerCheckoutForm.getApbKegReturnKegSizForm());
			GlobalMessages.addErrorMessage(model, "checkout.error.formentry.invalid");
			return ControllerConstants.Views.Pages.SingleStepCheckout.AddDeliveryDataPage;
		}

		final AddressData selectedAddress = getAddressDataFromRecordId(customerCheckoutForm.getDeliveryAddressId(), cartData);
		selectedAddress.setId(null);
		selectedAddress.setDeliveryInstruction(customerCheckoutForm.getDeliveryInstruction());
		selectedAddress.setSgaOrder(false);
		if (asahiSiteUtil.isSga())
		{
			selectedAddress.setSgaOrder(true);
			if(asahiSiteUtil.isBDECustomer() && customerCheckoutForm.getBdeCheckoutForm() != null) {
				apbCheckoutFacade.saveBDEOrderDetails(customerCheckoutForm.getBdeCheckoutForm());
			}
		}

		// Set the new address as the selected checkout delivery address
		acceleratorCheckoutFacade.setDeliveryAddress(selectedAddress);
		apbCheckoutFacade.setPaymentTypeInfo(customerCheckoutForm.getPaymentType(), customerCheckoutForm.getPoNumber());
		apbCheckoutFacade.setDeliveryTypeDetails(customerCheckoutForm.getDeliveryMethod().getDeliveryType(),
				customerCheckoutForm.getDeliveryMethod().getDeferredDeliveryDate());

		LOG.info("single checkout user {} with payment method {}", currentUser,customerCheckoutForm.getPaymentMethod());
		if (customerCheckoutForm.getPaymentMethod().equalsIgnoreCase(CARD))
		{
			String selectedCreditCardType = null;
			if (null != customerCheckoutForm.getAsahiCreditCardType())
			{
				selectedCreditCardType = customerCheckoutForm.getAsahiCreditCardType().toString().toUpperCase();
			}

			LOG.info("single checkout user {} with payment selected card type {}", currentUser,selectedCreditCardType);
			final List<CCPaymentInfoData> cardList = apbUserFacade.getCCPaymentInfos(true);
			if (selectedCreditCardType == null)
			{
				selectedCreditCardType = AsahiCreditCardTypeEnum.VISA.toString();
				if (CollectionUtils.isNotEmpty(cardList))
				{
					selectedCreditCardType = cardList.get(0).getCardType();
				}
			}
			LOG.info("single checkout user {} with payment updated card type {}", currentUser,selectedCreditCardType);

			cartData = apbCheckoutFacade.updateTotalwithCreditSurcharge(selectedCreditCardType, CARD);

			LOG.info("single checkout user {} after updateTotalwithCreditSurcharge", currentUser);

			if (null != customerCheckoutForm.getAsahiPaymentDetailsForm() && sabmCartFacade.isAddSurcharge()
					&& StringUtils.isNotEmpty(selectedCreditCardType)
					&& StringUtils.isNotEmpty(customerCheckoutForm.getAsahiPaymentDetailsForm().getCardTypeInfo())
					&& !selectedCreditCardType.equalsIgnoreCase(customerCheckoutForm.getAsahiPaymentDetailsForm().getCardTypeInfo()))
			{
				populateCommonModelAttributes(model, customerCheckoutForm);
				model.addAttribute(CART_DATA, cartData);
				apbCheckoutFacade.setDeliveryAddressIfAvailable();
				GlobalMessages.addErrorMessage(model, "asahi.payment.card.mismatch.message");
				LOG.info("single checkout user {} payment card mismatch error", currentUser);
				return enterStep(model, redirectModel);
			}

			/* payment integration start */

			/* create a PaymentData this will be used to handle further processing */
			final AsahiPaymentDetailsData asahiPaymentDetailsData = setAsahiPaymentDetailsData(
					customerCheckoutForm.getAsahiPaymentDetailsForm());
			getClientIPAddress(asahiPaymentDetailsData, request);
			updateCardAmountAndCardReference(asahiPaymentDetailsData, cartData);
			if (customerCheckoutForm.isSaveCreditCard() && allowAddCart(cardList.size()))
			{
				final AsahiPaymentDetailsForm paymentDetailsForm = customerCheckoutForm.getAsahiPaymentDetailsForm();
				acceleratorCheckoutFacade.createPaymentSubscription(pupulatePaymentCardData(paymentDetailsForm));
				LOG.info("single checkout user {} saved card payment success", currentUser);
			}
			try
			{
				LOG.info("single checkout user {} card payment with data start {}", currentUser, asahiPaymentDetailsData);
				apbCheckoutFacade.makeCreditCardPayment(asahiPaymentDetailsData);
				LOG.info("single checkout user {} card payment with data end", currentUser);
			}

			catch (final AsahiPaymentException e)
			{
				LOG.error("single checkout user {} card payment failed, Error {}", currentUser, e.getMessage());
				LOG.error("Credit Card Payment could not be completed.", e.getMessage());

				LOG.info("Check the errors below to troubleshoot");
				e.getErrorMessages().stream().forEach(error -> {
				    if(StringUtils.isNotEmpty(error) && StringUtils.isNotBlank(error)){
				        LOG.info(error);
                    }
                });
				LOG.info("--------- end of payment errors ----------");

				GlobalMessages.addErrorMessage(model, "asahi.payment.failed.message");
				model.addAttribute("creditCards", apbUserFacade.getCCPaymentInfos(true));
                model.addAttribute("updateIframe", true);
				return enterStep(model, redirectModel);
			}
			catch (final Exception e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Failed to make payment ", e);
				}
				LOG.error("single checkout user {} Failed to make payment, Error {}", currentUser, e.getMessage());
				GlobalMessages.addErrorMessage(model, "asahi.payment.failed.message");
				return enterStep(model, redirectModel);
			}

		}
		/* payment integration end */

		final OrderData orderData;
		try
		{
			if (customerCheckoutForm.getPaymentMethod().equalsIgnoreCase(CARD))
			{
				orderData = apbCheckoutFacade.placeOrder(customerCheckoutForm.getAsahiPaymentDetailsForm().getCardTypeInfo());
			 	if(orderData == null) {
					LOG.error("single checkout user {} placed order", currentUser);
					GlobalMessages.addErrorMessage(model, "asahi.payment.failed.message");
					return enterStep(model, redirectModel);
				}
			}
			else
			{
				orderData = apbCheckoutFacade.placeOrder(ACCOUNT);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			return enterStep(model, redirectModel);
		}
		//disable email sending for KEG return as the functionality is decommissioned in checkout page
		/*if ((!asahiSiteUtil.isSga()) && customerCheckoutForm.getKegReturnFlag())
		{
			sabmCustomerFacade.sendKegReturnEmail(apbKegReturnUtil.setKegReturnData(customerCheckoutForm));
		}*/

		return redirectToOrderConfirmationPage(orderData);
	}

	@PostMapping(value = "/updateCreditSurcharge", produces = "application/json")
	@ResponseBody
	@RequireHardLogIn
	public CartData addCreditSurcharge(final RedirectAttributes redirectAttributes,
			@RequestParam(value = "cardType", required = false) final String cardType,
			@RequestParam(value = "paymentMethod") final String paymentMethod)
	{
		if (paymentMethod == null || (CARD.equalsIgnoreCase(paymentMethod) && cardType == null))
		{
			return null;
		}
		return apbCheckoutFacade.updateTotalwithCreditSurcharge(cardType, paymentMethod);
	}

	@PostMapping("/changeDeliveryAddress")
	@ResponseBody
	@RequireHardLogIn
	public DeliveryInfoData changeDataForDeliveryAddress(@RequestParam(value = "recordId") final String addressRecordId)
	{
		return apbCheckoutFacade.getDeliveryInfo(addressRecordId);
	}

	private void updateCardAmountAndCardReference(final AsahiPaymentDetailsData asahiPaymentDetailsData, final CartData cartData)
	{

		final BigDecimal totalFormattedValue = cartData.getTotalPriceWithTax().getValue();
		/*
		 * final Double orderAmt = (totalFormattedValue.setScale(2,
		 * BigDecimal.ROUND_HALF_EVEN).doubleValue()) * 100;
		 */		
		LOG.info("Order Amoount before " + totalFormattedValue);
		final BigDecimal orderAmtBigDecimal = totalFormattedValue.setScale(2, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_EVEN);
		final BigDecimal additionCent = configurationService.getConfiguration().getBigDecimal(ADDITIONAL_CENT_TO_FATZEBRA, BigDecimal.ZERO);
		LOG.info("Order Amoount before adding cents " + orderAmtBigDecimal);
		final Double orderAmt = (orderAmtBigDecimal.add(additionCent)).doubleValue();
		LOG.info("Order Amoount " + orderAmt);
		asahiPaymentDetailsData.setTotalAmount(orderAmt);

	}


	private void getClientIPAddress(final AsahiPaymentDetailsData asahiPaymentDetailsData, final HttpServletRequest request)
	{

		asahiPaymentDetailsData.setCustomerIP(asahiPaymentIframeUrlUtil.getClientIPAddress(request));

	}

	private AsahiPaymentDetailsData setAsahiPaymentDetailsData(final AsahiPaymentDetailsForm asahiPaymentDetailsForm)
	{
		final AsahiPaymentDetailsData asahiPaymentDetailsData = new AsahiPaymentDetailsData();
		asahiPaymentDetailsData.setCardExpiry(asahiPaymentDetailsForm.getCardExpiry());
		asahiPaymentDetailsData.setCardNumber(asahiPaymentDetailsForm.getCardNumber());
		asahiPaymentDetailsData.setTransactionMessage(asahiPaymentDetailsForm.getMessage());
		asahiPaymentDetailsData.setCardToken(asahiPaymentDetailsForm.getCardToken());
		asahiPaymentDetailsData.setCardTypeInfo(asahiPaymentDetailsForm.getCardTypeInfo());
		asahiPaymentDetailsData.setResponseCode(asahiPaymentDetailsForm.getResponseCode());
		//new - add customer number
        asahiPaymentDetailsData.setCustomerNumber(asahiCoreUtil.getDefaultB2BUnit().getUid().substring(1));
		return asahiPaymentDetailsData;

	}


	@GetMapping("/back")
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@GetMapping("/next")
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	@ModelAttribute("paymentTypes")
	public Collection<B2BPaymentTypeData> getAllB2BPaymentTypes()
	{
		List<B2BPaymentTypeData> paymentType = null;
		if (!asahiSiteUtil.isSga())
		{
			paymentType = this.getPaymentTypes();
		}
		else
		{
            paymentType = apbCheckoutFacade.getPaymentTypesForCustomer(getAccNumForCurrentB2BUnit());
            if(sabmCustomerFacade.isOnAccountPaymentRestricted())
                paymentType = paymentType.stream().filter(data -> data.getCode().equalsIgnoreCase(CheckoutPaymentType.CARD.toString())).collect(Collectors.toList());
			if(!this.asahiConfigurationService.getBoolean("credit.card.enable.sga", false)){
			    paymentType = paymentType.stream().filter(data -> !data.getCode().equalsIgnoreCase(CheckoutPaymentType.CARD.toString())).collect(Collectors.toList());
            }
		}
		Collections.reverse(paymentType);
		return paymentType;
	}

	private AsahiB2BUnitModel getAccNumForCurrentB2BUnit()
	{
		final UserModel user = this.userService.getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !userService.isAnonymousUser(user))
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				return ((AsahiB2BUnitModel) b2bUnit).getPayerAccount();
			}
		}
		return null;
	}

	private List<B2BPaymentTypeData> getPaymentTypes()
	{
		final List<B2BPaymentTypeData> paymentTypes = b2bCheckoutFacade.getPaymentTypes();
		final List<B2BPaymentTypeData> modPaymentTypes = new ArrayList<B2BPaymentTypeData>();

		for (final B2BPaymentTypeData b2bPaymentTypeData : paymentTypes)
		{
			if (!PAYMENTMODE_DELIVERY.equals(b2bPaymentTypeData.getCode()))
			{
				modPaymentTypes.add(b2bPaymentTypeData);
			}
		}
		return modPaymentTypes;
	}
	
	protected CartData populateCommonModelAttributes(final Model model, final CustomerCheckoutForm customerCheckoutForm)
			throws CMSItemNotFoundException
	{
		final CartData cartData = apbCheckoutFacade.getCheckoutCart();

		if (!asahiSiteUtil.isSga())
		{
			apbCheckoutFacade.updateStockEntry(cartData);
		}

		model.addAttribute(CART_DATA, cartData);
	
		final CartModel cartModel = this.cartService.getSessionCart();
		String paymentType = null;
		if(null!=cartModel.getPaymentType()){
			paymentType = cartModel.getPaymentType().getCode();
			cartModel.setPaymentType(CheckoutPaymentType.DELIVERY);
			this.modelService.save(cartModel);
		}
		
		final List<? extends AddressData> addresses = getDeliveryAddresses(cartData.getDeliveryAddress()).stream()
				.filter(address -> null != address.getRecordId()).collect(Collectors.toList());
		
		final String cartAddressId = null != cartData.getDeliveryAddress() ? cartData.getDeliveryAddress().getId() : null;
		final List<AddressData> deliveryAddresses = placeDefaultAddressOnTop(addresses, cartAddressId);
		final DeliveryInfoData deliveryInfo = apbCheckoutFacade.getDeliveryInfo(null);
		if (StringUtils.isEmpty(customerCheckoutForm.getSelectedDeliveryAddress()))
		{
			customerCheckoutForm.setSelectedDeliveryAddress(
					CollectionUtils.isNotEmpty(deliveryAddresses) ? deliveryAddresses.get(0).getRecordId() : "NA");
			final List<KegSizeData> kegSizes = apbUserFacade.getKegSizes(getCmsSiteService().getCurrentSite());
			final List<ApbKegReturnKegSizForm> kegFormDataList = new ArrayList<>();
			kegSizes.stream().forEach(
					kegSize -> kegFormDataList.add(new ApbKegReturnKegSizForm(kegSize.getKegSize(), kegSize.getKegQuantity())));
			customerCheckoutForm.setApbKegReturnKegSizForm(kegFormDataList);
			final String firstDeliveryDate = CollectionUtils.isNotEmpty(deliveryInfo.getDeferredDeliveryOptions())
					? deliveryInfo.getDeferredDeliveryOptions().get(0) : StringUtils.EMPTY;
			//customerCheckoutForm.setDeliveryMethod(new DeliveryMethodForm("text", firstDeliveryDate));
		}
		else
		{
			final Optional<AddressData> selectedDeliveryAddress = deliveryAddresses.stream().filter(
					deliveryAddress -> customerCheckoutForm.getDeliveryAddressId().equalsIgnoreCase(deliveryAddress.getRecordId()))
					.findAny();
			if (selectedDeliveryAddress.isPresent())
			{
				selectedDeliveryAddress.get().setDeliveryInstruction(customerCheckoutForm.getDeliveryInstruction());

			}
			
		}
		if(null!=paymentType){
			cartModel.setPaymentType(enumerationService.getEnumerationValue(CheckoutPaymentType.class, paymentType));
			this.modelService.save(cartModel);
		}
		if(asahiSiteUtil.isBDECustomer()) {
			BdeOrderDetailsForm bdeCheckoutForm = null != customerCheckoutForm.getBdeCheckoutForm()? customerCheckoutForm.getBdeCheckoutForm(): new BdeOrderDetailsForm();
			bdeCheckoutForm.setEmailText(cartData.getBdeOrderEmailText());
			customerCheckoutForm.setBdeCheckoutForm(bdeCheckoutForm);
		}
		final String restrictedPattern = HtmlUtils.htmlUnescape(adhocCoreUtil.getConfigValue("apb.delivery.instruction.restricted.pattern"));
		deliveryAddresses.forEach(filterDeliveryAddress(restrictedPattern));
		model.addAttribute("deliveryAddresses", deliveryAddresses);
		model.addAttribute("deliveryInstructionLength",
				asahiConfigurationService.getString(DELIVERY_INSTRUCTION_CONFIGURED_LENGTH, "100"));
		model.addAttribute("isCheckout", true);
		model.addAttribute("customerCheckoutForm", customerCheckoutForm);
		model.addAttribute("deliveryInfoData", deliveryInfo);
		model.addAttribute("maxNumberOfCards", getMaxSavedCardsAllowed());
		model.addAttribute("visaSurcharge",
				asahiConfigurationService.getString(CREDIT_SURCHARGE_FOR_VISA + getCmsSiteService().getCurrentSite().getUid(), "10"));
		model.addAttribute("masterSurcharge", asahiConfigurationService
				.getString(CREDIT_SURCHARGE_FOR_MASTER + getCmsSiteService().getCurrentSite().getUid(), "10"));
		model.addAttribute("amexSurcharge",
				asahiConfigurationService.getString(CREDIT_SURCHARGE_FOR_AMEX + getCmsSiteService().getCurrentSite().getUid(), "10"));
		model.addAttribute("viewAllQuantity",
				Integer.getInteger(asahiConfigurationService.getString(VIEW_ALL_PRODUCTS_QUANTITY, "3"), 3));
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.checkout"));
		model.addAttribute(ADD_SURCHARGE, sabmCartFacade.isAddSurcharge());
		prepareDataForPage(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(CHECKOUT_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKOUT_CMS_PAGE_LABEL));

		if ((getContentPageForLabelOrId(CHECKOUT_CMS_PAGE_LABEL)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(CHECKOUT_CMS_PAGE_LABEL)).getBackgroundImage().getURL());
		}


		setCheckoutStepLinksForModel(model, getCheckoutStep());
		model.addAttribute("iframePostUrl", asahiPaymentIframeUrlUtil.getIframeUrl());
		return cartData;
	}

    private static Consumer<? super AddressData> filterDeliveryAddress(String p) {
	    return (address) -> {
	        if(address.getDeliveryInstruction() != null)
	            address.setDeliveryInstruction(address.getDeliveryInstruction().replaceAll(p, ""));
        };
    }

    private List<AddressData> placeDefaultAddressOnTop(final List<? extends AddressData> deliveryAddresses,
			final String defaultAddressId)
	{

		final List<AddressData> addresses = new ArrayList<>(deliveryAddresses);
		if (null != defaultAddressId && CollectionUtils.isNotEmpty(deliveryAddresses))
		{
			final Optional<AddressData> defaultAddress = addresses.stream()
					.filter(address -> address.getId().equals(defaultAddressId)).findFirst();
			if (defaultAddress.isPresent())
			{
				final AddressData defaultAddressData = defaultAddress.get();
				final int index = deliveryAddresses.indexOf(defaultAddressData);
				if (index > 0)
				{
					addresses.remove(index);
					addresses.add(0, defaultAddressData);
				}
			}

		}
		return addresses;
	}

	private AddressData getAddressDataFromRecordId(final String deliveryAddressId, final CartData cartData)
	{
		//		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		final List<? extends AddressData> addresses = getDeliveryAddresses(cartData.getDeliveryAddress()).stream()
				.collect(Collectors.toList());

		return addresses.stream()
				.filter(address -> address.getRecordId() != null && address.getRecordId().equals(deliveryAddressId)).findAny()
				.orElse(null);

	}

	@Override
	protected String redirectToOrderConfirmationPage(final OrderData orderData)
	{
		return getCheckoutStep().nextStep()
				+ (getCheckoutCustomerStrategy().isAnonymousCheckout() ? orderData.getGuid() : orderData.getCode());
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(SINGLE);
	}

	protected CustomerCheckoutFormValidator getCustomerCheckoutFormValidator()
	{
		return customerCheckoutFormValidator;
	}

	/**
	 * get keg Size based on active cmssite
	 *
	 * @return kegSizes
	 */
	@ModelAttribute("kegSizes")
	public List<KegSizeData> getKegSizes()
	{
		return apbUserFacade.getKegSizes(getCmsSiteService().getCurrentSite());
	}

	public boolean checkSuperUser()
	{
		final UserModel userModel = apbUserFacade.getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			return userModel.getGroups().stream().anyMatch(entry -> CollectionUtils.isNotEmpty(entry.getGroups())
					&& entry.getUid().equals(ApbStoreFrontContants.B2B_ADMIN_GROUP));
		}
		return false;
	}

	@ModelAttribute("creditCards")
	public List<CCPaymentInfoData> getCreditCard()
	{
		return apbUserFacade.getCCPaymentInfos(true);
	}

	private CCPaymentInfoData pupulatePaymentCardData(final AsahiPaymentDetailsForm paymentDetailsForm)
	{
		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		paymentInfoData.setCardNumber(paymentDetailsForm.getCardNumber());

		final String expiryDate = paymentDetailsForm.getCardExpiry();
		String[] splitExpDate = new String[2];
		if (null != expiryDate)
		{
			splitExpDate = expiryDate.split("/");
		}
		paymentInfoData.setCardType(setCardType(paymentDetailsForm.getCardTypeInfo().toString()));
		paymentInfoData.setToken(paymentDetailsForm.getCardToken());
		paymentInfoData.setExpiryMonth(splitExpDate[0]);
		paymentInfoData.setExpiryYear(splitExpDate[1]);
		paymentInfoData.setAccountHolderName(paymentDetailsForm.getCardHolderName());
		paymentInfoData.setSaved(true);
		paymentInfoData.setBillingAddress(setBillingAddressForCard());
		return paymentInfoData;
	}

	private String setCardType(final String cardType)
	{
		final String visaCardTypes = asahiConfigurationService
				.getString(VISA_TYPE_LIST + getCmsSiteService().getCurrentSite().getUid(), "Visa");
		final List<String> visaTypes = new ArrayList<>(Arrays.asList(visaCardTypes.split(",")));
		if (visaTypes.contains(cardType))
		{
			return CreditCardType.VISA.getCode();
		}
		final String masterCardTypes = asahiConfigurationService
				.getString(MASTER_TYPE_LIST + getCmsSiteService().getCurrentSite().getUid(), "MasterCard");
		final List<String> masterTypes = new ArrayList<>(Arrays.asList(masterCardTypes.split(",")));
		if (masterTypes.contains(cardType))
		{
			return CreditCardType.MASTER.getCode();
		}
		final String amexCardTypes = asahiConfigurationService
				.getString(AMEX_TYPE_LIST + getCmsSiteService().getCurrentSite().getUid(), "Amex");
		final List<String> amexTypes = new ArrayList<>(Arrays.asList(amexCardTypes.split(",")));
		if (amexTypes.contains(cardType))
		{
			return CreditCardType.AMEX.getCode();
		}
		return null;
	}

	private AddressData setBillingAddressForCard()
	{
		final AsahiB2BUnitModel b2bUnit = apbB2BUnitService.getCurrentB2BUnit();
		AddressData billingAddress = null;
		if (null != b2bUnit)
		{
			if (null != b2bUnit.getBillingAddress())
			{
				billingAddress = apbB2bAddressConverter.convert(b2bUnit.getBillingAddress());
			}
			else if (CollectionUtils.isNotEmpty(b2bUnit.getAddresses()))
			{
				billingAddress = apbB2bAddressConverter.convert(b2bUnit.getAddresses().iterator().next());
			}
			else
			{
				final List<AddressData> addresses = (List<AddressData>) acceleratorCheckoutFacade.getSupportedDeliveryAddresses(true);
				billingAddress = addresses.stream().filter(address -> address.isBillingAddress()).findFirst().orElse(null);
			}
		}
		if (null == billingAddress)
		{
			billingAddress = new AddressData();
			final CountryData country = new CountryData();
			country.setIsocode("US");
			billingAddress.setCountry(country);
		}

		return billingAddress;

	}

	private boolean allowAddCart(final Integer cardCount)
	{
		return cardCount < getMaxSavedCardsAllowed() ? true : false;
	}

	private int getMaxSavedCardsAllowed()
	{
		return Integer.parseInt(
				asahiConfigurationService.getString(MAX_CREDIT_CARDS_ALLOWED + getCmsSiteService().getCurrentSite().getUid(), "3"));
	}

	/**
	 * Method to validate cart before checkout
	 *
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/validateCheckoutCart", produces = "application/json")
	@RequireHardLogIn
	public String validateCheckoutCart(final Model model, final HttpServletRequest request)
	{
		if (asahiSiteUtil.isSga() && sabmCartFacade.hasSessionCart())
		{
		   LOG.info("Initiating checkout validation for products/cart entries");
           final LoginValidateInclusionData response = apbCheckoutFacade.updateCartWithInclusionList(false, 0);
           if(asahiConfigurationService.getBoolean("sga.fetch.total.gst.after.backend.failed", false)){
               asahiCoreUtil.setSessionCheckoutFlag(false);
               sabmCartFacade.getSessionCart();
           }
           else{
               asahiCoreUtil.setSessionCheckoutFlag(true);
               sabmCartFacade.getSessionCart();
           }
           model.addAttribute("response", response);
           request.getSession().setAttribute("wasCheckoutInterfce", true);
        }

		return "fragments/account/loginInclusionResponse";
	}
	
	private void setAdditionalInfoForSga(final Model model)
	{
		if(asahiSiteUtil.isSga())
		{
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
	}

}
