package com.sabmiller.core.search.solrfacetsearch.solr.impl;

import com.sabmiller.core.search.solrfacetsearch.solr.SABMSolrManagedResourceService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrCloudSearchProvider;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SABMSolrCloudSearchProvider extends SolrCloudSearchProvider {
    
    private SABMSolrManagedResourceService sabmSolrManagedResourceService;

    @Override protected void updateSynonymsOnServer(SolrClient solrClient, String indexName, String managedResourcePath,
            Map<String, Set<String>> synonyms, Map<String, Set<String>> serverSynonyms)
            throws SolrServiceException, SolrServerException, IOException {

        final SABMSolrManagedResourceService.DeleteFunction deleteFunction = (sc, in, path) -> this.executeDelete(sc, in, path);
        final SABMSolrManagedResourceService.PostFunction postFunction = (sc, in, path, payload) -> executePost(sc, in, path, payload);
        final SABMSolrManagedResourceService.EncodeFunction encodeFunction = (string) -> encode(string);

        getSabmSolrManagedResourceService()
                .updateSynonymsOnServer(solrClient, indexName, managedResourcePath, synonyms, serverSynonyms, deleteFunction, postFunction,
                        encodeFunction);
    }

    protected SABMSolrManagedResourceService getSabmSolrManagedResourceService() {
        return sabmSolrManagedResourceService;
    }

    public void setSabmSolrManagedResourceService(SABMSolrManagedResourceService sabmSolrManagedResourceService) {
        this.sabmSolrManagedResourceService = sabmSolrManagedResourceService;
    }
}
