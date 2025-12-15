/**
 *
 */
package com.apb.core.search.solrfacetsearch.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiB2BUnitAccountOrPayerNumberProviderTest
{
	@Spy
	@InjectMocks
	private final AsahiB2BUnitAccountOrPayerNumberProvider b2bUnitAccountorPayerNumberProvider = new AsahiB2BUnitAccountOrPayerNumberProvider();

	@Mock
	private IndexedProperty indexedProperty;

	@Mock
	private AsahiB2BUnitModel b2bunit;

	@Mock
	private LanguageModel lang;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Mock
	private IndexConfig indexConfig;

	@Before
	public void setup()
	{
		when(b2bunit.getUid()).thenReturn("b2bUnitId");
		when(lang.getIsocode()).thenReturn("langCode");
		Mockito.lenient().when(fieldNameProvider.getFieldNames(indexedProperty, null))
				.thenReturn(Collections.singletonList("fieldName"));
		when(fieldNameProvider.getFieldNames(indexedProperty, "langCode"))
				.thenReturn(Collections.singletonList("localizedFieldName"));
		Mockito.lenient().when(indexConfig.getLanguages()).thenReturn(Collections.singletonList(lang));

	}

	@Test
	public void getFieldValueTest() throws FieldValueProviderException
	{
		when(b2bunit.getPayerId()).thenReturn("payerId");
		assertEquals(1, b2bUnitAccountorPayerNumberProvider.createFieldValue(b2bunit, lang, indexedProperty).size());
	}

	@Test
	public void getFieldValueLocalisedTest()
	{
		assertEquals(1, b2bUnitAccountorPayerNumberProvider.createFieldValue(b2bunit, lang, indexedProperty).size());
	}
}
