package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import org.apache.commons.collections4.CollectionUtils;

public class HasVariantsValueResolver extends AbstractValueResolver<ProductModel,Void,Void> {
    @Override
    protected void addFieldValues(InputDocument inputDocument, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel productModel, ValueResolverContext<Void, Void> valueResolverContext) throws FieldValueProviderException {
        addFieldValue(inputDocument,indexerBatchContext,indexedProperty, CollectionUtils.isNotEmpty(productModel.getVariants()),valueResolverContext.getFieldQualifier());
    }
}
