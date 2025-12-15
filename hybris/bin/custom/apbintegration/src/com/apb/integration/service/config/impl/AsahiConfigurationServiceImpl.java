package com.apb.integration.service.config.impl;

import java.util.HashSet;
import java.util.Locale;

import jakarta.annotation.Resource;

import com.apb.integration.dao.config.AsahiConfigurationDao;
import com.apb.integration.service.config.AsahiConfigurationService;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.impl.DefaultConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.util.encryption.ValueEncryptor;

/**
 * The Class AsahiConfigurationServiceImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiConfigurationServiceImpl extends DefaultConfigurationService implements AsahiConfigurationService{

	/** The configuration dao. */
	@Resource(name="asahiConfigurationDao")
	private AsahiConfigurationDao asahiConfigurationDao;
	
	/** The catalog version service. */
	@Resource(name="catalogVersionService")
	private CatalogVersionService catalogVersionService;
	
	/** The i 18 n service. */
	@Resource(name="i18nService")
	private I18NService i18nService;
	
	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	@Override
	public String getString(String key, String defaultValue){
		
		//getting Config Value from Database
		final String configValueFromDB = this.asahiConfigurationDao.getConfigValueForKey(key);

		if(null!=configValueFromDB){
			return configValueFromDB;
		}else{
			//getting Config Value from Property File
			String configValueFromPropertyFile = super.getConfiguration().getString(key, defaultValue);
			return (null!=configValueFromPropertyFile) ? configValueFromPropertyFile:defaultValue;
		}
	}
	
	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the boolean
	 */
	@Override
	public boolean getBoolean(String key, boolean defaultValue){
		
		//getting Config Value from Database
		String configValue = this.asahiConfigurationDao.getConfigValueForKey(key);
		if(null!=configValue){
			if("true".equalsIgnoreCase(configValue)){
				return true;
			}else{
				return false;
			}
		}else{
			//getting Config Value from Property File
			return super.getConfiguration().getBoolean(key, defaultValue);
		}
	}
	
	/**
	 * Check if config key exist.
	 *
	 * @param configKey the config key
	 * @return the string
	 */
	@Override
	public String checkIfConfigKeyExist(String configKey) {
		//getting Config Value from Database
		return this.asahiConfigurationDao.getConfigValueForKey(configKey);
	}
	
	
	/* (non-Javadoc)
	 * @see com.apb.integration.service.config.AsahiConfigurationService#getDecryptedPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public String getDecryptedPassword(String encryptedPasswordKey, String defaultValue) {
		
		String encryptedPassword = getString(encryptedPasswordKey, defaultValue); 
		if(null != encryptedPassword)
		{
			final ValueEncryptor engine = Registry.getMasterTenant().getValueEncryptor();
			return engine.decrypt(encryptedPassword);
		}
		return defaultValue; 
	}
	
}
