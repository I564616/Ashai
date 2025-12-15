/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.impex.jalo.ErrorHandler.RESULT;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.jgroups.protocols.pbcast.STATE;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.facades.search.converters.populator.SABMCustomerSearchPagePopulator;
import com.sabmiller.facades.search.data.CustomerSearchPageData;


/**
 *
 */
@UnitTest
@SuppressWarnings("rawtypes")
public class SABMCustomerSearchPagePopulatorTest
{

	SABMCustomerSearchPagePopulator sabmCustomerSearchPagePopulator;

	@Mock
	private Converter<SolrSearchQueryData, STATE> searchStateConverter;
	@Mock
	private Converter<BreadcrumbData<SolrSearchQueryData>, BreadcrumbData<STATE>> breadcrumbConverter;
	@Mock
	private Converter<FacetData<SolrSearchQueryData>, FacetData<STATE>> facetConverter;
	@Mock
	private Converter<RESULT, CustomerData> searchResultCustomerConverter;


	@SuppressWarnings("unchecked")
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmCustomerSearchPagePopulator = new SABMCustomerSearchPagePopulator();
		sabmCustomerSearchPagePopulator.setBreadcrumbConverter(breadcrumbConverter);
		sabmCustomerSearchPagePopulator.setFacetConverter(facetConverter);
		sabmCustomerSearchPagePopulator.setSearchResultCustomerConverter(searchResultCustomerConverter);
		sabmCustomerSearchPagePopulator.setSearchStateConverter(searchStateConverter);
	}



	@SuppressWarnings("unchecked")
	@Test
	public void testPopulator()
	{

		final CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData> customerSearchSouce = new CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>();


		final CustomerSearchPageData<SearchStateData, CustomerData> customerSearchTarget = new CustomerSearchPageData<SearchStateData, CustomerData>();
		final SolrSearchQueryData searchQueryData = mock(SolrSearchQueryData.class);
		given(searchQueryData.getSort()).willReturn("name");
		final List<SolrSearchQueryTermData> filterTerms = new ArrayList<SolrSearchQueryTermData>();
		final SolrSearchQueryTermData solr = new SolrSearchQueryTermData();
		solr.setKey("name");
		solr.setValue("test");
		filterTerms.add(0, solr);
		given(searchQueryData.getFilterTerms()).willReturn(filterTerms);
		customerSearchSouce.setCurrentQuery(searchQueryData);

		customerSearchSouce.setFreeTextSearch("testTextSearch");


		sabmCustomerSearchPagePopulator.populate(customerSearchSouce, customerSearchTarget);

		Assert.assertEquals("testTextSearch", customerSearchTarget.getFreeTextSearch());


	}











}
