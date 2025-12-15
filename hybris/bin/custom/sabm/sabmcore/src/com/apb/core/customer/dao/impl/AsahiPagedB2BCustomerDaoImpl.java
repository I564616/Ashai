package com.apb.core.customer.dao.impl;

import de.hybris.platform.b2b.dao.impl.DefaultPagedB2BCustomerDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.annotation.Resource;

import com.apb.core.customer.dao.AsahiSortParameters;
import com.apb.core.util.AsahiSiteUtil;


public class AsahiPagedB2BCustomerDaoImpl extends DefaultPagedB2BCustomerDao
{

	private static final String DEFAULT_SORT_CODE = Config.getString("b2bcommerce.defaultSortCode", "byActiveDscName");
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	public AsahiPagedB2BCustomerDaoImpl(String typeCode)
	{
		super(typeCode);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public SearchPageData<B2BCustomerModel> find(PageableData pageableData)
	{
		if(!asahiSiteUtil.isCub())
		{
		AsahiSortParameters spStatus = new AsahiSortParameters();
		spStatus.addSortParameter("active", SortParameters.SortOrder.DESCENDING);
		spStatus.addSortParameter("name", SortParameters.SortOrder.ASCENDING);

		List sortQueries = Arrays.asList(new SortQueryData[]
		{ this.createSortQueryDataForStatus("byActiveDscName", new HashMap(), spStatus),
				this.createSortQueryData("byNameAsc", new HashMap(), SortParameters.singletonAscending("name")),
				this.createSortQueryData("byNameDsc", new HashMap(), SortParameters.singletonDescending("name")) });
		return this.getPagedFlexibleSearchService().search(sortQueries, DEFAULT_SORT_CODE, new HashMap(), pageableData);
		}
		else
		{
			return super.find(pageableData);
			
		}
	}

	protected SortQueryData createSortQueryDataForStatus(final String sortCode, final Map<String, ?> params,
			final AsahiSortParameters sortParameters)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(this.createFlexibleSearchQueryForStatus(params, sortParameters).getQuery());
		return result;
	}

	protected FlexibleSearchQuery createFlexibleSearchQueryForStatus(final Map<String, ?> params,
			final AsahiSortParameters sortParameters)
	{
		final StringBuilder builder = this.createQueryString();
		appendWhereClausesToBuilder(builder, params);
		appendOrderByClausesToBuilderForStatus(builder, sortParameters);
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(builder.toString());
		if (params != null && !params.isEmpty())
		{
			searchQuery.addQueryParameters(params);
		}
		return searchQuery;
	}
	

      protected StringBuilder createQueryString() {
         StringBuilder builder = new StringBuilder(25);
         builder.append("SELECT {c:").append("pk").append("} ");
         builder.append("FROM {").append("B2BCustomer!").append(" AS c} ");
         return builder;
      }


	protected void appendOrderByClausesToBuilderForStatus(final StringBuilder builder, final AsahiSortParameters sortParameters)
	{
		if (sortParameters != null && !sortParameters.isEmpty())
		{
			builder.append("ORDER BY ");
			boolean firstParam = true;
			final Map<String, SortParameters.SortOrder> sortParams = sortParameters.getSortParameters();
			for (final Entry<String, SortParameters.SortOrder> entry : sortParams.entrySet())
			{
				if (!firstParam)
				{
					builder.append(", ");
				}
				builder.append("{c:").append(entry.getKey()).append("} ").append(entry.getValue());
				firstParam = false;
			}
		}
	}




}
