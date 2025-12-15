
package com.sabmiller.core.search.solrfacetsearch.impl;


import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.RawQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hybris.platform.solrfacetsearch.search.impl.populators.AbstractFacetSearchQueryPopulator;
import org.apache.solr.client.solrj.SolrQuery;

    public class SABMFacetQueryFilterQueriesPopulator extends AbstractFacetSearchQueryPopulator {
        public SABMFacetQueryFilterQueriesPopulator() {
        }

        public void populate(SearchQueryConverterData source, SolrQuery target) {
            SearchQuery searchQuery = source.getSearchQuery();
            List<String> filterQueries = new ArrayList();
            this.addQueryFieldQueries(searchQuery, filterQueries);
            this.addRawQueries(searchQuery, filterQueries);
            Iterator var6 = filterQueries.iterator();

            while(var6.hasNext()) {
                String filterQuery = (String)var6.next();
                target.addFilterQuery(new String[]{filterQuery});
            }

        }

        protected void addQueryFieldQueries(SearchQuery searchQuery, List<String> queries) {
            Iterator var4 = searchQuery.getFilterQueries().iterator();

            while(var4.hasNext()) {
                QueryField filterQuery = (QueryField)var4.next();
                String query = this.convertQueryField(searchQuery, filterQuery);
                if(filterQuery.getField().equals("-code_string")){
                    query=query.replace("\\","");
                }
                queries.add(query);
            }

        }

        protected void addRawQueries(SearchQuery searchQuery, List<String> queries) {
            Iterator var4 = searchQuery.getFilterRawQueries().iterator();

            while(var4.hasNext()) {
                RawQuery filterRawQuery = (RawQuery)var4.next();
                String query = this.convertRawQuery(searchQuery, filterRawQuery);
                queries.add(query);
            }

        }
    }

