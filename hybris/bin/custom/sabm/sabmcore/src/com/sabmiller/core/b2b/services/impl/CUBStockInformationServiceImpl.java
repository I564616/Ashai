/**
 *
 */
package com.sabmiller.core.b2b.services.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;

import com.sabmiller.core.b2b.dao.CUBStockInformationDao;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;


/**
 * @author Siddarth
 *
 */
public class CUBStockInformationServiceImpl implements CUBStockInformationService
{

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.CUBStockInformationService#getCUBStockInformationForProduct(de.hybris.platform.
	 * core.model.product.ProductModel)
	 */

	@Resource
	private CUBStockInformationDao cubStockInformationDao;

	@Override
	@Cacheable(value = "stockCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(true,true,false,'stock',#plant.plantId,#productCode)")
	public CUBStockInformationModel getCUBStockInformationForProductAndPlant(final String productCode, final PlantModel plant)
	{
		return cubStockInformationDao.getCUBStockForProductAndPlant(productCode, plant);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.CUBStockInformationService#getCUBStockInformationForProduct(java.lang.String)
	 */
	@Override
	public List<CUBStockInformationModel> getCUBStockInformationForProduct(final String productCode)
	{
		return cubStockInformationDao.getCUBStockLinesForProductCode(productCode);
	}

}
