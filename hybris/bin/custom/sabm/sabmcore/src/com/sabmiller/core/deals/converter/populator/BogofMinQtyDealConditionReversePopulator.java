/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.deals.vo.DealsResponse.DealItem;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.MinQtyDealConditionModel;
import com.sabmiller.core.product.SabmUnitService;


/**
 * @author joshua.a.antony
 *
 */

public class BogofMinQtyDealConditionReversePopulator implements Populator<DealItem, DealModel>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;


	@Override
	public void populate(final DealItem source, final DealModel target)
	{
		final MinQtyDealConditionModel minQtyDealConditionModel = modelService.create(MinQtyDealConditionModel.class);
		minQtyDealConditionModel.setQuantity(Double.valueOf(source.getFreeGoodsQty().trim()).intValue());
		minQtyDealConditionModel.setMinQty(source.getMinimumQuantity());

		if (!StringUtils.isBlank(source.getUnitOfMeasure()))
		{
			minQtyDealConditionModel.setUnit(unitService.getUnitForCode(source.getUnitOfMeasure()));
		}

		final List<AbstractDealConditionModel> conditions = new ArrayList<AbstractDealConditionModel>();
		conditions.add(minQtyDealConditionModel);
		target.getConditionGroup().setDealConditions(conditions);
	}

}
