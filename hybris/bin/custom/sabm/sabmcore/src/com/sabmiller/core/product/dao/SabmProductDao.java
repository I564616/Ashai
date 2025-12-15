/**
 *
 */
package com.sabmiller.core.product.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;

import java.util.List;

import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * The Interface SabmProductDao.
 */
public interface SabmProductDao extends ProductDao
{

	/**
	 * Find product by code and hierarchy.
	 *
	 * @param code
	 *           the code
	 * @param heirarchy
	 *           the heirarchy
	 * @return the SABM alcohol variant product material model
	 */
	SABMAlcoholVariantProductMaterialModel findProductByCodeAndHierarchy(String code, String heirarchy);

	/**
	 * Find product ea ns without images.
	 *
	 * @return the list
	 */
	List<SABMAlcoholVariantProductEANModel> findProductEANsWithoutImages();

	/**
	 * Find material product by ean.
	 *
	 * @param eanProductModel
	 *           the ean product model
	 * @return the SABM alcohol variant product material model
	 */
	SABMAlcoholVariantProductMaterialModel findMaterialProductByEan(SABMAlcoholVariantProductEANModel eanProductModel);

	/**
	 * Gets the SABM alcohol product.
	 *
	 * @param dealBrand
	 *           the deal brand
	 * @return the SABM alcohol product
	 */
	SABMAlcoholProductModel getSABMAlcoholProduct(String dealBrand);

	/**
	 * Get the product by the Product's level2.
	 *
	 * @param dealBrand
	 *           the deal brand
	 * @return List<SABMAlcoholVariantProductMaterialModel>
	 */
	List<SABMAlcoholVariantProductMaterialModel> getProductByLevel2(String dealBrand);

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
	 * Retrieve all materials filtered by lifecycle status and SAP availability status
	 * */
	List<SABMAlcoholVariantProductEANModel> getMaterialProducts(final CatalogVersionModel catalogVersion);

	List<SABMAlcoholVariantProductMaterialModel> findMaterialsByCode(final String code);

	List<SABMAlcoholVariantProductMaterialModel> getKegMaterials(final CatalogVersionModel catalogVersion);

	List<OrderEntryModel> getOrderEntryForCustomerRule(final ProductModel productModel, final B2BUnitModel b2bUnitModel,
			final CMSSiteModel cmsSiteModel);

	List<OrderEntryModel> getOrderEntryForPlantRule(final ProductModel productModel, final PlantModel plantModel,
			final CMSSiteModel cmsSiteModel);

	List<OrderEntryModel> getOrderEntryForGlobalRule(final ProductModel productModel, final CMSSiteModel cmsSiteModel);
}
