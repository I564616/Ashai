package com.apb.core.util;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import bsh.This;


/**
 * Class for creating pagination data.
 */
public class AsahiPaginationUtil
{

	/**
	 * Method creates the pagination data
	 *
	 * @param pageableData
	 * @param searchResult
	 * @return
	 */
	private <T> PaginationData createPagination(final PageableData pageableData, final List<T> searchResult)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setSort(pageableData.getSort());
		paginationData.setTotalNumberOfResults(searchResult.size());

		// Calculate the number of pages
		paginationData.setNumberOfPages(
				(int) Math.ceil(((double) paginationData.getTotalNumberOfResults()) / paginationData.getPageSize()));

		// Work out the current page, fixing any invalid page values
		paginationData.setCurrentPage(Math.max(0, Math.min(paginationData.getNumberOfPages(), pageableData.getCurrentPage())));

		return paginationData;
	}

	/**
	 * @see This method will convert the page data for multi account results.
	 * @param source
	 * @param converter
	 * @param searchResult
	 * @param pageableData
	 * @return
	 */
	public <S, T> SearchPageData<T> convertPageData(final List<T> searchResult, final PageableData pageableData)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(createPagination(pageableData, searchResult));
		result.setResults(searchResult);
		return result;
	}

}
