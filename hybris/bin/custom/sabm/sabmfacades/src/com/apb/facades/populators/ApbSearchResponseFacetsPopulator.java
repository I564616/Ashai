package com.apb.facades.populators;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFacetsPopulator;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.store.services.BaseStoreService;

import com.apb.core.util.AsahiSiteUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * @param <FACET_SEARCH_CONFIG_TYPE>
 * @param <INDEXED_TYPE_TYPE>
 * @param <INDEXED_PROPERTY_TYPE>
 * @param <INDEXED_TYPE_SORT_TYPE>
 * @param <ITEM>
 * @see Apb specific response facet populator
 */
public class ApbSearchResponseFacetsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		extends
		SearchResponseFacetsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
{
	/**
	 * Category facet
	 */
	public static final String CATEGORYFACET = "category";
	/**
	 * hide brand
	 */
	public static final String BRANDFACET = "hideBrand";

	/**
	 * split delimiter
	 */
	public static final String SPLIT_REGEX = "_";

	/**
	 * configuration for hiding brand in category
	 */
	public static final String HIDE_BRAND_IN_CATEGORY_CONFIGURATION = "hide.brand.in.category";

	/**
	 * Logger
	 */
	public static final Logger LOG = LoggerFactory.getLogger(ApbSearchResponseFacetsPopulator.class);

	private ConfigurationService configurationService;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Override
	protected List<FacetData<SolrSearchQueryData>> buildFacets(final SearchResult solrSearchResult,
			final SolrSearchQueryData searchQueryData, final IndexedType indexedType)
	{
		if(!asahiSiteUtil.isCub())
		{
		final List<Facet> solrSearchResultFacets = solrSearchResult.getFacets();
		Facet brandFacetData = null;


		/* get the hideBrand from the solr response start */
		for (final Facet brandFacet : solrSearchResultFacets)
		{
			if (brandFacet != null && brandFacet.getName().equalsIgnoreCase(BRANDFACET))
			{
				brandFacetData = brandFacet;
				break;

			}
		}
		/* get the hideBrand from the solr response end */

		final List<FacetData<SolrSearchQueryData>> result = new ArrayList<>(solrSearchResultFacets.size());

		for (final Facet facet : solrSearchResultFacets)
		{
			final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facet.getName());

			// Ignore any facets with a priority less than or equal to 0 as they are for internal use only
			final FacetData<SolrSearchQueryData> facetData = createFacetData();
			facetData.setCode(facet.getName());
			facetData.setCategory(indexedProperty.isCategoryField());
			final String displayName = indexedProperty.getDisplayName();
			facetData.setName(displayName == null ? facet.getName() : displayName);
			facetData.setMultiSelect(facet.isMultiselect());
			facetData.setPriority(facet.getPriority());
			facetData.setVisible(indexedProperty.isVisible());

			buildFacetValues(facetData, facet, indexedProperty, solrSearchResult, searchQueryData, brandFacetData);
			// Only add the facet if there are values
			if (facetData.getValues() != null && !facetData.getValues().isEmpty())
			{
				result.add(facetData);
			}
		}
		return result;
		}
		else
		{
			return super.buildFacets(solrSearchResult, searchQueryData, indexedType);
		}
	}


	protected void buildFacetValues(final FacetData<SolrSearchQueryData> facetData, final Facet facet,
			final IndexedProperty indexedProperty, final SearchResult solrSearchResult, final SolrSearchQueryData searchQueryData,
			final Facet brandFacet)
	{

		final List<FacetValue> facetValues = facet.getFacetValues();
		boolean blockBrandFacet = false;
		final boolean checkConfig = configurationService.getConfiguration().getBoolean(HIDE_BRAND_IN_CATEGORY_CONFIGURATION, false);

		final HashMap<String, String> brandMap = generateBrandMap(brandFacet);

		if (facetValues != null && !facetValues.isEmpty())
		{
			final List<FacetValueData<SolrSearchQueryData>> allFacetValues = new ArrayList<>(facetValues.size());
			final List<FacetValueData<SolrSearchQueryData>> topFacetValuesData = new ArrayList<>();
			// Sort the facet values by Count in descending order
			Collections.sort(facetValues, FacetValueCountComparator.INSTANCE);

			for (final FacetValue facetValue : facetValues)
			{
				final FacetValueData<SolrSearchQueryData> facetValueData = buildFacetValue(facetData, facet, facetValue,
						solrSearchResult, searchQueryData);

				/* Restrict Brand Value from the category hierarchy start */
				if (checkConfig && facet.getName().equalsIgnoreCase(CATEGORYFACET))
				{
					blockBrandFacet = isCategoryContainsBrand(facetValue, brandMap);
				}
				setFacetValue(facetData, blockBrandFacet, allFacetValues, topFacetValuesData, facetValueData);
			}

			facetData.setValues(allFacetValues);
			if (topFacetValuesData.size() >= allFacetValues.size())
			{
				facetData.setTopValues(Collections.emptyList());
			}
		}
	}

	/**
	 *
	 * Restrict Brand Value from the category hierarchy end
	 *
	 * @param facetData
	 * @param blockBrandFacet
	 * @param allFacetValues
	 * @param topFacetValuesData
	 * @param facetValueData
	 */
	private void setFacetValue(final FacetData<SolrSearchQueryData> facetData, final boolean blockBrandFacet,
			final List<FacetValueData<SolrSearchQueryData>> allFacetValues,
			final List<FacetValueData<SolrSearchQueryData>> topFacetValuesData,
			final FacetValueData<SolrSearchQueryData> facetValueData)
	{
		/* Restrict Brand Value from the category hierarchy end */
		/* Do not add facet if its already being selected */
		if (facetValueData != null && !blockBrandFacet)
		{
			if (!facetValueData.isSelected())
			{
				allFacetValues.add(facetValueData);
			}
			if (topFacetValuesData.size() < 5 || facetValueData.isSelected())
			{
				topFacetValuesData.add(facetValueData);
			}
			facetData.setTopValues(topFacetValuesData);
		}
	}


	/* Generate the brand Map containing indexed value end */
	private HashMap<String, String> generateBrandMap(final Facet brandFacet)
	{
		final HashMap<String, String> brandMap = new HashMap<>();

		if (brandFacet != null)
		{
			final List<FacetValue> facetList = brandFacet.getFacetValues();
			for (final FacetValue facet : facetList)
			{
				final String[] tokens = facet.getName().split(SPLIT_REGEX);

				if (ArrayUtils.isNotEmpty(tokens))
				{
					brandMap.put(tokens[0], tokens[1]);
				}
			}
		}
		return brandMap;
	}
	/* Check whether the catergory is level 4 brand or not */

	private boolean isCategoryContainsBrand(final FacetValue facetValue, final HashMap<String, String> brandMap)
	{
		if (MapUtils.isNotEmpty(brandMap) && Boolean.valueOf(brandMap.get(facetValue.getName())))
		{
			return true;
		}
		return false;
	}

	/**
	 * Category sort based on product count(In Descending order)
	 */
	public static class FacetValueCountComparator extends AbstractComparator<FacetValue>
	{
		public static final FacetValueCountComparator INSTANCE = new FacetValueCountComparator();

		@Override
		protected int compareInstances(final FacetValue facet1, final FacetValue facet2)
		{
			final long facet1Count = facet1.getCount();
			final long facet2Count = facet2.getCount();
			return facet1Count < facet2Count ? 1 : (facet1Count > facet2Count ? -1 : 0);
		}
	}

}
