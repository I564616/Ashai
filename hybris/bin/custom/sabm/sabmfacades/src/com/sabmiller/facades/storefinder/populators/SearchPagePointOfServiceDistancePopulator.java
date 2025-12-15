/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.storefinder.populators;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.springframework.web.util.UriComponentsBuilder;

import com.apb.core.util.AsahiSiteUtil;


public class SearchPagePointOfServiceDistancePopulator<SOURCE extends StoreFinderSearchPageData<PointOfServiceDistanceData>, TARGET extends StoreFinderSearchPageData<PointOfServiceData>>
		implements Populator<SOURCE, TARGET>
{
	private Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceConverter;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	protected Converter<PointOfServiceDistanceData, PointOfServiceData> getPointOfServiceDistanceConverter()
	{
		return pointOfServiceDistanceConverter;
	}

	public void setPointOfServiceDistanceConverter(
			final Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceConverter)
	{
		this.pointOfServiceDistanceConverter = pointOfServiceDistanceConverter;
	}

	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		target.setPagination(source.getPagination());
		target.setSorts(source.getSorts());
		target.setLocationText(source.getLocationText());
		target.setBoundEastLongitude(source.getBoundEastLongitude());
		target.setBoundNorthLatitude(source.getBoundNorthLatitude());
		target.setBoundSouthLatitude(source.getBoundSouthLatitude());
		target.setBoundWestLongitude(source.getBoundWestLongitude());
		target.setSourceLatitude(source.getSourceLatitude());
		target.setSourceLongitude(source.getSourceLongitude());
		target.setResults(Converters.convertAll(source.getResults(), getPointOfServiceDistanceConverter()));
		if(asahiSiteUtil.isCub())
		{
   		if (target.getResults() != null && !target.getResults().isEmpty())
   		{
   			final String urlPrefix = "/store/";
   			final String urlQueryParams = getStoreUrlQueryParams(target);
   
   			for (final PointOfServiceData pointOfService : target.getResults())
   			{
   				pointOfService.setUrl(urlPrefix + pointOfService.getName() + urlQueryParams);
   			}
   		}
		}
	}

	protected String getStoreUrlQueryParams(final TARGET target)
	{
		if (target.getLocationText() != null && !target.getLocationText().isEmpty())
		{
			// Build URL to position query
			return UriComponentsBuilder.fromPath("").queryParam("lat", Double.valueOf(target.getSourceLatitude()))
					.queryParam("long", Double.valueOf(target.getSourceLongitude())).queryParam("q", target.getLocationText()).build()
					.toString();
		}
		// Build URL to position query
		return UriComponentsBuilder.fromPath("").queryParam("lat", Double.valueOf(target.getSourceLatitude()))
				.queryParam("long", Double.valueOf(target.getSourceLongitude())).build().toString();
	}
}
