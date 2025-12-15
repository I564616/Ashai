/**
 *
 */
package com.sabmiller.facades.product;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.List;

import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmProductFacade extends ProductFacade
{
	public void saveProduct(ProductData productData);

	public boolean productExistInOfflineCatalog(String productCode);

	/**
	 * According to the product for Deals
	 *
	 * @param productCode
	 * @return List<DealData>
	 */
	List<DealJson> getDealsForProduct(String productCode);

	List<SABMAlcoholVariantProductMaterialModel> getKegMaterials();

}
