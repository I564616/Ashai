/**
 *
 */
package com.sabmiller.core.b2bunit.converters.populator;

import de.hybris.platform.converters.Populator;

import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.facades.b2bunit.data.B2BUnitGroup;


/**
 * @author joshua.a.antony
 *
 */
public class B2BUnitGroupReversePopulator implements Populator<B2BUnitGroup, B2BUnitGroupModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final B2BUnitGroup source, final B2BUnitGroupModel target)
	{
		if (source != null)
		{
			target.setPrimaryGroupKey(source.getPrimaryGroupKey());
			target.setPrimaryGroupDescription(source.getPrimaryGroupDescription());
			target.setSubGroupKey(source.getSubGroupKey());
			target.setSubGroupDescription(source.getSubGroupDescription());
			target.setGroupKey(source.getGroupKey());
			target.setGroupDescription(source.getGroupDescription());
			target.setSubChannel(source.getSubChannel());
			target.setSubChannelDescription(source.getSubChannelDescription());
		}
	}

}
