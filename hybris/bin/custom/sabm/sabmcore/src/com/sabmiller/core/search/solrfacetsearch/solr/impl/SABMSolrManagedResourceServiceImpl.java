package com.sabmiller.core.search.solrfacetsearch.solr.impl;

import com.sabmiller.core.search.solrfacetsearch.solr.SABMSolrManagedResourceService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.*;

public class SABMSolrManagedResourceServiceImpl implements SABMSolrManagedResourceService {

    protected static final String MANAGED_INIT_ARGS_FIELD = "initArgs";
    protected static final String MANAGED_IGNORE_CASE_FIELD = "ignoreCase";
    protected static final String MANAGED_SYNONYMS_IGNORE_CASE_KEY = "solrfacetsearch.synonyms.filter.ignoreCase";
    protected static final String MANAGED_MAP_FIELD = "managedMap";

    private ConfigurationService configurationService;

    @Override public void updateSynonymsOnServer(SolrClient solrClient, String indexName, String managedResourcePath,
            Map<String, Set<String>> synonyms, Map<String, Set<String>> serverSynonyms, DeleteFunction deleteFunction,
            PostFunction postFunction, final EncodeFunction encodeFunction) throws SolrServiceException, SolrServerException, IOException {
        Objects.requireNonNull(deleteFunction, "deleteFunction is required to perform delete.");
        Objects.requireNonNull(postFunction, "postFunction is required to perform post.");
        Objects.requireNonNull(encodeFunction, "encodeFunction is required to perform encode.");

        final Set<String> synonymsToRemove = new HashSet();
        final Map<String, Set<String>> configSynonyms = new HashMap<>(synonyms);

        for (Map.Entry<String, Set<String>> entry : serverSynonyms.entrySet()) {

            final String synonym = entry.getKey();

            //if both are equal, then remove from configSynonym so it won't be resent

            if((synonyms.get(synonym)!=null) && CollectionUtils.isEqualCollection(entry.getValue(), synonyms.get(synonym))) {
                configSynonyms.remove(synonym);
            } else { // add to remove list if it's not equal
                synonymsToRemove.add(synonym);
            }
        }

        for (final String synonym : synonymsToRemove) {
            //delete each
            deleteFunction.delete(solrClient, indexName, managedResourcePath + "/" + encodeFunction.encode(synonym));
        }

        if(MapUtils.isEmpty(configSynonyms)) {
            //if configSynonyms is empty, then no need to send
            return;
        }

        final boolean ignoreCase = this.getConfigurationService().getConfiguration().getBoolean(MANAGED_SYNONYMS_IGNORE_CASE_KEY, true);

        final Map<String, Object> requestData = new LinkedHashMap();
        requestData.put(MANAGED_INIT_ARGS_FIELD, Collections.singletonMap(MANAGED_IGNORE_CASE_FIELD, Boolean.toString(ignoreCase)));
        requestData.put(MANAGED_MAP_FIELD, configSynonyms);
        postFunction.post(solrClient, indexName, managedResourcePath, requestData);
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
