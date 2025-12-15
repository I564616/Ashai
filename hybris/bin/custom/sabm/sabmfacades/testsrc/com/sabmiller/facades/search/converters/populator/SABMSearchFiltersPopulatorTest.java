/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class SABMSearchFiltersPopulatorTest
{
	private SABMSearchFiltersPopulator<Object, Object> sabmSearchFiltersPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmSearchFiltersPopulator = new SABMSearchFiltersPopulator<>();
	}

	@Test
	public void testPopulate()
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = mock(SearchQueryPageableData.class);
		final SolrSearchRequest<Object, IndexedType, IndexedProperty, SearchQuery, Object> target = new SolrSearchRequest<Object, IndexedType, IndexedProperty, SearchQuery, Object>();

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();


		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey("key1");
		searchQueryTermData1.setValue("value1");

		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey("key2");
		searchQueryTermData2.setValue("value2");

		final List<SolrSearchQueryTermData> terms = new ArrayList<SolrSearchQueryTermData>();
		terms.add(searchQueryTermData1);
		terms.add(searchQueryTermData2);

		solrSearchQueryData.setFilterTerms(terms);
		target.setSearchQueryData(solrSearchQueryData);

		final IndexedProperty indexedProperty1 = new IndexedProperty();
		final IndexedProperty indexedProperty2 = new IndexedProperty();

		final IndexedType indexedType = new IndexedType();
		target.setIndexedType(indexedType);

		final Map<String, IndexedProperty> indexedProperties = new HashMap<>();
		indexedProperties.put("key1", indexedProperty1);
		indexedProperties.put("key2", indexedProperty2);
		indexedType.setIndexedProperties(indexedProperties);
		indexedType.setCode("B2BCustomer");

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType);
		target.setSearchQuery(searchQuery);

		sabmSearchFiltersPopulator.populate(source, target);

		Assert.assertEquals(2, target.getIndexedPropertyValues().size());
		Assert.assertEquals("value2", target.getIndexedPropertyValues().get(1).getValue());
	}
}
