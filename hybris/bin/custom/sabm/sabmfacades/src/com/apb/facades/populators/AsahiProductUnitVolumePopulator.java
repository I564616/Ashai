package com.apb.facades.populators;

import com.apb.core.model.UnitVolumeModel;
import com.apb.facades.product.data.UnitVolumeData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * The Class AsahiProductUnitVolumePopulator.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiProductUnitVolumePopulator implements Populator<UnitVolumeModel, UnitVolumeData>
{

	/**
	 * Populate.
	 *
	 * @param source the UnitVolumeModel
	 * @param target the UnitVolumeData
	 * @throws ConversionException the conversion exception
	 */
	public void populate(final UnitVolumeModel source, final UnitVolumeData target) throws ConversionException
	{
		if (null != source)
		{
			target.setCode(source.getCode());
			target.setName(source.getName());
		}
	}
}
