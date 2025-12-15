/**
 *
 */
package com.apb.core.search.solrfacetsearch.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
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

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
public class AsahiCustomerAddressProviderTest
{
	@Spy
	@InjectMocks
	private final AsahiCustomerAddressProvider asahiCustomerAddressProvider = new AsahiCustomerAddressProvider();

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private IndexConfig indexConfig;

	@Mock
	private IndexedProperty indexedProperty;

	@Mock
	private AsahiB2BUnitModel b2bunit;

	@Mock
	private LanguageModel lang;

	@Mock
	private Object model;

	@Mock
	private B2BCustomerModel pModel;

	@Mock
	private AddressModel address;
	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private Object value;
	@Mock
	private ModelService modelService;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup()
	{
		when(b2bunit.getAddresses()).thenReturn(Collections.singletonList(address));
		when(indexedProperty.getName()).thenReturn("attributeName");
		when(lang.getIsocode()).thenReturn("langCode");
		when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(Collections.singletonList("fieldName"));
		when(fieldNameProvider.getFieldNames(indexedProperty, "langCode"))
				.thenReturn(Collections.singletonList("localizedFieldName"));
		when(indexConfig.getLanguages()).thenReturn(Collections.singletonList(lang));
		when(typeService.getComposedTypeForClass(address.getClass())).thenReturn(composedType);
		Mockito.lenient().when(modelService.getAttributeValue(address, "attributeName")).thenReturn(value);
	}

	@Test
	public void getFieldValueTest() throws FieldValueProviderException
	{
		when(b2bunit.getContactAddress()).thenReturn(address);
		Mockito.lenient().when(b2bunit.getMembers()).thenReturn(Collections.singleton(pModel));
		Mockito.lenient().when(pModel.getUid()).thenReturn("Customer Mail ID");
		when(indexedProperty.isLocalized()).thenReturn(false);
		assertEquals(1, asahiCustomerAddressProvider.getFieldValues(indexConfig, indexedProperty, b2bunit).size());
	}

	@Test
	public void getFieldValueLocalisedTest() throws FieldValueProviderException
	{
		when(indexedProperty.isLocalized()).thenReturn(true);
		assertEquals(1, asahiCustomerAddressProvider.getFieldValues(indexConfig, indexedProperty, b2bunit).size());
	}

	@Test
	public void getFieldValueExceptionTest() throws FieldValueProviderException
	{
		exception.expect(FieldValueProviderException.class);
		exception.expectMessage("Cannot evaluate name of non-b2bCustomer item");
		asahiCustomerAddressProvider.getFieldValues(indexConfig, indexedProperty, model);

	}
}
