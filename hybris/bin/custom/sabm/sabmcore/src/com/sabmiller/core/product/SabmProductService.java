/**
 *
 */
package com.sabmiller.core.product;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.Collection;
import java.util.List;

import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * The Interface SabmProductService.
 */
public interface SabmProductService extends ProductService
{

	/**
	 * Check if product is visible to the site.
	 *
	 * @param productModel
	 *           the product
	 * @return true if visible
	 */
	boolean isProductVisible(final SABMAlcoholVariantProductEANModel productModel);

	/**
	 * Check if product is purchasable to the site.
	 *
	 * @param productModel
	 *           the product
	 * @return true if purchasable
	 */
	boolean isProductPurchasable(final SABMAlcoholVariantProductEANModel productModel);

	/**
	 * Check if product is searchable to the site.
	 *
	 * @param productModel
	 *           the product
	 * @return true if searchable
	 */
	boolean isProductSearchable(final SABMAlcoholVariantProductEANModel productModel);

	/**
	 * Product exist.
	 *
	 * @param code
	 *           the code
	 * @param heirarchy
	 *           the heirarchy
	 * @return true, if successful
	 */
	boolean productExist(String code, String heirarchy);

	/**
	 * Product exist in offline catalog.
	 *
	 * @param code
	 *           the code
	 * @return true, if successful
	 */
	boolean productExistInOfflineCatalog(String code);

	/**
	 * Gets the product in sap hierarchy.
	 *
	 * @param code
	 *           the code
	 * @param hierarchy
	 *           the hierarchy
	 * @return the product in sap hierarchy
	 */
	SABMAlcoholVariantProductMaterialModel getProductInSapHierarchy(String code, String hierarchy);

	/**
	 * Gets the material from ean.
	 *
	 * @param eanProductCode
	 *           the ean product code
	 * @return the material from ean
	 */
	SABMAlcoholVariantProductMaterialModel getMaterialFromEan(String eanProductCode);

	/**
	 * Gets the material from ean.
	 *
	 * @param productModel
	 *           the product model
	 * @return the material from ean
	 * @throws ModelNotFoundException
	 *            the model not found exception
	 */
	SABMAlcoholVariantProductMaterialModel getMaterialFromEan(ProductModel productModel);

	/**
	 * Gets the material code from ean.
	 *
	 * @param eanProductCode
	 *           the ean product code
	 * @return the material code from ean
	 */
	String getMaterialCodeFromEan(String eanProductCode);

	/**
	 * Gets the ean from material.
	 *
	 * @param material
	 *           the material
	 * @return the ean from material
	 */
	SABMAlcoholVariantProductEANModel getEanFromMaterial(String material);

	/**
	 * Gets the ean code from material.
	 *
	 * @param material
	 *           the material
	 * @return the ean code from material
	 */
	String getEanCodeFromMaterial(String material);

	/**
	 * Generate alcohol product code.
	 *
	 * @param abv
	 *           the abv
	 * @param categoryAttribute
	 *           the category attribute
	 * @param categoryVariety
	 *           the category variety
	 * @param brand
	 *           the brand
	 * @param style
	 *           the style
	 * @return the string
	 */
	String generateAlcoholProductCode(String abv, String categoryAttribute, String categoryVariety, String brand, String style);

	/**
	 * Fetch core product range.
	 *
	 * @return the list
	 */
	List<SABMAlcoholVariantProductEANModel> fetchCoreProductRange();

	/**
	 * Get the Brand from the fetched product.
	 *
	 * @param dealBrand
	 *           the deal brand
	 * @return SABMAlcoholProductModel
	 */
	SABMAlcoholProductModel getSABMAlcoholProduct(final String dealBrand);

	/**
	 * Get the product by the Product's level2.
	 *
	 * @param dealBrand
	 *           the deal brand
	 * @return List<SABMAlcoholVariantProductMaterialModel>
	 */
	List<SABMAlcoholVariantProductMaterialModel> getProductByLevel2(final String dealBrand);

	/**
	 * Gets the product by hierarchy.
	 *
	 * @param level1
	 *           the level1
	 * @param level2
	 *           the level2
	 * @param level3
	 *           the level3
	 * @param level4
	 *           the level4
	 * @param level5
	 *           the level5
	 * @param level6
	 *           the level6
	 * @return the product by hierarchy
	 */
	List<SABMAlcoholVariantProductMaterialModel> getProductByHierarchy(String level1, String level2, String level3, String level4,
			String level5, String level6);

	/**
	 * Gets the product by hierarchy.
	 *
	 * @param condition
	 *           the condition
	 * @return the product by hierarchy
	 */
	List<SABMAlcoholVariantProductMaterialModel> getProductByHierarchy(ComplexDealConditionModel condition);

	/**
	 * Gets the product by hierarchy filter excluded.
	 *
	 * @param condition
	 *           the condition
	 * @return the product by hierarchy filter excluded
	 */
	List<SABMAlcoholVariantProductMaterialModel> getProductByHierarchyFilterExcluded(ComplexDealConditionModel condition);

	/**
	 * Gets the products by deal.
	 *
	 * @param deal
	 *           the deal
	 * @return the products by deal
	 */
	List<SABMAlcoholVariantProductMaterialModel> getProductsByDeal(DealModel deal);

	/**
	 * Gets the products code.
	 *
	 * @param products
	 *           the products
	 * @return the products code
	 */
	List<String> getProductsCode(Collection<? extends ProductModel> products);

	/**
	 * Retrieves a product by its code.
	 *
	 * @param catalogVersionModel
	 *           the catalog version to search the product from.
	 * @param code
	 *           the product code
	 * @return true, if the product exists, false otherwise.
	 */
	ProductModel getProductFromCodeWithGivenCatalogVersion(final CatalogVersionModel catalogVersionModel, final String code);

	/**
	 * Utility Service to filter out non purchaseable products.
	 *
	 * @param products
	 *           the products
	 * @return the collection
	 */
	Collection<ProductModel> filterNonPurchaseableProducts(final Collection<ProductModel> products);

	/**
	 * Gets the product for code without throwing exceptions.
	 *
	 * @param code
	 *           the code
	 * @return the product model or null in case of no product, ambiguous code, null/empty code
	 */
	ProductModel getProductForCodeSafe(String code);

	/**
	 * Find excluded product.
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return the list
	 */
	List<ProductModel> findExcludedProduct(List<AbstractDealConditionModel> dealConditions);

	/**
	 * Finds the product's sub-channel popularity
	 *
	 * @param eanProduct
	 *           the EAN product to retrieve the ranking from.
	 * @param subChannel
	 *           the sub-channel used to retrieve the Product's sub-channel popularity.
	 * @return the product's popularity rank, 999 if none is found.
	 */
	int getProductSubchannelPopularityRankBySubchannel(final SABMAlcoholVariantProductEANModel eanProduct,
			final String subChannel);

	ProductModel getProductForCodeForTemplate(final String code);

	/**
	 * Retrieve product hierarchy
	 * */
	boolean exportProductHierarchy(final CatalogVersionModel catalogVersion);

	SABMAlcoholVariantProductMaterialModel getMaterialByCode(final String code);

	List<SABMAlcoholVariantProductMaterialModel> getKegMaterials();

	int getAverageQuantity(final MaxOrderQtyModel maxOrderQty);
	MaxOrderQtyModel getMaxOrderQuantity(final ProductModel productModel);
}
