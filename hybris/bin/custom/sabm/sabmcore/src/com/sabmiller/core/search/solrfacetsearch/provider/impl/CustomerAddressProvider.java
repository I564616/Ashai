/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;


/**
 * CustomerAddressProvider
 */
public class CustomerAddressProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
	/** The Provider */
	private FieldNameProvider fieldNameProvider;
	/** The Service */
	private CommonI18NService commonI18NService;
	private ModelService modelService;
	private TypeService typeService;

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the typeService
	 */
	public TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * @param typeService
	 *           the typeService to set
	 */
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

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
			final B2BUnitModel b2bUnit = (B2BUnitModel) model;
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();

			final AddressModel address = getAddress(b2bUnit);
			final String attributeName = getAttributeName(indexedProperty);
			if (null != address)
			{
				fieldValues.addAll(getFieldValues(indexConfig, address, attributeName, indexedProperty));
			}
			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot evaluate name of non-b2bCustomer item");
		}
	}


	/**
	 * the method is get AddressModel from B2BUnitModel
	 *
	 * @param b2bUnitModel
	 * @return AddressModel
	 */
	protected AddressModel getAddress(final B2BUnitModel b2bUnitModel)
	{
		if (null != b2bUnitModel.getContactAddress())
		{
			return b2bUnitModel.getContactAddress();
		}
		else
		{
			final List<AddressModel> addressList = (List<AddressModel>) b2bUnitModel.getAddresses();
			if (CollectionUtils.isNotEmpty(addressList))
			{
				return addressList.get(0);
			}
		}
		return null;
	}

	protected Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final AddressModel address,
			final String attributeName, final IndexedProperty indexedProperty)
	{
		final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
		if (indexedProperty.isLocalized())
		{
			final Collection<LanguageModel> languages = indexConfig.getLanguages();
			for (final LanguageModel language : languages)
			{
				fieldValues.addAll(createFieldValue(address, language, attributeName, indexedProperty));
			}
		}
		else
		{
			fieldValues.addAll(createFieldValue(address, null, attributeName, indexedProperty));
		}
		return fieldValues;
	}


	protected Collection<FieldValue> createFieldValue(final AddressModel address, final LanguageModel language,
			final String attributeName, final IndexedProperty indexedProperty)
	{
		final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
		final Object value = getModelAttributeValue(address, attributeName);

		addFieldValues(fieldValues, indexedProperty, language, value);
		return fieldValues;
	}

	protected void addFieldValues(final Collection<FieldValue> fieldValues, final IndexedProperty indexedProperty,
			final LanguageModel language, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
				language == null ? null : language.getIsocode());
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}

	}

	protected Object getModelAttributeValue(final AddressModel address, final String attributeName)
	{
		Object value = null;

		final ComposedTypeModel composedType = getTypeService().getComposedTypeForClass(address.getClass());
		if (getTypeService().hasAttribute(composedType, attributeName))
		{
			value = getModelService().getAttributeValue(address, attributeName);
		}

		return value;
	}

	protected String getAttributeName(final IndexedProperty indexedProperty)
	{
		String attributeName = ValueProviderParameterUtils.getString(indexedProperty, "attribute", null);

		if (attributeName == null)
		{
			attributeName = indexedProperty.getName();
		}

		return attributeName;
	}

}
