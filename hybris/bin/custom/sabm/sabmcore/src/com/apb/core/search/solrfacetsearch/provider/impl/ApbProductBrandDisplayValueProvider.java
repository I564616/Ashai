package com.apb.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.CategoryCodeValueProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;


/**
 * @author Ganesh.Muddliyar
 * @see ValueProvider for the hiding level4 brands.
 */
public class ApbProductBrandDisplayValueProvider extends CategoryCodeValueProvider implements FieldValueProvider
{
	/**
	 * Separator for category code and boolean flag
	 */
	public static final String VALUE_SEPARATOR = "_";

	@Override
	protected Object getPropertyValue(final Object category)
	{
		final Object categoryCode = super.getPropertyValue(category);
		final Object brandDisplay = super.getPropertyValue(category, "hideBrandFacet");
		return categoryCode + VALUE_SEPARATOR + brandDisplay;
	}


}
