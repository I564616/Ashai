package com.apb.core.service.message.impl;

import java.util.HashSet;
import java.util.Locale;

import jakarta.annotation.Resource;

import com.apb.core.dao.message.AsahiMessageDao;
import com.apb.core.service.message.AsahiMessageService;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.config.impl.DefaultConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * The Class AsahiMessageServiceImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiMessageServiceImpl extends DefaultConfigurationService implements AsahiMessageService{

	/** The asahi message dao. */
	@Resource(name="asahiMessageDao")
	private AsahiMessageDao asahiMessageDao;
	
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
		
		Locale locale = this.i18nService.getCurrentLocale();
		//getting Message Value from Database
		final String messageValueFromDB = this.asahiMessageDao.getMessageValueForKey(key,
				this.getSessionCatalogVersions(), locale.getLanguage());

		return (null!=messageValueFromDB) ? messageValueFromDB: defaultValue;
	}
	
	/**
	 * Check if message key exist.
	 *
	 * @param messageKey the message key
	 * @return the string
	 */
	public String checkIfMessageKeyExist(String messageKey) {
		Locale locale = this.i18nService.getCurrentLocale();
		//getting Message Value from Database
		return this.asahiMessageDao.getMessageValueForKey(messageKey, this.getSessionCatalogVersions(), locale.getLanguage());
	}
	
	/**
	 * This method written to get the set of catalog versions.
	 * 
	 * @return HashSet<CatalogVersionModel>
	 */
	private HashSet<CatalogVersionModel> getSessionCatalogVersions(){
		return new HashSet<CatalogVersionModel>(this.catalogVersionService.getSessionCatalogVersions());
	}
}
