package com.apb.core.service.config;

import de.hybris.platform.servicelayer.config.ConfigurationService;


public interface AsahiConfigurationService extends ConfigurationService
{

	/**
	 * Check if config key exist.
	 *
	 * @param configKey
	 *           the config key
	 * @return the string
	 */
	String checkIfConfigKeyExist(String configKey);

	/**
	 * Gets the string.
	 *
	 * @param key
	 *           the key
	 * @param defaultValue
	 *           the default value
	 * @return the string
	 */
	String getString(String key, String defaultValue);

	/**
	 * Gets the boolean.
	 *
	 * @param key
	 *           the key
	 * @param defaultValue
	 *           the default value
	 * @return the boolean
	 */
	boolean getBoolean(String key, boolean defaultValue);
	
	/**
	 * Get's the int value from the property
	 * 
	 * @param key
	 * @param defaultValue
	 * @return int
	 */
	int getInt(String key, int defaultValue);


}
