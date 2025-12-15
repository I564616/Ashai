/**
 *
 */
package com.sabmiller.core.product;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;

import com.sabmiller.core.model.ProductUOMMappingModel;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmUnitService extends UnitService
{
	public UnitModel createUnit(String code);

	public boolean isValid(ProductUOMMappingModel uomMappingModel);
}
