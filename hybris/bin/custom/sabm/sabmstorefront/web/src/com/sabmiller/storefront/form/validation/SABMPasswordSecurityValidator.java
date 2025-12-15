/**
 *
 */
package com.sabmiller.storefront.form.validation;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PasswordValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


/**
 * SABPasswordSecurityValidator Password Security and Email
 *
 * @author yaopeng
 *
 */
@Component("sabmPasswordSecurityValidator")
public class SABMPasswordSecurityValidator extends PasswordValidator
{
	private static final Logger LOG = Logger.getLogger(SABMPasswordSecurityValidator.class);

	private SiteConfigService siteConfigService;

	/**
	 * @return the siteConfigService
	 */
	public SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	/**
	 * @param siteConfigService
	 *           the siteConfigService to set
	 */
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	@Override
	public boolean supports(final Class<?> paramClass)
	{
		return UpdatePasswordForm.class.equals(paramClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final UpdatePasswordForm passwordForm = (UpdatePasswordForm) object;
		final String currPasswd = passwordForm.getCurrentPassword();
		final String newPasswd = passwordForm.getNewPassword();
		final String checkPasswd = passwordForm.getCheckNewPassword();

		if (StringUtils.isEmpty(currPasswd))
		{
			errors.rejectValue("currentPassword", "profile.currentPassword.invalid");
		}
		//SAB-386 update
		if (StringUtils.isEmpty(newPasswd))
		{
			LOG.error("new password is empty");
			errors.rejectValue("newPassword", "profile.password.notblank");
		}
		else if (!validatePassword(newPasswd))
		{
			LOG.error("new password is invalidate");
			errors.rejectValue("newPassword", "updatePwd.pwd.invalid");
		}

		if (StringUtils.isEmpty(checkPasswd))
		{
			errors.rejectValue("checkNewPassword", "profile.password.notblank");
		}
		else if (!checkPasswd.equals(newPasswd))
		{
			errors.rejectValue("checkNewPassword", "validation.checkPwd.equals");
		}
	}

	/**
	 * checked param should be the user’s email only
	 *
	 * @param email
	 * @return boolean
	 */
	public boolean validateEmail(final String email)
	{
		//get regex value from project.properties
		final String reg = getRegex("validateEmail.format.regx");
		final Pattern pattern = Pattern.compile(reg);
		final Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * checked passWord should be Min 8 characters, mix of at least one uppercase, lowercase one number
	 *
	 * @param passWord
	 * @return boolean
	 */
	public boolean validatePassword(final String passWord)
	{
		//get regex value from project.properties
		final String reg = getRegex("validatePassword.format.regx");
		final Pattern pattern = Pattern.compile(reg);
		final Matcher matcher = pattern.matcher(passWord);
		return matcher.matches();
	}

	/**
	 * get regex value from project.properties
	 *
	 * @param regex
	 * @return String
	 */
	public String getRegex(final String regex)
	{
		final String value = getSiteConfigService().getString(regex, "");
		return StringUtils.trimToEmpty(value);
	}
}
