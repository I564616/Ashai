/**
 *
 */
package com.sabmiller.core.search.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.converter.Converter;

import com.sabmiller.core.search.solrfacetsearch.impl.DefaultSABMCustomerSearchService;
import com.sabmiller.facades.search.data.CustomerSearchPageData;


/**
 * JUnit for {@link DefaultSABMCustomerSearchService}
 */
@UnitTest
@SuppressWarnings("rawtypes")
public class DefaultSABMCustomerSearchServiceTest
{

	@Mock
	private Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter;
	@Mock
	private Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter;
	@Mock
	private Converter<SolrSearchResponse, CustomerSearchPageData<SolrSearchQueryData, CustomerData>> sabmCustomerSearchResponseConverter;
	@Mock
	private DefaultSABMCustomerSearchService<CustomerData> sabmCustomerSearchServic;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmCustomerSearchServic = new DefaultSABMCustomerSearchService<CustomerData>();
		sabmCustomerSearchServic.setSabmCustomerSearchResponseConverter(sabmCustomerSearchResponseConverter);
		sabmCustomerSearchServic.setSearchQueryPageableConverter(searchQueryPageableConverter);
		sabmCustomerSearchServic.setSearchRequestConverter(searchRequestConverter);

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDoSearch()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSort("name");
		final List<SolrSearchQueryTermData> filterTerms = new ArrayList<SolrSearchQueryTermData>();
		final SolrSearchQueryTermData solr = new SolrSearchQueryTermData();
		solr.setKey("name");
		solr.setValue("test");
		filterTerms.add(0, solr);
		searchQueryData.setFilterTerms(filterTerms);
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(0);
		final SolrSearchRequest solrSearchRequest = new SolrSearchRequest();
		solrSearchRequest.setSearchQueryData(searchQueryData);
		final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData = mock(SearchQueryPageableData.class);
		searchQueryPageableData.setSearchQueryData(searchQueryData);
		searchQueryPageableData.setPageableData(pageableData);
		given(searchQueryPageableData.getSearchQueryData()).willReturn(searchQueryData);
		given(searchQueryPageableData.getPageableData()).willReturn(pageableData);

		Mockito.when(searchQueryPageableConverter.convert(searchQueryPageableData)).thenReturn(solrSearchRequest);

		final SolrSearchResponse<?, ?, ?, ?, ?, CustomerData> solrSearchResponse = mock(SolrSearchResponse.class);
		final CustomerData searchResult = new CustomerData();
		searchResult.setUid("test@hybris.com");
		given(solrSearchResponse.getSearchResult()).willReturn(searchResult);
		Mockito.when(searchRequestConverter.convert(solrSearchRequest)).thenReturn(solrSearchResponse);

		final CustomerSearchPageData<SolrSearchQueryData, CustomerData> result = mock(CustomerSearchPageData.class);
		final List<CustomerData> customers = new ArrayList();
		customers.add(searchResult);
		given(result.getResults()).willReturn(customers);

		Mockito.when(sabmCustomerSearchResponseConverter.convert(solrSearchResponse)).thenReturn(result);

		final CustomerSearchPageData<SolrSearchQueryData, CustomerData> resultData = mock(CustomerSearchPageData.class);
		given(resultData.getResults()).willReturn(customers);

		Mockito.when(sabmCustomerSearchServic.textSearch(searchQueryData, pageableData)).thenReturn(resultData);
		Assert.assertEquals("test@hybris.com", resultData.getResults().get(0).getUid());

	}

}
