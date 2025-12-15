package com.apb.core.translators;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;

/**
 * The Class AsahiConfigurationImpexTranslator.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiConfigurationImpexTranslator extends AbstractValueTranslator{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiConfigurationImpexTranslator.class);
	
	/**
	 * Import value.
	 *
	 * @param newConfigvalue the new configvalue
	 * @param item the item
	 * @return the object
	 * @throws JaloInvalidParameterException the jalo invalid parameter exception
	 */
	@Override
	public Object importValue(String newConfigvalue, Item item)
			throws JaloInvalidParameterException {
		try {
			logger.debug("Validating the config impex with configKey : " + (String) item.getAttribute("configKey"));
			String existingConfigValue = (String) item.getAttribute("configValue");
			return (null!=existingConfigValue)? existingConfigValue: newConfigvalue;
		} catch (JaloSecurityException e1) {
			logger.error("Error has occured while validating the config impex. "+e1.getMessage());
		}
		return newConfigvalue;
	}
	
	/**
	 * Export value.
	 *
	 * @param arg0 the arg 0
	 * @return the string
	 * @throws JaloInvalidParameterException the jalo invalid parameter exception
	 */
	@Override
	public String exportValue(Object arg0) throws JaloInvalidParameterException {
		return null;
	}
}
