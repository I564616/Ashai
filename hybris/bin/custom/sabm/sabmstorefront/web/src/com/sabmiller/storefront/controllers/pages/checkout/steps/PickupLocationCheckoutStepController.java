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


import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import com.sabmiller.storefront.controllers.ControllerConstants;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


///NOT USED - DISABLING CONTROLLER

//@Controller
//@RequestMapping(value = "/checkout/multi/pickup-location")
public class PickupLocationCheckoutStepController extends AbstractCheckoutStepController
{
	private final static String PICKUP_LOCATION = "pickup-location";

	@GetMapping("/choose")
	@RequireHardLogIn
	@Override
	@PreValidateCheckoutStep(checkoutStep = PICKUP_LOCATION)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		// Try to set default delivery mode
		getCheckoutFacade().setDeliveryModeIfAvailable();

		model.addAttribute("cartData", getCheckoutFacade().getCheckoutCart());
		model.addAttribute("pickupConsolidationOptions", getCheckoutFacade().getConsolidatedPickupOptions());
		model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		return ControllerConstants.Views.Pages.MultiStepCheckout.ChoosePickupLocationPage;
	}

	@PostMapping("/choose")
	@RequireHardLogIn
	public String doSelectDeliveryLocation(@RequestParam(value = "posName") final String posName, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, CommerceCartModificationException
	{
		final ValidationResults validationResults = getCheckoutStep().validate(redirectAttributes);
		if (getCheckoutStep().checkIfValidationErrors(validationResults))
		{
			return getCheckoutStep().onValidation(validationResults);
		}

		//Consolidate the cart and add unsuccessful modifications to page
		model.addAttribute("validationData", getCheckoutFacade().consolidateCheckoutCart(posName));
		model.addAttribute("cartData", getCheckoutFacade().getCheckoutCart());
		model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		return ControllerConstants.Views.Pages.MultiStepCheckout.ChoosePickupLocationPage;
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

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(PICKUP_LOCATION);
	}

}
