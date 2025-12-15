/**
 *
 */
package com.sabmiller.core.b2bunit.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.facades.b2bunit.data.UnloadingPoint;


/**
 * @author joshua.a.antony
 *
 */
public class UnloadingPointPopulator implements Populator<UnloadingPointModel, UnloadingPoint>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final UnloadingPointModel source, final UnloadingPoint target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setMap(source.getMap());
	}

}
