/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.facades.product;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * The Interface ApbProductFacade.
 * 
 * @author Kuldeep.Singh1
 */
public interface ApbProductFacade extends ProductFacade{
	
	/**
	 * Update products.
	 *
	 * @param productData the product data
	 */
	public void importProducts(ProductData productData, final String siteUid);
} 