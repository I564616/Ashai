/**
 *
 */
package com.apb.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalModel;
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

import org.apache.commons.collections4.SetUtils;

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * CustomerEmailProvider
 */
public class AsahiCustomerEmailProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
	/** The Provider */
	private FieldNameProvider fieldNameProvider;
	/** The Service */
	private CommonI18NService commonI18NService;

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
		if (model instanceof AsahiB2BUnitModel)
		{
			final AsahiB2BUnitModel b2bunit = (AsahiB2BUnitModel) model;
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

	protected List<FieldValue> createFieldValue(final AsahiB2BUnitModel b2bunit, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		final List<String> customerEmails = new ArrayList<>();
		for (final PrincipalModel pModel : SetUtils.emptyIfNull(b2bunit.getMembers()))
		{
			if (pModel != null && pModel.getClass().getName().equals(B2BCustomerModel.class.getName()))
			{
				final B2BCustomerModel userModel = (B2BCustomerModel) pModel;

				customerEmails.add(userModel.getUid());
			}
		}
		addFieldValues(fieldValues, indexedProperty, language, customerEmails);
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

