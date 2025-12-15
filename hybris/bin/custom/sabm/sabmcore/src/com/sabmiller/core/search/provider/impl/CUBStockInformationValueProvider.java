/**
 *
 */
package com.sabmiller.core.search.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.CUBStockInformationDao;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.dao.SabmProductDao;


/**
 * @author Siddarth
 *
 */
public class CUBStockInformationValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
	@Resource
	private CUBStockInformationDao cubStockInformationDao;
	@Resource
	private FieldNameProvider solrFieldNameProvider;
	final static String SEARCH_CUB_STOCK_LINE_SEPERATOR = "_";
	final static String fieldName = "CUBStockInformation";
	@Resource(name = "productService")
	private SabmProductService productService;
	private static final Logger LOG = LoggerFactory.getLogger(CUBStockInformationValueProvider.class);
	@Resource
	private SabmProductDao productDao;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig config, final IndexedProperty property, final Object model)
			throws FieldValueProviderException
	{
		LOG.debug("Fetching CUB Stock Information");
		final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
		if (model instanceof SABMAlcoholVariantProductEANModel)
		{
			final SABMAlcoholVariantProductEANModel eanProductModel = (SABMAlcoholVariantProductEANModel) model;

			try
			{
				final String materialProductCode = getMaterialCodeFromEan(eanProductModel);
				final List<CUBStockInformationModel> cubStockLines = cubStockInformationDao
						.getCUBStockLinesForProductCode(materialProductCode);
				if (CollectionUtils.isNotEmpty(cubStockLines))
				{
					for (final CUBStockInformationModel cubStockInfo : cubStockLines)
					{
						final StringBuilder stockInfoString = new StringBuilder();
						stockInfoString.append(cubStockInfo.getPlant().getPlantId()).append(SEARCH_CUB_STOCK_LINE_SEPERATOR)
								.append(cubStockInfo.getStockStatus().getCode());
						if (stockInfoString.length() > 0)
						{
							addFieldValues(property, fieldValues, stockInfoString.toString());
						}
					}
				}
			}
			catch (final Exception e)
			{
				LOG.error("Unable to index CUB Stock Information for Product " + ((ProductModel) model).getCode());
				e.printStackTrace();
			}
		}
		return fieldValues;
	}

	private void addFieldValues(final IndexedProperty indexedProperty, final Collection<FieldValue> fieldValues,
			final String stockInfo)
	{
		final Collection<String> fieldNames = solrFieldNameProvider.getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, stockInfo));
		}
	}

	private String getMaterialCodeFromEan(final ProductModel productModel)
	{
		if (productModel instanceof SABMAlcoholVariantProductEANModel)
		{
			//First check for Lead Sku. If it does not exist, then query for material whose base product is this ean
			final SABMAlcoholVariantProductEANModel eanProductModel = (SABMAlcoholVariantProductEANModel) productModel;
			if (eanProductModel.getLeadSku() != null)
			{
				return eanProductModel.getLeadSku().getCode();
			}

			//Lead Sku not avaialble,query for material whose base product is this ean
			final SABMAlcoholVariantProductMaterialModel materialModel = productDao.findMaterialProductByEan(eanProductModel);
			if (materialModel != null)
			{
				return materialModel.getCode();
			}
		}

		throw new ModelNotFoundException("No Material Product found for this Ean Product " + productModel);
	}
}
