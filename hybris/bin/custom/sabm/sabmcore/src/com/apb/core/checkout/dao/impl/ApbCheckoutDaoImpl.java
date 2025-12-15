package com.apb.core.checkout.dao.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.checkout.dao.ApbCheckoutDao;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.constants.ApbQueryConstant;
import com.apb.integration.data.AsahiProductInfo;
import com.sabmiller.core.model.HolidayModel;


public class ApbCheckoutDaoImpl implements ApbCheckoutDao
{
	private final static Logger LOG = LoggerFactory.getLogger("ApbCheckoutDaoImpl");

	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public List<HolidayModel> getHolidayModelForRegionDate(final String regionCode, final DateTime dateTime)
	{
		List<HolidayModel> holidayModels = Collections.emptyList();
		try
		{

			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {hm:" + HolidayModel.PK + "}");
			queryString.append(" FROM {" + HolidayModel._TYPECODE + " AS hm ");
			queryString.append(" JOIN " + RegionModel._TYPECODE + " AS rm ");
			queryString.append(" ON {hm:" + HolidayModel.REGION + "} = {rm:" + RegionModel.PK + "}}");
			queryString.append(" Where CAST({hm:" + HolidayModel.DATE + "} as Date) =?date ");
			queryString.append(" AND {rm:" + RegionModel.ISOCODE + "} = ?regionCode");

			final FlexibleSearchQuery fsq = new FlexibleSearchQuery(queryString.toString());
			fsq.addQueryParameter("date", getYYYYMMDD(dateTime));
			fsq.addQueryParameter("regionCode", regionCode);

			final SearchResult<HolidayModel> searchResult = flexibleSearchService.search(fsq);

			if (searchResult != null && !searchResult.getResult().isEmpty())

			{
				holidayModels = searchResult.getResult();
			}
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("HolidayModel not found!", e);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occur!", e);
		}
		return holidayModels;
	}

	public static String getYYYYMMDD(final DateTime dateTime)
	{
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = null;
		date = format.format(dateTime.toDate());
		LOG.debug("Date : " + date);
		return date;
	}

	@Override
	public B2BUnitModel getB2BUnitForUid(final String b2bUnit)
	{
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder("SELECT {PK} FROM {B2BUnit} WHERE {uid} = ?uid");
		params.put("uid", b2bUnit);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		LOG.debug("B2B Unit for UID query : " + query.toString());
		final SearchResult<B2BUnitModel> result = this.flexibleSearchService.search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	@Override
	public List<AsahiProductInfo> getProductDetailsFromCart(final boolean updateCart, final long qty, final String code) {
		final StringBuilder query = new StringBuilder(ApbQueryConstant.GET_CART_PRODUCT_CODES);
		final FlexibleSearchQuery flexiQuery = new FlexibleSearchQuery(query.toString());
		flexiQuery.setResultClassList(Arrays.asList(String.class, String.class, String.class, Boolean.class, Boolean.class));
		flexiQuery.addQueryParameter("code", code);
		final SearchResult<List<Object>> searchResult = flexibleSearchService.search(flexiQuery);
		final List<AsahiProductInfo> products = new ArrayList<>();
		if (searchResult != null && !searchResult.getResult().isEmpty())
		{
			searchResult.getResult().forEach(row -> {
				final AsahiProductInfo product = new AsahiProductInfo();
				product.setMaterialNumber((String) row.get(0));

				product.setQuantity((String) row.get(1));

				if (BooleanUtils.isTrue((Boolean) row.get(3)) || BooleanUtils.isTrue((Boolean) row.get(4)))
				{
					product.setItemcat(ApbCoreConstants.FREE_ITEM_CATEGORY);
				}
				else
				{
					product.setItemcat(ApbCoreConstants.ITEM_CATEGORY);
				}

				String rowNum = (String) row.get(2);
				if (StringUtils.isNotEmpty(rowNum) && StringUtils.isNumeric((String) row.get(2)))
				{
					rowNum = (Integer.valueOf((String) row.get(2) + 1)).toString();
				}
				product.setLineNum(rowNum);
				products.add(product);
			});
		}

		return products;
	}
}
