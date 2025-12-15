/**
 *
 */
package com.sabmiller.core.b2b.dao;

import java.util.List;

import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;


/**
 * @author Siddarth
 *
 */
public interface CUBStockInformationDao
{
	List<CUBStockInformationModel> getAllCUBStockLines();

	/**
	 * @param productCode
	 * @param plant
	 * @return
	 */
	CUBStockInformationModel getCUBStockForProductAndPlant(String productCode, PlantModel plant);

	List<CUBStockInformationModel> getCUBStockLinesForProductCode(String productCode);
}
