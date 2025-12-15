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
package com.sabmiller.core.report.service;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchProviderException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.util.PGPUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import com.sabmiller.integration.salesforce.SabmSftpFileUpload;

import com.sabmiller.facades.salesforce.welcomemail.CustomerWelcomeMailData;
import com.sabmiller.core.report.service.salesforcedata.SalesforceWelcomeMailData;
import com.sabmiller.core.report.service.salesforcedata.SalesforceWelcomeMailToData;
import com.sabmiller.salesforcerestclient.SABMSFIntegrationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabmiller.salesforcerestclient.SABMSalesForceAccessTokenRequestHandler;
import com.sabmiller.salesforcerestclient.SABMSalesForceWelcomeMailPostHandler;
import com.sabmiller.salesforcerestclient.SFTokenRequest;
import com.sabmiller.salesforcerestclient.SFTokenResponse;
import com.sabmiller.salesforcerestclient.data.SalesforceEmailRequestData;
import com.sabmiller.salesforcerestclient.SalesForceEmailSmsPostResponse;
import com.sabmiller.core.report.strategy.DefaultUserToVenueDataExportStrategy;
import org.apache.commons.lang3.StringUtils;
import java.security.Key;


/**
 * A process action to send customer and related venue data to SFMC
 */
public class UserToVenueDataExportServiceImpl implements UserToVenueDataExportService
{
	private static final Logger LOG = LoggerFactory.getLogger(UserToVenueDataExportServiceImpl.class);

	private Map<String, DefaultUserToVenueDataExportStrategy> strategyMap = new HashMap<>();
	private FlexibleSearchService flexibleSearchService;

	private DefaultUserToVenueDataExportStrategy defaultStrategy;

	@Override
	public void generateReport(final String strategy) throws IOException, PGPException, NoSuchProviderException, Exception {
		final DefaultUserToVenueDataExportStrategy defaultStrategy = getDefaultUserToVenueDataExportStrategy(strategy);

		defaultStrategy.uploadFileToSFTP();
	}

	private DefaultUserToVenueDataExportStrategy getDefaultUserToVenueDataExportStrategy(final String strategy) {

		return strategyMap.getOrDefault(strategy, defaultStrategy);
	}


	public Map<String, DefaultUserToVenueDataExportStrategy> getStrategyMap() {
		return strategyMap;
	}

	public void setStrategyMap(final Map<String, DefaultUserToVenueDataExportStrategy> strategyMap) {
		this.strategyMap = strategyMap;
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	public DefaultUserToVenueDataExportStrategy getDefaultStrategy() {
		return defaultStrategy;
	}

	public void setDefaultStrategy(final DefaultUserToVenueDataExportStrategy defaultStrategy) {
		this.defaultStrategy = defaultStrategy;
	}


}
