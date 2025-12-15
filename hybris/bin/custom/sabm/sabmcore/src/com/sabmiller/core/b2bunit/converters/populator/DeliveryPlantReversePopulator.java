/**
 *
 */
package com.sabmiller.core.b2bunit.converters.populator;

import de.hybris.platform.converters.Populator;

import com.sabmiller.core.model.PlantModel;


/**
 * @author joshua.a.antony
 *
 */
public class DeliveryPlantReversePopulator implements Populator<String, PlantModel>
{

	@Override
	public void populate(final String source, final PlantModel target)
	{
		if (source != null)
		{
			target.setPlantId(source);
		}
	}

}
