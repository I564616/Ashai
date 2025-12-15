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

import de.hybris.platform.acceleratorfacades.product.data.ProductWrapperData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;

import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.data.QuickOrderData;
import com.apb.facades.order.data.AsahiQuickOrderData;
import com.apb.storefront.controllers.pages.ApbAbstractPageController;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.util.Config;
import web.src.com.apb.storefront.forms.AsahiQuickOrderForm;

import com.apb.storefront.controllers.ControllerConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 *
 */
@Controller
@RequestMapping(value = "/quickOrder")
public class QuickOrderPageController extends ApbAbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(QuickOrderPageController.class);
	
	private static final String QUICKORDER_NUMBER_MONTHS = "quickorder.number.month.sga";
	private static final String QUICKORDER_NUMBER_ORDERS_SHOW = "quickorder.number.order.show.sga";
	private static final String ERROR_MSG_TYPE = "errorMsg";
	private static final String CART_REDIRECT = "/cart";
	
	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "orderFacade")
	private SABMOrderFacade sabmOrderFacade;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "asahiCoreUtil")
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource(name = "sabmCartFacade")
	private SABMCartFacade sabmCartFacade;
	
	@GetMapping
	public String getQuickOrderPage(@RequestParam(value = "sort",required = false) final String sortCode,final Model model) throws CMSItemNotFoundException // NOSONAR
	{
		
		if (asahiCoreUtil.isNAPUserForSite()) {
			return FORWARD_PREFIX + "/404";
			// throw new CMSItemNotFoundException("Not allowed access since this user is a
			// part of NAP group");

		}
		
		storeCmsPageInModel(model, getContentPageForLabelOrId("quickOrderPage"));
		
		final Integer noOfMonths = Integer.parseInt(this.asahiConfigurationService.getString(QUICKORDER_NUMBER_MONTHS, "3"));
		final Integer noOfOrders = Integer.parseInt(this.asahiConfigurationService.getString(QUICKORDER_NUMBER_ORDERS_SHOW, "6"));
		
		if((getContentPageForLabelOrId("quickOrderPage")).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId("quickOrderPage")).getBackgroundImage().getURL());
		}

	
		model.addAttribute("noOfMonths",noOfMonths);
		model.addAttribute("noOfOrders",noOfOrders);
		model.addAttribute("isCartEmpty",sabmCartFacade.isCartEmpty());
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.quickOrder"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		
		final AsahiQuickOrderData quickOrderData = sabmOrderFacade.getQuickOrders(sortCode);
		model.addAttribute("quickOrderData",quickOrderData);
		if(asahiSiteUtil.isSga() && null != quickOrderData && null != quickOrderData.getShowExclusionError() && quickOrderData.getShowExclusionError().booleanValue())
		{
			if(null != quickOrderData.getAllProductExcluded() &&  quickOrderData.getAllProductExcluded().booleanValue())
			{
				GlobalMessages.addErrorMessage(model, "sga.allunavailable.error.message");
			}
			else{
				GlobalMessages.addErrorMessage(model, "sga.orderdetails.exclusion.error.message");
			}
		}
		return ControllerConstants.Views.Pages.QuickOrder.QuickOrderPage;
	}
	
	/**
	 * This Method adds the list of products for quick order.
	 */
	@RequireHardLogIn
	@ResponseBody
	@PostMapping("/cart/addQuickOrder")
	public final String addQuickOrderDataToCart(@RequestBody final AsahiQuickOrderForm form)
	{
		if(asahiSiteUtil.isSga())
		{
			
			if (asahiCoreUtil.isNAPUser()) {
				
				return CART_REDIRECT; 
			}
			
			if(form.getClearCart() && sabmCartFacade.hasSessionCart()){
				sabmCartFacade.removeSessionCart();
			}
			List<QuickOrderData> quickOrderDatas = form.getQuickOrderDataList();
			
			if(CollectionUtils.isNotEmpty(quickOrderDatas)){
				for(QuickOrderData quickOrderData : quickOrderDatas){
					try
					{
						sabmCartFacade.addToCart(quickOrderData.getCode(), Long.valueOf(quickOrderData.getQuantity()));
					}
					catch (final CommerceCartModificationException ex)
					{
						LOG.debug("Exception occurred...", ex);
					}
					}
				}
			}
			
		return CART_REDIRECT;
	}
	

	@GetMapping(value = "/productInfo", produces = "application/json")
	@ResponseBody
	public ProductWrapperData getProductInfo(@RequestParam("code") final String code)
	{
		ProductData productData = null;
		String errorMsg = null;
		try
		{
			productData = productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
					ProductOption.URL, ProductOption.STOCK, ProductOption.VARIANT_MATRIX_BASE, ProductOption.VARIANT_MATRIX_URL,
					ProductOption.VARIANT_MATRIX_MEDIA));
			if (Boolean.FALSE.equals(productData.getPurchasable()))
			{
				errorMsg = getErrorMessage("text.quickOrder.product.not.purchaseable", null);
			}
		}
		catch (final IllegalArgumentException iae)
		{
			errorMsg = getErrorMessage("text.quickOrder.product.not.unique", null);
			logDebugException(iae);
		}
		catch (final UnknownIdentifierException uie)
		{
			errorMsg = getErrorMessage("text.quickOrder.product.not.found", null);
			logDebugException(uie);
		}

		return createProductWrapperData(productData, errorMsg);
	}

	protected void logDebugException(final Exception ex)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Exception occurred...", ex);
		}
	}

	protected String getErrorMessage(final String messageKey, final Object[] args)
	{
		return getMessageSource().getMessage(messageKey, args, getI18nService().getCurrentLocale());
	}
	
	protected ProductWrapperData createProductWrapperData(final ProductData productData, final String errorMsg)
	{
		final ProductWrapperData productWrapperData = new ProductWrapperData();
		productWrapperData.setProductData(productData);
		productWrapperData.setErrorMsg(errorMsg);
		return productWrapperData;
	}

	
}