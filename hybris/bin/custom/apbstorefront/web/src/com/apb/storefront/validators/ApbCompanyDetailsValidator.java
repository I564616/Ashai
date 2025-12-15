package com.apb.storefront.validators;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.storefront.forms.ApbCompanyDetailsForm;


/**
 * Request Registration Validator
 */
@Component("apbCompanyDetailsValidator")
public class ApbCompanyDetailsValidator implements Validator
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbCompanyDetailsValidator.class);

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	/** The mobile validation pattern. */
	private static final String mobileValdationPattern = "customer.mobile.validation.pattern.";

	private static final String EMAIL_VALIDATION_KEY = "customer.email.validation.";
	
	/**
	 * ABN number validation pattern
	 */
	public static final String ABN_VALIDATION = "customer.abn.validation.pattern.";
	/**
	 * Default ABN number validation pattern from ConfigurationItem
	 */
	public static final String DEFAULT_ABN_VALIDATION = "^\\d{11,11}$";
	
	private static final String CUSTOMER_EMAIL_SPLIT = "customer.email.split";
	

	public void validate(final Object target, final Errors errors)
	{
		final ApbCompanyDetailsForm apbCompanyDetailsForm = (ApbCompanyDetailsForm) target;

		if (StringUtils.isEmpty(apbCompanyDetailsForm.getAccountNumber()))
		{
			errors.rejectValue("accountNumber", "company.detail.accountNumber.invalid");
		}
		if (StringUtils.isEmpty(apbCompanyDetailsForm.getAcccountName()))
		{
			errors.rejectValue("accountName", "company.detail.accountName.invalid");
		}
		if (StringUtils.isEmpty(apbCompanyDetailsForm.getTradingName()))
		{
			errors.rejectValue("tradingName", "company.detail.tradingName.invalid");
		}

		if (StringUtils.isEmpty(apbCompanyDetailsForm.getAbn()) || !validateAbnNumber(apbCompanyDetailsForm.getAbn()))
		{
			errors.rejectValue("abn", "company.detail.abn.invalid");
		}
	
		if (StringUtils.isEmpty(apbCompanyDetailsForm.getCompanyBillingAddress()))
		{
			errors.rejectValue("companyBillingAddress", "company.detail.companyBillingAddress.invalid");
		}
		validateEmail(errors, apbCompanyDetailsForm.getCompanyEmailAddress());

		if (StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyPhone())
				&& !validatePattern(apbCompanyDetailsForm.getCompanyPhone(), getMobileValdationPattern()))
		{
			errors.rejectValue("companyPhone", "company.detail.companyPhone.invalid");
		}
		if (StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyMobilePhone())
				&& !validatePattern(apbCompanyDetailsForm.getCompanyMobilePhone(), getMobileValdationPattern()))
		{
			errors.rejectValue("companyMobilePhone", "company.detail.companyMobilePhone.invalid");
		}
		
		if (StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyFax())
				&& !validatePattern(apbCompanyDetailsForm.getCompanyFax(), getMobileValdationPattern()))
		{
			errors.rejectValue("companyFax", "company.detail.companyMobilePhone.invalid");
		}
	}


	protected void validateEmail(final Errors errors, final String email)
	{
		String emailSeparator  = this.asahiConfigurationService.getString(CUSTOMER_EMAIL_SPLIT, ";");
		String[] separators = emailSeparator.split("");
		boolean containSeparator = true;
		for(String separator:separators)
		{
			if(email.contains(separator)) 
			{
				final String[] emailAddresses = email.split(separator);
				for(String newEmail:emailAddresses) 
				{
					String emailNoSpace = newEmail.trim();
					if (StringUtils.isEmpty(emailNoSpace) || StringUtils.length(emailNoSpace) > 255 || !validateEmailAddress(emailNoSpace))
					{
						errors.rejectValue("companyEmailAddress", "company.detail.companyEmailAddress.invalid");
						break;
					}
				}
				containSeparator = true;
				break;
			}
			else{
				containSeparator = false;
			}
		}
		
		if(!containSeparator && (StringUtils.isEmpty(email) || StringUtils.length(email) > 255 || !validateEmailAddress(email)))
		{
			errors.rejectValue("companyEmailAddress", "company.detail.companyEmailAddress.invalid");
		}
		
	}

	/**
	 * Gets the mobile valdation pattern.
	 *
	 * @return the mobile valdation pattern
	 */
	public String getMobileValdationPattern()
	{
		return this.asahiConfigurationService.getString(mobileValdationPattern + cmsSiteService.getCurrentSite().getUid(), " ");
	}

	/**
	 * @param email
	 * @return boolean
	 */
	public boolean validateEmailAddress(final String email)
	{
		final String pattern = emailValidationPattern();
		final Pattern email_regex = Pattern.compile(pattern);
		final Matcher matcher = email_regex.matcher(email);
		return matcher.matches();
	}

	/**
	 * @return
	 */
	public String emailValidationPattern()
	{
		return this.asahiConfigurationService.getString(EMAIL_VALIDATION_KEY + cmsSiteService.getCurrentSite().getUid(), " ");
	}

	public boolean supports(final Class<?> clazz)
	{
		return ApbCompanyDetailsForm.class.equals(clazz);
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
	
	protected boolean validateAbnNumber(final String value)
	{
		final Pattern apnValidator = Pattern.compile(this.asahiConfigurationService
				.getString(ABN_VALIDATION + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ABN_VALIDATION));
		final Matcher matcher = apnValidator.matcher(value);
		return matcher.matches();
	}


}
