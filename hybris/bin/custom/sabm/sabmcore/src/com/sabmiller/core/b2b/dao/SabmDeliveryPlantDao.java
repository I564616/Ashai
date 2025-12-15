/**
 *
 */
package com.sabmiller.core.b2b.dao;

import com.sabmiller.core.model.PlantModel;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmDeliveryPlantDao
{
	public PlantModel lookupPlant(final String plantId);
}
