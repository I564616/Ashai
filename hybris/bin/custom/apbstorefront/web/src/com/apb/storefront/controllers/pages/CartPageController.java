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

import de.hybris.platform.acceleratorfacades.cart.action.CartEntryAction;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionFacade;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.acceleratorfacades.csv.CsvFacade;
import de.hybris.platform.acceleratorfacades.flow.impl.SessionOverrideCheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCartPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SaveCartForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateQuantityForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.VoucherForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.SaveCartFormValidator;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.message.AsahiMessageService;
import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.apb.facades.cart.AsahiSaveCartFacade;
import com.apb.facades.checkout.APBCheckoutFacade;
import com.apb.facades.product.AsahiProductRefernceFacade;
import com.apb.facades.user.ApbUserFacade;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.data.ErrorDTO;
import com.apb.storefront.data.LoginValidateInclusionData;


/**
 * Controller for cart page
 */
@Controller
@RequestMapping(value = "/cart")
public class CartPageController extends AbstractCartPageController
{
	public static final String SHOW_CHECKOUT_STRATEGY_OPTIONS = "storefront.show.checkout.flows";
	public static final String ERROR_MSG_TYPE = "errorMsg";
	public static final String SUCCESSFUL_MODIFICATION_CODE = "success";
	public static final String VOUCHER_FORM = "voucherForm";
	public static final String SITE_QUOTES_ENABLED = "site.quotes.enabled.";
	private static final String CART_CHECKOUT_ERROR = "cart.checkout.error";

	private static final String ACTION_CODE_PATH_VARIABLE_PATTERN = "{actionCode:.*}";

	private static final String REDIRECT_CART_URL = REDIRECT_PREFIX + "/cart";
	private static final String REDIRECT_QUOTE_EDIT_URL = REDIRECT_PREFIX + "/quote/%s/edit/";
	private static final String REDIRECT_QUOTE_VIEW_URL = REDIRECT_PREFIX + "/my-account/my-quotes/%s/";
	private static final String TIMEOUT_FOR_UPDATE_MESSAGE_ON_CART_PRODUCT_UPDATE = "cart.update.message.display.time";
	private static final String DEFAULT_TIMEOUT_ON_PRODUCT_UPDATE_MESSAGE = "10000";
	private static final String CART_CMS_PAGE_LABEL = "cart";
	private static final String EMPTY_CART_CMS_PAGE_LABEL = "emptyCart";
	public static final String CASES_COUNT_FOR_DELIVERY_FEE = "cases.count.delivery.fee.apb";

	public static final String MAX_PACKED_PRODUCT_ORDER_QTY = "max.packed.product.order.qty.";
	public static final String MAX_BIB_PRODUCT_ORDER_QTY = "max.bib.product.order.qty.";

	public static final String MAX_MIX_PACKED_PRODUCT_ORDER_QTY = "max.mix.packed.product.order.qty.";

	private static final Logger LOG = LoggerFactory.getLogger(CartPageController.class);

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "asahiSaveCartFacade")
	private AsahiSaveCartFacade saveCartFacade;

	@Resource(name = "saveCartFormValidator")
	private SaveCartFormValidator saveCartFormValidator;

	@Resource(name = "csvFacade")
	private CsvFacade csvFacade;

	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "cartEntryActionFacade")
	private CartEntryActionFacade cartEntryActionFacade;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource(name = "cartFacade")
	private SABMCartFacade sabmCartFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "asahiMessageService")
	private AsahiMessageService asahiMessageService;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;

	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "asahiProductRefernceFacade")
	private AsahiProductRefernceFacade asahiProductRefernceFacade;

	@Resource
	private UserService userService;

	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;
	
	@Resource(name = "modelService")
	private ModelService modelService;
	
	
	@Resource(name = "apbCheckoutFacade")
	private APBCheckoutFacade apbCheckoutFacade;
	
	@Resource(name="cartService")
	private CartService cartService;

	@ModelAttribute("showCheckoutStrategies")
	public boolean isCheckoutStrategyVisible()
	{
		return getSiteConfigService().getBoolean(SHOW_CHECKOUT_STRATEGY_OPTIONS, false);
	}

	@RequireHardLogIn
	@GetMapping
	public String showCart(@RequestParam(value = "entryNumber", required = false) final Long entryNumber,
			@RequestParam(value = "qtyUpdateMsg", required = false) final String qtyUpdateMsg,
			@RequestParam(value = "error", required = false) final boolean error, final Model model,final HttpServletRequest request) throws CMSItemNotFoundException
	{
		
		if (asahiSiteUtil.isSga()){
			/*
			 * In case user comes back from checkout page without making payment
			 */
			if (asahiCoreUtil.isNAPUser()) {
				return FORWARD_PREFIX + "/404";
			}
			model.addAttribute("recommendationPopupEnabled", asahiConfigurationService.getBoolean("sga.recommendation.popup.enabled", false));

			if(null!=request && null!=request.getSession()){
				
				if(null!=request.getSession().getAttribute("wasUpdateCall")){
					request.getSession().removeAttribute("wasUpdateCall");
				}else{
					final LoginValidateInclusionData response = this.apbCheckoutFacade.updateCartWithInclusionList(false,0);
					
					if(null!=response && CollectionUtils.isNotEmpty(response.getErrors())){
						GlobalMessages.addErrorMessage(model, response.getErrors().get(0).getError());
						model.addAttribute("cartInclusionErrorType",response.getErrors().get(0).getErrorCode());
					}
					request.getSession().setAttribute("wasCheckoutInterfce", true);
				}
			}
			if(asahiSiteUtil.isBDECustomer()) {
				model.addAttribute("hasBonusStockProductOnly", sabmCartFacade.isBonusStockProductsInCart());
			}
		}

		if (!(model.containsAttribute("cartAction")))
		{
			sabmCartFacade.updateCartForPrice();
		}

		model.addAttribute("entryNumber", entryNumber);
		model.addAttribute("qtyUpdateMsg", qtyUpdateMsg);
		model.addAttribute("msgTimeOut", timeOutForUpdateMessageOnCart());
		model.addAttribute("error", error);

		if (asahiSiteUtil.isSga() && !userService.isAnonymousUser(userService.getCurrentUser())
				&& asahiConfigurationService.getBoolean(ApbStoreFrontContants.PRODUCT_REFERENCE_CART_ENABLE, false))
		{
			model.addAttribute("productRecommendation", asahiProductRefernceFacade.getCartRecommendedProducts());
		}

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		if (asahiSiteUtil.isApb() && assistedServiceFacade.isAssistedServiceAgentLoggedIn())
		{
			model.addAttribute("asmMode", "true");
			model.addAttribute("hasBonusStockProductOnly", sabmCartFacade.isBonusStockProductsInCart());
		}
		setAdditionalInfoForSga(model);
		return prepareCartUrl(model);
	}

	protected String prepareCartUrl(final Model model) throws CMSItemNotFoundException
	{

		final boolean isEmptyCart = prepareDataForCartPage(model);
		if (isEmptyCart)
		{
			return ControllerConstants.Views.Pages.Cart.EmptyCartPage;
		}
		return ControllerConstants.Views.Pages.Cart.CartPage;

	}

	protected Optional<String> getQuoteUrl()
	{
		final QuoteData quoteData = getCartFacade().getSessionCart().getQuoteData();

		return quoteData != null
				? (QuoteState.BUYER_OFFER.equals(quoteData.getState())
						? Optional.of(String.format(REDIRECT_QUOTE_VIEW_URL, urlEncode(quoteData.getCode())))
						: Optional.of(String.format(REDIRECT_QUOTE_EDIT_URL, urlEncode(quoteData.getCode()))))
				: Optional.empty();
	}

	/**
	 * Handle the '/cart/checkout' request url. This method checks to see if the cart is valid before allowing the
	 * checkout to begin. Note that this method does not require the user to be authenticated and therefore allows us to
	 * validate that the cart is valid without first forcing the user to login. The cart will be checked again once the
	 * user has logged in.
	 *
	 * @return The page to redirect to
	 */
	@GetMapping("/checkout")
	@RequireHardLogIn
	public String cartCheck(final RedirectAttributes redirectModel) throws CommerceCartModificationException
	{
		
		SessionOverrideCheckoutFlowFacade.resetSessionOverrides();

		if (!getCartFacade().hasEntries())
		{
			LOG.info("Missing or empty cart");

			// No session cart or empty session cart. Bounce back to the cart page.
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.error.checkout.empty.cart",
					null);
			return REDIRECT_CART_URL;
		}


		if (validateCart(redirectModel))
		{
			return REDIRECT_CART_URL;
		}

		// Redirect to the start of the checkout flow to begin the checkout process
		// We just redirect to the generic '/checkout' page which will actually select the checkout flow
		// to use. The customer is not necessarily logged in on this request, but will be forced to login
		// when they arrive on the '/checkout' page.
		return REDIRECT_PREFIX + "/checkout";
	}

	@GetMapping("/getProductVariantMatrix")
	public String getProductVariantMatrix(@RequestParam("productCode") final String productCode,
			@RequestParam(value = "readOnly", required = false, defaultValue = "false") final String readOnly, final Model model)
	{

		final ProductData productData = productFacade.getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES, ProductOption.VARIANT_MATRIX_BASE,
						ProductOption.VARIANT_MATRIX_PRICE, ProductOption.VARIANT_MATRIX_MEDIA, ProductOption.VARIANT_MATRIX_STOCK,
						ProductOption.VARIANT_MATRIX_URL));

		model.addAttribute("product", productData);
		model.addAttribute("readOnly", Boolean.valueOf(readOnly));

		return ControllerConstants.Views.Fragments.Cart.ExpandGridInCart;
	}

	// This controller method is used to allow the site to force the visitor through a specified checkout flow.
	// If you only have a static configured checkout flow then you can remove this method.
	@GetMapping("/checkout/select-flow")
	@RequireHardLogIn
	public String initCheck(final Model model, final RedirectAttributes redirectModel,
			@RequestParam(value = "flow", required = false) final String flow,
			@RequestParam(value = "pci", required = false) final String pci) throws CommerceCartModificationException
	{
		SessionOverrideCheckoutFlowFacade.resetSessionOverrides();

		if (!getCartFacade().hasEntries())
		{
			LOG.info("Missing or empty cart");

			// No session cart or empty session cart. Bounce back to the cart page.
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.error.checkout.empty.cart",
					null);
			return REDIRECT_CART_URL;
		}

		// Override the Checkout Flow setting in the session
		if (StringUtils.isNotBlank(flow))
		{
			final CheckoutFlowEnum checkoutFlow = enumerationService.getEnumerationValue(CheckoutFlowEnum.class,
					StringUtils.upperCase(flow));
			SessionOverrideCheckoutFlowFacade.setSessionOverrideCheckoutFlow(checkoutFlow);
		}

		// Override the Checkout PCI setting in the session
		if (StringUtils.isNotBlank(pci))
		{
			final CheckoutPciOptionEnum checkoutPci = enumerationService.getEnumerationValue(CheckoutPciOptionEnum.class,
					StringUtils.upperCase(pci));
			SessionOverrideCheckoutFlowFacade.setSessionOverrideSubscriptionPciOption(checkoutPci);
		}

		// Redirect to the start of the checkout flow to begin the checkout process
		// We just redirect to the generic '/checkout' page which will actually select the checkout flow
		// to use. The customer is not necessarily logged in on this request, but will be forced to login
		// when they arrive on the '/checkout' page.
		return REDIRECT_PREFIX + "/checkout";
	}

	@PostMapping("/entrygroups/{groupNumber}")
	public String removeGroup(@PathVariable("groupNumber") final Integer groupNumber, final Model model,
			final RedirectAttributes redirectModel)
	{
		final CartModificationData cartModification;
		try
		{
			cartModification = getCartFacade().removeEntryGroup(groupNumber);
			if (cartModification != null && !StringUtils.isEmpty(cartModification.getStatusMessage()))
			{
				GlobalMessages.addErrorMessage(model, cartModification.getStatusMessage());
			}
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.error(e.getMessage(), e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.export.cart.error", null);
		}
		return REDIRECT_CART_URL;
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
				model.addAttribute("cartAction", "UPDATE");
				CartModificationData cartModification = null;
				if (form.getQuantity().longValue() != 0)
				{
					CartModel cartUpdateEntry = cartService.getSessionCart();
					if(Objects.nonNull(cartUpdateEntry) && CollectionUtils.isNotEmpty(cartUpdateEntry.getEntries())) {
						for(AbstractOrderEntryModel cartEntryModel : cartUpdateEntry.getEntries()) {
							if(StringUtils.isNotBlank(cartEntryModel.getAsahiDealCode())) {
								for(AbstractOrderEntryModel cartEntryModelFree : cartUpdateEntry.getEntries()) {
								if(cartEntryModelFree.getIsFreeGood().TRUE && cartEntryModel.getAsahiDealCode().equals(cartEntryModelFree.getAsahiDealCode())) {
								cartEntryModel.setFreeGoodEntryNumber(String.valueOf(cartEntryModelFree.getEntryNumber()));
								getModelService().save(cartEntryModel);
								getModelService().refresh(cartEntryModel);
								}
								}
								}
						}
					}
					cartModification = getCartFacade().updateCartEntry(entryNumber, form.getQuantity().longValue());
					
					if (asahiSiteUtil.isSga()){
						
						asahiCoreUtil.removeSessionCheckoutFlag();
						
						this.apbCheckoutFacade.updateCartWithInclusionList(true, form.getQuantity());
						request.getSession().setAttribute("wasCheckoutInterfce", true);
						request.getSession().setAttribute("wasUpdateCall", true);
						if (cartModification.getQuantity() == form.getQuantity().longValue()) {
							removeOrUpdateFreeDealProductOnQtyUpdate(cartModification.getQuantity(), entryNumber);
						}
					}
				}

				return addQtyUpdateMessage(form, entryNumber, model, cartModification);

			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
			}
		}

		// if could not update cart, display cart/quote page again with error
		return prepareCartUrl(model);
	}


	private boolean prepareDataForCartPage(final Model model) throws CMSItemNotFoundException
	{
		continueUrl(model);

		final boolean isEmptyCart = createCartProductList(model);

		setupCartPageRestorationData(model);
		clearSessionRestorationData();

		model.addAttribute("savedCartCount", saveCartFacade.getSavedCartsCountForCurrentUser());
		if (!model.containsAttribute("saveCartForm"))
		{
			model.addAttribute("saveCartForm", new SaveCartForm());
		}

		// Because DefaultSiteConfigService.getProperty() doesn't set default boolean value for undefined property,
		// this property key was generated to use Config.getBoolean() method
		final String siteQuoteProperty = SITE_QUOTES_ENABLED.concat(getBaseSiteService().getCurrentBaseSite().getUid());
		model.addAttribute("siteQuoteEnabled", Config.getBoolean(siteQuoteProperty, Boolean.FALSE));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.cart"));
		model.addAttribute("pageType", PageType.CART.name());
		model.addAttribute("defaultMaxQuantity", baseSiteService.getCurrentBaseSite().getMaxOrderQty());
		return isEmptyCart;

	}


	private boolean createCartProductList(final Model model) throws CMSItemNotFoundException
	{
		final CartData cartData = sabmCartFacade.getSessionCartWithEntryOrdering(false);
		boolean isEmptyCart = true;
		createProductEntryList(model, cartData);
		int totalUnit = cartData.getTotalUnitCount();
		final int totalBonusQty = 0;



		if (totalUnit > 0)
		{
			isEmptyCart = false;
			if (!cartData.getPriceUpdated())
			{
				GlobalMessages.addErrorMessage(model,asahiSiteUtil.isApb()? "apb.price.not.fetched.services.msg":"price.not.fetched.services.msg");
				model.addAttribute("priceError", true);
			}
			else if (cartData.getEntries().stream().filter(entry -> !entry.isCalculated()).findFirst().isPresent())
			{
				GlobalMessages.addErrorMessage(model, "partial.price.error.services.msg");
				model.addAttribute("partialPriceError", true);
			}
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
			final BigDecimal deliveryCharges = cartData.getDeliveryCost().getValue();


			if (deliveryCharges != null && deliveryCharges.compareTo(BigDecimal.ZERO) > 0)
			{


				final String caseCountStr = asahiConfigurationService.getString(CASES_COUNT_FOR_DELIVERY_FEE, "5");
				final int caseCount = Integer.parseInt(caseCountStr);
				if (asahiSiteUtil.isApb() && assistedServiceFacade.isAssistedServiceModeLaunched())
				{
					totalUnit = removeBonusProductQty(cartData, totalUnit, totalBonusQty);

				}
				final int toAddCount = caseCount - totalUnit;
				if (toAddCount > 1)
				{
					GlobalMessages.addMessage(model, "accDeliveryMsgs", "cart.addMore.cases.remove.delivery.charges", new Object[]
					{ toAddCount, cartData.getDeliveryCost().getFormattedValue() });
				}
				else
				{
					GlobalMessages.addMessage(model, "accDeliveryMsgs", "cart.addMore.case.remove.delivery.charges", new Object[]
					{ toAddCount, cartData.getDeliveryCost().getFormattedValue() });
				}
			}

			//min order quantity check for sga
			model.addAttribute("minOrderQtyCheck", false);
			if (asahiSiteUtil.isSga())
			{
				final boolean disableCheckoutButton = sabmCartFacade.validateMinOrderQuantity(cartData);

				if (disableCheckoutButton)
				{
					disableAddToCart(model);
				}
			}


			storeCmsPageInModel(model, getContentPageForLabelOrId(CART_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CART_CMS_PAGE_LABEL));
		}
		else
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(EMPTY_CART_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(EMPTY_CART_CMS_PAGE_LABEL));
		}
		if (asahiSiteUtil.isSga() && null != cartData.getShowExclusionError() && cartData.getShowExclusionError().booleanValue())
		{
			GlobalMessages.addErrorMessage(model, "sga.basket.exclusion.error.message");
		}
		
		if (asahiSiteUtil.isSga() && Boolean.valueOf(asahiConfigurationService.getString("sga.product.status.available", "false")) &&
				BooleanUtils.isTrue(cartData.getOutofStockItemAvailable()))
		{
			GlobalMessages.addErrorMessage(model, "sga.cart.outofstock.error.message");
		}
		
		return isEmptyCart;
	}

	/**
	 * Method will remove the bonus qty from delivery amount calculation
	 *
	 * @param cartData
	 * @param totalUnit
	 * @param totalBonusQty
	 * @return
	 */
	private int removeBonusProductQty(final CartData cartData, int totalUnit, int totalBonusQty)
	{
		for (final OrderEntryData oe : cartData.getEntries())
		{
			if (oe.getIsBonusStock())
			{
				totalBonusQty = totalBonusQty + oe.getQuantity().intValue();

			}
		}
		totalUnit = totalUnit - totalBonusQty;
		return totalUnit;
	}

	/**
	 * <p>
	 * method to disable the checkout button and enables the error message on the cart page.
	 * </p>
	 *
	 * @param model
	 */
	private void disableAddToCart(final Model model)
	{
		model.addAttribute("minOrderQtyCheck", true);
		GlobalMessages.addErrorMessage(model, "sga.basket.max.order.qty.error.msg");
	}


	@Override
	protected void createProductEntryList(final Model model, final CartData cartData)
	{
		boolean hasPickUpCartEntries = false;
		final List<String> productList = new ArrayList<>();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				productList.add(entry.getProduct().getCode());
				if (!hasPickUpCartEntries && entry.getDeliveryPointOfService() != null)
				{
					hasPickUpCartEntries = true;
				}
				final UpdateQuantityForm uqf = new UpdateQuantityForm();
				uqf.setQuantity(entry.getQuantity());

				model.addAttribute("updateQuantityForm" + entry.getEntryNumber(), uqf);
			}
			if (!asahiSiteUtil.isSga())
			{
				sabmCartFacade.updateStockEntry(cartData, productList);
			}
		}
		if (CollectionUtils.isNotEmpty(cartData.getEntries()))
		{
			sabmCartFacade.updateProductEntries(cartData);
		}
		model.addAttribute("cartData", cartData);
		model.addAttribute("hasPickUpCartEntries", Boolean.valueOf(hasPickUpCartEntries));
	}


	protected void addFlashMessage(final UpdateQuantityForm form, final HttpServletRequest request,
			final RedirectAttributes redirectModel, final CartModificationData cartModification)
	{
		if (cartModification.getQuantity() == form.getQuantity().longValue())
		{
			// Success

			if (cartModification.getQuantity() == 0)
			{
				// Success in removing entry
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "basket.page.message.remove");
			}
			else
			{
				// Success in update quantity
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "basket.page.message.update");
			}
		}
		else if (cartModification.getQuantity() > 0)
		{
			// Less than successful
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"basket.page.message.update.reducedNumberOfItemsAdded.lowStock", new Object[]
					{ XSSFilterUtil.filter(cartModification.getEntry().getProduct().getName()), Long.valueOf(cartModification.getQuantity()), form.getQuantity(), request.getRequestURL().append(cartModification.getEntry().getProduct().getUrl()) });
		}
		else
		{
			// No more stock available
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"basket.page.message.update.reducedNumberOfItemsAdded.noStock", new Object[]
					{ XSSFilterUtil.filter(cartModification.getEntry().getProduct().getName()), request.getRequestURL().append(cartModification.getEntry().getProduct().getUrl()) });
		}
	}

	protected String addQtyUpdateMessage(final UpdateQuantityForm form, final long entryNumber, final Model model,
			final CartModificationData cartModification) throws CMSItemNotFoundException, CommerceCartModificationException
	{
		String qtyUpdateMsg = "";
		boolean error = true;
		// Updating zero quantity (removing) from cart via quantity update not allowed.
		if (form.getQuantity().longValue() == 0)
		{
			qtyUpdateMsg = "cart.inline.product.update.quantity.zero";
		}
		else if (cartModification.getQuantity() == form.getQuantity().longValue())
		{
			// Success in update quantity
			qtyUpdateMsg = "cart.inline.product.update.quantity.updated";
			error = false;
		}
		else if (cartModification.getQuantity() > 0)
		{
			// Quantity update requested more than quantity update allowed
			qtyUpdateMsg = "cart.inline.product.update.quantity.max.allowed";
		}

		return getCartPageRedirectUrl(entryNumber, qtyUpdateMsg, error, model);
	}
	

	private void removeOrUpdateFreeDealProductOnQtyUpdate(final long updatedQuantity,long entryNumber) throws CommerceCartModificationException {
		sabmCartFacade.removeOrUpdateFreeDealProductOnQtyUpdate(updatedQuantity, entryNumber);
		
	}

	@SuppressWarnings("boxing")
	@ResponseBody
	@PostMapping("/updateMultiD")
	public CartData updateCartQuantitiesMultiD(@RequestParam("entryNumber") final Integer entryNumber,
			@RequestParam("productCode") final String productCode, final Model model, @Valid final UpdateQuantityForm form,
			final BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
		{
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
		else
		{
			try
			{
				final CartModificationData cartModification = getCartFacade()
						.updateCartEntry(getOrderEntryData(form.getQuantity(), productCode, entryNumber));
				if (cartModification.getStatusCode().equals(SUCCESSFUL_MODIFICATION_CODE))
				{
					GlobalMessages.addMessage(model, GlobalMessages.CONF_MESSAGES_HOLDER, cartModification.getStatusMessage(), null);
				}
				else if (!model.containsAttribute(ERROR_MSG_TYPE))
				{
					GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, cartModification.getStatusMessage(), null);
				}
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
			}

		}
		return getCartFacade().getSessionCart();
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

	@PostMapping("/save")
	@RequireHardLogIn
	@ResponseBody
	public AsahiControllerMessage saveCart(final SaveCartForm form, final BindingResult bindingResult,
			final RedirectAttributes redirectModel)
	{
		final AsahiControllerMessage message = new AsahiControllerMessage();
		saveCartFormValidator.validate(form, bindingResult);
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				message.setErrorKey(true);
				message.setMessage(this.asahiMessageService.getString("basket.save.invalid.cart.on.error.apb", ""));
			}
			redirectModel.addFlashAttribute("saveCartForm", form);
		}
		else
		{
			final boolean isOrderTemplateSaved = this.saveCartFacade.saveOrderTemplate(form.getName());
			//check if cart already exist for Name and b2bUnit
			if (isOrderTemplateSaved)
			{
				message.setSuccessKey(true);
				message.setMessage(this.asahiMessageService.getString("basket.save.cart.on.success.apb", ""));
			}
			else
			{
				message.setErrorKey(true);
				message.setMessage(this.asahiMessageService.getString("basket.already.save.cart.on.error.apb", ""));
			}
		}
		return message;
	}

	@GetMapping(value = "/export", produces = "text/csv")
	public String exportCsvFile(final HttpServletResponse response, final RedirectAttributes redirectModel) throws IOException
	{
		response.setHeader("Content-Disposition", "attachment;filename=cart.csv");

		try (final StringWriter writer = new StringWriter())
		{
			try
			{
				final List<String> headers = new ArrayList<>();
				headers.add(getMessageSource().getMessage("basket.export.cart.item.sku", null, getI18nService().getCurrentLocale()));
				headers.add(
						getMessageSource().getMessage("basket.export.cart.item.quantity", null, getI18nService().getCurrentLocale()));
				headers.add(getMessageSource().getMessage("basket.export.cart.item.name", null, getI18nService().getCurrentLocale()));
				headers
						.add(getMessageSource().getMessage("basket.export.cart.item.price", null, getI18nService().getCurrentLocale()));

				final CartData cartData = getCartFacade().getSessionCartWithEntryOrdering(false);
				csvFacade.generateCsvFromCart(headers, true, cartData, writer);

				StreamUtils.copy(writer.toString(), StandardCharsets.UTF_8, response.getOutputStream());
			}
			catch (final IOException e)
			{
				LOG.error(e.getMessage(), e);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.export.cart.error", null);

				return REDIRECT_CART_URL;
			}

		}

		return null;
	}

	@PostMapping("/voucher/apply")
	public String applyVoucherAction(@Valid final VoucherForm form, final BindingResult bindingResult,
			final RedirectAttributes redirectAttributes)
	{
		try
		{
			if (bindingResult.hasErrors())
			{
				redirectAttributes.addFlashAttribute(ERROR_MSG_TYPE,
						getMessageSource().getMessage("text.voucher.apply.invalid.error", null, getI18nService().getCurrentLocale()));
			}
			else
			{
				voucherFacade.applyVoucher(form.getVoucherCode());
				redirectAttributes.addFlashAttribute("successMsg",
						getMessageSource().getMessage("text.voucher.apply.applied.success", new Object[]
						{ form.getVoucherCode() }, getI18nService().getCurrentLocale()));
			}
		}
		catch (final VoucherOperationException e)
		{
			redirectAttributes.addFlashAttribute(VOUCHER_FORM, form);
			redirectAttributes.addFlashAttribute(ERROR_MSG_TYPE,
					getMessageSource().getMessage(e.getMessage(), null,
							getMessageSource().getMessage("text.voucher.apply.invalid.error", null, getI18nService().getCurrentLocale()),
							getI18nService().getCurrentLocale()));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e.getMessage(), e);
			}

		}

		return REDIRECT_CART_URL;
	}

	@PostMapping("/voucher/remove")
	public String removeVoucher(@Valid final VoucherForm form, final RedirectAttributes redirectModel)
	{
		try
		{
			voucherFacade.releaseVoucher(form.getVoucherCode());
		}
		catch (final VoucherOperationException e)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "text.voucher.release.error",
					new Object[]
					{ form.getVoucherCode() });
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e.getMessage(), e);
			}

		}
		return REDIRECT_CART_URL;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	@PostMapping("/entry/execute/" + ACTION_CODE_PATH_VARIABLE_PATTERN)
	public String executeCartEntryAction(@PathVariable(value = "actionCode", required = true) final String actionCode,
			final RedirectAttributes redirectModel, @RequestParam("entryNumbers") final Long[] entryNumbers)
	{
		CartEntryAction action = null;
		try
		{
			action = CartEntryAction.valueOf(actionCode);

		}
		catch (final IllegalArgumentException e)
		{
			LOG.error(String.format("Unknown cart entry action %s", ApbXSSEncoderUtil.encodeValue(actionCode)), e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.page.entry.unknownAction");
			return getCartPageRedirectUrl();
		}

		try
		{
			redirectModel.addFlashAttribute("cartAction", "REMOVE");
			String dealCondition = StringUtils.EMPTY;
			if (asahiSiteUtil.isSga() && null != entryNumbers) {
				final CartEntryModel cartEntry = cartService.getEntryForNumber(cartService.getSessionCart(),
						(Arrays.asList(entryNumbers).get(0).intValue()));
				 dealCondition = cartEntry.getAsahiDealCode();
			}
			final Optional<String> redirectUrl = cartEntryActionFacade.executeAction(action, Arrays.asList(entryNumbers));
			CartModel cartAfterRemoval = cartService.getSessionCart();
			if(Objects.nonNull(cartAfterRemoval) && CollectionUtils.isNotEmpty(cartAfterRemoval.getEntries())) {
				for(AbstractOrderEntryModel cartEntryModel : cartAfterRemoval.getEntries()) {
					if(StringUtils.isNotEmpty(cartEntryModel.getFreeGoodEntryNumber())) {
						for(AbstractOrderEntryModel cartEntryModelFree : cartAfterRemoval.getEntries()) {
						if(cartEntryModelFree.getIsFreeGood().TRUE && cartEntryModel.getAsahiDealCode().equals(cartEntryModelFree.getAsahiDealCode())) {
						cartEntryModel.setFreeGoodEntryNumber(String.valueOf(cartEntryModelFree.getEntryNumber()));
						getModelService().save(cartEntryModel);
						getModelService().refresh(cartEntryModel);
						}
						}
						}
				}
			}
			final Optional<String> successMessageKey = cartEntryActionFacade.getSuccessMessageKey(action);
			if (successMessageKey.isPresent())
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, successMessageKey.get());
				
				//Remove SGA FreeDeal OrderEntry when parent entry is removed
				if (asahiSiteUtil.isSga() && StringUtils.isNotBlank(dealCondition) ) {
					final String asahiDealCondition = dealCondition;
					CartModel currentCart = cartService.getSessionCart();
					if (null != currentCart && CollectionUtils.isNotEmpty(currentCart.getEntries())) {
						AbstractOrderEntryModel freeDealEntry = currentCart.getEntries().stream().filter(entry -> BooleanUtils.isTrue(entry.getIsFreeGood())
								&& StringUtils.isNotBlank(entry.getAsahiDealCode()) 
								&& entry.getAsahiDealCode().equals(asahiDealCondition)).findFirst().orElse(null);
						if (null != freeDealEntry) {
							cartEntryActionFacade.executeAction(CartEntryAction.REMOVE, Arrays.asList(Long.valueOf(freeDealEntry.getEntryNumber())));
						}
					}
				}
			}
			if (redirectUrl.isPresent())
			{
				return redirectUrl.get();
			}
			else
			{
				if (asahiSiteUtil.isSga()){
					final LoginValidateInclusionData response = this.apbCheckoutFacade.updateCartWithInclusionList(false,0);
				}
				
				return getCartPageRedirectUrl();
			}
		}
		catch (final CartEntryActionException e)
		{
			LOG.error(String.format("Failed to execute action %s", action), e);
			final Optional<String> errorMessageKey = cartEntryActionFacade.getErrorMessageKey(action);
			if (errorMessageKey.isPresent())
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, errorMessageKey.get());
			}
			return getCartPageRedirectUrl();
		}
	}

	protected String getCartPageRedirectUrl()
	{
		final QuoteData quoteData = getCartFacade().getSessionCart().getQuoteData();
		return quoteData != null ? String.format(REDIRECT_QUOTE_EDIT_URL, urlEncode(quoteData.getCode())) : REDIRECT_CART_URL;
	}

	protected String getCartPageRedirectUrl(final Long entryNumber, final String qtyUpdateMsg, final boolean error,
			final Model model) throws CMSItemNotFoundException
	{
		final QuoteData quoteData = getCartFacade().getSessionCart().getQuoteData();
		return quoteData != null ? String.format(REDIRECT_QUOTE_EDIT_URL, urlEncode(quoteData.getCode()))
				: showCart(entryNumber, qtyUpdateMsg, error, model,null);
	}


	/**
	 * @param redirectModel
	 * @return
	 * @See Remove all the products from cart
	 */
	@PostMapping("/removeAllProducts")
	public String removeAllProducts(final RedirectAttributes redirectModel)
	{
		try
		{
			sabmCartFacade.removeAllProducts(redirectModel);
		}
		catch (final ModelRemovalException e)
		{
			LOG.error(e.getMessage(), e);
		}
		return REDIRECT_CART_URL;
	}

	private Long timeOutForUpdateMessageOnCart()
	{
		try
		{
			return Long.valueOf(asahiConfigurationService.getString(TIMEOUT_FOR_UPDATE_MESSAGE_ON_CART_PRODUCT_UPDATE,
					DEFAULT_TIMEOUT_ON_PRODUCT_UPDATE_MESSAGE));
		}
		catch (final NumberFormatException e)
		{
			return Long.valueOf(DEFAULT_TIMEOUT_ON_PRODUCT_UPDATE_MESSAGE);
		}
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

	@Override
	protected boolean validateCart(final RedirectAttributes redirectModel)
	{
		//Validate the cart
		List<CartModificationData> modifications = new ArrayList<>();
		try
		{
			modifications = sabmCartFacade.validateCartData();
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.error("Failed to validate cart", e);
		}
		if (!modifications.isEmpty())
		{
			final List<String> removedProducts = modifications.stream()
					.filter(modification -> modification.getStatusCode().equals(CommerceCartModificationStatus.UNAVAILABLE))
					.map(entry -> getProductEntryDetails(entry)).distinct().collect(Collectors.toList());
			redirectModel.addFlashAttribute("validationData", modifications);

			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.validation.unavailable",
					new Object[]
					{ String.join(", ", removedProducts) });
			// Invalid cart. Bounce back to the cart page.
			return true;
		}
		return false;
	}

	private String getProductEntryDetails(final CartModificationData modificationData)
	{
		final ProductData product = modificationData.getEntry().getProduct();
		return product.getApbBrand().getName() + " " + product.getName()
				+ (null != product.getUnitVolume() ? " " + product.getUnitVolume().getName() : StringUtils.EMPTY)
				+ (null != product.getPackageSize() ? " " + product.getPackageSize().getName() : StringUtils.EMPTY);
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
	
	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
		

}
