package com.apb.core.translators;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;

/**
 * The Class AsahiMessageImpexTranslator.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiMessageImpexTranslator extends AbstractValueTranslator{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiMessageImpexTranslator.class);
	
	/**
	 * Import value.
	 *
	 * @param newMessageValue the new message value
	 * @param item the item
	 * @return the object
	 * @throws JaloInvalidParameterException the jalo invalid parameter exception
	 */
	@Override
	public Object importValue(String newMessageValue, Item item)
			throws JaloInvalidParameterException {
		try {
			logger.debug("Validating the message impex with uid : " + (String) item.getAttribute("uid"));
			String existingMessageValue = (String) item.getAttribute("messageValue");
			return (null!=existingMessageValue)? existingMessageValue: newMessageValue;
		} catch (JaloSecurityException e1) {
			logger.error("Error has occured while validating the message impex "+e1.getMessage());
		}
		
		return newMessageValue;
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
