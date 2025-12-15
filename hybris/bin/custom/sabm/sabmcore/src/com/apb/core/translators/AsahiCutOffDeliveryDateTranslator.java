package com.apb.core.translators;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.CutOffDeliveryDateModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * @author Ashish.Monga
 *This translator gets the CutOffDeliveryDateModels for the cutOffDeliveryDate codes, and import the same.
 */
public class AsahiCutOffDeliveryDateTranslator extends AbstractValueTranslator {
	
	/* (non-Javadoc)
	 * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#importValue(java.lang.String, de.hybris.platform.jalo.Item)
	 * This method gets the CutOffDeliveryDateModels corresponding to the entered codes.
	 */
	@Override
	public Object importValue(final String cutOffDelDates, final Item item) throws JaloInvalidParameterException {
		
		List<CutOffDeliveryDateModel> cutOffDeliveryDateModels = null;
		if(StringUtils.isNotEmpty(cutOffDelDates)){
			final String cutOffDeliveryDates = cutOffDelDates.replaceAll(ApbCoreConstants.STRING_SEPARATOR_WHITESPACE,"");
			cutOffDeliveryDateModels = new ArrayList<>();
			for(final String cutOffDeliveryDateCode : cutOffDeliveryDates.split(ApbCoreConstants.STRING_SEPARATOR_PIPE)){
				final CutOffDeliveryDateModel cutOffDeliveryDateModel = new CutOffDeliveryDateModel();
				cutOffDeliveryDateModel.setCode(cutOffDeliveryDateCode);
				final FlexibleSearchService flexibleSearchService = (FlexibleSearchService) Registry.getApplicationContext().getBean(
						"flexibleSearchService");
				final List<CutOffDeliveryDateModel> cutOffDelDateModels = flexibleSearchService.getModelsByExample(cutOffDeliveryDateModel);
				if(CollectionUtils.isNotEmpty(cutOffDelDateModels)){
					cutOffDeliveryDateModels.add(cutOffDelDateModels.get(0));
				}
			}
		}
		return cutOffDeliveryDateModels;
	}
	
	@Override
	public String exportValue(final Object arg0) throws JaloInvalidParameterException {
		return null;
	}
	
}
