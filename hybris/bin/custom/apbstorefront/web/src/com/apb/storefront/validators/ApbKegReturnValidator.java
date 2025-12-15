package com.apb.storefront.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.apb.storefront.forms.ApbKegReturnForm;
import com.apb.storefront.forms.ApbRequestRegisterForm;


/**
 * Contact Us Validator
 */
@Component("apbKegReturnValidator")
public class ApbKegReturnValidator implements Validator
{
	public void validate(final Object target, final Errors errors)
	{
		final ApbKegReturnForm kegReturnForm = (ApbKegReturnForm) target;

		if (StringUtils.isEmpty(kegReturnForm.getPickupAddress()))
		{
			errors.rejectValue("pickupAddress", "keg.return.pickup.address.invalid");
		}
	}

	public boolean supports(final Class<?> clazz)
	{
		return ApbRequestRegisterForm.class.equals(clazz);
	}
}
