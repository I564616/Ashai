package com.sabmiller.integration.provider.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.integration.provider.DataSource;


/**
 * The Class SkuVantageDataSource.
 */
public class SkuVantageDataSource implements DataSource<ProductModel>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SkuVantageDataSource.class);

	/** The query prod no image. */
	protected final String QUERY_PROD_NO_IMAGE = "SELECT {pr." + SABMAlcoholVariantProductEANModel.PK + "} from {"
			+ SABMAlcoholVariantProductEANModel._TYPECODE + "! as pr} WHERE NOT EXISTS ({{ SELECT 1 from {" + MediaModel._TYPECODE
			+ " as me} WHERE {me." + MediaModel.PK + "}={pr." + SABMAlcoholVariantProductEANModel.PICTURE + "} }})";

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The flexible search service. */
	@Resource
	private FlexibleSearchService flexibleSearchService;

    private int batchSize = 500;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<ProductModel> iterator()
	{
		final List<ProductModel> productItems = findProductsNoImages();

		if (CollectionUtils.isNotEmpty(productItems))
		{
			return productItems.iterator();
		}
		else
		{
			LOG.debug("No prodcut find for data loader");
			return null;
		}
	}

	/**
	 * Find products no images.
	 *
	 * @return the list
	 */
	protected List<ProductModel> findProductsNoImages()
	{
		String query = QUERY_PROD_NO_IMAGE;

		final Collection<CatalogVersionModel> catalogs = catalogVersionService.getSessionCatalogVersions();

		final Map<String, Object> params = new HashMap<>();

		if (CollectionUtils.isNotEmpty(catalogs))
		{
			query += " AND {pr." + SABMAlcoholVariantProductEANModel.CATALOGVERSION + "} IN (?catalogs)";
			params.put("catalogs", catalogs);
		}

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, params);
        searchQuery.setCount(batchSize);

		final SearchResult<ProductModel> search = flexibleSearchService.search(searchQuery);

		return search.getResult();
	}

    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }
}
