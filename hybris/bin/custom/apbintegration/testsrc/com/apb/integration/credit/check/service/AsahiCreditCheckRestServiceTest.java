package com.apb.integration.credit.check.service;

import static org.mockito.BDDMockito.given;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.integration.credit.check.service.impl.AsahiCreditCheckIntegrationServiceImpl;
import com.apb.integration.data.ApbCreditCheckData;
import com.apb.integration.service.config.AsahiConfigurationService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;


@UnitTest
public class AsahiCreditCheckRestServiceTest
{

	private static final String ACCOUNT_NUMBER = "100686";
	private static final String REST_CREDIT_CHECK_STUB = "integration.credit.check.service.stub.check.apb";
	public static final String CREDIT_CHECK_MOCK_RESPONSE = "integration.credit.check.service.stub.response.apb";

	@InjectMocks
	private AsahiCreditCheckIntegrationServiceImpl asahiCreditCheckIntegrationService = new AsahiCreditCheckIntegrationServiceImpl();


	@Mock
	private AsahiConfigurationService asahiConfigurationService;


	@Before
	public void setUp() throws ImpExException
	{

		MockitoAnnotations.initMocks(this);
	}

	/**
	 * This is a sample test method.
	 */
	@Test
	public void testgetCreditCheck()
	{
		String responseJson = "{ \"creditCheckResponse\":[ { \"accountNum\": \"100686\", \"creditRemaining\": \"20000\", \"isBlocked\": \"false\" }]} ";
		given(asahiConfigurationService.getBoolean(REST_CREDIT_CHECK_STUB, false)).willReturn(true);
		given(asahiConfigurationService.getString(CREDIT_CHECK_MOCK_RESPONSE, "")).willReturn(responseJson);

		ApbCreditCheckData response = asahiCreditCheckIntegrationService.getCreditCheck(ACCOUNT_NUMBER);
		Assert.assertEquals(ACCOUNT_NUMBER, response.getAccountNum());
		Assert.assertEquals(false, response.isIsBlocked());
	}

}
