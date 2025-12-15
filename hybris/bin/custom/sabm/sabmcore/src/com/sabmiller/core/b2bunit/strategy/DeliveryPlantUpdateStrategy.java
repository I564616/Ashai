/**
 *
 */
package com.sabmiller.core.b2bunit.strategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import com.sabmiller.core.b2b.dao.SabmDeliveryPlantDao;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy;


/**
 * @author joshua.a.antony
 *
 */
public class DeliveryPlantUpdateStrategy extends
		AbstractRelationshipUpdateStrategy<B2BUnitModel, String, PlantModel, Populator<String, PlantModel>>
{

	@Resource(name = "sabmDeliveryPlantDao")
	private SabmDeliveryPlantDao deliveryPlantDao;

	@Resource(name = "deliveryPlantReversePopulator")
	private Populator<String, PlantModel> deliveryPlantReversePopulator;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#lookup(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected PlantModel lookup(final B2BUnitModel b2bUnitModel, final String plantId)
	{
		return deliveryPlantDao.lookupPlant(plantId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#getRelatedEntityModelConverter()
	 */
	@Override
	protected Populator<String, PlantModel> getRelatedEntityModelPopulator()
	{
		return deliveryPlantReversePopulator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#createModel()
	 */
	@Override
	protected PlantModel createModel(final B2BUnitModel model)
	{
		return getModelService().create(PlantModel.class);
	}

}
