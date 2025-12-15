package com.sabmiller.core.search.solrfacetsearch.solr;

import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface SABMSolrManagedResourceService {

    void updateSynonymsOnServer(SolrClient solrClient, String indexName, String managedResourcePath, Map<String, Set<String>> synonyms,
            Map<String, Set<String>> serverSynonyms, final DeleteFunction deleteFunction, final PostFunction postFunction,
            final EncodeFunction encodeFunction) throws SolrServiceException, SolrServerException, IOException;

    @FunctionalInterface interface DeleteFunction {
        void delete(final SolrClient solrClient, final String indexName, final String path) throws SolrServerException, IOException;
    }

    @FunctionalInterface interface PostFunction {
        void post(final SolrClient solrClient, final String indexName, final String path, final Object payload)
                throws SolrServerException, IOException;
    }

    @FunctionalInterface interface EncodeFunction {
        String encode(final String string) throws SolrServiceException;
    }
}
