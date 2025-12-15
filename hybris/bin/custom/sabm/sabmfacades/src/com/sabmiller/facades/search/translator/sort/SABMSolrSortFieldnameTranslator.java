package com.sabmiller.facades.search.translator.sort;

import de.hybris.platform.solrfacetsearch.config.IndexedTypeSortField;

public interface SABMSolrSortFieldnameTranslator {

    String translate(final IndexedTypeSortField indexedTypeSortField);
}
