package com.apb.integration.order.service;

import static org.mockito.BDDMockito.given;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.apb.core.model.AsahiOrderPayloadModel;
import com.apb.integration.data.ApbOrderResponseData;
import com.apb.integration.order.dto.AsahiOrderRequest;
import com.apb.integration.order.dto.AsahiOrderResponse;
import com.apb.integration.order.service.impl.AsahiOrderIntegrationServiceImpl;
import com.apb.integration.rest.client.AsahiRestClient;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.util.AsahiIntegrationUtil;
import com.asahi.integration.rest.client.AsahiRestClientUtil;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;


@UnitTest
public class AsahiOrderRestServiceTest
{
	private static final String INTEGRATION_ORDER_SERVICE_URL = "integration.order.service.url.apb";

	@InjectMocks
	private AsahiOrderIntegrationServiceImpl asahiOrderIntegrationService;

	@Mock
	private AsahiConfigurationService asahiConfigurationService;

	@Mock
	private Converter<OrderModel, AsahiOrderRequest> requestConverter;

	@Mock
	private AsahiRestClient asahiRestClient;
	
	@Mock
	private ModelService modelService;
	
	@Mock
	private PersistentKeyGenerator orderPayloadSequenceIdGenerator;
	
	@Mock
	private AsahiIntegrationUtil asahiIntegrationUtil;
	@Mock
	private AsahiRestClientUtil asahiRestClientUtil;

	private OrderModel order;

	@Before
	public void setUp() throws ImpExException
	{

		MockitoAnnotations.initMocks(this);
	}

	/**
	 * This is a sample test method.
	 */
	@Test
	public void testSendOrder()
	{

		AsahiOrderResponse orderResponse = new AsahiOrderResponse();
		orderResponse.setOrderStatus("SUCCESS");
		orderResponse.setOrderStatus("");
		BaseSiteModel baseSiteModel = new BaseSiteModel("sga");
		order = new OrderModel();
		order.setCode("100");
		order.setCompanyCode("ABC");
		order.setDeferredDelivery(false);
		order.setDeliveryCost(50.0);
		order.setSite(baseSiteModel);
		AsahiOrderPayloadModel orderPayloadModel = new AsahiOrderPayloadModel();
		ResponseEntity<String> responseEntity = new ResponseEntity(orderResponse, HttpStatus.OK);
		AsahiOrderRequest orderRequest = new AsahiOrderRequest();
		given(asahiConfigurationService.getString(INTEGRATION_ORDER_SERVICE_URL + order.getSite().getUid(), " ")).willReturn("url");
		given(requestConverter.convert(order)).willReturn(orderRequest);
		given(modelService.create(AsahiOrderPayloadModel.class)).willReturn(orderPayloadModel);
		given(orderPayloadSequenceIdGenerator.generate()).willReturn("20");
		given(asahiRestClient.executeOrderAXRestRequest("url", orderRequest, AsahiOrderResponse.class, "order"))
				.willReturn(responseEntity);
		given(asahiIntegrationUtil.getAPIConfiguration("service.order", order.getSite().getUid())).willReturn(null);
		ApbOrderResponseData orderResponseData = asahiOrderIntegrationService.sendOrder(order);
		Assert.assertEquals(HttpStatus.OK.value(), orderResponseData.getStatusCode().intValue());
	}
}
