package com.apb.core.service.config.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.servicelayer.config.impl.DefaultConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.dao.config.AsahiConfigurationDao;
import com.apb.core.service.config.AsahiConfigurationService;


/**
 * The Class AsahiConfigurationServiceImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiConfigurationServiceImpl extends DefaultConfigurationService implements AsahiConfigurationService
{

	/** The configuration dao. */
	@Resource(name = "configurationDao")
	private AsahiConfigurationDao configurationDao;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The i 18 n service. */
	@Resource(name = "i18nService")
	private I18NService i18nService;


	/**
	 * Gets the string.
	 *
	 * @param key
	 *           the key
	 * @param defaultValue
	 *           the default value
	 * @return the string
	 */
	@Override
	public String getString(final String key, final String defaultValue)
	{

		//getting Config Value from Database
		final String configValueFromDB = this.configurationDao.getConfigValueForKey(key);

		if (null != configValueFromDB)
		{
			return configValueFromDB;
		}
		else
		{
			//getting Config Value from Property File
			final String configValueFromPropertyFile = super.getConfiguration().getString(key, defaultValue);
			return (null != configValueFromPropertyFile) ? configValueFromPropertyFile : defaultValue;
		}
	}

	/**
	 * Check if config key exist.
	 *
	 * @param configKey
	 *           the config key
	 * @return the string
	 */
	@Override
	public String checkIfConfigKeyExist(final String configKey)
	{
		//getting Config Value from Database
		return this.configurationDao.getConfigValueForKey(configKey);
	}

	@Override
	public boolean getBoolean(final String key, final boolean defaultValue)
	{

		//getting Config Value from Database
		final String configValue = this.configurationDao.getConfigValueForKey(key);
		if (null != configValue)
		{
			if ("true".equalsIgnoreCase(configValue))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			//getting Config Value from Property File
			return super.getConfiguration().getBoolean(key, defaultValue);
		}
	}

	@Override
	public int getInt(String key, int defaultValue)
	{
		//getting Config Value from Database
		final String configValueFromDB = this.configurationDao.getConfigValueForKey(key);

		if (null != configValueFromDB)
		{
			return StringUtils.isNumeric(configValueFromDB)? Integer.valueOf(configValueFromDB) : defaultValue;
		}
		else
		{
			//getting Config Value from Property File
			return super.getConfiguration().getInt(key, defaultValue);
		}
	}
}
