/**
 *
 */
package com.sabmiller.facades.search;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.search.solrfacetsearch.SABMCustomerSearchService;
import com.sabmiller.facades.search.data.CustomerSearchPageData;
import com.sabmiller.facades.search.solrfacetsearch.impl.DefaultSABMCustomerSearchFacade;


/**
 * DefaultSABMCustomerSearchFacadeTest
 */
@UnitTest
@SuppressWarnings("unchecked")
public class DefaultSABMCustomerSearchFacadeTest
{
	private DefaultSABMCustomerSearchFacade<CustomerData> sabmCustomerSearchFacade;

	@Mock
	private SABMCustomerSearchService<SolrSearchQueryData, SearchResultValueData, CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>> customerSearchService;
	@Mock
	private ThreadContextService threadContextService;
	@Mock
	private Converter<CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>, CustomerSearchPageData<SearchStateData, CustomerData>> customerSearchPageConverter;
	@Mock
	private Converter<SearchQueryData, SolrSearchQueryData> solrSearchQueryDecoder;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmCustomerSearchFacade = new DefaultSABMCustomerSearchFacade<CustomerData>();
		sabmCustomerSearchFacade.setCustomerSearchPageConverter(customerSearchPageConverter);
		sabmCustomerSearchFacade.setCustomerSearchService(customerSearchService);
		sabmCustomerSearchFacade.setSolrSearchQueryDecoder(solrSearchQueryDecoder);
		sabmCustomerSearchFacade.setThreadContextService(threadContextService);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testTextSearch()
	{
		final SearchStateData searchState = mock(SearchStateData.class);
		final SearchQueryData query = mock(SearchQueryData.class);
		given(query.getValue()).willReturn("test");
		given(searchState.getQuery()).willReturn(query);
		final PageableData pageableData = mock(PageableData.class);
		given(pageableData.getCurrentPage()).willReturn(0);
		final SolrSearchQueryData searchQueryData = mock(SolrSearchQueryData.class);

		Mockito.when(solrSearchQueryDecoder.convert(searchState.getQuery())).thenReturn(searchQueryData);

		final CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData> customerRestultData = mock(
				CustomerSearchPageData.class);


		Mockito.when(customerSearchService.textSearch(searchQueryData, pageableData)).thenReturn(customerRestultData);
		final CustomerSearchPageData<SearchStateData, CustomerData> coverterData = mock(CustomerSearchPageData.class);
		final CustomerData searchResult = new CustomerData();
		searchResult.setUid("test@hybris.com");
		final List<CustomerData> customers = new ArrayList<CustomerData>();
		customers.add(searchResult);
		given(coverterData.getResults()).willReturn(customers);

		Mockito.when(customerSearchPageConverter.convert(customerRestultData)).thenReturn(coverterData);

		Mockito.when(sabmCustomerSearchFacade.textSearch(searchState, pageableData)).thenReturn(coverterData);

		Assert.assertEquals("test@hybris.com", coverterData.getResults().get(0).getUid());
	}








}
