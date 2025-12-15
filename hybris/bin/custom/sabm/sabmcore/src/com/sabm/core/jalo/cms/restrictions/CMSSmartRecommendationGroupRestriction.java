package com.sabm.core.jalo.cms.restrictions;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;
import org.apache.log4j.Logger;

public class CMSSmartRecommendationGroupRestriction extends GeneratedCMSSmartRecommendationGroupRestriction
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( CMSSmartRecommendationGroupRestriction.class.getName() );
	
	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem( ctx, type, allAttributes );
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}

	/**
	 * @param sessionContext
	 * @deprecated
	 */
	@Override
	public String getDescription(SessionContext sessionContext) {
		final StringBuilder result = new StringBuilder();
		result.append("Display for Smart Recommendation Group : ").append(getSmartRecommendationGroup(sessionContext).getCode());
		return result.toString();
	}
}
