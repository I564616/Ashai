/**
 *
 */
package com.sabmiller.core.mail;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import jakarta.annotation.Resource;


/**
 * @author joshua.a.antony
 *
 */
public class EmailConfig
{

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	public String getHost()
	{
		return configurationService.getConfiguration().getString("mail.smtp.server");
	}

	public String getPort()
	{
		return configurationService.getConfiguration().getString("mail.smtp.port");
	}

	public String getUserName()
	{
		return configurationService.getConfiguration().getString("mail.smtp.user");
	}

	public String getPassword()
	{
		return configurationService.getConfiguration().getString("mail.smtp.password");
	}

	public String getSupportEmail()
	{
		return configurationService.getConfiguration().getString("email.support");
	}

	public String getIntegrationMailFrom()
	{
		return configurationService.getConfiguration().getString("email.integration.import.from");
	}

	public String getMailFrom()
	{
		return configurationService.getConfiguration().getString("mail.from");
	}
}
