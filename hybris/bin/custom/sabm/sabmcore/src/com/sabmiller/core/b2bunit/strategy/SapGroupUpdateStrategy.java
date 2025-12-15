/**
 *
 */
package com.sabmiller.core.b2bunit.strategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy;
import com.sabmiller.facades.b2bunit.data.B2BUnitGroup;


/**
 * @author joshua.a.antony
 *
 */
public class SapGroupUpdateStrategy
		extends
		AbstractRelationshipUpdateStrategy<B2BUnitModel, B2BUnitGroup, B2BUnitGroupModel, Populator<B2BUnitGroup, B2BUnitGroupModel>>
{
	@Resource(name = "b2bUnitGroupReversePopulator")
	private Populator<B2BUnitGroup, B2BUnitGroupModel> b2bUnitGroupReversePopulator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#lookup(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected B2BUnitGroupModel lookup(final B2BUnitModel b2bUnitModel, final B2BUnitGroup sapGroup)
	{
		final B2BUnitGroupModel groupModel = b2bUnitModel.getSapGroup();
		return groupModel != null ? groupModel : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#getRelatedEntityModelConverter()
	 */
	@Override
	protected Populator<B2BUnitGroup, B2BUnitGroupModel> getRelatedEntityModelPopulator()
	{
		return b2bUnitGroupReversePopulator;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#createModel()
	 */
	@Override
	protected B2BUnitGroupModel createModel(final B2BUnitModel model)
	{
		return getModelService().create(B2BUnitGroupModel.class);
	}

}
