package com.apb.facades.populators;

import com.sabmiller.core.model.CustImpFailedRecordsModel;

import de.hybris.platform.commercefacades.customer.data.AsahiB2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 *
 */
public class CustImportFailedPopulator implements Populator<AsahiB2BUnitData, CustImpFailedRecordsModel>
{
	@Override
	public void populate(AsahiB2BUnitData source, CustImpFailedRecordsModel target) throws ConversionException {
		target.setUid(source.getUid());
		
			target.setAccountNum(source.getAccountNum());
		
			target.setName(source.getName());
		
			target.setPurposeCode(source.getPurposeCode());
		
			target.setSalesRepName(source.getSalesRepName());
		
		if(null != source.getActive())
			target.setActive(source.getActive());
	}
}
