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

import java.util.List;

import com.apb.facades.product.data.AlcoholTypeData;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.FlavourData;
import com.apb.facades.product.data.ItemGroupData;
import com.apb.facades.product.data.PackageTypeData;
import com.apb.facades.product.data.ProductGroupData;
import com.apb.facades.product.data.SubProductGroupData;

import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * The Interface AsahiProductRefernceFacade.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiProductRefernceFacade{
	
	/**
	 * Import product group.
	 *
	 * @param productGroupData the product group data
	 */
	public void importProductGroup(ProductGroupData productGroupData);
	
	/**
	 * Import alcohol type.
	 *
	 * @param alcoholTypeData the alcohol type data
	 */
	public void importAlcoholType(AlcoholTypeData alcoholTypeData);
	
	/**
	 * Import package type.
	 *
	 * @param packageTypeData the package type data
	 */
	public void importPackageType(PackageTypeData packageTypeData);
	
	/**
	 * Import flavour.
	 *
	 * @param flavourData the flavour data
	 */
	public void importFlavour(FlavourData flavourData);
	
	/**
	 * Import brand.
	 *
	 * @param brandData the brand data
	 */
	public void importBrand(BrandData brandData);
	
	/**
	 * Import item group.
	 *
	 * @param itemGroupData the item group data
	 */
	public void importItemGroup(ItemGroupData itemGroupData);
	
	/**
	 * Import sub product group.
	 *
	 * @param subProductGroupData the sub product group data
	 */
	public void importSubProductGroup(SubProductGroupData subProductGroupData);
	
	/** The method will fetch the PDP recommended product for the user
	 * 
	 * @param code the product code
	 * @return list of products
	 */
	public List<ProductData> getPDPRecommendedProducts(String code);
	
	/** The method will fetch the Cart recommended product for the user
	 * 
	 * @param code the product code
	 * @return list of products
	 */
	public List<ProductData> getCartRecommendedProducts();
} 