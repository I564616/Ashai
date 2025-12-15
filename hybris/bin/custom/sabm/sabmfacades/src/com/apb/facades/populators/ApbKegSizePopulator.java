package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;

import com.apb.core.model.KegReturnSizeModel;
import com.apb.facades.kegreturn.data.KegSizeData;


/**
 * ApbKegSizePopulator implement {@link Populator}
 */
public class ApbKegSizePopulator implements Populator<KegReturnSizeModel, KegSizeData>
{

	@Override
	public void populate(final KegReturnSizeModel source, final KegSizeData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setCode(source.getCode());
		target.setKegSize(source.getKegSize());
		target.setKegQuantity(source.getKegQuantity());
	}
}
