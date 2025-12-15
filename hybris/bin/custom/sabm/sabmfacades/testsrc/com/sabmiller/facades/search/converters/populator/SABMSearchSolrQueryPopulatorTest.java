/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class SABMSearchSolrQueryPopulatorTest
{

	private SABMSearchSolrQueryPopulator sabmSearchSolrQueryPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmSearchSolrQueryPopulator = new SABMSearchSolrQueryPopulator();
	}

	@Test
	public void testGetIndexedType()
	{
		final FacetSearchConfig config1 = mock(FacetSearchConfig.class);
		final FacetSearchConfig config2 = mock(FacetSearchConfig.class);

		final IndexConfig indexConfig1 = mock(IndexConfig.class);
		given(config1.getIndexConfig()).willReturn(indexConfig1);

		final IndexConfig indexConfig2 = mock(IndexConfig.class);
		given(config2.getIndexConfig()).willReturn(indexConfig2);

		final IndexedType indexedType1 = mock(IndexedType.class);
		given(indexedType1.getCode()).willReturn("B2BCustomer");
		final ComposedTypeModel composedType1 = new ComposedTypeModel();
		composedType1.setCatalogItemType(Boolean.TRUE);
		given(indexedType1.getComposedType()).willReturn(composedType1);

		final IndexedType indexedType2 = mock(IndexedType.class);
		given(indexedType2.getCode()).willReturn("Customer");
		final ComposedTypeModel composedType2 = new ComposedTypeModel();
		composedType2.setCatalogItemType(Boolean.TRUE);
		given(indexedType2.getComposedType()).willReturn(composedType2);

		final Map<String, IndexedType> indexTypes1 = new HashMap<>();
		final Map<String, IndexedType> indexTypes2 = new HashMap<>();
		indexTypes1.put("key1", indexedType1);
		indexTypes2.put("key2", indexedType2);

		given(indexConfig1.getIndexedTypes()).willReturn(indexTypes1);
		given(indexConfig2.getIndexedTypes()).willReturn(indexTypes2);

		final IndexedType indexedType_r1 = sabmSearchSolrQueryPopulator.getIndexedType(config1);
		final IndexedType indexedType_r2 = sabmSearchSolrQueryPopulator.getIndexedType(config2);

		//Assert.assertEquals(Boolean.FALSE, indexedType_r1.getComposedType().getCatalogItemType());
		Assert.assertEquals(null, indexedType_r1);
		Assert.assertEquals(null, indexedType_r2);
	}

}
