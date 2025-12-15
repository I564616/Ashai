package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.CUBMaxOrderQuantityDao;
import com.sabmiller.core.enums.MaxOrderQtyRuleType;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


public class MaxOrderQuantityValueResolver extends AbstractValueResolver<ProductModel, Void, Void>
{
	private static final Logger LOG = LoggerFactory.getLogger(MaxOrderQuantityValueResolver.class);
	private static final String INDEX_PROPERTY_DELIMETER = "_";
	private static final String CUSTOMER_MAX_ORDER_QTY = "customerMaxOrderQty";
	private static final String PLANT_MAX_ORDER_QTY = "plantMaxOrderQty";
	private static final String GLOBAL_MAX_ORDER_QTY = "globalMaxOrderQty";
	@Resource
	private CUBMaxOrderQuantityDao cubMaxOrderQuantityDao;
	@Override
	protected void addFieldValues(final InputDocument inputDocument, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<Void, Void> valueResolverContext) throws FieldValueProviderException
	{
		try {
			if (SABMAlcoholVariantProductEANModel.class.isInstance(productModel))
			{
				final SABMAlcoholVariantProductEANModel eanProductModel = (SABMAlcoholVariantProductEANModel) productModel;
				List<MaxOrderQtyModel> maxOrderQtyModels = cubMaxOrderQuantityDao.getCUBMaxOrderQuantityForProductCode(eanProductModel.getCode());
				System.out.println("******************indexing" + indexedProperty.getName());
				final String propertyName = indexedProperty.getName();
				List<String> propertyValue = Collections.emptyList();
				if(CollectionUtils.isNotEmpty(maxOrderQtyModels)) {
					if (CUSTOMER_MAX_ORDER_QTY.equals(propertyName))
					{
						maxOrderQtyModels = maxOrderQtyModels.stream()
								.filter(maxOrderQty -> MaxOrderQtyRuleType.CUSTOMER_RULE.equals(maxOrderQty.getRuleType()))
								.collect(Collectors.toList());
						if(CollectionUtils.isNotEmpty(maxOrderQtyModels)) {
							propertyValue = maxOrderQtyModels.stream()
									.map(maxOrderQty -> new StringBuilder().append(maxOrderQty.getB2bunit().getUid())
											.append(INDEX_PROPERTY_DELIMETER)
											.append(maxOrderQty.getDefaultAvgMaxOrderQtyEnabled() ? maxOrderQty.getDefaultAvgMaxOrderQty()
													: maxOrderQty.getMaxOrderQty())
											.append(INDEX_PROPERTY_DELIMETER).append(maxOrderQty.getStartDate()).append(INDEX_PROPERTY_DELIMETER)
											.append(maxOrderQty.getEndDate()).toString())
									.collect(Collectors.toList());
						}
					}
					else if (PLANT_MAX_ORDER_QTY.equals(propertyName))
					{
						maxOrderQtyModels = maxOrderQtyModels.stream()
								.filter(maxOrderQty -> MaxOrderQtyRuleType.PLANT_RULE.equals(maxOrderQty.getRuleType()))
								.collect(Collectors.toList());
						if(CollectionUtils.isNotEmpty(maxOrderQtyModels)) {
							propertyValue = maxOrderQtyModels.stream()
									.map(maxOrderQty -> new StringBuilder().append(maxOrderQty.getPlant().getPlantId())
											.append(INDEX_PROPERTY_DELIMETER)
											.append(maxOrderQty.getDefaultAvgMaxOrderQtyEnabled() ? maxOrderQty.getDefaultAvgMaxOrderQty()
													: maxOrderQty.getMaxOrderQty())
											.append(INDEX_PROPERTY_DELIMETER).append(maxOrderQty.getStartDate()).append(INDEX_PROPERTY_DELIMETER)
											.append(maxOrderQty.getEndDate()).toString())
									.collect(Collectors.toList());
						}
					}
					else if (GLOBAL_MAX_ORDER_QTY.equals(propertyName))
					{
						maxOrderQtyModels = maxOrderQtyModels.stream()
								.filter(maxOrderQty -> MaxOrderQtyRuleType.GLOBAL_RULE.equals(maxOrderQty.getRuleType()))
								.collect(Collectors.toList());
						if(CollectionUtils.isNotEmpty(maxOrderQtyModels)) {
							propertyValue = maxOrderQtyModels.stream()
									.map(maxOrderQty -> new StringBuilder().append(maxOrderQty.getDefaultAvgMaxOrderQtyEnabled()
											? maxOrderQty.getDefaultAvgMaxOrderQty()
													: maxOrderQty.getMaxOrderQty()).append(INDEX_PROPERTY_DELIMETER).append(maxOrderQty.getStartDate())
											.append(INDEX_PROPERTY_DELIMETER).append(maxOrderQty.getEndDate()).toString())
									.collect(Collectors.toList());
						}
					}
					System.out.println("****************propertyValue" + propertyValue);
					addFieldValue(inputDocument, indexerBatchContext, indexedProperty, propertyValue,
							valueResolverContext.getFieldQualifier());
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("Unable to index CUB Ma order Qunatity for Product " + ((ProductModel) productModel).getCode());
			e.printStackTrace();
		}
	}
}
