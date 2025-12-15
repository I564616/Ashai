/**
 *
 */
package com.sabmiller.core.strategy;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.sabmiller.core.b2bunit.strategy.UnloadingPointUpdateStrategy;


/**
 * @author joshua.a.antony
 *
 *         Generic framework to create/update the relationships within an entity. This is useful if we just have the
 *         entity data with all the relationships but are unaware if the relationships exist in the database or not.
 *         This can be used during the entity import process from external system to Hybris. Example : Assume that there
 *         is a request from the external system to update a B2BUnit with all its relationships. In that case, an update
 *         strategy needs to be created for each of the related entities (example : address, unloading points etc) by
 *         extending this class. {@link UnloadingPointUpdateStrategy} is a classic example of the use case.
 *
 *         This class shields the core implementations of the update. All the subclass needs to do is override lookup(),
 *         createModel() and getRelatedEntityModelPopulator() methods and provide the corresponding implementation - it
 *         need not worry if the entity already exist in the data store or not - this class would take care of that
 *         implementation in its deriveModel() method.
 *
 */
public abstract class AbstractRelationshipUpdateStrategy<MainEntityModel, RelatedEntityData, RelatedEntityModel, RelatedEntityModelPopulator extends Populator<RelatedEntityData, RelatedEntityModel>>
		implements RelationshipUpdateStrategy<MainEntityModel, RelatedEntityData, RelatedEntityModel>
{
	private final Logger LOG = Logger.getLogger(this.getClass());

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	public List<RelatedEntityModel> deriveModelList(final MainEntityModel mainEntityModel,
			final List<RelatedEntityData> relatedEntitiesData)
	{

		LOG.info("In deriveModelList() => mainEntityModel : " + mainEntityModel + " , relatedEntitiesData : " + relatedEntitiesData);

		final List<RelatedEntityModel> relatedEntityModels = new ArrayList<RelatedEntityModel>();
		for (final RelatedEntityData eachRelatedEntityData : relatedEntitiesData)
		{
			if (eachRelatedEntityData != null)
			{
				relatedEntityModels.add(deriveModel(mainEntityModel, eachRelatedEntityData));
			}
		}
		return relatedEntityModels;
	}

	/**
	 * Checks if the related entity exist for the model in the data store. If the related entity does not exist, a new
	 * entry is created and persisted in the data store. The actual call to the datastore to check if the relationship
	 * entity exist or not needs to be handled in the subclass through the lookup() method
	 */
	@Override
	public RelatedEntityModel deriveModel(final MainEntityModel mainEntityModel, final RelatedEntityData relatedEntitiesData)
	{
		RelatedEntityModel model = lookup(mainEntityModel, relatedEntitiesData);
		if (model == null)
		{
			LOG.info("Model not found : new model created ");
			model = createModel(mainEntityModel);
		}
		LOG.info("Invoking populate() on " + getRelatedEntityModelPopulator());

		getRelatedEntityModelPopulator().populate(relatedEntitiesData, model);
		modelService.save(model);
		return model;
	}

	protected abstract RelatedEntityModel lookup(MainEntityModel model, RelatedEntityData relatedEntityData);

	protected abstract RelatedEntityModelPopulator getRelatedEntityModelPopulator();

	protected abstract RelatedEntityModel createModel(MainEntityModel mainEntityModel);

	protected ModelService getModelService()
	{
		return modelService;
	}
}
