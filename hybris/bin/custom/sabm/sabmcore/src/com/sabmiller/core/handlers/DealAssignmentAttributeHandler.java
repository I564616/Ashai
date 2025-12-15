/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealModel;


/**
 * The Class ProductUnitListHandler.
 *
 * @author a.d.esposito
 */
public abstract class DealAssignmentAttributeHandler implements DynamicAttributeHandler<List<DealAssigneeModel>, DealModel>
{

	public abstract boolean isExcludeAssignee();

	@Override
	public List<DealAssigneeModel> get(final DealModel deal)
	{
		final List<DealAssigneeModel> assignees = new ArrayList<DealAssigneeModel>();
		for (final DealAssigneeModel dealAssignee : CollectionUtils.emptyIfNull(deal.getAssignees()))
		{
			if (isExcludeAssignee() && BooleanUtils.toBoolean(dealAssignee.getExclude()))
			{
				assignees.add(dealAssignee);
			}
			else if (!isExcludeAssignee() && !BooleanUtils.toBoolean(dealAssignee.getExclude()))
			{
				assignees.add(dealAssignee);
			}
		}

		return assignees;
	}


	/**
	 * setter of dynamic attribute unitList, throws exception because this is a dynamic attribute, only to fetch data.
	 *
	 */
	@Override
	public void set(final DealModel deal, final List<DealAssigneeModel> assignees)
	{
		throw new UnsupportedOperationException(
				"Setting of dynamic attribute 'assignTo' and 'excludeFrom' of DealModel is disabled!");
	}
}
