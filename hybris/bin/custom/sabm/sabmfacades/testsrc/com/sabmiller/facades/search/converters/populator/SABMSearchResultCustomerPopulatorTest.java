/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.GeneratedSabmCoreConstants.Enumerations.B2BUnitStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.constants.SabmFacadesConstants;


/**
 *
 */
@UnitTest
public class SABMSearchResultCustomerPopulatorTest
{
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private AsahiB2BUnitModel b2bUnit;

	@Mock
	private SabmB2BUnitService b2bUnitService;

	@InjectMocks
	private SABMSearchResultCustomerPopulator sabmSearchResultCustomerPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		//sabmSearchResultCustomerPopulator = new SABMSearchResultCustomerPopulator();
		sabmSearchResultCustomerPopulator.setCommonI18NService(commonI18NService);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testPopulate()
	{
		final SearchResultValueData source = mock(SearchResultValueData.class);

		final Map<String, Object> values = new HashMap<>();
		values.put(SabmFacadesConstants.CUSTOMER_NAME, "test_CUSTOMER_NAME");
		values.put(SabmFacadesConstants.CUSTOMER_ID, "test_CUSTOMER_ID");
		values.put(SabmFacadesConstants.CUSTOMER_ARR_STREET, "test_CUSTOMER_ARR_STREET");
		values.put(SabmFacadesConstants.CUSTOMER_ARR_SUBURB, "test_CUSTOMER_ARR_SUBURB");
		values.put(SabmFacadesConstants.CUSTOMER_ARR_POSTCODE, "test_CUSTOMER_ARR_POSTCODE");
		values.put(SabmFacadesConstants.CUSTOMER_ARR_ISOCODE, "test_CUSTOMER_ARR_ISOCODE");
		values.put(SabmFacadesConstants.CUSTOMER_ACC_PAY_NUMBER, "test_CUSTOMER_ACC_PAY_NUMBER");
		values.put(SabmFacadesConstants.CUSTOMER_PRIMARY_ADMIN_STATUS, "testPrimaryAdminStatus");
		values.put(SabmFacadesConstants.ASAHI_CUSTOMER_DEALS_EXISTS, true);
		values.put(SabmFacadesConstants.CUSTOMER_DEALS_EXISTS, true);
		values.put(SabmFacadesConstants.CUSTOMER_ORDERING_STATUS, "Test_Customer_Ordering_Status");
		given(source.getValues()).willReturn(values);
		given(b2bUnitService.findB2BUnitStatus("test_CUSTOMER_ID")).willReturn(B2BUnitStatus.ACTIVE);
		given(b2bUnitService.getUnitForUid("test_CUSTOMER_ID")).willReturn(b2bUnit);
		given(b2bUnitService.findB2BUnitStatus("test_CUSTOMER_ID")).willReturn("b2bUnitStatus");
		final CustomerData target = new CustomerData();
		sabmSearchResultCustomerPopulator.populate(source, target);

		Assert.assertEquals("test_CUSTOMER_NAME", target.getName());
		Assert.assertEquals("test_CUSTOMER_ID", target.getUid());
		Assert.assertEquals("test_CUSTOMER_ARR_STREET", target.getAddressStreetName());
		Assert.assertEquals("test_CUSTOMER_ARR_SUBURB", target.getAddressSuburb());
		Assert.assertEquals("test_CUSTOMER_ARR_POSTCODE", target.getPostCode());
		Assert.assertEquals("test_CUSTOMER_ARR_ISOCODE", target.getIsocodeShort());
		Assert.assertEquals("test_CUSTOMER_ACC_PAY_NUMBER", target.getAccountPayerNumber());
		Assert.assertEquals(true, target.getDealsExists());
		Assert.assertEquals("Test_Customer_Ordering_Status", target.getOrderingStatus());
	}

}
