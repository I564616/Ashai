/**
 *
 */
package com.sabmiller.core.search.solrfacetsearch.impl;

import com.sabmiller.core.constants.SabmCoreConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.impl.DefaultSolrProductSearchService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;

import jakarta.annotation.Resource;
import java.util.*;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * The SABM solr product search service
 *
 * @author xiaowu.a.zhang
 * @date 04/07/2016
 *
 */
public class DefaultSABMSolrProductSearchService<ITEM> extends DefaultSolrProductSearchService<ITEM> {
    protected static final Logger LOG = Logger.getLogger(DefaultSABMSolrProductSearchService.class);


    @SuppressWarnings("rawtypes")
    @Override
    protected ProductCategorySearchPageData<SolrSearchQueryData, ITEM, CategoryModel> doSearch(
            SolrSearchQueryData searchQueryData, PageableData pageableData) {
        return super.doSearch(searchQueryData,pageableData);
    }

}
