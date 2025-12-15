package com.apb.core.service.message;

import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * The Interface AsahiMessageService.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiMessageService extends ConfigurationService{

	/**
	 * Check if message key exist.
	 *
	 * @param messageKey the message key
	 * @return the string
	 */
	String checkIfMessageKeyExist(String messageKey);

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	String getString(String key, String defaultValue);

}
