package com.apb.storefront.validators;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.storefront.forms.ApbUpdateProfileForm;
import com.apb.storefront.forms.AsahiNotificationPrefForm;
import com.apb.core.util.AsahiSiteUtil;


/**
 * The Class ApbProfileValidator.
 *
 * @see Apb specific profile validator.
 */
@Component("apbProfileValidator")
public class ApbProfileValidator implements Validator
{

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The mobile valdation pattern. */
	private static final String MOBILE_VALDATION_PATTERN = "customer.mobile.validation.pattern.";

	@Autowired
	private CMSSiteService cmsSiteService;
	
	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;


	/**
	 * Supports.
	 *
	 * @param aClass
	 *           the a class
	 * @return true, if successful
	 */
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return ApbUpdateProfileForm.class.equals(aClass);
	}

	/**
	 * Validate.
	 *
	 * @param object
	 *           the object
	 * @param errors
	 *           the errors
	 */
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final ApbUpdateProfileForm profileForm = (ApbUpdateProfileForm) object;
		final String mobileNumber = profileForm.getMobileNumber();
		final String firstName = profileForm.getFirstName();
		final String lastName = profileForm.getLastName();
		final String emailAddress = profileForm.getEmailAddress();
		List<AsahiNotificationPrefForm> notificationPrefs = profileForm.getNotificationPrefs();
		if(! asahiSiteUtil.isSga()){
			final String titleCode = profileForm.getTitleCode();

			if (StringUtils.isEmpty(titleCode) || StringUtils.length(titleCode) > 255)
			{
				errors.rejectValue("titleCode", "profile.title.invalid");
			}
		}

		if (StringUtils.isBlank(firstName) || StringUtils.length(firstName) > 255)
		{
			errors.rejectValue("firstName", "profile.firstName.invalid");
		}

		if (StringUtils.isBlank(lastName) || StringUtils.length(lastName) > 255)
		{
			errors.rejectValue("lastName", "profile.lastName.invalid");
		}

		if (StringUtils.isNotEmpty(mobileNumber) && !validatePattern(mobileNumber, getMobileValdationPattern()))
		{
			errors.rejectValue("mobileNumber", "profile.mobileNumber.invalid.pattern.notmatch");
		}
		if(asahiSiteUtil.isSga()) 
		{
			if(StringUtils.isBlank(emailAddress) || StringUtils.length(emailAddress) > 255) {
				errors.rejectValue("emailAddress", "profile.emailAddress.invalid");
			}
			if (CollectionUtils.isEmpty(notificationPrefs) || notificationPrefs.size() != Integer.valueOf(6)) {
				errors.rejectValue("notificationPrefs", "profile.preferences.invalid");
			}
		}
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

	/**
	 * Gets the mobile valdation pattern.
	 *
	 * @return the mobile valdation pattern
	 */
	public String getMobileValdationPattern()
	{
		return this.asahiConfigurationService.getString(MOBILE_VALDATION_PATTERN + cmsSiteService.getCurrentSite().getUid(), "");
	}
}
