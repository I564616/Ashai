/**
 *
 */
package com.sabmiller.core.search.provider.impl;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * provider add product UOM index to SLOR
 *
 * @author xue.zeng
 *
 */
@SuppressWarnings("SE_BAD_FIELD")
public class ProductUOMProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{

	private FieldNameProvider fieldNameProvider;
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductUOMProvider.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.solrfacetsearch.provider.FieldValueProvider#getFieldValues(de.hybris.platform.solrfacetsearch.
	 * config.IndexConfig, de.hybris.platform.solrfacetsearch.config.IndexedProperty, java.lang.Object)
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof SABMAlcoholVariantProductEANModel)
		{
			final SABMAlcoholVariantProductEANModel product = (SABMAlcoholVariantProductEANModel) model;
			final Set<UnitModel> units = product.getUnitList();
			if (CollectionUtils.isNotEmpty(units))
			{
				final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
				final UnitModel drumUom = checkForDrumUom(units);
				if (drumUom != null)
				{
					addFieldValues(indexedProperty, fieldValues, drumUom);
				}
				else
				{
					for (final UnitModel unitModel : units)
					{
						addFieldValues(indexedProperty, fieldValues, unitModel);
					}
				}

				return fieldValues;
			}
		}
		else
		{
			LOG.error("this provider shall only be used with products, model is not SABMAlcoholVariantProductEANModel instance!");
		}

		return Collections.emptyList();
	}

	/**
	 * @param indexedProperty
	 * @param fieldValues
	 * @param uom
	 */
	private void addFieldValues(final IndexedProperty indexedProperty, final Collection<FieldValue> fieldValues,
			final UnitModel uom)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			if (!StringUtils.isBlank(uom.getCode()) && !StringUtils.isBlank(uom.getName()))
			{
				fieldValues.add(new FieldValue(fieldName,
						uom.getCode() + SabmCoreConstants.SEARCH_PRODUCT_UOM_SEPARATOR + uom.getName()));
			}
		}
	}

	/**
	 * @param units
	 * @return
	 */
	private UnitModel checkForDrumUom(final Set<UnitModel> units)
	{
		for (final UnitModel uom : units)
		{
			if (StringUtils.equalsIgnoreCase(uom.getCode(), "EA"))
			{
				return uom;
			}
		}
		return null;
	}

	/**
	 * @return the fieldNameProvider
	 */
	public FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	/**
	 * @param fieldNameProvider
	 *           the fieldNameProvider to set
	 */
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

}
