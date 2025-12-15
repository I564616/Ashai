
/*
 *
 */
package com.apb.integration.order.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.StringWriter;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.apb.core.model.AsahiOrderPayloadModel;
import com.apb.integration.data.ApbOrderResponseData;
import com.apb.integration.order.dto.AsahiOrderRequest;
import com.apb.integration.order.dto.AsahiOrderResponse;
import com.apb.integration.order.service.AsahiOrderIntegrationService;
import com.apb.integration.rest.client.AsahiRestClient;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.util.AsahiIntegrationUtil;
import com.asahi.integration.rest.client.AsahiRestClientUtil;


public class AsahiOrderIntegrationServiceImpl implements AsahiOrderIntegrationService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiOrderIntegrationServiceImpl.class);
	private static final String INTEGRATION_ORDER_SERVICE_URL = "integration.order.service.url.";
	private static final String WRITE_ORDER_PAYLOAD_IN_LOG = "service.order.write.in.log.";
	private static final String SGA_SITE_ID = "sga";

	@Resource(name = "asahiRestClient")
	private AsahiRestClient asahiRestClient;

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiconfigurationService;

	@Resource(name = "asahiOrderRequestConverter")
	private Converter<OrderModel, AsahiOrderRequest> requestConverter;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The order payload sequence id generator. */
	@Resource(name = "orderPayloadSequenceIdGenerator")
	private PersistentKeyGenerator orderPayloadSequenceIdGenerator;

	@Resource
	private AsahiRestClientUtil asahiRestClientUtil;

	@Resource
	private AsahiIntegrationUtil asahiIntegrationUtil;



	@Override
	public ApbOrderResponseData sendOrder(final OrderModel order)
	{

		final String url = this.asahiconfigurationService.getString(INTEGRATION_ORDER_SERVICE_URL + order.getSite().getUid(), " ");
		// a populator to convert request send to client
		final AsahiOrderRequest orderRequest = requestConverter.convert(order);
		ResponseEntity<String> responseEntity = null;


		final int result = 0;

		LOGGER.info("Calling Order Service with url---" + url);
		try
		{
			final JAXBContext contextRequestObj = JAXBContext.newInstance(AsahiOrderRequest.class);

			final Marshaller marshallerObj = contextRequestObj.createMarshaller();
			marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			final StringWriter orderXml = new StringWriter();
			marshallerObj.marshal(orderRequest, orderXml);

			if (this.asahiconfigurationService.getBoolean(WRITE_ORDER_PAYLOAD_IN_LOG + order.getSite().getUid(), false))
			{
				LOGGER.info("Order Service Payload---" + orderXml.toString());
			}

			//saving order payload
			final AsahiOrderPayloadModel orderPayload = this.modelService.create(AsahiOrderPayloadModel.class);
			orderPayload.setSequenceId(orderPayloadSequenceIdGenerator.generate().toString());
			orderPayload.setOrderId(order.getCode());
			orderPayload.setOrderXml(orderXml.toString());

			if (order.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID))
			{
				orderPayload.setBaseSiteId(order.getSite().getUid());
			}

			this.modelService.save(orderPayload);

		}
		catch (final Exception e)
		{
			LOGGER.error("exception in order xml request{" + e + "}");
		}

		if (order.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID))
		{
			final Map<String, String> config = asahiIntegrationUtil.getAPIConfiguration("service.order", order.getSite().getUid());
			asahiRestClientUtil.executeRestOrderRequest(orderRequest, AsahiOrderResponse.class, config);

		}
		else
		{
			responseEntity = asahiRestClient.executeOrderAXRestRequest(url, orderRequest, AsahiOrderResponse.class, "order");
		}
		final ApbOrderResponseData orderResponseData = new ApbOrderResponseData();
		orderResponseData.setStatusCode(404);
		if (responseEntity != null)
		{
			orderResponseData.setStatusCode(responseEntity.getStatusCodeValue());
			LOGGER.info(" Response status Code---" + responseEntity.getStatusCodeValue());
		}
		if (order.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID))
		{
			orderResponseData.setStatusCode(200);
			orderResponseData.setOrderStatus("success");
		}

		LOGGER.info("Returning Order Service Response---" + result);

		return orderResponseData;
	}
}
