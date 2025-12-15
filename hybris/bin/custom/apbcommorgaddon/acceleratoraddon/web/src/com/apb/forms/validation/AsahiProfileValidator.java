package com.apb.forms.validation;

import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateProfileForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ProfileValidator;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.forms.B2BCustomerForm;

/**
 * Validator for profile forms.
 */
@Component("asahiProfileValidator")
public class AsahiProfileValidator extends ProfileValidator
{
	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;
	
	private static final String REGEXPATTERN = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
	
	private static final int minEmailLength = 1;
	
	private static final int maxEmaillength = 255;
	
	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Autowired
	private CMSSiteService cmsSiteService;

	/** The mobile valdation pattern. */
	private static final String MOBILE_VALDATION_PATTERN = "customer.mobile.validation.pattern.";

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final B2BCustomerForm profileForm = (B2BCustomerForm) object;
		final String title = profileForm.getTitleCode();
		final String firstName = profileForm.getFirstName();
		final String lastName = profileForm.getLastName();
		final String mobileNumber = profileForm.getMobileNumber();
		final String email =profileForm.getEmail();
			
		if (StringUtils.isEmpty(email) ) {
			errors.rejectValue("email", "profile.email.blank");
		}
		else if (email!=null && (StringUtils.length(email)<1 || StringUtils.length(email)>255))
		{
			errors.rejectValue("email", "profile.email.invalid");
		}
		else if (!Pattern.matches(REGEXPATTERN, email))
		{
			errors.rejectValue("email", "profile.email.invalid");
		}	

		if (StringUtils.isEmpty(title) && !asahiSiteUtil.isSga())
		{
			errors.rejectValue("titleCode", "profile.title.invalid");
		}
		else if (StringUtils.length(title) > 255 && !asahiSiteUtil.isSga())
		{
			errors.rejectValue("titleCode", "profile.title.invalid");
		}
		
		if (!asahiSiteUtil.isSga() && StringUtils.isEmpty(profileForm.getRole())) {
			errors.rejectValue("role", "profile.access.invalid");
		}

		if (StringUtils.isBlank(firstName))
		{
			errors.rejectValue("firstName", "profile.firstName.invalid");
		}
		else if (StringUtils.length(firstName) > 255)
		{
			errors.rejectValue("firstName", "profile.firstName.invalid");
		}

		if (StringUtils.isBlank(lastName))
		{
			errors.rejectValue("lastName", "profile.lastName.invalid");
		}
		else if (StringUtils.length(lastName) > 255)
		{
			errors.rejectValue("lastName", "profile.lastName.invalid");
		}
		if (StringUtils.isNotEmpty(mobileNumber) && !validatePattern(mobileNumber, getMobileValdationPattern()))
		{
			errors.rejectValue("mobileNumber", "profile.mobileNumber.invalid.pattern.notmatch");
		}
		
		
		
	}
	
	/**
	 * Gets the mobile valdation pattern.
	 *
	 * @return the mobile valdation pattern
	 */
	public String getMobileValdationPattern()
	{
		return this.asahiConfigurationService.getString(MOBILE_VALDATION_PATTERN + cmsSiteService.getCurrentSite().getUid(), "");
	}

	/**
	 * Validate field using pattern.
	 *
	 * @param mobileNumber
	 *           the mobile number
	 * @return true, if successful
	 */
	public boolean validatePattern(final String mobileNumber, final String pattern)
	{
		boolean isValid = false;
		if (StringUtils.isNotBlank(pattern))
		{
			isValid = mobileNumber.matches(pattern);
		}

		return isValid;
	}
}
