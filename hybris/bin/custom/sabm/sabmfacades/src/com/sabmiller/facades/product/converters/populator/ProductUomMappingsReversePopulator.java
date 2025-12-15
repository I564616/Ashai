/**
 *
 */
package com.sabmiller.facades.product.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.facades.product.data.ProductUOMMappingData;


/**
 * @author joshua.a.antony
 *
 */
public class ProductUomMappingsReversePopulator implements Populator<ProductUOMMappingData, ProductUOMMappingModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(ProductUomMappingsReversePopulator.class);

	@Resource(name = "unitService")
	private SabmUnitService unitService;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ProductUOMMappingData source, final ProductUOMMappingModel target) throws ConversionException
	{
		try
		{
			target.setFromUnit(unitService.getUnitForCode(source.getFromUnit()));
			target.setToUnit(unitService.getUnitForCode(source.getToUnit()));
			target.setQtyConversion(source.getQtyConversion());
		}
		catch (final Exception e)
		{
			LOG.error("Exception occured while populating Alternate UOM. From Unit : {}, To Unit : {} ", source.getFromUnit(),
					source.getToUnit());
		}
	}

}
