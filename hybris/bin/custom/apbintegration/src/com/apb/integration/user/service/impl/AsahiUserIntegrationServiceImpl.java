package com.apb.integration.user.service.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import com.apb.core.model.AsahiOrderPayloadModel;
import com.apb.integration.data.ApbOrderResponseData;
import com.apb.integration.data.AsahiUserResponseData;
import com.apb.integration.data.AsahiUsersRequestData;
import com.apb.integration.data.AsahiUsersToSalesforceRequestDTO;
import com.apb.integration.order.dto.AsahiOrderRequest;
import com.apb.integration.order.dto.AsahiOrderResponse;
import com.apb.integration.price.dto.AsahiPriceResponse;
import com.apb.integration.rest.client.AsahiRestClient;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.user.service.AsahiUserIntegrationService;
import com.apb.integration.users.dao.AsahiUsersIntegrationDao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.google.gson.Gson;

public class AsahiUserIntegrationServiceImpl implements AsahiUserIntegrationService {

	private static final Logger LOGGER = LogManager.getLogger(AsahiUserIntegrationServiceImpl.class);
	private static final String INTEGRATION_USERS_SERVICE_URL = "integration.userstosf.service.url.";
	private static final String WRITE_USERSTOSF_PAYLOAD_IN_LOG = "service.userstosf.write.in.log.";
	private static final String SGA_SITE_ID = "sga";

	@Resource(name = "asahiRestClient")
	private AsahiRestClient asahiRestClient;

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiconfigurationService;

	@Resource(name = "asahiUsersIntegrationDao")
	private AsahiUsersIntegrationDao asahiUsersIntegrationDao;

	@Resource(name = "asahiUsersToSFRequestConverter")
	private Converter<B2BCustomerModel, AsahiUsersRequestData> requestConverter;

	@Override
	public AsahiUserResponseData sendUsersToSalesforce(List<B2BCustomerModel> users) {
		final String url = this.asahiconfigurationService.getString(INTEGRATION_USERS_SERVICE_URL + SGA_SITE_ID, " ");
		// a populator to convert request send to PI
		List<AsahiUsersRequestData> userRequestDataList=new ArrayList<AsahiUsersRequestData>();
		for(B2BCustomerModel user:users)
		{
			AsahiUsersRequestData usersRequestData = requestConverter.convert(user);
			userRequestDataList.add(usersRequestData);
		}
		AsahiUsersToSalesforceRequestDTO usersToSFRequest = new AsahiUsersToSalesforceRequestDTO();
		usersToSFRequest.setAlbUserRequest(userRequestDataList);

		ResponseEntity<String> responseEntity = null;
		final AsahiUserResponseData usersResponseData = new AsahiUserResponseData();

		LOGGER.info("Calling UsersToSF Service with url---" + url);
		final Gson gson = new Gson();
		try
		{
			LOGGER.info("Calling UsersToSF Service with Request---" + gson.toJson(usersToSFRequest));
			asahiRestClient.executePOSTRestRequest(url, usersToSFRequest, Object.class, "usersToSF");
			usersResponseData.setStatusCode(200);
			LOGGER.info("----Users Data successfully Sent to SF---"+usersResponseData.getStatusCode());



		}
		catch (final Exception e)
		{
			LOGGER.error("----Users Data failed to sent SF---" );
			LOGGER.error("exception in usersToSF request{" + e + "}");
			usersResponseData.setStatusCode(500);
			usersResponseData.setFailureReason(e.getMessage());
		}


		return usersResponseData;
	}

	@Override
	public OrderModel getALBFirstWebOrder(UserModel user) {
		return asahiUsersIntegrationDao.getALBFirstWebOrder(user);
	}

	@Override
	public OrderModel getALBLastOrder(UserModel user) {
		return asahiUsersIntegrationDao.getALBLastOrder(user);
	}

	@Override
	public OrderModel getALBLastWebOrder(UserModel user) {
		return asahiUsersIntegrationDao.getALBLastWebOrder(user);
	}


}
