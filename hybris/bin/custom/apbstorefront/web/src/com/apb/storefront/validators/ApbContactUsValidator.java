package com.apb.storefront.validators;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.forms.ApbContactUsForm;
import com.apb.storefront.forms.ApbRequestRegisterForm;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.forms.DiscrepancyForm;
import java.util.List;

/**
 * Contact Us Validator
 */
@Component("apbContactUsValidator")
public class ApbContactUsValidator implements Validator
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbContactUsValidator.class);

	public static final String INCORRECT_CHARGE_TYPE = "INCORRECT_CHARGE";
	public static final String REPORT_DEL_ISSUE_TYPE = "REPORT_DEL_ISSUE";
	public static final String DAMAGED_PRODUCTS_TYPE = "DAMAGED_PRODUCTS";
	public static final String INCORRECT_PRODUCTS_TYPE = "INCORRECT_PRODUCTS";
	public static final String WRONG_QTY_TYPE = "WRONG_QTY";


	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Resource(name="asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	public void validate(final Object target, final Errors errors)
	{
		final ApbContactUsForm contactUsForm = (ApbContactUsForm) target;

		if (StringUtils.isEmpty(contactUsForm.getName()))
		{
			errors.rejectValue("name", "contactus.name.invalid");
		}

		validateEmail(errors, contactUsForm.getEmailAddress());

		if (StringUtils.isEmpty(contactUsForm.getContactNumber())
				|| !(validatePattern(contactUsForm.getContactNumber(), getMobileValdationPattern())))
		{
			errors.rejectValue("contactNumber", "contactus.contact.number.invalid");
		}

		if( (! this.asahiConfigurationService.getBoolean("sga.contactus.update.available", false) && asahiSiteUtil.isSga()) | asahiSiteUtil.isApb()) {
			if (StringUtils.isNotEmpty(contactUsForm.getSubject()) && contactUsForm.getSubject().equalsIgnoreCase("1")) {
				errors.rejectValue("subject", "contactus.subject.invalid");
			}
			if (StringUtils.isEmpty(contactUsForm.getFurtherDetail())) {
				errors.rejectValue("furtherDetail", "contactus.further.detail.invalid");
			}
			if (StringUtils.isNotEmpty(contactUsForm.getSubjectFlag())
					&& contactUsForm.getSubjectFlag().equalsIgnoreCase("otherExists")) {
				if (StringUtils.isEmpty(contactUsForm.getSubjectOther())) {
					errors.rejectValue("subjectOther", "contactus.subject.other.invalid");
				}
			}

		}


	}

	protected void validateEmail(final Errors errors, final String email)
	{
		if (StringUtils.isEmpty(email) || StringUtils.length(email) > 255 || !validateEmailAddress(email))
		{
			errors.rejectValue("emailAddress", "contactus.email.address.invalid");
		}
	}

	/**
	 * @param email
	 * @return boolean
	 */
	public boolean validateEmailAddress(final String email)
	{
		final String emailPattern = this.asahiConfigurationService
				.getString(ApbStoreFrontContants.EMAIL_VALIDATION_KEY + cmsSiteService.getCurrentSite().getUid(), "");
		final Pattern EMAIL_REGEX = Pattern.compile(emailPattern);
		final Matcher matcher = EMAIL_REGEX.matcher(email);
		return matcher.matches();
	}

	public boolean supports(final Class<?> clazz)
	{
		return ApbRequestRegisterForm.class.equals(clazz);
	}

	/**
	 * Validate field using pattern.
	 *
	 * @param mobileNumber
	 *           the mobile number
	 * @param pattern
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
	
	public boolean validateMobilePattern(final String mobileNumber)
	{
		boolean isValid = false;
		if (StringUtils.isNotBlank(getMobileValdationPattern()))
		{
			isValid = mobileNumber.matches(getMobileValdationPattern());
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
		return this.asahiConfigurationService
				.getString(ApbStoreFrontContants.MOBILE_VALDATION_PATTERN + cmsSiteService.getCurrentSite().getUid(), " ");
	}


	public void setAsahiConfigurationService(AsahiConfigurationService asahiConfigurationService) {
		this.asahiConfigurationService = asahiConfigurationService;
	}
}
