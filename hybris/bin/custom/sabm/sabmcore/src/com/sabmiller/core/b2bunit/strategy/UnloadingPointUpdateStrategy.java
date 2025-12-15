/**
 *
 */
package com.sabmiller.core.b2bunit.strategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import com.sabmiller.core.b2bunit.converters.populator.UnloadingPointReversePopulator;
import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy;
import com.sabmiller.facades.b2bunit.data.UnloadingPoint;


/**
 * @author joshua.a.antony
 *
 */
public class UnloadingPointUpdateStrategy
		extends
		AbstractRelationshipUpdateStrategy<B2BUnitModel, UnloadingPoint, UnloadingPointModel, Populator<UnloadingPoint, UnloadingPointModel>>
{


	@Resource(name = "unloadingPointReversePopulator")
	private UnloadingPointReversePopulator unloadingPointReversePopulator;

	@Override
	protected UnloadingPointModel lookup(final B2BUnitModel b2bUnitModel, final UnloadingPoint unloadingPoint)
	{
		if (b2bUnitModel.getUnloadingPoints() != null)
		{
			for (final UnloadingPointModel upm : b2bUnitModel.getUnloadingPoints())
			{
				if (upm.getCode().equals(unloadingPoint.getCode()))
				{
					return upm;
				}
			}
		}
		return null;
	}

	@Override
	protected Populator<UnloadingPoint, UnloadingPointModel> getRelatedEntityModelPopulator()
	{
		return unloadingPointReversePopulator;
	}

	@Override
	protected UnloadingPointModel createModel(final B2BUnitModel model)
	{
		return getModelService().create(UnloadingPointModel.class);
	}

}
