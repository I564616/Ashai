/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.List;

import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealModel;


/**
 * The Class ProductUnitListHandler.
 *
 * @author a.d.esposito
 */
public class DealAssignToAttributeHandler extends DealAssignmentAttributeHandler implements
		DynamicAttributeHandler<List<DealAssigneeModel>, DealModel>
{

	@Override
	public boolean isExcludeAssignee()
	{
		return false;
	}

}
