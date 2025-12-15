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
package com.sabmiller.storefront.form.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sabmiller.facades.customer.CustomerJson;


/**
 * Validates registration forms.
 */
@Component("customerJsonValidator")
public class CustomerJsonValidator implements Validator
{
	/**
	 * INC0702037: Allow special characters like '
	 *
	 */
	public static final Pattern EMAIL_REGEX = Pattern.compile("\\b[A-Za-z0-9._'%&+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,10}\\b");

	public static final Pattern NAME_REGEX = Pattern.compile("\\b[A-Za-z-\\s]*\\b");
	public static final Pattern SURNAME_REGEX = Pattern.compile("\\b[A-Za-z-'\\s]*\\b");

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return CustomerJson.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final CustomerJson customerJson = (CustomerJson) object;

		final String firstName = customerJson.getFirstName();
		final String surName = customerJson.getSurName();
		final String email = customerJson.getEmail();

		if (StringUtils.isBlank(firstName))
		{
			errors.rejectValue("firstName", "register.firstName.invalid");
		}
		else if (StringUtils.length(firstName) > 255 || !validateName(firstName))
		{
			errors.rejectValue("firstName", "register.firstName.invalid");
		}

		if (StringUtils.isBlank(surName))
		{
			errors.rejectValue("surName", "register.surName.invalid");
		}
		else if (StringUtils.length(surName) > 255 || !validateSurName(surName))
		{
			errors.rejectValue("surName", "register.surName.invalid");
		}

		if (StringUtils.length(firstName) + StringUtils.length(surName) > 255)
		{
			errors.rejectValue("surName", "register.name.invalid");
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

	}

	private boolean validateSurName(final String surName)
	{
		final Matcher matcher = SURNAME_REGEX.matcher(surName);
		return matcher.matches();
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
