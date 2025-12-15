package com.apb.core.dao.config;


/**
 * The Interface AsahiConfigurationDao.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiConfigurationDao {
	
		/**
		 * Gets the config value for key.
		 *
		 * @param messageCode the message code
		 * @return the config value for key
		 */
		String getConfigValueForKey(final String messageCode);
}
