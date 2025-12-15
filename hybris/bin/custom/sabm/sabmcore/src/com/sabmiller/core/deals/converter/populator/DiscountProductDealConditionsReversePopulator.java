/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.deals.vo.DealsResponse.DealItem;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.product.SabmUnitService;


/**
 * @author joshua.a.antony
 *
 */

public class DiscountProductDealConditionsReversePopulator implements Populator<DealItem, DealModel>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;


	@Override
	public void populate(final DealItem source, final DealModel target) throws ConversionException
	{
		final ProductDealConditionModel productDealConditionModel = modelService.create(ProductDealConditionModel.class);
		productDealConditionModel.setProductCode(source.getMaterial());
		productDealConditionModel.setMinQty(source.getMinimumQuantity());
		if (!StringUtils.isBlank(source.getUnitOfMeasure()))
		{
			productDealConditionModel.setUnit(unitService.getUnitForCode(source.getUnitOfMeasure()));
		}
		productDealConditionModel.setDealCode(target.getCode());

		final List<AbstractDealConditionModel> conditions = new ArrayList<AbstractDealConditionModel>();
		conditions.add(productDealConditionModel);

		target.getConditionGroup().setDealConditions(conditions);
	}

}
