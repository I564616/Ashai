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

import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.apb.core.service.message.AsahiMessageService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.storefront.controllers.ControllerConstants;
import com.sabmiller.facades.cart.SABMCartFacade;


/**
 * Controller for MiniCart functionality which is not specific to a page.
 */
@Controller
public class MiniCartController extends AbstractController
{
	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String TOTAL_DISPLAY_PATH_VARIABLE_PATTERN = "{totalDisplay:.*}";
	private static final String COMPONENT_UID_PATH_VARIABLE_PATTERN = "{componentUid:.*}";
	private static final String EMPTY_CART_MESSAGE = "asahi.empty.cart.message.apb";

	@Resource(name = "cartFacade")
	private SABMCartFacade cartFacade;
	
	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@Resource(name = "asahiMessageService")
	private AsahiMessageService asahiMessageService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@GetMapping("/cart/miniCart/" + TOTAL_DISPLAY_PATH_VARIABLE_PATTERN)
	public String getMiniCart(@PathVariable final String totalDisplay, final Model model)
	{
		if(asahiCoreUtil.isNAPUserForSite())
		{
			return "";
		}
		final CartData cartData = cartFacade.getMiniCart();
		model.addAttribute("totalPrice", cartData.getTotalPrice());
		model.addAttribute("subTotal", cartData.getSubTotal());
		model.addAttribute("minicartSubTotal", cartData.getMinicartSubTotal());
		if (cartData.getDeliveryCost() != null)
		{
			final PriceData withoutDelivery = cartData.getDeliveryCost();
			withoutDelivery.setValue(cartData.getTotalPrice().getValue().subtract(cartData.getDeliveryCost().getValue()));
			model.addAttribute("totalNoDelivery", withoutDelivery);
		}
		else
		{
			model.addAttribute("totalNoDelivery", cartData.getTotalPrice());
		}

		model.addAttribute("totalItems", cartData.getTotalUnitCount());
		model.addAttribute("totalDisplay", totalDisplay);
		return ControllerConstants.Views.Fragments.Cart.MiniCartPanel;
	}

	@GetMapping("/cart/rollover/" + COMPONENT_UID_PATH_VARIABLE_PATTERN)
	public String rolloverMiniCartPopup(@PathVariable final String componentUid, final Model model) throws CMSItemNotFoundException
	{
		
		if(asahiCoreUtil.isNAPUserForSite())
		{
			return "";
		}
		
		CartData cartData;
		if (ControllerConstants.Views.Fragments.Cart.FETCH_DYNAMIC_PRICE_FOR_MINI_CART)
		{
			cartData = cartFacade.getSessionCart();
		}
		else
		{
			cartData = cartFacade.getMiniCart();
		}
		model.addAttribute("cartData", cartData);

		final MiniCartComponentModel component = (MiniCartComponentModel) cmsComponentService.getSimpleCMSComponent(componentUid);

		final List entries = cartData.getEntries();
		if (entries != null)
		{
			Collections.reverse(entries);
			if(asahiSiteUtil.isSga() && entries.size() > component.getShownProductCount()) 
			{
			 final int remaingUnvProd = cartData.getUnavProdCount()- cartFacade.getRemainUnavProd(entries,Integer.valueOf(component.getShownProductCount()));
			// No of Products Unavailable apart from which are showing in MiniCart
			model.addAttribute("unavProdCount", remaingUnvProd);
			}
			model.addAttribute("entries", entries);
			model.addAttribute("numberItemsInCart", Integer.valueOf(entries.size()));
			model.addAttribute("forceEnableCheckout", CollectionUtils.isNotEmpty(cartData.getRootGroups()));
			if (entries.size() < component.getShownProductCount())
			{
				model.addAttribute("numberShowing", Integer.valueOf(entries.size()));
			}
			else
			{
				model.addAttribute("numberShowing", Integer.valueOf(component.getShownProductCount()));
			}
		}

		//min order quantity check for sga
		model.addAttribute("minOrderQtyCheck", false);
		if (asahiSiteUtil.isSga())
		{
			final boolean disableCheckoutButton = cartFacade.validateMinOrderQuantity(cartData);
			//final boolean hasOnlyBonusProduct = cartFacade.isBonusStockProductsInCart();
			if (disableCheckoutButton)
			{
				disableAddToCart(model);
			}
		}

		model.addAttribute("lightboxBannerComponent", component.getLightboxBannerComponent());
		model.addAttribute("emptyCartMessage", this.asahiMessageService.getString(EMPTY_CART_MESSAGE, ""));

		return ControllerConstants.Views.Fragments.Cart.CartPopup;
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
	}
}
