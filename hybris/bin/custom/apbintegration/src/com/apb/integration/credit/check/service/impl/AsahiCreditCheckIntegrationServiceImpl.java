package com.apb.integration.credit.check.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.integration.credit.check.dto.AsahiCreditCheckReq;
import com.apb.integration.credit.check.dto.AsahiCreditCheckRequest;
import com.apb.integration.credit.check.dto.AsahiCreditCheckRes;
import com.apb.integration.credit.check.dto.AsahiCreditCheckResponse;
import com.apb.integration.credit.check.service.AsahiCreditCheckIntegrationService;
import com.apb.integration.data.ApbCreditCheckData;
import com.apb.integration.rest.client.AsahiRestClient;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


public class AsahiCreditCheckIntegrationServiceImpl implements AsahiCreditCheckIntegrationService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiCreditCheckIntegrationServiceImpl.class);
	private static final String REST_CREDIT_CHECK_URL = "integration.credit.check.service.url.apb";
	private static final String REST_CREDIT_CHECK_STUB = "integration.credit.check.service.stub.check.apb";
	private static final String REST_CREDIT_CHECK_OFFLINE = "integration.credit.check.service.offline.apb";
	public static final String CREDIT_CHECK_MOCK_RESPONSE = "integration.credit.check.service.stub.response.apb";

	@Resource(name = "asahiRestClient")
	private AsahiRestClient asahiRestClient;

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Override
	public ApbCreditCheckData getCreditCheck(final String accNum)
	{

		final AsahiCreditCheckReq creditCheckReq = new AsahiCreditCheckReq();
		final List<AsahiCreditCheckRequest> creditCheckRequestList = new ArrayList<>();
		final AsahiCreditCheckRequest creditCheckRequest = new AsahiCreditCheckRequest();
		creditCheckRequest.setAccountNum(accNum);
		creditCheckRequestList.add(creditCheckRequest);
		creditCheckReq.setCreditCheckRequest(creditCheckRequestList);

		final String url = this.asahiConfigurationService.getString(REST_CREDIT_CHECK_URL, " ");

		AsahiCreditCheckRes creditCheckRes;
		final Gson gson = new Gson();

		if (this.asahiConfigurationService.getBoolean(REST_CREDIT_CHECK_STUB, false))
		{
			creditCheckRes = creditCheckServiceMockResponse();

		}
		else
		{
			try
			{
				LOGGER.info("Credit Check Service Request ::" + gson.toJson(creditCheckReq));
				LOGGER.info("Credit Check URL ::" + url.replace('\n', '_').replace('\r', '_'));

				LOGGER.debug("CreditCheckCallStart");
				creditCheckRes = (AsahiCreditCheckRes) asahiRestClient.executePOSTRestRequest(url, creditCheckReq,
						AsahiCreditCheckRes.class, "creditCheck");
				LOGGER.info("CreditCheckCallEnd");
				LOGGER.info("Credit Check Response ::" + gson.toJson(creditCheckRes));
			}
			catch (final Exception e)
			{
				creditCheckRes = createDefaultResponse(accNum);
				LOGGER.error("exception in check credit", e);
			}
		}
		ApbCreditCheckData creditCheckData = new ApbCreditCheckData();
		if (null != creditCheckRes)
		{
			creditCheckData = convertCreditCheckResponse(creditCheckRes);
			return creditCheckData;
		}
		return null;

	}

	private AsahiCreditCheckRes createDefaultResponse(final String accNum)
	{
		AsahiCreditCheckRes creditCheckRes = new AsahiCreditCheckRes();
		if (this.asahiConfigurationService.getBoolean(REST_CREDIT_CHECK_OFFLINE, true))
		{
			creditCheckRes = creditCheckServiceMockResponse();
		}
		return creditCheckRes;

	}

	private ApbCreditCheckData convertCreditCheckResponse(final AsahiCreditCheckRes creditCheckRes)
	{
		final ApbCreditCheckData creditCheckData = new ApbCreditCheckData();
		if (CollectionUtils.isNotEmpty(creditCheckRes.getCreditCheckResponse()))
		{
			final AsahiCreditCheckResponse creditCheckResponse = creditCheckRes.getCreditCheckResponse().get(0);
			creditCheckData.setAccountNum(creditCheckResponse.getAccountNum());
			creditCheckData.setCreditRemaining(creditCheckResponse.getCreditRemaining());
			if (StringUtils.isNotEmpty(creditCheckResponse.getIsBlocked()))
			{
				creditCheckData.setIsBlocked(Boolean.parseBoolean(creditCheckResponse.getIsBlocked()));
			}
		}
		return creditCheckData;

	}

	public AsahiCreditCheckRes creditCheckServiceMockResponse()
	{

		final ObjectMapper mapper = new ObjectMapper();
		try
		{
			final String responseJson = this.asahiConfigurationService.getString(CREDIT_CHECK_MOCK_RESPONSE, "");
			final AsahiCreditCheckRes creditCheckRes = mapper.readValue(responseJson, AsahiCreditCheckRes.class);
			return creditCheckRes;

		}
		catch (final IOException exception)
		{
			LOGGER.error("exception in parsing mock response", exception);
			return null;
		}
	}

}
