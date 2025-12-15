/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;


/**
 * The Class RepDrivenDealStatusAttributeHandler.
 *
 * @author a.d.esposito
 */
public class IsScaleDealStatusAttributeHandler implements DynamicAttributeHandler<Boolean, DealModel>
{

	@Override
	public Boolean get(final DealModel deal)
	{
		final DealConditionGroupModel conditionGroup = deal.getConditionGroup();
		if (conditionGroup != null)
		{
			return BooleanUtils.toBoolean(conditionGroup.getMultipleScales());
		}
		return false;
	}


	/**
	 * setter of dynamic attribute unitList, throws exception because this is a dynamic attribute, only to fetch data.
	 *
	 */
	@Override
	public void set(final DealModel deal, final Boolean repDrivenDealStatus)
	{
		throw new UnsupportedOperationException("Setting of dynamic attribute 'scaleDeal' of DealModel is disabled!");
	}
}
