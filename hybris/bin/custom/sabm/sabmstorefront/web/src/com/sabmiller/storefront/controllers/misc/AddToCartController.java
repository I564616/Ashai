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
package com.sabmiller.storefront.controllers.misc;

import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.facades.recommendation.SABMRecommendationFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartOrderForm;
import de.hybris.platform.commercefacades.order.converters.populator.GroupCartModificationListPopulator;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.form.SABMAddToCartForm;
import com.apb.core.util.AsahiCoreUtil;


/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
@Scope("tenant")
public class AddToCartController extends AbstractController
{
	private static final String TYPE_MISMATCH_ERROR_CODE = "typeMismatch";
	private static final String ERROR_MSG_TYPE = "errorMsg";
	private static final String QUANTITY_INVALID_BINDING_MESSAGE_KEY = "basket.error.quantity.invalid.binding";
	private static final String SHOWN_PRODUCT_COUNT = "sabmstorefront.storefront.minicart.shownProductCount";

	private static final Logger LOG = LoggerFactory.getLogger(AddToCartController.class);

	@Resource(name = "cartFacade")
	private SABMCartFacade cartFacade;

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "groupCartModificationListPopulator")
	private GroupCartModificationListPopulator groupCartModificationListPopulator;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;


	@Resource(name = "recommendationFacade")
	private SABMRecommendationFacade recommendationFacade;

	@PostMapping(value = "/cart/add", produces = "application/json")
	public String addToCartForm(final Model model, @Valid final SABMAddToCartForm form, final BindingResult bindingErrors, final HttpServletRequest request,
			@RequestParam(value = "listOriginPos", required = false) final Integer listOriginPos)
	{
		model.addAttribute("requestOrigin", SabmUtils.getRequestOrigin(request.getHeader(SabmUtils.REFERER_KEY), SabmUtils.HOME));
		model.addAttribute("listOriginPos", listOriginPos);
		return addToCart(model, form, bindingErrors);
	}

	@PostMapping(value = "/cart/addAjax", produces = "application/json")
	public String addToCartAjax(final Model model, @Valid @RequestBody final SABMAddToCartForm form,
			final BindingResult bindingErrors)
	{
		return addToCart(model, form, bindingErrors);
	}
	
	@PostMapping(value = "/cart/view", produces = "application/json")
	public String viewMiniCart(final Model model)
	{
		model.addAttribute("cartData", cartFacade.getSessionMiniCart());
		return ControllerConstants.Views.Fragments.Cart.ViewCartPopup;
	}
	
	protected String addToCart(final Model model, final SABMAddToCartForm form, final BindingResult bindingErrors)
	{
		if(!asahiCoreUtil.isNAPUser()) {
			if (bindingErrors.hasErrors()) {
				return getViewWithBindingErrorMessages(model, bindingErrors);
			}
			final String code = form.getProductCodePost();
			final long qty = form.getQty();

			LOG.debug("addToCart: product code={}, qty={}", code, qty);

			if (qty <= 0) {
				model.addAttribute(ERROR_MSG_TYPE, "basket.error.quantity.invalid");
				model.addAttribute("quantity", Long.valueOf(0L));
			} else {
				try {
					addToCartReal(model, code, qty, form);
					recommendationFacade.checkProductForRecommendation(code, Long.valueOf(qty).intValue(), form.getUnit());
					model.addAttribute("recommendationsCount", recommendationFacade.getRecommendations().size());
				} catch (final CommerceCartModificationException ex) {
					model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
					model.addAttribute("quantity", Long.valueOf(0L));
					LOG.error("Failed to invoke add to cart by productCodePost [{}] ", code, ex);
				}
			}
			model.addAttribute("cartData", cartFacade.getSessionMiniCart());

			try {
				model.addAttribute("product", productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES)));
			} catch (UnknownIdentifierException | IllegalArgumentException e) {
				LOG.error("Unable to get product details for tag manager", e);
			}
		}
		
		
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}
	
	
	

	@PostMapping("/cart/add/smartOrder")
	public String addSmartOrderToCart(final Model model, @RequestBody final List<SABMAddToCartForm> form,
			final BindingResult bindingErrors)
	{
		if (bindingErrors.hasErrors())
		{
			return getViewWithBindingErrorMessages(model, bindingErrors);
		}

		for (final SABMAddToCartForm product : form)
		{
			try
			{
				final String invalidProduct = cartFacade.validateProductsBeforeAddtoCart(product.getProductCodePost());
				if (!StringUtils.isEmpty(invalidProduct))
				{
					continue;
				}
				else if (product.getQty() > 0L)
				{
					addToCartReal(model, product.getProductCodePost(), product.getQty(), product);
				}
			}
			catch (final CommerceCartModificationException ex)
			{
				model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
				model.addAttribute("quantity", Long.valueOf(0L));
				LOG.error("Failed to invoke add to cart by smartOrder [{}] ", form, ex);
			}
		}
		model.addAttribute("cartData", cartFacade.getSessionMiniCart());
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}


	@PostMapping(value = "/cart/add/deal", produces = MediaType.APPLICATION_JSON_VALUE)
	public String addDealToCart(final Model model, @Valid @RequestBody final SABMAddToCartForm form,
			final BindingResult bindingErrors, final HttpServletRequest request, 
			@RequestParam(value = "listOriginPos", required = false) final Integer listOriginPos)
	{
		if (bindingErrors.hasErrors())
		{
			return getViewWithBindingErrorMessages(model, bindingErrors);
		}

		try
		{
			DealJson dealJson = addDealToCartReal(model, form);

			if (dealJson != null)
			{
				if (dealJson.getRemainingQty() != null)
				{
					model.addAttribute("remainingQty", dealJson.getRemainingQty());
				}
				if (dealJson.getRemainingValue() != null)
				{
					model.addAttribute("remainingValue", dealJson.getRemainingValue());
				}
			}
		}
		catch (final CommerceCartModificationException ex)
		{
			model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
			model.addAttribute("quantity", Long.valueOf(0L));
			LOG.error("Failed to invoke add to cart by dealCode [{}] ", form.getDealCode(), ex);
		}
		model.addAttribute("cartData", cartFacade.getSessionMiniCart());

		model.addAttribute("requestOrigin", SabmUtils.getRequestOrigin(request.getHeader(SabmUtils.REFERER_KEY), SabmUtils.HOME));
		model.addAttribute("listOriginPos", listOriginPos);
		
		model.addAttribute("pageType", SABMWebConstants.PageType.DEAL.name());
		
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	@PostMapping(value = "/cart/add/partialdeal", produces = MediaType.APPLICATION_JSON_VALUE)
	public String addPartialDealToCart(final Model model, @Valid @RequestBody final List<SABMAddToCartForm> forms,
			final BindingResult bindingErrors)
	{
		if (CollectionUtils.isNotEmpty(forms))
		{
			for (final SABMAddToCartForm sabmAddToCartForm : forms)
			{
				try
				{
					addDealToCartReal(model, sabmAddToCartForm);
				}
				catch (final CommerceCartModificationException ex)
				{
					model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
					model.addAttribute("quantity", Long.valueOf(0L));
					LOG.error("Failed to invoke add to cart by dealCode [{}] ", sabmAddToCartForm.getDealCode(), ex);
				}
			}
		}
		model.addAttribute("cartData", cartFacade.getSessionMiniCart());

		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	/**
	 * Add deal to cart
	 *
	 * @param model
	 * @param form
	 * @throws CommerceCartModificationException
	 */
	private DealJson addDealToCartReal(final Model model, final SABMAddToCartForm form) throws CommerceCartModificationException
	{
		final List<SABMAddToCartForm> baseProducts = form.getBaseProducts();
		final List<String> invalidProducts = new ArrayList<String>();
	SABMRecommendationModel dealRecommendation = recommendationFacade.getRecommendationByDealID(form.getDealCode());
		Boolean isDealHaveRecommendation = true;
		if (CollectionUtils.isNotEmpty(baseProducts))
		{
			for (final SABMAddToCartForm product : baseProducts)
			{
				final String invalidProduct = cartFacade.validateProductsBeforeAddtoCart(product.getProductCodePost());
				if (!StringUtils.isEmpty(invalidProduct))
				{
					invalidProducts.add(invalidProduct);
					continue;
				}
				if (product.getQty() > 0L)
				{
					addToCartReal(model, product.getProductCodePost(), product.getQty(), product);
					if(isDealHaveRecommendation && !recommendationFacade.checkDealForRecommendation(dealRecommendation,
							product.getProductCodePost(), Long.valueOf(product.getQty()).intValue(),product.getUnit())){
						isDealHaveRecommendation = false;
					}
				}
			}
			if(dealRecommendation!=null && isDealHaveRecommendation){
				recommendationFacade.updateRecommendationStatus(dealRecommendation, RecommendationStatus.ACCEPTED);
				model.addAttribute("recommendationsCount", recommendationFacade.getRecommendations().size());
			}
			if (CollectionUtils.isNotEmpty(invalidProducts))
			{
				model.addAttribute("errorMessageForOrder", "order.global.addToCart.error");
				model.addAttribute("productTitles", invalidProducts);
			}
			else
			{
				return cartFacade.addApplyDealToCart(form.getDealCode(), DealConditionStatus.MANUAL);
			}
		}
		else
		{
			final String invalidProduct = cartFacade.validateProductsBeforeAddtoCart(form.getProductCodePost());
			if (!StringUtils.isEmpty(invalidProduct))
			{
				invalidProducts.add(invalidProduct);
			}
			else
			{
				addToCartReal(model, form.getProductCodePost(), form.getQty(), form);
			}
			if (CollectionUtils.isNotEmpty(invalidProducts))
			{
				model.addAttribute("errorMessageForOrder", "order.global.addToCart.error");
				model.addAttribute("productTitles", invalidProducts);
			}
		}

		return null;
	}

	/**
	 * Really add to cart action
	 *
	 * @param model
	 *           This front-end display object
	 * @param code
	 *           This is product code
	 * @param qty
	 *           Add quantity
	 * @param form
	 *           This is add to cart form
	 * @throws CommerceCartModificationException
	 */
	private void addToCartReal(final Model model, final String code, final long qty, final SABMAddToCartForm form)
			throws CommerceCartModificationException
	{
		CartModificationData cartModification = null;
		if (StringUtils.isNotEmpty(form.getUnit()))
		{
			cartModification = cartFacade.addToCart(code, form.getUnit(), qty);
		}
		else
		{
			cartModification = cartFacade.addToCart(code, qty);
		}

		if (StringUtils.isNotBlank(form.getSmartRecommendationModel())) {
			final int entryNumber = cartModification.getEntry().getEntryNumber();
			cartFacade.setSmartRecommendationModelToEntry(form.getSmartRecommendationModel(), entryNumber);
		}

		model.addAttribute("quantity", Long.valueOf(cartModification.getQuantityAdded()));
		model.addAttribute("entry", cartModification.getEntry());
		model.addAttribute("cartCode", cartModification.getCartCode());

		LOG.debug("addToCartReal: quantity={}", cartModification.getQuantityAdded());
		LOG.debug("addToCartReal: cartModification.getStatusCode={}", cartModification.getStatusCode());
		
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
				try
				{
					final long qty = cartEntry.getQuantity().longValue();
					final CartModificationData cartModificationData = cartFacade.addToCart(cartEntry.getProduct().getCode(), qty);
					if (cartModificationData.getQuantityAdded() == 0L)
					{
						multidErrorMsgs.add("basket.information.quantity.noItemsAdded." + cartModificationData.getStatusCode());
					}
					else if (cartModificationData.getQuantityAdded() < qty)
					{
						multidErrorMsgs
								.add("basket.information.quantity.reducedNumberOfItemsAdded." + cartModificationData.getStatusCode());
					}

					modificationDataList.add(cartModificationData);

				}
				catch (final CommerceCartModificationException ex)
				{
					multidErrorMsgs.add("basket.error.occurred");
					LOG.error("Failed to invoke addGrid to cart by cart [{}] ", form.getCartEntries(), ex);
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


		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	protected boolean isValidProductEntry(final OrderEntryData cartEntry)
	{
		return cartEntry.getProduct() != null && StringUtils.isNotBlank(cartEntry.getProduct().getCode());
	}

	protected boolean isValidQuantity(final OrderEntryData cartEntry)
	{
		return cartEntry.getQuantity() != null && cartEntry.getQuantity().longValue() >= 1L;
	}
}
