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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;


/**
 * The ProductUOMPopulator to populate the new ProductData attributes from ProductModel
 *
 * @author peng.yao
 * @date 2015-10-22
 */
public class ProductDealPopulator implements Populator<ProductModel, ProductData>
{
	private static final Logger LOG = Logger.getLogger(ProductDealPopulator.class);


	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;


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
   
   				try
   				{
   					target.setDealsFlag(false);

					if(!asahiCoreUtil.isNAPUser())
   					{   
						final List<String> deals = sabmDealsSearchFacade.getDealsForProduct(eanProduct.getCode());
	
						if (CollectionUtils.isNotEmpty(deals))
						{
							SabmStringUtils.getSortedDealTitles(deals);
							target.setDealsTitle(deals);
							target.setDealsFlag(true);
						}
				    }
   				}
   				catch (final Exception e)
   				{
   					LOG.warn("exception while fetching deal info for product");
   				}
   			}
   		}
   		else
   		{
   			LOG.error("Unable to populate a null source or null target");
   		}
		}
	}


}
