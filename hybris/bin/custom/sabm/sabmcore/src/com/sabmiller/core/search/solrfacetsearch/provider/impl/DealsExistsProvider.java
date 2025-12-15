/**
 *
 */
package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
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

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.DealModel;


/**
 * @author ross.hengjun.zhu
 *
 */
public class DealsExistsProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{

	/** The Provider */
	private FieldNameProvider fieldNameProvider;
	/** The Service */
	private CommonI18NService commonI18NService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;
	@Resource(name = "dealsService")
	private DealsService dealsService;

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof B2BUnitModel)
		{
			final B2BUnitModel b2bunit = (B2BUnitModel) model;
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();

			if (indexedProperty.isLocalized())
			{
				final Collection<LanguageModel> languages = indexConfig.getLanguages();
				for (final LanguageModel language : languages)
				{
					fieldValues.addAll(createFieldValue(b2bunit, language, indexedProperty));
				}
			}
			else
			{
				fieldValues.addAll(createFieldValue(b2bunit, null, indexedProperty));
			}
			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot evaluate name of non-b2bUnit item");
		}
	}

	protected List<FieldValue> createFieldValue(final B2BUnitModel b2bunit, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

		final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(b2bunit.getUid());
		final List<DealModel> deals = dealsService.getSpecificDeals(b2bUnit, true);

		if (deals != null && deals.size() != 0)
		{
			addFieldValues(fieldValues, indexedProperty, language, true);
		}
		else
		{
			addFieldValues(fieldValues, indexedProperty, language, false);
		}

		return fieldValues;
	}

	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty,
			final LanguageModel language, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
				language == null ? null : language.getIsocode());
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}

}
