/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;


/**
 * @author Siddarth
 *
 */
public class CUBStockInformationDaoImpl implements CUBStockInformationDao
{

	private final String GETALLROWS = "select {" + CUBStockInformationModel.PK + "} from {" + CUBStockInformationModel._TYPECODE
			+ "}";

	private final String GETSTOCKFORPRODUCTCODE = "select {" + CUBStockInformationModel.PK + "} from {"
			+ CUBStockInformationModel._TYPECODE + "} where {" + CUBStockInformationModel.PRODUCTCODE + "} =?productCode";

	private final String DELIVERYPLANTCRITERIA = " AND {" + CUBStockInformationModel.PLANT + "}=?plant";
	private final String FALLBACKDELIVERYPLANTCRITERIA = " AND ({" + CUBStockInformationModel.PLANT + "}=?plant OR {"
			+ CUBStockInformationModel.PLANT + "}=?fallbackPlant)";

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.CUBStockInformationDao#wipeAllRecords()
	 */
	@Override
	public List<CUBStockInformationModel> getAllCUBStockLines()
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(GETALLROWS);
		final SearchResult<CUBStockInformationModel> result = flexibleSearchService.search(fsq);
		return result.getResult();
	}

	@Override
	public CUBStockInformationModel getCUBStockForProductAndPlant(final String productCode, final PlantModel plant)
	{
		final StringBuilder stockQuery = new StringBuilder(GETSTOCKFORPRODUCTCODE);

		final Map<String, Object> params = new HashMap<>();
		params.put("productCode", productCode);
		params.put("plant", plant);
		if (plant.getFallbackPlant() != null)
		{
			params.put("fallbackPlant", plant.getFallbackPlant());
			stockQuery.append(FALLBACKDELIVERYPLANTCRITERIA);
		}
		else
		{
			stockQuery.append(DELIVERYPLANTCRITERIA);
		}
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(stockQuery);
		fsq.addQueryParameters(params);
		final SearchResult<CUBStockInformationModel> result = flexibleSearchService.search(fsq);
		return result.getCount() > 0 ? returnStockLine(result.getResult(), plant) : null;
	}

	private CUBStockInformationModel returnStockLine(final List<CUBStockInformationModel> stockLines, final PlantModel plant)
	{
		if (plant.getFallbackPlant() != null)
		{
			for (final CUBStockInformationModel stockLine : stockLines)
			{
				if (plant.equals(stockLine.getPlant()))
				{
					return stockLine;
				}
			}
		}
		return stockLines.get(0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.CUBStockInformationDao#getCUBStockLinesForProductCode(java.lang.String)
	 */
	@Override
	public List<CUBStockInformationModel> getCUBStockLinesForProductCode(final String productCode)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(GETSTOCKFORPRODUCTCODE);
		final Map<String, Object> params = new HashMap<>();
		params.put("productCode", productCode);
		fsq.addQueryParameters(params);
		final SearchResult<CUBStockInformationModel> result = flexibleSearchService.search(fsq);
		return result.getCount() > 0 ? result.getResult() : null;
	}

}
