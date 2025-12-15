/**
 *
 */
package com.sabmiller.core.cdlvalue.dao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabm.core.model.CDLValueModel;


/**
 * @author EG588BU
 *
 */
public class DefaultSabmCDLValueDao implements SabmCDLValueDao
{
	private final static Logger LOG = LoggerFactory.getLogger("DefaultSabmCDLValueDao");

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;
	
	private static final String CDLVALUE_FOR_LOCATION_AND_CONTAINER = "SELECT {CDL:" + CDLValueModel.PK + "} FROM {"
			+ CDLValueModel._TYPECODE + " AS CDL} WHERE {CDL:" + CDLValueModel.LOCATION + "}=?location AND {CDL:"
			+ CDLValueModel.CONTAINERTYPE + "}=?containerType";


	@Override
	public Optional<CDLValueModel> getCDLValueModel(final String location, final String containerType)
	{

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("location", location);
		params.put("containerType", containerType);
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(CDLVALUE_FOR_LOCATION_AND_CONTAINER, params);
		try
		{
			return Optional.of(flexibleSearchService.searchUnique(fQuery));
		}
		catch (final Exception e)
		{
			return Optional.empty();
		}
	}
}
