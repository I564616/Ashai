package com.sabmiller.facades.search.converters.populator;

import de.hybris.platform.core.PK;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSortField;
import de.hybris.platform.solrfacetsearch.search.OrderField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchQuerySortsPopulator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sabmiller.facades.search.translator.sort.SABMSolrSortFieldnameTranslator;

public class SABMFacetSearchQuerySortsPopulator extends FacetSearchQuerySortsPopulator {

    private Map<String, SABMSolrSortFieldnameTranslator> solrSortFieldnameTranslatorMap;
    /**
     * This method is overridden to add support for custom sort options like bestseller and popular
     * @param searchQueryConverterData
     * @param promotedItems
     * @param sorts
     */
    @Override protected void buildSorts(final SearchQueryConverterData searchQueryConverterData, final List<PK> promotedItems, final List<OrderField> sorts) {
   	 
 		if(searchQueryConverterData.getFacetSearchContext().getIndexedType().getIdentifier().equalsIgnoreCase("sabmStoreProductType"))
 		{
        final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
        final IndexedTypeSort currentSort = searchQueryConverterData.getFacetSearchContext().getNamedSort();
        if (currentSort != null) {

            if (currentSort.isApplyPromotedItems()) {
                promotedItems.addAll(searchQuery.getPromotedItems());
            }


            final Iterator var7 = currentSort.getFields().iterator();


            while(var7.hasNext()) {


                final IndexedTypeSortField indexedTypeSortField = (IndexedTypeSortField)var7.next();

				final String indexPropertyName = getFieldName(indexedTypeSortField);
				if (indexPropertyName != null)
				{
					//this is the only change made to the method, replaced with getFieldName method
					sorts.add(new OrderField(indexPropertyName,
							indexedTypeSortField.isAscending() ? OrderField.SortOrder.ASCENDING : OrderField.SortOrder.DESCENDING));
				}
            }

        } else {
            promotedItems.addAll(searchQuery.getPromotedItems());
            sorts.addAll(searchQuery.getSorts());
        }
 		}
 		else
        {
            super.buildSorts(searchQueryConverterData,promotedItems,sorts);
        }
    }

    protected String getFieldName(final IndexedTypeSortField indexedTypeSortField){
        final SABMSolrSortFieldnameTranslator solrSortFieldnameTranslator = getSolrSortFieldnameTranslatorMap().get(indexedTypeSortField.getFieldName());
        return solrSortFieldnameTranslator != null?solrSortFieldnameTranslator.translate(indexedTypeSortField):indexedTypeSortField.getFieldName();
    }

    protected Map<String, SABMSolrSortFieldnameTranslator> getSolrSortFieldnameTranslatorMap() {
        return solrSortFieldnameTranslatorMap;
    }

    public void setSolrSortFieldnameTranslatorMap(final Map<String, SABMSolrSortFieldnameTranslator> solrSortFieldnameTranslatorMap) {
        this.solrSortFieldnameTranslatorMap = solrSortFieldnameTranslatorMap;
    }
}
