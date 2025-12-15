/**
 *
 */
package com.apb.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * CustomerAccountPayerNumberProvider
 */
public class AsahiB2BUnitAccountOrPayerNumberProvider extends AsahiCustomerAccountPayerNumberProvider
{

	@Override
	protected List<FieldValue> createFieldValue(final AsahiB2BUnitModel b2bUnit, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		if (StringUtils.isNotEmpty(b2bUnit.getPayerId()))
		{
			addFieldValues(fieldValues, indexedProperty, language, b2bUnit.getUid() + b2bUnit.getPayerId());
		}
		else
		{
			addFieldValues(fieldValues, indexedProperty, language, b2bUnit.getUid());
		}
		return fieldValues;
	}
}

