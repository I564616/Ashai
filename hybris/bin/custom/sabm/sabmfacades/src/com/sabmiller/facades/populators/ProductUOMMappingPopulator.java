/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.facades.product.data.ProductUOMMappingData;


/**
 * The ProductUOMMappingPopulator to populate the UnitOfMeasureMappingData from ProductUOMMappingModel.
 *
 * @author peng.yao
 * @date 2015-10-22
 */
public class ProductUOMMappingPopulator implements Populator<ProductUOMMappingModel, ProductUOMMappingData>
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(ProductUOMMappingPopulator.class);
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/**
	 * Populate the target instance from the source instance.
	 *
	 * @param source
	 *           the @ProductUOMMappingModel
	 * @param target
	 *           the @ProductUOMMappingData
	 * @throws ConversionException
	 *            if fromUnit or toUnit are null in the source @ProductUOMMappingModel
	 */
	@Override
	public void populate(final ProductUOMMappingModel source, final ProductUOMMappingData target) throws ConversionException
	{
		if(asahiSiteUtil.isCub())
		{
   		if (source != null && target != null)
   		{
   			//if any attribute of the ProductUOMMappingModel is null, skipping the populating code.
   			if (source.getFromUnit() == null || source.getToUnit() == null || source.getQtyConversion() == null)
   			{
   				LOG.error("One or more attributes (fromUnit, toUnit, qtyConversion) are null in the productUOMMapping: "
   						+ source.getPk());
   			}
   			else
   			{
   				target.setFromUnit(source.getFromUnit().getName());
   				target.setQtyConversion(source.getQtyConversion());
   				target.setToUnit(source.getToUnit().getName());
   			}
   		}
   		else
   		{
   			LOG.error("Unable to populate a null source or null target");
   		}
		}
	}

}
