package com.sabmiller.core.search.solrfacetsearch.solr.indexer.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexer;
import org.apache.solr.common.SolrInputDocument;

import java.util.Collections;


public class SabmIndexerImpl extends DefaultIndexer {

    @Override
    protected SolrInputDocument createInputDocument(ItemModel model, IndexConfig indexConfig, IndexedType indexedType) throws FieldValueProviderException {
        final IndexerBatchContext context = getIndexerBatchContextFactory().getContext();
        if(IndexOperation.FULL.equals(context.getIndexOperation())) {
            return super.createInputDocument(model, indexConfig, indexedType);
        }
        return createInputDocument(model,indexConfig,indexedType, Collections.emptyList());
    }
}
