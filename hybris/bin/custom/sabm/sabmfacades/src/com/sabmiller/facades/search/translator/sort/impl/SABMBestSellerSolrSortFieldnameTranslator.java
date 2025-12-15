package com.sabmiller.facades.search.translator.sort.impl;

import com.sabmiller.facades.search.translator.sort.SABMAbstractSolrSortFieldnameTranslator;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSortField;

import static com.sabmiller.facades.constants.SabmFacadesConstants.SOLR_BESTSELLER_INDEXED_PROPERTY_PREFIX;

public class SABMBestSellerSolrSortFieldnameTranslator extends SABMAbstractSolrSortFieldnameTranslator {

    @Override public String translate(IndexedTypeSortField indexedTypeSortField) {
        return constructSolrSortFieldNameWithGivenSortPrefix(SOLR_BESTSELLER_INDEXED_PROPERTY_PREFIX);
    }
}
