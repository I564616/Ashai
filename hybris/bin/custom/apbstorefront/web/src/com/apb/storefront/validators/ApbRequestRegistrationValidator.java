package com.apb.storefront.validators;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.apb.storefront.forms.ApbRequestRegisterForm;


/**
 * Request Registration Validator
 */
@Component("apbRequestRegistrationValidator")
public class ApbRequestRegistrationValidator implements Validator
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbRequestRegistrationValidator.class);

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The mobile validation pattern. */
	private final String mobileValdationPattern = "customer.mobile.validation.pattern.";

	private final String postalCodeLength = "customer.postal.code.";

	private static final String EMAIL_VALIDATION_KEY = "customer.email.validation.";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	
	/**
	 * ABN number validation pattern
	 */
	public static final String ABN_VALIDATION = "customer.abn.validation.pattern.";
	/**
	 * Default ABN number validation pattern from ConfigurationItem
	 */
	public static final String DEFAULT_ABN_VALIDATION = "^\\d{11,11}$";

	@Autowired
	private CMSSiteService cmsSiteService;

	public void validate(final Object target, final Errors errors)
	{
		final ApbRequestRegisterForm requestRegisterForm = (ApbRequestRegisterForm) target;

		if (StringUtils.isEmpty(requestRegisterForm.getOutletName()))
		{
			errors.rejectValue("outletName", "register.request.outletName.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getTradingName()))
		{
			errors.rejectValue("tradingName", "register.request.tradingName.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getCompanyName()))
		{
			errors.rejectValue("companyName", "register.request.companyName.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getStreetNumber()))
		{
			errors.rejectValue("streetNumber", "register.request.streetNumber.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getStreetName()))
		{
			errors.rejectValue("streetName", "register.request.streetName.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getStreetAbreviation()))
		{
			errors.rejectValue("streetAbreviation", "register.request.streetAbreviation.invalid");
		}
		/*
		 * Make Unit No. / Shop No. field Optional - ACP -1404 if
		 * (StringUtils.isEmpty(requestRegisterForm.getUnitNoShopNo())) { errors.rejectValue("unitNoShopNo",
		 * "register.request.unitNoShopNo.invalid"); }
		 */
		if (StringUtils.isEmpty(requestRegisterForm.getSuburb()))
		{
			errors.rejectValue("suburb", "register.request.suburb.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getStateInvoice()))
		{
			errors.rejectValue("stateInvoice", "register.request.stateInvoice.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getPostcodeInvoice()))
		{
			errors.rejectValue("postcodeInvoice", "register.request.postcodeInvoice.invalid");
		}
		if (StringUtils.isNotEmpty(requestRegisterForm.getPostcodeInvoice())
				&& requestRegisterForm.getPostcodeInvoice().length() > Integer.parseInt(getPostalCodeLength()))
		{
			errors.rejectValue("postcodeInvoice", "register.request.postcode.max.length.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getContactName()))
		{
			errors.rejectValue("contactName", "register.request.contactName.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getPhoneNoInvoice())
				|| !validatePattern(requestRegisterForm.getPhoneNoInvoice(), getMobileValdationPattern()))
		{
			errors.rejectValue("phoneNoInvoice", "register.request.phoneNoInvoice.invalid");
		}
		if (StringUtils.isNotEmpty(requestRegisterForm.getAlternativePhoneNo())
				&& !validatePattern(requestRegisterForm.getAlternativePhoneNo(), getMobileValdationPattern()))
		{
			errors.rejectValue("alternativePhoneNo", "register.request.alternativePhoneNo.invalid");
		}
		if (StringUtils.isNotEmpty(requestRegisterForm.getPhoneNoReference())
				&& !validatePattern(requestRegisterForm.getPhoneNoReference(), getMobileValdationPattern()))
		{
			errors.rejectValue("phoneNoReference", "register.request.phoneNoReference.invalid");
		}
		if (StringUtils.isNotEmpty(requestRegisterForm.getDateBusinessEstablished())
				&& !validateDateFormat(requestRegisterForm.getDateBusinessEstablished()))
		{
			errors.rejectValue("dateBusinessEstablished", "register.request.dateBusinessEstablished.format.invalid");
		}
		if (StringUtils.isNotEmpty(requestRegisterForm.getDateandExpiryofLiquorLicense())
				&& !validateDateFormat(requestRegisterForm.getDateandExpiryofLiquorLicense()))
		{
			errors.rejectValue("dateandExpiryofLiquorLicense", "register.request.dateandExpiryofLiquorLicense.format.invalid");
		}
		validateEmail(errors, requestRegisterForm.getEmailAddress());
		if (StringUtils.isEmpty(requestRegisterForm.getAbn()) || !validateAbnNumber(requestRegisterForm.getAbn()))
		{
			errors.rejectValue("abn", "register.request.abn.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getAcn()))
		{
			errors.rejectValue("acn", "register.request.acn.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getShippingStreet()))
		{
			errors.rejectValue("shippingStreet", "register.request.shippingStreet.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getShippingSuburb()))
		{
			errors.rejectValue("shippingSuburb", "register.request.shippingSuburb.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getStateDelivery()))
		{
			errors.rejectValue("stateDelivery", "register.request.stateDelivery.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getPostcodeDelivery()))
		{
			errors.rejectValue("postcodeDelivery", "register.request.postcodeDelivery.invalid");
		}
		if (StringUtils.isNotEmpty(requestRegisterForm.getPostcodeDelivery())
				&& requestRegisterForm.getPostcodeDelivery().length() > Integer.parseInt(getPostalCodeLength()))
		{
			errors.rejectValue("postcodeDelivery", "register.request.postcode.max.length.invalid");
		}
		if (requestRegisterForm.isApplicantCarry())
		{
			if (StringUtils.isEmpty(requestRegisterForm.getTrustName()))
			{
				errors.rejectValue("trustName", "register.request.trustName.invalid");
			}
			if (StringUtils.isEmpty(requestRegisterForm.getTrustAbn()))
			{
				errors.rejectValue("trustAbn", "register.request.trustAbn.invalid");
			}
		}

		if (StringUtils.isEmpty(requestRegisterForm.getPurchasingOfficer()))
		{
			errors.rejectValue("purchasingOfficer", "register.request.purchasingOfficer.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getAccountsContact()))
		{
			errors.rejectValue("accountsContact", "register.request.accountsContact.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getName()))
		{
			errors.rejectValue("name", "register.request.name.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getPosition()))
		{
			errors.rejectValue("position", "register.request.position.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getAddress()))
		{
			errors.rejectValue("address", "register.request.address.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getPhoneNo())
				|| !validatePattern(requestRegisterForm.getPhoneNo(), getMobileValdationPattern()))
		{
			errors.rejectValue("phoneNo", "register.request.phoneNo.invalid");
		}
		if (StringUtils.isEmpty(requestRegisterForm.getDateofBirth()))
		{
			errors.rejectValue("dateofBirth", "register.request.dateofBirth.invalid");
		}
		if (StringUtils.isNotEmpty(requestRegisterForm.getDateofBirth())
				&& !validateDateFormat(requestRegisterForm.getDateofBirth()))
		{
			errors.rejectValue("dateofBirth", "register.request.dateofBirth.format.invalid");
		}
		if (!requestRegisterForm.isRequestTermsConditions())
		{
			errors.rejectValue("requestTermsConditions", "register.request.requestTermsConditions.invalid");
		}
	}

	protected void validateEmail(final Errors errors, final String email)
	{
		if (StringUtils.isEmpty(email) || StringUtils.length(email) > 255 || !validateEmailAddress(email))
		{
			errors.rejectValue("emailAddress", "register.request.emailAddress.invalid");
		}
	}

	/**
	 * @param email
	 * @return boolean
	 */
	public boolean validateEmailAddress(final String email)
	{
		final String emailPattern = this.asahiConfigurationService
				.getString(EMAIL_VALIDATION_KEY + cmsSiteService.getCurrentSite().getUid(), "");
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
		return this.asahiConfigurationService.getString(mobileValdationPattern + cmsSiteService.getCurrentSite().getUid(), " ");
	}

	/**
	 * date format : dd/mm/yyyy
	 *
	 * @param dateValue
	 * @return
	 */
	protected boolean validateDateFormat(final String dateValue)
	{
		Date date = null;
		try
		{
			final SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
			date = sdf.parse(dateValue);
			if (!dateValue.equals(sdf.format(date)))
			{
				date = null;
			}
		}
		catch (final ParseException ex)
		{
			LOG.error("Date Parsing Exception" + ex.getMessage());
		}
		if (date == null)
		{
			return Boolean.FALSE;
		}
		else
		{
			return Boolean.TRUE;
		}
	}

	/**
	 * Postal code should be max 4 digits validation pattern.
	 *
	 * @return the postal code length
	 */
	public String getPostalCodeLength()
	{
		return this.asahiConfigurationService.getString(postalCodeLength + cmsSiteService.getCurrentSite().getUid(), "4");
	}
	
	protected boolean validateAbnNumber(final String value)
	{
		final Pattern apnValidator = Pattern.compile(this.asahiConfigurationService
				.getString(ABN_VALIDATION + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ABN_VALIDATION));
		final Matcher matcher = apnValidator.matcher(value);
		return matcher.matches();
	}


}
