/**
 *
 */
package com.apb.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * CustomerAddressRegionProvider
 */
public class AsahiCustomerAddressRegionProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
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
			final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) model;

			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();

			if (indexedProperty.isLocalized())
			{
				final Collection<LanguageModel> languages = indexConfig.getLanguages();
				for (final LanguageModel language : languages)
				{
					fieldValues.addAll(createFieldValue(b2bUnit, language, indexedProperty));
				}
			}
			else
			{
				fieldValues.addAll(createFieldValue(b2bUnit, null, indexedProperty));
			}
			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot evaluate name of non-b2bCustomer item");
		}
	}

	protected List<FieldValue> createFieldValue(final AsahiB2BUnitModel b2bUnit, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

		final RegionModel region = getRegion(b2bUnit);
		if (null != region && StringUtils.isNotEmpty(region.getIsocodeShort()))
		{
			addFieldValues(fieldValues, indexedProperty, language, region.getIsocodeShort());
		}
		return fieldValues;
	}

	/**
	 * the method is get RegionModel from B2BUnitModel
	 *
	 * @param b2bUnitModel
	 * @return RegionModel
	 */
	protected RegionModel getRegion(final AsahiB2BUnitModel b2bUnitModel)
	{
		if (null != b2bUnitModel.getContactAddress())
		{
			final AddressModel address = b2bUnitModel.getContactAddress();
			if (null != address.getRegion())
			{
				return address.getRegion();
			}
		}
		else
		{
			final List<AddressModel> addressList = (List<AddressModel>) b2bUnitModel.getAddresses();
			if (CollectionUtils.isNotEmpty(addressList))
			{
				if (null != addressList.get(0).getRegion())
				{
					return addressList.get(0).getRegion();
				}
			}
		}
		return null;
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
