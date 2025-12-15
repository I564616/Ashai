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

import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.core.util.UOMUtils;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.facades.product.data.ProductUOMMappingData;
import com.sabmiller.facades.product.data.UomData;
import java.util.stream.Collectors;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * The ProductUOMPopulator to populate the new ProductData attributes from ProductModel
 *
 * @author peng.yao
 * @date 2015-10-22
 */
public class ProductUOMPopulator implements Populator<ProductModel, ProductData>
{
	private static final Logger LOG = Logger.getLogger(ProductUOMPopulator.class);

	@Resource(name = "unitService")
	private SabmUnitService unitService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private Populator<ProductUOMMappingModel, ProductUOMMappingData> productUOMMappingPopulator;

	@Resource
	private ConfigurationService configurationService;

	/**
	 * Populate the target instance from the source instance.
	 *
	 * @param source
	 * @param target
	 * @throws ConversionException
	 *
	 */
	@Override
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		
		if(asahiSiteUtil.isCub())
		{
   		if (source != null && target != null)
   		{
   			ProductModel variant = source;
   			SABMAlcoholVariantProductEANModel eanProduct = null;
   
   			//Checking if the source product is instanceof SABMAlcoholVariantProductEANModel because the attribute UomMappings belongs to it.
   			while (variant instanceof VariantProductModel)
   			{
   				if (variant.getClass().equals(SABMAlcoholVariantProductEANModel.class))
   				{
   					eanProduct = (SABMAlcoholVariantProductEANModel) variant;
   					break;
   				}
   
   				variant = ((VariantProductModel) variant).getBaseProduct();
   			}
   
   			if (eanProduct != null)
   			{
   				//change for fix the sonar issues
   				convertUomMappings(eanProduct, target);
   				final Set<UnitModel> unitModels = eanProduct.getUnitList();
   				if (CollectionUtils.isNotEmpty(unitModels))
   				{
					final List<String> excludedUoms = Arrays
							.asList(configurationService.getConfiguration().getString("uom.exclude.codes", "").split(","));
							
   					target.setUomList(!excludedUoms.isEmpty() && excludedUoms != null ? UOMUtils.getUomList(unitModels).stream()
								.filter(uom -> !excludedUoms.contains(uom.getCode()))
								.collect(Collectors.toList()) : UOMUtils.getUomList(unitModels));
   				}
   			}
   		}
   		else
   		{
   			LOG.error("Unable to populate a null source or null target");
   		}
		}
	}



	private void convertUomMappings(final SABMAlcoholVariantProductEANModel eanProduct, final ProductData target)
	{
		final List<ProductUOMMappingModel> uomMappings = eanProduct.getUomMappings();

		if (CollectionUtils.isNotEmpty(uomMappings))
		{

			final List<ProductUOMMappingData> uomMappingList = new ArrayList<>();

			for (final ProductUOMMappingModel productUOM : uomMappings)
			{
				if (unitService.isValid(productUOM))
				{
					final ProductUOMMappingData unitData = new ProductUOMMappingData();
					productUOMMappingPopulator.populate(productUOM, unitData);

					// if the mapping have some blank or error attributes, will not display it
					if (StringUtils.isNotEmpty(unitData.getFromUnit()) && StringUtils.isNotEmpty(unitData.getToUnit())
							&& unitData.getQtyConversion() > 0)
					{
						uomMappingList.add(unitData);
					}
				}
			}

			target.setUomMappingList(uomMappingList);
		}
	}
}
