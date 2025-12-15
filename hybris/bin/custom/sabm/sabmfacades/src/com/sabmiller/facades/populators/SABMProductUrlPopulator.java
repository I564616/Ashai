/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.product.converters.populator.ProductUrlPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.apb.core.util.AsahiSiteUtil;


/**
 * The Class SABMProductUrlPopulator.
 */
public class SABMProductUrlPopulator extends ProductUrlPopulator
{

	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.product.converters.populator.ProductUrlPopulator#populate(de.hybris.platform.
	 * core.model.product.ProductModel, de.hybris.platform.commercefacades.product.data.ProductData)
	 */
	@Override
	public void populate(final ProductModel source, final ProductData target)
	{
		if(asahiSiteUtil.isCub())
		{
   		Assert.notNull(source, "Parameter source cannot be null.");
   		Assert.notNull(target, "Parameter target cannot be null.");
   
   		target.setCode(source.getCode());
   
   		//Override productData name if sellingName is not empty
   		if (StringUtils.isNotEmpty(source.getSellingName()) && StringUtils.isNotEmpty(source.getPackConfiguration()))
   		{
   			target.setName(source.getSellingName());
   			target.setPackConfiguration(source.getPackConfiguration());
   		}
   		else
   		{
   			target.setName(source.getName());
   		}
   
   		target.setUrl(getProductModelUrlResolver().resolve(source));
		}
		else
		{
			super.populate(source, target);
		}
	}

}
