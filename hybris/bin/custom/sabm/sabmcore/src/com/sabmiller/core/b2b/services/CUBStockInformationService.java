/**
 *
 */
package com.sabmiller.core.b2b.services;

import java.util.List;

import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;


/**
 * @author Siddarth
 *
 */
public interface CUBStockInformationService
{

	CUBStockInformationModel getCUBStockInformationForProductAndPlant(String productCode, PlantModel plant);

	List<CUBStockInformationModel> getCUBStockInformationForProduct(String productCode);

}
