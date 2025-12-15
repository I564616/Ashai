/**
 *
 */
package com.sabmiller.core.search.listners;

import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener;

import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * Restricts search to "searchable" products
 *
 */
public class ProductSearchabeListener implements FacetSearchListener
{

	@Override
	public void beforeSearch(final FacetSearchContext facetSearchContext) throws FacetSearchException
	{

		if (!facetSearchContext.getIndexedType().getIdentifier().equalsIgnoreCase("sabmStoreProductType"))
		{
			return;
		}
		if (facetSearchContext.getIndexedType().getCode().equals(SabmCoreConstants.SOLR_INDEXTYPE_PRODUCT))
		{
			facetSearchContext.getSearchQuery().addFacetValue("searchable", "true");
		}

	}

	@Override
	public void afterSearch(final FacetSearchContext paramFacetSearchContext) throws FacetSearchException
	{
		//nothing to do here
	}

	@Override
	public void afterSearchError(final FacetSearchContext paramFacetSearchContext) throws FacetSearchException
	{
		//Empty method.
	}

}
