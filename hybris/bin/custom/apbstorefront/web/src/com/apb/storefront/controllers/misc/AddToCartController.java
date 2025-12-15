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
package com.apb.storefront.controllers.misc;

import de.hybris.platform.acceleratorfacades.product.data.ProductWrapperData;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartOrderForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToEntryGroupForm;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.converters.populator.GroupCartModificationListPopulator;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apb.core.cart.validation.strategy.AsahiBonusCartValidationStrategy;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.b2bunit.ApbB2BUnitFacade;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.controllers.ControllerConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
public class AddToCartController extends AbstractController
{
	private static final String QUANTITY_ATTR = "quantity";
	private static final String TYPE_MISMATCH_ERROR_CODE = "typeMismatch";
	private static final String ERROR_MSG_TYPE = "errorMsg";
	private static final String QUANTITY_INVALID_BINDING_MESSAGE_KEY = "basket.error.quantity.invalid.binding";
	private static final String SHOWN_PRODUCT_COUNT = "apbstorefront.storefront.minicart.shownProductCount";
	private static final String ANONYMOUS = "anonymous";
	private static final String SHOW_PRICE_ERROR_FOR_ADD_CART = "show.price.error.for.add.to.cart";

	private static final Logger LOG = LoggerFactory.getLogger(AddToCartController.class);

	@Resource(name = "cartFacade")
	private SABMCartFacade cartFacade;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "groupCartModificationListPopulator")
	private GroupCartModificationListPopulator groupCartModificationListPopulator;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "asahiBonusCartValidationStrategy")
	private AsahiBonusCartValidationStrategy asahiBonusCartValidationStrategy;

	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The apb B2B unit service. */
	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@PostMapping(value = "/cart/add", produces = "application/json")
	public String addToCart(@RequestParam("productCodePost") final String code,
			@RequestParam(value = "action", defaultValue = "") final String action, final Model model,
			@Valid final AddToCartForm form, final BindingResult bindingErrors, final HttpServletRequest request)
	{
		final UserModel user = getUserService().getCurrentUser();
		String accessType = null;
		
		if (user.getUid().equalsIgnoreCase(ANONYMOUS))
		{
			return REDIRECT_PREFIX + "/login";
		}
		else
		{
			if (asahiSiteUtil.isSga())
			{
				if (asahiCoreUtil.isSessionUserCreditBlock())
				{
					if(user instanceof B2BCustomerModel)
					{
						accessType = apbB2BUnitService.getSamAccessTypeForCustomer((B2BCustomerModel)user);
					}
					
					
					if (ApbCoreConstants.PAY_AND_ORDER_ACCESS.equalsIgnoreCase(accessType))
					{
						model.addAttribute(ERROR_MSG_TYPE, ApbFacadesConstants.SGA_ORDER_AND_PAY_BLOCK_ERROR);
						GlobalMessages.addErrorMessage(model, ApbFacadesConstants.SGA_ORDER_AND_PAY_BLOCK_ERROR);
					}
					else if (ApbCoreConstants.ORDER_ACCESS.equalsIgnoreCase(accessType))
					{
						model.addAttribute(ERROR_MSG_TYPE, ApbFacadesConstants.SGA_ORDER_ONLY_BLOCK_ERROR);
						GlobalMessages.addErrorMessage(model, ApbFacadesConstants.SGA_ORDER_ONLY_BLOCK_ERROR);
					}
					else if (ApbCoreConstants.PAY_ACCESS.equalsIgnoreCase(accessType))
					{
						model.addAttribute(ERROR_MSG_TYPE, ApbFacadesConstants.SGA_PAY_ONLY_BLOCK_ERROR);
						GlobalMessages.addErrorMessage(model, ApbFacadesConstants.SGA_PAY_ONLY_BLOCK_ERROR);
					}
					else {
					model.addAttribute(ERROR_MSG_TYPE, ApbFacadesConstants.SGA_CREDTI_BLOCK_ERROR);
					GlobalMessages.addErrorMessage(model, ApbFacadesConstants.SGA_CREDTI_BLOCK_ERROR);
					}
					return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
				}
				
				else if(asahiCoreUtil.isNAPUser())
				{
					GlobalMessages.addErrorMessage(model, ApbFacadesConstants.NAP_CART_ERROR);
					return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
					
				}

				final ProductData prodData = productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC));
				if (!prodData.getActive() || !inclusionExclusionProductStrategy.isProductIncluded(prodData.getCode()))
				{
					GlobalMessages.addErrorMessage(model, ApbFacadesConstants.NO_INDIVIDUAL_LINE_ITEM_MESSAGE);
					return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
				}
			}


			if (bindingErrors.hasErrors())
			{
				return getViewWithBindingErrorMessages(model, bindingErrors);
			}

			final Long qty = form.getQty();

			if (qty <= 0)
			{
				model.addAttribute(ERROR_MSG_TYPE, "basket.error.quantity.invalid");
				model.addAttribute(QUANTITY_ATTR, Long.valueOf(0L));
			}
			else
			{
				try
				{
					final CartModificationData cartModification = cartFacade.addToCart(code, qty, action);
					model.addAttribute(QUANTITY_ATTR, Long.valueOf(cartModification.getQuantityAdded()));
					model.addAttribute("entry", cartModification.getEntry());
					model.addAttribute("cartCode", cartModification.getCartCode());
					model.addAttribute("isQuote", Boolean.FALSE);

					request.getSession().setAttribute("wasCheckoutInterfce", false);

					if (cartModification.getQuantityAdded() == 0L)
					{
						model.addAttribute(ERROR_MSG_TYPE,
								"basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());

					}
					else if (cartModification.getQuantityAdded() < qty)
					{
						model.addAttribute(ERROR_MSG_TYPE,
								"basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
					}
					else
					{
						final CartData cartData = cartFacade.getSessionCartWithEntryOrdering(false);
						if (!cartData.getPriceUpdated() && showPriceErrorConfigured())
						{
							//GlobalMessages.addErrorMessage(model, "price.not.fetched.services.msg");
							model.addAttribute(ERROR_MSG_TYPE,asahiSiteUtil.isApb()? "apb.price.not.fetched.services.msg":"price.not.fetched.services.msg");
							model.addAttribute("priceError", true);
						}
					}
				}
				catch (final CommerceCartModificationException ex)
				{
					logDebugException(ex);
					model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
					model.addAttribute(QUANTITY_ATTR, Long.valueOf(0L));
				}
			}
			// model.addAttribute("product", productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC)));
		}
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	/**
	 * This method get Allowed Bonus Products that can be added in the cart
	 *
	 * @param productCode
	 * @return
	 */
	@GetMapping(value = "/getAllowedBonusProducts", produces = "application/json")
	@ResponseBody
	public long getAllowedBonusProducts(@RequestParam("productCode") final String productCode)
	{
		if ((asahiSiteUtil.isApb() && assistedServiceFacade.isAssistedServiceModeLaunched()) ||(asahiSiteUtil.isSga() && asahiSiteUtil.isBDECustomer()))
		{
			return asahiBonusCartValidationStrategy.getAllowedBonusQuantity(productCode);
		}
		return 0;
	}


	private boolean showPriceErrorConfigured()
	{
		return Boolean.parseBoolean(asahiConfigurationService.getString(SHOW_PRICE_ERROR_FOR_ADD_CART, "true"));
	}

	protected String getViewWithBindingErrorMessages(final Model model, final BindingResult bindingErrors)
	{
		for (final ObjectError error : bindingErrors.getAllErrors())
		{
			if (isTypeMismatchError(error))
			{
				model.addAttribute(ERROR_MSG_TYPE, QUANTITY_INVALID_BINDING_MESSAGE_KEY);
			}
			else
			{
				model.addAttribute(ERROR_MSG_TYPE, error.getDefaultMessage());
			}
		}
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	protected boolean isTypeMismatchError(final ObjectError error)
	{
		return error.getCode().equals(TYPE_MISMATCH_ERROR_CODE);
	}

	@PostMapping(value = "/cart/addGrid", produces = MediaType.APPLICATION_JSON_VALUE)
	public final String addGridToCart(@RequestBody final AddToCartOrderForm form, final Model model)
	{
		if (getUserService().getCurrentUser().getUid().equalsIgnoreCase(ANONYMOUS))
		{
			return REDIRECT_PREFIX + "/login";
		}
		else
		{
			final Set<String> multidErrorMsgs = new HashSet<String>();
			final List<CartModificationData> modificationDataList = new ArrayList<CartModificationData>();

			for (final OrderEntryData cartEntry : form.getCartEntries())
			{
				if (!isValidProductEntry(cartEntry))
				{
					LOG.error("Error processing entry");
				}
				else if (!isValidQuantity(cartEntry))
				{
					multidErrorMsgs.add("basket.error.quantity.invalid");
				}
				else
				{
					final String errorMsg = addEntryToCart(modificationDataList, cartEntry, true);
					if (StringUtils.isNotEmpty(errorMsg))
					{
						multidErrorMsgs.add(errorMsg);
					}

				}
			}

			if (CollectionUtils.isNotEmpty(modificationDataList))
			{
				groupCartModificationListPopulator.populate(null, modificationDataList);

				model.addAttribute("modifications", modificationDataList);
			}

			if (CollectionUtils.isNotEmpty(multidErrorMsgs))
			{
				model.addAttribute("multidErrorMsgs", multidErrorMsgs);
			}
			model.addAttribute("numberShowing", Integer.valueOf(Config.getInt(SHOWN_PRODUCT_COUNT, 3)));
		}
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	@RequestMapping(value = "/entrygroups/cart/addToEntryGroup", method =
	{ RequestMethod.POST, RequestMethod.GET })
	public String addEntryGroupToCart(final Model model, @Valid final AddToEntryGroupForm form, final BindingResult bindingErrors)
	{
		if (getUserService().getCurrentUser().getUid().equalsIgnoreCase(ANONYMOUS))
		{
			return REDIRECT_PREFIX + "/login";
		}
		else
		{
			if (bindingErrors.hasErrors())
			{
				return getViewWithBindingErrorMessages(model, bindingErrors);
			}
			final long qty = 1;
			try
			{
				final AddToCartParams addToCartParams = new AddToCartParams();
				addToCartParams.setEntryGroupNumbers(new HashSet(Collections.singletonList(form.getEntryGroupNumber())));
				addToCartParams.setProductCode(form.getProductCode());
				addToCartParams.setQuantity(qty);
				addToCartParams.setStoreId(null);
				final CartModificationData cartModification = cartFacade.addToCart(addToCartParams);
				model.addAttribute(QUANTITY_ATTR, Long.valueOf(cartModification.getQuantityAdded()));
				model.addAttribute("entry", cartModification.getEntry());
				model.addAttribute("cartCode", cartModification.getCartCode());

				if (cartModification.getQuantityAdded() == 0L)
				{
					model.addAttribute(ERROR_MSG_TYPE, "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
				}
				else if (cartModification.getQuantityAdded() < qty)
				{
					model.addAttribute(ERROR_MSG_TYPE,
							"basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
				}
			}
			catch (final CommerceCartModificationException ex)
			{
				logDebugException(ex);
				model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
				model.addAttribute(QUANTITY_ATTR, Long.valueOf(0L));
			}
			model.addAttribute("product",
					productFacade.getProductForCodeAndOptions(form.getProductCode(), Arrays.asList(ProductOption.BASIC)));
		}
		return REDIRECT_PREFIX + "/cart";
	}

	protected ProductWrapperData createProductWrapperData(final String sku, final String errorMsg)
	{
		final ProductWrapperData productWrapperData = new ProductWrapperData();
		final ProductData productData = new ProductData();
		productData.setCode(sku);
		productWrapperData.setProductData(productData);
		productWrapperData.setErrorMsg(errorMsg);
		return productWrapperData;
	}

	protected void logDebugException(final Exception ex)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Exception occurred...", ex);
		}
	}

	protected String addEntryToCart(final List<CartModificationData> modificationDataList, final OrderEntryData cartEntry,
			final boolean isReducedQtyError)
	{
		String errorMsg = StringUtils.EMPTY;
		try
		{
			final long qty = cartEntry.getQuantity().longValue();
			final CartModificationData cartModificationData = cartFacade.addToCart(cartEntry.getProduct().getCode(), qty);
			if (cartModificationData.getQuantityAdded() == 0L)
			{
				errorMsg = "basket.information.quantity.noItemsAdded." + cartModificationData.getStatusCode();
			}
			else if (cartModificationData.getQuantityAdded() < qty && isReducedQtyError)
			{
				errorMsg = "basket.information.quantity.reducedNumberOfItemsAdded." + cartModificationData.getStatusCode();
			}

			modificationDataList.add(cartModificationData);

		}
		catch (final CommerceCartModificationException ex)
		{
			errorMsg = "basket.error.occurred";
			logDebugException(ex);
		}
		return errorMsg;
	}

	protected boolean isValidProductEntry(final OrderEntryData cartEntry)
	{
		return cartEntry.getProduct() != null && StringUtils.isNotBlank(cartEntry.getProduct().getCode());
	}

	protected boolean isValidQuantity(final OrderEntryData cartEntry)
	{
		return cartEntry.getQuantity() != null && cartEntry.getQuantity().longValue() >= 1L;
	}

	public UserService getUserService()
	{
		return userService;
	}

}
