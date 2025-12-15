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
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * CustomerAccountPayerNumberProvider
 */
public class B2BUnitAccountOrPayerNumberProvider extends CustomerAccountPayerNumberProvider
{

	@Override
	protected List<FieldValue> createFieldValue(final B2BUnitModel b2bUnit, final LanguageModel language,
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

