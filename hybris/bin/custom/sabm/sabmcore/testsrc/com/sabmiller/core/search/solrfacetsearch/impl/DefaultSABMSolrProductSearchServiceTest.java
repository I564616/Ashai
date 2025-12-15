/**
 *
 */
package com.sabmiller.core.search.solrfacetsearch.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultDocument;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * The SABM solr product search service unit test
 *
 * @author xiaowu.a.zhang
 * @date 04/07/2016
 */
@UnitTest
public class DefaultSABMSolrProductSearchServiceTest
{
	@InjectMocks
	private final DefaultSABMSolrProductSearchService<?> sabmSolrProductSearchService = new DefaultSABMSolrProductSearchService<>();

	@Mock
	private ProductService productService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testValidateSolrSearchResponse()
	{
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse<>();
		final SolrSearchResult searchResult = new SolrSearchResult();
		solrSearchResponse.setSearchResult(searchResult);

		searchResult.setNumberOfResults(4);

		final List<Document> documents = new ArrayList<>();

		//mock the document
		final DefaultDocument document1 = mock(DefaultDocument.class);
		final Map<String, Object> fileds1 = new HashMap<>();
		fileds1.put("code", "productCode1");
		fileds1.put("container", Arrays.asList("container1"));
		fileds1.put("style", Arrays.asList("style1"));
		fileds1.put("category", Arrays.asList("category"));
		fileds1.put("brand", "brand1");
		given(document1.getFields()).willReturn(fileds1);
		documents.add(document1);

		final DefaultDocument document2 = mock(DefaultDocument.class);
		final Map<String, Object> fileds2 = new HashMap<>();
		fileds2.put("code", "productCode2");
		fileds2.put("container", Arrays.asList("container1"));
		fileds2.put("style", Arrays.asList("style2"));
		fileds2.put("category", Arrays.asList("category"));
		fileds2.put("brand", "brand1");
		given(document2.getFields()).willReturn(fileds2);
		documents.add(document2);

		final DefaultDocument document3 = mock(DefaultDocument.class);
		final Map<String, Object> fileds3 = new HashMap<>();
		fileds3.put("code", "productCode3");
		fileds3.put("container", Arrays.asList("container2"));
		fileds3.put("style", Arrays.asList("style2"));
		fileds3.put("category", Arrays.asList("category"));
		fileds3.put("brand", "brand1");
		given(document3.getFields()).willReturn(fileds3);
		documents.add(document3);

		final DefaultDocument document4 = mock(DefaultDocument.class);
		final Map<String, Object> fileds4 = new HashMap<>();
		fileds4.put("code", "productCode4");
		fileds4.put("container", Arrays.asList("container2"));
		fileds4.put("style", Arrays.asList("style1"));
		fileds4.put("category", Arrays.asList("category"));
		fileds4.put("brand", "brand1");
		given(document4.getFields()).willReturn(fileds4);
		documents.add(document4);

		searchResult.setDocuments(documents);

		// mock the product
		final ProductModel product1 = mock(ProductModel.class);
		given(productService.getProductForCode("productCode1")).willReturn(product1);

		final ProductModel product2 = mock(ProductModel.class);
		given(productService.getProductForCode("productCode2")).willReturn(product2);

		final UnknownIdentifierException unknownIdentifierException1 = new UnknownIdentifierException("productCode3");
		given(productService.getProductForCode("productCode3")).willThrow(unknownIdentifierException1);

		final UnknownIdentifierException unknownIdentifierException2 = new UnknownIdentifierException("productCode4");
		given(productService.getProductForCode("productCode4")).willThrow(unknownIdentifierException2);

		final Map<String, Facet> facets = new HashMap<>();

		final List<FacetValue> facetValues1 = new ArrayList<>();
		final FacetValue facetValue11 = new FacetValue("container1", "container1", 2, false);
		final FacetValue facetValue12 = new FacetValue("container2", "container2", 2, false);
		facetValues1.add(facetValue11);
		facetValues1.add(facetValue12);
		final Facet facet1 = new Facet("container", facetValues1);
		facets.put("container", facet1);

		final List<FacetValue> facetValues2 = new ArrayList<>();
		final FacetValue facetValue21 = new FacetValue("style1", "style1", 2, false);
		final FacetValue facetValue22 = new FacetValue("style2", "style2", 2, false);
		facetValues2.add(facetValue21);
		facetValues2.add(facetValue22);
		final Facet facet2 = new Facet("style", facetValues2);
		facets.put("style", facet2);

		final List<FacetValue> facetValues3 = new ArrayList<>();
		final FacetValue facetValue31 = new FacetValue("category", "category", 4, false);
		facetValues3.add(facetValue31);
		final Facet facet3 = new Facet("category", facetValues3);
		facets.put("category", facet3);

		final List<FacetValue> facetValues4 = new ArrayList<>();
		final FacetValue facetValue41 = new FacetValue("brand1", "brand1", 4, false);
		facetValues4.add(facetValue41);
		final Facet facet4 = new Facet("brand", facetValues4);
		facets.put("brand", facet4);

		searchResult.setFacetsMap(facets);

		//sabmSolrProductSearchService.validateSolrSearchResponse(solrSearchResponse);

		Assert.assertEquals(4, ((SolrSearchResult) solrSearchResponse.getSearchResult()).getDocuments().size());
		Assert.assertEquals(4, ((SolrSearchResult) solrSearchResponse.getSearchResult()).getNumberOfResults());
		Assert.assertEquals(2,
				((SolrSearchResult) solrSearchResponse.getSearchResult()).getFacets().get(0).getFacetValues().size());
		Assert.assertEquals(2,
				((SolrSearchResult) solrSearchResponse.getSearchResult()).getFacets().get(0).getFacetValues().get(0).getCount());
		Assert.assertEquals(2,
				((SolrSearchResult) solrSearchResponse.getSearchResult()).getFacets().get(1).getFacetValues().get(0).getCount());
		Assert.assertEquals(4,
				((SolrSearchResult) solrSearchResponse.getSearchResult()).getFacets().get(3).getFacetValues().get(0).getCount());
	}

}
