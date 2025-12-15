/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.comparators.ProductUOMComparator;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


/**
 * The Class ProductUnitListHandler.
 */
public class ProductUnitListHandler implements DynamicAttributeHandler<Set<UnitModel>, SABMAlcoholVariantProductEANModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ProductUnitListHandler.class);

	/**
	 * getter of dynamic attribute unitList. Fetch the data from the attribute UomMappings of
	 * the @SABMAlcoholVariantProductEANModel
	 *
	 * @param product
	 *           the product to fetch the related UnitModel Set
	 * @return a Set of @UnitModel, empty if no mapping is found in the product. The set is ordered by
	 *         ProductUOMMappingModel.qtyConversion
	 */
	@Override
	public Set<UnitModel> get(final SABMAlcoholVariantProductEANModel product)
	{
		//Creating a new Set to be returned. It'll be empty if no mapping is present in product.
		final Set<UnitModel> unitSet = new LinkedHashSet<>();

		final SortedSet<ProductUOMMappingModel> uomMappings = new TreeSet<>(ProductUOMComparator.INSTANCE);
		uomMappings.addAll(product.getUomMappings());

		LOG.debug("Unsorted uomMappings: {}", product.getUomMappings());
		LOG.debug("Sorted uomMappings: {}", uomMappings);

		if (CollectionUtils.isNotEmpty(uomMappings))
		{
			for (final ProductUOMMappingModel uomMapping : uomMappings)
			{
				if (uomMapping != null)
				{
					//Setting both the fromUnit and toUnit in the list to keep all the product's units. The Set will remove possible duplicates.
					if (uomMapping.getFromUnit() != null && uomMapping.getToUnit() != null)
					{
						if (uomMapping.getToUnit().getCode() != null)
						{
							if (uomMapping.getToUnit().getCode().equals(SabmCoreConstants.UNIT_KEG))
							{
								unitSet.add(uomMapping.getToUnit());
								break;
							}
							else
							{
								unitSet.add(uomMapping.getToUnit());
								unitSet.add(uomMapping.getFromUnit());
							}
						}
					}
				}
			}
		}
		else
		{
			LOG.debug("No UOM mapping found in product: {}", product.getCode());
		}

		LOG.debug("Returning Set<UnitModel> for product: {} is: {}", product.getCode(), unitSet);

		return unitSet;
	}

	/**
	 * setter of dynamic attribute unitList, throws exception because this is a dynamic attribute, only to fetch data.
	 *
	 * @param arg0
	 *           the arg0
	 * @param arg1
	 *           the arg1
	 */
	@Override
	public void set(final SABMAlcoholVariantProductEANModel arg0, final Set<UnitModel> arg1)
	{
		throw new UnsupportedOperationException("Set of dynamic attribute 'unitList' of SABMAlcoholVariantProductEAN is disabled!");
	}
}
