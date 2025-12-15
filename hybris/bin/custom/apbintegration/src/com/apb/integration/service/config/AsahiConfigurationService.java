package com.apb.integration.service.config;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public interface AsahiConfigurationService extends ConfigurationService{

	/**
	 * Check if config key exist.
	 *
	 * @param configKey the config key
	 * @return the string
	 */
	String checkIfConfigKeyExist(String configKey);

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	String getString(String key, String defaultValue);
	
	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the boolean
	 */
	boolean getBoolean(String key, boolean defaultValue);
	
	
	/**
	 * Gets the decrypted password.
	 *
	 * @param encryptedPassword the encrypted password
	 * @param defaultValue the default value
	 * @return the decrypted password
	 */
	String getDecryptedPassword(String encryptedPasswordKey,String defaultValue); 

}
