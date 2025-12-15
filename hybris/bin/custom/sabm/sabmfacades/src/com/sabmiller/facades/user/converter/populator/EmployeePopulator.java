/**
 *
 */
package com.sabmiller.facades.user.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.sabmiller.facades.user.EmployeeData;


/**
 * @author dale.bryan.a.mercado
 *
 */
public class EmployeePopulator implements Populator<UserModel, EmployeeData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final UserModel source, final EmployeeData target) throws ConversionException
	{
		// YTODO Auto-generated method stub
		target.setName(source.getName());
		target.setNormalizedUid(source.getUid().replaceAll("\\W", "_"));
		target.setUid(source.getUid());
		target.setEmail(source.getUid());

	}

}
