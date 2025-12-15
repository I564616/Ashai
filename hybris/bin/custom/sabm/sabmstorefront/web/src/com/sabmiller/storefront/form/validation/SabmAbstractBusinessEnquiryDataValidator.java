/**
 *
 */
package com.sabmiller.storefront.form.validation;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import jakarta.annotation.Resource;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;
import com.sap.security.core.server.csi.XSSEncoder;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmAbstractBusinessEnquiryDataValidator")
public class SabmAbstractBusinessEnquiryDataValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Map<String, SabmBusinessEnquiryValidation> businessEnquiryValidatorMap;

	@Resource
	private ConfigurationService configurationService;

	private static final String CONTACT_US_PREFERRED_CONTACT_PHONE = "Phone";
	private static final String CONTACT_US_PREFERRED_CONTACT_EMAIL = "Email";

	public static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

	private static final Logger LOG = LoggerFactory.getLogger(SabmAbstractBusinessEnquiryDataValidator.class.getName());

	@Override
	public void validate(final AbstractBusinessEnquiryData businessEnquiryData, final List<String> errors)
	{
		try
		{
			final String businessUnit = XSSEncoder.encodeHTML(businessEnquiryData.getBusinessUnit());
			final String emailAddress = businessEnquiryData.getEmailAddress();
			final String name = XSSEncoder.encodeHTML(businessEnquiryData.getName());
			final String preferredContact = XSSEncoder.encodeHTML(businessEnquiryData.getPreferredContactMethod());
			final String requestType = XSSEncoder.encodeHTML(businessEnquiryData.getRequestType());
			final String phoneNumber = XSSEncoder.encodeHTML(businessEnquiryData.getPhoneNumber());

			validateStringField(businessUnit, SabmAbstractBusinessEnquiryField.BUSINESS_UNIT, errors);

			if (preferredContact.equals(CONTACT_US_PREFERRED_CONTACT_EMAIL))
			{
				if (StringUtils.isEmpty(emailAddress) || !validateEmailAddress(emailAddress))
				{
					errors.add(configurationService.getConfiguration().getString(SabmAbstractBusinessEnquiryField.EMAIL.getProperty(),
							SabmAbstractBusinessEnquiryField.EMAIL.getDefaultMsg()));
				}
			}
			validateStringField(name, SabmAbstractBusinessEnquiryField.NAME, errors);
			validateStringField(preferredContact, SabmAbstractBusinessEnquiryField.PREFERRED_CONTACT, errors);
			validateStringField(requestType, SabmAbstractBusinessEnquiryField.REQUEST_TYPE, errors);

			if (CONTACT_US_PREFERRED_CONTACT_PHONE.equals(preferredContact))
			{
				validateStringField(phoneNumber, SabmAbstractBusinessEnquiryField.PHONE, errors);
			}
			try {
				businessEnquiryValidatorMap.get(businessEnquiryData.getRequestType()).validate(businessEnquiryData, errors);
			} catch (Exception e) {
				LOG.error("Exception validating data ", e);
			}

		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.error("issue with input field validation");
		}

	}

	private void validateStringField(final String field, final SabmAbstractBusinessEnquiryField fieldType,
			final List<String> errors)
	{
		if (StringUtils.isEmpty(field))
		{
			errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
		}
	}

	private enum SabmAbstractBusinessEnquiryField
	{
		NAME("enquiry.common.name.invalid", "Name is required."), BUSINESS_UNIT("enquiry.common.businessunit.invalid",
				"Business unit is required."), EMAIL("enquiry.common.email.invalid", "Email address is required."), PREFERRED_CONTACT(
						"enquiry.common.preferredcontact.invalid",
						"Preferred contact is required."), REQUEST_TYPE("enquiry.common.requesttype.invalid",
								"Request type is required."), PHONE("enquiry.common.phone.invalid", "Phone number is required.");

		private final String property;
		private final String defaultMsg;

		SabmAbstractBusinessEnquiryField(final String property, final String defaultMsg)
		{
			this.property = property;
			this.defaultMsg = defaultMsg;
		}

		public String getProperty()
		{
			return property;
		}

		public String getDefaultMsg()
		{
			return defaultMsg;
		}
	}

	public boolean validateEmailAddress(final String email)
	{

        //return EmailValidator.getInstance().isValid(email);
        return Pattern.compile(EMAIL_REGEX)
                .matcher(email)
                .matches();
	}
}
