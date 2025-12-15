/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import com.sabmiller.core.deals.vo.DealsResponse.DealItem;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.DealModel;



/**
 * For Once off deals. These deals are exactly similar to the Pricing Discount deals, with additional attributes.
 *
 * @author joshua.a.antony
 */

public class OnceOffDealsReverseConverter extends DiscountDealsReverseConverter
{
	@Override
	protected void populateAdditionalDetails(final DealItem discountItem, final DealModel target)
	{
		target.setMaxConditionBaseValue(discountItem.getMaxConditionBaseValue());
		target.setMaxConditionValue(discountItem.getMaxConditionValue());
		target.setMaxNumberOfOrders(discountItem.getMaxNumberOfOrders());
		target.setUsedConditionBaseValue(discountItem.getUsedConditionBaseValue());
		target.setUsedConditionValue(discountItem.getUsedConditionValue());
		target.setUsedNumberOfOrders(discountItem.getUsedNumberOfOrders());
	}

	@Override
	protected DealTypeEnum getDealType()
	{
		return DealTypeEnum.LIMITED;
	}
}
