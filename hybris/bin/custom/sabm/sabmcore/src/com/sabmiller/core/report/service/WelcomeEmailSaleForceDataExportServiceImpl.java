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
import org.apache.commons.lang3.StringUtils;
import java.security.Key;


/**
 * A process action to send customer data to SFMC to intiate welcome email from sales force.
 */
public class WelcomeEmailSaleForceDataExportServiceImpl implements WelcomeEmailSaleForceDataExportService
{
	private static final Logger LOG = LoggerFactory.getLogger(WelcomeEmailSaleForceDataExportServiceImpl.class);
	private SabmCSVFileGenerator sabmCSVFileGenerator;
	private SabmSftpFileUpload sabmSftpFileUpload;
	private SabmB2BUnitService b2bUnitService;
	private FlexibleSearchService flexibleSearchService;
	private SecureTokenService secureTokenService;
	private ModelService modelService;

	private static final String DATE_PATTERN = "yyyyMMddHHmmss";
	private final DateFormat dataDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Resource(name = "sabmSalesForceAccessTokenRequestHandler")
   SABMSalesForceAccessTokenRequestHandler sabmSalesForceAccessTokenRequestHandler;
   
	@Resource(name = "sabmSalesForceWelcomeMailPostHandler")
   SABMSalesForceWelcomeMailPostHandler sabmSalesForceWelcomeMailPostHandler;




	@Override
	public void generateReport(final String reportType) throws IOException, PGPException, NoSuchProviderException
	{
		final List<B2BCustomerModel> customers = getAllCustomerToSendWelcomeEmail();

		if (CollectionUtils.isNotEmpty(customers))
		{
			generateTokenAndUpdateCustomer(customers);
			final List<List<String>> reportData = getCustomerReportData(customers);
			uploadFileToSFTP(reportData);
			updateCustomerWelcomeEmailStatus(customers);
		}
	}



	protected List<B2BCustomerModel> getAllCustomerToSendWelcomeEmail()
	{
		final String query = "select {cus.PK} " + "from {B2BCustomer AS cus} "
				+ "WHERE {cus.onboardWithWelcomeEmail} = ?onboardWithWelcomeEmail and {cus.welcomeEmailStatus} = ?welcomeEmailStatus";

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("onboardWithWelcomeEmail", Boolean.TRUE);
		searchQuery.addQueryParameter("welcomeEmailStatus", Boolean.FALSE);
		final SearchResult<B2BCustomerModel> processes = flexibleSearchService.search(searchQuery);
		return processes.getResult();
	}

	private void generateTokenAndUpdateCustomer(final List<B2BCustomerModel> customers)
	{

		for (final B2BCustomerModel customerModel : customers)
		{
			final long timeStamp = Config.getLong("welcomeEmail.password.tokenValiditySecond", 9153600000000L);
			final SecureToken data = new SecureToken(customerModel.getUid().length() > 30 ? customerModel.getUid().substring(0, 30) : customerModel.getUid(),System.currentTimeMillis() + timeStamp);
			final String token = getSecureTokenService().encryptData(data);
			customerModel.setToken(token);
			modelService.save(customerModel);
		}
	}


	private void updateCustomerWelcomeEmailStatus(final List<B2BCustomerModel> customers)
	{


		for (final B2BCustomerModel customerModel : customers)
		{
			customerModel.setWelcomeEmailStatus(true);
			customerModel.setWelcomeEmailSentDate(new Date());
			modelService.save(customerModel);
			// update the status for the customer's b2bunit
			b2bUnitService.updateB2BUnitStatus(customerModel, Boolean.TRUE, Boolean.FALSE);
		}


	}

	private List<List<String>> getCustomerReportData(final List<B2BCustomerModel> customers)
	{


		final List<List<String>> reportData = new ArrayList<List<String>>();
		for (final B2BCustomerModel customer : customers)
		{
		final List<String> customerData = new ArrayList<>();
		customerData.add(customer.getPk() + "");
		customerData.add(SabmStringUtils.trimToEmpty(customer.getFirstName()));
		customerData.add(SabmStringUtils.trimToEmpty(customer.getLastName()));
		customerData.add(customer.getUid());
		customerData.add(dataDateFormat.format(customer.getCreationtime()));
			try {
				customerData.add(URLEncoder.encode(customer.getToken(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOG.error("Exception while encoding",e);
			}
		customerData.add(customer.getDefaultB2BUnit() != null ? customer.getDefaultB2BUnit().getUid() : "");
		customerData.add(customer.getDefaultB2BUnit() != null ? customer.getDefaultB2BUnit().getName() : "");
		customerData.add(customer.getDefaultB2BUnit() != null ? customer.getDefaultB2BUnit().getPayerId() : "");
		customerData
				.add(customer.getDefaultB2BUnit() != null ? getPaymentTerms(customer.getDefaultB2BUnit()) : "");
		customerData.add(customer.getPrimaryAdmin().toString());
		reportData.add(customerData);
		}
		return reportData;
	}

	private List<String> getHeaderLine(final List<String> headers)
	{
		headers.add("Pk");
		headers.add("FirstName");
		headers.add("LastName");
		headers.add("EmailOpt-In");
		headers.add("DateOfRegistration");
		headers.add("PasswordSetToken");
		headers.add("AccountNumber");
		headers.add("AccountName");
		headers.add("PayeeNumber");
		headers.add("PaymentTerms");
		headers.add("PrimaryAdmin");
		return headers;

	}


	private void uploadFileToSFTP(final List<List<String>> reportData)
			throws IOException, PGPException, NoSuchProviderException
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		final List<String> headers = new ArrayList<String>();
		final String fileExt = ".csv";
		final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("welcomeemail" + File.separator),
				sdf.format(new Date()) + "_HybrisWelcomeEmailUser", fileExt, reportData, getHeaderLine(headers));
		final String ecryptedFileName = SabmCSVUtils.getFullPath("welcomeemail" + File.separator) + File.separator
				+ sdf.format(new Date())
				+ "_HybrisWelcomeEmailUser" + ".csv.pgp";
		PGPPublicKey key = PGPUtils.readPublicKey(Config.getString("salesforce.encryptionkey", ""));
		try (final OutputStream out = new FileOutputStream(ecryptedFileName)) {
			PGPUtils.encryptFile(out, file, key, false, false);
		} catch (Exception e) {
			LOG.error("Exception while encrypting file:", e);
			throw e;
		}
		sabmSftpFileUpload.upload(new File(ecryptedFileName));
		LOG.info("uploaded welcome email file");
	}



	private String getPaymentTerms(final B2BUnitModel b2bUnit)
	{

		if (b2bUnit != null && b2bUnit.getPayerId() != null)
		{
			final B2BUnitModel parentB2BUnitModel = b2bUnitService.findTopLevelB2BUnit(b2bUnit.getPayerId());

			if (parentB2BUnitModel != null && parentB2BUnitModel.getPaymentRequired())
			{
				return "CASH";
			}

		}
		return "CREDIT";
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.report.service.WelcomeEmailSaleForceDataExportService#sendWelcomeEmail()
	 */
	@Override
	public boolean sendWelcomeEmail() throws SABMSFIntegrationException
	{
		final List<B2BCustomerModel> customers = getAllCustomerToSendWelcomeEmail();
		boolean sendSuccess = false;

		if (CollectionUtils.isNotEmpty(customers))
		{
			generateTokenAndUpdateCustomer(customers);
			final Map<B2BCustomerModel,CustomerWelcomeMailData> reportData = getCustomerWelcomeData(customers);
			try {
   				SFTokenRequest sfTokenRequest = new SFTokenRequest();
               String authDetails = sfTokenRequest.getOldSalesforceAuthenticateInfo();
               
               SFTokenResponse tokenResponse = sabmSalesForceAccessTokenRequestHandler.sendPostTokenRequest(authDetails, "admin");
               
               for(Map.Entry<B2BCustomerModel,CustomerWelcomeMailData> to : reportData.entrySet())
      			{
      				SalesforceWelcomeMailToData salesforceWelcomeMailToData = new SalesforceWelcomeMailToData(to.getValue());
      				SalesforceWelcomeMailData salesforceWelcomeMailData = new SalesforceWelcomeMailData(salesforceWelcomeMailToData, to.getValue());
      				
      				ObjectMapper mapper = new ObjectMapper();
      				String jsonInString = mapper.writeValueAsString(salesforceWelcomeMailData);
                  
                  if(tokenResponse != null && StringUtils.isNotEmpty(tokenResponse.getAccessToken())){
                  	
                  	SalesForceEmailSmsPostResponse salesForceEmailSmsPostResponse =  sabmSalesForceWelcomeMailPostHandler.sendPostRequest(jsonInString,tokenResponse, "admin");
                  	
                  	List<B2BCustomerModel> tempCustomer =new ArrayList<B2BCustomerModel>();
      	            tempCustomer.add(to.getKey());
      	            updateCustomerWelcomeEmailStatus(tempCustomer);
      	            
      	            sendSuccess = salesForceEmailSmsPostResponse.getSuccess();
                  }
      			}
				
			}catch (Exception e) {
              LOG.error("Error while sending welcome email to sales force server");
              throw new SABMSFIntegrationException(e.getMessage());
	      }							
			
			//updateCustomerWelcomeEmailStatus(customers);
		}

		return false;
	}

	private Map<B2BCustomerModel,CustomerWelcomeMailData> getCustomerWelcomeData(final List<B2BCustomerModel> customers)
	{

		Map<B2BCustomerModel,CustomerWelcomeMailData> reportData = new HashMap<B2BCustomerModel,CustomerWelcomeMailData>();
		for (final B2BCustomerModel customer : customers)
		{
			final CustomerWelcomeMailData customerData = new CustomerWelcomeMailData();
			customerData.setPrimaryKey(customer.getPk() + "");
			customerData.setFirstName(SabmStringUtils.trimToEmpty(customer.getFirstName()));
			customerData.setLastName(SabmStringUtils.trimToEmpty(customer.getLastName()));
			customerData.setEmail(customer.getUid());
			customerData.setRegistrationDate(customer.getCreationtime());
			try
			{
				customerData.setPasswordSetToken(URLEncoder.encode(customer.getToken(), "UTF-8"));
			}
			catch (final UnsupportedEncodingException e)
			{
				LOG.error("Exception while encoding", e);
			}
			customerData.setAccountNumber(customer.getDefaultB2BUnit() != null ? customer.getDefaultB2BUnit().getUid() : "");
			customerData.setVenue(customer.getDefaultB2BUnit() != null ? customer.getDefaultB2BUnit().getName() : "");
			customerData.setPayeeNumber(customer.getDefaultB2BUnit() != null ? customer.getDefaultB2BUnit().getPayerId() : "");
			customerData.setPaymentTerms(customer.getDefaultB2BUnit() != null ? getPaymentTerms(customer.getDefaultB2BUnit()) : "");
			customerData.setPrimaryAdmin(customer.getPrimaryAdmin().toString());
			reportData.put(customer, customerData);
		}
		return reportData;
	}
	

	/**
	 * @return the sabmCSVFileGenerator
	 */
	public SabmCSVFileGenerator getSabmCSVFileGenerator()
	{
		return sabmCSVFileGenerator;
	}


	/**
	 * @param sabmCSVFileGenerator
	 *           the sabmCSVFileGenerator to set
	 */
	public void setSabmCSVFileGenerator(final SabmCSVFileGenerator sabmCSVFileGenerator)
	{
		this.sabmCSVFileGenerator = sabmCSVFileGenerator;
	}

	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}


	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @return the sabmSftpFileUpload
	 */
	public SabmSftpFileUpload getSabmSftpFileUpload()
	{
		return sabmSftpFileUpload;
	}

	/**
	 * @param sabmSftpFileUpload
	 *           the sabmSftpFileUpload to set
	 */
	public void setSabmSftpFileUpload(final SabmSftpFileUpload sabmSftpFileUpload)
	{
		this.sabmSftpFileUpload = sabmSftpFileUpload;
	}



	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}



	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}



	/**
	 * @return the secureTokenService
	 */
	public SecureTokenService getSecureTokenService()
	{
		return secureTokenService;
	}



	/**
	 * @param secureTokenService
	 *           the secureTokenService to set
	 */
	public void setSecureTokenService(final SecureTokenService secureTokenService)
	{
		this.secureTokenService = secureTokenService;
	}



	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}



	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


}
