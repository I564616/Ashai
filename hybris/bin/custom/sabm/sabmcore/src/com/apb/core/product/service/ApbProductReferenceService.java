package com.apb.core.product.service;

import java.util.List;

import com.apb.core.model.AlcoholTypeModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.FlavourModel;
import com.apb.core.model.ItemGroupsModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.core.model.ProductGroupModel;
import com.apb.core.model.SubProductGroupModel;
import com.apb.core.model.UnitVolumeModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * The Interface ApbProductReferenceService.
 * @author Kuldeep.Singh1
 */
public interface ApbProductReferenceService {
	
	/**
	 * Gets the alcohol type for code.
	 *
	 * @param code the code
	 * @return the alcohol type for code
	 */
	public AlcoholTypeModel getAlcoholTypeForCode(String code);
	
	/**
	 * Gets the item group for code.
	 *
	 * @param code the code
	 * @return the item group for code
	 */
	public ItemGroupsModel getItemGroupForCode(String code);
	
	/**
	 * Gets the package type for code.
	 *
	 * @param code the code
	 * @return the package type for code
	 */
	public PackageTypeModel getPackageTypeForCode(String code);
	
	/**
	 * Gets the flavour for code.
	 *
	 * @param code the code
	 * @return the flavour for code
	 */
	public FlavourModel getFlavourForCode(String code);
	
	/**
	 * Gets the brand for code.
	 *
	 * @param code the code
	 * @return the brand for code
	 */
	public BrandModel getBrandForCode(String code);
	
	/**
	 * Gets the product for code.
	 *
	 * @param catalogVersionModel the catalog version model
	 * @param code the code
	 * @return the product for code
	 */
	public ProductModel getProductForCode(CatalogVersionModel catalogVersionModel, final String code);
	
	/**
	 * Gets the unit for code.
	 *
	 * @param code the code
	 * @return the unit for code
	 */
	public UnitModel getUnitForCode(String code);

	/**
	 * Gets the currency by iso code.
	 *
	 * @param currencyIso the currency iso
	 * @return the currency by iso code
	 */
	public CurrencyModel getCurrencyForIsoCode(String currencyIso);

	/**
	 * Gets the package size for code.
	 *
	 * @param code the code
	 * @return the package size for code
	 */
	public PackageSizeModel getPackageSizeForCode(String code);

	/**
	 * Gets the unit volume for code.
	 *
	 * @param code the code
	 * @return the unit volume for code
	 */
	public UnitVolumeModel getUnitVolumeForCode(String code);

	/**
	 * Gets the sub product group for code.
	 *
	 * @param code the code
	 * @return the sub product group for code
	 */
	public SubProductGroupModel getSubProductGroupForCode(String code);

	/**
	 * Gets the order entry by product and order id.
	 *
	 * @param productId the product id
	 * @param orderId the order id
	 * @return the order entry by product and order id
	 */
	public List<OrderEntryModel> getOrderEntryByProductAndOrderId(
			String productId, String orderId);
	
	/**
	 * Gets the product group for code.
	 *
	 * @param code the code
	 * @return the product group for code
	 */
	public ProductGroupModel getProductGroupForCode(String code);
	
	/** Get the last 3 monts orders
	 * 
	 * @param user the current user
	 */
	List<OrderModel> getRecommendedProducts(final CustomerModel User);
}
