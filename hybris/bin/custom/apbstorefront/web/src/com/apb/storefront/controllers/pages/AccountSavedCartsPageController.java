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

import de.hybris.platform.acceleratorfacades.ordergridform.OrderGridFormFacade;
import de.hybris.platform.acceleratorfacades.product.data.ReadOnlyOrderGridData;
import de.hybris.platform.acceleratorservices.enums.ImportStatus;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RestoreSaveCartForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SaveCartForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.RestoreSaveCartFormValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.SaveCartFormValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderTemplateData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import com.apb.storefront.forms.ProductOrderTemplateForm;

import com.apb.core.model.OrderTemplateModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.cart.AsahiSaveCartFacade;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.OrderTemplateEntryReorderForm;
import com.apb.storefront.forms.OrderTemplateReorderForm;
import com.apb.storefront.data.ErrorDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.util.AsahiCoreUtil;


/**
 * Controller for saved carts page
 */
@Controller
@RequestMapping("/my-account/saved-carts")
public class AccountSavedCartsPageController extends AbstractSearchPageController
{
	private static final String MY_ACCOUNT_SAVED_CARTS_URL = "/my-account/saved-carts";
	private static final String REDIRECT_TO_SAVED_CARTS_PAGE = REDIRECT_PREFIX + MY_ACCOUNT_SAVED_CARTS_URL;

	private static final String SAVED_CARTS_CMS_PAGE = "saved-carts";
	private static final String SAVED_CART_DETAILS_CMS_PAGE = "savedCartDetailsPage";

	private static final String SAVED_CART_CODE_PATH_VARIABLE_PATTERN = "{cartCode:.*}";

	private static final String REFRESH_UPLOADING_SAVED_CART = "refresh.uploading.saved.cart";
	private static final String REFRESH_UPLOADING_SAVED_CART_INTERVAL = "refresh.uploading.saved.cart.interval";
	private static final String SAVED_ORDER_TEMPLATE_LIST_SIZE = "saved.order.template.list.size.apb";
	private static final Logger LOG = LoggerFactory.getLogger(AccountSavedCartsPageController.class);
	
	private static final String CART_REDIRECT = "redirect:/cart";
	
	private static final String TEMPLATE_SORT_CODE="A-Z";

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "asahiSaveCartFacade")
	private AsahiSaveCartFacade saveCartFacade;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "orderGridFormFacade")
	private OrderGridFormFacade orderGridFormFacade;

	@Resource(name = "saveCartFormValidator")
	private SaveCartFormValidator saveCartFormValidator;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "restoreSaveCartFormValidator")
	private RestoreSaveCartFormValidator restoreSaveCartFormValidator;
	
	@Resource(name="asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	/** The asahi configuration service. */
	@Resource(name="asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@GetMapping
	@RequireHardLogIn
	public String savedCarts(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		// Handle paged search results
		
		if(asahiCoreUtil.isNAPUserForSite())
		{
			return FORWARD_PREFIX + "/404";
		}
		
		final PageableData pageableData = createPageableData(page, Integer.parseInt(this.asahiConfigurationService.getString(SAVED_ORDER_TEMPLATE_LIST_SIZE, "10")), sortCode, showMode);
		final SearchPageData<OrderTemplateData> searchPageData = this.saveCartFacade.getSavedCartsForCurrentUserB2BUnit(pageableData);
		populateModel(model, searchPageData, showMode);

		model.addAttribute("refreshSavedCart", getSiteConfigService().getBoolean(REFRESH_UPLOADING_SAVED_CART, false));
		model.addAttribute("refreshSavedCartInterval", getSiteConfigService().getLong(REFRESH_UPLOADING_SAVED_CART_INTERVAL, 0));

		storeCmsPageInModel(model, getContentPageForLabelOrId(SAVED_CARTS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SAVED_CARTS_CMS_PAGE));
		if((getContentPageForLabelOrId(SAVED_CARTS_CMS_PAGE)).getBackgroundImage() != null)
			model.addAttribute("media", (getContentPageForLabelOrId(SAVED_CARTS_CMS_PAGE)).getBackgroundImage().getURL());
		
		
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, accountBreadcrumbBuilder.getBreadcrumbs("text.account.savedCarts"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		setAdditionalInfoForSga(model);
		return getViewForPage(model);
	}

	@GetMapping("/" + SAVED_CART_CODE_PATH_VARIABLE_PATTERN)
	@RequireHardLogIn
	public String savedCart(@PathVariable("cartCode") final String cartCode, @RequestParam(value = "sort", required = true,defaultValue = TEMPLATE_SORT_CODE) final String sortCode,
			final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		
		if(asahiCoreUtil.isNAPUserForSite())
		{
			return FORWARD_PREFIX + "/404";
		}
		try
		{
			final CommerceSaveCartParameterData parameter = new CommerceSaveCartParameterData();
			parameter.setCartId(cartCode);
			OrderTemplateData templateData = this.saveCartFacade.getOrderTemplateForCodeAndB2BUnit(cartCode,sortCode);
			//final CommerceSaveCartResultData resultData = saveCartFacade.getCartForCodeAndCurrentUser(parameter);
			//final CartData cartData = resultData.getSavedCartData();
			if(asahiSiteUtil.isSga() &&  null != templateData.getShowExclusionError() && templateData.getShowExclusionError().booleanValue())
			{
				if(null != templateData.getAllProductExcluded() && templateData.getAllProductExcluded().booleanValue())
				{
					GlobalMessages.addErrorMessage(model, "sga.allunavailable.error.message");
				}
				else{
					GlobalMessages.addErrorMessage(model,"sga.template.exclusion.error.message");
				}
			}
			if (ImportStatus.PROCESSING.equals(templateData.getImportStatus()))
			{
				return REDIRECT_TO_SAVED_CARTS_PAGE;
			}
			
			model.addAttribute("savedCartData", templateData);
			if (templateData.getPriceError())
			{
				GlobalMessages.addErrorMessage(model, asahiSiteUtil.isApb()? "apb.price.not.fetched.services.msg":"price.not.fetched.services.msg");
				model.addAttribute("priceError", true);
			}
			else if(templateData.getTemplateEntry().stream().filter(entry -> !entry.isPriceUpdated()).findFirst().isPresent())
			{
				GlobalMessages.addErrorMessage(model, "partial.price.error.services.msg");
				model.addAttribute("partialPriceError", true);
			}
			final SaveCartForm saveCartForm = new SaveCartForm();
			saveCartForm.setDescription(templateData.getDescription());
			saveCartForm.setName(templateData.getName());
			model.addAttribute("saveCartForm", saveCartForm);

			final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
			breadcrumbs.add(new Breadcrumb(MY_ACCOUNT_SAVED_CARTS_URL, getMessageSource().getMessage("text.account.savedCarts",
					null, getI18nService().getCurrentLocale()), null));
			breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.savedCart.savedCartBreadcrumb",
					new Object[]
					{ templateData.getCode() }, "Order Template {0}", getI18nService().getCurrentLocale()), null));
			model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadcrumbs);

		}
		catch (final Exception e)
		{
			LOG.warn("Attempted to load a saved cart that does not exist or is not visible", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
			return REDIRECT_TO_SAVED_CARTS_PAGE;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SAVED_CART_DETAILS_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SAVED_CART_DETAILS_CMS_PAGE));
		if((getContentPageForLabelOrId(SAVED_CART_DETAILS_CMS_PAGE)).getBackgroundImage() != null)
			model.addAttribute("media", (getContentPageForLabelOrId(SAVED_CART_DETAILS_CMS_PAGE)).getBackgroundImage().getURL());
		setAdditionalInfoForSga(model);
		return getViewForPage(model);
	}

	@GetMapping(value = "/uploadingCarts", produces = "application/json")
	@ResponseBody
	@RequireHardLogIn
	public List<CartData> getUploadingSavedCarts(@RequestParam("cartCodes") final List<String> cartCodes)
			throws CommerceSaveCartException
	{
		final List<CartData> result = new ArrayList<CartData>();
		for (final String cartCode : cartCodes)
		{
			final CommerceSaveCartParameterData parameter = new CommerceSaveCartParameterData();
			parameter.setCartId(cartCode);

			final CommerceSaveCartResultData resultData = saveCartFacade.getCartForCodeAndCurrentUser(parameter);
			final CartData cartData = resultData.getSavedCartData();

			if (ImportStatus.COMPLETED.equals(cartData.getImportStatus()))
			{
				result.add(cartData);
			}
		}

		return result;
	}

	@GetMapping("/" + SAVED_CART_CODE_PATH_VARIABLE_PATTERN + "/getReadOnlyProductVariantMatrix")
	@RequireHardLogIn
	public String getProductVariantMatrixForResponsive(@PathVariable("cartCode") final String cartCode,
			@RequestParam("productCode") final String productCode, final Model model, final RedirectAttributes redirectModel)
	{
		try
		{
			final CommerceSaveCartParameterData parameter = new CommerceSaveCartParameterData();
			parameter.setCartId(cartCode);

			final CommerceSaveCartResultData resultData = saveCartFacade.getCartForCodeAndCurrentUser(parameter);
			final CartData cartData = resultData.getSavedCartData();

			final Map<String, ReadOnlyOrderGridData> readOnlyMultiDMap = orderGridFormFacade.getReadOnlyOrderGridForProductInOrder(
					productCode, Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES), cartData);
			model.addAttribute("readOnlyMultiDMap", readOnlyMultiDMap);
			setAdditionalInfoForSga(model);
			return ControllerConstants.Views.Fragments.Checkout.ReadOnlyExpandedOrderForm;
		}
		catch (final CommerceSaveCartException e)
		{
			LOG.warn("Attempted to load a saved cart that does not exist or is not visible", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
			return REDIRECT_TO_SAVED_CARTS_PAGE + "/" + cartCode;
		}
	}

	@PostMapping("/" + SAVED_CART_CODE_PATH_VARIABLE_PATTERN + "/edit")
	@RequireHardLogIn
	public String savedCartEdit(@PathVariable("cartCode") final String cartCode, final SaveCartForm form,
			final BindingResult bindingResult, final RedirectAttributes redirectModel) throws CommerceSaveCartException
	{
		saveCartFormValidator.validate(form, bindingResult);
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, error.getCode());
			}
			redirectModel.addFlashAttribute("saveCartForm", form);
		}
		else
		{
			final CommerceSaveCartParameterData commerceSaveCartParameterData = new CommerceSaveCartParameterData();
			commerceSaveCartParameterData.setCartId(cartCode);
			commerceSaveCartParameterData.setName(form.getName());
			commerceSaveCartParameterData.setDescription(form.getDescription());
			commerceSaveCartParameterData.setEnableHooks(false);
			try
			{
				final CommerceSaveCartResultData saveCartData = saveCartFacade.saveCart(commerceSaveCartParameterData);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
						"text.account.saveCart.edit.success", new Object[]
						{ saveCartData.getSavedCartData().getName() });
			}
			catch (final CommerceSaveCartException csce)
			{
				LOG.error(csce.getMessage(), csce);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"text.account.saveCart.edit.error", new Object[]
						{ form.getName() });
			}
		}
		return REDIRECT_TO_SAVED_CARTS_PAGE + "/" + cartCode;
	}

	@GetMapping("/{cartId}/restore")
	@RequireHardLogIn
	public String restoreSaveCartForId(@PathVariable(value = "cartId") final String cartId, final Model model)
			throws CommerceSaveCartException
	{
		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(cartId);
		final CommerceSaveCartResultData commerceSaveCartResultData = saveCartFacade.getCartForCodeAndCurrentUser(parameters);
		final boolean hasSessionCart = cartFacade.hasEntries();
		model.addAttribute("hasSessionCart", hasSessionCart);
		if (hasSessionCart)
		{
			model.addAttribute("autoGeneratedName", System.currentTimeMillis());
		}
		model.addAttribute(commerceSaveCartResultData);
		return ControllerConstants.Views.Fragments.Account.SavedCartRestorePopup;
	}

	@RequireHardLogIn
	@PostMapping("/{cartId}/restore")
	public @ResponseBody String postRestoreSaveCartForId(@PathVariable(value = "cartId") final String cartId,
			final RestoreSaveCartForm restoreSaveCartForm, final BindingResult bindingResult) throws CommerceSaveCartException
	{
		try
		{
			restoreSaveCartFormValidator.validate(restoreSaveCartForm, bindingResult);
			if (bindingResult.hasErrors())
			{
				return getMessageSource().getMessage(bindingResult.getFieldError().getCode(), null,
						getI18nService().getCurrentLocale());
			}

			if (restoreSaveCartForm.getCartName() != null && !restoreSaveCartForm.isPreventSaveActiveCart()
					&& cartFacade.hasEntries())
			{
				final CommerceSaveCartParameterData commerceSaveActiveCart = new CommerceSaveCartParameterData();
				commerceSaveActiveCart.setCartId(cartFacade.getSessionCart().getCode());
				commerceSaveActiveCart.setName(restoreSaveCartForm.getCartName());
				commerceSaveActiveCart.setEnableHooks(true);
				saveCartFacade.saveCart(commerceSaveActiveCart);
			}

			final CommerceSaveCartParameterData commerceSaveCartParameterData = new CommerceSaveCartParameterData();
			commerceSaveCartParameterData.setCartId(cartId);
			commerceSaveCartParameterData.setEnableHooks(true);
			if (restoreSaveCartForm.isKeepRestoredCart())
			{
				saveCartFacade.cloneSavedCart(commerceSaveCartParameterData);
			}
			saveCartFacade.restoreSavedCart(commerceSaveCartParameterData);
		}
		catch (final CommerceSaveCartException ex)
		{
			LOG.error("Error while restoring the cart for cartId " + cartId + " because of " + ex);
			return getMessageSource().getMessage("text.restore.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return String.valueOf(HttpStatus.OK);
	}

	@DeleteMapping("/{cartId}/delete")
	@ResponseStatus(value = HttpStatus.OK)
	@RequireHardLogIn
	public @ResponseBody String deleteSaveCartForId(@PathVariable(value = "cartId") final String cartId)
			throws CommerceSaveCartException
	{
		try
		{
			final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
			parameters.setCartId(cartId);
			saveCartFacade.flagForDeletion(cartId);
		}
		catch (final CommerceSaveCartException ex)
		{
			LOG.error("Error while deleting the saved cart with cartId " + cartId + " because of " + ex);
			return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return String.valueOf(HttpStatus.OK);
	}
	
	/**
	 * Delete order template for id.
	 *
	 * @param orderTemplateId the order template id
	 * @return the string
	 */
	@DeleteMapping("/{orderTemplateId}/deleteOrderTemplate")
	@ResponseStatus(value = HttpStatus.OK)
	@RequireHardLogIn
	public @ResponseBody String deleteOrderTemplateForId(@PathVariable(value = "orderTemplateId") final String orderTemplateId)
	{
		try
		{
			this.saveCartFacade.deleteOrderTemplateForId(orderTemplateId);
		}
		catch (final Exception ex)
		{
			LOG.error("Error while deleting the saved cart with cartId " + orderTemplateId + " because of " + ex);
			return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return String.valueOf(HttpStatus.OK);
	}
	
	/**
	 * Delete order template entry for PK.
	 *
	 * @param orderTemplateId the order template id
	 * @return the string
	 */
	@DeleteMapping("/{orderTemplateEntry}/deleteOrderTemplateEntry")
	@ResponseStatus(value = HttpStatus.OK)
	@RequireHardLogIn
	public @ResponseBody String deleteOrderTemplateEntryForPK(@PathVariable(value = "orderTemplateEntry") final String orderTemplateId)
	{
		try
		{
			this.saveCartFacade.deleteOrderTemplateEntryForPK(orderTemplateId);
		}
		catch (final Exception ex)
		{
			LOG.error("Error while deleting the saved cart with cartId " + orderTemplateId + " because of " + ex);
			return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return String.valueOf(HttpStatus.OK);
	}
	
	/**
	 * Delete all entries for order template.
	 *
	 * @param orderTemplateId the order template id
	 * @return the string
	 */
	@DeleteMapping("/{orderTemplate}/deleteAllEntriesForOrderTemplate")
	@ResponseStatus(value = HttpStatus.OK)
	@RequireHardLogIn
	public @ResponseBody String deleteAllEntriesForOrderTemplate(@PathVariable(value = "orderTemplate") final String orderTemplateId)
	{
		try
		{
			this.saveCartFacade.deleteAllEntriesForOrderTemplate(orderTemplateId);
		}
		catch (final Exception ex)
		{
			LOG.error("Error while order template with templateId " + orderTemplateId + " because of " + ex);
			return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return String.valueOf(HttpStatus.OK);
	}
	
	/**
	 * Reorder entries for order template.
	 *
	 * @param orderTemplateId the order template id
	 * @return the string
	 */
	@PostMapping("/{orderTemplate}/reorderEntries/keepTemplate")
	@ResponseStatus(value = HttpStatus.OK)
	@RequireHardLogIn
	public @ResponseBody String updateCartWithNewAndExistingEntries(@PathVariable(value = "orderTemplate") final String orderTemplateId)
	{
		try
		{
			this.saveCartFacade.reorderEntriesForOrderTemplate(orderTemplateId,true);
		}
		catch (final Exception ex)
		{
			LOG.error("Error while updating the order template with templateId " + orderTemplateId + " because of " + ex);
			return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return String.valueOf(HttpStatus.OK);
	}
	
	/**
	 * Reorder entries for order template.
	 *
	 * @param orderTemplateId the order template id
	 * @return the string
	 */
	@PostMapping("/{orderTemplate}/reorderEntries/overrideTemplate")
	@ResponseStatus(value = HttpStatus.OK)
	@RequireHardLogIn
	public @ResponseBody String updateCartWithNewEntries(@PathVariable(value = "orderTemplate") final String orderTemplateId)
	{
		try
		{
			this.saveCartFacade.reorderEntriesForOrderTemplate(orderTemplateId,false);
		}
		catch (final Exception ex)
		{
			LOG.error("Error while order template with templateId " + orderTemplateId + " because of " + ex);
			return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return String.valueOf(HttpStatus.OK);
	}
	
	@PostMapping("/reorderEntries/updateEntries")
	@RequireHardLogIn
	public String updateTemplateEntries(final OrderTemplateReorderForm form, final Model model)
	{
		try
		{
			if(CollectionUtils.isNotEmpty(form.getTemplateEntries())){
				Map<String,Long> entryQtyMap = new HashMap<>();
				for(OrderTemplateEntryReorderForm entry:form.getTemplateEntries()){
					entryQtyMap.put(entry.getEntryPK(), entry.getQty());
				}
				this.saveCartFacade.reorderOrderTemplateEntries(form.getTemplateCode(),entryQtyMap,form.isKeepCart());
			}
		}
		catch (final Exception ex)
		{
			LOG.error("Error while order template with templateId " + form.getTemplateCode() + " because of " + ex);
			//return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return CART_REDIRECT;
	}
	
	@PostMapping("/updateTemplate")
	@RequireHardLogIn
	@ResponseBody
	public boolean saveTemplate(final OrderTemplateReorderForm form, final Model model)
	{
		try
		{
			if(CollectionUtils.isNotEmpty(form.getTemplateEntries())){
				Map<String,Long> entryQtyMap = new HashMap<>();
				for(OrderTemplateEntryReorderForm entry:form.getTemplateEntries()){
					entryQtyMap.put(entry.getEntryPK(), entry.getQty());
				}
				final boolean success = this.saveCartFacade.saveOrderTemplate(form.getTemplateCode(),entryQtyMap);
				return success;
			}
		}
		catch (final Exception ex)
		{
			LOG.error("Error while order template with templateId " + form.getTemplateCode() + " because of " + ex);
			//return getMessageSource().getMessage("text.delete.savedcart.error", null, getI18nService().getCurrentLocale());
		}
		return false;
	}
	
	
	@PostMapping("/addProductToTemplate")
	@RequireHardLogIn
	@ResponseBody
	public ErrorDTO saveTemplate(final ProductOrderTemplateForm form, final Model model)
	{
		ErrorDTO errorDTO = new ErrorDTO();
		
		try
		{
				final boolean success = this.saveCartFacade.addProductToOrderTemplate(form.getTemplateCode(),form.getProduct(),Long.valueOf(form.getQuantity()),
						form.isExistingTemplate());
				if(success)
				{
					errorDTO.setError("false");
					errorDTO.setErrorCode(getMessageSource().getMessage("text.product.savedcart.add.success", null, getI18nService().getCurrentLocale()));
					return errorDTO;
				}
		}
		
		catch (final DuplicateUidException ex)
		{
			LOG.error("Template already exists " + form.getTemplateCode());
			errorDTO.setError("true");
			errorDTO.setErrorCode(getMessageSource().getMessage("basket.already.save.cart.on.error", null, getI18nService().getCurrentLocale()));
			return errorDTO;
		}
		catch (final Exception ex)
		{
			LOG.error("Error while adding product to order template with templateId " + form.getTemplateCode() + " because of " + ex);
		}
		errorDTO.setError("true");
		errorDTO.setErrorCode(getMessageSource().getMessage("text.product.savedcart.add.error", null, getI18nService().getCurrentLocale()));
		return errorDTO;
	}
	
	@PostMapping("/getAllOrderTemplate")
	@RequireHardLogIn
	@ResponseBody
	public List<OrderTemplateData> getAllTemplate(final Model model)
	{
		try
		{
				return this.saveCartFacade.getAllSavedCartsForCurrentUserB2BUnit();
		}
		catch (final Exception ex)
		{
			LOG.error("Error while fetching all order template"+ ex);
			return Collections.EMPTY_LIST;
		}
	}
	
	private void setAdditionalInfoForSga(final Model model)
	{
		if(asahiSiteUtil.isSga())
		{
			model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUser());			
			final String accessType = asahiCoreUtil.getCurrentUserAccessType();
			model.addAttribute("isAccessDenied",asahiCoreUtil.isSAMAccessDenied());
			
				final boolean isApprovalPending = asahiCoreUtil.isSAMAccessApprovalPending();
				model.addAttribute("isApprovalPending",isApprovalPending);
				
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
