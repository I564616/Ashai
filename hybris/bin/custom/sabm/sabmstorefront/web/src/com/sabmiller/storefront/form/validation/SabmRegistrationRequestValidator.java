/**
 *
 */
package com.sabmiller.storefront.form.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sabmiller.facades.registrationrequest.data.RegistrationRequestForm;



/**
 *
 */
@Component("registrationRequestValidator")
public class SabmRegistrationRequestValidator implements Validator
{
	public static final Pattern EMAIL_REGEX = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");

	public static final Pattern NAME_REGEX = Pattern.compile("\\b[A-Za-z-'\\s]*\\b"); 

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return RegistrationRequestForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final RegistrationRequestForm form = (RegistrationRequestForm) object;

		final String firstName = form.getFirstName();
		final String lastName = form.getLastName();
		final String email = form.getEmail();

		if (StringUtils.isBlank(firstName))
		{
			errors.rejectValue("firstName", "register.firstName.invalid");
		}
		else if (StringUtils.length(firstName) > 255 || !validateName(firstName))
		{
			errors.rejectValue("firstName", "register.firstName.invalid");
		}

		if (StringUtils.isBlank(lastName))
		{
			errors.rejectValue("lastName", "register.surName.invalid");
		}
		else if (StringUtils.length(lastName) > 255 || !validateName(lastName))
		{
			errors.rejectValue("lastName", "register.surName.invalid");
		}

		if (StringUtils.length(firstName) + StringUtils.length(lastName) > 255)
		{
			errors.rejectValue("lastName", "register.name.invalid");
			errors.rejectValue("firstName", "register.name.invalid");
		}

		if (StringUtils.isEmpty(email))
		{
			errors.rejectValue("email", "register.email.invalid");
		}
		else if (StringUtils.length(email) > 255 || !validateEmailAddress(email))
		{
			errors.rejectValue("email", "register.email.invalid");
		}

		if (StringUtils.isEmpty(form.getAccountName()))
		{
			errors.rejectValue("accountName", "register.firstName.invalid");
		}

		if (StringUtils.isEmpty(form.getWorkPhoneNum()))
		{
			errors.rejectValue("workPhoneNum", "register.firstName.invalid");
		}

		if (CollectionUtils.isEmpty(form.getAccoutType()))
		{
			errors.rejectValue("accoutType", "register.firstName.invalid");
		}

		if (CollectionUtils.isEmpty(form.getAccessType()))
		{
			errors.rejectValue("accessType", "register.firstName.invalid");
		}
	}

	private boolean validateEmailAddress(final String email)
	{
		final Matcher matcher = EMAIL_REGEX.matcher(email);
		return matcher.matches();
	}

	private boolean validateName(final String name)
	{
		final Matcher matcher = NAME_REGEX.matcher(name);
		return matcher.matches();
	}

}
