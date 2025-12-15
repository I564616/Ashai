package com.apb.core.translators;

import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.apb.core.constants.ApbCoreConstants;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import java.text.ParseException;

/**
 * @author Ashish.Monga
 * Converts timeSlot in hh:mm:ss format to date format.
 *
 */
public class AsahiDeliveryTimeSlotTranslator extends AbstractValueTranslator {

	private static final Logger LOGGER = LogManager.getLogger(AsahiDeliveryTimeSlotTranslator.class);
	
	@Override
	public Object importValue(final String timeSlot, final Item item) throws JaloInvalidParameterException {
		
		final SimpleDateFormat format = new SimpleDateFormat(ApbCoreConstants.DELIVERY_DATETIMESLOT_PATTERN);

		//convert String to Date
				try {
					 final Date date = new Date(System.currentTimeMillis());
					 final SimpleDateFormat formatter = new SimpleDateFormat(ApbCoreConstants.DATE_PATTERN_DDMMYYYY);
					 
					final StringBuilder dateTimeSlot = new StringBuilder(formatter.format(date));
					 dateTimeSlot.append(timeSlot);
					
					 return format.parse(dateTimeSlot.toString());
					 
				} catch (ParseException e) {
					LOGGER.error("Parse Exception occured" + e.getMessage());
				}
		
		return null;
	}
	
	@Override
	public String exportValue(final Object arg0) throws JaloInvalidParameterException {
		return null;
	}

}
