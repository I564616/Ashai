package com.apb.storefront.validators;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.storefront.forms.AsahiUpdatePwdForm;


/**
 * Request Registration Validator
 */
@Component("asahiUpdatePasswordFormValidator")
public class AsahiUpdatePasswordFormValidator implements Validator
{
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	private static final String DEFAULT_PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-zA-Z])(?!.*\\s).{5,10}$";

	private static final String PASSWORD_VALIDATION_KEY = "storefront.passwordPattern.";

	public void validate(final Object object, final Errors errors)
	{
		final AsahiUpdatePwdForm updatePasswordForm = (AsahiUpdatePwdForm) object;
		final String newPassword = updatePasswordForm.getPwd();
		final String checkPassword = updatePasswordForm.getCheckPwd();
		boolean regexMatch = Boolean.FALSE;
		if (StringUtils.isEmpty(checkPassword))
		{
			errors.rejectValue("checkPwd", "updatePwd.checkPwd.invalid");
		}
		if (StringUtils.isEmpty(newPassword))
		{
			errors.rejectValue("pwd", "updatePwd.pwd.invalid");
		}
		if (StringUtils.isNotEmpty(newPassword) && !validatePassword(newPassword))
		{
			errors.rejectValue("pwd", "updatePwd.pwd.invalid");
			regexMatch = Boolean.TRUE;
		}
		if (StringUtils.isNotEmpty(checkPassword) && !validatePassword(newPassword))
		{
			errors.rejectValue("checkPwd", "updatePwd.pwd.invalid");
		}
		if (!regexMatch && StringUtils.isNotEmpty(newPassword) && StringUtils.isNotEmpty(checkPassword)
				&& !StringUtils.equals(newPassword, checkPassword))
		{
			errors.rejectValue("checkPwd", "validation.checkPwd.equals");
		}
	}

	public boolean validatePassword(final String pwd)
	{
		final Pattern pattern;
		pattern = Pattern.compile(this.asahiConfigurationService
				.getString(PASSWORD_VALIDATION_KEY + cmsSiteService.getCurrentSite().getUid(), DEFAULT_PASSWORD_REGEX));
		return pattern.matcher(pwd).matches();
	}

	public boolean supports(final Class<?> aClass)
	{
		return AsahiUpdatePwdForm.class.equals(aClass);
	}

}
