package com.apb.storefront.validators;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.apb.storefront.forms.AsahiForgottenPwdForm;


/**
 * Request Registration Validator
 */
@Component("asahiForgotPasswordFormValidator")
public class AsahiForgotPasswordFormValidator implements Validator
{
	@Autowired
	private ConfigurationService configurationService;

	private static final String DEFAULT_EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
	private static final String EMAIL_VALIDATION_KEY = "customer.email.validation";

	public void validate(final Object target, final Errors errors)
	{
		final AsahiForgottenPwdForm asahiForgottenPwdForm = (AsahiForgottenPwdForm) target;
		validateEmail(errors, asahiForgottenPwdForm.getEmail());
	}

	protected void validateEmail(final Errors errors, final String email)
	{
		if (StringUtils.isEmpty(email))
		{
			errors.rejectValue("email", "forgottenPwd.email.null");
		}
		else if (StringUtils.length(email) > 255 || !validateEmailAddress(email))
		{
			errors.rejectValue("email", "forgottenPwd.email.invalid");
		}
	}

	/**
	 * @param email
	 * @return boolean
	 */
	public boolean validateEmailAddress(final String email)
	{
		final Pattern EMAIL_REGEX = Pattern
				.compile(configurationService.getConfiguration().getString(EMAIL_VALIDATION_KEY, DEFAULT_EMAIL_REGEX));


		final Matcher matcher = EMAIL_REGEX.matcher(email);
		return matcher.matches();
	}

	public boolean supports(final Class<?> clazz)
	{
		return AsahiForgottenPwdForm.class.equals(clazz);
	}

}
