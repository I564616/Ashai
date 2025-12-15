package com.sabmiller.facades.search.translator.sort.impl;

import com.sabmiller.facades.search.translator.sort.SABMAbstractSolrSortFieldnameTranslator;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSortField;

import static com.sabmiller.facades.constants.SabmFacadesConstants.SOLR_POPULAR_INDEXED_PROPERTY_PREFIX;

public class SABMPopularSolrSortFieldnameTranslator extends SABMAbstractSolrSortFieldnameTranslator {

    @Override public String translate(final IndexedTypeSortField indexedTypeSortField) {
        return constructSolrSortFieldNameWithGivenSortPrefix(SOLR_POPULAR_INDEXED_PROPERTY_PREFIX);
    }
}
