/**
 *
 */
package com.sabmiller.commons.translators;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;

import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.model.SABMRecommendationModel;


/**
 * @author Siddarth
 *
 */
public class SABMProductTranslator extends AbstractSpecialValueTranslator
{

	private ModelService modelService;
	private ProductService productService;
	private CatalogVersionService catalogVersionService;
	final String PRODUCTCATALOGID = "sabmProductCatalog";

	@Override
	public String performExport(final Item item) throws ImpExException
	{
		if (Registry.getCoreApplicationContext().getBean("modelService") instanceof ModelService)
		{
			modelService = (ModelService) Registry.getCoreApplicationContext().getBean("modelService");
			final SABMRecommendationModel recommendationModel = modelService.get(item);
			if (recommendationModel.getRecommendationType().equals(RecommendationType.PRODUCT)
					&& recommendationModel.getProductCode() != null)
			{
				return getProductName(recommendationModel.getProductCode());
			}
		}

		return null;
	}

	public String getProductName(final String productCode)
	{
		productService = (ProductService) Registry.getCoreApplicationContext().getBean("productService");
		catalogVersionService = (CatalogVersionService) Registry.getCoreApplicationContext().getBean("catalogVersionService");
		final ProductModel product = productService.getProductForCode(
				catalogVersionService.getCatalogVersion(PRODUCTCATALOGID, CatalogManager.ONLINE_VERSION), productCode);
		return product != null ? product.getName() : "";
	}
}