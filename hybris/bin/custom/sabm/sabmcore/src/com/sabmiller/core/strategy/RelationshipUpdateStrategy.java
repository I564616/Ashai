/**
 *
 */
package com.sabmiller.core.strategy;

import java.util.List;


/**
 * @author joshua.a.antony
 *
 *         Strategy to create/update relationships for model.
 *
 */
public interface RelationshipUpdateStrategy<MainEntityModel, RelatedEntityData, RelatedEntityModel>
{
	public List<RelatedEntityModel> deriveModelList(final MainEntityModel mainEntityModel,
			final List<RelatedEntityData> relatedEntitiesData);

	public RelatedEntityModel deriveModel(final MainEntityModel mainEntityModel, final RelatedEntityData relatedEntityData);

}
