/**
 *
 */
package com.sabmiller.core.b2b.dao;

import java.util.List;

import com.sabmiller.core.model.MaxOrderQtyModel;


/**
 * @author Siddarth
 *
 */
public interface CUBMaxOrderQuantityDao
{
	List<MaxOrderQtyModel> getCUBMaxOrderQuantityForProductCode(String productCode);
}
