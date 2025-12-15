package com.apb.integration.dataimport.batch.translator;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

public class OrderTemplateCodeTranslator extends AbstractValueTranslator{
	

	@Override
	public String exportValue(Object value) throws JaloInvalidParameterException {
		return value == null ? "" : value.toString();
	}

	@Override
	public Object importValue(String code, Item item) throws JaloInvalidParameterException {
		 if (!StringUtils.isBlank(code)) { 
			 final PersistentKeyGenerator generator = (PersistentKeyGenerator)Registry.getApplicationContext().getBean(
						"orderCodeGenerator");
			 if (null != generator) {
				 Object generatedValue = generator.generate();
		   		
		   		if (generatedValue instanceof String)
		   		{
		   			return ((String) generatedValue);
		   		}
		   		    return (String.valueOf(generatedValue));
			 }
		   		
		 }
		return code;
	}

}
