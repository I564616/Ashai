/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.sabmiller.facades.search.data.CustomerSearchPageData;


/**
 * SABMCustomerSearchPagePopulator
 */
public class SABMCustomerSearchPagePopulator<QUERY, STATE, RESULT, ITEM extends CustomerData, SCAT, CATEGORY>
		implements Populator<CustomerSearchPageData<QUERY, RESULT>, CustomerSearchPageData<STATE, ITEM>>
{

	private Converter<QUERY, STATE> searchStateConverter;

	private Converter<BreadcrumbData<QUERY>, BreadcrumbData<STATE>> breadcrumbConverter;

	private Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter;

	private Converter<RESULT, ITEM> searchResultCustomerConverter;


	/**
	 * @return the searchStateConverter
	 */
	public Converter<QUERY, STATE> getSearchStateConverter()
	{
		return searchStateConverter;
	}


	/**
	 * @param searchStateConverter
	 *           the searchStateConverter to set
	 */
	public void setSearchStateConverter(final Converter<QUERY, STATE> searchStateConverter)
	{
		this.searchStateConverter = searchStateConverter;
	}


	/**
	 * @return the breadcrumbConverter
	 */
	public Converter<BreadcrumbData<QUERY>, BreadcrumbData<STATE>> getBreadcrumbConverter()
	{
		return breadcrumbConverter;
	}


	/**
	 * @param breadcrumbConverter
	 *           the breadcrumbConverter to set
	 */
	public void setBreadcrumbConverter(final Converter<BreadcrumbData<QUERY>, BreadcrumbData<STATE>> breadcrumbConverter)
	{
		this.breadcrumbConverter = breadcrumbConverter;
	}


	/**
	 * @return the facetConverter
	 */
	public Converter<FacetData<QUERY>, FacetData<STATE>> getFacetConverter()
	{
		return facetConverter;
	}


	/**
	 * @param facetConverter
	 *           the facetConverter to set
	 */
	public void setFacetConverter(final Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter)
	{
		this.facetConverter = facetConverter;
	}

	/**
	 * @return the searchResultCustomerConverter
	 */
	public Converter<RESULT, ITEM> getSearchResultCustomerConverter()
	{
		return searchResultCustomerConverter;
	}

	/**
	 * @param searchResultCustomerConverter
	 *           the searchResultCustomerConverter to set
	 */
	public void setSearchResultCustomerConverter(final Converter<RESULT, ITEM> searchResultCustomerConverter)
	{
		this.searchResultCustomerConverter = searchResultCustomerConverter;
	}

	@Override
	public void populate(final CustomerSearchPageData<QUERY, RESULT> source, final CustomerSearchPageData<STATE, ITEM> target)
	{
		target.setFreeTextSearch(source.getFreeTextSearch());

		if (source.getBreadcrumbs() != null)
		{
			target.setBreadcrumbs(Converters.convertAll(source.getBreadcrumbs(), getBreadcrumbConverter()));
		}

		target.setCurrentQuery(getSearchStateConverter().convert(source.getCurrentQuery()));

		if (source.getFacets() != null)
		{
			target.setFacets(Converters.convertAll(source.getFacets(), getFacetConverter()));
		}

		target.setPagination(source.getPagination());

		if (source.getResults() != null)
		{
			target.setResults(Converters.convertAll(source.getResults(), getSearchResultCustomerConverter()));
		}

		target.setSorts(source.getSorts());

	}

}
