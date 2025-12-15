/**
 *
 */
package com.sabmiller.facades.email.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.mail.EmailConfig;
import com.sabmiller.facades.email.SABMEmailFacade;


/**
 * The Class SABMEmailFacadeImpl.
 */
public class SABMEmailFacadeImpl implements SABMEmailFacade
{

	/** The Constant EMAIL_DISPLAY. */
	public static final String EMAIL_DISPLAY = "Service Request";

	/** The Constant EMAIL_DISPLAY_CONTACTUS. */
	private static final String EMAIL_DISPLAY_CONTACTUS = "ContactUs";

	/** The Constant SERVICE_REQUEST_EMAIL_HEAD. */
	public static final String SERVICE_REQUEST_EMAIL_HEAD = "email.service.request.";

	/** The Constant CUSTOMER_ID_TITLE. */
	public static final String CUSTOMER_ID_TITLE = "Customer ID:";

	/** The Constant ACCOUNT_NAME_TITLE. */
	public static final String ACCOUNT_NAME_TITLE = "Account Name:";

	/** The Constant ACCOUNT_NUMBER_TITLE. */
	public static final String ACCOUNT_NUMBER_TITLE = "Account Number:";

	/** The Constant BUSINESS_UNIT_TITLE. */
	public static final String BUSINESS_UNIT_TITLE = "Business Unit:";

	/** The Constant SERVICE_REQUEST_TYPE. */
	public static final String SERVICE_REQUEST_TYPE = "Service Request type:";

	/** The Constant EMAIL_BODY_END_OF_LINE. */
	public static final String EMAIL_BODY_END_OF_LINE = "<br>";

	/** The Constant EMAIL_BODY_END_OF_LINE_KEY. */
	public static final String EMAIL_BODY_END_OF_LINE_KEY = "email.end.of.line";

	/** The email service. */
	private SystemEmailService emailService;

	/** The user service. */
	private UserService userService;

	/** The email config. */
	private EmailConfig emailConfig;

	/** The configuration service. */
	private ConfigurationService configurationService;

	/** The b2b unit service. */
	private SabmB2BUnitService b2bUnitService;

	/** The contact us email address. */
	@Value(value = "${email.contactUs:onlineenquiries@cub.com.au}")
	private String contactUsEmailAddress;

	/**
	 * Send the request Email.
	 *
	 * @param requestKey
	 *           the request key
	 * @param requestType
	 *           the request type
	 * @param text
	 *           the text
	 * @return the system email message model
	 */
	@Override
	public SystemEmailMessageModel sendServiceRequestEmail(final String requestKey, final String requestType, final String text)
	{
		String toEmailAddress = "";
		if (StringUtils.isNotEmpty(requestKey))
		{
			toEmailAddress = getConfigurationService().getConfiguration()
					.getString(SERVICE_REQUEST_EMAIL_HEAD + requestKey.replaceAll(" ", ""));
		}

		final UserModel currentUser = userService.getCurrentUser();
		if (currentUser instanceof B2BCustomerModel && StringUtils.isNotEmpty(toEmailAddress) && StringUtils.isNotEmpty(text))
		{
			final B2BCustomerModel currentCustomer = (B2BCustomerModel) currentUser;

			// construct the subject of the email
			final StringBuilder subject = new StringBuilder();
			subject.append(requestType);
			subject.append(" " + CUSTOMER_ID_TITLE + currentCustomer.getUid());

			// construct the body message of the email
			final List<String> messages = new ArrayList<String>();
			B2BUnitModel topLevelUnit = null;
			final B2BUnitModel b2bUnit = getB2bUnitService().getParent(currentCustomer);
			if (b2bUnit != null)
			{
				topLevelUnit = getB2bUnitService().findTopLevelB2BUnit(b2bUnit.getPayerId());
				if (topLevelUnit == null)
				{
					topLevelUnit = b2bUnit;
				}

				messages.add(ACCOUNT_NAME_TITLE + topLevelUnit.getName());
				messages.add(ACCOUNT_NUMBER_TITLE + topLevelUnit.getUid());
				messages.add(BUSINESS_UNIT_TITLE + b2bUnit.getUid());
				messages.add(CUSTOMER_ID_TITLE + currentCustomer.getUid());
				messages.add(SERVICE_REQUEST_TYPE + requestType);

				final String replacement = getConfigurationService().getConfiguration().getString(EMAIL_BODY_END_OF_LINE_KEY,
						EMAIL_BODY_END_OF_LINE);
				messages.add(text.replaceAll("\r\n", replacement).replaceAll("\n", replacement));
			}

			// construct the email message model
			final SystemEmailMessageModel systemEmailMessageModel = getEmailService().constructSystemEmail(emailConfig.getMailFrom(),
					toEmailAddress, EMAIL_DISPLAY, subject.toString(), messages, null);

			// send the email
			getEmailService().send(systemEmailMessageModel);

			return systemEmailMessageModel;
		}
		return null;
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.email.SABMEmailFacade#sendContactUsEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public SystemEmailMessageModel sendContactUsEmail(final String subject, final String message)
	{
		Assert.hasText(subject, "The field [subject] cannot be empty");
		// For email body
		final List<String> messages = new ArrayList<String>();

		//current customer
		final UserModel currentUser = userService.getCurrentUser();

		if (currentUser instanceof B2BCustomerModel && StringUtils.isNotEmpty(contactUsEmailAddress))
		{
			final B2BCustomerModel currentCustomer = (B2BCustomerModel) currentUser;
			final B2BUnitModel b2bUnit = getB2bUnitService().getParent(currentCustomer);
			if (b2bUnit != null)
			{
				//add list to create emil body.
				this.addMessageList(b2bUnit, messages, message, currentCustomer);
			}

			final SystemEmailMessageModel systemEmailMessageModel = getEmailService().constructSystemEmail(emailConfig.getMailFrom(),
					contactUsEmailAddress, EMAIL_DISPLAY_CONTACTUS, subject, messages, null);

			emailService.send(systemEmailMessageModel);
			return systemEmailMessageModel;
		}
		return null;

	}

	/**
	 * Adds the message list.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param messages
	 *           the messages
	 * @param message
	 *           the message
	 * @param currentCustomer
	 *           the current customer
	 */
	private void addMessageList(final B2BUnitModel b2bUnit, final List<String> messages, final String message,
			final B2BCustomerModel currentCustomer)
	{
		// Top Level B2BUnit
		B2BUnitModel topLevelUnit = getB2bUnitService().findTopLevelB2BUnit(b2bUnit.getPayerId());
		if (topLevelUnit == null)
		{
			topLevelUnit = b2bUnit;
		}
		messages.add(ACCOUNT_NAME_TITLE + topLevelUnit.getName());
		messages.add(ACCOUNT_NUMBER_TITLE + topLevelUnit.getUid());
		//Session B2BUnit Uid
		messages.add(BUSINESS_UNIT_TITLE + currentCustomer.getDefaultB2BUnit().getUid());
		//Customer email
		messages.add(CUSTOMER_ID_TITLE + currentCustomer.getContactEmail());
		final String replacement = getConfigurationService().getConfiguration().getString(EMAIL_BODY_END_OF_LINE_KEY,
				EMAIL_BODY_END_OF_LINE);
		messages.add(message.replaceAll("\r\n", replacement).replaceAll("\n", replacement));
	}



	/**
	 * Gets the email service.
	 *
	 * @return the emailService
	 */
	public SystemEmailService getEmailService()
	{
		return emailService;
	}

	/**
	 * Sets the email service.
	 *
	 * @param emailService
	 *           the emailService to set
	 */
	public void setEmailService(final SystemEmailService emailService)
	{
		this.emailService = emailService;
	}

	/**
	 * Gets the user service.
	 *
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets the user service.
	 *
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Gets the email config.
	 *
	 * @return the emailConfig
	 */
	public EmailConfig getEmailConfig()
	{
		return emailConfig;
	}

	/**
	 * Sets the email config.
	 *
	 * @param emailConfig
	 *           the emailConfig to set
	 */
	public void setEmailConfig(final EmailConfig emailConfig)
	{
		this.emailConfig = emailConfig;
	}

	/**
	 * Gets the configuration service.
	 *
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets the configuration service.
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets the b2b unit service.
	 *
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * Sets the b2b unit service.
	 *
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
