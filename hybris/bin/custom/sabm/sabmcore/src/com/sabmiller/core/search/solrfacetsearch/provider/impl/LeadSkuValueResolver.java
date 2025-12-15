package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.variants.model.VariantProductModel;

import org.assertj.core.util.IterableUtil;

import java.util.Collection;

public class LeadSkuValueResolver extends AbstractValueResolver<ProductModel,Void,Void> {

    @Override
    protected void addFieldValues(InputDocument inputDocument, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel productModel, ValueResolverContext<Void, Void> valueResolverContext) throws FieldValueProviderException {

        if(!SABMAlcoholVariantProductEANModel.class.isInstance(productModel)){
            return;
        }
        final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEAN = (SABMAlcoholVariantProductEANModel) productModel;

        final String primaryMaterial = getMaterialSku(sabmAlcoholVariantProductEAN);

        if(primaryMaterial == null){
            return;
        }

        addFieldValue(inputDocument,indexerBatchContext,indexedProperty,primaryMaterial,valueResolverContext.getFieldQualifier());

    }

    protected String getMaterialSku(final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEAN){


        final SABMAlcoholVariantProductMaterialModel sabmAlcoholVariantProductMaterialModel = sabmAlcoholVariantProductEAN.getLeadSku();
        if(sabmAlcoholVariantProductMaterialModel != null){
            return sabmAlcoholVariantProductMaterialModel.getCode();
        }

        final Collection<VariantProductModel> variants = sabmAlcoholVariantProductEAN.getVariants();
        if(IterableUtil.isNullOrEmpty(variants)){
            return null;
        }

        return variants.iterator().next().getCode();
    }
}
