package com.apb.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.OutletModel;


/**
 * @see SGA product Outlet value resolver.
 */
public class AsahiProductOutletValueResolver extends AbstractValueResolver<ProductModel, Object, Object>
{
	/* Indexing available outlets for a product. */
	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		if (productModel instanceof ApbProductModel && CollectionUtils.isNotEmpty(((ApbProductModel) productModel).getOutlet()))
		{
			for (final OutletModel outletList : ((ApbProductModel) productModel).getOutlet())
			{
				final String outletName = outletList.getName();
				if (!StringUtils.isBlank(outletName))
				{
					document.addField(indexedProperty, outletName, resolverContext.getFieldQualifier());
				}
			}
		}



	}

}

