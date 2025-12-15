package com.apb.storefront.checkout.steps.validation.impl;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.AbstractCheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;

public class ApbSingleStepCheckoutStepValidator extends AbstractCheckoutStepValidator {

	@Override
	public ValidationResults validateOnEnter(RedirectAttributes redirectAttributes) 
	{
		
		return ValidationResults.SUCCESS;

	}

}
